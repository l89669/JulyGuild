package com.github.julyss2019.mcsp.julyguild.config.gui.item;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.util.Util;
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

    public static PriorityItem getPriorityItem(@NotNull ConfigurationSection section, @NotNull GuildMember guildMember) {
        return getPriorityItem(section, guildMember, null);
    }

    public static PriorityItem getPriorityItem(@NotNull ConfigurationSection section, @NotNull GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
        Placeholder finalPlaceholder;
        Guild guild = guildMember.getGuild();

        if (section.getBoolean("use_gp", MainSettings.isGuiDefaultUseGp())) {
            finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
        } else {
            finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
        }



        return getPriorityItem(section, guildMember.getGuildPlayer().getOfflineBukkitPlayer(), finalPlaceholder);
    }

    public static PriorityItem getPriorityItem(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer) {
        return getPriorityItem(section, papiPlayer, null);
    }

    // 实现方法
    public static PriorityItem getPriorityItem(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer, @Nullable Placeholder placeholder) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        return new PriorityItem(section.getInt("priority"), getItemBuilder(section, papiPlayer, placeholder));
    }

    public static IndexItem getIndexItem(@NotNull ConfigurationSection section) {
        return getIndexItem(section, (OfflinePlayer) null, null);
    }

    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @NotNull GuildMember guildMember) {
        return getIndexItem(section, guildMember, null);
    }

    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @NotNull GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
        Placeholder finalPlaceholder;
        Guild guild = guildMember.getGuild();

        if (section.getBoolean("use_gp", MainSettings.isGuiDefaultUseGp())) {
            finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
        } else {
            finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
        }

        return getIndexItem(section, guildMember.getGuildPlayer().getOfflineBukkitPlayer(), finalPlaceholder);
    }

    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @Nullable Placeholder placeholder) {
        return getIndexItem(section, null, placeholder);
    }

    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer) {
        return getIndexItem(section, papiPlayer, null);
    }

    // 实现方法
    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer, @Nullable Placeholder placeholder) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        if (section.getInt("index", 0) <= 0) {
            throw new RuntimeException(section.getCurrentPath() + ".index 不合法");
        }

        return new IndexItem(section.getInt("index") - 1, getItemBuilder(section, papiPlayer, placeholder));
    }

    public static ItemBuilder getItemBuilder(@NotNull ConfigurationSection section, @NotNull GuildMember guildMember) {
        return getItemBuilder(section, guildMember, null);
    }

    public static ItemBuilder getItemBuilder(@NotNull ConfigurationSection section, @NotNull GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
        Placeholder finalPlaceholder;
        Guild guild = guildMember.getGuild();

        if (section.getBoolean("use_gp", MainSettings.isGuiDefaultUseGp())) {
            finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
        } else {
            finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
        }

        return getItemBuilder(section, guildMember.getGuildPlayer().getBukkitPlayer(), finalPlaceholder);
    }

    public static ItemBuilder getItemBuilder(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer) {
        return getItemBuilder(section, papiPlayer, null);
    }

    public static ItemBuilder getItemBuilder(@NotNull ConfigurationSection section, @Nullable OfflinePlayer papiPlayer, @Nullable Placeholder placeholder) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            itemBuilder.material(Material.valueOf(section.getString("material")));
        } catch (Exception e) {
            itemBuilder.material(Material.STONE);
            Util.sendColoredConsoleMessage("&c" + section.getCurrentPath() + ".material 不合法.");
        }

        itemBuilder
                .data((short) section.getInt("data", 0))
                .colored(section.getBoolean("colored", MainSettings.isGuiDefaultColored()));

        boolean usePapi = section.getBoolean("use_papi", MainSettings.isGuiDefaultUsePapi());

        if (section.contains("display_name")) {
            itemBuilder.displayName(replacePlaceholders(section.getString("display_name"), placeholder, !usePapi ? null : papiPlayer));
        }

        if (section.contains("lores")) {
            itemBuilder.lores(replacePlaceholders(section.getStringList("lores"), placeholder, !usePapi ? null : papiPlayer));
        }

        if (section.contains("skull_owner")) {
            itemBuilder.skullOwner(placeholder == null ? section.getString("skull_owner") : replacePlaceholders(section.getString("skull_owner"), placeholder, !usePapi ? null : papiPlayer));
        }

        if (section.contains("skull_texture")) {
            itemBuilder.skullTexture(placeholder == null ? section.getString("skull_texture") : replacePlaceholders(section.getString("skull_texture"), placeholder, !usePapi ? null : papiPlayer));
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
     * @param placeholder 占位符
     * @param papiPlayer 玩家（PAPI用）
     * @return
     */
    private static String replacePlaceholders(@NotNull String text, @Nullable Placeholder placeholder, @Nullable OfflinePlayer papiPlayer) {
        String result = text;

        if (placeholder != null) {
            result = PlaceholderText.replacePlaceholders(result, placeholder);
        }

        if (papiPlayer != null && JulyGuild.getInstance().isPlaceHolderAPIEnabled()) {
            result = PlaceholderAPI.setPlaceholders(papiPlayer, result);
        }

        return result;
    }

    /**
     * 替换占位符
     * @param list 文本列表
     * @param placeholder 占位符
     * @param papiPlayer 玩家（PAPI用）
     * @return
     */
    private static List<String> replacePlaceholders(@NotNull List<String> list, @Nullable Placeholder placeholder, @Nullable OfflinePlayer papiPlayer) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(replacePlaceholders(s, placeholder, papiPlayer));
        }

        return result;
    }
}
