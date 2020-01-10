package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

/**
 * 采用被动式更新设计，每次点击如果遇到无效的情况则强制更新，否则继续使用
 */
public abstract class GUI {
    private Inventory currentInventory;

    public abstract GuildPlayer getGuildPlayer();

    public abstract Inventory createInventory();

    public abstract GUIType getType();

    public abstract GUI getLastGUI();

    public void back() {
        GUI lastGUI = Optional.ofNullable(getLastGUI()).orElseThrow(() -> new RuntimeException("没有上一个GUI了"));

        lastGUI.reopen();
    }

    public Player getBukkitPlayer() {
        return getGuildPlayer().getBukkitPlayer();
    }

    public void close() {
        getGuildPlayer().closeGUI();
    }

    public void open() {
        if (!canUse()) {
            if (getLastGUI() != null) {
                getLastGUI().reopen();
            }

            return;
        }

        Inventory inventory = createInventory();

        this.currentInventory = inventory;
        getGuildPlayer().getBukkitPlayer().openInventory(inventory);
        getGuildPlayer().setUsingGUI(this);
    }

    public Inventory getCurrentInventory() {
        return currentInventory;
    }

    /**
     * 关闭，更新，打开
     */

    public void reopen() {
        close();
        open();
    }

    /**
     * 决定了该GUI是否能打开
     * @return
     */
    public abstract boolean canUse();
}
