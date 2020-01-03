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
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.listener.GUIListener;
import com.github.julyss2019.mcsp.julyguild.listener.TpAllListener;
import com.github.julyss2019.mcsp.julyguild.log.GuildLog;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.task.RequestCleanTask;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.PlayerPointsEconomy;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.VaultEconomy;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatInterceptor;
import com.github.julyss2019.mcsp.julylibrary.command.JulyCommandExecutor;
import com.github.julyss2019.mcsp.julylibrary.command.tab.JulyTabCommand;
import com.github.julyss2019.mcsp.julylibrary.command.tab.JulyTabCompleter;
import com.github.julyss2019.mcsp.julylibrary.config.JulyConfig;
import com.github.julyss2019.mcsp.julylibrary.logger.FileLogger;
import com.github.julyss2019.mcsp.julylibrary.utils.TimeUtil;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import com.google.gson.Gson;
import me.clip.placeholderapi.PlaceholderAPI;
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
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 强制依赖：JulyLibrary, Vault
 * 软依赖：PlaceholderAPI, PlayerPoints
 */
public class JulyGuild extends JavaPlugin {
    private final boolean CODING = true;
    private static JulyGuild instance;
    private static final Gson gson = new Gson();

    private GuildManager guildManager;
    private GuildPlayerManager guildPlayerManager;
    private CacheGuildManager cacheGuildManager;

    private final String[] VERSION_YML_FILES = new String[] {"config.yml", "resources/lang.yml", "gui.yml"};
    private final String[] DEPEND_PLUGINS = new String[] {"JulyLibrary", "Vault"};
    private final String[] INIT_FOLDERS = new String[] {"players", "guilds", "logs"};
    private final String[] INIT_FILES = new String[] {"gui.yml", "config.yml", "resources/shops/GuildShop.yml", "resources/lang.yml"};

    private JulyCommandExecutor julyCommandExecutor;
    private JulyTabCompleter julyTabCompleter;
    private VaultEconomy vaultEconomy;
    private PlayerPointsEconomy playerPointsEconomy;
    private FileLogger fileLogger;
    private PluginManager pluginManager;

    private YamlConfiguration langYamlConfig;
    private YamlConfiguration guiYamlConfig;
    private IconShopConfig iconShopConfig;
    private GuildShopConfig guildShopConfig;
    private PlaceholderAPIExpansion placeholderAPIExpansion;

    @Override
    public void onEnable() {
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

        /*
        第三方插件注入
         */
        if (pluginManager.isPluginEnabled("PlayerPoints")) {
            this.placeholderAPIExpansion = new PlaceholderAPIExpansion();

            if (!placeholderAPIExpansion.register()) {
                getLogger().warning("PlaceholderAPI: Hook失败.");
            } else {
                Util.sendColoredConsoleMessage("PlaceholderAPI: Hook成功.");
            }
        }

        if (!pluginManager.isPluginEnabled("Vault")) {
            Util.sendColoredConsoleMessage("&cVault: 未启用, 插件将被卸载.");
            setEnabled(false);
            return;
        } else {
            Economy tmp = setupEconomy();

            if (tmp == null) {
                Util.sendColoredConsoleMessage("&cVault: Hook失败, 插件将被卸载.");
                setEnabled(false);
                return;
            }

            this.vaultEconomy = new VaultEconomy(tmp);
            Util.sendColoredConsoleMessage("Vault: Hook成功.");
        }

        if (pluginManager.isPluginEnabled("PlayerPoints")) {
            this.playerPointsEconomy = new PlayerPointsEconomy(((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI());
            Util.sendColoredConsoleMessage("PlayerPoints: Hook成功.");
        } else {
            Util.sendColoredConsoleMessage("PlayerPoints: 未启用.");
        }

        julyCommandExecutor.setPrefix(langYamlConfig.getString("Global.command_prefix"));
        guildManager.loadAll();
        cacheGuildManager.startTask();

        getCommand("jguild").setExecutor(julyCommandExecutor);
        getCommand("jguild").setTabCompleter(julyTabCompleter);

        julyCommandExecutor.register(new TestCommand());

        registerCommands();
        registerListeners();
        runTasks();
        Util.sendColoredConsoleMessage("载入了 " + guildManager.getGuilds().size() + "个 公会.");
        //Util.sendColoredConsoleMessage("载入了 " + iconShopConfig.getIconMap().size() + "个 图标商店物品.");
        Util.sendColoredConsoleMessage("载入了 " + guildShopConfig.getShopItems().size() + "个 公会商店物品.");
        Util.sendColoredConsoleMessage("插件初始化完毕.");
        Util.sendColoredConsoleMessage("&c作者: 柒 月, QQ: 884633197, 插件交流群: 786184610.");


    }

    public void onDisable() {
        if (isPlaceHolderAPIEnabled()) {
            PlaceholderAPI.unregisterExpansion(placeholderAPIExpansion);
        }

        for (GuildPlayer guildPlayer : getGuildPlayerManager().getOnlineGuildPlayers()) {
            if (guildPlayer.getUsingGUI() != null) {
                guildPlayer.getUsingGUI().close();
            }
        }

        JulyChatInterceptor.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Util.sendColoredConsoleMessage("插件被卸载.");
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

    private void registerListeners() {
        pluginManager.registerEvents(new GUIListener(), this);
        pluginManager.registerEvents(new TpAllListener(), this);
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

    private String getCurrentFilePath() {
        ProtectionDomain protectionDomain = JulyGuild.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location;

        try {
            location = (codeSource == null ? null : codeSource.getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String path = (location == null ? null : location.getSchemeSpecificPart());

        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }

        File root = new File(path);

        if (!root.exists()) {
            throw new IllegalStateException(
                    "Unable to determine code source archive from " + root);
        }

        return root.getAbsolutePath();
    }

    private String getFileVersion(File file) {
        return !file.exists() ? null : YamlConfiguration.loadConfiguration(file).getString("version");
    }

    private String getLatestFileVersion(String fileName) {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(getResource(fileName))).getString("version");
    }

    /*
    初始化
     */
    private void init() {
        try {
            JarFile jarFile = new JarFile(getCurrentFilePath());
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String fileName = jarEntry.getName();

                // 过滤类文件
                if (fileName.startsWith("resources") && !jarEntry.isDirectory()) {
                    File outFile = new File(getDataFolder(), fileName.substring("resources/".length()).replace("/", File.separator));
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
                        InputStream in = getResource(fileName);
                        FileOutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];
                        int len;

                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                        warning("文件 " + outFile.getAbsolutePath() + " 被创建(v" + latestVersion + ").");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        YamlConfiguration latestConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("resources/config.yml")));
        File currentConfigFile = new File(getDataFolder(), "config.yml");
        YamlConfiguration currentConfigYml = YamlConfiguration.loadConfiguration(currentConfigFile);

        for (String key : latestConfig.getKeys(false)) {
            if (!currentConfigYml.contains(key)) {
                currentConfigYml.set(key, latestConfig.get(key));

                YamlUtil.saveYaml(currentConfigYml, currentConfigFile);
                warning("文件 " + currentConfigFile.getAbsolutePath() + " 被补全配置项 " + key + ".");
            }
        }

    }

    /**
     * 重载配置文件
     */
    public void reloadPluginConfig() {
        iconShopConfig.reset();
        guildShopConfig.reset();

        loadSpecialConfig();
        JulyConfig.loadConfig(this, YamlConfiguration.loadConfiguration(new File(getDataFolder(), "icon_shop_1.yml")), IconShopConfig.class);
        this.langYamlConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "resources/lang.yml"));
        this.guiYamlConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui.yml"));
    }

    private void onCoding() {
        for (String name : VERSION_YML_FILES) {
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), name));
            String currentVersion = yml.getString("VERSION", "0.0.0");
            String latestVersion = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource(name))).getString("VERSION");

            File oldFile = new File(getDataFolder(), name);
            File newFile = new File(getDataFolder(), name + "." + currentVersion);

            if (!oldFile.renameTo(newFile.exists() ? new File(getDataFolder(), name + "." + currentVersion + "." + System.currentTimeMillis()) : newFile)) {
                throw new RuntimeException("文件更名失败: " + name);
            }

            try {
                Files.copy(getResource(name), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("文件更新失败: " + name);
            }

            Util.sendColoredConsoleMessage("&e文件版本已更新: " + name + "(" + currentVersion + "->" + latestVersion + ").");
        }
    }

    /*
    更新版本文件
     */
    private void updateVersionYmlFiles() {
        for (String name : VERSION_YML_FILES) {
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), name));
            String currentVersion = yml.getString("VERSION", "0.0.0");
            String latestVersion = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource(name))).getString("VERSION");

            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                File oldFile = new File(getDataFolder(), name);

                if (!oldFile.renameTo(new File(getDataFolder(), name + "." + currentVersion))) {
                    throw new RuntimeException("文件更名失败: " + name);
                }

                try {
                    Files.copy(getResource(name), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("文件更新失败: " + name);
                }

                Util.sendColoredConsoleMessage("&e文件版本已更新: " + name + "(" + currentVersion + "->" + latestVersion + ").");
            }
        }
    }

    /**
     * 载入配置
     */
    private void loadConfig() {
        YamlConfiguration configYml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));



        JulyConfig.loadConfig(this, configYml, MainSettings.class);

        this.guildShopConfig = new GuildShopConfig();
        this.iconShopConfig = new IconShopConfig();
        this.langYamlConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "resources/lang.yml"));
        this.guiYamlConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui.yml"));
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

        FileConfiguration guildShopYml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "resources/shops/GuildShop.yml"));

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

    public YamlConfiguration getLangYamlConfig() {
        return langYamlConfig;
    }

    public YamlConfiguration getGuiYamlConfig() {
        return guiYamlConfig;
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
}
