package com.github.julyss2019.mcsp.julyguild;

import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.Position;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import org.bukkit.configuration.ConfigurationSection;

import java.text.SimpleDateFormat;

public class LangHelper {
    public static class Global {
        public static SimpleDateFormat getDateTimeFormat() {
            return new SimpleDateFormat(JulyGuild.getInstance().getLangYaml().getString("Global.date_time_format"));
        }

        public static String getPrefix() {
            return JulyGuild.getInstance().getLangYaml().getString("Global.prefix");
        }

        public static String getNickName(GuildMember guildMember) {
            ConfigurationSection langSection = JulyGuild.getInstance().getLangYaml();
            String format = langSection.getString("Global.nick_name");

            return PlaceholderText.replacePlaceholders(format, new Placeholder.Builder()
                    .addInner("PERMISSION", langSection.getString("Position." + guildMember.getPosition().name().toLowerCase()))
                    .addInner("NAME", guildMember.getName()).build());
        }

        public static String getPositionName(Position position) {
            return JulyGuild.getInstance().getLangYaml().getString("Position." + position.name().toLowerCase());
        }
    }
}
