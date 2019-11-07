package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.inventory.Inventory;

public class GuildMemberPlayerPlayerPageableGUI extends BasePlayerPageableGUI {
    private Guild guild;
    private Inventory inventory;
    private GUI lastGUI;

    public GuildMemberPlayerPlayerPageableGUI(GuildPlayer guildPlayer, Guild guild) {
        super(GUIType.MEMBER, guildPlayer);

        this.guild = guild;
        setCurrentPage(0);
    }

    public GuildMemberPlayerPlayerPageableGUI(GuildPlayer guildPlayer, Guild guild, GUI lastGUI) {
        this(guildPlayer, guild);

        this.lastGUI = lastGUI;
    }

    @Override
    public void setCurrentPage(int page) {
        super.setCurrentPage(page);

/*        InventoryBuilder inventoryBuilder = new InventoryBuilder().title(ConfigHandler.getString("GuildMemberPlayerPlayerPageableGUI.title").replace("%name%", guild.getName()).replace("%page%", String.valueOf(page))).colored().row(6);

        inventoryBuilder.item(53, CommonItem.BACK, new ItemListener() {
            @Override
            public void onClicked(InventoryClickEvent event) {
                close();

                if (guild.isValid()) {
                    if (lastGUI != null) {
                        lastGUI.build();
                        lastGUI.open();
                    } else {
                        new GuildInfoPlayerGUI(guildPlayer, guild).open();
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

        Guild.getSortedMembers(members);

        int memberSize = members.size();
        int itemCounter = page * 51;
        int loopCount = memberSize - itemCounter < 51 ? memberSize - itemCounter : 51;

        for (int i = 0; i < loopCount; i++) {
            GuildMember member = members.getIndexItem(itemCounter++);
            Permission permission = member.getPermission();
            String memberName = member.getName();

            inventoryBuilder.item(i, new SkullItemBuilder()
                    .owner(memberName)
                    .displayName(ConfigHandler.getString("GuildMemberPlayerPlayerPageableGUI.member.display_name"))
                    .addLore(Permission.getChineseName(permission))
                    .addLore("")
                    .addLore("&e金币贡献 &b▹ &e" + member.getDonatedMoney())
                    .addLore("&d点券贡献 &b▹ &d" + member.getDonatedPoints())
                    .addLore("&9入宗时间 &b▹ &9" + Util.YMD_SDF.format(member.getJoinTime()))
                    .colored()
                    .build());
        }

        this.inventory = inventoryBuilder.build();*/
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
