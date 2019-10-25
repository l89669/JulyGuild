package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;

import java.util.UUID;

public interface Request {
    GuildPlayer getRequester();
    long getCreationTime();
    boolean isTimeout();
    UUID getUUID();
}
