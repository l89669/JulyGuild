package com.github.julyss2019.mcsp.julyguild.placeholder;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.utils.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于内部的占位符
 */
public class Placeholder {
    private Map<String, String> placeholderMap;

    private Placeholder(Map<String, String> map) {
        this.placeholderMap = map;
    }

    private Placeholder() {}

    public Map<String, String> getPlaceholders() {
        return placeholderMap;
    }

    public Builder getBuilder() {
        Builder builder = new Builder();

        for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        return builder;
    }

    public static class Builder {
        private Map<String, String> map = new HashMap<>();

        public Builder() {}

        /**
         * 添加定义好的公会占位符
         * @param guild
         * @return
         */
        public Builder addGuildPlaceholders(Guild guild) {
            addInner("guild_name", guild.getName());
            addInner("guild_ranking", JulyGuild.getInstance().getCacheGuildManager().getRanking(guild));
            addInner("guild_owner", guild.getOwner().getName());
            addInner("guild_money", Util.SIMPLE_DECIMAL_FORMAT.format(guild.getGuildBank().getBalance(GuildBank.BalanceType.MONEY)));
            addInner("guild_points", Util.SIMPLE_DECIMAL_FORMAT.format(guild.getGuildBank().getBalance(GuildBank.BalanceType.POINTS)));
            addInner("guild_member_count", guild.getMemberCount());
            addInner("guild_max_member_count", guild.getAdditionMemberCount());
            addInner("guild_creation_time", TimeUtil.YMD_SDF.format(guild.getCreateTime()));
            return this;
        }

        public Builder addGuildMemberPlaceholders(GuildMember guildMember) {
            addInner("member_name", guildMember.getName());
            addInner("member_join_time", LangHelper.Global.getDateTimeFormat().format(guildMember.getJoinTime()));
            addInner("member_donate_money", String.valueOf(guildMember.getDonated(GuildBank.BalanceType.MONEY)));
            addInner("member_donate_points", String.valueOf(guildMember.getDonated(GuildBank.BalanceType.POINTS)));
            addInner("member_donate_points", String.valueOf(guildMember.getPosition().getChineseName()));
            return this;
        }

        /**
         * 添加内部的占位符
         * @param key
         * @param value
         * @return
         */
        public Builder addInner(String key, String value) {
            return add("{" + key + "}", value);
        }

        public Builder addInner(String key, int value) {
            return addInner(key, String.valueOf(value));
        }

        public Builder addInner(String key, double value) {
            return addInner(key, String.valueOf(value));
        }

        public Builder add(String key, double value) {
            return add(key, String.valueOf(value));
        }

        public Builder add(String key, int value) {
            return add(key, String.valueOf(value));
        }

        public Builder add(String key, @NotNull String value) {
            map.put(key, value);
            return this;
        }

        public Placeholder build() {
            return new Placeholder(map);
        }
    }
}
