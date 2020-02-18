package com.github.julyss2019.bukkit.plugins.julyguild.gui.entities;

import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.Guild;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildPermission;
import com.github.julyss2019.bukkit.plugins.julyguild.util.Util;
import com.github.julyss2019.bukkit.plugins.julyguild.JulyGuild;
import com.github.julyss2019.bukkit.plugins.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.bukkit.plugins.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.bukkit.plugins.julyguild.gui.GUI;
import com.github.julyss2019.bukkit.plugins.julyguild.guild.GuildMember;
import com.github.julyss2019.bukkit.plugins.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.bukkit.plugins.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuildMemberListGUI extends BasePlayerPageableGUI {
    private enum ViewerType {PLAYER, MANAGER}
    private static final List<GuildPermission> MANAGER_GUILD_PERMISSIONS = Arrays.asList(GuildPermission.MEMBER_KICK, GuildPermission.MANAGE_PERMISSION);
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ViewerType viewerType;
    private final Guild guild;
    private final Player bukkitPlayer = getBukkitPlayer();
    private ConfigurationSection thisGUISection;
    private List<Integer> positions;
    private int positionCount;
    private List<GuildMember> members;
    private int memberCount;

    public GuildMemberListGUI(@Nullable GUI lastGUI, @NotNull Guild guild, @NotNull GuildMember guildMember) {
        this(lastGUI, guild, guildMember.getGuildPlayer());
    }

    public GuildMemberListGUI(@Nullable GUI lastGUI, @NotNull Guild guild, @NotNull GuildPlayer guildPlayer) {
        super(lastGUI, Type.MEMBER_LIST, guildPlayer);

        this.guild = guild;

        GuildMember member = guild.getMember(guildPlayer);

        out:
        if (member == null) {
            viewerType = ViewerType.PLAYER;
        } else {
            for (GuildPermission guildPermission : MANAGER_GUILD_PERMISSIONS) {
                if (member.hasPermission(guildPermission)) {
                    viewerType = ViewerType.MANAGER;
                    break out;
                }
            }

            viewerType = ViewerType.PLAYER;
        }

        this.thisGUISection = plugin.getGUIYaml("GuildMemberListGUI").getConfigurationSection(viewerType.name());
        this.positions = Util.getIndexes(thisGUISection.getString( "items.member.indexes")); // 得到所有可供公会设置的位置
        this.positionCount = positions.size();
    }

    @Override
    public void update() {
        this.members = guild.getMembers();
        this.members.sort(Comparator.comparingLong(GuildMember::getJoinTime));
        this.memberCount = members.size();

        setPageCount(memberCount % positionCount == 0 ? memberCount / positionCount : memberCount / positionCount + 1);
    }

    @Override
    public Inventory createInventory() {
        Map<Integer, GuildMember> indexMap = new HashMap<>();
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer, new PlaceholderContainer()
                        .add("page", getCurrentPage() + 1)
                        .add("total_page", getPageCount()))
                .pageItems(thisGUISection.getConfigurationSection("items.page_items"), this)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer, new PlaceholderContainer().addGuildPlaceholders(guild)), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (canBack()) {
                            back();
                        }
                    }
                });

        if (viewerType == ViewerType.MANAGER) {
            guiBuilder.listener(new InventoryListener() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            int slot = event.getSlot();

                            if (indexMap.containsKey(slot)) {
                                GuildMember guildMember = indexMap.get(slot);

                                if (!guildMember.isValid()) {
                                    reopen();
                                    return;
                                }

                                close();

                                new GuildMemberManageGUI(GuildMemberListGUI.this, GuildMemberListGUI.this.guild.getMember(guildPlayer), guildMember).open();
                            }
                        }
                    });
        }

        int itemCounter = getCurrentPage() * positions.size();
        int loopCount = memberCount - itemCounter < memberCount ? memberCount - itemCounter : memberCount;

        for (int i = 0; i < loopCount; i++) {
            GuildMember guildMember = members.get(itemCounter++);
            ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items.member.icon"), bukkitPlayer, new PlaceholderContainer()
                    .addGuildPlaceholders(guild)
                    .addGuildMemberPlaceholders(guildMember));

            // 管理模式
            if (viewerType == ViewerType.MANAGER) {
                indexMap.put(positions.get(i), guildMember);
            }

            guiBuilder.item(positions.get(i), itemBuilder.build());
        }

        return guiBuilder.build();
    }

    @Override
    public boolean canUse() {
        return guild.isValid();
    }
}
