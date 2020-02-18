package com.github.julyss2019.bukkit.plugins.julyguild.gui;

import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.bukkit.plugins.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.bukkit.plugins.julyguild.player.GuildPlayer;
import com.github.julyss2019.bukkit.plugins.julyguild.thirdparty.economy.PlayerPointsEconomy;
import com.github.julyss2019.bukkit.plugins.julyguild.thirdparty.economy.VaultEconomy;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 基础支付GUI，仅包含：点券支付、金币支付、返回
 */
public abstract class BasePayGUI extends BasePlayerGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    protected final PlayerPointsEconomy playerPointsEconomy = plugin.getPlayerPointsEconomy();
    protected final VaultEconomy vaultEconomy = plugin.getVaultEconomy();
    private final ConfigurationSection section;
    private final PlaceholderContainer placeholderContainer;
    private final Player bukkitPlayer = getBukkitPlayer();

    protected BasePayGUI(@Nullable GUI lastGUI, @NotNull GUI.Type guiType, @NotNull GuildPlayer guildPlayer, @NotNull ConfigurationSection section) {
        this(lastGUI, guiType, guildPlayer, section, null);
    }

    protected BasePayGUI(@Nullable GUI lastGUI, @NotNull GUI.Type guiType, @NotNull GuildPlayer guildPlayer, @NotNull ConfigurationSection section, @Nullable PlaceholderContainer placeholderContainer) {
        super(lastGUI, guiType, guildPlayer);

        this.section = section;
        this.placeholderContainer = placeholderContainer;
    }

    public PlaceholderContainer getPlaceholderContainer() {
        return placeholderContainer;
    }

    @Override
    public abstract boolean canUse();

    public abstract void onMoneyPay();

    public abstract void onPointsPay();

    @Override
    public Inventory createInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(section, placeholderContainer)
                .item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.money"), bukkitPlayer, placeholderContainer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onMoneyPay();
                    }
                })
                .item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.points"), bukkitPlayer, placeholderContainer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onPointsPay();
                    }
                }).item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.back"), bukkitPlayer, placeholderContainer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (canBack()) {
                            back();
                        }
                    }
                });

        return guiBuilder.build();
    }
}
