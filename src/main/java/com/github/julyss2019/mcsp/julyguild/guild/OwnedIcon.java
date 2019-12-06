package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.config.ConfigGuildIcon;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OwnedIcon {
    private Guild guild;
    private ConfigGuildIcon configGuildIcon;

    public OwnedIcon(Guild guild, ConfigGuildIcon configGuildIcon) {
        this.guild = guild;
        this.configGuildIcon = configGuildIcon;
    }

    public Guild getGuild() {
        return guild;
    }

    public ConfigGuildIcon getConfigGuildIcon() {
        return configGuildIcon;
    }

    public String getName() {
        return getConfigGuildIcon().getName();
    }
}
