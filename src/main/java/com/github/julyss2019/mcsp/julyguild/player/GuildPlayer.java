package com.github.julyss2019.mcsp.julyguild.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.request.Receiver;
import com.github.julyss2019.mcsp.julyguild.request.Request;
import com.github.julyss2019.mcsp.julyguild.request.Sender;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class GuildPlayer implements Sender, Receiver {
    private final File file;
    private UUID uuid;
    private YamlConfiguration yml;
    private UUID guildUuid;
    private String name;
    private GUI usingGUI;

    GuildPlayer(File file) {
        this.file = file;

        if (!file.exists()) {
            throw new RuntimeException("玩家不存在");
        }

        load();
    }

    /**
     * 初始化
     * @return
     */
    public void load() {
        this.yml = YamlConfiguration.loadConfiguration(file);
        this.uuid = UUID.fromString(yml.getString("uuid"));
        this.guildUuid = Optional.ofNullable(yml.getString("guild")).map(UUID::fromString).orElse(null);
        this.name = Optional
                .ofNullable(Bukkit.getOfflinePlayer(getUuid()))
                .map(OfflinePlayer::getName)
                .orElse(uuid.toString());
    }

    public String getName() {
        return name;
    }

    public boolean isUsingGUI() {
        return usingGUI != null;
    }

    public boolean isUsingGUI(GUIType... types) {
        return Optional.ofNullable(usingGUI).filter(gui -> {
            for (GUIType type : types) {
                if (usingGUI.getType() == type) {
                    return true;
                }
            }

            return false;
        }).isPresent();
    }

    /**
     * 得到当前使用的GUI
     * @return
     */
    public GUI getUsingGUI() {
        return usingGUI;
    }

    /**
     * 关闭GUI
     */
    public void closeGUI() {
        if (!isOnline()) {
            throw new RuntimeException("离线状态下不能更新GUI");
        }

        setUsingGUI(null);
        getBukkitPlayer().closeInventory();
    }

    /**
     * 设置当前使用的GUI
     * @param usingGUI
     */
    public void setUsingGUI(GUI usingGUI) {
        if (!isOnline() && usingGUI != null) {
            throw new IllegalStateException("离线状态下不能设置GUI");
        }

        this.usingGUI = usingGUI;
    }

    /**
     * 更新GUI
     * @param guiTypes
     */
    public void updateGUI(GUIType... guiTypes) {
        if (!isOnline()) {
            throw new IllegalStateException("离线状态下不能更新GUI");
        }

        if (usingGUI != null) {
            for (GUIType guiType : guiTypes) {
                if (usingGUI.getType() == guiType) {
                    usingGUI.reopen();
                }
            }
        }
    }

    public Guild getGuild() {
        return JulyGuild.getInstance().getGuildManager().getGuild(guildUuid);
    }

    /**
     * 指向公会
     * @param guild
     */
    public void pointGuild(@Nullable Guild guild) {
        if (guild == null) {
            this.guildUuid = null;
            return;
        }

        if (!guild.isMember(this)) {
            throw new RuntimeException("不是该公会的成员");
        }

        this.guildUuid = guild.getUuid();
    }

    public UUID getGuildUuid() {
        return guildUuid;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isOnline() {
        Player tmp = getBukkitPlayer();

        return tmp != null && tmp.isOnline();
    }

    public boolean isInGuild() {
        return getGuild() != null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public OfflinePlayer getOfflineBukkitPlayer() {
        return Bukkit.getOfflinePlayer(getUuid());
    }

    public void save() {
        YamlUtil.saveYaml(yml, file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuildPlayer)) return false;
        GuildPlayer that = (GuildPlayer) o;
        return getUuid().equals(that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }

    @Override
    public Receiver.Type getReceiverType() {
        return Receiver.Type.GUILD_PLAYER;
    }

    @Override
    public Sender.Type getSenderType() {
        return Sender.Type.GUILD_PLAYER;
    }
}
