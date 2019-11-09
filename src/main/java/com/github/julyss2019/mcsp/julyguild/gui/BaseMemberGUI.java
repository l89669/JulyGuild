package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;

public abstract class BaseMemberGUI extends BasePlayerGUI {
    protected final GuildMember guildMember;

    public BaseMemberGUI(GUIType guiType, GuildMember guildMember) {
        super(guiType, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
    }

    public GuildMember getGuildMember() {
        return guildMember;
    }

    public Guild getGuild() {
        return guildMember.getGuild();
    }
}
