package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.*;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.GuildPermission;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.shop.reward.Reward;
import com.github.julyss2019.mcsp.julyguild.shop.reward.entities.GuildSetSpawnReward;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class GuildShopGUI extends BasePlayerGUI {
    private GuildMember guildMember;
    private YamlConfiguration shopYaml;
    private Guild guild;
    private JulyGuild plugin = JulyGuild.getInstance();
    private ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("Shop");
    private Player bukkitPlayer = getBukkitPlayer();

    /**
     * 使用引导Shop YAML
     * @param lastGUI
     * @param guildMember
     */
    protected GuildShopGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        this(lastGUI, guildMember, JulyGuild.getInstance().getShopYaml(MainSettings.getShopLauncher()));
    }

    protected GuildShopGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember, @NotNull YamlConfiguration shopYaml) {
        super(lastGUI, GUIType.SHOP, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.shopYaml = shopYaml;
        this.guild = guildMember.getGuild();
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid() && guildMember.hasPermission(GuildPermission.USE_SHOP);
    }

    @Override
    public Inventory createInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder().fromConfig(shopYaml, bukkitPlayer);

        guiBuilder.item(GUIItemManager.getIndexItem(shopYaml.getConfigurationSection("items.back")), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                back();
            }
        });

        if (shopYaml.contains("shop_items")) {
            for (String itemName : shopYaml.getConfigurationSection("shop_items").getKeys(false)) {
                ConfigurationSection itemSection = shopYaml.getConfigurationSection("shop_items").getConfigurationSection(itemName);
                ConfigurationSection sellSection = itemSection.getConfigurationSection("sell");
                Reward.Type rewardType = Reward.Type.valueOf(itemSection.getString("reward_type"));
                double price = Util.calculate(PlaceholderText.replacePlaceholders(sellSection.getString("price"), new PlaceholderContainer().addGuildPlaceholders(guild)));

                switch (rewardType) {
                    case GUILD_SET_SPAWN:
                        guiBuilder.item(GUIItemManager.getIndexItem(itemSection, new PlaceholderContainer().add("price", Util.SIMPLE_DECIMAL_FORMAT.format(price))), new ItemListener() {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                if (guildMoneyCheck(price)) {
                                    new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection, new GuildSetSpawnReward(itemSection)).open();
                                }
                            }
                        });

                        break;
                }

            }
        }

        return guiBuilder.build();
    }

    private boolean guildMoneyCheck(double cost) {
        if (!guild.getGuildBank().has(GuildBank.BalanceType.GMONEY, cost)) {
            Util.sendMsg(bukkitPlayer, thisLangSection.getString("gmoney_not_enough"), new PlaceholderContainer()
                    .add("need", new BigDecimal(cost).subtract(guild.getGuildBank().getBalance(GuildBank.BalanceType.GMONEY))));
            reopen(40L);
            return false;
        }

        return true;
    }
}
