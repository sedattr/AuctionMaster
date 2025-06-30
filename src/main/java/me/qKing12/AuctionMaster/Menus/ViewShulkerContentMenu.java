package me.qKing12.AuctionMaster.Menus;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;

import static me.qKing12.AuctionMaster.AuctionMaster.*;

public class ViewShulkerContentMenu {

    private final Inventory inventory;
    private final Player player;
    private final String goBackTo;
    private final String searchParam;
    private final Auction auction;

    private void goBack(){
        if (player == null || goBackTo == null || auction.isEnded()) {
            if (player != null)
                player.closeInventory();
            return;
        }
        new ViewAuctionMenu(player, auction, goBackTo, 0, searchParam);
    }

    public ViewShulkerContentMenu(Player player, ItemStack shulker, Auction auction, String goBackTo, String searchParam){
        this.player = player;
        this.goBackTo = goBackTo;
        this.searchParam = searchParam;
        this.auction = auction;
        inventory = Bukkit.createInventory(player, 36, utilsAPI.chat(player, AuctionMaster.configLoad.viewShulkerContentMenuName));

        Utils.playSound(player, "auction-view-menu-open");

        Bukkit.getScheduler().runTaskTimerAsynchronously(AuctionMaster.plugin, () -> {
            BlockStateMeta bsm = (BlockStateMeta) shulker.getItemMeta();
            assert bsm != null;
            ShulkerBox shulkerBox = (ShulkerBox) bsm.getBlockState();
            inventory.setContents(shulkerBox.getInventory().getContents());

            if (AuctionMaster.configLoad.useBackgoundGlass)
                for (int i = 27; i <= 35; i++) {
                    inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());
                }


            ArrayList<String> lore = new ArrayList<>();
            for (String line : AuctionMaster.configLoad.goBackLore)
                lore.add(utilsAPI.chat(player, line));
            inventory.setItem(AuctionMaster.menusCfg.getInt("view-shulker-content-menu.go-back-slot"), itemConstructor.getItem(AuctionMaster.configLoad.goBackMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.goBackName), lore));
        }, 0, 0);

        Bukkit.getPluginManager().registerEvents(new ClickListen(), AuctionMaster.plugin);
        player.openInventory(inventory);
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
                    if(e.getSlot() == AuctionMaster.menusCfg.getInt("view-shulker-content-menu.go-back-slot")) {
                        goBack();
                        Utils.playSound(player, "go-back-click");
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