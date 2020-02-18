package com.github.julyss2019.bukkit.plugins.julyguild.log;

import java.util.UUID;

public class BaseGuildLog implements GuildLog {
    private GuildLogType type;
    private Long creationTime;
    private UUID uuid;

    public BaseGuildLog(GuildLogType type, UUID uuid) {
        this.type = type;
        this.creationTime = System.currentTimeMillis();
        this.uuid = uuid;
    }

    public BaseGuildLog(GuildLogType type, UUID uuid, Long creationTime) {
        this(type, uuid);
        this.creationTime = creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public GuildLogType getType() {
        return type;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getUuid() {
        return getUuid();
    }
}
