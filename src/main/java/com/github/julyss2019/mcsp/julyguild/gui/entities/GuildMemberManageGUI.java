package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.gui.BaseConfirmGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.GuildPermission;
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
        super(lastGUI, GUIType.MEMBER_MANAGE, mangerGuildMember.getGuildPlayer());

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

        // 自己有权限且对方无权限才能使用
        if (guild.isOwner(managerGuildMember) || managerGuildMember.hasPermission(GuildPermission.MEMBER_KICK) && !targetGuildMember.hasPermission(GuildPermission.MEMBER_KICK)) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.kick"), targetBukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(GuildPermission.MEMBER_KICK)) {
                        return;
                    }

                    new BaseConfirmGUI(GuildMemberManageGUI.this
                            , guildPlayer
                            , thisGUISection.getConfigurationSection("items.kick.BaseConfirmGUI")
                            , new PlaceholderContainer().add("target", targetGuildMember.getName())) {
                        @Override
                        public boolean canUse() {
                            return targetGuildMember.isValid() && managerGuildMember.isValid() && managerGuildMember.hasPermission(GuildPermission.MEMBER_KICK);
                        }

                        @Override
                        public void onCancel() {
                            back();
                        }

                        @Override
                        public void onConfirm() {
                            guild.removeMember(targetGuildMember);
                            guildPlayer.updateGUI(GUIType.MEMBER_LIST);
                            targetGuildMember.getGuildPlayer().updateGUI(GUIType.MAIN);
                            back();
                        }
                    }.open();
                }
            });
        }

        if (managerGuildMember.hasPermission(GuildPermission.MANAGE_PERMISSION)) {
            guiBuilder.item(getToggleItem(thisGUISection.getConfigurationSection("items.manage_member_kick"), GuildPermission.MEMBER_KICK), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(GuildPermission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(GuildPermission.MEMBER_KICK, !targetGuildMember.hasPermission(GuildPermission.MEMBER_KICK));
                    reopen();
                }
            });
            guiBuilder.item(getToggleItem(thisGUISection.getConfigurationSection("items.manage_set_member_damage"), GuildPermission.SET_MEMBER_DAMAGE), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(GuildPermission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(GuildPermission.SET_MEMBER_DAMAGE, !targetGuildMember.hasPermission(GuildPermission.SET_MEMBER_DAMAGE));
                    reopen();
                }
            });
            guiBuilder.item(getToggleItem(thisGUISection.getConfigurationSection("items.manage_player_join_check"), GuildPermission.PLAYER_JOIN_CHECK), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (reopenIfInvalid(GuildPermission.MANAGE_PERMISSION)) {
                        return;
                    }

                    targetGuildMember.setPermission(GuildPermission.PLAYER_JOIN_CHECK, !targetGuildMember.hasPermission(GuildPermission.PLAYER_JOIN_CHECK));
                    reopen();
                }
            });
        }

        return guiBuilder.build();
    }

    /**
     * 重开GUI如果自己无权限或者对方有同样的权限了
     * @param guildPermission
     * @return
     */
    private boolean reopenIfInvalid(GuildPermission guildPermission) {
        if (!managerGuildMember.hasPermission(guildPermission) || managerGuildMember.hasPermission(guildPermission)) {
            reopen();
            return false;
        }

        return true;
    }

    /**
     * 得到物品（give，take两种状态）
     * @param section
     */
    private PriorityItem getToggleItem(ConfigurationSection section, GuildPermission guildPermission) {
        return GUIItemManager.getPriorityItem(section.getConfigurationSection(targetGuildMember.hasPermission(guildPermission) ? "take" : "give"), targetBukkitPlayer);
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
