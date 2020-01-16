package com.github.julyss2019.mcsp.julyguild;

import com.github.julyss2019.mcsp.julyguild.command.MainGUICommand;
import com.github.julyss2019.mcsp.julyguild.command.ReloadCommand;
import com.github.julyss2019.mcsp.julyguild.command.TestCommand;
import com.github.julyss2019.mcsp.julyguild.config.ConfigGuildIcon;
import com.github.julyss2019.mcsp.julyguild.config.ConfigGuildShopItem;
import com.github.julyss2019.mcsp.julyguild.config.GuildShopConfig;
import com.github.julyss2019.mcsp.julyguild.config.IconShopConfig;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.CacheGuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.listener.GUIListener;
import com.github.julyss2019.mcsp.julyguild.log.GuildLog;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.request.RequestManager;
import com.github.julyss2019.mcsp.julyguild.task.RequestCleanTask;
import com.github.julyss2019.mcsp.julyguild.thirdparty.PlaceholderAPIExpansion;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.PlayerPointsEconomy;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.VaultEconomy;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatInterceptor;
import com.github.julyss2019.mcsp.julylibrary.command.JulyCommandExecutor;
import com.github.julyss2019.mcsp.julylibrary.command.tab.JulyTabCommand;
import com.github.julyss2019.mcsp.julylibrary.command.tab.JulyTabCompleter;
import com.github.julyss2019.mcsp.julylibrary.config.JulyConfig;
import com.github.julyss2019.mcsp.julylibrary.logger.FileLogger;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import com.google.gson.Gson;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 强制依赖：JulyLibrary, Vault
 * 软依赖：PlaceholderAPI, PlayerPoints
 */
public class JulyGuild extends JavaPlugin {
    private final boolean DEV_MODE = true;
    private final String[] GUI_RESOURCES = new String[] {
            "GuildCreateGUI.yml",
            "GuildDonateGUI.yml",
            "GuildInfoGUI.yml",
            "GuildMemberListGUI.yml",
            "GuildMineGUI.yml",
            "GuildUpgradeGUI.yml",
            "GuildJoinCheckGUI.yml",
            "GuildMemberManageGUI.yml",
            "MainGUI.yml"}; // GUI资源文件
    private final String[] CONFIG_RESOURCES = new String[] {"config.yml", "lang.yml"}; // 根资源文件

    private final String[] DEPEND_PLUGINS = new String[] {"JulyLibrary", "Vault"};

    private static JulyGuild instance;
    private static final Gson gson = new Gson();

    private GuildManager guildManager;
    private GuildPlayerManager guildPlayerManager;
    private CacheGuildManager cacheGuildManager;
    private RequestManager requestManager;

    private JulyCommandExecutor julyCommandExecutor;
    private JulyTabCompleter julyTabCompleter;
    private VaultEconomy vaultEconomy;
    private PlayerPointsEconomy playerPointsEconomy;
    private FileLogger fileLogger;
    private PluginManager pluginManager;

    private YamlConfiguration langYaml;
    private IconShopConfig iconShopConfig;
    private GuildShopConfig guildShopConfig;
    private Map<String, YamlConfiguration> guiYamlMap = new HashMap<>();

    private Object placeholderAPIExpansion; // 这里不能使用 PlaceholderAPIExpansion 作为类型，否则可能会出现 NoClassDefFoundError。

    private String getFileVersion(File file) {
        return !file.exists() ? null : YamlConfiguration.loadConfiguration(file).getString("version");
    }

    private String getLatestFileVersion(String fileName) {
        InputStream inputStream = getResource(fileName);

        if (inputStream == null) {
            throw new RuntimeException(fileName + " 不存在");
        }

        return YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream)).getString("version");
    }

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

        String currentVersion = getFileVersion(outFile); // 当前版本
        String latestVersion = getLatestFileVersion(fileName); // 最新版本

        if (currentVersion != null && latestVersion != null && !currentVersion.equals(latestVersion)) {
            warning("文件 " + outFile.getAbsolutePath() + " 可能需要更新(v" + currentVersion + "," + latestVersion + ")");
        }

        if (!outFile.exists()) {
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in = getResource(fileName);
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

            warning("文件 " + outFile.getAbsolutePath() + " 被创建(v" + latestVersion + ").");
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
            YamlUtil.saveYaml(targetYaml, targetFile);
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
    }

    @Override
    public void onEnable() {
//        if (DEV_MODE) {
//            warning("&c警告: 当前处于开发模式.");
//            File[] files = new File(getDataFolder(), "config" + File.separator + "gui").listFiles();
//
//            if (files != null) {
//                for (File file : files) {
//                    file.delete();
//                }
//            }
//        }

        instance = this;
        this.pluginManager = Bukkit.getPluginManager();

        for (String pluginName : DEPEND_PLUGINS) {
            if (!Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                Util.sendColoredConsoleMessage("&c硬前置插件 " + pluginName + " 未被加载, 插件将被卸载.");
                setEnabled(false);
                return;
            }
        }

        init();
        loadConfig();

        if (MainSettings.isMetricsEnabled()) {
            new Metrics(this);
            Util.sendColoredConsoleMessage("bStats统计: 已启用.");
        }

        this.fileLogger = new FileLogger.Builder()
                .autoFlush(true)
                .loggerFolder(new File(getDataFolder(), "logs"))
                .fileName("%d{yyyy-MM-dd}.log").build();
        this.julyCommandExecutor = new JulyCommandExecutor();
        this.julyTabCompleter = new JulyTabCompleter();
        this.guildPlayerManager = new GuildPlayerManager();
        this.guildManager = new GuildManager();
        this.cacheGuildManager = new CacheGuildManager();
        this.requestManager = new RequestManager();

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

        julyCommandExecutor.setPrefix(langYaml.getString("Global.command_prefix"));
        guildManager.loadGuilds();
        checkGuilds();
        requestManager.loadRequests();
        cacheGuildManager.startTask();

        getCommand("jguild").setExecutor(julyCommandExecutor);
        getCommand("jguild").setTabCompleter(julyTabCompleter);

        julyCommandExecutor.register(new TestCommand());



        registerCommands();
        registerListeners();
        runTasks();
        info("载入了 " + guildManager.getGuilds().size() + "个 公会.");
        info("载入了 " + requestManager.getRequests().size() + "个 请求.");
        info("载入了 " + guildShopConfig.getShopItems().size() + "个 公会商店物品.");
        info("插件初始化完毕.");

        Util.sendColoredConsoleMessage("&b作者: 柒 月, QQ: 884633197, Bug反馈/插件交流群: 786184610.");
        Util.sendColoredConsoleMessage("编码测试: " + MainSettings.getAnnouncementDefault().toString());
    }

    @Override
    public void onDisable() {
        if (isPlaceHolderAPIEnabled()) {
            PlaceholderAPI.unregisterExpansion((PlaceholderExpansion) placeholderAPIExpansion);
        }

        for (GuildPlayer guildPlayer : getGuildPlayerManager().getOnlineGuildPlayers()) {
            if (guildPlayer.getUsingGUI() != null) {
                guildPlayer.getUsingGUI().close();
            }
        }

        JulyChatInterceptor.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Util.sendColoredConsoleMessage("&c插件被卸载.");
        Util.sendColoredConsoleMessage("&b作者: 柒 月, QQ: 884633197, Bug反馈/插件交流群: 786184610.");
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
    }

    public void writeGuildLog(GuildLog log) {
        fileLogger.i(gson.toJson(log));
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
        registerCommand(new MainGUICommand());
        registerCommand(new ReloadCommand());
    }

    private void registerCommand(JulyTabCommand command) {
        julyCommandExecutor.register(command);
        julyTabCompleter.register(command);
    }

    /**
     * 重载配置文件
     */
    public void reloadPluginConfig() {
        guiYamlMap.clear();
        loadConfig();
    }

    /**
     * 载入配置
     */
    private void loadConfig() {
        File configFile = getConfigFile("config.yml");
        YamlConfiguration configYaml;

        try {
            configYaml = YamlUtil.loadYaml(configFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("读取文件异常: " + configFile.getAbsolutePath(), e);
        }

        JulyConfig.loadConfig(this, configYaml, MainSettings.class);

        for (String fileName : GUI_RESOURCES) {
            File guiFile = getGUIFile(fileName);
            YamlConfiguration yaml;

            try {
                yaml = YamlUtil.loadYaml(guiFile, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("读取文件异常: " + guiFile.getAbsolutePath(), e);
            }

            guiYamlMap.put(fileName.substring(0, fileName.indexOf(".yml")), yaml);
        }

        this.guildShopConfig = new GuildShopConfig();
        this.iconShopConfig = new IconShopConfig();

        File langFile = getConfigFile("lang.yml");

        try {
            this.langYaml = YamlUtil.loadYaml(langFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("读取文件异常: " + langFile.getAbsolutePath(), e);
        }

        loadSpecialConfig();
    }

    /**
     * 载入特殊的配置
     */
    private void loadSpecialConfig() {
        YamlConfiguration iconShopYml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "icon_shop_1.yml"));

        if (iconShopYml.contains("items")) {
            ConfigurationSection itemsSection = iconShopYml.getConfigurationSection("items");

            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = iconShopYml.getConfigurationSection("items").getConfigurationSection(key);
                ConfigGuildIcon configGuildIcon = new ConfigGuildIcon();

                configGuildIcon.setMaterial(Material.valueOf(itemSection.getString("material")));
                configGuildIcon.setDurability((short) itemSection.getInt("durability"));
                configGuildIcon.setDisplayName(itemSection.getString("display_name"));
                configGuildIcon.setLores(itemSection.getStringList("lores"));
                configGuildIcon.setMoneyPayEnabled(itemSection.getBoolean("cost.money.enabled"));
                configGuildIcon.setPointsPayEnabled(itemSection.getBoolean("cost.points.enabled"));
                configGuildIcon.setMoneyCost(itemSection.getInt("cost.money.amount"));
                configGuildIcon.setPointsCost(itemSection.getInt("cost.points.amount"));

                this.iconShopConfig.addIcon(configGuildIcon);
            }
        }

        FileConfiguration guildShopYml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "shops/GuildShop.yml"));

        if (guildShopYml.contains("items")) {
            ConfigurationSection itemsSection = guildShopYml.getConfigurationSection("items");

            for (String shopItemName : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(shopItemName);
                ConfigGuildShopItem item = new ConfigGuildShopItem();

                item.setName(shopItemName);
                item.setIndex(itemSection.getInt("index"));
                item.setMaterial(Material.valueOf(itemSection.getString("material")));
                item.setDurability((short) itemSection.getInt("durability"));
                item.setDisplayName(itemSection.getString("display_name"));
                item.setLores(itemSection.getStringList("lores"));
                item.setTarget(ConfigGuildShopItem.Target.valueOf(itemSection.getString("target")));
                item.setMoneyEnabled(itemSection.getBoolean("cost.money.enabled"));
                item.setMoneyFormula(itemSection.getString("cost.money.formula"));
                item.setPointsEnabled(itemSection.getBoolean("cost.points.enabled"));
                item.setPointsFormula(itemSection.getString("cost.points.formula"));
                item.setMessage(itemSection.getString("message"));
                item.setRewardCommands(itemSection.getStringList("reward_commands"));

                this.guildShopConfig.addItem(item);
            }
        }
    }

    public YamlConfiguration getLangYaml() {
        return langYaml;
    }

    public YamlConfiguration getGUIYaml(String name) {
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

    public IconShopConfig getIconShopConfig() {
        return iconShopConfig;
    }

    public void warning(String msg) {
        Util.sendColoredConsoleMessage("&e" + msg);
    }

    public void info(String msg) {
        Util.sendColoredConsoleMessage("&f" + msg);
    }

    public void error(String msg) {
        Util.sendColoredConsoleMessage("&c" + msg);
    }

    public void error(String msg, RuntimeException exception) {
        error(msg);
        throw exception;
    }
}
