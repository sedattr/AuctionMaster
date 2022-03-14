package me.qKing12.AuctionMaster.AuctionObjects;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class SortingObject {
    private final HashMap<Player, Integer> sortCache = new HashMap<>();
    private final HashMap<Player, Integer> sortBINCache = new HashMap<>();

    private final ItemStack sortItem;
    private final ItemStack sortItemBIN;

    private final String highestBid;
    private final String lowestBid;
    private final String endingSoon;
    private final String mostBids;
    private final String clickToSwitch;

    private final String allAuctions;
    private final String binOnly;
    private final String auctionsOnly;
    private final String clickToSwitchBIN;

    public SortingObject(){
        sortItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.auctionsManagerCfg.getString("sort-auction-item"), Utils.chat(AuctionMaster.auctionsManagerCfg.getString("sort-auction-item-name")), null);
        sortItemBIN= AuctionMaster.itemConstructor.getItem(AuctionMaster.buyItNowCfg.getString("sort-item.material"), Utils.chat(AuctionMaster.buyItNowCfg.getString("sort-item.name")),null);

        highestBid= Utils.chat(AuctionMaster.auctionsManagerCfg.getString("sort-auction-item-sorting.highest-bid"));
        lowestBid= Utils.chat(AuctionMaster.auctionsManagerCfg.getString("sort-auction-item-sorting.lowest-bid"));
        endingSoon= Utils.chat(AuctionMaster.auctionsManagerCfg.getString("sort-auction-item-sorting.ending-soon"));
        mostBids= Utils.chat(AuctionMaster.auctionsManagerCfg.getString("sort-auction-item-sorting.most-bids"));
        clickToSwitch= Utils.chat(AuctionMaster.auctionsManagerCfg.getString("sort-auction-item-sorting.click-to-switch"));

        allAuctions= Utils.chat(AuctionMaster.buyItNowCfg.getString("sort-item.show-all"));
        binOnly= Utils.chat(AuctionMaster.buyItNowCfg.getString("sort-item.bin-only"));
        auctionsOnly= Utils.chat(AuctionMaster.buyItNowCfg.getString("sort-item.auctions-only"));
        clickToSwitchBIN= Utils.chat(AuctionMaster.buyItNowCfg.getString("sort-item.click-to-switch"));

    }

    public ItemStack getSortItem(Player p){
        int sort = sortCache.getOrDefault(p, 0);
        ItemStack toReturn = sortItem.clone();
        ItemMeta meta =toReturn.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add((sort==0? Utils.chat("&b► "): Utils.chat("&7"))+highestBid);
        lore.add((sort==1? Utils.chat("&b► "): Utils.chat("&7"))+lowestBid);
        lore.add((sort==2? Utils.chat("&b► "): Utils.chat("&7"))+endingSoon);
        lore.add((sort==3? Utils.chat("&b► "): Utils.chat("&7"))+mostBids);
        lore.add("");
        lore.add(clickToSwitch);
        meta.setLore(lore);
        toReturn.setItemMeta(meta);
        return toReturn;
    }

    public ItemStack getSortItemBIN(Player p){
        int sort = sortBINCache.getOrDefault(p, 0);
        ItemStack toReturn = sortItemBIN.clone();
        ItemMeta meta =toReturn.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add((sort==0? Utils.chat("&3► "): Utils.chat("&7"))+allAuctions);
        lore.add((sort==1? Utils.chat("&3► "): Utils.chat("&7"))+auctionsOnly);
        lore.add((sort==2? Utils.chat("&3► "): Utils.chat("&7"))+binOnly);
        lore.add("");
        lore.add(clickToSwitchBIN);
        meta.setLore(lore);
        toReturn.setItemMeta(meta);
        return toReturn;
    }

    public void changeSort(Player p){
        int sort=sortCache.getOrDefault(p, 0);
        if(sort==3)
            sortCache.put(p, 0);
        else
            sortCache.put(p, sort+1);
    }

    public void changeSortBIN(Player p){
        int sort=sortBINCache.getOrDefault(p, 0);
        if(sort==2)
            sortBINCache.put(p, 0);
        else
            sortBINCache.put(p, sort+1);
    }

    public int getSortIndex(Player p){
        return sortCache.getOrDefault(p, 0);
    }

    public int getSortIndexBIN(Player p){
        return sortBINCache.getOrDefault(p, 0);
    }

}
