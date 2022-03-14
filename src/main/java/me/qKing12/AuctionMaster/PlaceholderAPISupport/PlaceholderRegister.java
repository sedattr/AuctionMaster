package me.qKing12.AuctionMaster.PlaceholderAPISupport;

import me.qKing12.AuctionMaster.AuctionMaster;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.ArrayList;

/**
 * This class will automatically register as a placeholder expansion
 * when a jar including this class is added to the directory
 * {@code /plugins/PlaceholderAPI/expansions} on your server.
 * <br>
 * <br>If you create such a class inside your own plugin, you have to
 * register it manually in your plugins {@code onEnable()} by using
 * {@code new YourExpansionClass().register();}
 */
public class PlaceholderRegister extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return "qKing12";
    }

    @Override
    public String getIdentifier(){
        return "auctionmaster";
    }

    @Override
    public String getVersion(){
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){

        if(identifier.equals("own_auctions")){
            return String.valueOf(AuctionMaster.auctionsHandler.ownAuctions.getOrDefault(player.getUniqueId().toString(), new ArrayList<>()).size());
        }
        else if(identifier.equals("own_bids")){
            return String.valueOf(AuctionMaster.auctionsHandler.bidAuctions.getOrDefault(player.getUniqueId().toString(), new ArrayList<>()).size());
        }


        return null;
    }
}
