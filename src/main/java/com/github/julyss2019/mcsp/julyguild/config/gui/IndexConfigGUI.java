package com.github.julyss2019.mcsp.julyguild.config.gui;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.IndexItem;
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

        public Builder fromConfig(ConfigurationSection section, @org.jetbrains.annotations.Nullable Placeholder placeholder, @Nullable Player papiPlayer, boolean colored) {
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

        public Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer) {
            return fromConfig(section, null, papiPlayer, true);
        }

        public Builder fromConfig(ConfigurationSection section, @Nullable Placeholder placeholder, @Nullable Player papiPlayer) {
            return fromConfig(section, placeholder, papiPlayer, true);
        }

        private Builder setCustomItems(ConfigurationSection section, Player player) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection keySection = section.getConfigurationSection(key);

                if (keySection.isInt("index")) {
                    item(new IndexItem(section.getInt("index") - 1, GUIItemManager.getItemBuilder(keySection, null, player)));
                } else {
                    for (int index : Util.getIntegerList(keySection.getString("index"))) {
                        item(new IndexItem(index - 1, GUIItemManager.getItemBuilder(keySection, null, player)));
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
