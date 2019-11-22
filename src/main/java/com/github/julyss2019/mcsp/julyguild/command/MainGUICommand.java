package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.player.MainGUI;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julylibrary.command.tab.JulyTabCommand;
import com.github.julyss2019.mcsp.julylibrary.map.MapBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class MainGUICommand implements JulyTabCommand {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();
    private static final Map<String, String[]> tabMap = new MapBuilder<String, String[]>()
            .put("main", null).build();


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
    public Map<String, String[]> getTabCompleterMap() {
        return tabMap;
    }
}
