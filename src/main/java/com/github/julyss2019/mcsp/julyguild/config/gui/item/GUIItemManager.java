package com.github.julyss2019.mcsp.julyguild.config.gui.item;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GUIItemManager {
    /**
     * 得到优先级物品
     * 该物品支持插件内部占位符（由配置文件决定）
     * @param section
     * @param guildMember
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, GuildMember guildMember) {
        return getPriorityItem(section, guildMember, null);
    }

    /**
     * 得到优先级物品
     * 该物品支持插件内部占位符（由配置文件决定）
     * @param section
     * @param guildMember
     * @param placeholderBuilder
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
        Guild guild = guildMember.getGuild();
        Placeholder finalPlaceholder;

        if (section.getBoolean("use_gp", false)) {
            finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
        } else {
            finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
        }

        return getPriorityItem(section, guildMember.getBukkitPlayer(), finalPlaceholder);
    }

    /**
     * 得到优先级物品
     * @param section
     * @param player
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, Player player) {
        return getPriorityItem(section, player, null);
    }

    /**
     * 得到优先级物品
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, Player player, @Nullable Placeholder placeholder) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        return new PriorityItem(section.getInt("priority"), getItemBuilder(section, player, placeholder));
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
     * @param section
     * @param guildMember
     * @param placeholderBuilder
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, GuildMember guildMember, @Nullable Placeholder.Builder placeholderBuilder) {
        Guild guild = guildMember.getGuild();
        Placeholder finalPlaceholder;

        if (section.getBoolean("use_gp", false)) {
            finalPlaceholder = placeholderBuilder == null ? new Placeholder.Builder().addGuildPlaceholders(guild).build() : placeholderBuilder.addGuildPlaceholders(guild).build();
        } else {
            finalPlaceholder = placeholderBuilder == null ? null : placeholderBuilder.build();
        }

        return getIndexItem(section, guildMember.getBukkitPlayer(), finalPlaceholder);
    }

    /**
     * 得到索引物品
     * @param section
     * @param player
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, @Nullable Player player) {
        return getIndexItem(section, player, null);
    }

    /**
     * 得到索引物品
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, @Nullable Player player,  @Nullable Placeholder placeholder) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        if (section.getInt("index", 0) <= 0) {
            throw new RuntimeException(section.getCurrentPath() + ".index 不合法");
        }

        return new IndexItem(section.getInt("index") - 1, getItemBuilder(section, player, placeholder));
    }

    /**
     * 得到 ItemBuilder
     * 这个 ItemBuilder 是否着色将由配置文件的 colored 节点确定，默认为 true
     * @param section
     * @param player
     * @param placeholder
     * @return
     */
    public static ItemBuilder getItemBuilder(ConfigurationSection section, @Nullable Player player, @Nullable Placeholder placeholder) {
        return getItemBuilder(section, player, placeholder, section.getBoolean("colored", true));
    }

    /**
     * 得到 ItemBuilder
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @param colored 是否着色
     * @return
     */
    public static ItemBuilder getItemBuilder(ConfigurationSection section, @Nullable Player player, @Nullable Placeholder placeholder, boolean colored) {
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

        boolean usePapi = section.getBoolean("use_papi");

        if (section.contains("display_name")) {
            itemBuilder.displayName(replacePlaceholders(section.getString("display_name"), placeholder, !usePapi ? null : player));
        }

        if (section.contains("lores")) {
            itemBuilder.lores(replacePlaceholders(section.getStringList("lores"), placeholder, !usePapi ? null : player));
        }

        if (section.contains("skullOwner")) {
            itemBuilder.skullOwner(placeholder == null ? section.getString("skull") : replacePlaceholders(section.getString("skull"), placeholder, !usePapi ? null : player));
        }

        if (section.contains("skullTexture")) {
            itemBuilder.skullTexture(placeholder == null ? section.getString("skullTexture") : replacePlaceholders(section.getString("skullTexture"), placeholder, !usePapi ? null : player));
        }

        if (section.contains("flags")) {
            for (String flagName : section.getStringList("flags")) {
                itemBuilder.addItemFlag(ItemFlag.valueOf(flagName));
            }
        }

        if (section.contains("enchantments")) {
            for (String enchantment : section.getConfigurationSection("enchantments").getKeys(false)) {
                itemBuilder.enchant(Enchantment.getByName(enchantment)
                        , section.getConfigurationSection("enchantments").getInt(enchantment));
            }
        }

        return itemBuilder;
    }

    /**
     * 替换占位符
     * @param text
     * @param placeholder
     * @param player
     * @return
     */
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

    /**
     * 替换占位符
     * @param list
     * @param placeholder
     * @param player
     * @return
     */
    private static List<String> replacePlaceholders(List<String> list, @Nullable Placeholder placeholder, @Nullable Player player) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(replacePlaceholders(s, placeholder, player));
        }

        return result;
    }
}
