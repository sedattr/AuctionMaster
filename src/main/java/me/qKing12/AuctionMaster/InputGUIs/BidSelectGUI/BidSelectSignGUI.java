package me.qKing12.AuctionMaster.InputGUIs.BidSelectGUI;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.ViewAuctionMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class BidSelectSignGUI {
    public BidSelectSignGUI(Player p, Auction auction, String goBackTo, double minimumBid) throws SignGUIVersionException {
        List<String> lines = AuctionMaster.auctionsManagerCfg.getStringList("starting-bid-sign-message");
        SignGUI.builder()
                .setLines("", lines.get(0), lines.get(1), lines.get(2))
                .setHandler((player, entry) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String input = entry.getLineWithoutColor(0);
                            double inputArg = Utils.moneyInput(input);

                            try {
                                double bidSelect = AuctionMaster.numberFormatHelper.useDecimals ? inputArg : Math.floor(inputArg);
                                if(bidSelect>=minimumBid)
                                    new ViewAuctionMenu(p, auction, goBackTo, bidSelect, null);
                                else
                                    new ViewAuctionMenu(p, auction, goBackTo, 0, null);
                            } catch(Exception x) {
                                p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("edit-bid-deny-message")));
                                new ViewAuctionMenu(p, auction, goBackTo, 0, null);
                            }
                        }
                    }.runTask(AuctionMaster.plugin);

                    return Collections.emptyList();
                }).build().open(p);

    }
}
