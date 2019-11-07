package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class BasePlayerPageableGUI implements Pageable {
    private int currentPage;
    protected final GUIType guiType;
    protected final GuildPlayer guildPlayer;

    public BasePlayerPageableGUI(GUIType guiType, GuildPlayer guildPlayer) {
        this.guiType = guiType;
        this.guildPlayer = guildPlayer;
    }

    @Override
    public void build() {
        setCurrentPage(0);
    }

    @Override
    public GUIType getType() {
        return guiType;
    }

    @Override
    public void nextPage() {
        if (!hasNext()) {
            throw new IllegalArgumentException("没有下一页了");
        }

        currentPage++;
        setCurrentPage(currentPage);
    }

    @Override
    public void previousPage() {
        if (!hasPrecious()) {
            throw new IllegalArgumentException("没有上一页了");
        }

        currentPage--;
        setCurrentPage(currentPage);
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(null, InventoryType.CHEST, "BasePlayerPageableGUI");
    }

    // 需要子类来覆盖
    @Override
    public void setCurrentPage(int page) {
        if (page < 0 || page >= getTotalPage()) {
            throw new IllegalArgumentException("页数不合法: " + page);
        }
    }

    // 需要子类来覆盖
    @Override
    public int getTotalPage() {
        return 0;
    }
}
