package com.github.julyss2019.bukkit.plugins.julyguild.messagebox;

import java.util.UUID;

public interface Message {
    long getCreationTime();
    String getMessage();
    UUID getUuid();
}
