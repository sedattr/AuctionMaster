package me.qKing12.AuctionMaster.API.Events;

import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class TopBidClaim extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Auction auction;
    private final ItemStack itemWon;

    public TopBidClaim(Player player, Auction auction, ItemStack itemWon) {
        this.player = player;
        this.auction=auction;
        this.itemWon=itemWon;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Auction getAuction() {
        return this.auction;
    }

    public ItemStack getItemWon() {
        return this.itemWon;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
