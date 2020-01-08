package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.request.entities.JoinRequest;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public interface Request {
    enum Type {
        JOIN(JoinRequest.class);

        private Class<? extends Request> clazz;

        Type(Class<? extends Request> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends Request> getClazz() {
            return clazz;
        }
    }
    Sender getSender();
    Receiver getReceiver();
    long getCreationTime();
    UUID getUuid();
    Type getType();
    void onSaveData(ConfigurationSection section);
    void onLoadData(ConfigurationSection section);
    default void delete() {
        JulyGuild.getInstance().getRequestManager().deleteRequest(this);
    }
    default void send() {
        JulyGuild.getInstance().getRequestManager().sendRequest(this);
    }
}
