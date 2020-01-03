package com.github.julyss2019.mcsp.julyguild.util;

import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static final DecimalFormat SIMPLE_DECIMAL_FORMAT = new DecimalFormat("0.00");

    /**
     * 通过字符串来获得整数列表
     * @param str 以 "," 作为分隔符的字符串 以 "-" 作为范围界定符的字符串 单个字符串
     * @return
     */
    public static List<Integer> getRangeIntegerList(@NotNull String str) {
        List<Integer> result = new ArrayList<>();

        try {
            String[] split1 = str.split(",");

            for (String splitStr : split1) {
                String[] split2 = splitStr.split("-");

                if (split2.length == 2) {
                    int max = Integer.parseInt(split2[1]);

                    for (int i = Integer.parseInt(split2[0]); i <= max; i++) {
                        result.add(i);
                    }
                } else {
                    result.add(Integer.parseInt(splitStr));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("字符串 " + str + " 不是有效的表达式");
        }

        return result;
    }

    public static String getTimeLeftStr(long timeLeft) {
        long h = timeLeft / 60 / 60;
        long m = timeLeft / 60;
        long s = timeLeft % 60;

        return (h == 0 ? "" : h + "时") + (m == 0 ? "" : m + "分" + (s == 0 ? "钟" : "")) + (s == 0 && (h != 0 || m != 0) ? "" : s + "秒");
    }

    public static void sendColoredMessage(CommandSender cs, String msg, Placeholder placeholder) {
        sendColoredMessage(cs, PlaceholderText.replacePlaceholders(msg, placeholder));
    }

    public static void sendColoredMessage(CommandSender cs, String msg) {
        JulyMessage.sendColoredMessage(cs, LangHelper.Global.getPrefix() + msg);
    }

    public static void sendColoredConsoleMessage(String msg) {
        JulyMessage.sendColoredMessage(Bukkit.getConsoleSender(), "&a[JulyGuild] &f" + msg);
    }

    public static int parseIntegerOrThrow(String str, String message) {
        int result;

        try {
            result = Integer.parseInt(str);
        } catch (Exception e) {
            throw new RuntimeException(message);
        }

        return result;
    }
}
