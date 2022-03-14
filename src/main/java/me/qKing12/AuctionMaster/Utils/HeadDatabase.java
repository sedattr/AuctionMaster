package me.qKing12.AuctionMaster.Utils;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.qKing12.AuctionMaster.AuctionMaster;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeadDatabase implements Listener {
    static HeadDatabaseAPI headApi = new HeadDatabaseAPI();

    public HeadDatabase(){
        Bukkit.getPluginManager().registerEvents(this, AuctionMaster.plugin);
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e){
        headApi = new HeadDatabaseAPI();
        AuctionMaster.plugin.getLogger().info("HeadDatabase detected.");
    }
}
