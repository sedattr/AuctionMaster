package me.qKing12.AuctionMaster.InputGUIs;

import me.qKing12.AuctionMaster.AuctionMaster;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class ChatListener {

    private final Player p;
    private final ListenUp listener;

    private boolean listening;

    private chatHandler handler;

    public ChatListener(Player p, chatHandler handler){
        this.p=p;
        this.listener=new ListenUp();
        Bukkit.getPluginManager().registerEvents(listener, AuctionMaster.plugin);
        this.listening=true;
        this.handler=handler;
        Bukkit.getScheduler().runTaskLater(AuctionMaster.plugin, () -> {
            if(this.listening){
                HandlerList.unregisterAll(listener);
                this.handler=null;
                p.sendMessage(utilsAPI.chat(p, AuctionMaster.plugin.getConfig().getString("input-guis.chat-listener-end-message")));
            }
        }, 200);
    }

    private class ListenUp implements Listener {

        @EventHandler
        public void chatListener(AsyncPlayerChatEvent e){
            if(e.getPlayer().equals(p)){
                e.setCancelled(true);
                HandlerList.unregisterAll(listener);
                listening=false;
                handler.onChat(e.getMessage());

                handler=null;

            }
        }
    }

    public interface chatHandler{
        void onChat(String input);
    }
}
