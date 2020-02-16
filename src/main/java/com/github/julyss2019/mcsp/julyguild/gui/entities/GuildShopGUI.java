package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.ShopItemConfirmGUI;
import com.github.julyss2019.mcsp.julyguild.guild.*;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class GuildShopGUI extends BasePlayerGUI {
    private enum RewardType {
        GUILD_SET_SPAWN, GUILD_UPGRADE, GUILD_TP_ALL, GUILD_SHOP, GUILD_ICON, NONE, BACK, CUSTOM
    }

    private GuildMember guildMember;
    private YamlConfiguration yml;
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
        this(lastGUI, guildMember, JulyGuild.getInstance().getShopYaml(MainSettings.getGuildShopLauncher()));
    }

    protected GuildShopGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember, @NotNull YamlConfiguration yml) {
        super(lastGUI, Type.SHOP, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.yml = yml;
        this.guild = guildMember.getGuild();
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid() && guildMember.hasPermission(GuildPermission.USE_SHOP);
    }

    @Override
    public Inventory createInventory() {
        InventoryBuilder inventoryBuilder = new InventoryBuilder()
                .title(yml.getString("title"))
                .row(yml.getInt("row"));


        if (yml.contains("items")) {
            for (String shopItemName : yml.getConfigurationSection("items").getKeys(false)) {
                ConfigurationSection shopItemSection = yml.getConfigurationSection("items").getConfigurationSection(shopItemName);
                RewardType rewardType = RewardType.valueOf(shopItemSection.getString("reward_type"));

                switch (rewardType) {
                    case GUILD_SET_SPAWN: {
                        setSetGuildSpawnReward(shopItemSection, inventoryBuilder);
                        break;
                    }
                    case GUILD_UPGRADE: {
                        setGuildUpgradeReward(shopItemSection, inventoryBuilder);
                        break;
                    }
                    case NONE:
                        setNoneReward(shopItemSection, inventoryBuilder);
                        break;
                    case BACK:
                        setBackReward(shopItemSection, inventoryBuilder);
                        break;
                    case GUILD_SHOP:
                        setShopReward(shopItemSection, inventoryBuilder);
                        break;
                    case GUILD_ICON:
                        setGuildIconReward(shopItemSection, inventoryBuilder);
                        break;
                    case GUILD_TP_ALL:
                        setGuildTpAllReward(shopItemSection, inventoryBuilder);
                        break;
                }

            }
        }

        return inventoryBuilder.build();
    }

    private void setGuildTpAllReward(@NotNull ConfigurationSection shopItemSection, @NotNull InventoryBuilder inventoryBuilder) {
        ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
        double price = sellSection.getDouble("price");
    }

    private void setGuildIconReward(@NotNull ConfigurationSection shopItemSection, @NotNull InventoryBuilder inventoryBuilder) {
        ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
        double price = sellSection.getDouble("price");

        inventoryBuilder.item(shopItemSection.getInt("index") - 1, GUIItemManager.getItemBuilder(shopItemSection.getConfigurationSection("icon"), bukkitPlayer, new PlaceholderContainer()
                .add("price", price)).build(), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (checkGuildMoneyOrNotify(price)) {
                    new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection.getConfigurationSection("ConfirmGUI"), new PlaceholderContainer().add("price", price), price) {
                        @Override
                        public void onPaid() {
                            ConfigurationSection guildIconSection = sellSection.getConfigurationSection("guild_icon");

                            GuildIcon guildIcon = getGuild().giveIcon(Material.valueOf(guildIconSection.getString("material")), (short) guildIconSection.getInt("durability"), guildIconSection.getString("first_lore"), guildIconSection.getString("display_name"));

                            getGuild().setCurrentIcon(guildIcon);
                            Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(sellSection.getString("success_message"), new PlaceholderContainer()
                                    .add("display_name", guildIcon.getDisplayName())
                                    .add("price", price)));
                            back(40L);
                        }
                    }.open();
                }
            }
        });
    }

    private void setNoneReward(@NotNull ConfigurationSection shopItemSection, @NotNull InventoryBuilder inventoryBuilder) {
        inventoryBuilder.item(shopItemSection.getInt("index") - 1, GUIItemManager.getItemBuilder(shopItemSection.getConfigurationSection("icon"), bukkitPlayer).build());
    }

    private void setShopReward(@NotNull ConfigurationSection shopItemSection, InventoryBuilder inventoryBuilder) {
        inventoryBuilder.item(shopItemSection.getInt("index") - 1, GUIItemManager.getItemBuilder(shopItemSection.getConfigurationSection("icon"), bukkitPlayer).build(), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                close();
                new GuildShopGUI(lastGUI, guildMember, plugin.getShopYaml(shopItemSection.getString("shop"))).open();
            }
        });
    }

    private void setBackReward(@NotNull ConfigurationSection shopItemSection, @NotNull InventoryBuilder inventoryBuilder) {
        inventoryBuilder.item(shopItemSection.getInt("index") - 1, GUIItemManager.getItemBuilder(shopItemSection.getConfigurationSection("icon"), bukkitPlayer).build(), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                back();
            }
        });
    }

    private void setGuildUpgradeReward(@NotNull ConfigurationSection shopItemSection, @NotNull InventoryBuilder inventoryBuilder) {
        boolean available = guild.getMaxMemberCount() + 1 <= MainSettings.getGuildUpgradeMaxMemberCount();
        ConfigurationSection subItemSection = shopItemSection.getConfigurationSection(available ? "available" : "unavailable");
        int index = subItemSection.getInt("index") - 1;
        ConfigurationSection iconSection = subItemSection.getConfigurationSection("icon");

        if (available) {
            ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
            double price = sellSection.getDouble("price");

            inventoryBuilder.item(index, GUIItemManager.getItemBuilder(iconSection, bukkitPlayer, new PlaceholderContainer()
                    .add("old", guild.getMaxMemberCount())
                    .add("new", guild.getMaxMemberCount() + 1)
                    .add("price", price)).build(), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (checkGuildMoneyOrNotify(price)) {
                        new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection.getConfigurationSection("ConfirmGUI"), new PlaceholderContainer().add("price", price), price) {
                            @Override
                            public boolean canUse() {
                                return super.canUse() && getGuild().getMaxMemberCount() + 1 <= MainSettings.getGuildUpgradeMaxMemberCount();
                            }

                            @Override
                            public void onPaid() {
                                getGuild().setAdditionMemberCount(getGuild().getAdditionMemberCount() + 1);
                                Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(sellSection.getString("success_message"), new PlaceholderContainer()
                                        .add("old", guild.getMaxMemberCount() - 1)
                                        .add("new", guild.getMaxMemberCount())
                                        .add("price", price)));
                                back(40L);
                            }
                        }.open();
                    }
                }
            });
        } else {
            inventoryBuilder.item(index, GUIItemManager.getItemBuilder(iconSection, bukkitPlayer).build());
        }
    }

    private void setSetGuildSpawnReward(@NotNull ConfigurationSection shopItemSection, @NotNull InventoryBuilder inventoryBuilder) {
        ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
        double price = sellSection.getDouble("price");

        inventoryBuilder.item(shopItemSection.getInt("index") - 1, GUIItemManager.getItemBuilder(shopItemSection.getConfigurationSection("icon"), bukkitPlayer, new PlaceholderContainer()
                .add("price", price)).build(), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (checkGuildMoneyOrNotify(price)) {
                    new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection.getConfigurationSection("ConfirmGUI"), new PlaceholderContainer().add("price", price), price) {
                        @Override
                        public void onPaid() {
                            getGuild().setSpawn(getBukkitPlayer().getLocation());
                            Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(sellSection.getString("success_message"), new PlaceholderContainer().add("price", Util.SIMPLE_DECIMAL_FORMAT.format(price))));
                            back(40L);
                        }
                    }.open();
                }
            }
        });
    }

    private boolean checkGuildMoneyOrNotify(double cost) {
        if (!guild.getGuildBank().has(GuildBank.BalanceType.GMONEY, cost)) {
            Util.sendMsg(bukkitPlayer, thisLangSection.getString("gmoney_not_enough"), new PlaceholderContainer()
                    .add("need", new BigDecimal(cost).subtract(guild.getGuildBank().getBalance(GuildBank.BalanceType.GMONEY))));
            reopen(40L);
            return false;
        }

        return true;
    }
}
