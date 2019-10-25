package com.github.julyss2019.mcsp.julyguild.config;

import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    private static Map<String, String> map = new HashMap<>();

    public static void put(@NotNull String k, @NotNull String v) {
        map.put(k, JulyMessage.toColoredMessage(v));
    }

    public static String get(@NotNull String k) {
        return get(k);
    }

    public static String get(@NotNull String k, @Nullable String[][] placeholders) {
        if (!map.containsKey(k)) {
            throw new IllegalArgumentException("键不存在");
        }

        String v = map.get(k);

        if (placeholders != null) {
            for (String[] placeholder : placeholders) {
                if (placeholder.length != 2) {
                    throw new IllegalArgumentException("占位符长度不合法");
                }

                if (placeholder[0] == null || placeholder[1] == null) {
                    throw new IllegalArgumentException("占位符不合法");
                }

                v = v.replace(placeholder[0], placeholder[1]);
            }
        }

        return v;
    }

    /**
     * 得到昵称（权限名+名字）
     * @param guildMember
     * @return
     */
    public static String getNickName(GuildMember guildMember) {
        return get("Global.nick_name").replace("%PERMISSION%", Lang.get("Permission." + guildMember.getPermission().name()).replace("%NAME%", guildMember.getName()));
    }
}
