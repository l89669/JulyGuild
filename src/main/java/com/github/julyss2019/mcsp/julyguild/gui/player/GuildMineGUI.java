package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.Lang;
import com.github.julyss2019.mcsp.julyguild.config.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildAdmin;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatFilter;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import com.github.julyss2019.mcsp.julylibrary.item.SkullItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuildMineGUI extends BaseGUI {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static MainSettings mainSettings = plugin.getMainSettings();
    private Inventory inventory;
    private Guild guild;

    public GuildMineGUI(GuildPlayer guildPlayer) {
        super(GUIType.MINE, guildPlayer);

        this.guild = this.guildPlayer.getGuild();
        build();
    }

    @Override
    public void build() {
        GuildMember member = guild.getMember(playerName);
        InventoryBuilder inventoryBuilder = new InventoryBuilder().title("&e&l我的宗门").colored().row(6);

        List<String> memberLores = new ArrayList<>();
        List<GuildMember> guildMembers = guild.getMembers();

        Guild.sortMembers(guildMembers);

        for (GuildMember guildMember : guild.getMembers()) {
            if (memberLores.size() < 10) {
                memberLores.add(Lang.getNickName(guildMember));
            } else {
                break;
            }
        }

        inventoryBuilder
                // 宗门信息
                .item(2, 5, new ItemBuilder().
                        material(Material.SIGN)
                        .displayName(PlaceholderAPI.setPlaceholders(bukkitPlayer, mainSettings.getGlobalGuildInfoDisplayName()))
                        .lores(PlaceholderAPI.setPlaceholders(bukkitPlayer, mainSettings.getGlobalGuildInfoLores()))
                        .colored()
                        .build())
                // 个人信息
                .item(2, 3, new ItemBuilder().
                        material(Material.SIGN)
                        .displayName(PlaceholderAPI.setPlaceholders(bukkitPlayer, mainSettings.getMineGUIPlayerInfoDisplayName()))
                        .lores(PlaceholderAPI.setPlaceholders(bukkitPlayer, mainSettings.getMineGUIPlayerInfoLores()))
                        .colored()
                        .build())
                .item(2, 4, new ItemBuilder()
                        .material(Material.PAINTING)
                        .displayName("&f宗门公告")
                        .lores(guild.getAnnouncements())
                        .colored()
                        .build())
                .item(1, 4, new SkullItemBuilder()
                        .owner("Notch")
                        .displayName("&f宗门成员")
                        .addLore("&b>> &a点击查看详细信息")
                        .addLore("")
                        .addLores(memberLores)
                        .addLore(guild.getMemberCount() > 10 ? "&7和 &e" + (guild.getMemberCount() - 10) + "个 &7成员..." : null)
                        .colored().build(), new ItemListener() {
                            @Override
                            public void onClicked(InventoryClickEvent event) {
                                close();
                                new GuildMemberGUI(guildPlayer, guild, GuildMineGUI.this).open();
                            }
                        }
                )
                .item(3, 4, new ItemBuilder()
                        .material(Material.EMERALD)
                        .displayName("&e贡献")
                        .addLore("&b>> &a点击贡献金币" + (mainSettings.isDonatePointsEnabled() ? "/点券" : ""))
                        .colored().build(), new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        new GuildDonateGUI(guildPlayer).open();;
                    }
                });


        if (member instanceof GuildAdmin) {
            inventoryBuilder
                    .item(45, new ItemBuilder()
                            .material(Material.ENDER_PORTAL_FRAME)
                            .displayName("&f管理宗门")
                            .colored().build(), new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();
                            new GuildManageGUI(guildPlayer).open();
                        }
                    });
        } else {
            inventoryBuilder
                    .item(5, 0, new ItemBuilder()
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

        inventoryBuilder.item(53, CommonItem.BACK, new ItemListener() {
            @Override
            public void onClicked(InventoryClickEvent event) {
                close();
                new MainGUI(guildPlayer).open();
            }
        });

        this.inventory = inventoryBuilder.build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
