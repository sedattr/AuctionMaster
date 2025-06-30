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

import java.util.*;

import static me.qKing12.AuctionMaster.AuctionMaster.*;

public class ViewPlayerAuctions {

    private Inventory inventory;
    private Player player;
    private final ClickListen listener = new ClickListen();
    private final HashMap<Integer, Auction> auctions = new HashMap<>();
    private BukkitTask keepUpdated;
    private int goNextSlot;
    private int goBackSlot;
    private int pageNumber;
    private String uuid;

    /*
    Creates the previous/next page buttons.

    Example: setupPage(true, pageNumber, size - 6);
 */
    private void setupPage(Boolean nextOrPrevious, Integer page, Integer slot){
        ArrayList<String> lore = new ArrayList<>();

        if (nextOrPrevious) {
            for(String line : AuctionMaster.configLoad.nextPageLore)
                lore.add(utilsAPI.chat(player, line.replace("%page-number%", String.valueOf(page + 1))));
            inventory.setItem(goNextSlot = slot, itemConstructor.getItem(configLoad.nextPageMaterial, utilsAPI.chat(player, configLoad.nextPageName.replace("%page-number%", String.valueOf(page + 1))), lore));
        } else {
            for(String line : AuctionMaster.configLoad.previousPageLore)
                lore.add(utilsAPI.chat(player, line.replace("%page-number%", String.valueOf(page - 1))));
            inventory.setItem(goBackSlot = slot, itemConstructor.getItem(configLoad.previousPageMaterial, utilsAPI.chat(player, configLoad.previousPageName.replace("%page-number%", String.valueOf(page - 1))), lore));
        }
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

    public ViewPlayerAuctions(Player player, String uuid, Integer pageNumber) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.player = player;
            this.pageNumber = pageNumber;

            if(AuctionMaster.auctionsHandler.ownAuctions.get(uuid)==null){
                player.sendMessage(utilsAPI.chat(player, plugin.getConfig().getString("no-auctions-message")));
                return;
            }
            ArrayList<Auction> auctions = new ArrayList<>();

            for(Auction auction : auctionsHandler.ownAuctions.get(uuid)){
                if(!auction.isEnded())
                    auctions.add(auction);
            }

            if(auctions.isEmpty()){
                player.sendMessage(utilsAPI.chat(player, plugin.getConfig().getString("no-auctions-message")));
                return;
            }

            this.uuid = uuid;

            int normalSize = auctions.size();
            int currentAuctionListing = Math.min(normalSize, 28);

            int size = 2;
            size += currentAuctionListing / 7;
            if (currentAuctionListing % 7 > 0)
                size += 1;
            size *= 9;

            inventory = Bukkit.createInventory(player, size, utilsAPI.chat(player, AuctionMaster.configLoad.viewPlayerAuctionsMenuName.replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName())));

            int relativeSlot = size - 9;
            for (int i = 1; i < 8; i++) {
                inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());
                inventory.setItem(relativeSlot + i, AuctionMaster.configLoad.backgroundGlass.clone());
            }
            for (int i = 0; i < size; i += 9) {
                inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());
                inventory.setItem(i + 8, AuctionMaster.configLoad.backgroundGlass.clone());
            }

            int slot = 10, i = 0, rawPageNumber = pageNumber - 1;
            for (Auction auction : auctions) {
                i++;

                if (i <= (rawPageNumber)*28)
                    continue;
                else if (slot > 43)
                    break;

                inventory.setItem(slot, auction.getUpdatedDisplay());
                this.auctions.put(slot, auction);
                if (slot % 9 == 7)
                    slot += 3;
                else
                    slot++;
            }
            keepUpdated();

            if (pageNumber > 1)
                setupPage(false, pageNumber, size - 6);

            if (normalSize > currentAuctionListing*(pageNumber))
                setupPage(true, pageNumber, size - 4);

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
                if(e.getCurrentItem()==null || e.getCurrentItem().getType().equals(Material.AIR) || e.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE)) {
                    return;
                }
                if(e.getClickedInventory().equals(inventory)) {
                    if (e.getSlot() == goBackSlot) {
                        Utils.playSound(player, "previous-page-click");
                        new ViewPlayerAuctions(player, uuid, pageNumber - 1);
                    } else if (e.getSlot() == goNextSlot) {
                        Utils.playSound(player, "next-page-click");
                        new ViewPlayerAuctions(player, uuid, pageNumber + 1);
                    }
                    else if(auctions.containsKey(e.getSlot())){
                        new ViewAuctionMenu(player, auctions.get(e.getSlot()), uuid, 0, null);
                    }
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(inventory.equals(e.getInventory())) {
                if(keepUpdated != null)
                    keepUpdated.cancel();
                HandlerList.unregisterAll(this);
            }
        }
    }

}
