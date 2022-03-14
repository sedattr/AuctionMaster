package me.qKing12.AuctionMaster.AuctionObjects.Categories;

import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface Category {

    ArrayList<Auction> getAuctionsCoins();
    ArrayList<Auction> getAuctionsTime();
    ArrayList<Auction> getAuctionsBids();
    ArrayList<Auction> getAuctions(Player p);
    ItemStack getDisplay();
    ItemStack getDisplayCategoryItemSelected();
    int getSlot();
    ItemStack getBackgroundGlass();

    boolean removeFromCategory(Auction auction);
}
