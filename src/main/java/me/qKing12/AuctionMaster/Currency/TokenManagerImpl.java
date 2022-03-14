package me.qKing12.AuctionMaster.Currency;

import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TokenManagerImpl implements Currency {
    private final TokenManager tokenManager;

    public boolean hasMoney(Player p, double money) {
        final long balance = tokenManager.getTokens(p).orElse(0);
        return balance >= money;
    }

    public boolean removeMoney(Player p, double money) {
        if(hasMoney(p, money)) {
            tokenManager.removeTokens(p, (long) money);
            return true;
        }
        return false;
    }

    public void addMoney(Player p, double money) {
        tokenManager.addTokens(p, (long)money);
    }

    public TokenManagerImpl() {
        tokenManager = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
    }
}
