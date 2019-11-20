package com.github.julyss2019.mcsp.julyguild.config.gui.item;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
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
     * @param section
     * @param player
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, @Nullable Player player) {
        return getPriorityItem(section, player, null, true);
    }

    /**
     * 得到优先级物品
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, @Nullable Player player, @Nullable Placeholder placeholder) {
        return getPriorityItem(section, player, placeholder, true);
    }

    /**
     * 得到优先级物品
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @param colored 是否转换颜色
     * @return
     */
    public static PriorityItem getPriorityItem(ConfigurationSection section, @Nullable Player player, @Nullable Placeholder placeholder, boolean colored) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        ItemBuilder itemBuilder = getItemBuilder(section, placeholder, player, colored);

        return new PriorityItem(section.getInt("priority"), itemBuilder);
    }

    /**
     * 得到索引物品
     * @param section
     * @param player
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, @Nullable Player player) {
        return getIndexItem(section, player, null, true);
    }

    /**
     * 得到索引物品
     * @param section
     * @param placeholder
     * @param player
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section, @Nullable Player player, @Nullable Placeholder placeholder) {
        return getIndexItem(section, player, placeholder, true);
    }

    /**
     * 得到索引物品
     * @param section 配置
     * @param placeholder 占位符
     * @param player 玩家，用于PAPI
     * @param colored 是否转换颜色
     * @return
     */
    public static IndexItem getIndexItem(ConfigurationSection section,@Nullable Player player,  @Nullable Placeholder placeholder, boolean colored) {
        if (!section.getBoolean("enabled", true)) {
            return null;
        }

        if (section.getInt("index", 0) <= 0) {
            throw new RuntimeException(section.getCurrentPath() + ".index 不合法");
        }

        return new IndexItem(section.getInt("index") - 1, getItemBuilder(section, placeholder, player, colored));
    }

    /**
     * 得到物品
     * @param section
     * @return
     */
    public static ItemBuilder getItemBuilder(ConfigurationSection section) {
        return getItemBuilder(section, null, null, true);
    }

    public static ItemBuilder getItemBuilder(ConfigurationSection section, Placeholder placeholder, Player player) {
        return getItemBuilder(section, placeholder, player, true);
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
            Util.sendColoredConsoleMessage("&c" + section.getCurrentPath() + ".material 不合法.");
        }

        itemBuilder
                .data((short) section.getInt("data", 0))
                .colored(colored);

        boolean sectionPapiEnabled = section.getBoolean("papi");

        if (section.contains("display_name")) {
            itemBuilder.displayName(replacePlaceholders(section.getString("display_name"), placeholder, !sectionPapiEnabled ? null : player));
        }

        if (section.contains("lores")) {
            itemBuilder.lores(replacePlaceholders(section.getStringList("lores"), placeholder, !sectionPapiEnabled ? null : player));
        }

        if (section.contains("skullOwner")) {
            itemBuilder.skullOwner(placeholder == null ? section.getString("skull") : replacePlaceholders(section.getString("skull"), placeholder, !sectionPapiEnabled ? null : player));
        }

        if (section.contains("skullTexture")) {
            itemBuilder.skullTexture(placeholder == null ? section.getString("skullTexture") : replacePlaceholders(section.getString("skullTexture"), placeholder, !sectionPapiEnabled ? null : player));
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
