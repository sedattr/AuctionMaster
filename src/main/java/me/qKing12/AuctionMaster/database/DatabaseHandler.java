package me.qKing12.AuctionMaster.database;

import me.qKing12.AuctionMaster.AuctionObjects.Auction;

import java.util.HashMap;

public interface DatabaseHandler {
    void deletePreviewItems(String id);
    void registerPreviewItem(String player, String item);
    void removePreviewItem(String player);
    void insertAuction(Auction auction);
    void updateAuctionField(String id, HashMap<String, String> toUpdate);
    boolean deleteAuction(String id);
    void addToOwnBids(String player, String toAdd);
    boolean removeFromOwnBids(String player, String toRemove);
    void resetOwnBids(String player);
    boolean removeFromOwnAuctions(String player, String toRemove);
    void resetOwnAuctions(String player);
    void addToOwnAuctions(String player, String toAdd);
}
