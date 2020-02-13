package com.github.julyss2019.mcsp.julyguild.shop.reward;

import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import org.jetbrains.annotations.NotNull;

public interface Reward {
    public enum Type {
        GUILD_SET_SPAWN, GUILD_UPGRADE, GUILD_TP_ALL, CUSTOM;
    }

    Type getFunctionType();
    void execute(@NotNull GuildMember guildMember);
}
