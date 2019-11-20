package com.github.julyss2019.mcsp.julyguild.thirdparty.economy;

import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class PlayerPointsEconomy implements ThirdPartyEconomy {
    private PlayerPointsAPI playerPointsAPI;

    public PlayerPointsEconomy(PlayerPointsAPI playerPointsAPI) {
        this.playerPointsAPI = playerPointsAPI;
    }

    @Override
    public void withdraw(Player player, int amount) {
        playerPointsAPI.take(player.getUniqueId(), amount);
    }

    @Override
    public void deposit(Player player, int amount) {
        playerPointsAPI.give(player.getUniqueId(), amount);
    }

    @Override
    public void withdraw(Player player, double amount) {
        throw new RuntimeException("PlayerPoints 不支持 double");
    }

    @Override
    public void deposit(Player player, double amount) {
        throw new RuntimeException("PlayerPoints 不支持 double");
    }

    @Override
    public BigDecimal getBalance(Player player) {
        return new BigDecimal(playerPointsAPI.look(player.getUniqueId()));
    }

    @Override
    public ThirdPartyEconomy.Type getType() {
        return Type.PLAYER_POINTS;
    }
}
