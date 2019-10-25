package com.github.julyss2019.mcsp.julyguild.request.player;

import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;

public class BaseGuildPlayerRequest extends BaseRequest implements GuildPlayerRequest {
    private GuildPlayerRequestType type;

    public BaseGuildPlayerRequest(GuildPlayerRequestType type) {
        this.type = type;
    }

    @Override
    public GuildPlayerRequestType getType() {
        return type;
    }
}
