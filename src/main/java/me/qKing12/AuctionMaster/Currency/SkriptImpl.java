package me.qKing12.AuctionMaster.Currency;

import ch.njol.skript.variables.Variables;
import org.bukkit.entity.Player;

import static me.qKing12.AuctionMaster.AuctionMaster.currencyCfg;

public class SkriptImpl implements Currency {

    public boolean hasMoney(Player p, double money){
            Object variable = Variables.getVariable(currencyCfg.getString("skript-variable").replace("%player%", p.getName().toLowerCase()).replace("%player's uuid%", p.getUniqueId().toString()), null, false);
            try {
                return (long) variable >= (long) money;
            }catch(Exception e){
                return !((double) variable < money);
            }
    }

    public boolean removeMoney(Player p, double money){
        if(hasMoney(p, money)) {
            Object variable = Variables.getVariable(currencyCfg.getString("skript-variable").replace("%player%", p.getName().toLowerCase()).replace("%player's uuid%", p.getUniqueId().toString()), null, false);
            try {
                long setVar = (long) variable - (long) money;
                Variables.setVariable(currencyCfg.getString("skript-variable").replace("%player%", p.getName().toLowerCase()).replace("%player's uuid%", p.getUniqueId().toString()), setVar, null, false);
            } catch (Exception e) {
                double setVar = (double) variable - money;
                Variables.setVariable(currencyCfg.getString("skript-variable").replace("%player%", p.getName().toLowerCase()).replace("%player's uuid%", p.getUniqueId().toString()), setVar, null, false);
            }
            return true;
        }
        return false;
    }

    public void addMoney(Player p, double money) {
        Object variable = Variables.getVariable(currencyCfg.getString("skript-variable").replace("%player%", p.getName().toLowerCase()).replace("%player's uuid%", p.getUniqueId().toString()), null, false);
        try {
            long setVar = (long)money + (long)variable;
            Variables.setVariable(currencyCfg.getString("skript-variable").replace("%player%", p.getName().toLowerCase()).replace("%player's uuid%", p.getUniqueId().toString()), setVar, null, false);
        } catch (Exception e) {
            double setVar = money + (double) variable;
            Variables.setVariable(currencyCfg.getString("skript-variable").replace("%player%", p.getName().toLowerCase()).replace("%player's uuid%", p.getUniqueId().toString()), setVar, null, false);
        }

    }
}
