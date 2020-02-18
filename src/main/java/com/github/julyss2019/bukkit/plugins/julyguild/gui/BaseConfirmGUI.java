package com.github.julyss2019.bukkit.plugins.julyguild.gui;

import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.bukkit.plugins.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.bukkit.plugins.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseConfirmGUI extends BasePlayerGUI {
    private final ConfigurationSection section;
    private final Player bukkitPlayer = getBukkitPlayer();
    private final PlaceholderContainer placeholderContainer;

    protected BaseConfirmGUI(@Nullable GUI lastGUI, @NotNull GuildPlayer guildPlayer, @NotNull ConfigurationSection section) {
        this(lastGUI, guildPlayer, section, null);
    }

    protected BaseConfirmGUI(@Nullable GUI lastGUI, @NotNull GuildPlayer guildPlayer, @NotNull ConfigurationSection section, @Nullable PlaceholderContainer placeholderContainer) {
        super(lastGUI, Type.CONFIRM, guildPlayer);

        this.section = section;
        this.placeholderContainer = placeholderContainer;
    }

    public PlaceholderContainer getPlaceholderContainer() {
        return placeholderContainer;
    }

    @Override
    public abstract boolean canUse();

    public abstract void onConfirm();

    public abstract void onCancel();

    @Override
    public Inventory createInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(section, placeholderContainer)
                .item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.cancel"), bukkitPlayer, placeholderContainer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onCancel();
                    }
                })
                .item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.confirm"), bukkitPlayer, placeholderContainer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onConfirm();
                    }
                });

        return guiBuilder.build();
    }
}
