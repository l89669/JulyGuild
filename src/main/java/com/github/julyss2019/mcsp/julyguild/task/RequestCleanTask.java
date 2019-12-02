package com.github.julyss2019.mcsp.julyguild.task;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.request.guild.GuildRequest;
import org.bukkit.scheduler.BukkitRunnable;

public class RequestCleanTask extends BukkitRunnable {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static GuildManager guildManager = plugin.getGuildManager();

    @Override
    public void run() {
        for (Guild guild : guildManager.getGuilds()) {
            for (GuildRequest guildRequest : guild.getRequests()) {
                if (guildRequest.isTimeout()) {
                    guild.removeRequest(guildRequest);
                    guild.updateMembersGUI(GUIType.PLAYER_JOIN_REQUEST);
                }
            }
        }
    }
}
