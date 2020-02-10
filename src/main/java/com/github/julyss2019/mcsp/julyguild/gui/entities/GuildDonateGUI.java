package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.BasePayGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildDonateGUI extends BasePayGUI {
    private JulyGuild plugin = JulyGuild.getInstance();
    private GuildMember guildMember;

    protected GuildDonateGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        super(lastGUI, guildMember.getGuildPlayer(), JulyGuild.getInstance().getGUIYaml("GuildDonateGUI"));

        this.guildMember = guildMember;
    }


    @Override
    public boolean canUse() {
        return guildMember.isValid();
    }

    @Override
    public void onMoneyPay() {

    }

    @Override
    public void onPointsPay() {

    }
}
