package com.github.julyss2019.mcsp.julyguild;

import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import org.bukkit.configuration.ConfigurationSection;

public class LangHelper {
    public static class Global {
        public static String getPrefix() {
            return JulyGuild.getInstance().getLangYamlConfig().getString("Global.prefix");
        }

        public static String getNickName(GuildMember guildMember) {
            ConfigurationSection langSection = JulyGuild.getInstance().getLangYamlConfig();
            String format = langSection.getString("Global.nick_name");

            return PlaceholderText.replacePlaceholders(format, new Placeholder.Builder()
                    .add("%PERMISSION%", langSection.getString("Permission." + guildMember.getPermission().name().toLowerCase()))
                    .add("%NAME%", guildMember.getName()).build());
        }
    }
}
