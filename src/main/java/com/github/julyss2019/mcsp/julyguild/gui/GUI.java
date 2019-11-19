package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface GUI {
    GuildPlayer getGuildPlayer();

    Inventory getGUI();

    default Player getBukkitPlayer() {
        return getGuildPlayer().getBukkitPlayer();
    }

    default void close() {
        getGuildPlayer().getBukkitPlayer().closeInventory();
        getGuildPlayer().setUsingGUI(null);
    }

    default void open() {
        Inventory gui = getGUI();

        if (!gui.equals(getGuildPlayer().getBukkitPlayer().getOpenInventory().getTopInventory())) {
            getGuildPlayer().getBukkitPlayer().openInventory(gui);
            getGuildPlayer().setUsingGUI(this);
        }
    }

    default void reopen() {
        close();
        open();
    }

    GUIType getType();
}
