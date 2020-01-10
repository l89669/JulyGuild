package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.messagebox.YamlMessageBox;

public class GuildMessageBox extends YamlMessageBox {
    private Guild guild;

    public GuildMessageBox(Guild guild) {
        super(guild.getYaml().getConfigurationSection("message_box"));

        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public void save() {
        guild.save();
    }
}
