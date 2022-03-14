package me.qKing12.AuctionMaster.ItemConstructor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface ItemConstructor {

    ItemStack getItem(Material material, String name, ArrayList<String> lore);

    ItemStack getItem(Material material, short data, String name, ArrayList<String> lore);

    ItemStack getItem(String material, String name, ArrayList<String> lore);

    ItemStack getItem(ItemStack item, String name, ArrayList<String> lore);

    ItemStack getItemFromMaterial(String material);

}
