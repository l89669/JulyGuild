package com.github.julyss2019.mcsp.julyguild.shop.reward;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public abstract class BaseReward implements Reward {
    protected ConfigurationSection section;
    protected Type type;

    public BaseReward(@NotNull Type type, ConfigurationSection section) {
        this.type  = type;
        this.section = section;
    }

    @Override
    public Type getFunctionType() {
        return null;
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
