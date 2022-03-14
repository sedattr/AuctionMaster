package me.qKing12.AuctionMaster.Currency;

import me.qKing12.AuctionMaster.AuctionMaster;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerPointsImpl implements Currency {
    private final PlayerPoints playerPoints;

    public boolean hasMoney(Player p, double money){
        int balance = playerPoints.getAPI().look(p.getUniqueId());
        return balance >= money;
    }

    public boolean removeMoney(Player p, double money){
        if(hasMoney(p, money)) {
            playerPoints.getAPI().take(p.getUniqueId(), (int) money);
            return true;
        }
        return false;
    }

    public void addMoney(Player p, double money){
            playerPoints.getAPI().give(p.getUniqueId(), (int)money);
    }

    public PlayerPointsImpl(){
            final Plugin plugin2 = AuctionMaster.plugin.getServer().getPluginManager().getPlugin("PlayerPoints");
            playerPoints = (PlayerPoints) plugin2;
    }
}
