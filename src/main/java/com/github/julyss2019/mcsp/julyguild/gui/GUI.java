package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public interface GUI {
    GuildPlayer getGuildPlayer();

    Inventory getInventory();

    GUIType getType();

    default Player getBukkitPlayer() {
        return getGuildPlayer().getBukkitPlayer();
    }

    default void close() {
        getGuildPlayer().closeGUI();
    }

    default void open() {
        getGuildPlayer().getBukkitPlayer().openInventory(getInventory());
        getGuildPlayer().setUsingGUI(this);
    }

    default void reopen() {
        close();
        open();
    }
}
