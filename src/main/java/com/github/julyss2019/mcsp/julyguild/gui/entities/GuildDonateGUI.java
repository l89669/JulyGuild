package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.BaseConfirmGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.PlayerPointsEconomy;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.VaultEconomy;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class GuildDonateGUI extends BasePlayerGUI {
    private enum PayType {
        MONEY, POINTS
    }

    private JulyGuild plugin = JulyGuild.getInstance();
    private final GuildMember guildMember;
    private final Guild guild;
    private final Player bukkitPlayer = getBukkitPlayer();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildDonateGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildDonateGUI");
    private final PlayerPointsEconomy playerPointsEconomy = plugin.getPlayerPointsEconomy();
    private final VaultEconomy vaultEconomy = plugin.getVaultEconomy();

    protected GuildDonateGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        super(lastGUI, GUIType.DONATE, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.guild = guildMember.getGuild();
    }


    @Override
    public boolean canUse() {
        return guildMember.isValid();
    }

    @Override
    public Inventory createInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer);

        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (canBack()) {
                    back();
                }
            }
        });

        if (thisGUISection.contains("donate_items")) {
            for (String itemName : thisGUISection.getConfigurationSection("donate_items").getKeys(false)) {
                ConfigurationSection itemSection = thisGUISection.getConfigurationSection("donate_items").getConfigurationSection(itemName);
                ConfigurationSection donateItemSection = itemSection.getConfigurationSection("donate");

                PayType payType = PayType.valueOf(donateItemSection.getString("pay_type"));
                int cost = donateItemSection.getInt("cost");
                int reward = donateItemSection.getInt("reward");
                ConfigurationSection confirmGUISection = donateItemSection.getConfigurationSection("ConfirmGUI");

                guiBuilder.item(GUIItemManager.getIndexItem(itemSection, bukkitPlayer, new PlaceholderContainer().add("cost", cost)
                        .add("cost", cost)
                        .add("reward", reward)), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (payType == PayType.POINTS) {
                            if (!playerPointsEconomy.has(bukkitPlayer, cost)) {
                                Util.sendMsg(bukkitPlayer, thisLangSection.getString("points.not_enough"), new PlaceholderContainer()
                                        .add("need", cost - playerPointsEconomy.getBalance(bukkitPlayer)));
                                reopen(40);
                                return;
                            }

                            new BaseConfirmGUI(GuildDonateGUI.this, guildPlayer, confirmGUISection, new PlaceholderContainer()
                                    .add("reward", reward)
                                    .add("cost", cost)) {
                                @Override
                                public boolean canUse() {
                                    return guildMember.isValid() && playerPointsEconomy.has(bukkitPlayer, cost);
                                }

                                @Override
                                public void onConfirm() {
                                    playerPointsEconomy.withdraw(bukkitPlayer, cost);
                                    guild.getGuildBank().deposit(GuildBank.BalanceType.GMONEY, new BigDecimal(reward));
                                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("points.success"), new PlaceholderContainer()
                                            .add("reward", reward)
                                            .add("cost", cost));
                                    close();
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            getLastGUI().open();
                                        }
                                    }.runTaskLater(plugin, 40L);
                                }

                                @Override
                                public void onCancel() {
                                    if (canBack()) {
                                        back();
                                    }
                                }
                            }.open();
                        }

                        if (payType == PayType.MONEY) {
                            if (!vaultEconomy.has(bukkitPlayer, cost)) {
                                Util.sendMsg(bukkitPlayer, thisLangSection.getString("money.not_enough"), new PlaceholderContainer()
                                        .add("need", cost - vaultEconomy.getBalance(bukkitPlayer)));
                                reopen(40);
                                return;
                            }

                            new BaseConfirmGUI(GuildDonateGUI.this, guildPlayer, confirmGUISection, new PlaceholderContainer()
                                    .add("reward", reward)
                                    .add("cost", cost)) {
                                @Override
                                public boolean canUse() {
                                    return guildMember.isValid() && vaultEconomy.has(bukkitPlayer, cost);
                                }

                                @Override
                                public void onConfirm() {
                                    vaultEconomy.withdraw(bukkitPlayer, cost);
                                    guild.getGuildBank().deposit(GuildBank.BalanceType.GMONEY, new BigDecimal(reward));
                                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("money.success"), new PlaceholderContainer()
                                            .add("reward", reward)
                                            .add("cost", cost));
                                    close();
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            getLastGUI().open();
                                        }
                                    }.runTaskLater(plugin, 40L);
                                }

                                @Override
                                public void onCancel() {
                                    if (canBack()) {
                                        back();
                                    }
                                }
                            }.open();
                        }
                    }
                });
            }
        }

        return guiBuilder.build();
    }
}
