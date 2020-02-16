package com.github.julyss2019.mcsp.julyguild.config.gui.item;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class GUIItemManager {
    public static PriorityItem getPriorityItem(@NotNull ConfigurationSection section) {
        return getPriorityItem(section, (OfflinePlayer) null, null);
    }

    public static PriorityItem getPriorityItem(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer) {
        return getPriorityItem(section, papiPlayer, null);
    }

    public static PriorityItem getPriorityItem(@NotNull ConfigurationSection section, @Nullable PlaceholderContainer placeholderContainer) {
        return getPriorityItem(section, null, placeholderContainer);
    }

    // 实现方法
    public static PriorityItem getPriorityItem(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer, @Nullable PlaceholderContainer placeholderContainer) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        return new PriorityItem(section.getInt("priority"), getItemBuilder(section.getConfigurationSection("icon"), papiPlayer, placeholderContainer));
    }


    public static IndexItem getIndexItem(@NotNull ConfigurationSection section) {
        return getIndexItem(section, (OfflinePlayer) null, null);
    }

    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @Nullable PlaceholderContainer placeholderContainer) {
        return getIndexItem(section, null, placeholderContainer);
    }

    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer) {
        return getIndexItem(section, papiPlayer, null);
    }

    // 实现方法
    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer, @Nullable PlaceholderContainer placeholderContainer) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        if (section.getInt("index", 0) <= 0) {
            throw new RuntimeException("index 不合法: " + section.getCurrentPath());
        }

        return new IndexItem(section.getInt("index") - 1, getItemBuilder(section.getConfigurationSection("icon"), papiPlayer, placeholderContainer));
    }


    public static ItemBuilder getItemBuilder(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer) {
        return getItemBuilder(section, papiPlayer, null);
    }

    // 实现方法
    public static ItemBuilder getItemBuilder(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer, @Nullable PlaceholderContainer placeholderContainer) {
        ItemBuilder itemBuilder = new ItemBuilder();
        boolean usePapi = section.getBoolean("use_papi", MainSettings.isGuildGuiDefaultUsePapi());

        Material material;

        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            throw new RuntimeException(section.getCurrentPath() + ".material 不合法");
        }

        itemBuilder
                .material(material)
                .durability((short) section.getInt("durability", 0))
                .colored(section.getBoolean("colored", MainSettings.isGuildGuiDefaultColored()));

        if (MainSettings.isGuildGuiDefaultHideAllFlags()) {
            itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        }

        if (section.contains("display_name")) {
            itemBuilder.displayName(replacePlaceholders(section.getString("display_name"), placeholderContainer, !usePapi ? null : papiPlayer));
        }

        if (section.contains("lores")) {
            itemBuilder.lores(replacePlaceholders(section.getStringList("lores"), placeholderContainer, !usePapi ? null : papiPlayer));
        }

        if (section.contains("skull_owner")) {
            itemBuilder.skullOwner(placeholderContainer == null ? section.getString("skull_owner") : replacePlaceholders(section.getString("skull_owner"), placeholderContainer, !usePapi ? null : papiPlayer));
        }

        if (section.contains("skull_texture")) {
            itemBuilder.skullTexture(placeholderContainer == null ? section.getString("skull_texture") : replacePlaceholders(section.getString("skull_texture"), placeholderContainer, !usePapi ? null : papiPlayer));
        }

        if (section.contains("flags")) {
            for (String flagName : section.getStringList("flags")) {
                if (flagName.equals("*")) {
                    itemBuilder.addItemFlags(ItemFlag.values());
                    break;
                }

                try {
                    itemBuilder.addItemFlag(ItemFlag.valueOf(flagName));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("flags 不合法: " + section, e);
                }
            }
        }

        if (section.contains("enchantments")) {
            if (section.isConfigurationSection("enchantments")) {
                ConfigurationSection enchantmentSection = section.getConfigurationSection("enchantments");

                for (String enchantmentName : Optional.ofNullable(enchantmentSection.getKeys(false)).orElse(new HashSet<>())) {
                    itemBuilder.enchant(Enchantment.getByName(enchantmentName)
                            , section.getConfigurationSection("enchantments").getInt(enchantmentName));
                }
            } else {
                throw new RuntimeException("enchantments 不合法: " + section);
            }
        }

        return itemBuilder;
    }


    /**
     * 替换占位符
     * @param text 文本
     * @param placeholderContainer 占位符
     * @param papiPlayer 玩家（PAPI用）
     * @return
     */
    private static String replacePlaceholders(@NotNull String text, @Nullable PlaceholderContainer placeholderContainer, @Nullable OfflinePlayer papiPlayer) {
        String result = text;

        if (placeholderContainer != null) {
            result = PlaceholderText.replacePlaceholders(result, placeholderContainer);
        }

        if (papiPlayer != null && JulyGuild.getInstance().isPlaceHolderAPIEnabled()) {
            result = PlaceholderAPI.setPlaceholders(papiPlayer, result);
        }

        return result;
    }

    /**
     * 替换占位符
     * @param list 文本列表
     * @param placeholderContainer 占位符
     * @param papiPlayer 玩家（PAPI用）
     * @return
     */
    private static List<String> replacePlaceholders(@NotNull List<String> list, @Nullable PlaceholderContainer placeholderContainer, @Nullable OfflinePlayer papiPlayer) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(replacePlaceholders(s, placeholderContainer, papiPlayer));
        }

        return result;
    }
}
