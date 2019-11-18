package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatFilter;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

public class GuildDonateGUI extends BaseMemberGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final Player bukkitPlayer = getBukkitPlayer();
    private final Economy vault = plugin.getVaultAPI();
    private final PlayerPointsAPI playerPointsAPI = plugin.getPlayerPointsAPI();
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("GuildDonateGUI");
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildDonateGUI");

    public GuildDonateGUI(GuildMember guildMember) {
        super(GUIType.DONATE, guildMember);
    }

    @Override
    public Inventory getInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer);

        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.money"), bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                close();
                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.input.message"), new Placeholder.Builder()
                        .addInner("CANCEL_STRING", MainSettings.getDonateInputCancelString()).build());
                JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                    @Override
                    public void onChat(AsyncPlayerChatEvent event) {
                        JulyChatFilter.unregisterChatFilter(bukkitPlayer);

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

                        if (!vault.has(bukkitPlayer, amount)) {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.not_enough"));
                            return;
                        }

                        if (vault.withdrawPlayer(bukkitPlayer, amount).type != EconomyResponse.ResponseType.SUCCESS) {
                            throw new RuntimeException("扣除金币失败");
                        }

                        guild.getGuildBank().deposit(GuildBank.BalanceType.MONEY, amount);
                        guildMember.addDonated(GuildBank.BalanceType.MONEY, amount);
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.success"), new Placeholder.Builder()
                                .addInner("amount", amount).build());
                    }

                    @Override
                    public void onTimeout() {
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("money.input.timeout"));
                    }
                }, MainSettings.getDonateInputWaitSecond());
            }
        });

        if (plugin.isPlayerPointsHooked()) {
            guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.points"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.input.message"), new Placeholder.Builder()
                            .addInner("CANCEL_STRING", MainSettings.getDonateInputCancelString()).build());
                    JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                        @Override
                        public void onChat(AsyncPlayerChatEvent event) {
                            JulyChatFilter.unregisterChatFilter(bukkitPlayer);

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

                            int amount = Integer.parseInt(message);

                            if (!vault.has(bukkitPlayer, amount)) {
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.not_enough"));
                                return;
                            }

                            playerPointsAPI.take(bukkitPlayer.getUniqueId(), amount);
                            guild.getGuildBank().deposit(GuildBank.BalanceType.POINTS, amount);
                            guildMember.addDonated(GuildBank.BalanceType.POINTS, amount);
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.success"), new Placeholder.Builder()
                                    .addInner("amount", amount).build());
                        }

                        @Override
                        public void onTimeout() {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("points.input.timeout"));
                        }
                    });
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
