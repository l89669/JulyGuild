package com.github.julyss2019.mcsp.julyguild.request.entities;

import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;
import org.jetbrains.annotations.NotNull;

public class TpAllRequest extends BaseRequest<GuildMember, GuildMember> {
    public TpAllRequest() {}

    public TpAllRequest(@NotNull GuildMember sender, @NotNull GuildMember receiver) {
        super(sender, receiver);
    }

    @Override
    public Type getType() {
        return Type.TP_ALL;
    }

    @Override
    public boolean isValid() {
        return (System.currentTimeMillis() - getCreationTime()) / 1000L < MainSettings.getGuildTpAllTimeout() && getSender().isValid() && getReceiver().isValid() && getSender().isOnline();
    }
}
