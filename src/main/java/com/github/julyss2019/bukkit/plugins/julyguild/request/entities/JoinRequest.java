package com.github.julyss2019.bukkit.plugins.julyguild.request.entities;

import com.github.julyss2019.bukkit.plugins.julyguild.guild.Guild;
import com.github.julyss2019.bukkit.plugins.julyguild.config.setting.MainSettings;
import com.github.julyss2019.bukkit.plugins.julyguild.player.GuildPlayer;
import com.github.julyss2019.bukkit.plugins.julyguild.request.BaseRequest;
import org.jetbrains.annotations.NotNull;

public class JoinRequest extends BaseRequest<GuildPlayer, Guild> {
    private GuildPlayer sender;
    private Guild receiver;

    public JoinRequest() {}

    public JoinRequest(@NotNull GuildPlayer sender, @NotNull Guild receiver) {
        super(sender, receiver);

        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public Type getType() {
        return Type.JOIN;
    }

    @Override
    public boolean isValid() {
        return (System.currentTimeMillis() - getCreationTime()) / 1000L < MainSettings.getGuildRequestJoinTimeout() && !sender.isInGuild() && receiver.isValid();
    }
}
