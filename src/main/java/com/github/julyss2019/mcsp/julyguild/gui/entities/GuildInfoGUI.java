package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.*;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.Permission;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.guild.requests.GuildRequestType;
import com.github.julyss2019.mcsp.julyguild.guild.requests.JoinGuildRequest;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;


/**
 * 查看公会成员，申请加入公会
 */
public class GuildInfoGUI extends BasePlayerGUI {
    private final Player bukkitPlayer;
    private final Guild guild;
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildInfoGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildInfoGUI");

    public GuildInfoGUI(GuildPlayer guildPlayer, Guild guild, @Nullable GUI lastGUI) {
        super(GUIType.INFO, guildPlayer, lastGUI);

        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
        this.guild = guild;
    }

    @Override
    public Inventory getInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer, guild)
                .item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.request_join"), bukkitPlayer, guild), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();

                        if (guild.hasRequest(guildPlayer, GuildRequestType.JOIN)) {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("request_join.already_have"));
                            return;
                        }

                        if (guild.getMemberCount() >= guild.getMaxMemberCount()) {
                            Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("request_join.guild_full"), new Placeholder.Builder().addInner("max", guild.getMaxMemberCount()).build());
                            return;
                        }

                        guild.addRequest(JoinGuildRequest.createNew(guildPlayer));
                        Util.sendColoredMessage(bukkitPlayer, thisLangSection.getString("request_join.success"), new Placeholder.Builder().addGuildPlaceholders(guild).build());

                        for (GuildMember member : guild.getMembers()) {
                            if (member.isOnline() && member.hasPermission(Permission.PLAYER_CHECK)) {

                            }
                        }
                    }
                }).item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.members"), bukkitPlayer, guild), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildMemberListGUI(guild, guildPlayer, GuildInfoGUI.this).open();
                    }
                }).item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer, guild), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        back();
                    }
                });

        return guiBuilder.build();
    }

    public Guild getGuild() {
        return guild;
    }
}
