package com.github.julyss2019.mcsp.julyguild.request;

import java.util.Collection;
import java.util.UUID;

public interface Sender {
    enum Type {
        GUILD, GUILD_PLAYER
    }
    Type getSenderType();
    Collection<Request> getSentRequests();
    UUID getUuid();
}
