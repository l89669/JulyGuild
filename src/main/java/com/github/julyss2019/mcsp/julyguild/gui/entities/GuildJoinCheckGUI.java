package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.Request;
import com.github.julyss2019.mcsp.julyguild.request.RequestManager;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildJoinCheckGUI extends BasePlayerPageableGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final RequestManager requestManager = plugin.getRequestManager();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildJoinCheckGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildJoinCheckGUI");
    private final Player bukkitPlayer = getBukkitPlayer();
    private final List<Integer> itemIndexes = Util.getIndexes(thisGUISection.getString("items.request.indexes")); // 请求物品位置
    private final int itemIndexCount = itemIndexes.size(); // 请求物品位置数量
    private final GuildMember guildMember;
    private final Guild guild;

    private List<Request> requests;
    private int requestCount;

    public GuildJoinCheckGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        super(lastGUI, Type.PLAYER_JOIN_CHECK, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.guild = guildMember.getGuild();
    }

    @Override
    public void update() {
        this.requests = guild.getReceivedRequests();
        this.requestCount = requests.size();

        setPageCount(requestCount % itemIndexCount == 0 ? requestCount / itemIndexCount : requestCount / itemIndexCount + 1);
    }

    @Override
    public Inventory createInventory() {
        Map<Integer, Request> indexMap = new HashMap<>();
        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer, new PlaceholderContainer()
                        .add("page", getCurrentPage() + 1)
                        .add("total_page", getPageCount()))
                .pageItems(thisGUISection.getConfigurationSection("items.page_items"), this)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (canBack()) {
                            back();
                        }
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
                                Util.sendMsg(bukkitPlayer, thisLangSection.getString("invalid"));
                                reopen(20L);
                                return;
                            }

                            if (action == InventoryAction.PICKUP_ALL) {
                                requestManager.deleteRequest(request);
                                guild.addMember(sender);

                                guild.broadcastMessage(PlaceholderText.replacePlaceholders(thisLangSection.getString("accept.broadcast"), new PlaceholderContainer()
                                        .add("player", sender.getName())));
                                reopen(20L);
                                return;
                            }

                            if (action == InventoryAction.PICKUP_HALF) {
                                requestManager.deleteRequest(request);
                                Util.sendMsg(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("deny.approver"), new PlaceholderContainer()
                                        .add("player", sender.getName())));
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
                ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items.request.icon"), sender.getOfflineBukkitPlayer(), new PlaceholderContainer()
                        .add("sender_name", sender.getName())
                        .add("send_time", LangHelper.Global.getDateTimeFormat().format(request.getCreationTime())));

                guiBuilder.item(itemIndexes.get(i), itemBuilder.build());
                indexMap.put(itemIndexes.get(i), request);
            }
        }

        return guiBuilder.build();
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid();
    }
}
