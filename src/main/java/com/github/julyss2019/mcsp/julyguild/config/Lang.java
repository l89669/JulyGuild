package com.github.julyss2019.mcsp.julyguild.config;

import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    private static Map<String, String> map = new HashMap<>();

    public static void put(@NotNull String k, @NotNull String v) {
        map.put(k, JulyMessage.toColoredMessage(v));
    }

    public static String get(@NotNull  String k) {
        if (!map.containsKey(k)) {
            throw new IllegalArgumentException("键不存在");
        }

        return map.get(k);
    }
}
