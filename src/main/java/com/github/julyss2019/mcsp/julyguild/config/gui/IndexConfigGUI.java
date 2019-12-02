package com.github.julyss2019.mcsp.julyguild.config.gui;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.IndexItem;
import com.github.julyss2019.mcsp.julyguild.gui.PageableGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 提供了各种方法：支持了PAPI变量，公会变量，内部变量
 */
public class IndexConfigGUI {
    public static class Builder extends InventoryBuilder {
        /**
         * 从配置文件载入相关设置
         * 支持公会变量
         * @param section 配置节点
         * @param guildMember 公会成员
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, GuildMember guildMember) {
            return fromConfig(section, guildMember, null);
        }

        /**
         * 从配置文件载入相关设置
         * 支持公会变量
         * @param section 配置节点
         * @param guildMember 公会成员
         * @param placeholderBuilder 内部占位符构造器
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
            return fromConfig(section, guildMember.getGuildPlayer().getBukkitPlayer(), guildMember.getGuild(), placeholderBuilder);
        }

        /**
         * 从配置文件载入相关设置
         * @param section 配置节点
         * @param papiPlayer 玩家
         * @param guild 公会
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, Player papiPlayer, Guild guild) {
            return fromConfig(section, papiPlayer, guild, null);
        }

        /**
         * 从配置文件载入相关设置
         * @param section 配置节点
         * @param papiPlayer 玩家
         * @param guild 公会
         * @param placeholderBuilder 内部占位符构造器
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, Player papiPlayer, Guild guild, @Nullable Placeholder.Builder placeholderBuilder) {
            Placeholder finalPlaceholder;

            if (section.getBoolean("use_gp", false)) {
                finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
            } else {
                finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
            }

            return fromConfig(section, papiPlayer, finalPlaceholder);
        }

        /**
         * 从配置文件载入相关设置
         * @param section 配置节点
         * @param papiPlayer 玩家
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, Player papiPlayer) {
            return fromConfig(section, papiPlayer, (Placeholder) null);
        }

        /**
         * 从配置文件载入相关设置
         * @param section 配置节点
         * @param papiPlayer 玩家
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer, @Nullable Placeholder placeholder) {
            return fromConfig(section, papiPlayer, placeholder, section.getBoolean("colored", true));
        }

        /**
         * 从配置文件载入相关设置
         * @param section 配置节点
         * @param papiPlayer 玩家
         * @param placeholder 内部占位符
         * @param colored 着色
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer, @Nullable Placeholder placeholder, boolean colored) {
            row(section.getInt( "row"));

            String finalTitle = section.getString("title");

            if (placeholder != null) {
                finalTitle = PlaceholderText.replacePlaceholders(section.getString("title"), placeholder);
            }

            if (JulyGuild.getInstance().isPlaceHolderAPIEnabled() && papiPlayer != null) {
                finalTitle = PlaceholderAPI.setPlaceholders(papiPlayer, finalTitle);
            }

            title(finalTitle);
            colored(colored);

            // 其他物品
            if (section.contains("other_items")) {
                setCustomItems(section.getConfigurationSection("other_items"), papiPlayer);
            }

            return this;
        }

        private Builder setCustomItems(ConfigurationSection section, Player player) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection keySection = section.getConfigurationSection(key);

                if (keySection.isInt("index")) {
                    item(new IndexItem(section.getInt("index") - 1, GUIItemManager.getItemBuilder(keySection, player)));
                } else {
                    for (int index : Util.getRangeIntegerList(keySection.getString("index"))) {
                        item(new IndexItem(index - 1, GUIItemManager.getItemBuilder(keySection, player)));
                    }
                }
            }

            return this;
        }

        public Builder pageItems(ConfigurationSection section, PageableGUI pageableGUI, OfflinePlayer offlinePlayer, Guild guild) {
            item(GUIItemManager.getIndexItem(section.getConfigurationSection("precious_page").getConfigurationSection(pageableGUI.hasPreciousPage() ? "have" : "not_have"), offlinePlayer, guild), !pageableGUI.hasPreciousPage() ? null : new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    pageableGUI.previousPage();
                }
            });
            item(GUIItemManager.getIndexItem(section.getConfigurationSection("next_page").getConfigurationSection(pageableGUI.hasNextPage() ? "have" : "not_have"), offlinePlayer, guild), !pageableGUI.hasNextPage() ? null : new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    pageableGUI.nextPage();
                }
            });

            return this;
        }

        public Builder pageItems(ConfigurationSection section, PageableGUI pageableGUI, OfflinePlayer offlinePlayer) {
            item(GUIItemManager.getIndexItem(section.getConfigurationSection("precious_page").getConfigurationSection(pageableGUI.hasPreciousPage() ? "have" : "not_have"), offlinePlayer), !pageableGUI.hasPreciousPage() ? null : new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    pageableGUI.previousPage();
                }
            });
            item(GUIItemManager.getIndexItem(section.getConfigurationSection("next_page").getConfigurationSection(pageableGUI.hasNextPage() ? "have" : "not_have"), offlinePlayer), !pageableGUI.hasNextPage() ? null : new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    pageableGUI.nextPage();
                }
            });

            return this;
        }

        public Builder pageItems(ConfigurationSection section, PageableGUI pageableGUI, GuildMember guildMember) {
            item(GUIItemManager.getIndexItem(section.getConfigurationSection("precious_page").getConfigurationSection(pageableGUI.hasPreciousPage() ? "have" : "not_have"), guildMember), !pageableGUI.hasPreciousPage() ? null : new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    pageableGUI.previousPage();
                }
            });
            item(GUIItemManager.getIndexItem(section.getConfigurationSection("next_page").getConfigurationSection(pageableGUI.hasNextPage() ? "have" : "not_have"), guildMember), !pageableGUI.hasNextPage() ? null : new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    pageableGUI.nextPage();
                }
            });

            return this;
        }

        /**
         * 应用索引物品
         * @param item 物品
         * @param itemListener 物品监听器
         * @return
         */
        public Builder item(@Nullable IndexItem item, ItemListener itemListener) {
            if (item != null) {
                item(item.getIndex(), item.getItemBuilder().build(), itemListener);
            }

            return this;
        }

        /**
         * 应用索引物品
         * @param item 物品
         * @return
         */
        public Builder item(@Nullable IndexItem item) {
            item(item, null);
            return this;
        }
    }
}
