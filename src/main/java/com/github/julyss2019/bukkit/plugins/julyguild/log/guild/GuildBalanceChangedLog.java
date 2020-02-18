package com.github.julyss2019.bukkit.plugins.julyguild.log.guild;

import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildBank;
import com.github.julyss2019.bukkit.plugins.julyguild.log.BaseGuildLog;
import com.github.julyss2019.bukkit.plugins.julyguild.log.GuildLogType;

import java.math.BigDecimal;
import java.util.UUID;

public class GuildBalanceChangedLog extends BaseGuildLog {
    private GuildBank.BalanceType balanceType;
    private BigDecimal oldBalance;
    private BigDecimal newBalance;

    public GuildBalanceChangedLog(UUID uuid, GuildBank.BalanceType balanceType, BigDecimal oldBalance, BigDecimal newBalance) {
        super(GuildLogType.BALANCE_CHANGED, uuid);

        this.balanceType = balanceType;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    public GuildBank.BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(GuildBank.BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    public BigDecimal getOldBalance() {
        return oldBalance;
    }

    public void setOldBalance(BigDecimal oldBalance) {
        this.oldBalance = oldBalance;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }
}
