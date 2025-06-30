package me.qKing12.AuctionMaster.Menus;

import me.qKing12.AuctionMaster.AuctionObjects.Auction;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.qKing12.AuctionMaster.AuctionMaster.*;

public class ManageOwnBidsMenu {

    private Inventory inventory;
    private Player player;
    private final ClickListen listener = new ClickListen();
    private final HashMap<Integer, Auction> auctions = new HashMap<>();
    private BukkitTask keepUpdated;
    private int goBackSlot;
    private int collectAllSlot=-1;

    private final ArrayList<Auction> toCollectAll=new ArrayList<>();

    private Boolean collectAll() {
        if (player == null)
            return false;

        if (!toCollectAll.isEmpty()) {
            for (Auction auction : toCollectAll)
                auction.claimBid(player);
            player.sendMessage(utilsAPI.chat(player, AuctionMaster.auctionsManagerCfg.getString("collect-all-message")));
        }

        player.closeInventory();
        return true;
    }

    private void keepUpdated(){
        keepUpdated=Bukkit.getScheduler().runTaskTimerAsynchronously(AuctionMaster.plugin, () -> {
            for (Map.Entry<Integer, Auction> entry : auctions.entrySet()) {
                try {
                    inventory.setItem(entry.getKey(), entry.getValue().getUpdatedDisplay());
                } catch (NullPointerException x) {
                    if (inventory != null)
                        x.printStackTrace();
                }
            }
        }, 20, 20);
    }

    public ManageOwnBidsMenu(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.player = player;

            ArrayList<Auction> auctions = AuctionMaster.auctionsHandler.bidAuctions.getOrDefault(player.getUniqueId().toString(), new ArrayList<>());

            int size = 2;
            size += auctions.size() / 7;
            if (auctions.size() % 7 > 0)
                size += 1;
            size *= 9;

            inventory = Bukkit.createInventory(player, size, utilsAPI.chat(player, configLoad.manageOwnBidsMenuName));

            int relativeSlot = size - 9;
            for (int i = 1; i < 8; i++) {
                inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());
                inventory.setItem(relativeSlot + i, AuctionMaster.configLoad.backgroundGlass.clone());
            }
            for (int i = 0; i < size; i += 9) {
                inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());
                inventory.setItem(i + 8, AuctionMaster.configLoad.backgroundGlass.clone());
            }

            int slot = 10;
            for (Auction auction : auctions) {
                inventory.setItem(slot, auction.getUpdatedDisplay());
                if (auction.isEnded())
                    toCollectAll.add(auction);
                this.auctions.put(slot, auction);
                if (slot % 9 == 7)
                    slot += 3;
                else
                    slot++;
            }
            keepUpdated();

            ArrayList<String> lore = new ArrayList<>();
            for (String line : AuctionMaster.configLoad.goBackLore)
                lore.add(utilsAPI.chat(player, line));
            inventory.setItem(goBackSlot = size - 5, itemConstructor.getItem(AuctionMaster.configLoad.goBackMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.goBackName), lore));

            if (toCollectAll.size() > 1) {
                double coinsToCollect = 0;
                int contor = 0;
                for (Auction auction : toCollectAll) {
                    if (auction.getBids().getTopBidUUID().equalsIgnoreCase(player.getUniqueId().toString()))
                        contor++;
                    else
                        coinsToCollect += auction.getLastBid(player.getUniqueId().toString()).getCoins();
                }
                collectAllSlot = size - 8;
                lore = new ArrayList<>();
                for (String line : AuctionMaster.configLoad.collectAllLoreBids)
                    lore.add(utilsAPI.chat(player, line
                            .replace("%auctions%", String.valueOf(toCollectAll.size()))
                            .replace("%coins%", AuctionMaster.numberFormatHelper.formatNumber(coinsToCollect))
                            .replace("%items%", String.valueOf(contor))
                    ));
                inventory.setItem(size - 8, itemConstructor.getItem(AuctionMaster.configLoad.collectAllMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.collectAllName), lore));
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getPluginManager().registerEvents(listener, AuctionMaster.plugin);
                player.openInventory(inventory);
            });
        });
    }

    public class ClickListen implements Listener {
        private Boolean singleClick = false;

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getInventory().equals(inventory)){
                e.setCancelled(true);
                if(e.getCurrentItem()==null || e.getCurrentItem().getType().equals(Material.AIR)) {
                    return;
                }
                if(e.getClickedInventory().equals(inventory)) {
                    if(e.getSlot() == goBackSlot) {
                        Utils.playSound(player, "go-back-click");
                        new MainAuctionMenu(player);
                    }
                    else if(auctions.containsKey(e.getSlot())){
                        new ViewAuctionMenu(player, auctions.get(e.getSlot()), "ownBids", 0, null);
                    }
                    else if(e.getSlot()==collectAllSlot){
                        if (singleClick)
                            return;
                        singleClick = true;

                        Utils.playSound(player, "claim-all-click");
                        singleClick = collectAll();
                    }
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(inventory.equals(e.getInventory())) {
                if (keepUpdated!=null) {
                    keepUpdated.cancel();
                }
                HandlerList.unregisterAll(this);
            }
        }
    }

}
