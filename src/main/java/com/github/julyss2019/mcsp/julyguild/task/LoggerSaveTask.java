package com.github.julyss2019.mcsp.julyguild.task;

import com.github.julyss2019.mcsp.julyguild.logger.GuildLogger;
import org.bukkit.scheduler.BukkitRunnable;

public class LoggerSaveTask extends BukkitRunnable {
	@Override
	public void run() {
		GuildLogger.flushWriter();
	}
}
