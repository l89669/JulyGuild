package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.exception.GuildPromoteException;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

public class GuildPromoteGUI extends BaseGUI {
    private Inventory inventory;
    private Guild guild;
    private GuildBank guildBank;
    private boolean moneyEnabled;
    private boolean pointsEnabled;
    private final JulyGuild plugin = JulyGuild.getInstance();

    public GuildPromoteGUI(GuildPlayer guildPlayer) {
        super(GUIType.PROMOTE, guildPlayer);

        this.guild = this.guildPlayer.getGuild();
        this.guildBank = guild.getGuildBank();
        this.moneyEnabled = MainSettings.isPromoteMoneyEnabled();
        this.pointsEnabled = MainSettings.isPromotePointsEnabled();
        build();
    }

    @Override
    public void build() {
        InventoryBuilder inventoryBuilder = new InventoryBuilder()
                .row(3)
                .title("&e&l宗门升级")
                .colored()
                .item(26, CommonItem.BACK, new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        new GuildManageGUI(guildPlayer).open();
                    }
                })
                .listener(new InventoryListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {

                    }
                });


        int currentMaxMemberCount = guild.getMaxMemberCount();

        if (currentMaxMemberCount + 1 > MainSettings.getPromoteMoneyMaxMemberCount()) {
            inventoryBuilder.item(1, 3, new ItemBuilder().material(Material.BARRIER).displayName("&f使用金币升级").addLore("&c已封顶").colored().build());
        } else {
            int needMoney;

            try {
                needMoney = (int) Parser.parse(PlaceholderAPI.setPlaceholders(bukkitPlayer, MainSettings.getPromoteMoneyFormula())).evaluate();
            } catch (ParseException e) {
                e.printStackTrace();
                throw new GuildPromoteException("升级公式不合法");
            }

            inventoryBuilder
                    .item(1, 3, new ItemBuilder()
                            .material(moneyEnabled ? Material.GOLD_INGOT : Material.BARRIER)
                            .displayName("&a使用金币升级")
                            .addLores(moneyEnabled ? new String[] {"&a>> &e点击升级", "", "&b花费&f: &b" + needMoney, "&d人数&f: &d" + currentMaxMemberCount + "->" + (currentMaxMemberCount + 1)} : new String[] {"&a>> &c未启用"})
                            .colored()
                            .build(), !moneyEnabled ? null : new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            close();

                            if (!guildBank.has(GuildBank.BalanceType.MONEY, needMoney)) {
                                Util.sendColoredMessage(bukkitPlayer, "&c金币不足.");
                                return;
                            }

                            guildBank.withdraw(GuildBank.BalanceType.MONEY, needMoney);
                            guild.setMaxMemberCount(guild.getMaxMemberCount() + 1);
                            Util.sendColoredMessage(bukkitPlayer, "&d升级成功, 宗门目前可最多容纳: &e" + guild.getMaxMemberCount() + "人&d.");

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    build();
                                    open();
                                }
                            }.runTaskLater(plugin, 20L);
                        }
                    });
        }

        if (currentMaxMemberCount + 1 > MainSettings.getPromotePointMaxMemberCount()) {
            inventoryBuilder.item(1, 5, new ItemBuilder().material(Material.BARRIER).displayName("&f使用点券升级").addLore("&c已封顶").colored().build());
        } else {
            int needPoints;

            try {
                needPoints = (int) Parser.parse(PlaceholderAPI.setPlaceholders(bukkitPlayer, MainSettings.getPromotePointFormula())).evaluate();
            } catch (ParseException e) {
                e.printStackTrace();
                throw new GuildPromoteException("升级公式不合法");
            }

            inventoryBuilder
                    .item(1, 5, new ItemBuilder()
                            .material(pointsEnabled ? Material.DIAMOND : Material.BARRIER)
                            .displayName("&a使用点券升级")
                            .addLores(pointsEnabled ? new String[] {"&a>> &e点击升级", "", "&b花费&f: &b" + needPoints, "&d人数&f: &d" + currentMaxMemberCount + "->" + (currentMaxMemberCount + 1)} : new String[] {"&a>> &c未启用"})
                            .colored()
                            .build(), !pointsEnabled ? null : new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            if (!guildBank.has(GuildBank.BalanceType.POINTS, needPoints)) {
                                Util.sendColoredMessage(bukkitPlayer, "&c点券不足.");
                                return;
                            }

                            guildBank.withdraw(GuildBank.BalanceType.POINTS, needPoints);
                            guild.setMaxMemberCount(guild.getMaxMemberCount() + 1);
                            Util.sendColoredMessage(bukkitPlayer, "&d升级成功, 宗门目前可最多容纳: &e" + guild.getMaxMemberCount() + "人&d.");
                            close();

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    build();
                                    open();
                                }
                            }.runTaskLater(plugin, 20L);
                        }
                    });
        }

        this.inventory = inventoryBuilder.build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
