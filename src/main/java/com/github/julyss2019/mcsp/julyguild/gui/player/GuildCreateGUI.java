package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
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
                .fromConfig(thisGuiSection)
                .colored()
                .listener(new InventoryListener() {
            @Override
            public void onClose(InventoryCloseEvent event) {
                if (noAction) {
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("on_close"));
                }
            }
        });

        // 金币
        if (MainConfig.isGuildCreateCostMoneyEnabled()) {
            guiBuilder.item(ConfigGUIItem.get(thisGuiSection.getConfigurationSection("money"), new Placeholder.Builder().add("%AMOUNT%", String.valueOf(MainConfig.getGuildCreateCostMoneyAmount())).build()), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    noAction = false;
                    close();

                    if (guildPlayer.isInGuild()) {
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("already_in"));
                        return;
                    }

                    double playerMoney = vault.getBalance(bukkitPlayer);

                    if (playerMoney < MainConfig.getGuildCreateCostMoneyAmount()) {
                        Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("money_not_enough"), new Placeholder.Builder().add("%AMOUNT%", MainConfig.getGuildCreateCostMoneyAmount() - playerMoney + "个 &c金币!").build()));
                        return;
                    }

                    vault.withdrawPlayer(bukkitPlayer, MainConfig.getGuildCreateCostMoneyAmount());
                    createGuild(guildPlayer, guildName);
                }
            });
        }

        // 点券
        if (MainConfig.isGuildCreateCostPointsEnabled()) {
            guiBuilder.item(ConfigGUIItem.get(thisGuiSection.getConfigurationSection("points"), new Placeholder.Builder().add("%AMOUNT%", String.valueOf(MainConfig.getGuildCreateCostPointsAmount())).build()), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    noAction = false;
                    close();

                    if (guildPlayer.isInGuild()) {
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("already_in"));
                        return;
                    }

                    int playerPoints = playerPointsAPI.look(bukkitPlayer.getUniqueId());

                    if (playerPoints < MainConfig.getGuildCreateCostPointsAmount()) {
                        Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("points_not_enough"), new Placeholder.Builder().add("%AMOUNT%", String.valueOf(MainConfig.getGuildCreateCostPointsAmount() - playerPoints)).build()));
                        return;
                    }

                    playerPointsAPI.take(bukkitPlayer.getUniqueId(), MainConfig.getGuildCreateCostPointsAmount());
                    createGuild(guildPlayer, guildName);
                }
            });
        }

        // 建帮令
        if (MainConfig.isGuildCreateCostItemEnabled()) {
            guiBuilder.item(ConfigGUIItem.get(thisGuiSection.getConfigurationSection("item"), new Placeholder.Builder().add("%AMOUNT%", String.valueOf(MainConfig.getGuildCreateCostItemAmount())).build()), new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    noAction = false;
                    close();

                    if (guildPlayer.isInGuild()) {
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("already_in"));
                        return;
                    }

                    int hadItemAmount = PlayerUtil.getItemAmount(bukkitPlayer, itemStack -> ItemUtil.containsLore(itemStack, MainConfig.getGuildCreateCostItemKeyLore()));


                    if (hadItemAmount < MainConfig.getGuildCreateCostItemAmount()) {
                        PlayerUtil.takeItems(bukkitPlayer, itemStack -> ItemUtil.containsLore(itemStack, MainConfig.getGuildCreateCostItemKeyLore()), MainConfig.getGuildCreateCostItemAmount());
                        createGuild(guildPlayer, guildName);
                    } else {
                        Util.sendColoredMessage(bukkitPlayer,
                                PlaceholderText.replacePlaceholders(thisLangSection.getString("item_not_enough"), new Placeholder.Builder().add("%AMOUNT%", String.valueOf(hadItemAmount - MainConfig.getGuildCreateCostItemAmount())).build()));
                    }
                }
            });
        }

        this.inventory = guiBuilder.build();
    }

    private void createGuild(GuildPlayer guildPlayer, String guildName) {
        Player bukkitPlayer = guildPlayer.getBukkitPlayer();

        guildManager.createGuild(guildPlayer, guildName);
        JulyMessage.broadcastColoredMessage(PlaceholderText.replacePlaceholders(thisLangSection.getString("success.broadcast"), new Placeholder.Builder().add("%PLAYER%", playerName).add("%GUILD%", guildName).build()));
        JulyMessage.sendTitle(bukkitPlayer, new TitleBuilder().text(thisLangSection.getString("success.self_title")).colored().build());

        new BukkitRunnable() {
            @Override
            public void run() {
                bukkitPlayer.performCommand("jguild main");
            }
        }.runTaskLater(plugin, 20L);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
