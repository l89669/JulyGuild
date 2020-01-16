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

public abstract class ConfirmGUI extends BasePlayerGUI {
    private final ConfigurationSection section;
    private final Player bukkitPlayer = getBukkitPlayer();
    private final Placeholder placeholder;

    protected ConfirmGUI(@Nullable GUI lastGUI, GuildPlayer guildPlayer, ConfigurationSection section, Placeholder placeholder) {
        super(lastGUI, GUIType.PLAYER_CONFIRM, guildPlayer);

        this.section = section;
        this.placeholder = placeholder;
    }

    @Override
    public abstract boolean canUse();

    public abstract void onConfirm();

    public abstract void onCancel();

    @Override
    public Inventory createInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(section, placeholder)
                .item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.cancel"), bukkitPlayer, placeholder), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onCancel();
                    }
                })
                .item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.confirm"), bukkitPlayer, placeholder), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onConfirm();
                    }
                });
        return guiBuilder.build();
    }
}
