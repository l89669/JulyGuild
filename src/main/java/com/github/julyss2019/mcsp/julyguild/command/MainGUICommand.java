package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.player.MainGUI;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainGUICommand implements Command {
    private JulyGuild plugin = JulyGuild.getInstance();
    private GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    @Override
    public boolean onCommand(CommandSender cs, String[] args) {
        new MainGUI(guildPlayerManager.getGuildPlayer((Player) cs)).open();
        return true;
    }

    @Override
    public String getFirstArg() {
        return "main";
    }

    @Override
    public String getDescription() {
        return "打开主界面";
    }
}
