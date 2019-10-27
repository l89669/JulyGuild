package com.github.julyss2019.mcsp.julyguild.config;

import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lang {
    private static Map<String, Object> map = new HashMap<>();

    public static void putString(@NotNull String k, @NotNull String v) {
        if (map.containsKey(k.toLowerCase())) {
            throw new IllegalArgumentException("键已存在");
        }

        map.put(k.toLowerCase(), JulyMessage.toColoredMessage(v));
    }

    public static void putStringList(@NotNull String k, @NotNull List<String> v) {
        if (map.containsKey(k.toLowerCase())) {
            throw new IllegalArgumentException("键已存在");
        }

        List<String> coloredList = new ArrayList<>();

        for (String s : v) {
            coloredList.add(JulyMessage.toColoredMessage(s));
        }

        map.put(k.toLowerCase(), coloredList);
    }

    public static List<String> getStringList(@NotNull String k) {
        return getStringList(k, null);
    }

    public static List<String> getStringList(@NotNull String k, @Nullable String[][] placeholders) {
        String lowerCaseKey = k.toLowerCase();

        if (!map.containsKey(lowerCaseKey)) {
            return new ArrayList<>();
        }

        if (!(map.get(lowerCaseKey) instanceof List)) {
            throw new IllegalArgumentException("值非字符串列表类型");
        }

        List<String> valueList = (List<String>) map.get(lowerCaseKey);
        List<String> resultList = new ArrayList<>();

        if (placeholders != null) {
            for (String s : valueList) {
                for (String[] placeholder : placeholders) {
                    if (placeholder.length != 2) {
                        throw new IllegalArgumentException("占位符长度不合法");
                    }

                    if (placeholder[0] == null || placeholder[1] == null) {
                        throw new IllegalArgumentException("占位符不合法");
                    }

                    resultList.add(s.replace(placeholder[0], placeholder[1]));
                }
            }
        }

        return resultList;
    }


    public static String getString(@NotNull String k) {
        return getString(k, null);
    }

    public static String getString(@NotNull String k, @Nullable String[][] placeholders) {
        String lowerCaseKey = k.toLowerCase();

        if (!map.containsKey(lowerCaseKey)) {
            return "";
        }

        if (!(map.get(lowerCaseKey) instanceof String)) {
            throw new IllegalArgumentException("值非字符串类型");
        }

        String v = (String) map.get(lowerCaseKey);

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

    public static Map<String, Object> getAll() {
        return (Map<String, Object>) ((HashMap) map).clone();
    }

    public static class Global {
        /**
         * 得到昵称（权限名+名字）
         * @param guildMember
         * @return
         */
        public static String getNickName(GuildMember guildMember) {
            return getString("Global.nick_name").replace("%PERMISSION%", Lang.getString("Permission." + guildMember.getPermission().name()).replace("%NAME%", guildMember.getName()));
        }

        public static String getEnabled(boolean b) {
            return b ? getString("enabled") : getString("disable");
        }

        public static String getDisabled() {
            return getString("disabled");
        }

        public static String getMoney() {
            return getString("money");
        }

        public static String getPoints() {
            return getString("points");
        }
    }
}
