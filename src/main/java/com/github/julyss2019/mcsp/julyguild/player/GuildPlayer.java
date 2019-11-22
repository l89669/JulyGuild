package com.github.julyss2019.mcsp.julyguild.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.request.player.PlayerRequest;
import com.github.julyss2019.mcsp.julyguild.request.player.PlayerRequestType;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuildPlayer {
    private String name;
    private File file;
    private YamlConfiguration yml;
    private GUI usingGUI;
    private Map<String, PlayerRequest> requestMap = new HashMap<>();

    protected GuildPlayer(String name) {
        this.name = name;
    }

    /**
     * 得到当前使用的GUI
     * @return
     */
    public GUI getUsingGUI() {
        return usingGUI;
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
     * 添加请求，不存储到文件系统
     */
    public void addRequest(PlayerRequest request) {
        requestMap.put(request.getUUID().toString(), request);
    }

    /**
     * 得到请求
     * @return
     */
    public Collection<PlayerRequest> getRequests() {
        return requestMap.values();
    }

    /**
     * 删除请求
     * @param uuid
     */
    public void removeRequest(String uuid) {
        requestMap.remove(uuid);
    }

    /**
     * 是否有请求
     * @param uuid
     * @return
     */
    public boolean hasRequest(String uuid) {
        return requestMap.containsKey(uuid);
    }

    /**
     * 是否有请求
     * @param type
     * @return
     */
    public boolean hasRequest(PlayerRequestType type) {
        for (PlayerRequest request : getRequests()) {
            if (request.getType() == type) {
                return true;
            }
        }

        return false;
    }

    public PlayerRequest getOnlyOneRequest(PlayerRequestType type) {
        for (PlayerRequest request : getRequests()) {
            if (request.getType() == type) {
                return request;
            }
        }

        return null;
    }

    /**
     * 更新GUI
     * @param guiTypes
     */
    public void updateGUI(GUIType... guiTypes) {
        if (!isOnline()) {
            throw new IllegalStateException("离线状态下不能更新GUI");
        }

        GUI usingGUI = this.usingGUI;

        if (usingGUI != null) {
            for (GUIType guiType : guiTypes) {
                if (usingGUI.getType() == guiType) {
                    usingGUI.reopen();
                }
            }
        }
    }

    /**
     * 初始化
     * @return
     */
    public GuildPlayer load() {
        this.file = new File(JulyGuild.getInstance().getDataFolder(), "players" + File.separator + name + ".yml");

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.yml = YamlConfiguration.loadConfiguration(file);
        return this;
    }

    public String getName() {
        return name;
    }

    public Guild getGuild() {
        return JulyGuild.getInstance().getGuildManager().getGuild(yml.getString("guild"));
    }

    public void setGuild(Guild guild) {
        yml.set("guild", guild == null ? null : guild.getUUID().toString());
        save();
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(name);
    }

    public boolean isOnline() {
        Player tmp = getBukkitPlayer();

        return tmp != null && tmp.isOnline();
    }

    public boolean isInGuild() {
        return getGuild() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuildPlayer)) return false;
        GuildPlayer that = (GuildPlayer) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void save() {
        YamlUtil.saveYaml(yml, file);
    }
}
