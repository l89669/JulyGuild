package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildPermission;
import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.BaseConfirmGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class GuildMemberManageGUI extends BasePlayerGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildMemberManageGUI");
    private final Guild guild;
    private final GuildMember managerGuildMember;
    private final GuildMember targetGuildMember;
    private final Player targetBukkitPlayer;


    public GuildMemberManageGUI(@Nullable GUI lastGUI, @NotNull GuildMember mangerGuildMember, @NotNull GuildMember targetGuildMember) {
        super(lastGUI, Type.MEMBER_MANAGE, mangerGuildMember.getGuildPlayer());

        this.managerGuildMember = mangerGuildMember;
        this.targetGuildMember = targetGuildMember;
        this.targetBukkitPlayer = targetGuildMember.getGuildPlayer().getBukkitPlayer();
        this.guild = mangerGuildMember.getGuild();
    }

    @Override
    public Inventory createInventory() {
        PriorityConfigGUI.Builder guiBuilder = (PriorityConfigGUI.Builder) new PriorityConfigGUI.Builder()
                .fromConfig(thisGUISection, targetBukkitPlayer, new PlaceholderContainer().addGuildMemberPlaceholders(targetGuildMember))
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), targetBukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (canBack()) {
                            back();
                        }
                    }
                });

        // 如果管理者和被管理员一样
        if (managerGuildMember.equals(targetGuildMember)) {
            return guiBuilder.build();
        }

        // 是主人或自己有权限且对方无踢人权限
        if (guild.isOwner(managerGuildMember) || managerGuildMember.hasPermission(GuildPermission.MEMBER_KICK) && !targetGuildMember.hasPermission(GuildPermission.MEMBER_KICK)) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.member_kick"), targetBukkitPlayer, new PlaceholderContainer().addGuildMemberPlaceholders(targetGuildMember)), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (checkManagerPerOrReopen(GuildPermission.MEMBER_KICK)) {
                        return;
                    }

                    new BaseConfirmGUI(GuildMemberManageGUI.this
                            , guildPlayer
                            , thisGUISection.getConfigurationSection("items.member_kick.ConfirmGUI")
                            , new PlaceholderContainer().add("target", targetGuildMember.getName())) {
                        @Override
                        public boolean canUse() {
                            return targetGuildMember.isValid() && managerGuildMember.isValid()
                                    && (guild.isOwner(managerGuildMember) || (managerGuildMember.hasPermission(GuildPermission.MEMBER_KICK) && !targetGuildMember.hasPermission(GuildPermission.MEMBER_KICK)));
                        }

                        @Override
                        public void onCancel() {
                            back();
                        }

                        @Override
                        public void onConfirm() {
                            guild.removeMember(targetGuildMember);
                            back();
                        }
                    }.open();
                }
            });
        }

        if (managerGuildMember.hasPermission(GuildPermission.MANAGE_PERMISSION)) {
            guiBuilder.item(getPerToggleItem(thisGUISection.getConfigurationSection("items.per_member_kick"), GuildPermission.MEMBER_KICK), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!checkManagerPerOrReopen(GuildPermission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(GuildPermission.MEMBER_KICK, !targetGuildMember.hasPermission(GuildPermission.MEMBER_KICK));
                    reopen();
                }
            });
            guiBuilder.item(getPerToggleItem(thisGUISection.getConfigurationSection("items.per_set_member_damage"), GuildPermission.SET_MEMBER_DAMAGE), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!checkManagerPerOrReopen(GuildPermission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(GuildPermission.SET_MEMBER_DAMAGE, !targetGuildMember.hasPermission(GuildPermission.SET_MEMBER_DAMAGE));
                    reopen();
                }
            });
            guiBuilder.item(getPerToggleItem(thisGUISection.getConfigurationSection("items.per_player_join_check"), GuildPermission.PLAYER_JOIN_CHECK), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!checkManagerPerOrReopen(GuildPermission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(GuildPermission.PLAYER_JOIN_CHECK, !targetGuildMember.hasPermission(GuildPermission.PLAYER_JOIN_CHECK));
                    reopen();
                }
            });
            guiBuilder.item(getPerToggleItem(thisGUISection.getConfigurationSection("items.per_use_shop"), GuildPermission.USE_SHOP), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!checkManagerPerOrReopen(GuildPermission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(GuildPermission.USE_SHOP, !targetGuildMember.hasPermission(GuildPermission.USE_SHOP));
                    reopen();
                }
            });
            guiBuilder.item(getPerToggleItem(thisGUISection.getConfigurationSection("items.per_use_icon_repository"), GuildPermission.USE_ICON_REPOSITORY), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!checkManagerPerOrReopen(GuildPermission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(GuildPermission.USE_ICON_REPOSITORY, !targetGuildMember.hasPermission(GuildPermission.USE_ICON_REPOSITORY));
                    reopen();
                }
            });
        }

        return guiBuilder.build();
    }

    /**
     * 检查管理者权限，如果没权限则尝试重开GUI
     * @param guildPermission
     * @return
     */
    private boolean checkManagerPerOrReopen(@NotNull GuildPermission guildPermission) {
        if (!managerGuildMember.hasPermission(guildPermission) || targetGuildMember.hasPermission(guildPermission)) {
            reopen();
            return false;
        }

        return true;
    }

    /**
     * 得到权限状态物品（give，take两种状态）
     * @param section
     */
    private PriorityItem getPerToggleItem(@NotNull ConfigurationSection section, @NotNull GuildPermission guildPermission) {
        return GUIItemManager.getPriorityItem(section.getConfigurationSection(targetGuildMember.hasPermission(guildPermission) ? "take" : "give"), targetBukkitPlayer, new PlaceholderContainer().addGuildMemberPlaceholders(targetGuildMember));
    }

    @Override
    public boolean canUse() {
        if (!managerGuildMember.isValid() || !targetGuildMember.isValid()) {
            return false;
        }

        Set<GuildPermission> guildPermissions = managerGuildMember.getGuildPermissions(); // 最新的权限

        return guildPermissions.contains(GuildPermission.MEMBER_KICK) || guildPermissions.contains(GuildPermission.MANAGE_PERMISSION);
    }
}
