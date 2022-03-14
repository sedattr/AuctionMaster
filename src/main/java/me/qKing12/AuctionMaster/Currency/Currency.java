package me.qKing12.AuctionMaster.Currency;

import org.bukkit.entity.Player;

public interface Currency {

    boolean hasMoney(Player p, double money);

    void addMoney(Player p, double money);

    boolean removeMoney(Player p, double money);

}
