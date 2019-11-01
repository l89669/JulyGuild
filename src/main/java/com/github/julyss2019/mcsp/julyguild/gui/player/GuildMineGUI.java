package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.config.ConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.ConfigGUIItem;
import com.github.julyss2019.mcsp.julyguild.gui.BaseGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuildMineGUI extends BaseGUI {
    private JulyGuild plugin = JulyGuild.getInstance();
    private ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildMineGUI");
    private Inventory inventory;

    public GuildMineGUI(GuildPlayer guildPlayer) {
        super(GUIType.MINE, guildPlayer);

        build();
    }

    @Override
    public void build() {
        ConfigGUI.Builder guiBuilder = new ConfigGUI.Builder().fromConfig(thisGUISection)
                .item(ConfigGUIItem.getIndexItem(thisGUISection.getConfigurationSection("items.back")), new ItemListener() {
            @Override
            public void onClicked(InventoryClickEvent event) {
                close();
                new MainGUI(guildPlayer).open();
            }
        })
                .item(ConfigGUIItem.getIndexItem(thisGUISection.getConfigurationSection("items.guild_info"), bukkitPlayer))
                .item(ConfigGUIItem.getIndexItem(thisGUISection.getConfigurationSection("items.self_info"), bukkitPlayer));

        // 公会公告
        ConfigGUIItem guildAnnouncementItem = ConfigGUIItem.getIndexItem(thisGUISection.getConfigurationSection("items._guild_announcements"));

        guildAnnouncementItem.getItemBuilder().lores(guild.getAnnouncements());
        guiBuilder.item(guildAnnouncementItem);

        // 公会成员
        List<String> memberLores = new ArrayList<>();

        for (GuildMember guildMember : guild.getSortedMembers()) {
            if (memberLores.size() < 10) {
                memberLores.add(LangHelper.Global.getNickName(guildMember));
            } else {
                break;
            }
        }

        ConfigGUIItem guildMemberItem = ConfigGUIItem.getIndexItem(thisGUISection.getConfigurationSection("items._guild_members"));

        guildMemberItem.getItemBuilder().lores(memberLores);
        guiBuilder.item(guildMemberItem);


        /*
        公会信息，个人信息，公告，成员，捐献，(退出|解散)，管理
         */


/*        guiBuilder
                // 宗门信息
                .item(2, 5, new ItemBuilder().
                        material(Material.SIGN)
                        .displayName(PlaceholderAPI.setPlaceholders(bukkitPlayer, mainConfig.getGlobalGuildInfoDisplayName()))
                        .lores(PlaceholderAPI.setPlaceholders(bukkitPlayer, mainConfig.getGlobalGuildInfoLores()))
                        .colored()
                        .build())
                // 个人信息
                .item(2, 3, new ItemBuilder().
                        material(Material.SIGN)
                        .displayName(PlaceholderAPI.setPlaceholders(bukkitPlayer, mainConfig.getMineGUIPlayerInfoDisplayName()))
                        .lores(PlaceholderAPI.setPlaceholders(bukkitPlayer, mainConfig.getMineGUIPlayerInfoLores()))
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
                        .addLore("&b>> &a点击贡献金币" + (mainConfig.isDonatePointsEnabled() ? "/点券" : ""))
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
        });*/

        this.inventory = guiBuilder.build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
