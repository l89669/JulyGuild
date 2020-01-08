package com.github.julyss2019.mcsp.julyguild.request.entities;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;
import com.github.julyss2019.mcsp.julyguild.request.Receiver;
import com.github.julyss2019.mcsp.julyguild.request.Sender;
import org.bukkit.configuration.ConfigurationSection;

public class JoinRequest extends BaseRequest {
    public JoinRequest() {}

    public JoinRequest(GuildPlayer sender, Guild receiver) {
        super(sender, receiver);
    }

    @Override
    public Type getType() {
        return Type.JOIN;
    }
}
