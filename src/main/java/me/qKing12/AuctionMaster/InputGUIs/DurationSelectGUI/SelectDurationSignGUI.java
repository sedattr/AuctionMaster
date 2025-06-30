package me.qKing12.AuctionMaster.InputGUIs.DurationSelectGUI;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.CreateAuctionMainMenu;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class SelectDurationSignGUI {
    public SelectDurationSignGUI(Player p, int maximum_hours, boolean minutes) throws SignGUIVersionException {
        List<String> lines = AuctionMaster.auctionsManagerCfg.getStringList("duration-sign-message");
        SignGUI.builder()
                .setLines("", lines.get(0), lines.get(1), lines.get(2).replace("%time-format%", minutes? AuctionMaster.configLoad.minutes : AuctionMaster.configLoad.hours))
                .setHandler((player, entry) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String input = entry.getLineWithoutColor(0);
                            try {
                                int timeInput = Integer.parseInt(input);
                                if (minutes) {
                                    if (timeInput>59 || timeInput<1)
                                        p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("duration-sign-deny")));
                                    else
                                        AuctionMaster.auctionsHandler.startingDuration.put(p.getUniqueId().toString(), timeInput*60000);
                                }
                                else {
                                    if (timeInput>332 || timeInput<1)
                                        p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("duration-sign-deny")));
                                    else{
                                        if(maximum_hours!=-1 && maximum_hours<timeInput)
                                            p.sendMessage(utilsAPI.chat(p, AuctionMaster.plugin.getConfig().getString("duration-limit-reached-message")));
                                        else
                                            AuctionMaster.auctionsHandler.startingDuration.put(p.getUniqueId().toString(), timeInput*3600000);
                                    }
                                }
                            } catch(Exception x) {
                                p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("duration-sign-deny")));
                            }

                            new CreateAuctionMainMenu(p);
                        }
                    }.runTask(AuctionMaster.plugin);

                    return Collections.emptyList();
                }).build().open(p);
    }
}