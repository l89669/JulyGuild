package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * 一个GUI的实现类
 */
public abstract class BasePlayerGUI implements GUI {
    protected final GUI lastGUI;
    protected final GUIType type;
    protected final GuildPlayer guildPlayer;

    protected BasePlayerGUI(@Nullable GUI lastGUI, GUIType guiType, GuildPlayer guildPlayer) {
        this.lastGUI = lastGUI;
        this.type = guiType;
        this.guildPlayer = guildPlayer;
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
