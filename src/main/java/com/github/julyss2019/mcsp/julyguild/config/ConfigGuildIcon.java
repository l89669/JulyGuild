package com.github.julyss2019.mcsp.julyguild.config;


import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import org.bukkit.Material;

import java.util.List;

public class ConfigGuildIcon {
    private String name;
    private org.bukkit.Material material;
    private short durability;
    private String displayName;
    private List<String> lores;
    private boolean pointsPayEnabled;
    private boolean moneyPayEnabled;
    private int pointsCost;
    private int moneyCost;

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public short getDurability() {
        return durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLores() {
        return lores;
    }

    public void setLores(List<String> lores) {
        this.lores = lores;
    }

    public boolean isPointsPayEnabled() {
        return pointsPayEnabled && JulyGuild.getInstance().isPlayerPointsHooked();
    }

    public void setPointsPayEnabled(boolean pointsPayEnabled) {
        this.pointsPayEnabled = pointsPayEnabled;
    }

    public boolean isMoneyPayEnabled() {
        return moneyPayEnabled;
    }

    public void setMoneyPayEnabled(boolean moneyPayEnabled) {
        this.moneyPayEnabled = moneyPayEnabled;
    }

    public int getPointsCost() {
        return pointsCost;
    }

    public void setPointsCost(int pointsCost) {
        this.pointsCost = pointsCost;
    }

    public int getMoneyCost() {
        return moneyCost;
    }

    public void setMoneyCost(int moneyCost) {
        this.moneyCost = moneyCost;
    }
}
