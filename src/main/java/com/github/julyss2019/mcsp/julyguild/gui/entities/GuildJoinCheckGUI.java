package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberPageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.Permission;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.Request;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildJoinCheckGUI extends BaseMemberPageableGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildJoinCheckGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildJoinCheckGUI");
    private final Player bukkitPlayer = getBukkitPlayer();
    private final Guild guild = guildMember.getGuild();
    private final List<Integer> itemIndexes = Util.getRangeIntegerList(thisGUISection.getString("items.request.indexes")); // 请求物品位置
    private final int itemIndexCount = itemIndexes.size(); // 请求物品位置数量

    private List<Request> requests;
    private int requestCount;

    public GuildJoinCheckGUI(GuildMember guildMember, @Nullable GUI lastGUI) {
        super(GUIType.PLAYER_JOIN_CHECK, guildMember, lastGUI);

        update();

        if (getTotalPage() > 0) {
            setCurrentPage(0);
        }
    }

    @Override
    public void update() {
        this.requests = guild.getReceivedRequests();
        this.requestCount = requests.size();

        setTotalPage(requestCount % itemIndexCount == 0 ? requestCount / itemIndexCount : requestCount / itemIndexCount + 1);
    }

    @Override
    public Inventory createInventory() {
        Map<Integer, Request> indexMap = new HashMap<>();
        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, guildMember, new Placeholder.Builder()
                        .addInner("page", getCurrentPage() + 1)
                        .addInner("total_page", getTotalPage()))
                .pageItems(thisGUISection.getConfigurationSection("items.page_items"), this, bukkitPlayer)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), guildMember), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        back();
                    }
                })
                .listener(new InventoryListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int index = event.getSlot();

                        if (indexMap.containsKey(index)) {
                            Request request = indexMap.get(index);
                            GuildPlayer sender = (GuildPlayer) request.getSender();
                            InventoryAction action = event.getAction();

                            if (!request.isValid()) {
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("invalid"));
                                reopen(20L);
                                return;
                            }

                            if (action == InventoryAction.PICKUP_ALL) {
                                request.delete();
                                guild.addMember(sender);

                                guild.broadcastMessage(PlaceholderText.replacePlaceholders(thisLangSection.getString("accept.broadcast"), new Placeholder.Builder()
                                        .addInner("player", sender.getName())
                                        .build()));
                                reopen(20L);
                                return;
                            }

                            if (action == InventoryAction.PICKUP_HALF) {
                                request.delete();
                                Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("deny.approver"), new Placeholder.Builder()
                                        .addInner("player", sender.getName())
                                        .build()));
                                reopen(20L);
                            }
                        }
                    }
                });

        if (getCurrentPage() != -1) {
            int itemCounter = getCurrentPage() * itemIndexes.size();
            int loopCount = requestCount - itemCounter < itemIndexCount ? requestCount - itemCounter : itemIndexCount; // 循环次数，根据当前能够显示的数量决定

            for (int i = 0; i < loopCount; i++) {
                Request request = requests.get(itemCounter++);
                GuildPlayer sender = (GuildPlayer) request.getSender();
                ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items.request"), sender.getOfflineBukkitPlayer(), new Placeholder.Builder()
                        .addInner("sender_name", sender.getName())
                        .addInner("send_time", LangHelper.Global.getDateTimeFormat().format(request.getCreationTime()))
                        .build());

                guiBuilder.item(itemIndexes.get(i) - 1, itemBuilder.build());
                indexMap.put(itemIndexes.get(i) - 1, request);
            }
        }

        return guiBuilder.build();
    }

    @Override
    public boolean canUse() {
        return guild.isValid() && guildMember.hasPermission(Permission.PLAYER_JOIN_CHECK);
    }
}
