package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import org.bukkit.inventory.Inventory;

public class GuildDonateGUI extends BaseMemberGUI {
    private Inventory inventory;
    private GuildMember guildMember;
    private Permission permission;
    private GuildBank guildBank;
    private boolean pointsEnabled;
    private boolean moneyEnabled;

    public GuildDonateGUI(GuildMember guildMember) {
        super(GUIType.DONATE, guildMember);

        this.guildMember = guild.getMember(guildPlayer.getName());
        this.permission = guildMember.getPermission();
        this.guildBank = guild.getGuildBank();
        this.pointsEnabled = MainSettings.isDonatePointsEnabled();
        this.moneyEnabled = MainSettings.isDonateMoneyEnabled();
        build();
    }

    @Override
    public void build() {
        super.build();

//        this.inventory = new InventoryBuilder().title(ConfigHandler.getString("GuildDonateGUI.title")).colored().row(3)
//                .item(1, 2, new ItemBuilder()
//                        .material(moneyEnabled ? Material.GOLD_INGOT : Material.BARRIER)
//                        .displayName("&f贡献金币")
//                        .addLore(moneyEnabled ? "&b>> &a点击贡献金币" : "&b>> &c未启用")
//                        .colored().build(), new ItemListener() {
//                    @Override
//                    public void onClicked(InventoryClickEvent event) {
//                        if (!moneyEnabled) {
//                            return;
//                        }
//
//                        close();
//                        Util.sendColoredMessage(bukkitPlayer, "&e请在聊天栏输入并发送要赞助的金币数量: ");
//                        JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
//                            @Override
//                            public void onChat(AsyncPlayerChatEvent event) {
//                                JulyChatFilter.unregisterChatFilter(bukkitPlayer);
//                                event.setCancelled(true);
//
//                                int amount;
//
//                                try {
//                                    amount = Integer.parseInt(event.getMessage());
//                                } catch (Exception e) {
//                                    Util.sendColoredMessage(bukkitPlayer, "&c数量不正确!");
//                                    return;
//                                }
//
//                                if (amount < mainConfig.getDonateMinMoney()) {
//                                    Util.sendColoredMessage(bukkitPlayer, "&c最小金币赞助额为 &e" + mainConfig.getDonateMinMoney() + "&c.");
//                                    return;
//                                }
//
//                                if (vault.getBalance(bukkitPlayer) < amount) {
//                                    Util.sendColoredMessage(bukkitPlayer, "&c金币不足.");
//                                    return;
//                                }
//
//                                vault.withdrawPlayer(bukkitPlayer, amount);
//                                guildMember.addDonatedMoney(amount);
//                                guildBank.deposit(GuildBank.BalanceType.MONEY, amount);
//                                guild.broadcastMessage(ConfigHandler.getString("GuildDonateGUI.money.message", new String[][] {{"%AMOUNT%", String.valueOf(amount)}, {"%NICK_NAME%", ConfigHandler.getNickName(guildMember)}}));
//                            }
//                        });
//                    }
//                })
//                .item(1, 6, new ItemBuilder()
//                        .material(pointsEnabled ? Material.DIAMOND : Material.BARRIER)
//                        .displayName(ConfigHandler.getString("GuildDonateGUI.points.icon.display_name"))
//                        .addLore(ConfigHandler.getString(pointsEnabled ? "GuildDonateGUI.points.lore.on" : "GuildDonateGUI.points.lore.off"))
//                        .colored().build(), new ItemListener() {
//                    @Override
//                    public void onClicked(InventoryClickEvent event) {
//                        if (!pointsEnabled) {
//                            return;
//                        }
//
//                        close();
//                        Util.sendColoredMessage(bukkitPlayer, "&e请在聊天栏输入并发送要赞助的点券数量: ");
//                        JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
//                            @Override
//                            public void onChat(AsyncPlayerChatEvent event) {
//                                JulyChatFilter.unregisterChatFilter(bukkitPlayer);
//                                event.setCancelled(true);
//
//                                int amount;
//
//                                try {
//                                    amount = Integer.parseInt(event.getMessage());
//                                } catch (Exception e) {
//                                    Util.sendColoredMessage(bukkitPlayer, "&c数量不正确!");
//                                    return;
//                                }
//
//                                if (amount < mainConfig.getDonateMinPoints()) {
//                                    Util.sendColoredMessage(bukkitPlayer, "&c最小点券赞助额为 &e" + mainConfig.getDonateMinPoints() + "&c.");
//                                    return;
//                                }
//
//                                UUID uuid = bukkitPlayer.getUniqueId();
//
//                                if (playerPointsAPI.look(uuid) < amount) {
//                                    Util.sendColoredMessage(bukkitPlayer, "&c点券不足.");
//                                    return;
//                                }
//
//                                new BukkitRunnable() {
//                                    @Override
//                                    public void run() {
//                                        playerPointsAPI.take(uuid, amount);
//                                        guildMember.addDonatedPoints(amount);
//                                        guildBank.deposit(GuildBank.BalanceType.POINTS, amount);
//                                        guild.broadcastMessage(ConfigHandler.getString("GuildDonateGUI.points.message", new String[][] {{"%AMOUNT%", String.valueOf(amount)}, {"%NICK_NAME%", ConfigHandler.getNickName(guildMember)}}));
//                                    }
//                                }.runTask(plugin);
//                            }
//                        });
//                    }
//                })
//                .item(26, CommonItem.BACK, new ItemListener() {
//                    @Override
//                    public void onClicked(InventoryClickEvent event) {
//                        close();
//                        new GuildMinePlayerGUI(guildPlayer).open();
//                    }
//                })
//                .build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
