package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements Command {
    private static JulyGuild plugin = JulyGuild.getInstance();

    @Override
    public boolean onCommand(CommandSender cs, String[] args) {
        plugin.reloadPluginConfig();

        for (GuildPlayer guildPlayer : plugin.getGuildPlayerManager().getSortedOnlineGuildPlayers()) {
            if (guildPlayer.getUsingGUI() != null) {
                guildPlayer.getUsingGUI().close();
            }
        }

        Util.sendColoredMessage(cs, "&f重载配置完毕.");
        return true;
    }

    @Override
    public String getFirstArg() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "JulyGuild.admin";
    }

    @Override
    public boolean isOnlyPlayerCanUse() {
        return false;
    }
}
