package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.request.entities.JoinRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements Command {
    @Override
    public boolean onCommand(CommandSender cs, String[] args) {
        JulyGuild.getInstance().getGuildPlayerManager().getGuildPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId()).getGuild().delete();

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
