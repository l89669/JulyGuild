package com.github.julyss2019.mcsp.julyguild.log;

public interface GuildLog {
    GuildLogType getType();
    long getCreationTime();
    String getUuid();
}
