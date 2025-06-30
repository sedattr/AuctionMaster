package me.qKing12.AuctionMaster.InputGUIs.EditDurationGUI;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.AdminMenus.ViewAuctionAdminMenu;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZonedDateTime;
import java.util.Collections;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class EditDurationSignGUI {
    public EditDurationSignGUI(Player p, Auction auction, String goBackTo, boolean rightClick) throws SignGUIVersionException {
        SignGUI.builder()
                .setLines("", "^^^^^^^^^^", "Enter minutes, ex: 20", "or -20 to speed")
                .setHandler((player, entry) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String input = entry.getLineWithoutColor(0);

                            try {
                                int timeInput = Integer.parseInt(input);
                                if (rightClick)
                                    auction.addMinutesToAuction(timeInput);
                                else
                                    auction.setEndingDate(ZonedDateTime.now().toInstant().toEpochMilli() + timeInput * 60000L);
                            } catch(Exception x) {
                                p.sendMessage(utilsAPI.chat(p, "&cInvalid number."));
                            }

                            new ViewAuctionAdminMenu(p, auction, goBackTo);
                        }
                    }.runTask(AuctionMaster.plugin);

                    return Collections.emptyList();
                }).build().open(p);
    }
}
