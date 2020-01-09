package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public interface GUI {
    GuildPlayer getGuildPlayer();

    Inventory getInventory();

    GUIType getType();

    GUI getLastGUI();

    default void back() {
        GUI gui = Optional.ofNullable(getLastGUI()).orElseThrow(() -> new RuntimeException("没有上一个GUI了"));

        gui.reopen();
    }

    default Player getBukkitPlayer() {
        return getGuildPlayer().getBukkitPlayer();
    }

    default void close() {
        getGuildPlayer().closeGUI();
    }

    default void open() {
        if (!isValid()) {
            if (getLastGUI() != null) {
                getLastGUI().reopen();
            }

            return;
        }

        getGuildPlayer().getBukkitPlayer().openInventory(getInventory());
        getGuildPlayer().setUsingGUI(this);
    }

    /**
     * 关闭，更新，打开
     */

    default void reopen() {
        close();
        open();
    }

    boolean isValid();
}
