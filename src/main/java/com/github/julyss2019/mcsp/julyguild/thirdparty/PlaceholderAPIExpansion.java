package com.github.julyss2019.mcsp.julyguild.thirdparty;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.guild.CacheGuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * PAPI扩展
 */
public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    private static final JulyGuild plugin = JulyGuild.getInstance();
    private static final YamlConfiguration langYml = plugin.getLangYaml();
    private static final CacheGuildManager cacheGuildManager = plugin.getCacheGuildManager();
    private static final GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    @Override
    public String getIdentifier() {
        return "guild";
    }

    @Override
    public String getAuthor() {
        return "July_ss";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer(p.getUniqueId());
        Guild guild = guildPlayer.getGuild();
        boolean isInGuild = guild != null;

        if (params.equalsIgnoreCase("is_in_guild")) {
            return String.valueOf(isInGuild);
        }

        if (!isInGuild) {
            return langYml.getString("papi.non_str");
        }

        GuildMember guildMember = guild.getMember(guildPlayer);
        GuildBank guildBank = guild.getGuildBank();

        switch (params.toLowerCase()) {
            case "name":
                return guild.getName();
            case "member_per":
                return LangHelper.Global.getPositionName(guildMember.getPosition());
            case "member_donate_money":
                return LangHelper.Global.getDateTimeFormat().format(guildMember.getDonated(GuildBank.BalanceType.MONEY));
            case "member_donate_points":
                return LangHelper.Global.getDateTimeFormat().format(guildMember.getDonated(GuildBank.BalanceType.POINTS));
            case "member_join_time":
                return LangHelper.Global.getDateTimeFormat().format(guildMember.getJoinTime());
            case "ranking":
                return String.valueOf(cacheGuildManager.getRanking(guild));
            case "owner":
                return guild.getOwner().getName();
            case "member_count":
                return String.valueOf(guild.getMemberCount());
            case "max_member_count":
                return String.valueOf(guild.getMaxMemberCount());
            case "creation_time":
                return LangHelper.Global.getDateTimeFormat().format(guild.getCreateTime());
            case "money":
                return guildBank.getBalance(GuildBank.BalanceType.MONEY).toString();
            case "points":
                return guildBank.getBalance(GuildBank.BalanceType.POINTS).toString();
            case "online_member_count":
                return String.valueOf(guild.getOnlineMembers().size());
        }

        return null;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        return onPlaceholderRequest(p, params);
    }

    @Override
    public boolean canRegister() {
        return true;
    }
}