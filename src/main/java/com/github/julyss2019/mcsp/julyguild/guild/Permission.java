package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

public enum Permission {
    GUILD_UPGRADE,
    MEMBER_KICK, // 成员踢出
    MEMBER_SET_ADMIN, // 成员设置管理员
    PLAYER_JOIN_CHECK, // 玩家审批
    MEMBER_SET_PERMISSION // 成员设置权限
    ;

    String getChineseName(Permission permission) {
        return JulyGuild.getInstance().getLangYaml().getConfigurationSection("Permission").getString(permission.name());
    }
}
