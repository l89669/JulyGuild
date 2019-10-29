package com.github.julyss2019.mcsp.julyguild.config;

import com.github.julyss2019.mcsp.julyguild.Placeholder;
import com.github.julyss2019.mcsp.julyguild.PlaceholderText;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class ConfigGUIItem {
    private ItemBuilder itemBuilder;
    private int index;

    public ConfigGUIItem(ItemBuilder itemBuilder, int index) {
        this.itemBuilder = itemBuilder;
        this.index = index;
    }

    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }

    public int getIndex() {
        return index;
    }

    public static ConfigGUIItem get(ConfigurationSection section) {
        return get(section, null, true);
    }

    public static ConfigGUIItem get(ConfigurationSection section, @Nullable Placeholder placeholder) {
        return get(section, placeholder, true);
    }

    public static ConfigGUIItem get(ConfigurationSection section, @Nullable Placeholder placeholder, boolean colored) {
        ItemBuilder itemBuilder = new ItemBuilder()
            .material(section.getString("material"))
            .data((short) section.getInt("data"))
            .displayName(placeholder == null ? section.getString("display_name") : PlaceholderText.replacePlaceholders(section.getString("display_name"), placeholder))
            .lores(placeholder == null ? section.getStringList("lores") : PlaceholderText.replacePlaceholders(section.getStringList("lores"), placeholder))
            .colored(colored);

        return new ConfigGUIItem(itemBuilder, section.getInt("index") - 1);
    }
}
