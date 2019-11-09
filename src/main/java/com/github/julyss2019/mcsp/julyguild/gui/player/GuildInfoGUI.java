package com.github.julyss2019.mcsp.julyguild.gui.player;

import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.CommonItem;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildAdmin;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.request.GuildRequestType;
import com.github.julyss2019.mcsp.julyguild.guild.request.JoinGuildRequest;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import com.github.julyss2019.mcsp.julylibrary.item.SkullItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class GuildInfoGUI extends BasePlayerGUI {
    private Player bukkitPlayer;
    private Guild guild;
    private Inventory inventory;
    private int lastPage;

    public GuildInfoGUI(GuildPlayer guildPlayer, Guild guild) {
        this(guildPlayer, guild, 0);
    }

    public GuildInfoGUI(GuildPlayer guildPlayer, Guild guild, int lastPage) {
        super(GUIType.INFO, guildPlayer);

        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
        this.guild = guild;
        this.lastPage = lastPage;
        build();
    }


    public void build() {

        List<String> memberLores = new ArrayList<>();
        List<GuildMember> guildMembers = guild.getSortedMembers();

        for (GuildMember guildMember : guildMembers) {
            if (memberLores.size() < 10) {
                //memberLores.add(ConfigHandler.getNickName(guildMember));
            } else {
                break;
            }
        }

        InventoryBuilder inventoryBuilder = new InventoryBuilder().title("&f" + guild.getName()).colored().row(3)
                .item(1, 3, new SkullItemBuilder()
                        .owner("Notch")
                        .displayName("&f宗门成员")
                        .addLore("&b>> &a点击查看详细信息")
                        .addLore("")
                        .addLores(memberLores)
                        .addLore(guild.getMemberCount() > 10 ? "&7和 &e" + (guild.getMemberCount() - 10) + "个 &7成员..." : null)
                        .enchant(Enchantment.DURABILITY, 1)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        .colored().build(), new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                            close();
                            //new GuildMemberGUI(guildPlayer, guild).open();
                    }
                })
                .item(1, 5, new ItemBuilder()
                        .material(Material.ITEM_FRAME)
                        .displayName("&f宗门公告")
                        .colored()
                        .lores(guild.getAnnouncements())
                        .enchant(Enchantment.DURABILITY, 1)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS).build()
                )

                .item(2, 8, CommonItem.BACK_TO_MAIN, new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        MainGUI gui = new MainGUI(guildPlayer);

                        gui.setCurrentPage(gui.isValidPage(lastPage) ? lastPage : 0);
                        gui.open();
                    }
                });


        if (!guildPlayer.isInGuild()) {
                inventoryBuilder.item(1, 4, new ItemBuilder()
                        .material(Material.MAGMA_CREAM)
                        .displayName("&a申请加入宗门")
                        .colored()
                        .enchant(Enchantment.DURABILITY, 1)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        .build(), new ItemListener() {
                    @Override
                    public void onClicked(InventoryClickEvent event) {
                        close();

                        if (guild.hasRequest(guildPlayer, GuildRequestType.JOIN)) {
                            Util.sendColoredMessage(bukkitPlayer, "&c你已经有一个申请加入请求了, 请等待审批.");
                            return;
                        }

                        if (guild.getMemberCount() >= guild.getMaxMemberCount()) {
                            Util.sendColoredMessage(bukkitPlayer, "&c宗门人数已满(" + guild.getMemberCount() + "/" + guild.getMaxMemberCount() + ").");
                            return;
                        }

                        guild.addRequest(JoinGuildRequest.createNew(guildPlayer));
                        Util.sendColoredMessage(bukkitPlayer, "&d已向 &e" + guild.getName() + " &d宗门发送加入申请, 请等待审核!");

                        for (GuildMember guildMember : guild.getMembers()) {
                            if (guildMember instanceof GuildAdmin && guildMember.isOnline()) {
                                Util.sendColoredMessage(guildMember.getGuildPlayer().getBukkitPlayer(), "&e你的宗门收到一个加入请求, 请及时处理!");
                            }
                        }
                    }
                });
        }

        this.inventory = inventoryBuilder.build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
