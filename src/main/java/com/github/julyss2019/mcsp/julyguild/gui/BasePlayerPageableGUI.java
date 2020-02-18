package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class BasePlayerPageableGUI extends PageableGUI {
    public BasePlayerPageableGUI(@Nullable GUI lastGUI, GUI.Type guiType, GuildPlayer guildPlayer) {
        super(lastGUI, guiType, guildPlayer);
    }
}
