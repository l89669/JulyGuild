package com.github.julyss2019.mcsp.julyguild.guild.player;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class GuildMember {
    private Guild guild;
    private GuildPlayer guildPlayer;
    private String name;
    private ConfigurationSection memberSection;

    public GuildMember(Guild guild, GuildPlayer guildPlayer) {
        this.guild = guild;
        this.guildPlayer = guildPlayer;
        this.name = guildPlayer.getName();
        this.memberSection = guild.getYml().getConfigurationSection("members." + guildPlayer.getName());

        load();
    }

    public GuildMember(Guild guild, GuildPlayer player, ConfigurationSection memberSection) {
        this.guild = guild;
        this.guildPlayer = player;
        this.name = player.getName();
        this.memberSection = memberSection;

        load();
    }

    public void load() {
        if (memberSection == null) {
            this.memberSection = guild.getYml().createSection("members." + guildPlayer.getName());
        }
    }

    public void addDonated(GuildBank.BalanceType balanceType, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("数量必须大于0");
        }

        setDonated(balanceType, getDonated(balanceType).add(new BigDecimal(amount)));
    }

    public BigDecimal getDonated(GuildBank.BalanceType balanceType) {
        return new BigDecimal(memberSection.getString("donated." + balanceType.name(), "0"));
    }

    public void setDonated(GuildBank.BalanceType balanceType, BigDecimal value) {
        memberSection.set("donated." + balanceType.name(), value.toString());
        save();
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(getName());
    }

    public long getJoinTime() {
        return memberSection.getLong("join_time");
    }

    public Guild getGuild() {
        return guild;
    }

    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return getGuildPlayer().isOnline();
    }

    public Permission getPermission() {
        return Permission.MEMBER;
    }

    public void save() {
        guild.save();
    }
}
