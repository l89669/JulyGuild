package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.Receiver;
import com.github.julyss2019.mcsp.julyguild.request.Sender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;

public class GuildMember implements GuildHuman, Receiver, Sender {
    private Guild guild;
    private GuildPlayer guildPlayer;
    private UUID uuid;
    private ConfigurationSection section;
    private Set<GuildPermission> guildPermissions = new HashSet<>();
    private long joinTime;
    private Map<GuildBank.BalanceType, BigDecimal> donatedMap = new HashMap<>();

    GuildMember(@NotNull Guild guild, @NotNull GuildPlayer guildPlayer) {
        this.guild = guild;
        this.guildPlayer = guildPlayer;
        this.uuid = guildPlayer.getUuid();

        load();
    }

    private void load() {
        if (!guild.getYaml().contains("members")) {
            guild.getYaml().createSection("members");
        }

        this.section = guild.getYaml().getConfigurationSection("members").getConfigurationSection(uuid.toString());

        if (section.contains("permissions")) {
            Set<String> permissions = section.getConfigurationSection("permissions").getKeys(false);

            if (permissions != null) {
                permissions.forEach(s -> this.guildPermissions.add(GuildPermission.valueOf(s)));
            }
        }

        if (section.contains("donated")) {
            for (String type : section.getConfigurationSection("donated").getKeys(false)) {
                GuildBank.BalanceType balanceType = GuildBank.BalanceType.valueOf(type);

                donatedMap.put(balanceType, new BigDecimal(section.getString("donated." + balanceType.name(), "0")));
            }
        }

        this.joinTime = section.getLong("join_time");
    }

    public String getName() {
        return getGuildPlayer().getName();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void removePermission(@NotNull GuildPermission guildPermission) {
        setPermission(guildPermission, false);
    }

    public void addPermission(@NotNull GuildPermission guildPermission) {
        setPermission(guildPermission, true);
    }

    /**
     * 设置权限
     * @param guildPermission
     * @param b true 为设置 false 为删除
     */
    public void setPermission(@NotNull GuildPermission guildPermission, boolean b) {
        if (b && guildPermissions.contains(guildPermission)) {
            throw new RuntimeException("成员已经有该权限了");
        } else if (!b && !guildPermissions.contains(guildPermission)) {
            throw new RuntimeException("成员没有该权限");
        }

        Set<GuildPermission> newGuildPermissions = getGuildPermissions();

        if (b) {
            newGuildPermissions.add(guildPermission);
        } else {
            newGuildPermissions.remove(guildPermission);
        }

        Set<String> stringPermissions = new HashSet<>();

        newGuildPermissions.forEach(permission1 -> stringPermissions.add(permission1.name()));

        section.set("permissions", stringPermissions);
    }

    public Set<GuildPermission> getGuildPermissions() {
        return guildPermissions;
    }

    public boolean hasPermission(@NotNull GuildPermission guildPermission) {
        return getPosition() == GuildPosition.OWNER || getGuildPermissions().contains(guildPermission);
    }

    public void addDonated(@NotNull GuildBank.BalanceType balanceType, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("数量必须大于0");
        }

        setDonated(balanceType, getDonated(balanceType).add(new BigDecimal(amount)));
    }

    public BigDecimal getDonated(@NotNull GuildBank.BalanceType balanceType) {
        return donatedMap.getOrDefault(balanceType, new BigDecimal(0));
    }

    public void setDonated(@NotNull GuildBank.BalanceType balanceType, @NotNull BigDecimal value) {
        section.set("donated." + balanceType.name(), value.toString());
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
        return guildPlayer;
    }

    public boolean isOnline() {
        return getGuildPlayer().isOnline();
    }

    public GuildPosition getPosition() {
        return GuildPosition.MEMBER;
    }

    public boolean isValid() {
        return guild.isValid() && guild.isMember(uuid);
    }

    public void save() {
        guild.save();
    }
}
