package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;

public abstract class BaseMemberPageableGUI extends BasePlayerPageableGUI {
    protected final GuildMember guildMember;

    public BaseMemberPageableGUI(GUIType guiType, GuildMember guildMember) {
        super(guiType, guildMember.getGuildPlayer());
        this.guildMember = guildMember;
    }

    public GuildMember getGuildMember() {
        return guildMember;
    }

    public Guild getGuild() {
        return getGuildMember().getGuild();
    }
}
