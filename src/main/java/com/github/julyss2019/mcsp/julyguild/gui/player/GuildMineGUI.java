package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseMemberGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatFilter;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuildMineGUI extends BaseMemberGUI {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildMineGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYamlConfig().getConfigurationSection("GuildMineGUI");
    private final Player bukkitPlayer;
    private final Permission permission;

    public GuildMineGUI(GuildMember guildMember) {
        super(GUIType.MINE, guildMember);

        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
        this.permission = guildMember.getPermission();
    }

    @Override
    public Inventory getGUI() {
        List<Integer> positions = new ArrayList<>(); // 可供填充的位置

        try {
            for (String configPosStr : thisGUISection.getString("positions").split(",")) {
                String[] range = configPosStr.split("-"); // 范围界定符

                if (range.length == 1) {
                    positions.add(Integer.parseInt(range[0]));
                } else if (range.length == 2) {
                    for (int i = Integer.parseInt(range[0]); i <= Integer.parseInt(range[1]); i++) {
                        positions.add(i);
                    }
                } else {
                    throw new RuntimeException("位置不合法");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("位置不合法", e.getCause());
        }

        PriorityConfigGUI.Builder guiBuilder = (PriorityConfigGUI.Builder) new PriorityConfigGUI.Builder(positions)
                .fromConfig(thisGUISection, bukkitPlayer)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();
                        new MainGUI(guildPlayer).open();
                    }
                });


        guiBuilder
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_info"), new Placeholder.Builder().addGuildPlaceholders(guild).build(), bukkitPlayer))
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.self_info"), bukkitPlayer))
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_members." + guildMember.getPermission().name().toLowerCase()), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildMemberGUI(guildMember).open();
                    }
                })
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_donate"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildDonateGUI(guildMember).open();
                    }
                })
                .item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_upgrade"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildUpgradeGUI(guildMember).open();
                    }
                });



        // 公会公告
        PriorityItem guildAnnouncementItem = GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items._guild_announcements"), bukkitPlayer);

        guildAnnouncementItem.getItemBuilder().lores(guild.getAnnouncements());
        guiBuilder.item(guildAnnouncementItem);


        // 解散或退出
        if (permission == Permission.OWNER) {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_dismiss"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("dismiss.confirm"), new Placeholder.Builder()
                    .add("{WAIT}", MainSettings.getDismissWait())
                    .add("{CONFIRM_STR}", MainSettings.getDismissConfirmStr()).build());
                    JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                        @Override
                        public void onChat(AsyncPlayerChatEvent event) {
                            JulyChatFilter.unregisterChatFilter(bukkitPlayer);

                            if (event.getMessage().equalsIgnoreCase(MainSettings.getDismissConfirmStr())) {
                                guild.delete();
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("dismiss.success"));
                            } else {
                                Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("dismiss.failed"));
                            }
                        }

                        @Override
                        public void onTimeout() {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("dismiss.timeout"));
                        }
                    }, MainSettings.getDismissWait());
                }
            });
        } else {
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_exit"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {

                }
            });
        }

        return guiBuilder.build();
    }
}
