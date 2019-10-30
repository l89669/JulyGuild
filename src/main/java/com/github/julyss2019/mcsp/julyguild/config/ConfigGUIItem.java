package com.github.julyss2019.mcsp.julyguild.config;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public static ConfigGUIItem fromConfig(ConfigurationSection section) {
        return fromConfig(section, null, null, true);
    }

    public static ConfigGUIItem fromConfig(ConfigurationSection section, @Nullable Placeholder placeholder) {
        return fromConfig(section, placeholder, null, true);
    }

    public static ConfigGUIItem fromConfig(ConfigurationSection section, @Nullable Placeholder placeholder, @Nullable Player player) {
        return fromConfig(section, placeholder, player, true);
    }

    public static ConfigGUIItem fromConfig(ConfigurationSection section, @Nullable Placeholder placeholder, @Nullable Player player, boolean colored) {
        ItemBuilder itemBuilder = new ItemBuilder()
            .material(section.getString("material"))
            .data((short) section.getInt("data"))
            .displayName(replacePlaceholders(section.getString("display_name"), placeholder, player))
            .lores(replacePlaceholders(section.getStringList("lores"), placeholder, player))
            .colored(colored);

        if (section.contains("skull")) {
            itemBuilder.skullOwner(placeholder == null ? section.getString("skull") : PlaceholderText.replacePlaceholders(section.getString("skull"), placeholder));
        }

        return new ConfigGUIItem(itemBuilder, section.getInt("index") - 1);
    }

    private static String replacePlaceholders(String text, @Nullable Placeholder placeholder, @Nullable Player player) {
        String result = text;

        if (placeholder != null) {
            result = PlaceholderText.replacePlaceholders(result, placeholder);
        }

        if (player != null && JulyGuild.getInstance().isPlaceHolderAPIEnabled()) {
            result = PlaceholderAPI.setPlaceholders(player, result);
        }

        return result;
    }

    private static List<String> replacePlaceholders(List<String> list, @Nullable Placeholder placeholder, @Nullable Player player) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(replacePlaceholders(s, placeholder, player));
        }

        return result;
    }
}
