package com.github.julyss2019.mcsp.julyguild.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IconShopConfig {
    private static ConfigGuildIcon defaultIcon; // 默认图标
    private static Map<String, ConfigGuildIcon> iconMap = new HashMap<>();

    public static void reset() {
        iconMap.clear();
    }

    public static ConfigGuildIcon getIcon(String name) {
        return iconMap.get(name);
    }

    /**
     * 添加图标
     * @param icon
     */
    public static void addIcon(ConfigGuildIcon icon) {
        iconMap.put(icon.getName(), icon);
    }

    /**
     * 得到所有图标
     * @return
     */
    public static Collection<ConfigGuildIcon> getIconMap() {
        return iconMap.values();
    }

    /**
     * 得到默认图标
     * @return
     */
    public static ConfigGuildIcon getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * 设置默认图标
     * @param defaultIcon
     */
    public static void setDefaultIcon(ConfigGuildIcon defaultIcon) {
        IconShopConfig.defaultIcon = defaultIcon;
    }
}
