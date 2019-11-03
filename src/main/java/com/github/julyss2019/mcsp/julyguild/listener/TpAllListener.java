package com.github.julyss2019.mcsp.julyguild.listener;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.request.player.PlayerRequestType;
import com.github.julyss2019.mcsp.julyguild.request.player.TpRequest;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.TitleBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;

public class TpAllListener implements Listener {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();
    private static Map<String, Long> lastSneakMap = new HashMap<>();
    private static Map<String, Integer> sneakCounterMap = new HashMap<>();

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!event.isSneaking()) {
            return;
        }

        String playerName = player.getName();
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer(player);
        TpRequest tpRequest = (TpRequest) guildPlayer.getOnlyOneRequest(PlayerRequestType.TP);

        if (tpRequest != null && !tpRequest.isTimeout()) {
            if (System.currentTimeMillis() - lastSneakMap.getOrDefault(playerName, 0L) < MainSettings.getTpAllShiftCountInterval()) {
                sneakCounterMap.put(playerName, sneakCounterMap.getOrDefault(playerName, 0) + 1);

                JulyMessage.sendTitle(player, new TitleBuilder().text("&c再按" + (MainSettings.getTpAllShiftCount() - sneakCounterMap.get(playerName)) + "次").colored().build());
            }

            lastSneakMap.put(playerName, System.currentTimeMillis());

            if (sneakCounterMap.getOrDefault(playerName, 0) == MainSettings.getTpAllShiftCount()) {
                guildPlayer.removeRequest(tpRequest.getUUID().toString());
                player.teleport(tpRequest.getLocation());
                JulyMessage.sendTitle(player, new TitleBuilder().text("&a已传送").colored().build());

                GuildPlayer requester = tpRequest.getRequester();

                if (requester.isOnline()) {
                    Util.sendColoredMessage(requester.getBukkitPlayer(), "&e成员 &c" + player.getName() + " &e已集结.");
                }

                resetPlayer(playerName);
            }
        }
    }

    public static void resetPlayer(String playerName) {
        lastSneakMap.remove(playerName);
        sneakCounterMap.remove(playerName);
    }
}
