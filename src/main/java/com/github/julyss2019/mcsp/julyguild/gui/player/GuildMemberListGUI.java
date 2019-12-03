package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.Permission;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildMemberListGUI extends BasePlayerPageableGUI {
    private enum ViewerType {PLAYER, MANAGER}
    private static final List<Permission> managerPermissions = Arrays.asList(Permission.MEMBER_KICK, Permission.MEMBER_SET_ADMIN, Permission.MEMBER_SET_PERMISSION);
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ViewerType viewerType;
    private final Guild guild;
    private final Player bukkitPlayer = getBukkitPlayer();
    private ConfigurationSection thisGUISection;
    private List<Integer> positions;
    private int positionCount;

    public GuildMemberListGUI(Guild guild, GuildMember guildMember, @Nullable GUI lastGUI) {
        this(guild, guildMember.getGuildPlayer(), lastGUI);
    }

    public GuildMemberListGUI(Guild guild, GuildPlayer guildPlayer, @Nullable GUI lastGUI) {
        super(GUIType.MEMBER, guildPlayer, lastGUI);

        this.guild = guild;

        GuildMember member = guild.getMember(guildPlayer);

        out:
        if (member == null) {
            viewerType = ViewerType.PLAYER;
        } else {
            for (Permission permission : managerPermissions) {
                if (member.hasPermission(permission)) {
                    viewerType = ViewerType.MANAGER;
                    break out;
                }
            }

            viewerType = ViewerType.PLAYER;
        }

        this.thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildMemberListGUI").getConfigurationSection(viewerType.name().toLowerCase());
        this.positions = Util.getRangeIntegerList(thisGUISection.getString("positions")); // 得到所有可供公会设置的位置
        this.positionCount = positions.size();
    }

    @Override
    public Inventory getInventory() {
        List<GuildMember> members = guild.getMembers();
        int memberCount = members.size();

        setTotalPage(memberCount == 0 ? 1 : memberCount % positionCount == 0 ? memberCount / positionCount : memberCount / positionCount + 1);

        Map<Integer, GuildMember> positionMap = new HashMap<>();
        IndexConfigGUI.Builder guiBuilder = (IndexConfigGUI.Builder) new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer)
                .pageItems(thisGUISection.getConfigurationSection("items.page_items"), this, bukkitPlayer, guild)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer, guild), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        back();
                    }
                })
                .listener(new InventoryListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int slot = event.getSlot();

                        if (positionMap.containsKey(slot)) {

                        }
                    }
                });

        int itemCounter = getCurrentPage() * positions.size();
        int loopCount = memberCount - itemCounter < memberCount ? memberCount - itemCounter : memberCount;

        for (int i = 0; i < loopCount; i++) {
            GuildMember guildMember = members.get(itemCounter++);
            ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items").getConfigurationSection("member")
                    , guildMember, new Placeholder.Builder().addGuildMemberPlaceholders(guildMember));

            positionMap.put(positions.get(i) - 1, guildMember);
            guiBuilder.item(positions.get(i) - 1, itemBuilder.build());
        }

        return guiBuilder.build();
    }
}
