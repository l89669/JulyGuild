package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public abstract class BasePayGUI extends BasePlayerGUI {
    private final ConfigurationSection section;
    private final Player bukkitPlayer = getBukkitPlayer();
    private final Placeholder placeholder;

    protected BasePayGUI(@Nullable GUI lastGUI, GuildPlayer guildPlayer, ConfigurationSection section, Placeholder placeholder) {
        super(lastGUI, GUIType.BASE_CONFIRM, guildPlayer);

        this.section = section;
        this.placeholder = placeholder;
    }

    @Override
    public abstract boolean canUse();

    public abstract void onMoneyPay();

    public abstract void onPointsPay();

    @Override
    public Inventory createInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(section, placeholder)
                .item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.money"), bukkitPlayer, placeholder), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onPointsPay();
                    }
                })
                .item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.points"), bukkitPlayer, placeholder), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onMoneyPay();
                    }
                });
        return guiBuilder.build();
    }
}
