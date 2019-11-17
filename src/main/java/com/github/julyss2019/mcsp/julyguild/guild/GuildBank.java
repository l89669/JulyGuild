package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.log.guild.GuildBalanceChangedLog;
import com.github.julyss2019.mcsp.julylibrary.logger.FileLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class GuildBank {
    private static JulyGuild plugin = JulyGuild.getInstance();
    public enum BalanceType {POINTS, MONEY}

    private Guild guild;
    private BigDecimal moneyBigDecimal;
    private BigDecimal pointsBigDecimal;
    private ConfigurationSection section;

    public GuildBank(Guild guild) {
        this.guild = guild;

        if (!guild.getYml().contains("bank")) {
            guild.getYml().createSection("bank");
        }

        this.section = guild.getYml().getConfigurationSection("bank");
    }

    public GuildBank load() {
        this.moneyBigDecimal = new BigDecimal(section.getString("money", "0"));
        this.pointsBigDecimal = new BigDecimal(section.getString("points", "0"));
        return this;
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
        if (balanceType == BalanceType.MONEY) {
            return moneyBigDecimal.compareTo(valueBigDecimal) >= 0;
        }

        if (balanceType == BalanceType.POINTS) {
            return pointsBigDecimal.compareTo(valueBigDecimal) >= 0;
        }

        throw new IllegalArgumentException("非法的类型");
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
            throw new IllegalArgumentException("余额不足");
        }

        setBalance(balanceType, getBalance(balanceType).subtract(amountBigDecimal));
    }

    /**
     * 设置金币或点券
     * @param balanceType 类型
     * @param bigDecimal BigDecimal
     */
    public void setBalance(@NotNull GuildBank.BalanceType balanceType, BigDecimal bigDecimal) {
        if (balanceType == BalanceType.MONEY) {
            BigDecimal oldMoney = this.moneyBigDecimal;

            section.set("money", bigDecimal.toString());
            guild.save();
            this.moneyBigDecimal = bigDecimal;
            plugin.writeGuildLog(FileLogger.LoggerLevel.INFO, new GuildBalanceChangedLog(guild.getUUID().toString(), BalanceType.MONEY, oldMoney, this.moneyBigDecimal));
        } else if (balanceType == BalanceType.POINTS) {
            BigDecimal oldPoints = this.pointsBigDecimal;

            section.set("points", oldPoints.toString());
            guild.save();
            this.pointsBigDecimal = bigDecimal;
            plugin.writeGuildLog(FileLogger.LoggerLevel.INFO, new GuildBalanceChangedLog(guild.getUUID().toString(), BalanceType.POINTS, oldPoints, this.pointsBigDecimal));
        }
    }

    /**
     * 得到金币或点券
     * @param balanceType
     * @return
     */
    public BigDecimal getBalance(@NotNull GuildBank.BalanceType balanceType) {
        if (balanceType == BalanceType.MONEY) {
            return moneyBigDecimal;
        }

        if (balanceType == BalanceType.POINTS) {
            return pointsBigDecimal;
        }

        throw new IllegalArgumentException("非法的类型");
    }
}
