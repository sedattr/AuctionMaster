package me.qKing12.AuctionMaster.Menus;

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

public class MainAuctionMenu {

    private Inventory inventory;
    private Player player;
    private final ClickListen listener = new ClickListen();

    public MainAuctionMenu(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.player = player;
            inventory = Bukkit.createInventory(player, AuctionMaster.configLoad.mainMenuSize, utilsAPI.chat(player, AuctionMaster.configLoad.mainMenuName));

            if (AuctionMaster.configLoad.useBackgoundGlass)
                for (int i = 0; i < AuctionMaster.configLoad.mainMenuSize; i++)
                    inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());

                    ArrayList<String> lore = new ArrayList<>();
                    for (String line : AuctionMaster.configLoad.closeMenuLore)
                        lore.add(utilsAPI.chat(player, line));
                    if (AuctionMaster.menusCfg.getInt("main-menu.close-menu-slot") >= 0)
                        inventory.setItem(AuctionMaster.menusCfg.getInt("main-menu.close-menu-slot"), itemConstructor.getItem(AuctionMaster.configLoad.closeMenuMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.closeMenuName), lore));

            lore = new ArrayList<>();
            for (String line : AuctionMaster.configLoad.browsingMenuItemLore)
                lore.add(utilsAPI.chat(player, line));

            if (AuctionMaster.menusCfg.getInt("main-menu.browsing-menu-slot") >= 0)
            inventory.setItem(AuctionMaster.menusCfg.getInt("main-menu.browsing-menu-slot"), itemConstructor.getItem(AuctionMaster.configLoad.browsingMenuItemMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.browsingMenuItemName), lore));

            lore = new ArrayList<>();
            if (AuctionMaster.auctionsHandler.bidAuctions.containsKey(player.getUniqueId().toString())) {
                for (String line : AuctionMaster.configLoad.viewBidsMenuItemLoreWithBids)
                    lore.add(utilsAPI.chat(player, line
                            .replace("%total-top-bid%", String.valueOf(AuctionMaster.auctionsHandler.topBidsCount(player.getUniqueId().toString())))
                            .replace("%total-bids%", String.valueOf(AuctionMaster.auctionsHandler.totalBidsOnOtherAuctions(player.getUniqueId().toString())))
                    ));
            } else {
                for (String line : AuctionMaster.configLoad.viewBidsMenuItemLoreWithoutBids)
                    lore.add(utilsAPI.chat(player, line));
            }

            if (AuctionMaster.menusCfg.getInt("main-menu.view-bids-menu-slot") >= 0)
            inventory.setItem(AuctionMaster.menusCfg.getInt("main-menu.view-bids-menu-slot"), itemConstructor.getItem(AuctionMaster.configLoad.viewBidsMenuItemMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.viewBidsMenuItemName), lore));


            lore = new ArrayList<>();
            if (AuctionMaster.auctionsHandler.ownAuctions.containsKey(player.getUniqueId().toString())) {
                for (String line : AuctionMaster.configLoad.manageAuctionsItemLoreWithAuctions)
                    lore.add(utilsAPI.chat(player, line
                            .replace("%auctions%", String.valueOf(AuctionMaster.auctionsHandler.ownAuctions.get(player.getUniqueId().toString()).size()))
                            .replace("%bids%", String.valueOf(AuctionMaster.auctionsHandler.totalBidsOnOwnAuctions(player.getUniqueId().toString())))
                            .replace("%coins%", AuctionMaster.numberFormatHelper.formatNumber(AuctionMaster.auctionsHandler.totalCoinsOnOwnAuctions(player.getUniqueId().toString())))
                    ));
            } else {
                for (String line : AuctionMaster.configLoad.manageAuctionsItemLoreWithoutAuctions)
                    lore.add(utilsAPI.chat(player, line));
            }

            if (AuctionMaster.menusCfg.getInt("main-menu.manage-auctions-menu-slot") >= 0)
            inventory.setItem(AuctionMaster.menusCfg.getInt("main-menu.manage-auctions-menu-slot"), itemConstructor.getItem(AuctionMaster.configLoad.manageAuctionsItemMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.manageAuctionsItemName), lore));

            if (AuctionMaster.deliveries != null && AuctionMaster.menusCfg.getInt("main-menu.delivery-menu-slot")!=-1) {
                lore = new ArrayList<>();
                for (String line : AuctionMaster.configLoad.mainMenuDeliveryLore)
                    lore.add(utilsAPI.chat(player, line));

                if (AuctionMaster.menusCfg.getInt("main-menu.delivery-menu-slot") >= 0)
                inventory.setItem(AuctionMaster.menusCfg.getInt("main-menu.delivery-menu-slot"), itemConstructor.getItem(AuctionMaster.configLoad.mainMenuDeliveryItem, utilsAPI.chat(player, AuctionMaster.configLoad.mainMenuDeliveryName), lore));
            }

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
                    if (e.getSlot() == AuctionMaster.menusCfg.getInt("main-menu.manage-auctions-menu-slot")) {
                        Utils.playSound(player, "own-auctions-click");
                        if (!AuctionMaster.auctionsHandler.ownAuctions.containsKey(player.getUniqueId().toString()))
                            new CreateAuctionMainMenu(player);
                        else
                            new ManageOwnAuctionsMenu(player, 1);
                    }
                    else if(e.getSlot() == AuctionMaster.menusCfg.getInt("main-menu.browsing-menu-slot")){
                        Utils.playSound(player, "auction-browser-click");
                        new BrowsingAuctionsMenu(player, AuctionMaster.menusCfg.getString("browsing-menu.default-category"), 0, null);
                    }
                    else if(e.getSlot() == AuctionMaster.menusCfg.getInt("main-menu.view-bids-menu-slot")){
                        if(AuctionMaster.auctionsHandler.bidAuctions.containsKey(player.getUniqueId().toString())) {
                            Utils.playSound(player, "view-bids-has-click");
                            new ManageOwnBidsMenu(player);
                        }
                        else{
                            Utils.playSound(player, "view-bids-no-has-click");
                        }
                    }
                    else if(AuctionMaster.deliveries!=null && e.getSlot()== AuctionMaster.menusCfg.getInt("main-menu.delivery-menu-slot")){
                        Utils.playSound(player, "delivery-menu-click");
                        new DeliveryPlayerMenu(player, true);
                    }
                    else if(e.getSlot()== AuctionMaster.menusCfg.getInt("main-menu.close-menu-slot")){
                        Utils.playSound(player, "close-menu-click");
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
