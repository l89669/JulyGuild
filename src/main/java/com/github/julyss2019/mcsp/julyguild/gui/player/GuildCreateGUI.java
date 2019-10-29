package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.Placeholder;
import com.github.julyss2019.mcsp.julyguild.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.config.ConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.ConfigGUIItem;
import com.github.julyss2019.mcsp.julyguild.config.MainConfig;
import com.github.julyss2019.mcsp.julyguild.gui.BaseGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.TitleBuilder;
import com.github.julyss2019.mcsp.julylibrary.utils.ItemUtil;
import com.github.julyss2019.mcsp.julylibrary.utils.PlayerUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class GuildCreateGUI extends BaseGUI {
    private String guildName;
    private Inventory inventory;
    private boolean noAction = true;

    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("GuildCreateGUI");
    private final ConfigurationSection thisGuiSection = plugin.getGuiYamlConfig().getConfigurationSection("GuildCreateGUI");
    private final Economy vault = plugin.getVaultAPI();
    private final PlayerPointsAPI playerPointsAPI = plugin.getPlayerPointsAPI();
    private final GuildManager guildManager = plugin.getGuildManager();

    public GuildCreateGUI(GuildPlayer guildPlayer, String guildName) {
        super(GUIType.CREATE, guildPlayer);

        this.guildName = guildName;
        build();
    }

    @Override
    public void build() {
        super.build();

        ConfigGUI.Builder guiBuilder = (ConfigGUI.Builder) new ConfigGUI.Builder()
                .byConfig(thisGuiSection)
                .colored()
                .listener(new InventoryListener() {
            @Override
            public void onClose(InventoryCloseEvent event) {
                if (noAction) {
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("on_close"));
                }
            }
        });

        if (MainConfig.isGuildCreateCostMoneyEnabled()) {
            guiBuilder.item(ConfigGUIItem.get(thisGuiSection.getConfigurationSection("money"), new Placeholder.Builder().add("%AMOUNT%", String.valueOf(MainConfig.getGuildCreateCostMoneyAmount())).build()), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    noAction = false;
                    close();

                    if (guildPlayer.isInGuild()) {
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("GuildCreateGUI.already_in"));
                        return;
                    }

                    double playerMoney = vault.getBalance(bukkitPlayer);

                    if (playerMoney < MainConfig.getGuildCreateCostMoneyAmount()) {
                        Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("GuildCreateGUI.money_not_enough"), new Placeholder.Builder().add("%AMOUNT%", MainConfig.getGuildCreateCostMoneyAmount() - playerMoney + "个 &c金币!").build()));
                        return;
                    }

                    vault.withdrawPlayer(bukkitPlayer, MainConfig.getGuildCreateCostMoneyAmount());
                    createGuild(guildPlayer, guildName);
                }
            });
        }

        if (MainConfig.isGuildCreateCostPointsEnabled()) {
            guiBuilder.item(ConfigGUIItem.get(thisGuiSection.getConfigurationSection("points"), new Placeholder.Builder().add("%AMOUNT%", String.valueOf(MainConfig.getGuildCreateCostPointsAmount())).build()), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    noAction = false;
                    close();

                    if (guildPlayer.isInGuild()) {
                        Util.sendColoredMessage(bukkitPlayer, "&c你已经在一个宗门里了.");
                        return;
                    }

                    int playerPoints = playerPointsAPI.look(bukkitPlayer.getUniqueId());

                    if (playerPoints < MainConfig.getGuildCreateCostPointsAmount()) {
                        Util.sendColoredMessage(bukkitPlayer, "&c要创建宗门, 你还需要 &e" + (MainConfig.getGuildCreateCostPointsAmount() - playerPoints) + "个 &c点券!");
                        return;
                    }

                    playerPointsAPI.take(bukkitPlayer.getUniqueId(), MainConfig.getGuildCreateCostPointsAmount());
                    createGuild(guildPlayer, guildName);
                }
            });
        }

        if (MainConfig.isGuildCreateCostItemEnabled()) {
            guiBuilder.item(ConfigGUIItem.get(thisGuiSection.getConfigurationSection("item"), new Placeholder.Builder().add("%AMOUNT%", String.valueOf(MainConfig.getGuildCreateCostItemAmount())).build()), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    noAction = false;
                    close();

                    if (guildPlayer.isInGuild()) {
                        Util.sendColoredMessage(bukkitPlayer, "&c你已经在一个宗门里了.");
                        return;
                    }

                    if (PlayerUtil.hasItem(bukkitPlayer, itemStack -> ItemUtil.containsLore(itemStack, MainConfig.getGuildCreateCostItemKeyLore()), MainConfig.getGuildCreateCostItemAmount())) {
                        PlayerUtil.takeItems(bukkitPlayer, itemStack -> ItemUtil.containsLore(itemStack, MainConfig.getGuildCreateCostItemKeyLore()), MainConfig    .getGuildCreateCostItemAmount());
                        createGuild(guildPlayer, guildName);
                    } else {
                        Util.sendColoredMessage(bukkitPlayer, "&c要创建宗门, 你还需要 &e建帮令x" + MainConfig.getGuildCreateCostItemAmount());
                    }
                }
            });
        }

        this.inventory = guiBuilder.build();
    }

    private void createGuild(GuildPlayer guildPlayer, String guildName) {
        Player bukkitPlayer = guildPlayer.getBukkitPlayer();

        guildManager.createGuild(guildPlayer, guildName);
        JulyMessage.broadcastColoredMessage("&d恭喜 &e" + bukkitPlayer.getName() + " &d创建宗门 &e" + guildName + " &d成功!");
        JulyMessage.sendTitle(bukkitPlayer, new TitleBuilder().text("&d创建宗门成功").colored().build());

        new BukkitRunnable() {
            @Override
            public void run() {
                bukkitPlayer.performCommand("jguild main");
            }
        }.runTaskLater(plugin, 60L);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
