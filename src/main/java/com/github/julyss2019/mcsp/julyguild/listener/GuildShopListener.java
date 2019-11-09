package com.github.julyss2019.mcsp.julyguild.listener;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildShopItemBuyGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GuildShopListener implements Listener {
    private JulyGuild plugin = JulyGuild.getInstance();
    private GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    private void check(Player player) {
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer(player);

        if (!guildPlayer.isInGuild()) {
            return;
        }

        Guild guild = guildPlayer.getGuild();
        GuildPlayer guildOwner = guild.getOwner().getGuildPlayer();

        if (guildOwner.isOnline() && !guildPlayer.equals(guildOwner) && guildOwner.getUsingGUI() instanceof GuildShopItemBuyGUI) {
            guildOwner.getUsingGUI().reopen();
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                check(event.getPlayer());
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                check(event.getPlayer());
            }
        }.runTaskLater(plugin, 1L);
    }
}
