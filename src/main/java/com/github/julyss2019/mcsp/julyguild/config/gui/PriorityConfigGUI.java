package com.github.julyss2019.mcsp.julyguild.config.gui;

import com.github.julyss2019.mcsp.julyguild.config.gui.item.IndexItem;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.gui.PageableGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.apache.commons.lang3.Validate;
import org.bukkit.OfflinePlayer;
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
        private List<Integer> availableIndexes;
        private Map<PriorityItem, ItemListener> priorityMap = new HashMap<>();

        public Builder() {}

        @Override
        public PriorityConfigGUI.Builder fromConfig(ConfigurationSection section, GuildMember guildMember) {
            availableIndexes(Util.getIndexes(section.getString("indexes")));
            super.fromConfig(section, guildMember);
            return this;
        }

        @Override
        public PriorityConfigGUI.Builder fromConfig(ConfigurationSection section, GuildMember guildMember, Placeholder.@Nullable Builder placeholderBuilder) {
            availableIndexes(Util.getIndexes(section.getString("indexes")));
            super.fromConfig(section, guildMember, placeholderBuilder);
            return this;
        }

        @Override
        public PriorityConfigGUI.Builder fromConfig(ConfigurationSection section, Player papiPlayer, Guild guild) {
            availableIndexes(Util.getIndexes(section.getString("indexes")));
            super.fromConfig(section, papiPlayer, guild);
            return this;
        }

        @Override
        public PriorityConfigGUI.Builder fromConfig(ConfigurationSection section, Player papiPlayer, Guild guild, Placeholder.@Nullable Builder placeholderBuilder) {
            availableIndexes(Util.getIndexes(section.getString("indexes")));
            super.fromConfig(section, papiPlayer, guild, placeholderBuilder);
            return this;
        }

        @Override
        public PriorityConfigGUI.Builder fromConfig(ConfigurationSection section, Player papiPlayer) {
            availableIndexes(Util.getIndexes(section.getString("indexes")));
            super.fromConfig(section, papiPlayer);
            return this;
        }

        @Override
        public PriorityConfigGUI.Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer, @Nullable Placeholder placeholder) {
            availableIndexes(Util.getIndexes(section.getString("indexes")));
            super.fromConfig(section, papiPlayer, placeholder);
            return this;
        }

        @Override
        public PriorityConfigGUI.Builder fromConfig(ConfigurationSection section, @Nullable Player papiPlayer, @Nullable Placeholder placeholder, boolean colored) {
            availableIndexes(Util.getIndexes(section.getString("indexes")));
            super.fromConfig(section, papiPlayer, placeholder, colored);
            return this;
        }

        private Builder availableIndexes(List<Integer> availablePositions) {
            this.availableIndexes = availablePositions;
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
            Validate.notNull(availableIndexes, "availableIndexes 未设置");

            if (availableIndexes.size() < priorityMap.size()) {
                throw new IllegalArgumentException("物品数量超过了可供设置的数量");
            }

            List<PriorityItem> items = new ArrayList<>(priorityMap.keySet());

            items.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));

            for (int i = 0; i < items.size(); i++) {
                PriorityItem item = items.get(i);

                item(availableIndexes.get(i), item.getItemBuilder().build(), priorityMap.get(item));
            }

            return super.build();
        }
    }
}
