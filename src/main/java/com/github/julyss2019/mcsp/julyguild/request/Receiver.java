package com.github.julyss2019.mcsp.julyguild.request;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Receiver {
    List<Request> getReceivedRequests();
    void removeReceivedRequest(@NotNull Request request);
    void receiveRequest(@NotNull Request request);
}
