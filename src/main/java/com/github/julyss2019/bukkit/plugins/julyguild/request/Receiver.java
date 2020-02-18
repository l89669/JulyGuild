package com.github.julyss2019.bukkit.plugins.julyguild.request;

import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;

import java.util.List;

public interface Receiver {
    enum Type {
        GUILD, GUILD_PLAYER, GUILD_MEMBER
    }

    default List<Request> getReceivedRequests() {
        return JulyGuild.getInstance().getRequestManager().getReceivedRequests(this);
    }
}
