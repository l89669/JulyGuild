package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

/**
 * 职位
 */
public enum Position {
    MEMBER(0), OWNER(1);

    int level;

    Position(int level) {
        this.level = level;
    }

    public String getChineseName() {
        return JulyGuild.getInstance().getLangYaml().getString("Position." + name().toLowerCase());
    }

    public int getLevel() {
        return level;
    }
}
