package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class GuildMember {
    private Guild guild;
    private GuildPlayer guildPlayer;
    private String name;
    private ConfigurationSection memberSection;
    private Set<Permission> permissions = new HashSet<>();

    public GuildMember(Guild guild, GuildPlayer guildPlayer) {
        this(guild, guildPlayer, guild.getYaml().getConfigurationSection("members." + guildPlayer.getName()));
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
            this.memberSection = guild.getYaml().createSection("members." + guildPlayer.getName());
        }

        if (memberSection.contains("permissions")) {
            Set<String> permissions = memberSection.getConfigurationSection("permissions").getKeys(false);

            if (permissions != null) {
                permissions.forEach(s -> this.permissions.add(Permission.valueOf(s)));
            }
        }
    }

    public void removePermission(Permission permission) {
        setPermission(permission, false);
    }

    public void addPermission(Permission permission) {
        setPermission(permission, true);
    }

    /**
     * 设置权限
     * @param permission
     * @param b true 为设置 false 为删除
     */
    public void setPermission(Permission permission, boolean b) {
        if (b && permissions.contains(permission)) {
            throw new RuntimeException("成员已经有该权限了");
        } else if (!b && !permissions.contains(permission)) {
            throw new RuntimeException("成员没有该权限");
        }

        Set<Permission> newPermissions = getPermissions();

        if (b) {
            newPermissions.add(permission);
        } else {
            newPermissions.remove(permission);
        }

        Set<String> stringPermissions = new HashSet<>();

        newPermissions.forEach(permission1 -> stringPermissions.add(permission1.name()));

        memberSection.set("permissions", stringPermissions);
    }

    public Set<Permission> getPermissions() {
        return new HashSet<>(permissions);
    }

    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
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

    public Position getPosition() {
        return Position.MEMBER;
    }

    public void save() {
        guild.save();
    }
}
