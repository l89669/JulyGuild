package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.event.GuildCreateEvent;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildInfoGUI;
import com.github.julyss2019.mcsp.julyguild.gui.player.MainGUI;
import com.github.julyss2019.mcsp.julyguild.log.guild.GuildCreateLog;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GuildManager {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();
    private final Map<UUID, Guild> guildMap = new HashMap<>();

    public GuildManager() {}

    /**
     * 创建公会
     * @param owner 公会主人
     * @return
     */
    public void createGuild(GuildPlayer owner, @NotNull String guildName) {
        if (owner.isInGuild()) {
            throw new IllegalArgumentException("主人已经有公会了!");
        }

        String uuid = UUID.randomUUID().toString();
        File file = new File(plugin.getDataFolder(), "guilds" + File.separator + uuid + ".yml");

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException("文件创建失败: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        long creationTime = System.currentTimeMillis();

        yml.set("name", guildName);
        yml.set("uuid", uuid);
        yml.set("owner.join_time", System.currentTimeMillis());
        yml.set("owner.name", owner.getName());
        yml.set("creation_time", creationTime);

        YamlUtil.saveYaml(yml, file);
        load(file);
        owner.pointGuild(getGuild(uuid));

        // 更新所有玩家的GUI
        for (GuildPlayer guildPlayer : guildPlayerManager.getOnlineGuildPlayers()) {
            guildPlayer.updateGUI(GUIType.MAIN);
        }

        // 触发 Bukkit 事件
        Bukkit.getPluginManager().callEvent(new GuildCreateEvent(getGuild(uuid), owner));
        plugin.writeGuildLog(new GuildCreateLog(uuid, guildName, owner.getName()));
    }

    public int getGuildCount() {
        return guildMap.size();
    }

    public Collection<Guild> getGuilds() {
        return guildMap.values();
    }

    /**
     * 得到宗门列表
     * @return
     */
    public List<Guild> getSortedGuilds() {
        return new ArrayList<>(guildMap.values()).stream().sorted((o1, o2) -> o1.getRank() > o2.getRank() ? -1 : 0).collect(Collectors.toList());
    }

    /**
     * 卸载公会
     * @param guild
     */
    public void unload(Guild guild) {
        // 面向公会成员
        guild.getOnlineMembers().forEach(guildMember -> {
            GuildPlayer guildPlayer = guildMember.getGuildPlayer();
            GUI usingGUI = guildPlayer.getUsingGUI();

            // 重新打开 MainGUI
            if (usingGUI.getType() == GUIType.MAIN) {
                usingGUI.reopen();
            } else {
                guildPlayer.setUsingGUI(new MainGUI(guildPlayer).open());
            }
        });

        // 面向所有玩家
        guildPlayerManager.getOnlineGuildPlayers().forEach(guildPlayer -> {
            GUI usingGUI = guildPlayer.getUsingGUI();

            if (usingGUI.getType() == GUIType.MAIN) {
                guildPlayer.getUsingGUI().reopen();
            // 正在查看即将被 unload 的 GUI 的玩家
            } else if (usingGUI instanceof GuildInfoGUI && ((GuildInfoGUI) usingGUI).getGuild().equals(guild)) {
                guildPlayer.setUsingGUI(new MainGUI(guildPlayer).open());
            }
        });

        guildMap.remove(guild.getUuid());
        JulyGuild.getInstance().getCacheGuildManager().updateSortedGuilds();
    }

    /**
     * 载入公会
     * @param file
     */
    private void load(File file) {
        Guild guild = new Guild(file);

        // 如果没有删除则存入Map
        if (guild.isDeleted()) {
            return;
        }

        guildMap.put(guild.getUuid(), guild);
        JulyGuild.getInstance().getCacheGuildManager().updateSortedGuilds();

        for (GuildPlayer guildPlayer : guildPlayerManager.getOnlineGuildPlayers()) {
            guildPlayer.updateGUI(GUIType.MAIN);
        }
    }

    /**
     * 载入所有公会
     */
    public void loadAll() {
        guildMap.clear();

        File guildFolder = new File(plugin.getDataFolder(), "guilds");

        if (!guildFolder.exists()) {
            return;
        }

        File[] guildFiles = guildFolder.listFiles();

        if (guildFiles != null) {
            for (File guildFile : guildFiles) {
                load(guildFile);
            }
        }
    }

    public Guild getGuild(UUID uuid) {
        return getGuild(uuid.toString());
    }

    /**
     * 得到公会
     * @param uuid
     * @return
     */
    public Guild getGuild(String uuid) {
        return guildMap.get(uuid);
    }

    public void unloadAll() {
        for (Guild guild : getGuilds()) {
            unload(guild);
        }
    }
}
