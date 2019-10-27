package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.gui.BasePageableGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.player.GuildManageGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.chat.JulyChatFilter;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import com.github.julyss2019.mcsp.julylibrary.item.SkullItemBuilder;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuildMemberManageGUI extends BasePageableGUI {
    private Inventory inventory;
    private Guild guild;
    private List<GuildMember> guildMembers = new ArrayList<>();

    public GuildMemberManageGUI(GuildPlayer guildPlayer) {
        super(GUIType.MEMBER_MANAGE, guildPlayer);

        this.guild = this.guildPlayer.getGuild();
        build();
    }

    @Override
    public void setCurrentPage(int page) {
        super.setCurrentPage(page);

        InventoryBuilder inventoryBuilder = new InventoryBuilder().title("&e&l宗门成员管理").colored().row(6)
                .listener(new InventoryListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        int index = event.getSlot() + getCurrentPage() * 51;

                        if (index < guildMembers.size()) {
                            GuildMember guildMember = guildMembers.get(index);
                            InventoryAction action = event.getAction();

                            if (action == InventoryAction.PICKUP_ALL) {
                                close();
                                Util.sendColoredMessage(bukkitPlayer, "&e管理员拥有的权限: 审批玩家.");
                                Util.sendColoredMessage(bukkitPlayer, "&c如果要将会员 &e" + guildMember.getName() + " &c设置为宗门管理员, 请在 &e10秒内 &c在聊天栏输入并发送: &econfirm");
                                JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                                    @Override
                                    public void onChat(AsyncPlayerChatEvent event) {
                                        event.setCancelled(true);
                                        JulyChatFilter.unregisterChatFilter(bukkitPlayer);

                                        if (event.getMessage().equalsIgnoreCase("confirm")) {
                                            guild.setMemberPermission(guildMember, Permission.ADMIN);
                                            Util.sendColoredMessage(bukkitPlayer, "&c已设置成员 &e" +guildMember.getName() + " &c为宗门管理员.");
                                        }
                                    }

                                    @Override
                                    public void onTimeout() {
                                        Util.sendColoredMessage(bukkitPlayer, "&c确认已超时.");
                                    }
                                }, 10);
                            } else if (action == InventoryAction.PICKUP_HALF) {
                                close();
                                Util.sendColoredMessage(bukkitPlayer, "&c如果要从宗门移出会员 &e" + guildMember.getName() + " &c, 请在 &e10秒内 &c在聊天栏输入并发送: &econfirm");
                                JulyChatFilter.registerChatFilter(bukkitPlayer, new ChatListener() {
                                    @Override
                                    public void onChat(AsyncPlayerChatEvent event) {
                                        event.setCancelled(true);
                                        JulyChatFilter.unregisterChatFilter(bukkitPlayer);

                                        if (event.getMessage().equalsIgnoreCase("confirm")) {
                                            guild.removeMember(guildMember);
                                            Util.sendColoredMessage(bukkitPlayer, "&c已移出会员 &e" +guildMember.getName() + "&c.");
                                        }
                                    }

                                    @Override
                                    public void onTimeout() {
                                        Util.sendColoredMessage(bukkitPlayer, "&c确认已超时.");
                                    }
                                }, 10);
                            }
                        }
                    }
                });

        inventoryBuilder.item(53, CommonItem.BACK, new ItemListener() {
            @Override
            public void onClicked(InventoryClickEvent event) {
                close();

                if (guild.isValid()) {
                    new GuildManageGUI(guildPlayer).open();
                }
            }
        });

        if (guild.getMemberCount() > 51) {
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


        Permission permission = guild.getMember(guildPlayer.getName()).getPermission();

        if (permission == Permission.OWNER) {
            this.guildMembers = guild.getMembers().stream().filter(guildMember -> guildMember.getPermission() == Permission.MEMBER || guildMember.getPermission() == Permission.ADMIN).collect(Collectors.toList());
        } else if (permission == Permission.ADMIN) {
            this.guildMembers = guild.getMembers().stream().filter(guildMember -> guildMember.getPermission() == Permission.MEMBER).collect(Collectors.toList());
        }

        Guild.sortMembers(guildMembers);

        int memberSize = guildMembers.size();
        int itemCounter = page * 51;
        int loopCount = memberSize - itemCounter < 51 ? memberSize - itemCounter : 51;

        for (int i = 0; i < loopCount; i++) {
            GuildMember member = guildMembers.get(itemCounter++);
            String memberName = member.getName();

            ItemBuilder itemBuilder = new SkullItemBuilder()
                    .owner(memberName)
                    .displayName("&f" + memberName)
                    .addLore("---")
                    .addLore("")
                    .addLore("&d左键 &b▹ &d任命管理员")
                    .addLore("&c右键 &b▹ &c移出宗门")
                    .addLore("")
                    .addLore("&e金币贡献 &b▹ &e¥" + member.getDonatedMoney())
                    .addLore("&a入宗时间 &b▹ &a" + Util.YMD_SDF.format(member.getJoinTime()))
                    .colored();

            inventoryBuilder.item(i, itemBuilder.build());
        }

        this.inventory = inventoryBuilder.build();
    }

    @Override
    public int getTotalPage() {
        int memberSize = guildMembers.size();

        return memberSize == 0 ? 1 : memberSize % 51 == 0 ? memberSize / 51 : memberSize / 51 + 1;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
