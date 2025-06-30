package me.qKing12.AuctionMaster.AuctionObjects.Categories;

import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Global implements Category {

    ArrayList<Auction> orderedAuctionsMoney = new ArrayList<>();
    ArrayList<Auction> orderedAuctionsTime = new ArrayList<>();
    ArrayList<Auction> orderedAuctionsBids = new ArrayList<>();

    private final ItemStack backgroundGlass;
    private final int slot;
    private final ItemStack displayCategoryItem;
    private final ItemStack displayCategoryItemSelected;

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public ItemStack getDisplay() {
        return displayCategoryItem;
    }

    @Override
    public ItemStack getDisplayCategoryItemSelected() {
        return displayCategoryItemSelected;
    }

    @Override
    public ItemStack getBackgroundGlass() {
        return backgroundGlass;
    }

    @Override
    public ArrayList<Auction> getAuctionsBids() {
        return orderedAuctionsBids;
    }

    @Override
    public ArrayList<Auction> getAuctionsCoins() {
        return orderedAuctionsMoney;
    }

    @Override
    public ArrayList<Auction> getAuctionsTime() {
        return orderedAuctionsTime;
    }

    public ArrayList<Auction> getAuctions(Player p) {
        int sortIndex = AuctionMaster.auctionsHandler.sortingObject.getSortIndex(p);
        if (sortIndex == 0) {
            ArrayList<Auction> auctions = (ArrayList<Auction>) orderedAuctionsMoney.clone();
            Collections.reverse(auctions);
            return auctions;
        } else if (sortIndex == 1) {
            return orderedAuctionsMoney;
        } else if (sortIndex == 2) {
            return orderedAuctionsTime;
        } else if (sortIndex == 3) {
            return orderedAuctionsBids;
        }
        return null;
    }

    public Global() {
        backgroundGlass = AuctionMaster.itemConstructor.getItemFromMaterial("160:" + AuctionMaster.plugin.getConfig().getString("global-menu-color"));
        ItemMeta meta = backgroundGlass.getItemMeta();
        meta.setDisplayName(" ");
        backgroundGlass.setItemMeta(meta);

        displayCategoryItem = AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("global-category-item"));
        meta = displayCategoryItem.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(Utils.chat(AuctionMaster.plugin.getConfig().getString("global-category-name")));
        ArrayList<String> lore = new ArrayList<>();
        for (String line : AuctionMaster.plugin.getConfig().getStringList("global-category-lore"))
            lore.add(Utils.chat(line));
        lore.add(" ");
        lore.add(Utils.chat(AuctionMaster.plugin.getConfig().getString("category-no-browsing")));
        meta.setLore(lore);
        displayCategoryItem.setItemMeta(meta);

        lore.set(lore.size() - 1, Utils.chat(AuctionMaster.plugin.getConfig().getString("category-browsing")));
        meta.setLore(lore);
        displayCategoryItemSelected = displayCategoryItem.clone();
        displayCategoryItemSelected.setItemMeta(meta);
        displayCategoryItemSelected.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);

        slot = AuctionMaster.menusCfg.getInt("browsing-menu.global-slot");
    }

    public void sort() {
        orderedAuctionsMoney.sort(Comparator.comparing(Auction::getCoins));
        orderedAuctionsBids.sort(Comparator.comparing(Auction -> Auction.getBids().getNumberOfBids()));
        orderedAuctionsTime.sort(Comparator.comparing(Auction::getEndingDate));
        Collections.reverse(orderedAuctionsBids);
    }

    public ArrayList<Auction> getOrderedAuctionsBids() {
        return orderedAuctionsBids;
    }

    public ArrayList<Auction> getOrderedAuctionsMoney() {
        return orderedAuctionsMoney;
    }

    public ArrayList<Auction> getOrderedAuctionsTime() {
        return orderedAuctionsTime;
    }

    public boolean addToCategory(Auction auction) {

        if(!orderedAuctionsBids.contains(auction)) {
            orderedAuctionsBids.add(auction);
            orderedAuctionsMoney.add(auction);
            orderedAuctionsTime.add(auction);
            sort();
        }
        return true;

    }

    public boolean removeFromCategory(Auction auction){
        if(orderedAuctionsBids.contains(auction)) {
            orderedAuctionsBids.remove(auction);
            orderedAuctionsMoney.remove(auction);
            orderedAuctionsTime.remove(auction);
            return true;
        }
        return false;
    }

}
