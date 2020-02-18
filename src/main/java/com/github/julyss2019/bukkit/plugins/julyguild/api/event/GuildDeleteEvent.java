package com.github.julyss2019.bukkit.plugins.julyguild.api.event;

import com.github.julyss2019.bukkit.plugins.julyguild.guild.Guild;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildDeleteEvent extends Event {
    private Guild guild;
    private static HandlerList handlerList = new HandlerList();

    public GuildDeleteEvent(@NotNull Guild guild) {
        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
