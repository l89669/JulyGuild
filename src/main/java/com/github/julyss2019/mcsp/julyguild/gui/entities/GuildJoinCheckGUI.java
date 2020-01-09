package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberPageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class GuildJoinCheckGUI extends BaseMemberPageableGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildJoinCheckGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildJoinCheckGUI");
    private final Player bukkitPlayer = getBukkitPlayer();

    public GuildJoinCheckGUI(GuildMember guildMember, @Nullable GUI lastGUI) {
        super(GUIType.PLAYER_JOIN_CHECK, guildMember, lastGUI);
    }

    @Override
    public Inventory getInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder().fromConfig(thisGUISection, bukkitPlayer)
                .pageItems(thisGUISection.getConfigurationSection("items.page_items"), this,bukkitPlayer);

        return guiBuilder.build();
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
