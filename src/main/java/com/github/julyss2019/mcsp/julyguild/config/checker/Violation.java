package com.github.julyss2019.mcsp.julyguild.config.checker;

import org.bukkit.configuration.ConfigurationSection;

@Deprecated
public interface Violation {
	ConfigurationSection getSection();

	String getMessage();
}
