package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMemberGUI extends BasePlayerGUI {
    protected final GuildMember guildMember;

    public BaseMemberGUI(GUIType guiType, GuildMember guildMember, @Nullable GUI lastGUI) {
        super(guiType, guildMember.getGuildPlayer(), lastGUI);

        this.guildMember = guildMember;
    }

    public GuildMember getGuildMember() {
        return guildMember;
    }
}
