package com.github.julyss2019.bukkit.plugins.julyguild.log;

public interface GuildLog {
    GuildLogType getType();
    long getCreationTime();
    String getUuid();
}
