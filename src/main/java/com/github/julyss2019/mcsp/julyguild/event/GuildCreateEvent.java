package com.github.julyss2019.mcsp.julyguild.event;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildCreateEvent extends Event {
    private Guild guild;
    private GuildPlayer guildPlayer;
    private static HandlerList handlerList = new HandlerList();

    public GuildCreateEvent(Guild guild, GuildPlayer guildPlayer) {
        this.guild = guild;
        this.guildPlayer = guildPlayer;
    }

    public Guild getGuild() {
        return guild;
    }

    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
