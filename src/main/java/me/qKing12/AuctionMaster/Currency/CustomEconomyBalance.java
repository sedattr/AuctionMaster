package me.qKing12.AuctionMaster.Currency;

import com.itzrozzadev.customeconomy.api.CustomEconomyAPI;
import com.itzrozzadev.customeconomy.api.CustomEconomyCache;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class CustomEconomyBalance implements Currency{

    public boolean hasMoney(Player p, double money){
        CustomEconomyCache econ = CustomEconomyAPI.getCache(p);
        try {
            return econ.getBalance()>=money;
        }catch(Exception x){
            return false;
        }
    }

    public boolean removeMoney(Player p, double money){
        CustomEconomyCache econ = CustomEconomyAPI.getCache(p);
        try {
            if (hasMoney(p, money)) {
                econ.subBalance(money);
                return true;
            }
            return false;
        }catch(Exception x){
            return false;
        }
    }

    @Override
    public void addMoney(Player p, double money) {
        CustomEconomyCache econ = CustomEconomyAPI.getCache(p);
        try {
            econ.addBalance(money);
        }catch(Exception ignored) {

        }
    }

}
