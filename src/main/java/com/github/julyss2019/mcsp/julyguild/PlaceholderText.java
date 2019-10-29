package com.github.julyss2019.mcsp.julyguild;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaceholderText {
    public static List<String> replacePlaceholders(@NotNull List<String> list, @NotNull Placeholder placeholder) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(PlaceholderText.replacePlaceholders(s, placeholder));
        }

        return result;
    }


    public static String replacePlaceholders(@NotNull String s, @NotNull Placeholder placeholder) {
        String result = s;

        for (Map.Entry<String, String> entry : placeholder.getPlaceholders().entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
