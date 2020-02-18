package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CommonItem {
    public static final ItemStack NEXT_PAGE = new ItemBuilder().material(Material.BOOK).displayName("&c下一页").colored().build();
    public static final ItemStack PREVIOUS_PAGE = new ItemBuilder().material(Material.BOOK).displayName("&c上一页").colored().build();
    public static final ItemStack BACK_TO_MAIN = new ItemBuilder().material(Material.BOOK_AND_QUILL).displayName("&c返回至主界面").colored().build();
    public static final ItemStack BACK = new ItemBuilder().material(Material.BOOK_AND_QUILL).displayName("&c返回").colored().build();
}
