package me.qKing12.AuctionMaster.Menus;

import me.qKing12.AuctionMaster.InputGUIs.DurationSelectGUI.SelectDurationGUI;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

import static me.qKing12.AuctionMaster.AuctionMaster.*;

public class DurationSelectMenu {

    private static int getMaximumDuration(Player p){
        if(p.hasPermission("auctionmaster.limit.duration.-1"))
            return -1;
        for (int start = 48; start >= 1; start--)
            if (p.hasPermission("auctionmaster.limit.duration." + start))
                return start;
        return -1;
    }

    private Inventory inventory;
    private Player player;
    private final ClickListen listener = new ClickListen();
    private int maximum_hours;

    public DurationSelectMenu(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.player = player;
            inventory = Bukkit.createInventory(player, AuctionMaster.configLoad.durationMenuSize, utilsAPI.chat(player, auctionsManagerCfg.getString("duration-menu-name")));

            if (AuctionMaster.plugin.getConfig().getBoolean("use-duration-limit"))
                maximum_hours = getMaximumDuration(player);
            else maximum_hours = -1;

            long startingDuration;
            if (AuctionMaster.auctionsHandler.startingDuration.containsKey(player.getUniqueId().toString()))
                startingDuration = AuctionMaster.auctionsHandler.startingDuration.get(player.getUniqueId().toString());
            else
                startingDuration = AuctionMaster.configLoad.defaultDuration;


            if (AuctionMaster.configLoad.useBackgoundGlass)
                for (int i = 0; i < configLoad.durationMenuSize; i++)
                    inventory.setItem(i, AuctionMaster.configLoad.backgroundGlass.clone());

            boolean custom = true;
            double hoursToCompare;

            hoursToCompare = AuctionMaster.menusCfg.getDouble("duration-select-menu.first-item.hours");
            if (maximum_hours == -1 || hoursToCompare <= maximum_hours) {
                if (hoursToCompare < 1)
                    hoursToCompare = hoursToCompare * 6000000;
                else
                    hoursToCompare = hoursToCompare * 3600000;
                ItemStack toSet = AuctionMaster.configLoad.firstTimeItem.clone();
                if (hoursToCompare == startingDuration) {
                    custom = false;
                    toSet.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
                }
                inventory.setItem(AuctionMaster.menusCfg.getInt("duration-select-menu.first-item.slot"), toSet);
            }

            hoursToCompare = AuctionMaster.menusCfg.getDouble("duration-select-menu.second-item.hours");
            if (maximum_hours == -1 || hoursToCompare <= maximum_hours) {
                if (hoursToCompare < 1)
                    hoursToCompare = hoursToCompare * 6000000;
                else
                    hoursToCompare = hoursToCompare * 3600000;
                ItemStack toSet = AuctionMaster.configLoad.secondTimeItem.clone();
                if (hoursToCompare == startingDuration) {
                    custom = false;
                    toSet.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
                }
                inventory.setItem(AuctionMaster.menusCfg.getInt("duration-select-menu.second-item.slot"), toSet);
            }

            hoursToCompare = AuctionMaster.menusCfg.getDouble("duration-select-menu.third-item.hours");
            if (maximum_hours == -1 || hoursToCompare <= maximum_hours) {
                if (hoursToCompare < 1)
                    hoursToCompare = hoursToCompare * 6000000;
                else
                    hoursToCompare = hoursToCompare * 3600000;
                ItemStack toSet = AuctionMaster.configLoad.thirdTimeItem.clone();
                if (hoursToCompare == startingDuration) {
                    custom = false;
                    toSet.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
                }
                inventory.setItem(AuctionMaster.menusCfg.getInt("duration-select-menu.third-item.slot"), toSet);
            }

            hoursToCompare = AuctionMaster.menusCfg.getDouble("duration-select-menu.forth-item.hours");
            if (maximum_hours == -1 || hoursToCompare <= maximum_hours) {
                if (hoursToCompare < 1)
                    hoursToCompare = hoursToCompare * 6000000;
                else
                    hoursToCompare = hoursToCompare * 3600000;
                ItemStack toSet = AuctionMaster.configLoad.forthTimeItem.clone();
                if (hoursToCompare == startingDuration) {
                    custom = false;
                    toSet.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
                }
                inventory.setItem(AuctionMaster.menusCfg.getInt("duration-select-menu.forth-item.slot"), toSet);
            }

            hoursToCompare = AuctionMaster.menusCfg.getDouble("duration-select-menu.fifth-item.hours");
            if (maximum_hours == -1 || hoursToCompare <= maximum_hours) {
                if (hoursToCompare < 1)
                    hoursToCompare = hoursToCompare * 6000000;
                else
                    hoursToCompare = hoursToCompare * 3600000;
                ItemStack toSet = AuctionMaster.configLoad.fifthTimeItem.clone();
                if (hoursToCompare == startingDuration) {
                    custom = false;
                    toSet.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
                }
                inventory.setItem(AuctionMaster.menusCfg.getInt("duration-select-menu.fifth-item.slot"), toSet);
            }

            ItemStack toSet = AuctionMaster.configLoad.customTimeItem.clone();
            if (custom)
                toSet.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
            inventory.setItem(AuctionMaster.menusCfg.getInt("duration-select-menu.custom-time-item-slot"), toSet);

            ArrayList<String> lore = new ArrayList<>();
            for (String line : AuctionMaster.configLoad.goBackLore)
                lore.add(utilsAPI.chat(player, line));
            inventory.setItem(AuctionMaster.menusCfg.getInt("duration-select-menu.go-back-slot"), itemConstructor.getItem(AuctionMaster.configLoad.goBackMaterial, utilsAPI.chat(player, AuctionMaster.configLoad.goBackName), lore));

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
                if(Objects.equals(e.getClickedInventory(), inventory)) {
                    if (e.getSlot() == AuctionMaster.menusCfg.getInt("duration-select-menu.go-back-slot")) {
                        new CreateAuctionMainMenu(player);
                        Utils.playSound(player, "go-back-click");
                    } else if (e.getSlot() == AuctionMaster.menusCfg.getInt("duration-select-menu.first-item.slot")) {
                        Utils.playSound(player, "1-hour");
                        double duration = AuctionMaster.menusCfg.getDouble("duration-select-menu.first-item.hours");
                        if (duration < 1)
                            duration = duration * 6000000;
                        else
                            duration = duration * 3600000;
                        AuctionMaster.auctionsHandler.startingDuration.put(player.getUniqueId().toString(), (int) duration);
                        new CreateAuctionMainMenu(player);
                    } else if (e.getSlot() == AuctionMaster.menusCfg.getInt("duration-select-menu.second-item.slot")) {
                        Utils.playSound(player, "6-hours");
                        double duration = AuctionMaster.menusCfg.getDouble("duration-select-menu.second-item.hours");
                        if (duration < 1)
                            duration = duration * 6000000;
                        else
                            duration = duration * 3600000;
                        AuctionMaster.auctionsHandler.startingDuration.put(player.getUniqueId().toString(), (int) duration);
                        new CreateAuctionMainMenu(player);
                    } else if (e.getSlot() == AuctionMaster.menusCfg.getInt("duration-select-menu.third-item.slot")) {
                        Utils.playSound(player, "12-hours");
                        double duration = AuctionMaster.menusCfg.getDouble("duration-select-menu.third-item.hours");
                        if (duration < 1)
                            duration = duration * 6000000;
                        else
                            duration = duration * 3600000;
                        AuctionMaster.auctionsHandler.startingDuration.put(player.getUniqueId().toString(), (int) duration);
                        new CreateAuctionMainMenu(player);
                    } else if (e.getSlot() == AuctionMaster.menusCfg.getInt("duration-select-menu.forth-item.slot")) {
                        Utils.playSound(player, "24-hours");
                        double duration = AuctionMaster.menusCfg.getDouble("duration-select-menu.forth-item.hours");
                        if (duration < 1)
                            duration = duration * 6000000;
                        else
                            duration = duration * 3600000;
                        AuctionMaster.auctionsHandler.startingDuration.put(player.getUniqueId().toString(), (int) duration);
                        new CreateAuctionMainMenu(player);
                    } else if (e.getSlot() == AuctionMaster.menusCfg.getInt("duration-select-menu.fifth-item.slot")) {
                        Utils.playSound(player, "2-days");
                        double duration = AuctionMaster.menusCfg.getDouble("duration-select-menu.fifth-item.hours");
                        if (duration < 1)
                            duration = duration * 6000000;
                        else
                            duration = duration * 3600000;
                        AuctionMaster.auctionsHandler.startingDuration.put(player.getUniqueId().toString(), (int) duration);
                        new CreateAuctionMainMenu(player);
                    } else if (e.getSlot() == AuctionMaster.menusCfg.getInt("duration-select-menu.custom-time-item-slot")) {
                        Utils.playSound(player, "custom-duration-click");
                        SelectDurationGUI.selectDuration.openGUI(player, maximum_hours, e.getClick().equals(ClickType.RIGHT));
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
