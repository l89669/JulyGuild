package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.config.ConfigGuildShopItem;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import org.bukkit.inventory.Inventory;

public class GuildShopItemBuyGUI extends BaseMemberGUI {
    private Inventory inventory;
    private ConfigGuildShopItem shopItem;
    private GuildBank guildBank;

    private boolean moneyEnabled;
    private boolean pointsEnabled;

    public GuildShopItemBuyGUI(GuildMember guildMember, ConfigGuildShopItem shopItem) {
        super(GUIType.SHOP_BUY, guildMember, null);

        this.guildBank = guild.getGuildBank();
        this.shopItem = shopItem;
        this.moneyEnabled = shopItem.isMoneyEnabled();
        this.pointsEnabled = shopItem.isPointsEnabled();
        build();
    }


    public void build() {
//        super.build();
//
//        int memberCount = shopItem.getTarget() == ConfigGuildShopItem.Target.ONLINE_MEMBERS ? guild.getOnlineMemberCount() : guild.getMemberCount();
//        int moneyCost = 0;
//        int pointsCost = 0;
//
//        if (moneyEnabled) {
//            try {
//                moneyCost = (int) Parser.parse(shopItem.getMoneyFormula().replace("%MEMBER_COUNT%", String.valueOf(memberCount))).evaluate();
//            } catch (ParseException e) {
//                e.printStackTrace();
//                throw new RuntimeException("金币公式不合法");
//            }
//        }
//
//        if (pointsEnabled) {
//            try {
//                pointsCost = (int) Parser.parse(shopItem.getPointsFormula().replace("%MEMBER_COUNT%", String.valueOf(memberCount))).evaluate();
//            } catch (ParseException e) {
//                e.printStackTrace();
//                throw new RuntimeException("点券公式不合法");
//            }
//        }
//
//        int finalMoneyCost = moneyCost;
//        int finalPointsCost = pointsCost;
//        this.inventory = new InventoryBuilder()
//                .title("&e&l购买 " + shopItem.getDisplayName())
//                .colored()
//                .row(3)
//                .item(1, 2, new ItemBuilder()
//                        .material(moneyEnabled ? Material.GOLD_INGOT : Material.BARRIER)
//                        .displayName("&e使用金币购买")
//                        .addLores(moneyEnabled ? new String[] {"&b>> &a点击购买", "&a价格: " + moneyCost} : new String[] {"&b>> &c未启用"})
//                        .colored().build(), !moneyEnabled ? null : new ItemListener() {
//                    @Override
//                    public void onClicked(InventoryClickEvent event) {
//                        close();
//
//                        if (!guildBank.has(GuildBank.BalanceType.MONEY, finalMoneyCost)) {
//                            Util.sendColoredMessage(guildMember, "&c宗门银行金币不足.");
//                            return;
//                        }
//
//                        for (String command : shopItem.getRewardCommands()) {
//                            (shopItem.getTarget() == ConfigGuildShopItem.Target.ONLINE_MEMBERS ? guild.getOnlineMembers() : guild.getMembers()).forEach(guildMember -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%MEMBER%", guildMember.getName())));
//                        }
//
//                        guildBank.withdraw(GuildBank.BalanceType.MONEY, finalMoneyCost);
//                        Util.sendColoredMessage(bukkitPlayer, shopItem.getMessage()
//                                .replace("%COST_AMOUNT%", String.valueOf(finalMoneyCost))
//                                .replace("%COST_TYPE%", "金币")
//                                .replace("%MEMBER_COUNT%", String.valueOf(memberCount))
//                        );
//                    }
//                })
//                .item(1, 6, new ItemBuilder()
//                        .material(pointsEnabled ? Material.DIAMOND : Material.BARRIER)
//                        .displayName("&e使用点券购买")
//                        .addLores(pointsEnabled ? new String[] {"&b>> &a点击购买", "&a价格: " + pointsCost} : new String[] {"&b>> &c未启用"})
//                        .colored().build(), !pointsEnabled ? null : new ItemListener() {
//                    @Override
//                    public void onClicked(InventoryClickEvent event) {
//                        close();
//
//                        if (!guildBank.has(GuildBank.BalanceType.MONEY, finalPointsCost)) {
//                            Util.sendColoredMessage(bukkitPlayer, "&c宗门银行点券不足.");
//                            return;
//                        }
//
//                        for (String command : shopItem.getRewardCommands()) {
//                            (shopItem.getTarget() == ConfigGuildShopItem.Target.ONLINE_MEMBERS ? guild.getOnlineMembers() : guild.getMembers()).forEach(guildMember -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%MEMBER%", guildMember.getName())));
//                        }
//
//                        guildBank.withdraw(GuildBank.BalanceType.POINTS, finalPointsCost);
//                        Util.sendColoredMessage(bukkitPlayer, shopItem.getMessage()
//                                .replace("%COST_AMOUNT%", String.valueOf(finalPointsCost))
//                                .replace("%COST_TYPE%", "点券")
//                                .replace("%MEMBER_COUNT%", String.valueOf(memberCount))
//                        );
//                    }
//                })
//                .item(26, CommonItem.BACK, new ItemListener() {
//                    @Override
//                    public void onClicked(InventoryClickEvent event) {
//                        close();
//                        new GuildShopGUI(g).open();
//                    }
//                })
//                .build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
