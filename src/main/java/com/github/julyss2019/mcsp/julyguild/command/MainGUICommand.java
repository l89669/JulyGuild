package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.entities.MainGUI;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julylibrary.command.tab.JulyTabCommand;
import com.github.julyss2019.mcsp.julylibrary.command.tab.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainGUICommand implements JulyTabCommand {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();
    private final TabCompleter tabCompleter = new TabCompleter.Builder().command(this).build();

    @Override
    public String getPermission() {
        return "JulyGuild.use";
    }

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
    public boolean isOnlyPlayerCanUse() {
        return true;
    }

    @Override
    public String getDescription() {
        return "打开主界面";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return tabCompleter;
    }
}
