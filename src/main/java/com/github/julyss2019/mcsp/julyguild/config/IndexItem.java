package com.github.julyss2019.mcsp.julyguild.config;

import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;

public class IndexItem {
    private ItemBuilder itemBuilder;
    private int index;

    IndexItem(ItemBuilder itemBuilder, int index) {
        this.itemBuilder = itemBuilder;
        this.index = index;
    }

    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }

    public int getIndex() {
        return index;
    }
}
