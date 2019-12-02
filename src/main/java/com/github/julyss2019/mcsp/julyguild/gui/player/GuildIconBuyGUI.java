package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.config.ConfigGuildIcon;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
// TODO: update
/**
 * 公会图标购买GUI
 */
public class GuildIconBuyGUI extends BaseMemberGUI {
    private ConfigGuildIcon configGuildIcon;
    private Inventory inventory;
    private Guild guild;
    private GuildBank guildBank;
    private Player bukkitPlayer;
    private boolean pointsPayEnabled;
    private boolean moneyPayEnabled;

    public GuildIconBuyGUI(GuildMember guildMember, ConfigGuildIcon configGuildIcon) {
        super(GUIType.ICON_SHOP_BUY, guildMember, null);

        this.guild = guildPlayer.getGuild();
        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
        this.configGuildIcon = configGuildIcon;
        this.guildBank = guild.getGuildBank();
        this.moneyPayEnabled = configGuildIcon.isMoneyPayEnabled(); // 图标是否开启了金币购买
        this.pointsPayEnabled = configGuildIcon.isPointsPayEnabled(); // 图标是否开启了金币购买
        build();
    }


    public void build() {


        this.inventory = new InventoryBuilder()
                .title("&e&l请选择支付方式")
                .row(3)
                .colored()
                .item(26, CommonItem.BACK, new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        new GuildIconShopGUI(guildPlayer).open();
                    }
                })

                .item(1, 2, new ItemBuilder()
                        .material(moneyPayEnabled ? Material.GOLD_INGOT : Material.BARRIER)
                        .displayName("&f使用 &e金币x" + configGuildIcon.getMoneyCost() + " &f支付")
                        .addLore(moneyPayEnabled ? "&b>> &a点击支付" : "&b>> &c未启用")
                        .addLore("")
                        .colored()
                        .build(), !moneyPayEnabled ? null : new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        int fee = configGuildIcon.getMoneyCost();

                        if (!guildBank.has(GuildBank.BalanceType.MONEY, fee)) {
                            Util.sendColoredMessage(bukkitPlayer, "&c宗门银行金币不足.");
                            return;
                        }

                        guildBank.withdraw(GuildBank.BalanceType.MONEY, fee);
                        Util.sendColoredMessage(bukkitPlayer, "&d购买成功!");
                        guild.setCurrentIcon(guild.giveIcon(configGuildIcon.getMaterial(), configGuildIcon.getDurability()));
                        close();
                    }
                })
                .item(1, 6, new ItemBuilder()
                        .material(pointsPayEnabled ? Material.DIAMOND : Material.BARRIER)
                        .displayName("&f使用 &e点券x" + configGuildIcon.getPointsCost() + " &f支付")
                        .addLore(pointsPayEnabled ? "&b>> &a点击支付" : "&b>> &c未启用")
                        .addLore("")
                        .colored()
                        .build(), !pointsPayEnabled ? null : new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        int fee = configGuildIcon.getPointsCost();

                        if (!guildBank.has(GuildBank.BalanceType.POINTS, fee)) {
                            Util.sendColoredMessage(bukkitPlayer, "&c宗门银行点券不足.");
                            return;
                        }

                        guildBank.withdraw(GuildBank.BalanceType.POINTS, fee);
                        Util.sendColoredMessage(bukkitPlayer, "&d购买成功!");
                        guild.setCurrentIcon(guild.giveIcon(configGuildIcon.getMaterial(), configGuildIcon.getDurability()));
                        close();
                    }
                }).build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
