package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMemberGUI extends BasePlayerGUI {
    protected final GuildMember guildMember;

    public BaseMemberGUI(@Nullable GUI lastGUI, GUIType guiType, GuildMember guildMember) {
        super(lastGUI, guiType, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
    }

    public GuildMember getGuildMember() {
        return guildMember;
    }
}
