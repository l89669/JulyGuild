package com.github.julyss2019.mcsp.julyguild;

import com.github.julyss2019.mcsp.julyguild.guild.CacheGuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * PAPI扩展
 */
public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static CacheGuildManager cacheGuildManager = plugin.getCacheGuildManager();
    private static GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    @Override
    public String getIdentifier() {
        return "GUILD";
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
        String playerName = p.getName();
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer(playerName);
        Guild guild = guildPlayer.getGuild();
        boolean isInGuild = guild != null;

        if (params.equalsIgnoreCase("is_in_guild")) {
            return String.valueOf(isInGuild);
        }

        if (!isInGuild) {
            return "无";
        }

        GuildBank guildBank = guild.getGuildBank();

        switch (params.toLowerCase()) {
            case "name":
                return guild.getName();
            case "member_per":
                return Permission.getChineseName(guild.getMember(guildPlayer).getPermission());
            case "member_donate_money":
                return String.valueOf((int) guild.getMember(playerName).getDonatedMoney());
            case "member_donate_points":
                return String.valueOf((int) guild.getMember(playerName).getDonatedPoints());
            case "member_join_time":
                return Util.YMD_SDF.format(guild.getMember(playerName).getJoinTime());
            case "ranking":
                return String.valueOf(cacheGuildManager.getRanking(guild));
            case "owner":
                return guild.getOwner().getName();
            case "member_count":
                return String.valueOf(guild.getMemberCount());
            case "max_member_count":
                return String.valueOf(guild.getMaxMemberCount());
            case "creation_time":
                return Util.YMD_SDF.format(guild.getCreationTime());
            case "money":
                return String.valueOf((int) guildBank.getBalance(GuildBank.BalanceType.MONEY));
            case "points":
                return String.valueOf((int) guildBank.getBalance(GuildBank.BalanceType.POINTS));
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
