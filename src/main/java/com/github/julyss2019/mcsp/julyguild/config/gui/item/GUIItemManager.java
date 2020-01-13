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
    /**
     * 得到优先级物品
     * @param section 配置节点
     * @param papiPlayer 玩家
     * @param guild 公会
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, OfflinePlayer papiPlayer, Guild guild) {
        return getPriorityItem(section, papiPlayer, guild, null);
    }

    /**
     * 得到优先级物品
     * @param section 配置节点
     * @param papiPlayer 玩家
     * @param guild 公会
     * @param placeholderBuilder 内部占位符构造器
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, OfflinePlayer papiPlayer, Guild guild, @Nullable Placeholder.Builder placeholderBuilder) {
        Placeholder finalPlaceholder;

        if (section.getBoolean("use_gp", false)) {
            finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
        } else {
            finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
        }

        return getPriorityItem(section, papiPlayer, finalPlaceholder);
    }

    /**
     * 得到优先级物品
     * 该物品支持插件内部占位符（由配置文件决定）
     * @param section 配置节点
     * @param guildMember 公会成员
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, GuildMember guildMember) {
        return getPriorityItem(section, guildMember, null);
    }

    /**
     * 得到优先级物品
     * 该物品支持插件内部占位符（由配置文件决定）
     * @param section 配置节点
     * @param guildMember 公会成员
     * @param placeholderBuilder 内部占位符构造器
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
        return getPriorityItem(section, guildMember.getGuildPlayer().getOfflineBukkitPlayer(), guildMember.getGuild(), placeholderBuilder);
    }

    /**
     * 得到优先级物品
     * @param section 配置节点
     * @param papiPlayer 玩家
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, OfflinePlayer papiPlayer) {
        return getPriorityItem(section, papiPlayer, (Placeholder) null);
    }

    /**
     * 得到优先级物品
     * @param section 配置节点
     * @param placeholder 内部占位符
     * @param papiPlayer 玩家
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, OfflinePlayer papiPlayer, @Nullable Placeholder placeholder) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        return new PriorityItem(section.getInt("priority"), getItemBuilder(section, papiPlayer, placeholder));
    }

    /**
     * 得到索引物品
     * @param section 配置节点
     * @param papiPlayer 玩家
     * @param guild 公会
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, OfflinePlayer papiPlayer, Guild guild) {
        return getIndexItem(section, papiPlayer, guild, null);
    }

    /**
     * 得到索引物品
     * @param section 配置节点
     * @param papiPlayer 玩家
     * @param guild 公会
     * @param placeholderBuilder 内部变量构造器
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, OfflinePlayer papiPlayer, Guild guild, @Nullable Placeholder.Builder placeholderBuilder) {
        Placeholder finalPlaceholder;

        if (section.getBoolean("use_gp", MainSettings.isGuiDefaultUseGp())) {
            finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
        } else {
            finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
        }

        return getIndexItem(section, papiPlayer, finalPlaceholder);
    }

    /**
     * 得到索引物品
     * 这个物品将根据配置文件中的 use_gp 项 来确定是否为其添加内部公会变量
     * @param section
     * @param guildMember
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, GuildMember guildMember) {
        return getIndexItem(section, guildMember, null);
    }

    /**
     * 得到索引物品
     * 这个物品将根据配置文件中的 use_gp 项 来确定是否为其添加内部公会变量
     * @param section 配置节点
     * @param guildMember 公会成员
     * @param placeholderBuilder 内部占位符构造器
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
        return getIndexItem(section, guildMember.getGuildPlayer().getOfflineBukkitPlayer(), guildMember.getGuild(), placeholderBuilder);
    }

    /**
     * 得到索引物品
     * @param section 配置节点
     * @param papiPlayer 玩家
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, OfflinePlayer papiPlayer) {
        return getIndexItem(section, papiPlayer, (Placeholder) null);
    }

    /**
     * 得到索引物品
     * @param section 配置节点
     * @param papiPlayer 玩家
     * @param placeholder 占位符
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, OfflinePlayer papiPlayer, @Nullable Placeholder placeholder) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        if (section.getInt("index", 0) <= 0) {
            throw new RuntimeException(section.getCurrentPath() + ".index 不合法");
        }

        return new IndexItem(section.getInt("index") - 1, getItemBuilder(section, papiPlayer, placeholder));
    }

    public static ItemBuilder getItemBuilder(ConfigurationSection section, OfflinePlayer papiPlayer, Guild guild) {
        return getItemBuilder(section, papiPlayer, guild, null);
    }

    public static ItemBuilder getItemBuilder(ConfigurationSection section, OfflinePlayer papiPlayer, Guild guild, @Nullable Placeholder.Builder placeholderBuilder) {
        Placeholder finalPlaceholder;

        if (section.getBoolean("use_gp", MainSettings.isGuiDefaultUseGp())) {
            finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
        } else {
            finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
        }

        return getItemBuilder(section, papiPlayer, finalPlaceholder);
    }

    public static ItemBuilder getItemBuilder(ConfigurationSection section, GuildMember guildMember) {
        return getItemBuilder(section, guildMember, null);
    }

    public static ItemBuilder getItemBuilder(ConfigurationSection section, GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
        return getItemBuilder(section, guildMember.getGuildPlayer().getOfflineBukkitPlayer(), guildMember.getGuild(), placeholderBuilder);
    }

    public static ItemBuilder getItemBuilder(ConfigurationSection section, @Nullable OfflinePlayer papiPlayer) {
        return getItemBuilder(section, papiPlayer, (Placeholder) null);
    }

    /**
     * 得到 ItemBuilder
     * 这个 ItemBuilder 是否着色将由配置文件的 colored 节点确定，默认为 true
     * @param section 配置节点
     * @param papiPlayer 玩家
     * @param placeholder 内部占位符
     * @return
     */
    public static ItemBuilder getItemBuilder(ConfigurationSection section, @Nullable OfflinePlayer papiPlayer, @Nullable Placeholder placeholder) {
        return getItemBuilder(section, papiPlayer, placeholder, section.getBoolean("colored", true));
    }

    /**
     * 得到 ItemBuilder
     * @param section 配置
     * @param placeholder 占位符
     * @param papiPlayer 玩家
     * @param colored 是否着色
     * @return
     */
    public static ItemBuilder getItemBuilder(ConfigurationSection section, @Nullable OfflinePlayer papiPlayer, @Nullable Placeholder placeholder, boolean colored) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            itemBuilder.material(Material.valueOf(section.getString("material")));
        } catch (Exception e) {
            itemBuilder.material(Material.STONE);
            Util.sendColoredConsoleMessage("&c" + section.getCurrentPath() + ".material 不合法.");
        }

        itemBuilder
                .data((short) section.getInt("data", 0))
                .colored(colored);

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
     * @param text
     * @param placeholder
     * @param papiPlayer
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
     * @param list
     * @param placeholder
     * @param papiPlayer
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
