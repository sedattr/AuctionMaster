package me.qKing12.AuctionMaster.AuctionObjects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Auction {
    boolean isBIN();

     boolean checkForDeletion();

     boolean forceEnd();

     void addMinutesToAuction(int minutes);

     void setEndingDate(long date);

     void adminRemoveAuction(boolean withDeliveries);

 boolean sellerClaim(Player player);

     void claimBid(Player player);

     void topBidClaim(Player player);

     void normalBidClaim(Player player);

     boolean placeBid(Player player, double amount, int cacheBids);

     Bids.Bid getLastBid(String uuid);

     boolean isEnded();

     ItemStack getBidHistory();

     ItemStack generateDisplay();

     ItemStack getUpdatedDisplay();

     String getSellerUUID();

     String getSellerDisplayName();

     String getSellerName();

     String getId();

     Bids getBids();

     long getEndingDate();

     ItemStack getItemStack();

     String getDisplayName();

     double getCoins();
}
