package com.github.julyss2019.mcsp.julyguild.placeholder;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julylibrary.utils.TimeUtil;

import java.util.HashMap;
import java.util.Map;

public class Placeholder {
    private Map<String, String> placeholderMap;

    private Placeholder(Map<String, String> map) {
        this.placeholderMap = map;
    }

    private Placeholder() {}

    public Map<String, String> getPlaceholders() {
        return placeholderMap;
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
            addInner("guild_money", (int) guild.getOwner().getDonatedMoney());
            addInner("guild_points", (int) guild.getOwner().getDonatedMoney());
            addInner("guild_member_count", guild.getMemberCount());
            addInner("guild_max_member_count", guild.getMaxMemberCount());
            addInner("guild_creation_time", TimeUtil.YMD_SDF.format(guild.getCreationTime()));
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
            return add("{" + key + "}", value);
        }

        public Builder add(String key, int value) {
            return add(key, String.valueOf(value));
        }

        public Builder add(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Placeholder build() {
            return new Placeholder(map);
        }
    }
}
