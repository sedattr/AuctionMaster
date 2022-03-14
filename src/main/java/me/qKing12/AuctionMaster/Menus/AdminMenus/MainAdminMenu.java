package me.qKing12.AuctionMaster.Menus.AdminMenus;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

import static me.qKing12.AuctionMaster.AuctionMaster.*;

public class MainAdminMenu {

    private Inventory inventory;
    private Player player;
    private final ClickListen listener = new ClickListen();

    public MainAdminMenu(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.player = player;
            inventory = Bukkit.createInventory(player, 36, Utils.chat("&8Admin Menu"));

            if (AuctionMaster.configLoad.useBackgoundGlass)
                for (int i = 0; i < 36; i++)
                    inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());

            inventory.setItem(10, AuctionMaster.configLoad.adminDelivery);
            inventory.setItem(12, AuctionMaster.configLoad.adminNpcCreate);
            inventory.setItem(13, AuctionMaster.configLoad.adminHelp);
            inventory.setItem(14, AuctionMaster.configLoad.adminDebugNames);
            inventory.setItem(16, AuctionMaster.configLoad.adminManageEndedAuctions);

            ArrayList<String> lore = new ArrayList<>();
            for (String line : AuctionMaster.configLoad.closeMenuLore)
                lore.add(utilsAPI.chat(player, line));
            inventory.setItem(31, itemConstructor.getItem(AuctionMaster.configLoad.closeMenuMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.closeMenuName), lore));

            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getPluginManager().registerEvents(listener, AuctionMaster.plugin);
                player.openInventory(inventory);
            });
        });
    }

    public class ClickListen implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getInventory().equals(inventory)){
                e.setCancelled(true);
                if(e.getCurrentItem()==null || e.getCurrentItem().getType().equals(Material.AIR)) {
                    return;
                }
                if(e.getClickedInventory().equals(inventory)) {
                    if (e.getSlot() == 31) {
                        player.closeInventory();
                    }
                    else if(e.getSlot()==10){
                        if(AuctionMaster.deliveries==null){
                            player.sendMessage(Utils.chat("&cDeliveries are disabled!"));
                            return;
                        }
                        new DeliveryAdminMenu(player, null);
                    }
                    else if(e.getSlot()==16){
                        new EndedAuctionsMenu(player, 0);
                    }
                    else if(e.getSlot()==12){
                        if(AuctionMaster.auctionNPC!=null)
                            AuctionMaster.auctionNPC.createNpc(player);
                        else
                            player.sendMessage(Utils.chat("&cYou either don't have Citizens Plugin or don't have auction-npc-use set to true in config.yml!"));
                        player.closeInventory();
                    }
                    else if(e.getSlot()==14){
                        if(AuctionMaster.auctionNPC!=null) {
                            AuctionMaster.auctionNPC.debugHolos();
                            player.sendMessage(Utils.chat("&aDone!"));
                        }
                        else
                            player.sendMessage(Utils.chat("&cYou either don't have Citizens Plugin or don't have auction-npc-use set to true in config.yml!"));
                        player.closeInventory();
                    }
                    else if(e.getSlot()==13){
                        player.sendMessage(Utils.chat("&7&m---------------------------------------"));
                        player.sendMessage(Utils.chat("&c/ahadmin createnpc &8- &7Creates a AuctionMaster NPC"));
                        player.sendMessage(Utils.chat("&c/ahadmin debugNames &8- &7Debug names of all NPCs"));
                        player.sendMessage(Utils.chat("&c/ahadmin delivery <player name> &8- &7Open Delivery Menu with a Selected Player"));
                        player.sendMessage(Utils.chat("&c/ahadmin manage &8- &7Open the Manage Auctions Menu"));
                        player.sendMessage(Utils.chat("&c/ahadmin manage ended &8- &7Open the Manage Auctions Menu for Ended Auctions"));
                        player.sendMessage(Utils.chat("&c/ahadmin transfer <p1> <p2> &8- &7Transfers all auction data from p1 to p2"));
                        player.sendMessage(Utils.chat("&c/ahadmin forceopen <player> &8- &7Force opens the auction menu to a player"));
                        player.sendMessage(Utils.chat("&c/ahadmin reload &8- &7Reloads the plugin"));
                        player.sendMessage(Utils.chat("&c/ahadmin give <player> <base64 item> &8- &7(Console Only) Advanced command, please read it''s use on spigot page"));
                        player.sendMessage(Utils.chat("&7&m---------------------------------------"));
                        player.closeInventory();
                    }
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(inventory.equals(e.getInventory())) {
                HandlerList.unregisterAll(this);
            }
        }
    }

}
