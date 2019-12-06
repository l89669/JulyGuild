package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.*;

public class GuildMember {
    private Guild guild;
    private UUID uuid;
    private ConfigurationSection memberSection;
    private Set<Permission> permissions = new HashSet<>();
    private long joinTime;
    private Map<GuildBank.BalanceType, BigDecimal> donatedMap = new HashMap<>();

    GuildMember(Guild guild, UUID uuid) {
        this.guild = guild;
        this.uuid = uuid;

        load();
    }

    private void load() {
        if (!guild.getYaml().contains("members")) {
            guild.getYaml().createSection("members");
        }

        this.memberSection = guild.getYaml().getConfigurationSection("members").getConfigurationSection(uuid.toString());

        if (memberSection.contains("permissions")) {
            Set<String> permissions = memberSection.getConfigurationSection("permissions").getKeys(false);

            if (permissions != null) {
                permissions.forEach(s -> this.permissions.add(Permission.valueOf(s)));
            }
        }

        for (String type : memberSection.getConfigurationSection("donated").getKeys(false)) {
            GuildBank.BalanceType balanceType = GuildBank.BalanceType.valueOf(type);

            donatedMap.put(balanceType, new BigDecimal(memberSection.getString("donated." + balanceType.name(), "0")));
        }

        this.joinTime = memberSection.getLong("join_time");
    }

    public String getName() {
        return getGuildPlayer().getName();
    }

    public UUID getUuid() {
        return uuid;
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
        if (getPosition() == Position.OWNER) {
            throw new RuntimeException("会长不允许被设置权限");
        }

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
        if (getPosition() == Position.OWNER) {
            Set<Permission> result = new HashSet<>();

            Collections.addAll(result, Permission.values());
            return result;
        }

        return new HashSet<>(permissions);
    }

    public boolean hasPermission(Permission permission) {
        return getPosition() == Position.OWNER || getPermissions().contains(permission);
    }

    public void addDonated(GuildBank.BalanceType balanceType, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("数量必须大于0");
        }

        setDonated(balanceType, getDonated(balanceType).add(new BigDecimal(amount)));
    }

    public BigDecimal getDonated(GuildBank.BalanceType balanceType) {
        return donatedMap.get(balanceType);
    }

    public void setDonated(GuildBank.BalanceType balanceType, BigDecimal value) {
        memberSection.set("donated." + balanceType.name(), value.toString());
        save();
        donatedMap.put(balanceType, value);
    }

    public long getJoinTime() {
        return joinTime;
    }

    public Guild getGuild() {
        return guild;
    }

    public GuildPlayer getGuildPlayer() {
        return JulyGuild.getInstance().getGuildPlayerManager().getGuildPlayer(uuid);
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
