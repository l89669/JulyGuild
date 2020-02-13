package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GuildOwner extends GuildMember {


    GuildOwner(@NotNull Guild guild, @NotNull GuildPlayer guildPlayer) {
        super(guild, guildPlayer);
    }

    @Override
    public GuildPosition getPosition() {
        return GuildPosition.OWNER;
    }

    @Override
    public Set<GuildPermission> getGuildPermissions() {
        return new HashSet<>(Arrays.asList(GuildPermission.values()));
    }
}
