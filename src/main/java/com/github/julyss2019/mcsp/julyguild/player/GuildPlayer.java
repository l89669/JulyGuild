package com.github.julyss2019.mcsp.julyguild.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.PageableGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.request.Receiver;
import com.github.julyss2019.mcsp.julyguild.request.Sender;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class GuildPlayer implements Sender, Receiver {
    private final File file;
    private UUID uuid;
    private YamlConfiguration yaml;
    private UUID guildUuid;
    private String name;
    private GUI usingGUI;
    private GuildPlayerMessageBox messageBox;

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
        this.yaml = YamlConfiguration.loadConfiguration(file);
        this.uuid = UUID.fromString(yaml.getString("uuid"));
        this.guildUuid = Optional.ofNullable(yaml.getString("guild")).map(UUID::fromString).orElse(null);
        this.name = Optional
                .ofNullable(Bukkit.getOfflinePlayer(getUuid()))
                .map(OfflinePlayer::getName)
                .orElse(uuid.toString());
        this.messageBox = new GuildPlayerMessageBox(this);
    }

    public String getName() {
        return name;
    }

    public void closeInventory() {
        getBukkitPlayer().closeInventory();
    }

    public boolean isUsingGUI() {
        return usingGUI != null;
    }

    public GUI getUsingGUI() {
        return usingGUI;
    }

    public void setUsingGUI(GUI usingGUI) {
        this.usingGUI = usingGUI;
    }

    public Guild getGuild() {
        return guildUuid == null ? null : JulyGuild.getInstance().getGuildManager().getGuild(guildUuid);
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

    /**
     * 深度更新GUI
     */
    public void updateGUI(GUIType... guiTypes) {
        GUIType usingGUIType = usingGUI.getType();

        for (GUIType guiType : guiTypes) {
            if (usingGUIType == guiType) {
                usingGUI.reopen();
            }

            GUI lastGUI = usingGUI;

            while ((lastGUI = lastGUI.getLastGUI()) != null) {
                if (lastGUI.canUse() && lastGUI instanceof PageableGUI) {
                    ((PageableGUI) lastGUI).update();
                }
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public OfflinePlayer getOfflineBukkitPlayer() {
        return Bukkit.getOfflinePlayer(getUuid());
    }

    public void save() {
        YamlUtil.saveYaml(yaml, file);
    }

    public YamlConfiguration getYaml() {
        return yaml;
    }

    public GuildPlayerMessageBox getMessageBox() {
        return messageBox;
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
