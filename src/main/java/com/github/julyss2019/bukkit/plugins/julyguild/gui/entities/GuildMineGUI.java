package com.github.julyss2019.bukkit.plugins.julyguild.gui.entities;

import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.bukkit.plugins.julyguild.config.setting.MainSettings;
import com.github.julyss2019.bukkit.plugins.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.bukkit.plugins.julyguild.gui.GUI;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.Guild;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildMember;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildPermission;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildPosition;
import com.github.julyss2019.bukkit.plugins.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.bukkit.plugins.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.bukkit.plugins.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatInterceptor;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.Title;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GuildMineGUI extends BasePlayerGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildMineGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildMineGUI");
    private final Player bukkitPlayer;
    private final GuildPosition guildPosition;
    private final GuildMember guildMember;
    private final Guild guild;

    public GuildMineGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        super(lastGUI, Type.MINE, guildMember.getGuildPlayer());

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
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.self_info"), bukkitPlayer, new PlaceholderContainer().addGuildMemberPlaceholders(guildMember)))
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
                            close();

                            AtomicInteger atomicInteger = new AtomicInteger();

                            BukkitTask bukkitTask = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (atomicInteger.get() < MainSettings.getGuildSpawnTeleportWait()) {
                                        if (JulyMessage.canUseTitle()) {
                                            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder()
                                                    .text(PlaceholderText.replacePlaceholders(thisLangSection.getString("guild_spawn.count_down.title")
                                                            , new PlaceholderContainer().add("count_down", MainSettings.getGuildSpawnTeleportWait() - atomicInteger.get())))
                                                    .colored().build());
                                            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder()
                                                    .type(Title.Type.SUBTITLE)
                                                    .text(PlaceholderText.replacePlaceholders(thisLangSection.getString("guild_spawn.count_down.subtitle")
                                                            , new PlaceholderContainer().add("count_down", MainSettings.getGuildSpawnTeleportWait() - atomicInteger.get())))
                                                    .colored().build());
                                        } else {
                                            Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_spawn.count_down.msg"));
                                        }

                                        atomicInteger.addAndGet(1);
                                    } else {
                                        cancel();
                                        bukkitPlayer.teleport(guild.getSpawn().getLocation());

                                        if (JulyMessage.canUseTitle()) {
                                            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().text(thisLangSection.getString("guild_spawn.teleported.title")).colored().build());
                                            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().type(Title.Type.SUBTITLE).text(thisLangSection.getString("guild_spawn.teleported.subtitle")).colored().build());
                                        } else {
                                            Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_spawn.teleported.msg"));
                                        }

                                        guildPlayer.setTeleportTask(null);
                                    }
                                }
                            }.runTaskTimer(plugin, 0L, 20L);

                            guildPlayer.setTeleportTask(bukkitTask);
                        }
                    }
                });

        // 公会公告
        PriorityItem guildAnnouncementItem = GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_announcements"), bukkitPlayer);
        List<String> announcements = guild.getAnnouncements();

        guildAnnouncementItem.getItemBuilder().lores(announcements.size() == 0 ? MainSettings.getGuildAnnouncementDefault() : announcements);
        guiBuilder.item(guildAnnouncementItem);

        // 公会商店
        if (guildMember.hasPermission(GuildPermission.USE_SHOP)) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_shop"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    new GuildShopGUI(GuildMineGUI.this, guildMember).open();
                }
            });
        }

        // 成员免伤
        if (guildMember.hasPermission(GuildPermission.SET_MEMBER_DAMAGE)) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_set_member_damage." + (guild.isMemberDamageEnabled() ? "turn_off" : "turn_on")), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    guild.setMemberDamageEnabled(!guild.isMemberDamageEnabled());
                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_set_member_damage." + (!guild.isMemberDamageEnabled() ? "turn_off" : "turn_on")));
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

        if (guildMember.hasPermission(GuildPermission.USE_ICON_REPOSITORY)) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_icon_repository"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    new GuildIconRepositoryGUI(GuildMineGUI.this, guildMember).open();
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
                    .add("wait", MainSettings.getGuildDismissWait())
                    .add("confirm_str", MainSettings.getGuildDismissConfirmStr()));
                    new ChatInterceptor.Builder()
                            .player(bukkitPlayer)
                            .plugin(plugin)
                            .timeout(MainSettings.getGuildDismissWait())
                            .chatListener(new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (event.getMessage().equalsIgnoreCase(MainSettings.getGuildDismissConfirmStr())) {
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
                            .add("wait", MainSettings.getGuildDismissWait())
                            .add("confirm_str", MainSettings.getGuildDismissConfirmStr()));
                    new ChatInterceptor.Builder()
                            .player(bukkitPlayer)
                            .plugin(plugin)
                            .timeout(MainSettings.getGuildExitWait())
                            .chatListener(new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (event.getMessage().equalsIgnoreCase(MainSettings.getGuildExitConfirmStr())) {
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
