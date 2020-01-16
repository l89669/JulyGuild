package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.Permission;
import com.github.julyss2019.mcsp.julyguild.guild.Position;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class GuildMemberManageGUI extends BaseMemberGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final Player bukkitPlayer = getBukkitPlayer();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildMemberManageGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildMemberManageGUI");
    private final Guild guild = guildMember.getGuild();
    private final GuildMember targetGuildMember;


    public GuildMemberManageGUI(@Nullable GUI lastGUI, GuildMember mangerGuildMember, GuildMember targetGuildMember) {
        super(lastGUI, GUIType.MEMBER_MANAGE, mangerGuildMember);

        this.targetGuildMember = targetGuildMember;
    }

    @Override
    public Inventory createInventory() {
        PriorityConfigGUI.Builder guiBuilder = (PriorityConfigGUI.Builder) new PriorityConfigGUI.Builder()
                .fromConfig(thisGUISection, targetGuildMember, new Placeholder.Builder().addGuildMemberPlaceholders(targetGuildMember))
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), targetGuildMember), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        back();
                    }
                });

        // 如果管理者和被管理员一样
        if (guildMember.equals(targetGuildMember)) {
            return guiBuilder.build();
        }

        // 自己有权限且对方无权限才能使用
        if (guild.isOwner(guildMember) || guildMember.hasPermission(Permission.MEMBER_KICK) && !targetGuildMember.hasPermission(Permission.MEMBER_KICK)) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.kick"), targetGuildMember), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(Permission.MEMBER_KICK)) {
                        return;
                    }

                    guild.removeMember(targetGuildMember);
                    back();
                }
            });
        }

        if (guildMember.hasPermission(Permission.MANAGE_PERMISSION)) {
            guiBuilder.item(getToggleItem(thisGUISection.getConfigurationSection("items.manage_member_kick"), Permission.MEMBER_KICK), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(Permission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(Permission.MEMBER_KICK, !targetGuildMember.hasPermission(Permission.MEMBER_KICK));
                    reopen();
                }
            });
            guiBuilder.item(getToggleItem(thisGUISection.getConfigurationSection("items.manage_set_spawn"), Permission.SET_SPAWN), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(Permission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(Permission.SET_SPAWN, !targetGuildMember.hasPermission(Permission.SET_SPAWN));
                    reopen();
                }
            });
            guiBuilder.item(getToggleItem(thisGUISection.getConfigurationSection("items.manage_set_member_pvp"), Permission.SET_MEMBER_PVP), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(Permission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(Permission.SET_MEMBER_PVP, !targetGuildMember.hasPermission(Permission.SET_MEMBER_PVP));
                    reopen();
                }
            });
            guiBuilder.item(getToggleItem(thisGUISection.getConfigurationSection("items.manage_player_join_check"), Permission.PLAYER_JOIN_CHECK), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(Permission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(Permission.PLAYER_JOIN_CHECK, !targetGuildMember.hasPermission(Permission.PLAYER_JOIN_CHECK));
                    reopen();
                }
            });
            guiBuilder.item(getToggleItem(thisGUISection.getConfigurationSection("items.manage_guild_upgrade"), Permission.GUILD_UPGRADE), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(Permission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(Permission.GUILD_UPGRADE, !targetGuildMember.hasPermission(Permission.GUILD_UPGRADE));
                    reopen();
                }
            });
        }

        return guiBuilder.build();
    }

    /**
     * 重开GUI如果自己无权限或者对方有同样的权限了
     * @param permission
     * @return
     */
    private boolean reopenIfInvalid(Permission permission) {
        if (!guildMember.hasPermission(permission) || guildMember.hasPermission(permission)) {
            reopen();
            return false;
        }

        return true;
    }

    /**
     * 得到物品（give，take两种状态）
     * @param section
     */
    private PriorityItem getToggleItem(ConfigurationSection section, Permission permission) {
        return GUIItemManager.getPriorityItem(section.getConfigurationSection(targetGuildMember.hasPermission(permission) ? "take" : "give"), targetGuildMember);
    }

    @Override
    public boolean canUse() {
        if (!guildMember.isValid() || !targetGuildMember.isValid()) {
            return false;
        }

        Set<Permission> permissions = guildMember.getPermissions(); // 最新的权限

        return permissions.contains(Permission.MEMBER_KICK) || permissions.contains(Permission.MANAGE_PERMISSION);
    }
}
