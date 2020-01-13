package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.entities.MainGUI;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/**
 * 采用被动式，容错式更新设计，每次点击如果遇到无效的情况则强制更新，否则继续使用
 */
public interface GUI {
    boolean canUse();

    GUI getLastGUI();

    GuildPlayer getGuildPlayer();

    Inventory createInventory();

    GUIType getType();

    default Player getBukkitPlayer() {
        return getGuildPlayer().getBukkitPlayer();
    }

    default void openLater(long tick) {
        new BukkitRunnable() {
            @Override
            public void run() {
                open();
            }
        }.runTaskLater(JulyGuild.getInstance(), tick);
    }

    default void open() {
        // 检查能否使用
        if (!canUse()) {
            GUI lastGUI = getLastGUI();

            if (lastGUI != null) {
                lastGUI.open();
            } else {
                new MainGUI(getGuildPlayer()).open();
            }
        }

        Inventory inventory = createInventory();

        getGuildPlayer().getBukkitPlayer().openInventory(inventory);
        getGuildPlayer().setUsingGUI(this);
    }

    default boolean canBack() {
        return getLastGUI() != null;
    }

    default void back() {
        close();

        GUI lastGUI = Optional.ofNullable(getLastGUI()).orElseThrow(() -> new RuntimeException("没有上一个GUI了"));

        lastGUI.open();
    }

    default void close() {
        if (!this.equals(getGuildPlayer().getUsingGUI())) {
            throw new RuntimeException("当前GUI没在使用");
        }

        getGuildPlayer().closeInventory();
    }

    /**
     * 关闭，打开
     */

    default void reopen() {
        close();
        open();
    }

    /**
     * 关闭，延时，打开
     * @param later
     */
    default void reopen(long later) {
        close();

        new BukkitRunnable() {
            @Override
            public void run() {
                open();
            }
        }.runTaskLater(JulyGuild.getInstance(), later);
    }
}
