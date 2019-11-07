package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class BaseMemberPageableGUI implements Pageable {
    private int currentPage;
    private GUIType guiType;
    private GuildMember guildMember;

    public BaseMemberPageableGUI(GUIType guiType, GuildMember guildMember) {
        this.guiType = guiType;
        this.guildMember = guildMember;
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
        return guildMember.getGuildPlayer();
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(null, InventoryType.CHEST, "BaseMemberPageableGUI");
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
