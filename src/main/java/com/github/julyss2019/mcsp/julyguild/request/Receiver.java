package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

import java.util.Collection;
import java.util.UUID;

public interface Receiver {
    enum Type {
        GUILD, GUILD_PLAYER
    }
    Receiver.Type getReceiverType();
    default Collection<Request> getReceivedRequests() {
        return JulyGuild.getInstance().getRequestManager().getReceivedRequests(this);
    }
    UUID getUuid();
}
