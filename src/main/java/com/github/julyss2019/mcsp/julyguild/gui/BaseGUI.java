package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BaseGUI implements GUI {
    private GUIType type;
    protected GuildPlayer guildPlayer;
    protected Player bukkitPlayer;
    protected String playerName;
    protected Guild guild;


    protected BaseGUI(GUIType guiType, GuildPlayer guildPlayer) {
        this.type = guiType;
        this.playerName = guildPlayer.getName();
        this.guildPlayer = guildPlayer;
        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
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
