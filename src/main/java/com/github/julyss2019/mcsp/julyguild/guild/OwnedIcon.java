package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.config.ConfigGuildIcon;

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
