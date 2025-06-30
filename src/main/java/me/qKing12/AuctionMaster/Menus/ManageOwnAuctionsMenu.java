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

public class ManageOwnAuctionsMenu {

    private final Inventory inventory;
    private final Player player;
    private final ClickListen listener = new ClickListen();
    private final HashMap<Integer, Auction> auctions = new HashMap<>();
    private BukkitTask keepUpdated;
    private final int createMenuSlot;
    private int goBackSlot;
    private int goNextSlot;
    private final int pageNumber;
    private int collectAllSlot=-1;

    private final ArrayList<Auction> toCollectAll=new ArrayList<>();

    /*
        Creates the previous/next page buttons.

        Example: setupPage(true, pageNumber, size - 6);
     */
    private void setupPage(Boolean nextOrPrevious, Integer page, Integer slot){
        ArrayList<String> lore = new ArrayList<>();

        if (nextOrPrevious) {
            for(String line : AuctionMaster.configLoad.nextPageLore)
                lore.add(utilsAPI.chat(player, line.replace("%page-number%", String.valueOf(page+2))));
            inventory.setItem(goNextSlot = slot, itemConstructor.getItem(configLoad.nextPageMaterial, utilsAPI.chat(player, configLoad.nextPageName.replace("%page-number%", String.valueOf(page + 2))), lore));
        } else {
            for(String line : AuctionMaster.configLoad.previousPageLore)
                lore.add(utilsAPI.chat(player, line.replace("%page-number%", String.valueOf(page))));
            inventory.setItem(goBackSlot = slot, itemConstructor.getItem(configLoad.previousPageMaterial, utilsAPI.chat(player, configLoad.previousPageName.replace("%page-number%", String.valueOf(page))), lore));
        }
    }

    private Boolean collectAll() {
        if (player == null)
            return false;
        if (toCollectAll.isEmpty())
            return false;

        for (Auction auction : toCollectAll) {
            if (auction == null)
                continue;

            auction.sellerClaim(player);
        }

        player.sendMessage(utilsAPI.chat(player, AuctionMaster.auctionsManagerCfg.getString("collect-all-message")));
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

    public ManageOwnAuctionsMenu(Player player, Integer pageNumber) {
        this.player = player;
        this.pageNumber = pageNumber;
        pageNumber -= 1;

        ArrayList<Auction> auctions = AuctionMaster.auctionsHandler.ownAuctions.getOrDefault(player.getUniqueId().toString(), new ArrayList<>());

        int normalSize = auctions.size();
        int currentAuctionListing = Math.min(normalSize, 28);

        int size = 2;
        size += currentAuctionListing / 7;
        if (currentAuctionListing % 7 > 0)
            size += 1;
        size *= 9;

        inventory = Bukkit.createInventory(player, size, utilsAPI.chat(player, AuctionMaster.configLoad.manageOwnAuctionsMenuName));

        int relativeSlot = size - 9;
        for (int i = 1; i < 8; i++) {
            inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());
            inventory.setItem(relativeSlot + i, AuctionMaster.configLoad.backgroundGlass.clone());
        }
        for (int i = 0; i < size; i += 9) {
            inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());
            inventory.setItem(i + 8, AuctionMaster.configLoad.backgroundGlass.clone());
        }

        int slot = 10, i = 0;
        for (Auction auction : auctions) {
            i++;

            if (i <= pageNumber*28)
                continue;
            else if (slot > 43)
                break;

            inventory.setItem(slot, auction.getUpdatedDisplay());
            if (auction.isEnded())
                toCollectAll.add(auction);
            this.auctions.put(slot, auction);
            if (slot % 9 == 7)
                slot += 3;
            else
                slot++;
        }

        ArrayList<String> lore = new ArrayList<>();
        for (String line : AuctionMaster.configLoad.manageAuctionsItemLoreWithoutAuctions)
            lore.add(utilsAPI.chat(player, line));
        inventory.setItem(createMenuSlot = size - 2, itemConstructor.getItem(AuctionMaster.configLoad.manageAuctionsItemMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.manageAuctionsItemName), lore));

        lore = new ArrayList<>();
        for (String line : AuctionMaster.configLoad.goBackLore)
            lore.add(utilsAPI.chat(player, line));

        if (pageNumber == 0)
            inventory.setItem(goBackSlot = size - 6, itemConstructor.getItem(AuctionMaster.configLoad.goBackMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.goBackName), lore));
        else
            setupPage(false, pageNumber, size - 6);

        if (normalSize > currentAuctionListing*(pageNumber+1))
            setupPage(true, pageNumber, size - 4);

        if (toCollectAll.size() > 1) {
            double coinsToCollect = 0;
            int contor = 0;
            for (Auction auction : toCollectAll) {
                if (auction.getBids().getNumberOfBids() == 0)
                    contor++;
                else
                    coinsToCollect += auction.getBids().getTopBidCoins();
            }
            collectAllSlot = size - 8;
            lore = new ArrayList<>();
            for (String line : AuctionMaster.configLoad.collectAllLoreOwnAuctions)
                lore.add(utilsAPI.chat(player, line
                        .replace("%auctions%", String.valueOf(toCollectAll.size()))
                        .replace("%coins%", AuctionMaster.numberFormatHelper.formatNumber(coinsToCollect))
                        .replace("%items%", String.valueOf(contor))
                ));
            inventory.setItem(size - 8, itemConstructor.getItem(AuctionMaster.configLoad.collectAllMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.collectAllName), lore));
        }
        Bukkit.getPluginManager().registerEvents(listener, AuctionMaster.plugin);
        player.openInventory(inventory);

        keepUpdated();
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
                    if (e.getSlot() == createMenuSlot)
                        new CreateAuctionMainMenu(player);
                    else if(e.getSlot()==collectAllSlot){
                        if (singleClick)
                            return;
                        singleClick = true;

                        Utils.playSound(player, "claim-all-click");
                        singleClick = collectAll();
                    }

                    else if (e.getSlot() == goBackSlot) {
                        if (pageNumber == 1) {
                            Utils.playSound(player, "go-back-click");
                            new MainAuctionMenu(player);
                        } else {
                            Utils.playSound(player, "previous-page-click");
                            new ManageOwnAuctionsMenu(player, pageNumber - 1);
                        }
                    } else if (e.getSlot() == goNextSlot) {
                        Utils.playSound(player, "next-page-click");
                        new ManageOwnAuctionsMenu(player, pageNumber + 1);
                    }
                    else if (auctions.containsKey(e.getSlot())){
                        if (singleClick)
                            return;
                        singleClick = true;

                        new ViewAuctionMenu(player, auctions.get(e.getSlot()), "ownAuction", 0, null);
                    }
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if (inventory.equals(e.getInventory())) {
                if (keepUpdated!=null) {
                    keepUpdated.cancel();
                }
                HandlerList.unregisterAll(this);
            }
        }
    }
}