package com.github.julyss2019.mcsp.julyguild.guild;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class GuildSpawn {
    private String worldName;
    private double x, y, z;
    private float yaw, pitch;

    GuildSpawn(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static GuildSpawn createByLocation(Location location) {
        return new GuildSpawn(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
