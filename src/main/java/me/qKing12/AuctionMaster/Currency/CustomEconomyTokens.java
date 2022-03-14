package me.qKing12.AuctionMaster.Currency;

import com.itzrozzadev.customeconomy.api.CustomEconomyAPI;
import com.itzrozzadev.customeconomy.api.CustomEconomyCache;
import org.bukkit.entity.Player;

public class CustomEconomyTokens implements Currency{

    public boolean hasMoney(Player p, double money){
        CustomEconomyCache econ = CustomEconomyAPI.getCache(p);
        try {
            return econ.getTokens()>=money;
        }catch(Exception x){
            return false;
        }
    }

    public boolean removeMoney(Player p, double money){
        CustomEconomyCache econ = CustomEconomyAPI.getCache(p);
        try {
            if (hasMoney(p, money)) {
                econ.subTokens(money);
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
            econ.addTokens(money);
        }catch(Exception x) {

        }
    }

}
