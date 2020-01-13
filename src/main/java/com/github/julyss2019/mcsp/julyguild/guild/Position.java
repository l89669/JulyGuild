package com.github.julyss2019.mcsp.julyguild.guild;

/**
 * 职位
 */
public enum Position {
    MEMBER(0), OWNER(1);

    int level;

    Position(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
