package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.gui.BaseGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatFilter;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GuildDonateGUI extends BaseGUI {
    private Inventory inventory;
    private GuildMember guildMember;
    private Permission permission;
    private GuildBank guildBank;
    private boolean pointsEnabled;
    private boolean moneyEnabled;

    public GuildDonateGUI(GuildPlayer guildPlayer) {
        super(GUIType.DONATE, guildPlayer);

        this.guildMember = guild.getMember(guildPlayer.getName());
        this.permission = guildMember.getPermission();
        this.guildBank = guild.getGuildBank();
        this.pointsEnabled = mainSettings.isDonatePointsEnabled();
        this.moneyEnabled = mainSettings.isDonateMoneyEnabled();
        build();
    }

    @Override
    public void build() {
        super.build();

        this.inventory = new InventoryBuilder().title("&e&l贡献").colored().row(3)
                .item(1, 2, new ItemBuilder()
                        .material(moneyEnabled ? Material.GOLD_INGOT : Material.BARRIER)
                        .displayName("&f贡献金币")
                        .addLore(moneyEnabled ? "&b>> &a点击贡献金币" : "&b>> &c未启用")
                        .colored().build(), new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        if (!moneyEnabled) {
                            return;
                        }

                        close();
                        Util.sendColoredMessage(bukkitPlayer, "&e请在聊天栏输入并发送要赞助的金币数量: ");
                        JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                            @Override
                            public void onChat(AsyncPlayerChatEvent event) {
                                JulyChatFilter.unregisterChatFilter(bukkitPlayer);
                                event.setCancelled(true);

                                int amount;

                                try {
                                    amount = Integer.parseInt(event.getMessage());
                                } catch (Exception e) {
                                    Util.sendColoredMessage(bukkitPlayer, "&c数量不正确!");
                                    return;
                                }

                                if (amount < mainSettings.getDonateMinMoney()) {
                                    Util.sendColoredMessage(bukkitPlayer, "&c最小金币赞助额为 &e" + mainSettings.getDonateMinMoney() + "&c.");
                                    return;
                                }

                                if (vault.getBalance(bukkitPlayer) < amount) {
                                    Util.sendColoredMessage(bukkitPlayer, "&c金币不足.");
                                    return;
                                }

                                vault.withdrawPlayer(bukkitPlayer, amount);
                                guildMember.addDonatedMoney(amount);
                                guildBank.deposit(GuildBank.BalanceType.MONEY, amount);
                                guild.broadcastMessage(permission.getColor() + "[" + permission.getChineseName() + "]&e" + guildPlayer.getName() + " &d为宗门赞助了 &e" + amount + "个 &d金币&d!");
                            }
                        });
                    }
                })
                .item(1, 6, new ItemBuilder()
                        .material(pointsEnabled ? Material.DIAMOND : Material.BARRIER)
                        .displayName("&f贡献点券")
                        .addLore(pointsEnabled ? "&b>> &a点击贡献点券" : "&b>> &c未启用")
                        .colored().build(), new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        if (!pointsEnabled) {
                            return;
                        }

                        close();
                        Util.sendColoredMessage(bukkitPlayer, "&e请在聊天栏输入并发送要赞助的点券数量: ");
                        JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                            @Override
                            public void onChat(AsyncPlayerChatEvent event) {
                                JulyChatFilter.unregisterChatFilter(bukkitPlayer);
                                event.setCancelled(true);

                                int amount;

                                try {
                                    amount = Integer.parseInt(event.getMessage());
                                } catch (Exception e) {
                                    Util.sendColoredMessage(bukkitPlayer, "&c数量不正确!");
                                    return;
                                }

                                if (amount < mainSettings.getDonateMinPoints()) {
                                    Util.sendColoredMessage(bukkitPlayer, "&c最小点券赞助额为 &e" + mainSettings.getDonateMinPoints() + "&c.");
                                    return;
                                }

                                UUID uuid = bukkitPlayer.getUniqueId();

                                if (playerPointsAPI.look(uuid) < amount) {
                                    Util.sendColoredMessage(bukkitPlayer, "&c点券不足.");
                                    return;
                                }

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        playerPointsAPI.take(uuid, amount);
                                        guildMember.addDonatedPoints(amount);
                                        guildBank.deposit(GuildBank.BalanceType.POINTS, amount);
                                        guild.broadcastMessage(permission.getColor() + "[" + permission.getChineseName() + "] &e" + guildPlayer.getName() + " &d为宗门赞助了 &e" + amount + "个 &d点券&d!");
                                    }
                                }.runTask(plugin);
                            }
                        });
                    }
                })
                .item(26, CommonItem.BACK, new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        new GuildMineGUI(guildPlayer).open();
                    }
                })
                .build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
