package com.github.julyss2019.mcsp.julyguild.guild.player;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GuildOwner extends GuildMember {
    public GuildOwner(Guild guild, GuildPlayer player) {
        super(guild, player, guild.getYaml().getConfigurationSection("owner"));
    }

    @Override
    public Position getPosition() {
        return Position.OWNER;
    }

    @Override
    public void setPermission(Permission permission, boolean b) {
        throw new RuntimeException("会长不允许被设置权限");
    }

    @Override
    public Set<Permission> getPermissions() {
        Set<Permission> result = new HashSet<>();

        Collections.addAll(result, Permission.values());
        return result;
    }
}
