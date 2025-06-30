package me.qKing12.AuctionMaster.InputGUIs.DeliveryGUI;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.AdminMenus.DeliveryAdminMenu;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class DeliverySignGUI {
    public DeliverySignGUI(Player p) throws SignGUIVersionException {
        SignGUI.builder()
                .setLines("", "^^^^^^^^^^", "Enter player", "name here")
                .setHandler((player, entry) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String input = entry.getLineWithoutColor(0);
                            new DeliveryAdminMenu(p, input.replace(" ", "").isEmpty() ? null : input);
                        }
                    }.runTask(AuctionMaster.plugin);

                    return Collections.emptyList();
                }).build().open(p);
    }
}