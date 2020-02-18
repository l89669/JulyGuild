package com.github.julyss2019.bukkit.plugins.julyguild.gui.entities;

import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.bukkit.plugins.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.bukkit.plugins.julyguild.gui.GUI;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.Guild;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildIcon;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildMember;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildPermission;
import com.github.julyss2019.bukkit.plugins.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.bukkit.plugins.julyguild.util.Util;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.bukkit.plugins.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuildIconRepositoryGUI extends BasePlayerPageableGUI {
    private JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildIconRepositoryGUI");
    private final List<Integer> itemIndexes = Util.getIndexes(thisGUISection.getString("items.guild_icon.indexes")); // 得到所有可供公会设置的位置
    private final int itemIndexCount = itemIndexes.size();
    private final Player bukkitPlayer = getBukkitPlayer();
    private GuildMember guildMember;
    private List<GuildIcon> icons = new ArrayList<>();
    private int iconCount;
    private Guild guild;

    public GuildIconRepositoryGUI(@Nullable GUI lastGUI, GuildMember guildMember) {
        super(lastGUI, Type.ICON_REPOSITORY, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.guild = guildMember.getGuild();
    }

    @Override
    public void update() {
        this.icons = guild.getIcons();

        icons.add(0, null);

        //noinspection ComparatorMethodParameterNotUsed
        icons.sort((o1, o2) -> o1.equals(guild.getCurrentIcon()) ? -1 : 1);

        this.iconCount = icons.size();

        setPageCount(iconCount % itemIndexCount == 0 ? iconCount / itemIndexCount : iconCount / itemIndexCount + 1);
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid() && guildMember.hasPermission(GuildPermission.USE_ICON_REPOSITORY);
    }

    @Override
    public Inventory createInventory() {
        Map<Integer, GuildIcon> indexMap = new HashMap<>();
        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder().fromConfig(thisGUISection, bukkitPlayer, new PlaceholderContainer()
                .add("total_page", getPageCount())
                .add("page", getCurrentPage() + 1))
                .listener(new InventoryListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int slot = event.getRawSlot();

                        if (!indexMap.containsKey(slot)) {
                            return;
                        }

                        GuildIcon guildIcon = indexMap.get(slot);

                        if (guildIcon == null) {
                            guild.setCurrentIcon(null);
                        } else {
                            if (!guildIcon.isValid()) {
                                reopen();
                                return;
                            }

                            guild.setCurrentIcon(guildIcon);
                        }

                        reopen();
                    }
                });


        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                back();
            }
        });

        if (getCurrentPage() != -1) {
            int itemCounter = getCurrentPage() * itemIndexes.size();
            int loopCount = iconCount - itemCounter < itemIndexCount ? iconCount - itemCounter : itemIndexCount; // 循环次数，根据当前能够显示的数量决定

            for (int i = 0; i < loopCount; i++) {
                GuildIcon guildIcon = icons.get(itemCounter);
                ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items.guild_icon." + (Objects.equals(guildIcon, guild.getCurrentIcon()) ? "using" : "not_using") + ".icon")
                        , bukkitPlayer);

                if (guildIcon == null) {
                    itemBuilder.material(MainSettings.getGuildIconDefaultMaterial()).durability(MainSettings.getGuildIconDefaultDurability());

                    if (!StringUtils.isEmpty(MainSettings.getGuildIconDefaultFirstLore())) {
                        itemBuilder.insertLore(0, MainSettings.getGuildIconDefaultFirstLore());
                    }
                } else {
                    itemBuilder.material(guildIcon.getMaterial()).durability(guildIcon.getDurability());

                    if (guildIcon.getFirstLore() != null) {
                        itemBuilder.insertLore(0, guildIcon.getFirstLore());
                    }
                }

                if (guildIcon != null) {
                    itemBuilder.displayName(guildIcon.getDisplayName());
                }

                guiBuilder.item(itemIndexes.get(i), itemBuilder.build());
                indexMap.put(itemIndexes.get(i), guildIcon);
                itemCounter++;
            }
        }

        return guiBuilder.build();
    }
}
