package com.github.julyss2019.bukkit.plugins.julyguild.api;

import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildManager;
import com.github.julyss2019.bukkit.plugins.julyguild.player.GuildPlayerManager;

public class JulyGuildAPI {
    public static GuildManager getGuildManager() {
        return JulyGuild.getInstance().getGuildManager();
    }

    public static GuildPlayerManager getGuildPlayerManager() {
        return JulyGuild.getInstance().getGuildPlayerManager();
    }
}
