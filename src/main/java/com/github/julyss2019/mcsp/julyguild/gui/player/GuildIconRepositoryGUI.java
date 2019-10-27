package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.config.Lang;
import com.github.julyss2019.mcsp.julyguild.gui.BasePageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildManageGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.OwnedIcon;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class GuildIconRepositoryGUI extends BasePageableGUI {
    private Inventory inventory;
    private Guild guild;
    private List<OwnedIcon> icons = new ArrayList<>();

    public GuildIconRepositoryGUI(GuildPlayer guildPlayer) {
        super(GUIType.ICON_REPOSITORY, guildPlayer);

        this.guild = this.guildPlayer.getGuild();
        setCurrentPage(0);
    }

    @Override
    public int getTotalPage() {
        int iconSize = icons.size();

        return iconSize == 0 ? 1 : iconSize % 51 == 0 ? iconSize / 51 : iconSize / 51 + 1;
    }

    @Override
    public void setCurrentPage(int page) {
        super.setCurrentPage(page);

        InventoryBuilder inventoryBuilder = new InventoryBuilder().title(Lang.getString("GuildIconRepositoryGUI.title")).row(6).colored().listener(new InventoryListener() {
            @Override
            public void onClicked(InventoryClickEvent event) {
                int index = getCurrentPage() * 51 + event.getSlot();

                if (index < icons.size()) {
                    OwnedIcon icon = icons.get(index);

                    if (!guild.getCurrentIcon().equals(icon)) {
                        guild.setCurrentIcon(icon);
                        close();
                        build();
                        open();
                    }
                }
            }
        });

        inventoryBuilder.item(53, CommonItem.BACK, new ItemListener() {
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

        this.icons = guild.getIcons();

        int iconSize = icons.size();
        int itemCounter = page * 51;
        int loopCount = iconSize - itemCounter < 51 ? iconSize - itemCounter : 51;

        for (int i = 0; i < loopCount; i++) {
            OwnedIcon icon = icons.get(itemCounter++);
            ItemBuilder itemBuilder = new ItemBuilder().material(icon.getMaterial()).durability(icon.getDurability()).colored();

            if (guild.getCurrentIcon().equals(icon)) {
                itemBuilder.addLore(Lang.getString("GuildIconRepositoryGUI.current_use"));
                itemBuilder.enchant(Enchantment.DURABILITY, 1);
                itemBuilder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            } else {
                itemBuilder.addLore(Lang.getString("GuildIconRepositoryGUI.set"));
            }

            inventoryBuilder.item(i, itemBuilder.build());
        }

        this.inventory = inventoryBuilder.build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
