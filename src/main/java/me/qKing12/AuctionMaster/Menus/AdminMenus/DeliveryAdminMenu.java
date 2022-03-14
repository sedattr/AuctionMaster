package me.qKing12.AuctionMaster.Menus.AdminMenus;

import me.qKing12.AuctionMaster.InputGUIs.DeliveryGUI.DeliveryGUI;
import me.qKing12.AuctionMaster.Utils.SkullTexture;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

import static me.qKing12.AuctionMaster.AuctionMaster.*;

public class DeliveryAdminMenu {

    private Inventory inventory;
    private Player player;
    private final ClickListen listener = new ClickListen();
    private ArrayList<ItemStack> deliveryItems;
    private double deliveryCoins;
    private String targetPlayerUUID;

    private void generatePlayerHead(String name) {
        ItemStack head;
        String coins;
        String deliveryItems;
        if (name == null) {
            name = "None";
            deliveryItems = "None";
            coins = "None";
            head=SkullTexture.getSkull("mhf_question");
        } else {
            coins = this.deliveryCoins == 0 ? "None" : numberFormatHelper.formatNumber(deliveryCoins);
            deliveryItems = this.deliveryItems.size() == 0 ? "None" : String.valueOf(this.deliveryItems.size());
            head = SkullTexture.getSkull(name);
        }
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("selected-player-item-name")));
        ArrayList<String> lore = new ArrayList<>();
        for (String line : adminCfg.getStringList("selected-player-item-lore"))
            lore.add(Utils.chat(line).replace("%player%", name).replace("%coins%", coins).replace("%items-count%", deliveryItems));

        meta.setLore(lore);
        head.setItemMeta(meta);
        inventory.setItem(13, head);
    }

    public DeliveryAdminMenu(Player player, String targetPlayer){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.player = player;
            inventory = Bukkit.createInventory(player, 54, Utils.chat("&8Admin Menu"));

            if (configLoad.useBackgoundGlass)
                for (int i = 0; i < 54; i++)
                    inventory.setItem(i, configLoad.backgroundGlass.clone());

            String name;
            if (targetPlayer != null) {
                if (targetPlayer.length() > 16) {
                    targetPlayerUUID = targetPlayer;
                    name = Bukkit.getOfflinePlayer(UUID.fromString(targetPlayer)).getName();
                } else {
                    targetPlayerUUID = Bukkit.getOfflinePlayer(targetPlayer).getUniqueId().toString();
                    name = targetPlayer;
                }
                deliveryItems = deliveries.getDeliveryItems(targetPlayerUUID);
                deliveryCoins = deliveries.getCoins(targetPlayerUUID);
            } else
                name = null;

            generatePlayerHead(name);

            inventory.setItem(28, configLoad.adminDeliverySendDelivery);
            inventory.setItem(30, configLoad.adminDeliverySetDelivery);
            inventory.setItem(32, configLoad.adminDeliveryRemoveDelivery);
            inventory.setItem(34, configLoad.adminDeliveryCopyDelivery);

            ArrayList<String> lore = new ArrayList<>();
            for (String line : configLoad.goBackLore)
                lore.add(utilsAPI.chat(player, line));
            inventory.setItem(49, itemConstructor.getItem(configLoad.goBackMaterial, utilsAPI.chat(player, configLoad.goBackName), lore));

            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getPluginManager().registerEvents(listener, plugin);
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
                    if (e.getSlot() == 49) {
                        new MainAdminMenu(player);
                    }
                    else if(e.getSlot()==13){
                        DeliveryGUI.deliveryInstance.openGUI(player);
                    }
                    else if(targetPlayerUUID==null){
                        player.sendMessage(Utils.chat(adminCfg.getString("no-selected-player-message")));
                    }
                    else if(e.getSlot()==28){
                        new DeliveryHandleMenu(player, targetPlayerUUID, 0, deliveryItems, true, null);
                    }
                    else if(e.getSlot()==30){
                        new DeliveryHandleMenu(player, targetPlayerUUID, 0, deliveryItems, false, null);
                    }
                    else if(deliveryCoins==0 && deliveryItems.isEmpty()){
                        player.sendMessage(Utils.chat(adminCfg.getString("no-delivery-to-work-with")));
                    }
                    else if(e.getSlot()==32){
                        if(e.getCurrentItem().getAmount()==2)
                            e.getCurrentItem().setAmount(1);
                        else {
                            deliveries.removeDelivery(targetPlayerUUID);
                            new DeliveryAdminMenu(player, targetPlayerUUID);
                        }
                    }
                    else if(e.getSlot()==34){
                        for(ItemStack item : deliveryItems){
                            if (Utils.getEmptySlots(player) != 0)
                                player.getInventory().addItem(item);
                            else
                                player.getWorld().dropItem(player.getLocation(), item);
                        }
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
