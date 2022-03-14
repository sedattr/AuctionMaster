package me.qKing12.AuctionMaster.API.Events;

import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OutbidEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final Player outbidder;
    private final Player player;
    private final Auction auction;
    private final double bidAmount;

    public OutbidEvent(Player outbidder, Player player, Auction auction, double bidAmount) {
        this.outbidder = outbidder;
        this.player = player;
        this.auction=auction;
        this.bidAmount=bidAmount;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Player getOutbidder() {
        return this.outbidder;
    }

    public Auction getAuction() {
        return this.auction;
    }

    public double getBidAmount() {
        return this.bidAmount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
