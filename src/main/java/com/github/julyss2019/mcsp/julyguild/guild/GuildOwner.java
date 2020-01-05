package com.github.julyss2019.mcsp.julyguild.guild;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GuildOwner extends GuildMember {
    GuildOwner(Guild guild, UUID uniqueId) {
        super(guild, uniqueId);
    }

    @Override
    public Position getPosition() {
        return Position.OWNER;
    }

    @Override
    public Set<Permission> getPermissions() {
        return new HashSet<>(Arrays.asList(Permission.values()));
    }
}
