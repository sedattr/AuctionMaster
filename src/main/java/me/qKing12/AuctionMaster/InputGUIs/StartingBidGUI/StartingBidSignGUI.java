package me.qKing12.AuctionMaster.InputGUIs.StartingBidGUI;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.CreateAuctionMainMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class StartingBidSignGUI {
    public StartingBidSignGUI(Player p) throws SignGUIVersionException {
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
                                double timeInput = AuctionMaster.numberFormatHelper.useDecimals ? inputArg : Math.floor(inputArg);
                                if (timeInput < 1)
                                    p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("starting-bid-sign-deny")));
                                else
                                    AuctionMaster.auctionsHandler.startingBid.put(p.getUniqueId().toString(), timeInput);
                            } catch(Exception x) {
                                p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("starting-bid-sign-deny")));
                            }

                            new CreateAuctionMainMenu(p);
                        }
                    }.runTask(AuctionMaster.plugin);

                    return Collections.emptyList();
                }).build().open(p);
    }
}