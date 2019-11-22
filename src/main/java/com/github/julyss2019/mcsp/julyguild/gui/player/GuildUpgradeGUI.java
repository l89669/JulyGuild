package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.math.BigDecimal;

public class GuildUpgradeGUI extends BaseMemberGUI {
    private final Player bukkitPlayer;
    private final Guild guild;
    private final GuildBank guildBank;
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildUpgradeGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("GuildUpgradeGUI");

    public GuildUpgradeGUI(GuildMember guildMember) {
        super(GUIType.PROMOTE, guildMember);

        this.bukkitPlayer = getBukkitPlayer();
        this.guild = getGuild();
        this.guildBank = guild.getGuildBank();
    }

    @Override
    public Inventory getGUI() {
        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer)
                .colored();

        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                new GuildMineGUI(guildMember).open();
            }
        });

        int oldMaxMemberCount = guild.getMaxMemberCount(); // 当前最大成员数

        // 金币升级封顶
        if (oldMaxMemberCount + 1 > MainSettings.getUpgradeMoneyMaxMemberCount()) {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.money.full"), bukkitPlayer));
        } else {
            int needMoney;

            try {
                needMoney = (int) Parser.parse(PlaceholderAPI.setPlaceholders(bukkitPlayer, MainSettings.getUpgradeMoneyFormula())).evaluate();
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("公会升级公式不合法");
            }

            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.money.available"), bukkitPlayer, new Placeholder.Builder()
                    .addInner("cost", needMoney)
                    .addInner("old", oldMaxMemberCount)
                    .addInner("new", oldMaxMemberCount + 1).build()), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();

                    if (!guildBank.has(GuildBank.BalanceType.MONEY, needMoney)) {
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.not_enough"), new Placeholder.Builder()
                                .addInner("need", String.valueOf(new BigDecimal(needMoney).subtract(guildBank.getBalance(GuildBank.BalanceType.MONEY)).toString())).build());
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                reopen();
                            }
                        }.runTaskLater(plugin, 20L);
                        return;
                    }

                    guildBank.withdraw(GuildBank.BalanceType.MONEY, needMoney);
                    guild.setMaxMemberCount(guild.getMaxMemberCount() + 1);
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.success"), new Placeholder.Builder()
                            .addInner("old", String.valueOf(oldMaxMemberCount))
                            .addInner("new", String.valueOf(oldMaxMemberCount + 1)).build());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            reopen();
                        }
                    }.runTaskLater(plugin, 20L);
                }
            });
        }

        // 点券升级封顶
        if (oldMaxMemberCount + 1 > MainSettings.getUpgradePointsMaxMemberCount()) {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.points.full"), bukkitPlayer));
        } else {
            int needPoints;

            try {
                needPoints = (int) Parser.parse(PlaceholderAPI.setPlaceholders(bukkitPlayer, MainSettings.getUpgradePointFormula())).evaluate();
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("公会升级公式不合法");
            }

            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.points.available"), bukkitPlayer, new Placeholder.Builder()
                    .addInner("cost", needPoints)
                    .addInner("old", oldMaxMemberCount)
                    .addInner("new", oldMaxMemberCount + 1).build()), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();

                    if (!guildBank.has(GuildBank.BalanceType.POINTS, needPoints)) {
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.not_enough"), new Placeholder.Builder()
                                .addInner("need", new BigDecimal(needPoints).subtract(guildBank.getBalance(GuildBank.BalanceType.POINTS)).toString()).build());
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                reopen();
                            }
                        }.runTaskLater(plugin, 20L);
                        return;
                    }

                    guildBank.withdraw(GuildBank.BalanceType.POINTS, needPoints);
                    guild.setMaxMemberCount(guild.getMaxMemberCount() + 1);
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.success"), new Placeholder.Builder()
                            .addInner("old", String.valueOf(oldMaxMemberCount))
                            .addInner("new", String.valueOf(oldMaxMemberCount + 1)).build());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            reopen();
                        }
                    }.runTaskLater(plugin, 20L);
                }
            });
        }


        return guiBuilder.build();
    }
}
