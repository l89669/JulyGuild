package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

import java.util.List;
import java.util.UUID;

public interface Sender {
    enum Type {
        GUILD, GUILD_PLAYER
    }
    Type getSenderType();
    default List<Request> getSentRequests() {
        return JulyGuild.getInstance().getRequestManager().getSentRequests(this);
    }
    UUID getUuid();
}
