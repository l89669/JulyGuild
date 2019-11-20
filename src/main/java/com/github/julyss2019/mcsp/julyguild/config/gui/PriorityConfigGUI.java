package com.github.julyss2019.mcsp.julyguild.config.gui;

import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriorityConfigGUI {
    public static class Builder extends IndexConfigGUI.Builder {
        private List<Integer> availablePositions;
        private Map<PriorityItem, ItemListener> priorityMap = new HashMap<>();

        public Builder(List<Integer> availablePositions) {
            this.availablePositions = availablePositions;
        }

        public Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer) {
            return fromConfig(section, papiPlayer, null, true);
        }

        public Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer, @Nullable Placeholder placeholder) {
            return fromConfig(section, papiPlayer, placeholder, true);
        }

        public Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer, @Nullable Placeholder placeholder, boolean colored) {
            super.fromConfig(section, papiPlayer, placeholder, colored);
            return this;
        }

        public Builder item(PriorityItem priorityItem) {
            this.priorityMap.put(priorityItem, null);
            return this;
        }

        public Builder item(PriorityItem priorityItem, @Nullable ItemListener itemListener) {
            this.priorityMap.put(priorityItem, itemListener);
            return this;
        }

        @Override
        public Inventory build() {
            if (availablePositions.size() < priorityMap.size()) {
                throw new IllegalArgumentException("物品数量超过了可供设置的数量");
            }

            List<PriorityItem> items = new ArrayList<>(priorityMap.keySet());

            items.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));

            for (int i = 0; i < items.size(); i++) {
                PriorityItem item = items.get(i);

                item(availablePositions.get(i), item.getItemBuilder().build(), priorityMap.get(item));
            }

            return super.build();
        }
    }
}
