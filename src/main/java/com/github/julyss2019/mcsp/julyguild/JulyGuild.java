package com.github.julyss2019.mcsp.julyguild;

import com.github.julyss2019.mcsp.julyguild.command.Command;
import com.github.julyss2019.mcsp.julyguild.command.MainGUICommand;
import com.github.julyss2019.mcsp.julyguild.command.ReloadCommand;
import com.github.julyss2019.mcsp.julyguild.config.*;
import com.github.julyss2019.mcsp.julyguild.guild.CacheGuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.listener.GUIListener;
import com.github.julyss2019.mcsp.julyguild.listener.GuildShopListener;
import com.github.julyss2019.mcsp.julyguild.listener.TpAllListener;
import com.github.julyss2019.mcsp.julyguild.log.GuildLog;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.task.RequestCleanTask;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.command.JulyCommandExecutor;
import com.github.julyss2019.mcsp.julylibrary.config.JulyConfig;
import com.github.julyss2019.mcsp.julylibrary.logger.FileLogger;
import com.github.julyss2019.mcsp.julylibrary.logger.JulyFileLogger;
import com.google.gson.Gson;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class JulyGuild extends JavaPlugin {
    private static JulyGuild instance;
    private static final Gson gson = new Gson();

    private GuildManager guildManager;
    private GuildPlayerManager guildPlayerManager;
    private CacheGuildManager cacheGuildManager;

    private MainSettings mainSettings;
    private IconShopSettings iconShopSettings;
    private GuildShopSettings guildShopSettings;

    private final String[] VERSION_YML_FILES = new String[] {"config.yml", "lang.yml"};
    private final String[] DEPEND_PLUGINS = new String[] {"JulyLibrary", "PlaceholderAPI", "Vault"};
    private final String[] INIT_FOLDERS = new String[] {"players", "guilds", "logs"};
    private final String[] INIT_FILES = new String[] {"config.yml", "icon_shop.yml", "guild_shop.yml", "lang.yml"};

    private JulyCommandExecutor julyCommandExecutor;
    private PlayerPointsAPI playerPointsAPI;
    private Economy vaultAPI;
    private FileLogger fileLogger;
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        for (String pluginName : DEPEND_PLUGINS) {
            if (!Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                Util.sendColoredConsoleMessage("&c[!] 硬前置插件 " + pluginName + " 未成功加载, 插件将被卸载.");
                setEnabled(false);
                return;
            }
        }

        instance = this;
        this.pluginManager = Bukkit.getPluginManager();

        init();
        updateVersionYmlFiles();
        loadConfig();

        if (mainSettings.isMetricsEnabled()) {
            new Metrics(this);
            Util.sendColoredConsoleMessage("&e[!] bStats 统计已启用.");
        }

        this.fileLogger = JulyFileLogger.getLogger(new File(getDataFolder(), "logs"), null, 5);
        this.julyCommandExecutor = new JulyCommandExecutor(this);
        this.guildPlayerManager = new GuildPlayerManager();
        this.guildManager = new GuildManager();
        this.cacheGuildManager = new CacheGuildManager();
        PlaceholderAPIExpansion placeholderAPIExpansion = new PlaceholderAPIExpansion();

        /*
        第三方插件注入
         */
        if (!pluginManager.isPluginEnabled("Vault") || !setupEconomy()) {
            Util.sendColoredConsoleMessage("&c[!] Vault: Hook失败, 插件将被卸载.");
            setEnabled(false);
            return;
        } else {
            Util.sendColoredConsoleMessage("&eVault: Hook成功.");
        }

        if (!placeholderAPIExpansion.register()) {
            getLogger().warning("&c[!] PlaceholderAPI: Hook失败, 插件将被卸载.");
            setEnabled(false);
            return;
        } else {
            Util.sendColoredConsoleMessage("&ePlaceholderAPI: Hook成功.");
        }

        if (pluginManager.isPluginEnabled("PlayerPoints")) {
            this.playerPointsAPI = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
            Util.sendColoredConsoleMessage("&ePlayerPoints: Hook成功.");
        } else {
            Util.sendColoredConsoleMessage("&ePlayerPoints: 未启用.");
        }

        julyCommandExecutor.setPrefix(Lang.getString("JulyGuild.command_prefix"));
        guildManager.loadGuilds();
        cacheGuildManager.startTask();

        getCommand("jguild").setExecutor(julyCommandExecutor);
        registerCommands();
        registerListeners();
        runTasks();
        Util.sendColoredMessage(Bukkit.getConsoleSender(), "载入了 " + guildManager.getGuilds().size() + "个 宗门.");
        Util.sendColoredMessage(Bukkit.getConsoleSender(), "载入了 " + iconShopSettings.getConfigGuildIcons().size() + "个 图标商店物品.");
        Util.sendColoredMessage(Bukkit.getConsoleSender(), "载入了 " + guildShopSettings.getConfigGuildShopItems().size() + "个 宗门商店物品.");
        Util.sendColoredMessage(Bukkit.getConsoleSender(), "插件初始化完毕.");

        for (Map.Entry<String, Object> entry : Lang.getAll().entrySet()) {
            getLogger().info(entry.getKey() + ", " + entry.getValue());
        }
    }

    public boolean isPlaceHolderAPIEnabled() {
        return pluginManager.isPluginEnabled("PlaceholderAPI");
    }

    @Override
    public void onDisable() {
        if (isPlaceHolderAPIEnabled() && PlaceholderAPI.isRegistered("guild")) {
            PlaceholderAPI.unregisterPlaceholderHook("guild");
        }

        for (GuildPlayer guildPlayer : getGuildPlayerManager().getOnlineGuildPlayers()) {
            if (guildPlayer.getUsingGUI() != null) {
                guildPlayer.getUsingGUI().close();
            }
        }

        Util.sendColoredConsoleMessage("插件被卸载.");
    }

    public GuildShopSettings getGuildShopSettings() {
        return guildShopSettings;
    }

    private void runTasks() {
        new RequestCleanTask().runTaskTimerAsynchronously(this, 0L, 20L);
    }

    public CacheGuildManager getCacheGuildManager() {
        return cacheGuildManager;
    }

    public IconShopSettings getIconShopSettings() {
        return iconShopSettings;
    }

    private void registerListeners() {
        pluginManager.registerEvents(new GUIListener(), this);
        pluginManager.registerEvents(new TpAllListener(), this);
        pluginManager.registerEvents(new GuildShopListener(), this);
    }

    public void writeGuildLog(FileLogger.LoggerLevel loggerLevel, GuildLog log) {
        fileLogger.log(loggerLevel, gson.toJson(log));
    }

    public FileLogger getFileLogger() {
        return fileLogger;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (economyProvider != null) {
            vaultAPI = economyProvider.getProvider();
        }

        return (vaultAPI != null);
    }

    private void registerCommands() {
        registerCommand(new MainGUICommand());
        registerCommand(new ReloadCommand());
    }

    private void registerCommand(Command command) {
        julyCommandExecutor.register(command);
    }

    private void init() {
        for (String folderPath : INIT_FOLDERS) {
            File folder = new File(getDataFolder(), folderPath);

            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    setEnabled(false);
                    throw new RuntimeException("&c[!] 创建文件夹失败: " + folderPath + ", 插件将被卸载.");
                }
            }
        }

        for (String filePath : INIT_FILES) {
            File file = new File(getDataFolder(), filePath);

            if (!file.exists()) {
                saveResource(filePath, false);
            }
        }
    }

    public void reloadPluginConfig() {
        mainSettings.reset();
        iconShopSettings.reset();
        guildShopSettings.reset();

        JulyConfig.reloadConfig(this, YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml")), mainSettings);
        JulyConfig.reloadConfig(this, YamlConfiguration.loadConfiguration(new File(getDataFolder(), "icon_shop.yml")), iconShopSettings);

        loadSpecialConfig();
        loadLang();
    }

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

                Util.sendColoredConsoleMessage("文件版本已更新: " + name + "(" + currentVersion + "->" + latestVersion + ").");
            }
        }
    }

    private void loadConfig() {
        YamlConfiguration configYml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        this.mainSettings = (MainSettings) JulyConfig.loadConfig(this, configYml, MainSettings.class);
        this.iconShopSettings = (IconShopSettings) JulyConfig.loadConfig(this, YamlConfiguration.loadConfiguration(new File(getDataFolder(), "icon_shop.yml")), IconShopSettings.class);
        this.guildShopSettings = new GuildShopSettings();

        loadSpecialConfig();
        loadLang();
    }

    public boolean isPlayerPointsHooked() {
        return getPlayerPointsAPI() != null;
    }

    private void loadLang(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            // 迭代到类型不是 ConfigurationSection 为止
            if (section.isConfigurationSection(key)) {
                loadLang(section.getConfigurationSection(key));
            } else if (section.isString(key)) {
                String path = section.getParent() == null ? key : section.getCurrentPath() + "." + key;

                Lang.putString(path, section.getString(key));
            } else if (section.isList(key)) {
                String path = section.getParent() == null ? key : section.getCurrentPath() + "." + key;

                Lang.putStringList(path, section.getStringList(key));
            }
        }
    }

    private void loadLang() {
        loadLang(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "lang.yml")));
    }

    private void loadSpecialConfig() {
        FileConfiguration iconShopConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "icon_shop.yml"));

        if (iconShopConfig.contains("items")) {
            ConfigurationSection itemsSection = iconShopConfig.getConfigurationSection("items");

            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = iconShopConfig.getConfigurationSection("items").getConfigurationSection(key);
                ConfigGuildIcon configGuildIcon = new ConfigGuildIcon();

                configGuildIcon.setMaterial(Material.valueOf(itemSection.getString("material")));
                configGuildIcon.setDurability((short) itemSection.getInt("durability"));
                configGuildIcon.setDisplayName(itemSection.getString("display_name"));
                configGuildIcon.setLores(itemSection.getStringList("lores"));
                configGuildIcon.setMoneyPayEnabled(itemSection.getBoolean("cost.money.enabled"));
                configGuildIcon.setPointsPayEnabled(itemSection.getBoolean("cost.points.enabled"));
                configGuildIcon.setMoneyCost(itemSection.getInt("cost.money.amount"));
                configGuildIcon.setPointsCost(itemSection.getInt("cost.points.amount"));

                iconShopSettings.addConfigGuildIcon(configGuildIcon);
            }
        }

        FileConfiguration guildShopConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "guild_shop.yml"));

        if (guildShopConfig.contains("items")) {
            ConfigurationSection itemsSection = guildShopConfig.getConfigurationSection("items");

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

                guildShopSettings.addItem(item);
            }
        }
    }

    public MainSettings getMainSettings() {
        return mainSettings;
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

    public PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }

    public Economy getVaultAPI() {
        return vaultAPI;
    }
}
