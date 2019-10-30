package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.config.ConfigGuildShopItem;
import com.github.julyss2019.mcsp.julyguild.gui.BasePageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildManageGUI;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildShopItemBuyGUI;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class GuildShopGUI extends BasePageableGUI {
    private Inventory inventory;

    public GuildShopGUI(GuildPlayer guildPlayer) {
        super(GUIType.SHOP, guildPlayer);

        build();
    }

    @Override
    public void setCurrentPage(int page) {
  /*      super.setCurrentPage(page);

        InventoryBuilder inventoryBuilder = new InventoryBuilder().title("&e&l宗门商店(" + (page + 1) + ")").colored().row(6).listener(new InventoryListener() {
            @Override
            public void onClicked(InventoryClickEvent event) {
                int index = event.getSlot() + getCurrentPage() * 51;

                if (index < shopItems.size()) {
                    close();
                    new GuildShopItemBuyGUI(guildPlayer, shopItems.fromConfig(index)).open();
                }
            }
        });

        inventoryBuilder
                .item(53, CommonItem.BACK, new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        new GuildManageGUI(guildPlayer).open();
                    }
                });

        if (getTotalPage() > 1) {
            inventoryBuilder.item(51, CommonItem.PREVIOUS_PAGE, new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    if (hasPrecious()) {
                        close();
                        previousPage();
                    }
                }
            });
            inventoryBuilder.item(52, CommonItem.NEXT_PAGE, new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    if (hasNext()) {
                        close();
                        nextPage();
                    }
                }
            });
        }

        int itemSize = shopItems.size();
        int itemCounter = page * 51;
        int loopCount = itemSize - itemCounter < 51 ? itemSize - itemCounter : 51;

        for (int i = 0; i < loopCount; i++) {
            ConfigGuildShopItem shopItem = shopItems.fromConfig(itemCounter++);

            inventoryBuilder.item(i, shopItem.getItemBuilder().build());
        }

        this.inventory = inventoryBuilder.build();*/
    }

    @Override
    public int getTotalPage() {
/*        int size = shopItems.size();

        return size == 0 ? 1 : size % 51 == 0 ? size / 51 : size / 51 + 1;*/
return 0;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
