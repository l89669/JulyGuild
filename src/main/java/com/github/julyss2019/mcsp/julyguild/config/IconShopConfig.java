package com.github.julyss2019.mcsp.julyguild.config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IconShopConfig {
    private ConfigGuildIcon defaultIcon; // 默认图标
    private List<ConfigGuildIcon> icons = new ArrayList<>();

    public void reset() {
        icons.clear();
    }

    /**
     * 添加图标
     * @param icon
     */
    public void addIcon(@NotNull ConfigGuildIcon icon) {
        icons.add(icon);
    }

    /**
     * 得到所有图标
     * @return
     */
    public List<ConfigGuildIcon> getIcons() {
        return new ArrayList<>(icons);
    }

    /**
     * 得到默认图标
     * @return
     */
    public ConfigGuildIcon getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * 设置默认图标
     * @param defaultIcon
     */
    public void setDefaultIcon(ConfigGuildIcon defaultIcon) {
        this.defaultIcon = defaultIcon;
    }
}
