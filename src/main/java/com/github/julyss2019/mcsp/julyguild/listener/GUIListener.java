package com.github.julyss2019.mcsp.julyguild.listener;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIListener implements Listener {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer((Player) event.getPlayer());

        guildPlayer.setUsingGUI(null);
    }
}
