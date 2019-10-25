package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.Lang;
import com.github.julyss2019.mcsp.julyguild.config.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BasePageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildCreateGUI;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildInfoGUI;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildMineGUI;
import com.github.julyss2019.mcsp.julyguild.guild.CacheGuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatFilter;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import com.github.julyss2019.mcsp.julylibrary.item.SkullItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainGUI extends BasePageableGUI {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static MainSettings mainSettings = plugin.getMainSettings();
    private static CacheGuildManager cacheGuildManager = plugin.getCacheGuildManager();
    private Inventory inventory;
    private List<Guild> guilds = new ArrayList<>();

    public MainGUI(GuildPlayer guildPlayer) {
        super(GUIType.MAIN, guildPlayer);

        setCurrentPage(0);
    }

    @Override
    public void setCurrentPage(int page) {
        super.setCurrentPage(page);

        InventoryBuilder inventoryBuilder = new InventoryBuilder()
                .row(6)
                .colored()
                .title(Lang.get("MainGUI.title").replace("%page%", String.valueOf((getCurrentPage() + 1))))
                .listener(new InventoryListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        int index = event.getSlot() + getCurrentPage() * 43;

                        if (index < guilds.size()) {
                            Guild guild = guilds.get(index);
                            GUI newGUI = new GuildInfoGUI(guildPlayer, guild, getCurrentPage());

                            close();
                            guildPlayer.setUsingGUI(newGUI);
                            newGUI.open();
                        }
                    }
                });

        // 如果大于1页则提供翻页按钮
        if (getTotalPage() > 1) {
            inventoryBuilder
                    .item(4, 7, CommonItem.PREVIOUS_PAGE, new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            if (hasPrecious()) {
                                close();
                                previousPage();
                                open();
                            }
                        }
                    })
                    .item(4, 8, CommonItem.NEXT_PAGE, new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            if (hasNext()) {
                                close();
                                nextPage();
                                open();
                            }
                        }
                    });
        }

        if (guildPlayer.isInGuild()) {
            inventoryBuilder.item(5, 4, new SkullItemBuilder()
                    .owner(playerName)
                    .displayName("&f我的宗门")
                    .enchant(Enchantment.DURABILITY, 1)
                    .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                    .colored()
                    .build(), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    close();
                    new GuildMineGUI(guildPlayer).open();
                }
            });
        } else {
            inventoryBuilder.item(5, 2, new ItemBuilder()
                            .material(Material.NETHER_STAR)
                            .displayName("&d创建宗门").colored()
                            .enchant(Enchantment.DURABILITY, 1)
                            .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                            .build()
                    , new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            Util.sendColoredMessage(bukkitPlayer, "&e请在聊天栏输入并发送宗门名: ");
                            close();
                            JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    event.setCancelled(true);
                                    JulyChatFilter.unregisterChatFilter(bukkitPlayer);

                                    String guildName = event.getMessage();

                                    if (!guildName.matches(mainSettings.getGuildCreateNameRegex())) {
                                        Util.sendColoredMessage(bukkitPlayer, mainSettings.getGuildCreateNameNotValidMsg());
                                        return;
                                    }

                                    if (guildName.contains("§") && !bukkitPlayer.hasPermission("JulyGuild.create.colored")) {
                                        Util.sendColoredMessage(bukkitPlayer, "&c你没有权限使用彩色的宗门名!");
                                        return;
                                    }

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            close();
                                            new GuildCreateGUI(guildPlayer, guildName).open();
                                            JulyChatFilter.unregisterChatFilter(bukkitPlayer);
                                        }
                                    }.runTaskLater(plugin, 1L);

                                    event.setCancelled(true);
                                }
                            });
                        }
                    });
            inventoryBuilder.item(5, 6, new ItemBuilder()
                    .colored()
                    .material(Material.EMERALD)
                    .enchant(Enchantment.DURABILITY, 1)
                    .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                    .displayName("&a加入宗门")
                    .addLore("&7- &f单击上方的图标以查看信息或加入宗门").build());
        }

        guilds.clear();
        guilds.addAll(cacheGuildManager.getSortedGuilds());
        int guildSize = guilds.size();
        int itemCounter = page * 43;
        int loopCount = guildSize - itemCounter < 43 ? guildSize - itemCounter : 43;

        for (int i = 0; i < loopCount; i++) {
            Guild guild = guilds.get(itemCounter++);

            inventoryBuilder.item(i, new ItemBuilder(guild.getCurrentIcon().getItemStack())
                    .lores(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(guild.getOwner().getName()), mainSettings.getMainGUIRankingListLores()))
                    .displayName(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(guild.getOwner().getName()), mainSettings.getMainGUIRankingListDisplayName()))
                    .colored()
                    .enchant(Enchantment.DURABILITY, 1)
                    .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                    .build());
        }

        this.inventory = inventoryBuilder.build();
    }

    @Override
    public int getTotalPage() {
        int guildSize = guilds.size();

        return guildSize == 0 ? 1 : guildSize % 43 == 0 ? guildSize / 43 : guildSize / 43 + 1;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public List<Guild> getGuilds() {
        return guilds;
    }
}
