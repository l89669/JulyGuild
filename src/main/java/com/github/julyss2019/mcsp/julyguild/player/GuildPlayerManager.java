package com.github.julyss2019.mcsp.julyguild.player;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GuildPlayerManager {
    private Map<UUID, GuildPlayer> guildPlayerMap = new HashMap<>();

    public GuildPlayer getGuildPlayer(UUID uniqueId) {
        if (!guildPlayerMap.containsKey(uniqueId)) {
            guildPlayerMap.put(uniqueId, new GuildPlayer(uniqueId).load());
        }

        return guildPlayerMap.get(uniqueId);
    }

    public GuildPlayer getGuildPlayer(Player player) {
        return getGuildPlayer(player.getUniqueId());
    }

    public Collection<GuildPlayer> getOnlineGuildPlayers() {
        return guildPlayerMap.size() == 0 ? new ArrayList<>() : guildPlayerMap.values().stream().filter(GuildPlayer::isOnline).collect(Collectors.toList());
    }

    public Collection<GuildPlayer> getGuildsPlayers() {
        return guildPlayerMap.values();
    }
}
