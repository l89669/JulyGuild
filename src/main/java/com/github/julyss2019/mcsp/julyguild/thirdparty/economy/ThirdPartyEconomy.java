package com.github.julyss2019.mcsp.julyguild.thirdparty.economy;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

public interface ThirdPartyEconomy {
    enum Type {
        VAULT, PLAYER_POINTS
    }

    Type getType();

    default void withdraw(Player player, int amount) {
        withdraw(player, amount);
    }

    default void deposit(Player player, int amount) {
        deposit(player, amount);
    }

    void withdraw(Player player, double amount);

    void deposit(Player player, double amount);

    BigDecimal getBalance(Player player);

    default boolean isEnough(Player player, int amount) {
        return getBalance(player).compareTo(new BigDecimal(amount)) >= 0;
    }

    default boolean isEnough(Player player, double amount) {
        return getBalance(player).compareTo(new BigDecimal(amount)) >= 0;
    }
}
