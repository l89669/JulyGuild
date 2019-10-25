package com.github.julyss2019.mcsp.julyguild.guild.request;

import com.github.julyss2019.mcsp.julyguild.request.Request;

public interface GuildRequest extends Request {
    GuildRequestType getType();
    boolean isOnlyOne();
}
