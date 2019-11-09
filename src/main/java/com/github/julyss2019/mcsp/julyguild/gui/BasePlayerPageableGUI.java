package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;

public abstract class BasePlayerPageableGUI extends PageableGUI {
    public BasePlayerPageableGUI(GUIType guiType, GuildPlayer guildPlayer) {
        super(guiType, guildPlayer);
    }
}
