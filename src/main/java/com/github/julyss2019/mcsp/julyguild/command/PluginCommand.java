package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.commandv2.JulyCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.SubCommandHandler;
import org.bukkit.command.CommandSender;

public class PluginCommand implements JulyCommand {
    private JulyGuild plugin = JulyGuild.getInstance();

    @SubCommandHandler(firstArg = "reload", description = "重载插件配置", length = 0)
    public void onReload(CommandSender cs, String[] args) {
        plugin.reloadPluginConfig();
        Util.sendColoredMessage(cs, "&f配置重载完毕.");
    }

    @SubCommandHandler(firstArg = "version", description = "插件版本", length = 0)
    public void onVersion(CommandSender cs, String[] args) {
        Util.sendColoredMessage(cs, "&f插件版本: " + cs.getName() + ".");
        Util.sendColoredMessage(cs, "&f作者: July_ss, 插件交流群: 786184610.");
    }

    @Override
    public String getFirstArg() {
        return "plugin";
    }

    @Override
    public String getDescription() {
        return "插件相关";
    }
}
