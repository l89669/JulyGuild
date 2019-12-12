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

/**
 * 主GUI
 * @version 1.0.0
 */
public class MainGUI extends BasePlayerPageableGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final Player bukkitPlayer = guildPlayer.getBukkitPlayer();
    private final String playerName = bukkitPlayer.getName();
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("MainGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("MainGUI");
    private final List<Integer> positions = Util.getRangeIntegerList(thisGUISection.getString("positions")); // 得到所有可供公会设置的位置
    private final int positionCount = positions.size();

    public MainGUI(GuildPlayer guildPlayer) {
        super(GUIType.MAIN, guildPlayer, null);
    }

    @Override
    public Inventory getInventory() {
        List<Guild> guilds = plugin.getCacheGuildManager().getSortedGuilds();
        int guildSize = guilds.size();
        Map<Integer, Guild> guildIndexMap = new HashMap<>(); // slot 对应的公会uuid

        setTotalPage(guildSize == 0 ? 1 : guildSize % positionCount == 0 ? guildSize / positionCount : guildSize / positionCount + 1);

        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer, new Placeholder.Builder()
                        .addInner("page", String.valueOf(getCurrentPage() + 1))
                        .addInner("total_page", String.valueOf(getTotalPage())).build())
                .colored()
                .listener(new InventoryListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int slot = event.getSlot();

                        if (guildIndexMap.containsKey(slot)) {
                            close();
                            new GuildInfoGUI(guildPlayer, guildIndexMap.get(slot), MainGUI.this).open();
                        }
                    }
                });


        guiBuilder.pageItems(thisGUISection.getConfigurationSection("items.page_items"), this, bukkitPlayer);
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.close"), bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                close();
            }
        });

        if (guildPlayer.isInGuild()) {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.my_guild"), bukkitPlayer, new Placeholder.Builder().add("%PLAYER%", playerName).build()), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    new GuildMineGUI(guildPlayer.getGuild().getMember(guildPlayer), null).open();
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
                                            close();
                                            new GuildCreateGUI(guildPlayer, guildName, MainGUI.this).open();
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

        int itemCounter = getCurrentPage() * positions.size();
        int loopCount = guildSize - itemCounter < positionCount ? guildSize - itemCounter : positionCount;

        // 公会图标
        for (int i = 0; i < loopCount; i++) {
            Guild guild = guilds.get(itemCounter++);
            ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items.guild"), bukkitPlayer, guild)
                    .material(Material.STONE);

            guildIndexMap.put(positions.get(i) - 1, guild);
            guiBuilder.item(positions.get(i) - 1, itemBuilder.build());
        }

        return guiBuilder.build();
    }
}
