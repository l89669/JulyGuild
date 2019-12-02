package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.Permission;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildMemberGUI extends BasePlayerPageableGUI {
    private enum ViewerType {PLAYER, MANAGER}
    private static final List<Permission> managerPermissions = Arrays.asList(Permission.MEMBER_KICK, Permission.MEMBER_SET_ADMIN, Permission.MEMBER_SET_PERMISSION);
    private final JulyGuild plugin = JulyGuild.getInstance();
    private ConfigurationSection thisGUISection;
    private final ViewerType viewerType;
    private final Guild guild;
    private final Player bukkitPlayer = getBukkitPlayer();
    private List<Integer> positions;
    private int positionCount;
    private int memberCount;

    public GuildMemberGUI(Guild guild, GuildMember guildMember) {
        this(guild, guildMember.getGuildPlayer());
    }

    public GuildMemberGUI(Guild guild, GuildPlayer guildPlayer) {
        super(GUIType.MEMBER, guildPlayer);
        this.guild = guild;

        GuildMember member = guild.getMember(guildPlayer);

        out:
        if (member == null) {
            this.viewerType = ViewerType.PLAYER;
        } else {
            for (Permission permission : managerPermissions) {
                if (member.hasPermission(permission)) {
                    this.viewerType = ViewerType.MANAGER;
                    break out;
                }
            }

            this.viewerType = ViewerType.PLAYER;
        }

        this.thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildMemberGUI").getConfigurationSection(viewerType.name().toLowerCase());
        this.positions = Util.getRangeIntegerList(thisGUISection.getString("positions")); // 得到所有可供公会设置的位置
        this.positionCount = positions.size();
    }

    @Override
    public Inventory getInventory() {
        System.out.println(thisGUISection.getConfigurationSection("items.pageable"));
        List<GuildMember> members = guild.getMembers();
        this.memberCount = members.size();
        Map<Integer, String> positionMap = new HashMap<>();
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer)
                .pageable(thisGUISection.getConfigurationSection("items.pageable"), this, bukkitPlayer, guild)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer, guild), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildInfoGUI(guildPlayer, guild).open();
                    }
                });
        int itemCounter = getCurrentPage() * positions.size();
        int loopCount = memberCount - itemCounter < memberCount ? memberCount - itemCounter : memberCount;

        for (int i = 0; i < loopCount; i++) {
            GuildMember guildMember = members.get(itemCounter++);
            ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items").getConfigurationSection("member")
                    , guildMember, new Placeholder.Builder().addGuildMemberPlaceholders(guildMember));

            positionMap.put(positions.get(i) - 1, guildMember.getName());
            guiBuilder.item(positions.get(i) - 1, itemBuilder.build());
        }

        return guiBuilder.build();
    }

    @Override
    public int getTotalPage() {
        return memberCount == 0 ? 1 : memberCount % positionCount == 0 ? memberCount / positionCount : memberCount / positionCount + 1;
    }
}
