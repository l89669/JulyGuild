package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.MainConfig;
import com.github.julyss2019.mcsp.julyguild.gui.BaseGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.listener.TpAllListener;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.player.TpRequest;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.TitleBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuildTpAllBuyGUI extends BaseGUI {
    private static JulyGuild plugin = JulyGuild.getInstance();

    private Inventory inventory;
    private Guild guild;
    private GuildBank guildBank;

    public GuildTpAllBuyGUI(GuildPlayer guildPlayer) {
        super(GUIType.TP_ALL_BUY, guildPlayer);

        this.guild = guildPlayer.getGuild();
        this.guildBank = guild.getGuildBank();
        build();
    }

    @Override
    public void build() {
   /*     this.inventory = new InventoryBuilder().title("&e&l请选择支付方式").row(3).colored()
                .item(26, CommonItem.BACK, new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        new GuildManageGUI(guildPlayer).open();
                    }
                })
                .item(1, 4, new ItemBuilder()
                        .material(Material.GOLD_INGOT)
                        .displayName("&f使用 &a金币x" + mainConfig.getTpAllCostMoney() + " &f支付")
                        .addLore("")
                        .addLore("&b• &d点击支付&b •")
                        .addLore("")
                        .colored()
                        .build(), new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();

                        if (!mainConfig.getTpAllAllowedSendWorlds().contains("*") && mainConfig.getTpAllAllowedSendWorlds().contains(bukkitPlayer.getWorld().getName())) {
                            Util.sendColoredMessage(bukkitPlayer, "&c当前世界不能发起全员集结令.");
                            return;
                        }

                        if (!guildBank.has(GuildBank.BalanceType.MONEY, mainConfig.getTpAllCostMoney())) {
                            Util.sendColoredMessage(bukkitPlayer, "&c宗门银行金币不足.");
                            return;
                        }

                        int validCounter = 0;
                        int offlineCounter = 0;
                        int diffWorldCounter = 0;

                        for (GuildMember member : guild.getMembers()) {
                            if (member.getName().equals(guildPlayer.getName())) {
                                continue;
                            }

                            if (member.isOnline()) {
                                if (!mainConfig.getTpAllAllowedReceiveWorlds().contains("*") && !mainConfig.getTpAllAllowedReceiveWorlds().contains(member.getBukkitPlayer().getWorld().getName())) {
                                    diffWorldCounter++;
                                    continue;
                                }

                                member.getGuildPlayer().getGuildPlayer().addRequest(TpRequest.createNew(guildPlayer, bukkitPlayer.getLocation()));
                                TpAllListener.resetPlayer(member.getName()); // 重置
                                JulyMessage.sendTitle(member.getBukkitPlayer(), new TitleBuilder().text("&b全员集结令").colored().build());
                                Util.sendColoredMessage(member.getBukkitPlayer(), "&e宗主 &c" + bukkitPlayer.getName() + " &e请求你传送到TA那, 如果要传送请在 &c" + mainConfig.getTpAllShiftTimeout() + "秒内 &e快速按 &c" + mainConfig.getTpAllShiftCount() + "次 &eShift键!");
                                validCounter++;
                            } else {
                                offlineCounter++;
                            }
                        }

                        if (validCounter == 0) {
                            Util.sendColoredMessage(bukkitPlayer, "&c队员均离线或暂时无法传送!");
                            return;
                        }

                        guildBank.withdraw(GuildBank.BalanceType.MONEY, mainConfig.getTpAllCostMoney());

                        Util.sendColoredMessage(bukkitPlayer, "&e成功向 &c" + validCounter + "个 &e成员发送了全员集结令, 请等待确认!");

                        if (offlineCounter != 0) {
                            Util.sendColoredMessage(bukkitPlayer, "&c离线的玩家有 &e" + offlineCounter + "个&c.");
                        }

                        if (diffWorldCounter != 0) {
                            Util.sendColoredMessage(bukkitPlayer, "&c暂时无法集结的玩家有 &e" + diffWorldCounter + "个&c.");
                        }
                    }
                }).build();*/
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
