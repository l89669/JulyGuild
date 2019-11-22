package com.github.julyss2019.mcsp.julyguild.guild.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

/**
 * 职位
 */
public enum Position {
    MEMBER(0), ADMIN(1), OWNER(2);

    int level;

    Position(int level) {
        this.level = level;
    }

    public static String getChineseName(Position position) {
        return JulyGuild.getInstance().getLangYamlConfig().getString("Position." + position.name().toLowerCase());
    }

    public int getLevel() {
        return level;
    }
}
