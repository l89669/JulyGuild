package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.PlayerPointsEconomy;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.VaultEconomy;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatInterceptor;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

import static com.github.julyss2019.mcsp.julyguild.guild.GuildBank.BalanceType.MONEY;
import static com.github.julyss2019.mcsp.julyguild.guild.GuildBank.BalanceType.POINTS;

public class GuildDonateGUI extends BaseMemberGUI {
    public static class ConfirmGUI extends BaseMemberGUI {
        private final JulyGuild plugin = JulyGuild.getInstance();
        private final VaultEconomy vaultEconomy = plugin.getVaultEconomy();
        private final PlayerPointsEconomy playerPointsEconomy = plugin.getPlayerPointsEconomy();
        private final Player bukkitPlayer = getBukkitPlayer();
        private final ConfigurationSection parentLangSection = plugin.getLangYamlConfig().getConfigurationSection("GuildDonateGUI");
        private final ConfigurationSection thisGUISection;
        private final GuildBank.BalanceType donateType;
        private final int donateAmount;

        public ConfirmGUI(GuildMember guildMember, GuildBank.BalanceType donateType, int donateAmount) {
            super(GUIType.DONATE_CONFIRM, guildMember);

            this.donateType = donateType;
            this.donateAmount = donateAmount;
            this.thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildDonateGUI").getConfigurationSection("ConfirmGUI").getConfigurationSection(donateType.name().toLowerCase());
        }

        @Override
        public Inventory getInventory() {
            double fee = donateAmount * (donateType == MONEY ? MainSettings.getDonateMoneyFee() : MainSettings.getDonatePointsFee());
            double exactlyDonateAmount = donateAmount - fee;

            IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                    .fromConfig(thisGUISection, bukkitPlayer);

            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.confirm"), guildMember, new Placeholder.Builder()
                    .addInner("donate_amount", donateAmount)
                    .addInner("fee", fee)
                    .addInner("exactly_donate_amount", Util.SIMPLE_DECIMAL_FORMAT.format(exactlyDonateAmount))), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();

                    switch (donateType) {
                        case MONEY:
                            if (!vaultEconomy.has(bukkitPlayer, donateAmount)) {
                                Util.sendColoredMessage(bukkitPlayer, parentLangSection.getString("money.not_enough"));
                                return;
                            }

                            vaultEconomy.withdraw(bukkitPlayer, donateAmount);
                            break;
                        case POINTS:
                            if (!playerPointsEconomy.has(bukkitPlayer, donateAmount)) {
                                Util.sendColoredMessage(bukkitPlayer, parentLangSection.getString("points.not_enough"));
                                return;
                            }

                            playerPointsEconomy.withdraw(bukkitPlayer, donateAmount);
                            break;
                            default:
                                throw new UnsupportedOperationException("无效的类型");
                    }

                    guild.getGuildBank().deposit(donateType, exactlyDonateAmount); // 添加到公会银行
                    guildMember.addDonated(donateType, exactlyDonateAmount); // 为个人赞助额添加
                    Util.sendColoredMessage(bukkitPlayer, parentLangSection.getConfigurationSection(donateType.name().toLowerCase()).getString("success"), new Placeholder.Builder()
                            .addInner("exactly_donate_amount", Util.SIMPLE_DECIMAL_FORMAT.format(exactlyDonateAmount))
                            .addInner("donate_amount", donateAmount)
                            .addInner("fee", Util.SIMPLE_DECIMAL_FORMAT.format(fee)).build());
                }
            });

            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    new GuildDonateGUI(guildMember).open();
                }
            });

            return guiBuilder.build();
        }
    }

    private final JulyGuild plugin = JulyGuild.getInstance();
    private final Player bukkitPlayer = getBukkitPlayer();
    private final VaultEconomy vaultEconomy = plugin.getVaultEconomy();
    private final PlayerPointsEconomy playerPointsEconomy = plugin.getPlayerPointsEconomy();
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("GuildDonateGUI");
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildDonateGUI");

    public GuildDonateGUI(GuildMember guildMember) {
        super(GUIType.DONATE, guildMember);
    }

    @Override
    public Inventory getInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer);

        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.money"), bukkitPlayer, new Placeholder.Builder().addInner("fee", (int) (MainSettings.getDonateMoneyFee() * 100)).build()), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                close();
                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.input.message"), new Placeholder.Builder()
                        .addInner("cancel_string", MainSettings.getDonateInputCancelString()).build());

                new ChatInterceptor.Builder()
                        .player(bukkitPlayer)
                        .plugin(plugin)
                        .timeout(MainSettings.getDonateInputWaitSecond()).onlyFirst(true).chatListener(new ChatListener() {
                    @Override
                    public void onChat(AsyncPlayerChatEvent event) {
                        String message = event.getMessage();

                        // 捐赠取消字符串
                        if (message.equalsIgnoreCase(MainSettings.getDonateInputCancelString())) {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.input.cancelled"));
                            return;
                        }

                        // 字符串合法性
                        if (!message.matches("[0-9]+")) {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.input.amount_invalid"));
                            return;
                        }

                        int amount = Integer.parseInt(message);

                        if (!vaultEconomy.has(bukkitPlayer, amount)) {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.not_enough"), new Placeholder.Builder().add("owned", String.valueOf(vaultEconomy.getBalance(bukkitPlayer))).build());
                            return;
                        }

                        new GuildDonateGUI.ConfirmGUI(guildMember, MONEY, amount).open();
                    }

                    @Override
                    public void onTimeout(AsyncPlayerChatEvent event) {
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.input.timeout"));
                    }
                }).build().register();
            }
        });

        if (plugin.isPlayerPointsHooked()) {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.points"), bukkitPlayer, new Placeholder.Builder().addInner("fee", (int) (MainSettings.getDonatePointsFee() * 100)).build()), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.input.message"), new Placeholder.Builder()
                            .addInner("cancel_string", MainSettings.getDonateInputCancelString()).build());

                    new ChatInterceptor.Builder()
                            .plugin(plugin)
                            .player(bukkitPlayer)
                            .timeout(MainSettings.getDonateInputWaitSecond()).onlyFirst(true).chatListener(new ChatListener() {
                        @Override
                        public void onChat(AsyncPlayerChatEvent event) {
                            String message = event.getMessage();

                            // 捐赠取消字符串
                            if (message.equalsIgnoreCase(MainSettings.getDonateInputCancelString())) {
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.input.cancelled"));
                                return;
                            }

                            // 字符串合法性
                            if (!message.matches("[0-9]+")) {
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.input.amount_invalid"));
                                return;
                            }

                            // 数量
                            int amount = Integer.parseInt(message);

                            if (!playerPointsEconomy.has(bukkitPlayer, amount)) {
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.not_enough")
                                        , new Placeholder.Builder().addInner("owned", String.valueOf(playerPointsEconomy.getBalance(bukkitPlayer))).build());
                                return;
                            }

                            // 打开确认GUI
                            new GuildDonateGUI.ConfirmGUI(guildMember, POINTS, amount).open();
                        }

                        @Override
                        public void onTimeout(AsyncPlayerChatEvent event) {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.input.timeout"));
                        }
                    }).build().register();
                }
            });
        }

        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                close();
                new GuildMineGUI(guildMember).open();
            }
        });

        return guiBuilder.build();
    }
}
