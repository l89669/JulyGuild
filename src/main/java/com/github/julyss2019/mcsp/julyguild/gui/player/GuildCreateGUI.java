package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.GuildSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.TitleBuilder;
import com.github.julyss2019.mcsp.julylibrary.utils.ItemUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GuildCreateGUI extends BaseGUI {
    private static JulyGuild plugin = JulyGuild.getInstance();
    private static GuildSettings settings = plugin.getGuildSettings();
    private static Economy vault = plugin.getVaultAPI();
    private static PlayerPointsAPI playerPointsAPI = plugin.getPlayerPointsAPI();
    private static GuildManager guildManager = plugin.getGuildManager();
    private String guildName;
    private Inventory inventory;
    private boolean noAction = true;

    public GuildCreateGUI(GuildPlayer guildPlayer, String guildName) {
        super(GUIType.CREATE, guildPlayer);

        this.guildName = guildName;
        build();
    }

    @Override
    public void build() {
        super.build();

        InventoryBuilder inventoryBuilder = new InventoryBuilder().row(3).title("&e&l请选择支付方式").colored().listener(new InventoryListener() {
            @Override
            public void onClose(InventoryCloseEvent event) {
                if (noAction) {
                    JulyMessage.sendColoredMessages(bukkitPlayer, "&c创建宗门失败!");
                }
            }
        });

        if (settings.isGuildCreateCostMoneyEnabled()) {
            inventoryBuilder.item(1, 2, new ItemBuilder()
                    .material(Material.GOLD_INGOT)
                    .displayName("&f使用 &a金币x" + settings.getGuildCreateCostMoneyAmount() + " &f支付")
                    .addLore("")
                    .addLore("&a• &d点击支付&a •")
                    .addLore("")
                    .addLore("&7- &e公会名 &b▹ &f" + guildName)
                    .colored()
                    .build()
                    , new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            noAction = false;
                            close();

                            if (offlineGuildPlayer.isInGuild()) {
                                JulyMessage.sendColoredMessage(bukkitPlayer, "&c你已经在一个宗门里了.");
                                return;
                            }

                            double playerMoney = vault.getBalance(bukkitPlayer);

                            if (playerMoney < settings.getGuildCreateCostMoneyAmount()) {
                                JulyMessage.sendColoredMessage(bukkitPlayer, "&c要创建宗门, 你还需要 &e" + (settings.getGuildCreateCostMoneyAmount() - playerMoney) + "个 &c金币!");
                                return;
                            }

                            vault.withdrawPlayer(bukkitPlayer, settings.getGuildCreateCostMoneyAmount());
                            createGuild(guildPlayer, guildName);
                        }
                    });
        }

        if (settings.isGuildCreateCostPointsEnabled()) {
            inventoryBuilder.item(1, 4, new ItemBuilder()
                    .material(Material.DIAMOND)
                    .displayName("&f使用 &a点券x" + settings.getGuildCreateCostPointsAmount() + " &f支付")
                    .addLore("")
                    .addLore("&a• &d点击支付&a •")
                    .addLore("")
                    .addLore("&7- &e公会名 &b▹ &f" + guildName)
                    .colored()
                    .build()
                    , new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            noAction = false;
                            close();

                            if (offlineGuildPlayer.isInGuild()) {
                                JulyMessage.sendColoredMessage(bukkitPlayer, "&c你已经在一个宗门里了.");
                                return;
                            }

                            int playerPoints = playerPointsAPI.look(bukkitPlayer.getUniqueId());

                            if (playerPoints < settings.getGuildCreateCostPointsAmount()) {
                                JulyMessage.sendColoredMessage(bukkitPlayer, "&c要创建宗门, 你还需要 &e" + (settings.getGuildCreateCostPointsAmount() - playerPoints) + "个 &c点券!");
                                return;
                            }

                            playerPointsAPI.take(bukkitPlayer.getUniqueId(), settings.getGuildCreateCostPointsAmount());
                            createGuild(guildPlayer, guildName);
                        }
            });
        }

        if (settings.isGuildCreateCostItemEnabled()) {
            inventoryBuilder.item(1, 6, new ItemBuilder()
                    .material(Material.NAME_TAG)
                    .displayName("&f使用 &a建帮令x" + settings.getGuildCreateCostItemAmount() + " &f支付")
                    .addLore("")
                    .addLore("&a• &d点击支付&a •")
                    .addLore("")
                    .addLore("&7- &e公会名 &b▹ &f" + guildName)
                    .colored()
                    .build()
                    , new ItemListener() {
                        @Override
                        public void onClicked(InventoryClickEvent event) {
                            noAction = false;
                            close();

                            if (offlineGuildPlayer.isInGuild()) {
                                JulyMessage.sendColoredMessage(bukkitPlayer, "&c你已经在一个宗门里了.");
                                return;
                            }

                            for (ItemStack itemStack : bukkitPlayer.getInventory().getContents()) {
                                if (ItemUtil.containsLore(itemStack, settings.getGuildCreateCostItemKeyLore())) {
                                    int amount = itemStack.getAmount();

                                    if (amount >= settings.getGuildCreateCostItemAmount()) {
                                        if (amount - settings.getGuildCreateCostItemAmount() == 0) {
                                            itemStack.setType(Material.AIR);
                                        } else {
                                            itemStack.setAmount(settings.getGuildCreateCostItemAmount() - amount);
                                        }

                                        createGuild(guildPlayer, guildName);
                                        return;
                                    }
                                }
                            }

                            JulyMessage.sendColoredMessage(bukkitPlayer, "&c要创建宗门, 你还需要 &e建帮令x" + settings.getGuildCreateCostItemAmount() + "&c(请重叠放置).");
                        }
                    });
        }

        this.inventory = inventoryBuilder.build();
    }

    private void createGuild(GuildPlayer guildPlayer, String guildName) {
        Player bukkitPlayer = guildPlayer.getBukkitPlayer();

        guildManager.createGuild(guildPlayer, guildName);
        JulyMessage.sendColoredMessage(bukkitPlayer, "&d恭喜 &e" + bukkitPlayer.getName() + " &d创建宗门 &e" + guildName + " &d成功!");
        JulyMessage.sendTitle(bukkitPlayer, new TitleBuilder().text("&d创建宗门成功").colored().build());

        new BukkitRunnable() {
            @Override
            public void run() {
                bukkitPlayer.performCommand("guild main");
            }
        }.runTaskLater(plugin, 60L);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
