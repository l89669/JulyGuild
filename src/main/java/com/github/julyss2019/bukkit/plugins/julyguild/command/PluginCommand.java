package com.github.julyss2019.bukkit.plugins.julyguild.command;

import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;
import com.github.julyss2019.bukkit.plugins.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.commandv2.JulyCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.MainCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.SubCommand;
import org.bukkit.command.CommandSender;

@MainCommand(firstArg = "plugin", description = "插件相关")
public class PluginCommand implements JulyCommand {
    private JulyGuild plugin = JulyGuild.getInstance();

    @SubCommand(firstArg = "reload", description = "重载插件配置", length = 0)
    public void onReload(CommandSender cs, String[] args) {
        plugin.reloadPluginConfig();
        Util.sendMsg(cs, "&f配置重载完毕.");
    }

    @SubCommand(firstArg = "version", description = "插件版本", length = 0)
    public void onVersion(CommandSender cs, String[] args) {
        Util.sendMsg(cs, "&f插件版本: " + JulyGuild.VERSION + ".");
        Util.sendMsg(cs, "&f作者: 柒 月, 插件交流群: 786184610.");
    }
}
