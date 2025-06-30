package me.qKing12.AuctionMaster.InputGUIs.SearchGUI;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.BrowsingAuctionsMenu;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class SearchSignGUI {
    public SearchSignGUI(Player p, String category) throws SignGUIVersionException {
        List<String> lines = AuctionMaster.auctionsManagerCfg.getStringList("search-sign-message");
        SignGUI.builder()
                .setLines("", lines.get(0), lines.get(1), lines.get(2))
                .setHandler((player, entry) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String input = entry.getLineWithoutColor(0);

                            new BrowsingAuctionsMenu(p, category, 0, input.isEmpty() ? null : AuctionMaster.auctionsHandler.auctions.isEmpty() ? null : input);
                        }
                    }.runTask(AuctionMaster.plugin);

                    return Collections.emptyList();
                }).build().open(p);
    }
}
