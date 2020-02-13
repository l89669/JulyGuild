package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

public enum GuildPermission {
    MEMBER_KICK, // 成员踢出
    SET_MEMBER_DAMAGE, // 成员PVP
    PLAYER_JOIN_CHECK, // 玩家审批
    MANAGE_PERMISSION, // 管理权限
    USE_SHOP; // 商店

    String getChineseName(GuildPermission guildPermission) {
        return JulyGuild.getInstance().getLangYaml().getConfigurationSection("GuildPermission").getString(guildPermission.name());
    }
}
