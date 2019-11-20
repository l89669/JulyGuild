package com.github.julyss2019.mcsp.julyguild.thirdparty.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class VaultEconomy implements ThirdPartyEconomy {
    private Economy economy;

    public VaultEconomy(Economy economy) {
        this.economy = economy;
    }

    @Override
    public Type getType() {
        return Type.VAULT;
    }

    @Override
    public void withdraw(Player player, double amount) {
        if (economy.withdrawPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS) {
            throw new RuntimeException("扣除金币是吧");
        }
    }

    @Override
    public void deposit(Player player, double amount) {
        if (economy.depositPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS) {
            throw new RuntimeException("扣除金币是吧");
        }
    }

    @Override
    public BigDecimal getBalance(Player player) {
        return new BigDecimal(economy.getBalance(player));
    }
}
