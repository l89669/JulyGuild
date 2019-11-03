package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatFilter;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import com.github.julyss2019.mcsp.julylibrary.item.SkullItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GuildManageGUI extends BaseGUI {
    private Inventory inventory;
    private Guild guild;
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static Map<String, Long> tpAllIntervalMap = new HashMap<>();

    public GuildManageGUI(GuildPlayer guildPlayer) {
        super(GUIType.MANAGE, guildPlayer);

        this.guild = this.guildPlayer.getGuild();
        build();
    }

    @Override
    public void build() {
        InventoryBuilder inventoryBuilder = new InventoryBuilder().row(4).title("&e&l宗门管理").colored()
                .item(35, CommonItem.BACK, new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();

                        if (guild.isValid()) {
                            new GuildMineGUI(guildPlayer).open();
                        }
                    }
                });

        if (guildPlayer.getGuild().getMember(guildPlayer.getName()).getPermission() == Permission.OWNER) {
            inventoryBuilder
                    .item(1, 2, new ItemBuilder()
                                    .material(Material.GOLD_BARDING)
                                    .displayName("&f审批玩家")
                                    .addLore("&b>> &a处理玩家加入宗门的请求")
                                    .colored()
                                    .build()
                            , new ItemListener() {
                                @Override
                                public void onClicked(InventoryClickEvent event) {
                                    close();
                                    new GuildPlayerRequestGUI(guildPlayer).open();
                                }
                            })

                    .item(1, 3, new ItemBuilder()
                            .material(Material.PAINTING)
                            .displayName("&f设置公告")
                            .addLore("&b>> &a点击设置公告")
                            .addLore("&b>> &a玩家可以在宗门介绍页和我的宗门中看见公告")
                            .addLore("")
                            .addLores(guild.getAnnouncements())
                            .colored()
                            .build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();
                            Util.sendColoredMessage(bukkitPlayer, "&e请在聊天栏输入并发送要设置的公告, 使用符号 &c" + MainSettings.getAnnouncementSplitChar() + " &e来换行, 最多支持 &c" + MainSettings.getAnnouncementMaxCount() + "行&e: ");

                            JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    event.setCancelled(true);

                                    String[] messages = event.getMessage().split(MainSettings.getAnnouncementSplitChar());

                                    if (messages.length > MainSettings.getAnnouncementMaxCount()) {
                                        Util.sendColoredMessage(bukkitPlayer, "&c公告最多能设置 &e" + MainSettings.getAnnouncementMaxCount() + "条&c, 使用分隔符 &e" + MainSettings.getAnnouncementSplitChar() + " &c换行.");
                                        return;
                                    }

                                    guild.setAnnouncements(Arrays.asList(messages));
                                    Util.sendColoredMessage(bukkitPlayer, "&d设置公告成功!");
                                    JulyChatFilter.unregisterChatFilter(bukkitPlayer);
                                }
                            });
                        }
                    })

                    .item(1, 4, new SkullItemBuilder()
                                    .owner("Notch")
                                    .displayName("&f成员管理").colored()
                                    .addLore("&b>> &a任职或移出成员")
                                    .build()
                            , new ItemListener() {
                                @Override
                                public void onClicked(InventoryClickEvent event) {
                                    close();
                                    new GuildMemberManageGUI(guildPlayer).open();
                                }
                            })

                    .item(1, 5, new ItemBuilder()
                            .material(Material.EXP_BOTTLE)
                            .displayName("&f宗门升级")
                            .addLore("&b>> &a升级宗门最大人数")
                            .colored()
                            .build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();
                            new GuildPromoteGUI(guildPlayer).open();
                        }
                    })

                    .item(1, 6, new ItemBuilder()
                            .material(Material.DOUBLE_PLANT)
                            .displayName("&f图标购买")
                            .addLore("&b>> &a点击购买宗门图标")
                            .colored()
                            .build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();
                            new GuildIconShopGUI(guildPlayer).open();
                        }
                    })

                    .item(2, 2, new ItemBuilder()
                            .material(Material.PAPER)
                            .displayName("&f图标仓库")
                            .colored()
                            .addLore("&b>> &a设置你购买的的宗门图标")
                            .build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();
                            new GuildIconRepositoryGUI(guildPlayer).open();
                        }
                    })

                    .item(2, 3, new ItemBuilder()
                            .material(Material.NAME_TAG)
                            .displayName("&f转让宗门")
                            .colored()
                            .addLore("&b>> &a将宗门转让给一个成员")
                            .build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();
                            Util.sendColoredMessage(bukkitPlayer, "&c如果要转让宗门, 请在 &e60秒内 &c输入欲转让给的成员ID: ");
                            JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    event.setCancelled(true);
                                    JulyChatFilter.unregisterChatFilter(bukkitPlayer);
                                    String name = event.getMessage();

                                    if (name.equalsIgnoreCase(playerName)) {
                                        Util.sendColoredMessage(bukkitPlayer, "&c你不能转让给你自己.");
                                        return;
                                    }

                                    if (!guild.isMember(name)) {
                                        Util.sendColoredMessage(bukkitPlayer, "&c玩家 &e" + name + " &c不是你宗门的成员.");
                                        return;
                                    }

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            guild.setOwner(guild.getMember(name));
                                        }
                                    }.runTaskLater(plugin, 1L);

                                    Util.sendColoredMessage(bukkitPlayer, "&d成功将宗门转让给 &e" + name + "&d.");
                                }
                            }, 60);
                        }
                    })

                    .item(2, 4, new ItemBuilder()
                            .material(Material.CHEST)
                            .displayName("&f宗门商店")
                            .addLore("&b>> &a宗门专用商店")
                            .colored()
                            .build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();
                            new GuildShopGUI(guildPlayer).open();
                        }
                    })

                    .item(2, 5, new ItemBuilder()
                            .material(Material.EYE_OF_ENDER)
                            .displayName("&f全员集结令")
                            .addLore("&b>> &a请求全体成员传送到你所在的位置")
                            .colored()
                            .build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();

                            long interval = (System.currentTimeMillis() - tpAllIntervalMap.getOrDefault(bukkitPlayer.getName(), 0L)) / 1000L;

                            if (interval < MainSettings.getTpAllInterval()) {
                                Util.sendColoredMessage(bukkitPlayer, "&c冷却中: &e" + Util.getTimeLeftStr(MainSettings.getTpAllInterval() - interval) + "&c.");
                                return;
                            }

                            tpAllIntervalMap.put(bukkitPlayer.getName(), System.currentTimeMillis());
                            close();
                            new GuildTpAllBuyGUI(guildPlayer).open();
                        }
                    })

                    .item(2, 6, new ItemBuilder()
                            .material(Material.BARRIER)
                            .displayName("&c解散宗门")
                            .colored()
                            .build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();
                            Util.sendColoredMessage(bukkitPlayer, "&c（该操作不可恢复，请谨慎操作）如果要解散宗门, 请在 &e10秒内 &c聊天栏输入并发送: &econfirm");
                            JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                                @Override
                                public void onTimeout() {
                                    Util.sendColoredMessage(bukkitPlayer, "&c确认请求已超时.");
                                }

                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    event.setCancelled(true);

                                    if (event.getMessage().equals("confirm")) {
                                        guild.delete();
                                        Util.sendColoredMessage(bukkitPlayer, "&d解散宗门成功.");
                                    } else {
                                        Util.sendColoredMessage(bukkitPlayer, "&e解散宗门失败.");
                                    }

                                    JulyChatFilter.unregisterChatFilter(bukkitPlayer);
                                }
                            }, 10);
                        }
                    });
        } else {
            inventoryBuilder
                    .item(1, 3, new ItemBuilder()
                                    .material(Material.GOLD_BARDING)
                                    .displayName("&f审批玩家")
                                    .addLore("&b>> &a处理玩家加入宗门的请求")
                                    .colored()
                                    .build()
                            , new ItemListener() {
                                @Override
                                public void onClicked(InventoryClickEvent event) {
                                    close();
                                    new GuildPlayerRequestGUI(guildPlayer).open();
                                }
                            });
            inventoryBuilder
                    .item(1, 5, new ItemBuilder()
                                    .material(Material.IRON_DOOR)
                                    .displayName("&c退出宗门")
                                    .colored()
                                    .build()
                            , new ItemListener() {
                                @Override
                                public void onClicked(InventoryClickEvent event) {
                                    close();
                                    Util.sendColoredMessage(bukkitPlayer, "&c如果要退出宗门, 请在聊天栏输入并发送: &econfirm");
                                    JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                                        @Override
                                        public void onChat(AsyncPlayerChatEvent event) {
                                            event.setCancelled(true);

                                            if (event.getMessage().equals("confirm")) {
                                                guild.removeMember(guild.getMember(playerName));
                                                Util.sendColoredMessage(bukkitPlayer, "&d退出宗门成功.");
                                            } else {
                                                Util.sendColoredMessage(bukkitPlayer, "&e退出宗门失败.");
                                            }

                                            JulyChatFilter.unregisterChatFilter(bukkitPlayer);
                                        }
                                    });

                                }
                            });

        }

        this.inventory = inventoryBuilder.build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
