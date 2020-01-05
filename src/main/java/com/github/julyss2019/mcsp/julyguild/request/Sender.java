package com.github.julyss2019.mcsp.julyguild.request;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Sender {
    void removeSentRequest(@NotNull Request request);
    Collection<Request> getSentRequests();
    void sendRequest(@NotNull Request request);
}
