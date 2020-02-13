package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julylibrary.validate.NotNull;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GuildIcon {
    private Guild guild;
    private UUID uuid;
    private String displayName;
    private Material material;
    private short durability;
    private String firstLore;

    public GuildIcon(@NotNull Guild guild, @NotNull UUID uuid, @NotNull Material material, short durability, @Nullable String firstLore) {
        this.guild = guild;
        this.uuid = uuid;
        this.material = material;
        this.durability = durability;
        this.firstLore = firstLore;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Guild getGuild() {
        return guild;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public short getDurability() {
        return durability;
    }

    public String getFirstLore() {
        return firstLore;
    }
}
