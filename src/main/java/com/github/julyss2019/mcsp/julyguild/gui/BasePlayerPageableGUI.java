package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class BasePlayerPageableGUI extends PageableGUI {
    private final GUI lastGUI;

    public BasePlayerPageableGUI(GUIType guiType, GuildPlayer guildPlayer, @Nullable GUI lastGUI) {
        super(guiType, guildPlayer);

        this.lastGUI = lastGUI;
    }

    @Override
    public GUI getLastGUI() {
        return lastGUI;
    }
}
