package com.github.julyss2019.mcsp.julyguild.request;

import java.util.Collection;
import java.util.UUID;

public interface Receiver {
    enum Type {
        GUILD, GUILD_PLAYER
    }
    Receiver.Type getReceiverType();
    Collection<Request> getReceivedRequests();
    UUID getUuid();
}
