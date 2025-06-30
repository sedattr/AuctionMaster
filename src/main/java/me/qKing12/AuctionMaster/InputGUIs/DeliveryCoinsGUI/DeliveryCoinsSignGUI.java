package me.qKing12.AuctionMaster.InputGUIs.DeliveryCoinsGUI;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.AdminMenus.DeliveryHandleMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;

public class DeliveryCoinsSignGUI {
    public DeliveryCoinsSignGUI(Player p, double deliveryCoins, ArrayList<ItemStack> deliveryItems, String targetPlayerUUID, boolean send, Inventory inventory) throws SignGUIVersionException {
        SignGUI.builder()
                .setLines("", "^^^^^^^^^^", "Enter amount of", "coins to deliver")
                .setHandler((player, entry) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String input = entry.getLineWithoutColor(0);
                            try {
                                new DeliveryHandleMenu(p, targetPlayerUUID, Double.parseDouble(input), deliveryItems, send, inventory);
                            } catch (Exception x) {
                                p.sendMessage(Utils.chat("&cInvalid number!"));
                                new DeliveryHandleMenu(p, targetPlayerUUID, deliveryCoins, deliveryItems, send, inventory);
                            }
                        }
                    }.runTask(AuctionMaster.plugin);

                    return Collections.emptyList();
                }).build().open(p);
    }
}