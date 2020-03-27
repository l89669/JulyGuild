package com.github.julyss2019.mcsp.julyguild.config.checker;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class MaterialViolation extends BaseViolation {


	public MaterialViolation(@NotNull ConfigurationSection section, @NotNull String message) {
		super(section, message);
	}
}
