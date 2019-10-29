package com.github.julyss2019.mcsp.julyguild.request.player;

import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;

public class BasePlayerRequest extends BaseRequest implements PlayerRequest {
    private PlayerRequestType type;

    public BasePlayerRequest(PlayerRequestType type) {
        this.type = type;
    }

    @Override
    public PlayerRequestType getType() {
        return type;
    }
}
