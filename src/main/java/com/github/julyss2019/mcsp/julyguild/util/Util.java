package com.github.julyss2019.mcsp.julyguild.util;

import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;

public class Util {
    public static SimpleDateFormat YMD_SDF = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat YMDHM_SDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");

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
