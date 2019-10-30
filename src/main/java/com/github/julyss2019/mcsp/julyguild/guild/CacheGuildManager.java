package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CacheGuildManager {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static GuildManager guildManager = plugin.getGuildManager();
    private List<Guild> sortedGuilds = new ArrayList<>();

    public CacheGuildManager() {

    }

    public void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                sortedGuilds = guildManager.getSortedGuilds();
            }
        }.runTaskTimer(plugin, 0L, 20L * 60L);
    }

    public void updateSortedGuilds() {
        this.sortedGuilds = guildManager.getSortedGuilds();
    }

    public List<Guild> getSortedGuilds() {
        return sortedGuilds;
    }

    public int getRanking(Guild guild) {
        return sortedGuilds.indexOf(guild) + 1;
    }

    public void reset() {
        sortedGuilds.clear();
    }
}
