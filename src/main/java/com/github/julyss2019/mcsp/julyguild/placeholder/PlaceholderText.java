package com.github.julyss2019.mcsp.julyguild.placeholder;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderText {
    /**
     * 通过 PlaceholderAPI
     * @return
     */
    public static List<String> replacePlaceholders(List<String> list, Player player) {
        return JulyGuild.getInstance().isPlaceHolderAPIEnabled() ? PlaceholderAPI.setPlaceholders(player, list) : list;
    }

    /**
     * 通过 PlaceholderAPI
     * @return
     */
    public static String replacePlaceholders(String s, Player player) {
        return JulyGuild.getInstance().isPlaceHolderAPIEnabled() ? PlaceholderAPI.setPlaceholders(player, s) : s;
    }

    public static List<String> replacePlaceholders(List<String> list, Placeholder placeholder) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(PlaceholderText.replacePlaceholders(s, placeholder));
        }

        return result;
    }

    public static List<String> replacePlaceholders(List<String> list, Placeholder placeholder, Player player) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(replacePlaceholders(replacePlaceholders(s, placeholder), player));
        }

        return result;
    }

    public static String replacePlaceholders(String s, Placeholder placeholder, Player player) {
        return replacePlaceholders(replacePlaceholders(s, placeholder), player);
    }


    public static String replacePlaceholders(String s, Placeholder placeholder) {
        String result = s;

        for (Map.Entry<String, String> entry : placeholder.getPlaceholders().entrySet()) {
            // 对正则符号进行转义
            result = ignoreCaseReplace(result, entry.getKey()
                    .replace("\\", "\\\\").replace("*", "\\*")
                    .replace("+", "\\+").replace("|", "\\|")
                    .replace("{", "\\{").replace("}", "\\}")
                    .replace("(", "\\(").replace(")", "\\)")
                    .replace("^", "\\^").replace("$", "\\$")
                    .replace("[", "\\[").replace("]", "\\]")
                    .replace("?", "\\?").replace(",", "\\,")
                    .replace(".", "\\.").replace("&", "\\&"), entry.getValue());
        }

        return result;
    }

    private static String ignoreCaseReplace(String source, String target, String replacement) {
        Pattern p = Pattern.compile(target, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(source);

        return m.replaceAll(replacement);
    }
}
