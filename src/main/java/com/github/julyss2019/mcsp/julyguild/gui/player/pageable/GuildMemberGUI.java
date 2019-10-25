package com.github.julyss2019.mcsp.julyguild.gui.player.pageable;

import com.github.julyss2019.mcsp.julyguild.config.Lang;
import com.github.julyss2019.mcsp.julyguild.gui.BasePageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildInfoGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.SkullItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class GuildMemberGUI extends BasePageableGUI {
    private Guild guild;
    private Inventory inventory;
    private GUI lastGUI;

    public GuildMemberGUI(GuildPlayer guildPlayer, Guild guild) {
        super(GUIType.MEMBER, guildPlayer);

        this.guild = guild;
        setCurrentPage(0);
    }

    public GuildMemberGUI(GuildPlayer guildPlayer, Guild guild, GUI lastGUI) {
        this(guildPlayer, guild);

        this.lastGUI = lastGUI;
    }

    @Override
    public void setCurrentPage(int page) {
        super.setCurrentPage(page);

        InventoryBuilder inventoryBuilder = new InventoryBuilder().title(Lang.get("GuildMemberGUI.title").replace("%name%", guild.getName()).replace("%page%", String.valueOf(page))).colored().row(6);

        inventoryBuilder.item(53, CommonItem.BACK, new ItemListener() {
            @Override
            public void onClicked(InventoryClickEvent event) {
                close();

                if (guild.isValid()) {
                    if (lastGUI != null) {
                        lastGUI.build();
                        lastGUI.open();
                    } else {
                        new GuildInfoGUI(guildPlayer, guild).open();
                    }
                }
            }
        });

        if (getTotalPage() > 1) {
            inventoryBuilder.item(51, CommonItem.PREVIOUS_PAGE, new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    if (hasPrecious()) {
                        close();
                        previousPage();
                    }
                }
            });
            inventoryBuilder.item(52, CommonItem.NEXT_PAGE, new ItemListener() {
                @Override
                public void onClicked(InventoryClickEvent event) {
                    if (hasNext()) {
                        close();
                        nextPage();
                    }
                }
            });
        }

        List<GuildMember> members = guild.getMembers();

        Guild.sortMembers(members);

        int memberSize = members.size();
        int itemCounter = page * 51;
        int loopCount = memberSize - itemCounter < 51 ? memberSize - itemCounter : 51;

        for (int i = 0; i < loopCount; i++) {
            GuildMember member = members.get(itemCounter++);
            Permission permission = member.getPermission();
            String memberName = member.getName();

            inventoryBuilder.item(i, new SkullItemBuilder()
                    .owner(memberName)
                    .displayName(Lang.get("GuildMemberGUI.member.display_name"))
                    .addLore(Permission.getChineseName(permission))
                    .addLore("")
                    .addLore("&e金币贡献 &b▹ &e" + member.getDonatedMoney())
                    .addLore("&d点券贡献 &b▹ &d" + member.getDonatedPoints())
                    .addLore("&9入宗时间 &b▹ &9" + Util.YMD_SDF.format(member.getJoinTime()))
                    .colored()
                    .build());
        }

        this.inventory = inventoryBuilder.build();
    }

    @Override
    public int getTotalPage() {
        int memberSize = guild.getMemberCount();

        return memberSize == 0 ? 1 : memberSize % 51 == 0 ? memberSize / 51 : memberSize / 51 + 1;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
