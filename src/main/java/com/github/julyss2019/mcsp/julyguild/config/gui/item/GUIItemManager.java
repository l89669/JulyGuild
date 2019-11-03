package com.github.julyss2019.mcsp.julyguild.config.gui.item;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.IndexItem;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GUIItemManager {
    public static ItemBuilder getItemBuilder(ConfigurationSection section) {
        return getItemBuilder(section, null, null, true);
    }

    /**
     * 得到优先级物品
     * @param section 配置
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section) {
        return getPriorityItem(section, null, null, true);
    }

    /**
     * 得到优先级物品
     * @param section 配置
     * @param placeholder 占位符
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, @Nullable Placeholder placeholder) {
        return getPriorityItem(section, placeholder, null, true);
    }

    public static PriorityItem getPriorityItem(ConfigurationSection section, @Nullable Player player) {
        return getPriorityItem(section, null, player, true);
    }

    /**
     * 得到优先级物品
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, @Nullable Placeholder placeholder, @Nullable Player player) {
        return getPriorityItem(section, placeholder, player, true);
    }

    /**
     * 得到优先级物品
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @param colored 是否转换颜色
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, @Nullable Placeholder placeholder, @Nullable Player player, boolean colored) {
        ItemBuilder itemBuilder = getItemBuilder(section, placeholder, player, colored);

        return new PriorityItem(section.getInt("priority"), itemBuilder);
    }

    /**
     * 得到 ItemBuilder
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @param colored 是否转换颜色
     * @return
     */
    public static ItemBuilder getItemBuilder(ConfigurationSection section, @Nullable Placeholder placeholder, @Nullable Player player, boolean colored) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            itemBuilder.material(Material.valueOf(section.getString("material")));
        } catch (Exception e) {
            itemBuilder.material(Material.STONE);
            Util.sendColoredConsoleMessage("&c" + section.getCurrentPath() + " material 不合法.");
        }

        itemBuilder
                .data((short) section.getInt("data", 0))
                .displayName(replacePlaceholders(section.getString("display_name"), placeholder, player))
                .colored(colored);

        if (section.contains("lores")) {
            itemBuilder.lores(replacePlaceholders(section.getStringList("lores"), placeholder, player));
        }

        if (section.contains("skullOwner")) {
            itemBuilder.skullOwner(placeholder == null ? section.getString("skull") : PlaceholderText.replacePlaceholders(section.getString("skull"), placeholder));
        }

        if (section.contains("skullTexture")) {
            itemBuilder.skullTexture(placeholder == null ? section.getString("skullTexture") : PlaceholderText.replacePlaceholders(section.getString("skullTexture"), placeholder));
        }

        return itemBuilder;
    }

    /**
     * 得到索引物品
     * @param section 配置
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section) {
        return getIndexItem(section, null, null, true);
    }

    /**
     * 得到索引物品
     * @param section 配置
     * @param player 玩家
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, @Nullable Player player) {
        return getIndexItem(section, null, player, true);
    }

    /**
     * 得到索引物品
     * @param section 配置
     * @param placeholder 占位符
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, @Nullable Placeholder placeholder) {
        return getIndexItem(section, placeholder, null, true);
    }

    public static IndexItem getIndexItem(ConfigurationSection section, @Nullable Placeholder placeholder, @Nullable Player player) {
        return getIndexItem(section, placeholder, player, true);
    }

    /**
     * 得到索引物品
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @param colored 是否转换颜色
     * @return
     */
    public static IndexItem getIndexItem(@NotNull ConfigurationSection section, @Nullable Placeholder placeholder, @Nullable Player player, boolean colored) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        return new IndexItem(getItemBuilder(section, placeholder, player, colored), section.getInt("index") - 1);
    }

    private static String replacePlaceholders(@NotNull String text, @Nullable Placeholder placeholder, @Nullable Player player) {
        String result = text;

        if (placeholder != null) {
            result = PlaceholderText.replacePlaceholders(result, placeholder);
        }

        if (player != null && JulyGuild.getInstance().isPlaceHolderAPIEnabled()) {
            result = PlaceholderAPI.setPlaceholders(player, result);
        }

        return result;
    }

    private static List<String> replacePlaceholders(@NotNull List<String> list, @Nullable Placeholder placeholder, @Nullable Player player) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(replacePlaceholders(s, placeholder, player));
        }

        return result;
    }
}
