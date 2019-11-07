package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.inventory.Inventory;

public class BasePlayerGUI implements GUI {
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
    public Inventory getInventory() {
        return null;
    }

    @Override
    public void build() {}

    @Override
    public GUIType getType() {
        return type;
    }
}
