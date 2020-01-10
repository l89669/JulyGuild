package com.github.julyss2019.mcsp.julyguild.messagebox;

import java.util.UUID;

public class BaseMessage implements Message {
    private UUID uuid;
    private String message;
    private long creationTime;

    public BaseMessage(UUID uuid, String message, long creationTime) {
        this.uuid = uuid;
        this.message = message;
        this.creationTime = creationTime;
    }

    public BaseMessage(String message) {
        this.creationTime = System.currentTimeMillis();
        this.message = message;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }
}
