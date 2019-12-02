package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TestCommand implements Command {
    @Override
    public boolean onCommand(CommandSender cs, String[] args) {
        if (args.length == 1) {
            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                JulyGuild.getInstance().getGuildManager().createGuild(JulyGuild.getInstance().getGuildPlayerManager().getGuildPlayer(UUID.randomUUID().toString()), System.currentTimeMillis() + "");
            }

            cs.sendMessage("OK");
        }

        return true;
    }

    @Override
    public boolean isOnlyPlayerCanUse() {
        return false;
    }

    @Override
    public String getFirstArg() {
        return "test";
    }

    @Override
    public String getPermission() {
        return "JulyGuild.tester";
    }
}
