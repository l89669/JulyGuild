package com.github.julyss2019.mcsp.julyguild.request;

import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public interface Request {
    enum Type {
        JOIN("JoinRequest");

        private String className;

        Type(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }
    Sender getSender();
    Receiver getReceiver();
    long getCreationTime();
    UUID getUuid();
    Type getType();
    void onSave(ConfigurationSection section);
    void onRead(ConfigurationSection section);
}
