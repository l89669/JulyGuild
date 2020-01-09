package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * 一个 Backable GUI的实现类
 */
public abstract class BasePlayerGUI implements GUI {
    protected final GUIType type;
    protected final GUI lastGUI;
    protected final GuildPlayer guildPlayer;

    protected BasePlayerGUI(GUIType guiType, GuildPlayer guildPlayer, @Nullable GUI lastGUI) {
        this.type = guiType;
        this.guildPlayer = guildPlayer;
        this.lastGUI = lastGUI;
    }

    @Override
    public GUI getLastGUI() {
        return lastGUI;
    }

    @Override
    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    @Override
    public GUIType getType() {
        return type;
    }
}
