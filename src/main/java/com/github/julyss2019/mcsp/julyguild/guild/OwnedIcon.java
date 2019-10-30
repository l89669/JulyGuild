package com.github.julyss2019.mcsp.julyguild.guild;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OwnedIcon {
    private Material material;
    private short data;
    private String firstLore;
    private UUID uuid;

    protected OwnedIcon(Material material, short data, UUID uuid) {
        this.material = material;
        this.data = data;
        this.uuid = uuid;
    }

    protected OwnedIcon(Material material, short data, String firstLore, UUID uuid) {
        this.material = material;
        this.data = data;
        this.firstLore = firstLore;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Material getMaterial() {
        return material;
    }

    public short getData() {
        return data;
    }

    public String getFirstLore() {
        return firstLore;
    }

    public static OwnedIcon createNew(Material material, short durability, @Nullable String firstLore) {
        return firstLore == null ? new OwnedIcon(material, durability, UUID.randomUUID()) : new OwnedIcon(material, durability, firstLore, UUID.randomUUID());
    }

    public static OwnedIcon createNew(Material material, short durability) {
        return new OwnedIcon(material, durability, UUID.randomUUID());
    }
}
