package com.github.julyss2019.mcsp.julyguild.request.entities;

import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;

public class JoinRequest extends BaseRequest {
    public JoinRequest() {}

    public JoinRequest(GuildPlayer sender, Guild receiver) {
        super(sender, receiver);
    }

    @Override
    public Type getType() {
        return Type.JOIN;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && (System.currentTimeMillis() - getCreationTime()) / 1000L < MainSettings.getGuildRequestJoinTimeout() && !((GuildPlayer) getSender()).isInGuild() && ((Guild) getReceiver()).isValid();
    }
}
