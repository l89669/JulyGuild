package com.github.julyss2019.mcsp.julyguild.config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuildShopConfig {
    private List<ConfigGuildShopItem> shopItems = new ArrayList<>();

    /**
     * 重置
     */
    public void reset() {
        shopItems.clear();
    }

    /**
     * 添加商店物品
     * @param item
     */
    public void addItem(@NotNull ConfigGuildShopItem item) {
        shopItems.add(item);
    }

    /**
     * 对商店物品进行排序
     * 按index
     */
    public void sortItems() {
        shopItems.sort(Comparator.comparingInt(ConfigGuildShopItem::getIndex));
    }

    /**
     * 得到所有商店物品
     * @return
     */
    public List<ConfigGuildShopItem> getShopItems() {
        return new ArrayList<>(shopItems);
    }
}
