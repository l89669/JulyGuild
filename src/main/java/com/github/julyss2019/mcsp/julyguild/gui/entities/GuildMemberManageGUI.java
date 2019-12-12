package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class GuildMemberManageGUI extends BaseMemberGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final Player bukkitPlayer = getBukkitPlayer();
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildMemberManageGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("GuildMemberManageGUI");

    public GuildMemberManageGUI(GuildMember guildMember, @Nullable GUI lastGUI) {
        super(GUIType.MEMBER_MANAGE, guildMember, lastGUI);
    }

    @Override
    public Inventory getInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, guildMember);


        return null;
    }
}
