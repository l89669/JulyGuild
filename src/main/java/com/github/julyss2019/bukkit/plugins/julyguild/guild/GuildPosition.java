package com.github.julyss2019.bukkit.plugins.julyguild.guild;

/**
 * 职位
 */
public enum GuildPosition {
    MEMBER(0), OWNER(1);

    int level;

    GuildPosition(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
