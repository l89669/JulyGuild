package com.github.julyss2019.bukkit.plugins.julyguild.log.guild;

import com.github.julyss2019.bukkit.plugins.julyguild.log.GuildLogType;
import com.github.julyss2019.bukkit.plugins.julyguild.log.BaseGuildLog;

import java.util.UUID;

public class GuildCreateLog extends BaseGuildLog {
    private String owner;
    private String guildName;

    public GuildCreateLog(UUID uuid, String guildName, String owner) {
        super(GuildLogType.CREATE, uuid);

        this.owner = owner;
        this.guildName = guildName;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
