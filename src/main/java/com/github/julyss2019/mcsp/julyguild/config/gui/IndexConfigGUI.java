package com.github.julyss2019.mcsp.julyguild.config.gui;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.IndexItem;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class IndexConfigGUI {
    public static class Builder extends InventoryBuilder {
        /**
         * 从配置文件载入相关设置
         * 支持公会变量
         * @param section
         * @param guildMember 公会成员
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, GuildMember guildMember) {
            return fromConfig(section, guildMember, null);
        }

        /**
         * 从配置文件载入相关设置
         * 支持公会变量
         * @param section
         * @param guildMember
         * @param placeholderBuilder
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
            Placeholder finalPlaceholder;
            Guild guild = guildMember.getGuild();

            if (section.getBoolean("use_gp", false)) {
                finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
            } else {
                finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
            }

            return fromConfig(section, guildMember.getBukkitPlayer(), finalPlaceholder);
        }

        /**
         * 从配置载入相关设置
         * @param section
         * @param papiPlayer
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer) {
            return fromConfig(section, papiPlayer, null);
        }

        /**
         * 从配置载入相关设置
         * @param section
         * @param papiPlayer
         * @return
         */
        public Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer, @Nullable Placeholder placeholder) {
            return fromConfig(section, papiPlayer, placeholder, section.getBoolean("colored", true));
        }

        /**
         * 从配置文件载入相关设置
         * @param section
         * @param papiPlayer
         * @param placeholder
         * @param colored
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
                    item(new IndexItem(section.getInt("index") - 1, GUIItemManager.getItemBuilder(keySection, player, null)));
                } else {
                    for (int index : Util.getIntegerList(keySection.getString("index"))) {
                        item(new IndexItem(index - 1, GUIItemManager.getItemBuilder(keySection, player, null)));
                    }
                }
            }

            return this;
        }


        public Builder item(@Nullable IndexItem item, ItemListener itemListener) {
            if (item != null) {
                item(item.getIndex(), item.getItemBuilder().build(), itemListener);
            }

            return this;
        }

        public Builder item(@Nullable IndexItem item) {
            item(item, null);
            return this;
        }
    }
}
