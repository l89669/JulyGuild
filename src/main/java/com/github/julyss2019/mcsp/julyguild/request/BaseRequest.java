package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;

import java.util.UUID;

public class BaseRequest implements Request {
    private static JulyGuild plugin = JulyGuild.getInstance();

    private GuildPlayer requester;
    private long time;
    private UUID uuid;

    public BaseRequest() {}

    public BaseRequest(GuildPlayer requester, long time, UUID uuid) {
        this.requester = requester;
        this.time = time;
        this.uuid = uuid;
    }

    /**
     * 设置请求者
     * @param requester
     */
    public void setRequester(GuildPlayer requester) {
        this.requester = requester;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getTime() {
        return time;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public GuildPlayer getRequester() {
        return requester;
    }

    @Override
    public long getCreationTime() {
        return time;
    }

    @Override
    public boolean isTimeout() {
        return true;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }
}
