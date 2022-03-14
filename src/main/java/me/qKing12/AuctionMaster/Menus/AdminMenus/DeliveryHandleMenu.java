package me.qKing12.AuctionMaster.Menus.AdminMenus;

import me.qKing12.AuctionMaster.InputGUIs.DeliveryCoinsGUI.DeliveryCoinsGUI;
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

public class DeliveryHandleMenu {

    private Inventory inventory;
    private Player player;
    private final ClickListen listener = new ClickListen();
    private ArrayList<ItemStack> deliveryItems;
    private double deliveryCoins;
    private String targetPlayer;
    private boolean send;

    private void generateCoinsItem(){
        ItemStack setCoins = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta meta = setCoins.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("delivery-coins-item-name")));
        ArrayList<String> lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("delivery-coins-item-lore"))
            lore.add(Utils.chat(line).replace("%coins%", numberFormatHelper.formatNumber(deliveryCoins)));
        meta.setLore(lore);
        setCoins.setItemMeta(meta);
        inventory.setItem(10, setCoins);
    }

    public DeliveryHandleMenu(Player player, String targetPlayer, double deliveryCoins, ArrayList<ItemStack> deliveryItems, boolean send, Inventory inventory){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.player = player;
            this.send = send;
            this.deliveryCoins = deliveryCoins;
            this.deliveryItems = deliveryItems;
            this.targetPlayer = targetPlayer;
            if (inventory == null) {
                this.inventory = Bukkit.createInventory(player, 54, Utils.chat("&8Admin Menu"));

                for (int i = 0; i < 9; i++) {
                    this.inventory.setItem(i, configLoad.backgroundGlass);
                    this.inventory.setItem(i + 36, configLoad.backgroundGlass);
                    this.inventory.setItem(i + 45, configLoad.backgroundGlass);
                }
                for (int i = 0; i < 45; i += 9) {
                    this.inventory.setItem(i, configLoad.backgroundGlass);
                    this.inventory.setItem(i + 1, configLoad.backgroundGlass);
                    this.inventory.setItem(i + 2, configLoad.backgroundGlass);
                    this.inventory.setItem(i + 8, configLoad.backgroundGlass);
                }

                generateCoinsItem();

                this.inventory.setItem(48, configLoad.adminDeliveryConfirmDelivery);
                this.inventory.setItem(50, configLoad.adminDeliveryCancelDelivery);
            } else
                this.inventory = inventory;

            generateCoinsItem();

            Bukkit.getScheduler().runTask(plugin, () -> {
                player.openInventory(this.inventory);
                Bukkit.getPluginManager().registerEvents(listener, plugin);
            });
        });
    }

    public class ClickListen implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getInventory().equals(inventory)){
                if(e.getClickedInventory().equals(inventory)) {
                    if((e.getSlot()%9>7 || e.getSlot()%9<3) || e.getSlot()/9==0 || e.getSlot()/9>3)
                        e.setCancelled(true);
                    if(e.getCurrentItem()==null || e.getCurrentItem().getType().equals(Material.AIR)) {
                        return;
                    }
                    if (e.getSlot() == 50) {
                        new DeliveryAdminMenu(player, targetPlayer);
                    }
                    else if(e.getSlot()==10){
                        DeliveryCoinsGUI.deliveryInstance.openGUI(player, deliveryCoins, deliveryItems, targetPlayer, send, inventory);
                    }
                    else if(e.getSlot()==48) {
                        if (!send)
                            deliveryItems.clear();
                        else
                            deliveryCoins += deliveries.getCoins(targetPlayer);

                        ArrayList<ItemStack> itemStacks = new ArrayList<>();
                        int slotToGet = 12;
                        while (slotToGet < 35) {
                            if (inventory.getItem(slotToGet) != null) {
                                itemStacks.add(inventory.getItem(slotToGet));
                            }

                            if (slotToGet % 9 == 7)
                                slotToGet += 5;
                            else
                                slotToGet++;
                        }
                        if (deliveryCoins == 0 && itemStacks.isEmpty()) {
                            if (send) {
                                player.sendMessage(Utils.chat(adminCfg.getString("no-delivery-to-send")));
                            } else
                                player.sendMessage(Utils.chat(adminCfg.getString("no-delivery-to-set")));
                            return;
                        }
                        else if(send){
                            Player p = Bukkit.getPlayer(UUID.fromString(targetPlayer));
                            if(p!=null) {
                                Utils.playSound(p, "ah-delivery-got");
                                p.sendMessage(utilsAPI.chat(p, plugin.getConfig().getString("delivery-got-message").replace("%coins%", numberFormatHelper.formatNumber(deliveryCoins)).replace("%item-count%", itemStacks.size() + "")));
                            }
                        }
                        deliveryItems.addAll(itemStacks);
                        deliveries.setCoinsAndItems(targetPlayer, deliveryItems, deliveryCoins);
                        new DeliveryAdminMenu(player, targetPlayer);
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
