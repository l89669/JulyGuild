package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.log.guild.GuildBalanceChangedLog;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class GuildBank {
    public enum BalanceType {
        GMONEY // 公会币
    }

    private final JulyGuild plugin = JulyGuild.getInstance();
    private final Guild guild;
    private final Map<BalanceType, BigDecimal> balanceMap = new HashMap<>();
    private ConfigurationSection section;

    GuildBank(Guild guild) {
        this.guild = guild;

        load();
    }

    private void load() {
        this.section = guild.getYaml().getConfigurationSection("bank");

        if (section == null) {
            section = guild.getYaml().createSection("bank");
        }

        for (String key : section.getKeys(false)) {
            balanceMap.put(BalanceType.valueOf(key), new BigDecimal(section.getString(key)));
        }
    }

    /**
     * 是否拥有足够的金币或点券
     * @param balanceType 类型
     * @param value 值
     * @return
     */
    public boolean has(@NotNull GuildBank.BalanceType balanceType, double value) {
        return has(balanceType, new BigDecimal(value));
    }

    /**
     * 是否拥有足够的金币或点券
     * @param balanceType 类型
     * @param valueBigDecimal BigDecimal
     * @return
     */
    public boolean has(@NotNull GuildBank.BalanceType balanceType, BigDecimal valueBigDecimal) {
        return getBalance(balanceType).compareTo(valueBigDecimal) >= 0;
    }

    /**
     * 给予金币或点券
     * @param balanceType 类型
     * @param amount 数量
     */
    public void deposit(@NotNull GuildBank.BalanceType balanceType, double amount) {
        deposit(balanceType, new BigDecimal(amount));
    }

    /**
     * 给予金币或点券
     * @param balanceType 类型
     * @param amountBigDecimal BigDecimal
     */
    public void deposit(@NotNull GuildBank.BalanceType balanceType, BigDecimal amountBigDecimal) {
        if (amountBigDecimal.compareTo(new BigDecimal(0)) <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        setBalance(balanceType, getBalance(balanceType).add(amountBigDecimal));
    }

    /**
     * 拿走金币或点券
     * @param balanceType 类型
     * @param amount 数量
     */
    public void withdraw(@NotNull GuildBank.BalanceType balanceType, double amount) {
        withdraw(balanceType, new BigDecimal(amount));
    }

    /**
     * 拿走金币或点券
     * @param balanceType 类型
     * @param amountBigDecimal BigDecimal
     */
    public void withdraw(@NotNull GuildBank.BalanceType balanceType, BigDecimal amountBigDecimal) {
        if (amountBigDecimal.compareTo(new BigDecimal(0)) <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        if (!has(balanceType, amountBigDecimal)) {
            throw new RuntimeException("余额不足");
        }

        setBalance(balanceType, getBalance(balanceType).subtract(amountBigDecimal));
    }

    /**
     * 设置金币或点券
     * @param balanceType 类型
     * @param bigDecimal BigDecimal
     */
    public void setBalance(@NotNull GuildBank.BalanceType balanceType, BigDecimal bigDecimal) {
        BigDecimal old = getBalance(balanceType);

        section.set(balanceType.name(), bigDecimal.toString());
        guild.save();
        balanceMap.put(balanceType, bigDecimal);
        plugin.writeGuildLog(new GuildBalanceChangedLog(guild.getUuid(), balanceType, old, bigDecimal));
    }

    /**
     * 得到金币或点券
     * @param balanceType
     * @return
     */
    public BigDecimal getBalance(@NotNull GuildBank.BalanceType balanceType) {
        return balanceMap.getOrDefault(balanceType, new BigDecimal("0"));
    }
}
