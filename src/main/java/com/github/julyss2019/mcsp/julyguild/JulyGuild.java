package com.github.julyss2019.mcsp.julyguild;

import com.github.julyss2019.mcsp.julyguild.guild.CacheGuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.listener.GuildListener;
import com.github.julyss2019.mcsp.julyguild.log.GuildLog;
import com.github.julyss2019.mcsp.julyguild.request.RequestManager;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julyguild.command.GUICommand;
import com.github.julyss2019.mcsp.julyguild.command.PluginCommand;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.listener.GUIListener;
import com.github.julyss2019.mcsp.julyguild.listener.TeleportListener;
import com.github.julyss2019.mcsp.julyguild.listener.TpAllListener;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.task.RequestCleanTask;
import com.github.julyss2019.mcsp.julyguild.thirdparty.PlaceholderAPIExpansion;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.PlayerPointsEconomy;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.VaultEconomy;
import com.github.julyss2019.mcsp.julylibrary.JulyLibrary;
import com.github.julyss2019.mcsp.julylibrary.commandv2.JulyCommandHandler;
import com.github.julyss2019.mcsp.julylibrary.config.JulyConfig;
import com.github.julyss2019.mcsp.julylibrary.logger.FileLogger;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.utils.FileUtil;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 强制依赖：JulyLibrary, Vault
 * 软依赖：PlaceholderAPI, PlayerPoints
 */
public class JulyGuild extends JavaPlugin {
    public static final String VERSION = "2.0.1";
    private final boolean DEV_MODE = false;
    private final String[] GUI_RESOURCES = new String[] {
            "GuildCreateGUI.yml",
            "GuildInfoGUI.yml",
            "GuildMemberListGUI.yml",
            "GuildMineGUI.yml",
            "GuildDonateGUI.yml",
            "GuildJoinCheckGUI.yml",
            "GuildMemberManageGUI.yml",
            "GuildIconRepositoryGUI.yml",
            "MainGUI.yml"}; // GUI资源文件
    private final String[] CONFIG_RESOURCES = new String[] {"conf.yml", "lang.yml"}; // 根资源文件
    private final String[] SHOP_RESOURCES = new String[] {"Shop1.yml", "Shop2.yml"};
    private final String[] DEPEND_PLUGINS = new String[] {"JulyLibrary", "Vault"};

    private static JulyGuild instance;

    private GuildManager guildManager;
    private GuildPlayerManager guildPlayerManager;
    private CacheGuildManager cacheGuildManager;
    private RequestManager requestManager;

    private JulyCommandHandler julyCommandHandler;
    private VaultEconomy vaultEconomy;
    private PlayerPointsEconomy playerPointsEconomy;
    private FileLogger fileLogger;
    private PluginManager pluginManager;

    private YamlConfiguration langYaml;
    private Map<String, YamlConfiguration> guiYamlMap = new HashMap<>();
    private Map<String, YamlConfiguration> shopYamlMap = new HashMap<>();

    private Object placeholderAPIExpansion; // 这里不能使用 PlaceholderAPIExpansion 作为类型，否则可能会出现 NoClassDefFoundError。

    /**
     * 创建jar包内的资源文件（如果不存在）
     * @param fileName
     * @param outFile
     */
    private void saveResourceFile(String fileName, File outFile) {
        File outParentFile = outFile.getParentFile();

        // 创建父文件夹
        if (!outParentFile.exists() && !outParentFile.mkdirs()) {
            setEnabled(false);
            throw new RuntimeException("创建文件夹失败: " + outParentFile.getAbsolutePath());
        }

        if (!outFile.exists()) {
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in = getResource(fileName);

                if (in == null) {
                    throw new RuntimeException("文件不存在: " + fileName);
                }

                out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.close();
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            warning("文件 " + outFile.getAbsolutePath() + " 被创建.");
        }
    }

    private File getConfigFile(String fileName) {
        return new File(getDataFolder(), "config" + File.separator + fileName);
    }

    private File getGUIFile(String fileName) {
        return new File(getDataFolder(), "config" + File.separator + "gui" + File.separator +  fileName);
    }

    /**
     * 补全YAML文件
     * @param targetFile 目标配置文件
     * @param completedSection 完整的配置文件
     */
    private void completeConfig(File targetFile, YamlConfiguration targetYaml, ConfigurationSection completedSection) {
        boolean changed = false;

        for (String key : completedSection.getKeys(false)) {
            if (completedSection.isConfigurationSection(key)) {
                completeConfig(targetFile, targetYaml, completedSection.getConfigurationSection(key));
            } else {
                String sectionPath = completedSection.getCurrentPath();
                String fullPath = sectionPath.equals("") ? key : sectionPath + "." + key;

                if (!targetYaml.contains(fullPath)) {
                    targetYaml.set(fullPath, completedSection.get(key));
                    warning("文件 " + targetFile.getAbsolutePath() + " 被补全配置项 " + fullPath + ".");
                    changed = true;
                }
            }
        }

        if (changed) {
            YamlUtil.saveYaml(targetYaml, targetFile, StandardCharsets.UTF_8);
        }
    }

    private void init() {
        for (String fileName : CONFIG_RESOURCES) {
            File file = getConfigFile(fileName);

            saveResourceFile(fileName, file); // 创建文件如果不存在
            completeConfig(file, YamlConfiguration.loadConfiguration(file), YamlConfiguration.loadConfiguration(new InputStreamReader(getResource(fileName), StandardCharsets.UTF_8))); // 补全配置文件，注意编码
        }

        for (String fileName : GUI_RESOURCES) {
            saveResourceFile("gui/" + fileName, new File(getDataFolder(), "config" + File.separator + "gui" + File.separator + fileName));
        }

        for (String fileName : SHOP_RESOURCES) {
            saveResourceFile("shop/" + fileName, new File(getDataFolder(), "config" + File.separator + "shop" + File.separator + fileName));
        }
    }

    @Override
    public void onEnable() {
        info("插件版本: " + VERSION + ".");
        info("作者: 柒 月, QQ: 884633197, Bug反馈/插件交流群: 786184610.");

        if (DEV_MODE) {
            warning("&c警告: 当前处于开发模式.");
            File dataFolder = new File(getDataFolder(), "config");

            if (dataFolder.exists()) {
                dataFolder.renameTo(new File(dataFolder.getParentFile(), dataFolder.getName() + "_" + System.currentTimeMillis()));
            }
        }

        instance = this;
        this.pluginManager = Bukkit.getPluginManager();
        this.guildPlayerManager = new GuildPlayerManager();
        this.guildManager = new GuildManager();
        this.cacheGuildManager = new CacheGuildManager();
        this.requestManager = new RequestManager();
        this.julyCommandHandler = new JulyCommandHandler();

        for (String pluginName : DEPEND_PLUGINS) {
            if (!Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                error("硬前置插件 " + pluginName + " 未被加载, 插件将被卸载.");
                setEnabled(false);
                return;
            }
        }

        init();
        loadConfig();

        if (MainSettings.isMetricsEnabled()) {
            new Metrics(this);
            Util.sendConsoleMsg("bStats统计: 已启用.");
        }

        this.fileLogger = new FileLogger.Builder()
                .autoFlush(true)
                .loggerFolder(new File(getDataFolder(), "logs"))
                .fileName("%d{yyyy-MM-dd}.log").build();


        /*
        第三方插件注入
         */
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            this.placeholderAPIExpansion = new PlaceholderAPIExpansion();

            if (!((PlaceholderAPIExpansion) placeholderAPIExpansion).register()) {
                error("PlaceholderAPI: Hook失败.");
            } else {
                info("PlaceholderAPI: Hook成功.");
            }
        }

        if (!pluginManager.isPluginEnabled("Vault")) {
            error("Vault: 未启用, 插件将被卸载.");
            setEnabled(false);
            return;
        } else {
            Economy tmp = setupEconomy();

            if (tmp == null) {
                error("Vault: Hook失败, 插件将被卸载.");
                setEnabled(false);
                return;
            }

            this.vaultEconomy = new VaultEconomy(tmp);
            info("Vault: Hook成功.");
        }

        if (pluginManager.isPluginEnabled("PlayerPoints")) {
            this.playerPointsEconomy = new PlayerPointsEconomy(((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI());
            info("PlayerPoints: Hook成功.");
        } else {
            info("PlayerPoints: 未启用.");
        }

        guildManager.loadGuilds();
        checkGuilds();
        requestManager.loadRequests();
        cacheGuildManager.startTask();

        julyCommandHandler.setCommandFormat(LangHelper.Global.getPrefix() + langYaml.getString("Command.command_format"));
        julyCommandHandler.setSubCommandFormat(LangHelper.Global.getPrefix() + langYaml.getString("Command.sub_command_format"));
        julyCommandHandler.setNoneMessage(LangHelper.Global.getPrefix() + langYaml.getString("Command.none"));
        julyCommandHandler.setNoPermissionMessage(LangHelper.Global.getPrefix() + langYaml.getString("Command.no_per"));
        julyCommandHandler.setOnlyConsoleCanUseMessage(LangHelper.Global.getPrefix() + langYaml.getString("Command.only_console_can_use"));
        julyCommandHandler.setOnlyPlayerCanUseMessage(LangHelper.Global.getPrefix() + langYaml.getString("Command.only_player_can_use"));

        getCommand("jguild").setExecutor(julyCommandHandler);

        registerCommands();
        registerListeners();
        runTasks();
        info("载入了 " + guildManager.getGuilds().size() + "个 公会.");
        info("载入了 " + requestManager.getRequests().size() + "个 请求.");
        info("插件初始化完毕.");
    }

    @Override
    public void onDisable() {
        if (isPlaceHolderAPIEnabled() && placeholderAPIExpansion != null) {
            PlaceholderAPI.unregisterExpansion((PlaceholderExpansion) placeholderAPIExpansion);
        }

        if (guildPlayerManager != null) {
            for (GuildPlayer guildPlayer : guildPlayerManager.getOnlineGuildPlayers()) {
                if (guildPlayer.getUsingGUI() != null) {
                    guildPlayer.getUsingGUI().close();
                }
            }
        }

        JulyLibrary.getInstance().getChatInterceptorManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        info("插件被卸载.");
        info("作者: 柒 月, QQ: 884633197, Bug反馈/插件交流群: 786184610.");
    }

    private void checkGuilds() {
        info("开始验证公会: ");

        for (Guild guild : getGuildManager().getGuilds()) {
            for (Guild guild1 : getGuildManager().getGuilds()) {
                if (guild != guild1 && guild.getOwner().getUuid().equals(guild1.getOwner().getUuid())) {
                    error("公会 " + guild.getUuid() + " <-> " + guild1.getUuid() + " 存在两个一个的主人.", new RuntimeException("存在未处理的公会异常"));
                }

                for (GuildMember guildMember : guild.getMembers()) {
                    for (GuildMember guildMember1 : guild1.getMembers()) {
                        if (guild != guild1 && guildMember.getUuid().equals(guildMember1.getUuid())) {
                            error("公会 " + guild.getUuid() + " <-> " + guild1.getUuid() + " 存在两个一个的成员 " + guildMember.getUuid() + ".", new RuntimeException("存在未处理的公会异常"));
                        }
                    }
                }
            }
        }

        info("公会验证完毕.");
    }

    public boolean isPlaceHolderAPIEnabled() {
        return pluginManager.isPluginEnabled("PlaceholderAPI");
    }

    private void runTasks() {
        new RequestCleanTask().runTaskTimerAsynchronously(this, 0L, 20L);
    }

    public CacheGuildManager getCacheGuildManager() {
        return cacheGuildManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    private void registerListeners() {
        pluginManager.registerEvents(new GUIListener(), this);
        pluginManager.registerEvents(new GuildListener(), this);
        pluginManager.registerEvents(new TeleportListener(), this);
        pluginManager.registerEvents(new TpAllListener(), this);
    }

    public void writeGuildLog(GuildLog log) {

    }

    public FileLogger getFileLogger() {
        return fileLogger;
    }

    private Economy setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (economyProvider != null) {
            return economyProvider.getProvider();
        }

        return null;
    }

    private void registerCommands() {
        julyCommandHandler.registerCommand(new GUICommand());
        julyCommandHandler.registerCommand(new PluginCommand());
    }

    /**
     * 重载配置文件
     */
    public void reloadPluginConfig() {
        guiYamlMap.clear();
        loadConfig();
        getGuildPlayerManager().getOnlineGuildPlayers().forEach(guildPlayer -> {
            if (guildPlayer.isUsingGUI()) {
                guildPlayer.closeInventory();
                JulyMessage.sendColoredMessage(guildPlayer.getBukkitPlayer(), "&c插件配置重载被迫关闭GUI.");
            }
        });
    }

    /**
     * 载入配置
     */
    private void loadConfig() {
        File configFile = getConfigFile("conf.yml");
        YamlConfiguration configYaml;

        try {
            configYaml = YamlUtil.loadYaml(configFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("读取文件异常: " + configFile.getAbsolutePath(), e);
        }

        JulyConfig.loadConfig(this, configYaml, MainSettings.class);

        for (String fileName : GUI_RESOURCES) {
            File guiFile = getGUIFile(fileName);
            YamlConfiguration guiYaml;

            try {
                guiYaml = YamlUtil.loadYaml(guiFile, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("读取文件异常: " + guiFile.getAbsolutePath(), e);
            }

            guiYamlMap.put(FileUtil.getFileName(guiFile), guiYaml);
        }

        File[] shopFiles = new File(getDataFolder(), "config" + File.separator + "shop").listFiles();

        //noinspection ConstantConditions
        for (File shopFile : shopFiles) {
            YamlConfiguration shopYaml;

            try {
                shopYaml = YamlUtil.loadYaml(shopFile, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("读取文件异常: " + shopFile.getAbsolutePath(), e);
            }

            String shopName = shopYaml.getString("name");

            if (shopName == null) {
                throw new RuntimeException("name 未设置: " + shopFile.getAbsolutePath());
            }

            shopYamlMap.put(shopName, shopYaml);
        }

        File langFile = getConfigFile("lang.yml");

        try {
            this.langYaml = YamlUtil.loadYaml(langFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("读取文件异常: " + langFile.getAbsolutePath(), e);
        }
    }

    public YamlConfiguration getShopYaml(@NotNull String name) {
        return shopYamlMap.get(name);
    }

    public YamlConfiguration getLangYaml() {
        return langYaml;
    }

    public YamlConfiguration getGUIYaml(@NotNull String name) {
        return guiYamlMap.get(name);
    }

    public VaultEconomy getVaultEconomy() {
        return vaultEconomy;
    }

    public PlayerPointsEconomy getPlayerPointsEconomy() {
        return playerPointsEconomy;
    }

    public boolean isPlayerPointsHooked() {
        return getPlayerPointsEconomy() != null;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public GuildPlayerManager getGuildPlayerManager() {
        return guildPlayerManager;
    }

    public static JulyGuild getInstance() {
        return instance;
    }

    public void warning(String msg) {
        Util.sendConsoleMsg("&e" + msg);
    }

    public void info(String msg) {
        Util.sendConsoleMsg("&f" + msg);
    }

    public void error(String msg) {
        Util.sendConsoleMsg("&c" + msg);
    }

    public void error(String msg, RuntimeException exception) {
        error(msg);
        throw exception;
    }
}
