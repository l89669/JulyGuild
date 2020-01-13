package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatInterceptor;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
/*
返回时强制更新，手动强制更新
 */

/**
 * 主GUI
 * @version 1.0.0
 */
public class MainGUI extends BasePlayerPageableGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final Player bukkitPlayer = guildPlayer.getBukkitPlayer();
    private final String playerName = bukkitPlayer.getName();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("MainGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("MainGUI");
    private final List<Integer> itemIndexes = Util.getIndexes(thisGUISection.getString("items.guild.indexes")); // 得到所有可供公会设置的位置
    private final int itemIndexCount = itemIndexes.size();
    private List<Guild> guilds;
    private int guildCount;

    public MainGUI(GuildPlayer guildPlayer) {
        super(null, GUIType.MAIN, guildPlayer);

        update();

        if (getTotalPage() > 0) {
            setCurrentPage(0);
        }
    }

    @Override
    public void update() {
        this.guilds = plugin.getCacheGuildManager().getSortedGuilds();
        this.guildCount = guilds.size();

        setTotalPage(guildCount % itemIndexCount == 0 ? guildCount / itemIndexCount : guildCount / itemIndexCount + 1);
    }

    @Override
    public Inventory createInventory() {
        Map<Integer, Guild> indexMap = new HashMap<>(); // slot 对应的公会uuid

        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer, new Placeholder.Builder()
                        .addInner("page", String.valueOf(getCurrentPage() + 1))
                        .addInner("total_page", String.valueOf(getTotalPage())).build())
                .colored()
                .listener(new InventoryListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int slot = event.getSlot();

                        if (indexMap.containsKey(slot)) {
                            close();

                            Guild guild = indexMap.get(slot);

                            if (!guild.isValid()) {
                                reopen();
                                return;
                            }

                            if (guild.isMember(guildPlayer)) {
                                new GuildMineGUI(MainGUI.this, guild.getMember(guildPlayer)).open();
                            } else {
                                new GuildInfoGUI(MainGUI.this, guildPlayer, guild).open();
                            }
                        }
                    }
                });

        guiBuilder.pageItems(thisGUISection.getConfigurationSection("items.page_items"), this, bukkitPlayer);

        if (guildPlayer.isInGuild()) {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.my_guild"), bukkitPlayer, new Placeholder.Builder().add("%PLAYER%", playerName).build()), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    new GuildMineGUI(MainGUI.this, guildPlayer.getGuild().getMember(guildPlayer)).open();
                }
            });
        } else {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.create_guild"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.input.tip"), new Placeholder.Builder().addInner("cancel_str", MainSettings.getCreateInputCancelStr()).build());
                    new ChatInterceptor.Builder()
                            .plugin(plugin)
                            .player(bukkitPlayer)
                            .onlyFirst(true)
                            .timeout(MainSettings.getCreateInputWaitSec())
                            .chatListener(new ChatListener() {
                                @Override
                                public void onChat(AsyncPlayerChatEvent event) {
                                    String msg = event.getMessage();

                                    if (msg.equalsIgnoreCase(MainSettings.getCreateInputCancelStr())) {
                                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.input.cancelled"));
                                        return;
                                    }

                                    String guildName = ChatColor.translateAlternateColorCodes('&', msg);

                                    if (MainSettings.isCreateNoDuplicationName()) {
                                        for (Guild guild : plugin.getGuildManager().getGuilds()) {
                                            if (guild.getName().equalsIgnoreCase(guildName)) {
                                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.input.no_duplication_name"));
                                                return;
                                            }
                                        }
                                    }

                                    if (!guildName.matches(MainSettings.getCreateNameRegex())) {
                                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.input.regex_not_match"));
                                        return;
                                    }

                                    if (guildName.contains("§") && !bukkitPlayer.hasPermission("JulyGuild.create.colored")) {
                                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.no_colored_name_permission"));
                                        return;
                                    }

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            new GuildCreateGUI(MainGUI.this, guildPlayer, guildName).open();
                                        }
                                    }.runTaskLater(plugin, 1L);
                                }

                                @Override
                                public void onTimeout(AsyncPlayerChatEvent event) {
                                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.input.timeout"));
                                }
                            }).build().register();
                }
            });
        }

        if (getCurrentPage() != -1) {
            int itemCounter = getCurrentPage() * itemIndexes.size();
            int loopCount = guildCount - itemCounter < itemIndexCount ? guildCount - itemCounter : itemIndexCount; // 循环次数，根据当前能够显示的数量决定

            // 公会图标
            for (int i = 0; i < loopCount; i++) {
                Guild guild = guilds.get(itemCounter++);
                ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items.guild"), bukkitPlayer, guild)
                        .material(Material.STONE);

                indexMap.put(itemIndexes.get(i), guild);
                guiBuilder.item(itemIndexes.get(i), itemBuilder.build());
            }
        }

        return guiBuilder.build();
    }

    @Override
    public boolean canUse() {
        return true;
    }
}