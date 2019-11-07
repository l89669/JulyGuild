package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;

public class BaseMemberGUI extends BasePlayerGUI {
    protected final GuildMember guildMember;

    public BaseMemberGUI(GUIType guiType, GuildMember guildMember) {
        super(guiType, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
    }
}
