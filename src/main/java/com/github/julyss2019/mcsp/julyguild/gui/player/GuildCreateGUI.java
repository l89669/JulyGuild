package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.PlayerPointsEconomy;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.VaultEconomy;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.Title;
import com.github.julyss2019.mcsp.julylibrary.utils.ItemUtil;
import com.github.julyss2019.mcsp.julylibrary.utils.PlayerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 公会创建GUI
 * @version 1.0.0
 */

public class GuildCreateGUI extends BasePlayerGUI {
    private final String guildName;
    private boolean noAction = true;

    private final Player bukkitPlayer;
    private final String playerName;
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("GuildCreateGUI");
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildCreateGUI");
    private final VaultEconomy vaultEconomy = plugin.getVaultEconomy();
    private final PlayerPointsEconomy playerPointsEconomy = plugin.getPlayerPointsEconomy();
    private final GuildManager guildManager = plugin.getGuildManager();

    public GuildCreateGUI(GuildPlayer guildPlayer, String guildName) {
        super(GUIType.CREATE, guildPlayer);

        this.bukkitPlayer = getBukkitPlayer();
        this.playerName = bukkitPlayer.getName();
        this.guildName = guildName;
    }

    @Override
    public Inventory getGUI() {
        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer)
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
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.money"), bukkitPlayer,
                new Placeholder.Builder()
                        .addInner("amount", String.valueOf(MainSettings.getCreateCostMoneyAmount()))
                        .addInner("name", guildName).build()), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                noAction = false;
                close();

                if (guildPlayer.isInGuild()) {
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("already_in"));
                    return;
                }

                if (vaultEconomy.has(bukkitPlayer, MainSettings.getCreateCostMoneyAmount())) {
                    Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("money.not_enough"), new Placeholder.Builder()
                            .addInner("need", vaultEconomy.getBalance(bukkitPlayer) - MainSettings.getCreateCostMoneyAmount()).build()));
                    return;
                }

                vaultEconomy.withdraw(bukkitPlayer, MainSettings.getCreateCostMoneyAmount());
                createGuild(guildPlayer, guildName);
            }
        });

        // 点券
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.points"), bukkitPlayer,
                new Placeholder.Builder()
                        .addInner("amount", String.valueOf(MainSettings.getCreateCostPointsAmount()))
                        .addInner("name", guildName).build()), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                noAction = false;
                close();

                if (guildPlayer.isInGuild()) {
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("already_in"));
                    return;
                }

                int playerPoints = playerPointsEconomy.getBalance(bukkitPlayer);

                if (playerPoints < MainSettings.getCreateCostPointsAmount()) {
                    Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("points.not_enough"), new Placeholder.Builder()
                            .addInner("need", String.valueOf(MainSettings.getCreateCostPointsAmount() - playerPoints)).build()));
                    return;
                }

                playerPointsEconomy.withdraw(bukkitPlayer, MainSettings.getCreateCostPointsAmount());
                createGuild(guildPlayer, guildName);
            }
        });

        // 建帮令
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.item"), bukkitPlayer,
                new Placeholder.Builder().addInner("AMOUNT", String.valueOf(MainSettings.getCreateCostItemAmount()))
                        .addInner("name", guildName)
                        .build()), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                noAction = false;
                close();

                if (guildPlayer.isInGuild()) {
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("already_in"));
                    return;
                }

                int hadItemAmount = PlayerUtil.getItemAmount(bukkitPlayer, itemStack -> ItemUtil.containsLore(itemStack, MainSettings.getCreateCostItemKeyLore()));

                if (hadItemAmount < MainSettings.getCreateCostItemAmount()) {
                    PlayerUtil.takeItems(bukkitPlayer, itemStack -> ItemUtil.containsLore(itemStack, MainSettings.getCreateCostItemKeyLore()), MainSettings.getCreateCostItemAmount());
                    createGuild(guildPlayer, guildName);
                } else {
                    Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("item.not_enough"), new Placeholder.Builder()
                            .addInner("need", String.valueOf(hadItemAmount - MainSettings.getCreateCostItemAmount())).build()));
                }
            }
        });

        return guiBuilder.build();
    }

    private void createGuild(GuildPlayer guildPlayer, String guildName) {
        Player bukkitPlayer = guildPlayer.getBukkitPlayer();

        guildManager.createGuild(guildPlayer, guildName);
        JulyMessage.broadcastColoredMessage(PlaceholderText.replacePlaceholders(thisLangSection.getString("success.broadcast"), new Placeholder.Builder().addInner("PLAYER", playerName).addInner("GUILD", guildName).build()));
        JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().text(thisLangSection.getString("success.self_title")).colored().build());

        new BukkitRunnable() {
            @Override
            public void run() {
                new MainGUI(guildPlayer).open();
            }
        }.runTaskLater(plugin, 20L * 3L);
    }
}
