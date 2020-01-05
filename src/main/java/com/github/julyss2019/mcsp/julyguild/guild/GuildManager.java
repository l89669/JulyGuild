package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.api.event.GuildCreateEvent;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.log.guild.GuildCreateLog;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class GuildManager {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();
    private final Map<UUID, Guild> guildMap = new HashMap<>();

    public GuildManager() {}

    /**
     * 创建公会
     * @param ownerPlayer 公会主人
     * @return
     */
    public void createGuild(GuildPlayer ownerPlayer, @NotNull String guildName) {
        if (ownerPlayer.isInGuild()) {
            throw new IllegalArgumentException("主人已经有公会了");
        }

        UUID uuid = UUID.randomUUID();
        File file = new File(plugin.getDataFolder(), "data" + File.separator + "guilds" + File.separator + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        yml.set("uuid", uuid.toString());
        yml.set("name", guildName);
        yml.set("creation_time", System.currentTimeMillis());
        yml.set("members." + ownerPlayer.getUuid() + ".position", Position.OWNER.name());

        YamlUtil.saveYaml(yml, file);

        ownerPlayer.pointGuild(uuid); // 指向新公会
        load(file); // 载入公会

        // 更新所有玩家的GUI
        for (GuildPlayer guildPlayer : guildPlayerManager.getOnlineGuildPlayers()) {
            guildPlayer.updateGUI(GUIType.MAIN);
        }

        // 触发 Bukkit 事件
        Bukkit.getPluginManager().callEvent(new GuildCreateEvent(getGuild(uuid), ownerPlayer));
        plugin.writeGuildLog(new GuildCreateLog(uuid, guildName, ownerPlayer.getName()));
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
        guildMap.remove(guild.getUuid());
        JulyGuild.getInstance().getCacheGuildManager().updateSortedGuilds();
    }

    /**
     * 载入公会
     * @param file
     */
    private void load(File file) {
        Guild guild = new Guild(file);

        // 被删除
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
        return guildMap.get(uuid);
    }

    public void unloadAll() {
        for (Guild guild : getGuilds()) {
            unload(guild);
        }
    }
}
