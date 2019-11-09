package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.inventory.Inventory;

public abstract class BasePlayerGUI implements GUI {
    private GUIType type;
    protected final GuildPlayer guildPlayer;
    protected final Guild guild;

    protected BasePlayerGUI(GUIType guiType, GuildPlayer guildPlayer) {
        this.type = guiType;
        this.guildPlayer = guildPlayer;
        this.guild = guildPlayer.getGuild();
    }

    @Override
    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    @Override
    public GUIType getType() {
        return type;
    }
}
