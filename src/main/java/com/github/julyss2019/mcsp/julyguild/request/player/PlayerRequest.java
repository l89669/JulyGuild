package com.github.julyss2019.mcsp.julyguild.request.player;

import com.github.julyss2019.mcsp.julyguild.request.Request;

public interface PlayerRequest extends Request {
    PlayerRequestType getType();
}
