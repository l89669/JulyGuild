package com.github.julyss2019.bukkit.plugins.julyguild.request;

import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;

import java.util.List;

public interface Sender {
    enum Type {
        GUILD, GUILD_PLAYER, GUILD_MEMBER
    }

    default List<Request> getSentRequests() {
        return JulyGuild.getInstance().getRequestManager().getSentRequests(this);
    }
}
