package com.github.julyss2019.mcsp.julyguild.guild.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

public enum Permission {
    MEMBER_KICK, // 成员踢出
    MEMBER_SET_ADMIN, // 成员设置管理员
    PLAYER_CHECK, // 玩家审批
    MEMBER_SET_PERMISSION // 成员设置权限
    ;

    String getChineseName(Permission permission) {
        return JulyGuild.getInstance().getLangYamlConfig().getConfigurationSection("Permission").getString( permission.name());
    }
}
