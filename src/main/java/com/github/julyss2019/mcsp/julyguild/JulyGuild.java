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
import com.github.julyss2019.mcsp.julyguild.tasks.RequestCleanTask;
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

import java.io.File;

public class JulyGuild extends JavaPlugin {
    private static JulyGuild instance;
    private static final Gson gson = new Gson();

    private GuildManager guildManager;
    private GuildPlayerManager guildPlayerManager;
    private CacheGuildManager cacheGuildManager;

    private MainSettings mainSettings;
    private IconShopSettings iconShopSettings;
    private GuildShopSettings guildShopSettings;

    private String[] initFolderPaths = new String[] {"players", "guilds", "logs"};
    private String[] initFilePaths = new String[] {"config.yml", "icon_shop.yml", "guild_shop.yml"};
    private JulyCommandExecutor julyCommandExecutor;
    private PlayerPointsAPI playerPointsAPI;
    private Economy vaultAPI;
    private FileLogger fileLogger;
    private PluginManager pluginManager;
    private PlaceholderAPIExpansion placeholderAPIExpansion;

    @Override
    public void onEnable() {
        new Metrics(this);

        instance = this;
        this.pluginManager = Bukkit.getPluginManager();

        init();
        loadConfig();

        this.fileLogger = JulyFileLogger.getLogger(new File(getDataFolder(), "logs"), null, 5);
        this.julyCommandExecutor = new JulyCommandExecutor(this);
        this.guildPlayerManager = new GuildPlayerManager();
        this.guildManager = new GuildManager();
        this.cacheGuildManager = new CacheGuildManager();
        this.placeholderAPIExpansion = new PlaceholderAPIExpansion();

        /*
        第三方插件注入
         */
        if (!pluginManager.isPluginEnabled("Vault") || !setupEconomy()) {
            Util.sendColoredConsoleMessage("&cVault: Hook失败.");
            setEnabled(false);
            return;
        } else {
            Util.sendColoredConsoleMessage("&eVault: Hook成功.");
        }

        if (!placeholderAPIExpansion.register()) {
            getLogger().warning("&cPlaceholderAPI: Hook失败.");
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

        julyCommandExecutor.setPrefix("§a[宗门] §f");
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

        Util.sendColoredMessage(Bukkit.getConsoleSender(), "插件被卸载.");
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
        for (String folderPath : initFolderPaths) {
            File folder = new File(getDataFolder(), folderPath);

            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    setEnabled(false);
                    throw new RuntimeException("&c创建文件夹失败: " + folderPath);
                }
            }
        }

        for (String filePath : initFilePaths) {
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
    }

    private void loadConfig() {
        reloadConfig(); // 必须先重读，否则将发生死循环
        YamlConfiguration configYml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        String oldConfigVersion = configYml.getString("config_version", "0.0.0");
        String currentConfigVersion = getConfig().getDefaultSection().getString("config_version"); // 最新版本


        // 如果配置版本不一样，重命名文件，创建新的配置
        if (!currentConfigVersion.equals(oldConfigVersion)) {
            File configFile = new File(getDataFolder(), "config.yml");

            if (configFile.exists() && !configFile.renameTo(new File(getDataFolder(), "config.yml." + oldConfigVersion))) {
                throw new RuntimeException("&c配置文件更新失败.");
            }

            Util.sendColoredConsoleMessage("&e配置文件版本不一致, 将重新生成(" + oldConfigVersion + "->" + currentConfigVersion + ").");
            saveDefaultConfig();
            loadConfig();
            return;
        }

        this.mainSettings = (MainSettings) JulyConfig.loadConfig(this, configYml, MainSettings.class);
        this.iconShopSettings = (IconShopSettings) JulyConfig.loadConfig(this, YamlConfiguration.loadConfiguration(new File(getDataFolder(), "icon_shop.yml")), IconShopSettings.class);
        this.guildShopSettings = new GuildShopSettings();

        loadSpecialConfig();
    }

    public boolean isPlayerPointsHooked() {
        return getPlayerPointsAPI() != null;
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
