package com.github.julyss2019.mcsp.julyguild.messagebox;

import java.util.Collection;

public interface MessageBox {
    Collection<Message> getMessages();
    void removeMessage(Message message);
    void sendMessage(Message message);
}
