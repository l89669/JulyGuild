package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildShopGUI extends BasePlayerGUI {
    private GuildMember guildMember;

    protected GuildShopGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember, @NotNull YamlConfiguration shopYaml) {
        super(lastGUI, GUIType.SHOP, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
    }

    @Override
    public boolean canUse() {
        return false;
    }

    @Override
    public Inventory createInventory() {
        return null;
    }
}
