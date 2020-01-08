package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Position;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class GuildJoinCheckGUI extends BasePlayerPageableGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildJoinCheckGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildJoinCheckGUI");

    public GuildJoinCheckGUI(GuildPlayer guildPlayer, @Nullable GUI lastGUI) {
        super(GUIType.PLAYER_JOIN_CHECK, guildPlayer, lastGUI);
    }

    @Override
    public Inventory getInventory() {
        Inven
    }
}
