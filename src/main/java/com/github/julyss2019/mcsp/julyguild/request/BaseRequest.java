package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public abstract class BaseRequest implements Request {
    private long creationTime;
    private UUID uuid;
    private Sender sender;
    private Receiver receiver;
    private boolean valid = true;

    public BaseRequest() {
    }

    public BaseRequest(Sender sender, Receiver receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.uuid = UUID.randomUUID();
        this.creationTime = System.currentTimeMillis();
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
    public Sender getSender() {
        return sender;
    }

    @Override
    public Receiver getReceiver() {
        return receiver;
    }

    @Override
    public void onSave(ConfigurationSection section) {
        section.set("creation_time", getCreationTime());
        section.set("uuid", getUuid().toString());
        section.set("type", getType().name());
        section.set("sender.type", getSender().getSenderType().name());
        section.set("receiver.type", getReceiver().getReceiverType().name());
        section.set("sender.uuid", getSender().getUuid().toString());
        section.set("receiver.uuid", getReceiver().getUuid().toString());
    }

    @Override
    public void onLoad(ConfigurationSection section) {
        this.creationTime = section.getLong("creation_time");
        this.uuid = UUID.fromString(section.getString("uuid"));

        switch (Sender.Type.valueOf(section.getString("sender.type"))) {
            case GUILD:
                this.sender = JulyGuild.getInstance().getGuildManager().getGuild(UUID.fromString(section.getString("sender.uuid")));
                break;
            case GUILD_PLAYER:
                this.sender = JulyGuild.getInstance().getGuildPlayerManager().getGuildPlayer(UUID.fromString(section.getString("sender.uuid")));
                break;
            default:
                throw new RuntimeException("不支持的发送者类型");
        }

        switch (Receiver.Type.valueOf(section.getString("receiver.type"))) {
            case GUILD:
                this.receiver = JulyGuild.getInstance().getGuildManager().getGuild(UUID.fromString(section.getString("receiver.uuid")));
                break;
            case GUILD_PLAYER:
                this.receiver = JulyGuild.getInstance().getGuildPlayerManager().getGuildPlayer(UUID.fromString(section.getString("receiver.uuid")));
                break;
            default:
                throw new RuntimeException("不支持的接收者类型");
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean b) {
        this.valid = b;
    }
}
