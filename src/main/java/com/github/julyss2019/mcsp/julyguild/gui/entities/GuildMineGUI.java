package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.*;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.GuildPermission;
import com.github.julyss2019.mcsp.julyguild.guild.GuildPosition;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatInterceptor;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildMineGUI extends BasePlayerGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildMineGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildMineGUI");
    private final Player bukkitPlayer;
    private final GuildPosition guildPosition;
    private final GuildMember guildMember;
    private final Guild guild;

    public GuildMineGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        super(lastGUI, GUIType.MINE, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
        this.guildPosition = guildMember.getPosition();
        this.guild = guildMember.getGuild();
    }

    @Override
    public Inventory createInventory() {
        PriorityConfigGUI.Builder guiBuilder = (PriorityConfigGUI.Builder) new PriorityConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (canBack()) {
                            back();
                        }
                    }
                });

        guiBuilder
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_info"), bukkitPlayer, new PlaceholderContainer().addGuildPlaceholders(guild)))
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.self_info"), bukkitPlayer))
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_members." + ((guildMember.hasPermission(GuildPermission.MEMBER_KICK) || guildMember.hasPermission(GuildPermission.MANAGE_PERMISSION)) ? "manager" : "member")), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildMemberListGUI(GuildMineGUI.this, guild, guildMember).open();
                    }
                })
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_donate"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildDonateGUI(GuildMineGUI.this, guildMember).open();
                    }
                })
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_spawn." + (guild.hasSpawn() ? "available" : "unavailable")), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (guild.hasSpawn()) {
                            bukkitPlayer.teleport(guild.getSpawn().getLocation());
                        }
                    }
                })
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_shop"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        new GuildShopGUI(GuildMineGUI.this, guildMember).open();
                    }
                });

        // 公会公告
        PriorityItem guildAnnouncementItem = GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_announcements"), bukkitPlayer);

        guildAnnouncementItem.getItemBuilder().lores(guild.getAnnouncements());
        guiBuilder.item(guildAnnouncementItem);

        // 成员免伤
        if (guildMember.hasPermission(GuildPermission.SET_MEMBER_DAMAGE)) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_set_member_damage." + (guild.isMemberDamageEnabled() ? "state_on" : "state_off")), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    guild.setMemberDamageEnabled(!guild.isMemberDamageEnabled());
                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_set_member_damage." + (guild.isMemberDamageEnabled() ? "state_on" : "state_off")));
                    reopen();
                }
            });
        }

        // 入会审批
        if (guildMember.hasPermission(GuildPermission.PLAYER_JOIN_CHECK)) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_join_check"), bukkitPlayer), new ItemListener() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            close();
                            new GuildJoinCheckGUI(GuildMineGUI.this, guildMember).open();
                        }
                    });
        }

        // 解散或退出
        if (guildPosition == GuildPosition.OWNER) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_dismiss"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_dismiss.confirm"), new PlaceholderContainer()
                    .add("wait", MainSettings.getDismissWait())
                    .add("confirm_str", MainSettings.getDismissConfirmStr()));
                    new ChatInterceptor.Builder()
                            .player(bukkitPlayer)
                            .plugin(plugin)
                            .timeout(MainSettings.getDismissWait())
                            .chatListener(new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (event.getMessage().equalsIgnoreCase(MainSettings.getDismissConfirmStr())) {
                                        guild.delete();
                                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_dismiss.success"));
                                    } else {
                                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_dismiss.failed"));
                                    }
                                }

                                @Override
                                public void onTimeout(AsyncPlayerChatEvent event) {
                                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_dismiss.timeout"));
                                }
                            }).build().register();
                }
            });
        } else {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_exit"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_exit.confirm"), new PlaceholderContainer()
                            .add("wait", MainSettings.getDismissWait())
                            .add("confirm_str", MainSettings.getDismissConfirmStr()));
                    new ChatInterceptor.Builder()
                            .player(bukkitPlayer)
                            .plugin(plugin)
                            .timeout(MainSettings.getExitWait())
                            .chatListener(new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (event.getMessage().equalsIgnoreCase(MainSettings.getExitConfirmStr())) {
                                        guild.removeMember(guildMember);
                                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_exit.success"));
                                    } else {
                                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_exit.failed"));
                                    }
                                }

                                @Override
                                public void onTimeout(AsyncPlayerChatEvent event) {
                                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_exit.timeout"));
                                }
                            }).build().register();
                }
            });
        }

        return guiBuilder.build();
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid();
    }
}
