package com.github.julyss2019.mcsp.julyguild.shop.reward.entities;

import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.shop.reward.BaseReward;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class GuildSetSpawnReward extends BaseReward {
    public GuildSetSpawnReward(ConfigurationSection section) {
        super(Type.GUILD_SET_SPAWN, section);
    }

    @Override
    public void execute(@NotNull GuildMember guildMember) {
        guildMember.getGuild().setSpawn(guildMember.getGuildPlayer().getBukkitPlayer().getLocation());
    }
}
