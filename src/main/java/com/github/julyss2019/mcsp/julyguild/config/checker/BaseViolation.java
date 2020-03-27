package com.github.julyss2019.mcsp.julyguild.config.checker;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class BaseViolation implements Violation {
	private ConfigurationSection section;
	private String message;

	public BaseViolation(@NotNull ConfigurationSection section, @NotNull String message) {
		this.message = message;
		this.section = section;
	}

	@Override
	public ConfigurationSection getSection() {
		return section;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
