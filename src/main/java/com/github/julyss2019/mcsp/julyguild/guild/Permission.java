package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

public enum Permission {
    MEMBER_KICK, // 成员踢出
    SET_SPAWN, // 设置主城
    SET_MEMBER_PVP, // 成员PVP
    PLAYER_JOIN_CHECK, // 玩家审批
    GUILD_UPGRADE,  // 公会升级
    MANAGE_PERMISSION // 给予权限
    ;

    String getChineseName(Permission permission) {
        return JulyGuild.getInstance().getLangYaml().getConfigurationSection("Permission").getString(permission.name());
    }
}
