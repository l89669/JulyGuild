package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.command.tab.JulyTabCommand;
import com.github.julyss2019.mcsp.julylibrary.command.tab.TabCompleter;
import com.github.julyss2019.mcsp.julylibrary.map.MapBuilder;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class ReloadCommand implements JulyTabCommand {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final TabCompleter tabCompleter = new TabCompleter.Builder().command(this).build();

    @Override
    public boolean onCommand(CommandSender cs, String[] args) {
        plugin.reloadPluginConfig();

        for (GuildPlayer guildPlayer : plugin.getGuildPlayerManager().getOnlineGuildPlayers()) {
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

    @Override
    public TabCompleter getTabCompleter() {
        return tabCompleter;
    }
}
