package com.github.julyss2019.mcsp.julyguild.request.guild;

import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;

public class BaseGuildRequest extends BaseRequest implements GuildRequest {
    private GuildRequestType guildRequestType;

    public BaseGuildRequest(GuildRequestType guildRequestType) {
        this.guildRequestType = guildRequestType;
    }


    @Override
    public GuildRequestType getType() {
        return guildRequestType;
    }

    @Override
    public boolean isOnlyOne() {
        return false;
    }
}
