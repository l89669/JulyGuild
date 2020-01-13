package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberPageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.Permission;
import com.github.julyss2019.mcsp.julyguild.guild.Position;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatInterceptor;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class GuildMineGUI extends BaseMemberGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildMineGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildMineGUI");
    private final Player bukkitPlayer;
    private final Position position;
    private final Guild guild = guildMember.getGuild();

    public GuildMineGUI(@Nullable GUI lastGUI, GuildMember guildMember) {
        super(lastGUI, GUIType.MINE, guildMember);

        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
        this.position = guildMember.getPosition();
    }

    @Override
    public Inventory createInventory() {
        PriorityConfigGUI.Builder guiBuilder = (PriorityConfigGUI.Builder) new PriorityConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        back();
                    }
                });

        guiBuilder
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_info"), bukkitPlayer, new Placeholder.Builder().addGuildPlaceholders(guild).build()))
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.self_info"), bukkitPlayer))
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_members." + ((guildMember.hasPermission(Permission.MEMBER_KICK) || guildMember.hasPermission(Permission.MANAGE_PERMISSION)) ? "manager" : "member")), bukkitPlayer), new ItemListener() {
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
                });

        // 公会公告
        PriorityItem guildAnnouncementItem = GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_announcements"), bukkitPlayer);

        guildAnnouncementItem.getItemBuilder().lores(guild.getAnnouncements());
        guiBuilder.item(guildAnnouncementItem);

        // 公会升级
        if (guildMember.hasPermission(Permission.GUILD_UPGRADE)) {
            guiBuilder
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_upgrade"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildUpgradeGUI(GuildMineGUI.this, guildMember).open();
                    }
                });
        }

        // 入会审批
        if (guildMember.hasPermission(Permission.PLAYER_JOIN_CHECK)) {
            guiBuilder
                    .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_join_check"), bukkitPlayer), new ItemListener() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            close();
                            new GuildJoinCheckGUI(GuildMineGUI.this, guildMember).open();
                        }
                    });
        }

        // 解散或退出
        if (position == Position.OWNER) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_dismiss"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("guild_dismiss.confirm"), new Placeholder.Builder()
                    .addInner("wait", MainSettings.getDismissWait())
                    .addInner("confirm_str", MainSettings.getDismissConfirmStr()).build());
                    new ChatInterceptor.Builder()
                            .player(bukkitPlayer)
                            .plugin(plugin)
                            .timeout(MainSettings.getDismissWait())
                            .chatListener(new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (event.getMessage().equalsIgnoreCase(MainSettings.getDismissConfirmStr())) {
                                        guild.delete();
                                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("guild_dismiss.success"));
                                    } else {
                                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("guild_dismiss.failed"));
                                    }
                                }

                                @Override
                                public void onTimeout(AsyncPlayerChatEvent event) {
                                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("guild_dismiss.timeout"));
                                }
                            }).build().register();
                }
            });
        } else {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_exit"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("guild_exit.confirm"), new Placeholder.Builder()
                            .addInner("wait", MainSettings.getDismissWait())
                            .addInner("confirm_str", MainSettings.getDismissConfirmStr()).build());
                    new ChatInterceptor.Builder()
                            .player(bukkitPlayer)
                            .plugin(plugin)
                            .timeout(MainSettings.getExitWait())
                            .chatListener(new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (event.getMessage().equalsIgnoreCase(MainSettings.getExitConfirmStr())) {
                                        guild.removeMember(guildMember);
                                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("guild_exit.success"));
                                    } else {
                                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("guild_exit.failed"));
                                    }
                                }

                                @Override
                                public void onTimeout(AsyncPlayerChatEvent event) {
                                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("guild_exit.timeout"));
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
