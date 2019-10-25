package com.github.julyss2019.mcsp.julyguild.request.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.MainSettings;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.Location;

import java.util.UUID;

public class TpRequest extends BaseGuildPlayerRequest {
    private Location location;
    private static MainSettings mainSettings = JulyGuild.getInstance().getMainSettings();

    public TpRequest(Location location) {
        super(GuildPlayerRequestType.TP);

        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public static TpRequest createNew(GuildPlayer requester, Location location) {
        TpRequest instance = new TpRequest(location);

        instance.setRequester(requester);
        instance.setTime(System.currentTimeMillis());
        instance.setUuid(UUID.randomUUID());
        return instance;
    }

    @Override
    public boolean isTimeout() {
        return (System.currentTimeMillis() - getCreationTime()) / 1000 > mainSettings.getTpAllShiftTimeout();
    }
}
