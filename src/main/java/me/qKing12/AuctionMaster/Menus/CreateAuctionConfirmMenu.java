package me.qKing12.AuctionMaster.Menus;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.AuctionObjects.AuctionBIN;
import me.qKing12.AuctionMaster.AuctionObjects.AuctionClassic;
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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.qKing12.AuctionMaster.AuctionMaster.*;

public class CreateAuctionConfirmMenu {
    private Inventory inventory;
    private final ClickListen listener = new ClickListen();
    private double fee;

    public CreateAuctionConfirmMenu(Player player, double fee){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.fee = fee;
            inventory = Bukkit.createInventory(player, AuctionMaster.configLoad.createAuctionConfirmMenuSize, utilsAPI.chat(player, AuctionMaster.configLoad.createAuctionConfirmMenuName));

            if (AuctionMaster.configLoad.useBackgoundGlass)
                for (int i = 0; i < AuctionMaster.configLoad.createAuctionConfirmMenuSize; i++)
                    inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());

            ArrayList<String> lore = new ArrayList<>();
            for (String line : AuctionMaster.configLoad.confirmItemLore)
                lore.add(utilsAPI.chat(player, line.replace("%cost%", AuctionMaster.numberFormatHelper.formatNumber(fee))));
            inventory.setItem(AuctionMaster.menusCfg.getInt("create-auction-confirm-menu.confirm-item-slot"), itemConstructor.getItem(AuctionMaster.configLoad.confirmItemMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.confirmItemName.replace("%cost%", AuctionMaster.numberFormatHelper.formatNumber(fee))), lore));

            lore = new ArrayList<>();
            for (String line : AuctionMaster.configLoad.cancelItemLore)
                lore.add(utilsAPI.chat(player, line));
            inventory.setItem(AuctionMaster.menusCfg.getInt("create-auction-confirm-menu.cancel-item-slot"), itemConstructor.getItem(AuctionMaster.configLoad.cancelItemMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.cancelItemName), lore));

            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getPluginManager().registerEvents(listener, AuctionMaster.plugin);
                player.openInventory(inventory);
            });
        });
    }

    public class ClickListen implements Listener {
        private boolean singleClick=false;

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if (!(e.getWhoClicked() instanceof Player player))
                return;
            if (!e.getInventory().equals(inventory))
                return;
            if (e.getClickedInventory() == null)
                return;
            if (!e.getClickedInventory().equals(inventory))
                return;

            e.setCancelled(true);
            if (e.getCurrentItem()==null || e.getCurrentItem().getType().equals(Material.AIR))
                return;

            if (e.getClickedInventory().equals(inventory)) {
                if (e.getSlot() == AuctionMaster.menusCfg.getInt("create-auction-confirm-menu.cancel-item-slot")) {
                    Utils.playSound(player, "auction-cancel");
                    new CreateAuctionMainMenu(player);
                }
                else if (e.getSlot() == AuctionMaster.menusCfg.getInt("create-auction-confirm-menu.confirm-item-slot")){
                    if(!AuctionMaster.economy.hasMoney(player, fee)) {
                        new CreateAuctionMainMenu(player);
                        return;
                    }
                    if(singleClick)
                        return;
                    singleClick=true;

                    String uuid = player.getUniqueId().toString();
                    double startingBid = AuctionMaster.auctionsHandler.startingBid.getOrDefault(uuid, AuctionMaster.configLoad.defaultStartingBid);
                    long duration;
                    if (AuctionMaster.auctionsHandler.startingDuration.containsKey(uuid))
                        duration= AuctionMaster.auctionsHandler.startingDuration.get(uuid);
                    else
                        duration= AuctionMaster.configLoad.defaultDuration;

                    Boolean status;
                    ItemStack currentPreviewItem = AuctionMaster.auctionsHandler.previewItems.get(uuid);
                    if (currentPreviewItem == null) return;
                    if (auctionsHandler.buyItNowSelected != null && (configLoad.onlyBuyItNow || ((AuctionMaster.configLoad.defaultBuyItNow && !auctionsHandler.buyItNowSelected.contains(player.getUniqueId().toString())) || (!configLoad.defaultBuyItNow && auctionsHandler.buyItNowSelected.contains(player.getUniqueId().toString())))))
                        status = AuctionMaster.auctionsHandler.createAuction(new AuctionBIN(player, startingBid, duration, currentPreviewItem));
                    else
                        status = AuctionMaster.auctionsHandler.createAuction(new AuctionClassic(player, startingBid, duration, currentPreviewItem));

                    Utils.playSound(player, "auction-confirm");

                    if(AuctionMaster.auctionsHandler.ownAuctions.containsKey(player.getUniqueId().toString()))
                        new ManageOwnAuctionsMenu(player, 1);
                    else
                        player.closeInventory();

                    if (!status) {

                        try {
                            /*boolean auctionItemAdded = false;
                            for (Auction auction : auctionsHandler.ownAuctions.get(uuid)) {
                               if (auction.getItemStack().equals(currentPreviewItem) && (auction.getEndingDate() == duration)) {
                                   auctionItemAdded = true;
                                   break;
                               }
                            }
                            if (!auctionItemAdded) return;*/
                            return;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    AuctionMaster.economy.removeMoney(player, fee);
                    AuctionMaster.auctionsHandler.previewItems.remove(uuid);
                    AuctionMaster.auctionsDatabase.removePreviewItem(uuid);
                    AuctionMaster.auctionsHandler.startingDuration.remove(uuid);
                    AuctionMaster.auctionsHandler.startingBid.remove(uuid);

                    String text = utilsAPI.chat(player, AuctionMaster.auctionsManagerCfg.getString("auction-created-message"));
                    if (text != null && !text.equals(""))
                        player.sendMessage(text);
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(inventory.equals(e.getInventory())) {
                HandlerList.unregisterAll(this);
                inventory = null;
            }
        }
    }
}