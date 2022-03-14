package me.qKing12.AuctionMaster.Currency;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class VaultImpl implements Currency{
    private Economy econ;

    public boolean hasMoney(Player p, double money){
        try {
            return econ.has(p, money);
        }catch(Exception x){
            econ=getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            return econ.has(p, money);
        }
    }

    public boolean removeMoney(Player p, double money){
        try {
            if (hasMoney(p, money)) {
                econ.withdrawPlayer(p, money);
                return true;
            }
            return false;
        }catch(Exception x){
            econ=getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            if (hasMoney(p, money)) {
                econ.withdrawPlayer(p, money);
                return true;
            }
            return false;
        }
    }

    @Override
    public void addMoney(Player p, double money) {
        try {
            econ.depositPlayer(p, money);
        }catch(Exception x) {
            econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            econ.depositPlayer(p, money);
        }
    }

    public VaultImpl() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
        }
    }
}
