package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.OwnedIcon;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatFilter;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * 主GUI
 * @version 1.0.0
 */
public class MainGUI extends BasePlayerPageableGUI {
    private final List<Guild> guilds = new ArrayList<>();

    private final Player bukkitPlayer;
    private final String playerName;
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("MainGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("MainGUI");
    private final List<Integer> positions = Util.getIntegerList(thisGUISection.getString("positions")); // 得到所有可供公会设置的位置

    public MainGUI(GuildPlayer guildPlayer) {
        super(GUIType.MAIN, guildPlayer);

        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
        this.playerName = bukkitPlayer.getName();
        this.guilds.addAll(plugin.getCacheGuildManager().getSortedGuilds());
    }

    @Override
    public Inventory getGUI() {
        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, new Placeholder.Builder()
                        .addInner("PAGE", String.valueOf(getCurrentPage() + 1))
                        .addInner("TOTAL_PAGE", String.valueOf(getTotalPage())).build(), bukkitPlayer)
                .row(6)
                .colored()
                .listener(new InventoryListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
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


        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.precious_page").getConfigurationSection(hasPrecious() ? "have" : "not_have"), bukkitPlayer));
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.next_page").getConfigurationSection(hasNext() ? "have" : "not_have"), bukkitPlayer));
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.close"), bukkitPlayer));

        // 如果大于1页则提供翻页按钮
        if (getTotalPage() > 1) {
            guiBuilder
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
                        public void onClick(InventoryClickEvent event) {
                            if (hasNext()) {
                                close();
                                nextPage();
                                open();
                            }
                        }
                    });
        }

        if (guildPlayer.isInGuild()) {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.my_guild"), new Placeholder.Builder().add("%PLAYER%", playerName).build(), bukkitPlayer), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    close();
                    new GuildMineGUI(guildPlayer.getGuild().getMember(guildPlayer)).open();
                }
            });
        } else {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.create_guild"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    close();
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.input"));
                    JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                        @Override
                        public void onChat(AsyncPlayerChatEvent event) {
                            event.setCancelled(true);
                            JulyChatFilter.unregisterChatFilter(bukkitPlayer);

                            String guildName = ChatColor.translateAlternateColorCodes('&', event.getMessage());

                            if (!guildName.matches(MainSettings.getCreateNameRegex())) {
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.regex_not_match"));
                                return;
                            }

                            if (guildName.contains("§") && !bukkitPlayer.hasPermission("JulyGuild.create.colored")) {
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("create.no_colored_name_permission"));
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
        }

        int guildSize = guilds.size();
        int itemCounter = getCurrentPage() * positions.size();
        int loopCount = guildSize - itemCounter < 43 ? guildSize - itemCounter : 43;

        // 公会图标
        for (int i = 0; i < loopCount; i++) {
            Guild guild = guilds.get(itemCounter++);
            OwnedIcon ownedIcon = guild.getIcon();
            ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items._guild"), new Placeholder.Builder().addGuildPlaceholders(guild).build(), bukkitPlayer)
                    .material(ownedIcon.getMaterial())
                    .data(ownedIcon.getData())
                    .insertLore(0, ownedIcon.getFirstLore());

            guiBuilder.item(i, itemBuilder.build());
        }

        return guiBuilder.build();
    }

    @Override
    public int getTotalPage() {
        int guildSize = guilds.size();

        return guildSize == 0 ? 1 : guildSize % positions.size() == 0 ? guildSize / 43 : guildSize / 43 + 1;
    }
}
