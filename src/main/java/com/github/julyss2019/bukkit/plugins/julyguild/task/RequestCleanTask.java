package com.github.julyss2019.bukkit.plugins.julyguild.task;

import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;
import com.github.julyss2019.bukkit.plugins.julyguild.request.RequestManager;
import org.bukkit.scheduler.BukkitRunnable;

public class RequestCleanTask extends BukkitRunnable {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static RequestManager requestManager = plugin.getRequestManager();

    @Override
    public void run() {
        requestManager.getRequests().forEach(request -> {
            if (!request.isValid()) {
                requestManager.deleteRequest(request);
            }
        });
    }
}
