package com.github.julyss2019.mcsp.julyguild.request;

import java.util.UUID;

public abstract class BaseRequest implements Request {
    private long creationTime = System.currentTimeMillis();
    private UUID uuid = UUID.randomUUID();
    private RequestType requestType;
    private Sender sender;
    private Receiver receiver;

    public BaseRequest(RequestType requestType, Sender sender, Receiver receiver) {
        this.requestType = requestType;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public RequestType getType() {
        return requestType;
    }

    @Override
    public Sender getSender() {
        return sender;
    }

    @Override
    public Receiver getReceiver() {
        return receiver;
    }
}
