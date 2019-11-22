package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerPageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.naming.ldap.PagedResultsControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuildMemberGUI extends BasePlayerPageableGUI {
    private enum ViewerType {PLAYER, MANAGER}
    private static final List<Permission> managerPermissions = Arrays.asList(Permission.MEMBER_KICK, Permission.MEMBER_SET_ADMIN, Permission.MEMBER_SET_PERMISSION);
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final ConfigurationSection thisGUISection = plugin.getGuiYamlConfig().getConfigurationSection("GuildMemberGUI");
    private final ViewerType viewerType;
    private final Guild guild;
    private final Player bukkitPlayer = getBukkitPlayer();
    private final List<GuildMember> guildMembers = new ArrayList<>();

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

        guildMembers.addAll(guild.getMembers());
    }

    @Override
    public Inventory getGUI() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection.getConfigurationSection(viewerType.name().toLowerCase()), bukkitPlayer);


        return guiBuilder.build();
    }

    @Override
    public int getTotalPage() {
        return 0;
    }
}
