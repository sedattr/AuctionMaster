package me.qKing12.AuctionMaster.InputGUIs.StartingBidGUI;

import me.qKing12.AuctionMaster.InputGUIs.ChatListener;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.CreateAuctionMainMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class StartingBidGUI {
    private ItemStack paper;

    public interface StartingBid{
        void openGUI(Player p);
    }

    public static StartingBid selectStartingBid;

    public StartingBidGUI(){
        switch (AuctionMaster.inputType) {
            case "chat":
                selectStartingBid=this::chatTrigger;
                break;
            case "anvil":
                paper = new ItemStack(Material.PAPER);
                ArrayList<String> lore=new ArrayList<>();
                for(String line : AuctionMaster.auctionsManagerCfg.getStringList("starting-bid-sign-message"))
                    lore.add(Utils.chat(line));
                paper= AuctionMaster.itemConstructor.getItem(paper, " ", lore);
                selectStartingBid=this::anvilTrigger;
                break;
            case "sign":
                selectStartingBid=this::signTrigger;
                break;
        }
    }

    private void signTrigger(Player p){
        new StartingBidSignGUI(p);
    }

    private void anvilTrigger(Player p){
        new net.wesjd.anvilgui.AnvilGUI.Builder()
                .onComplete((target, reply) -> {
                    try{
                        double timeInput = AuctionMaster.numberFormatHelper.useDecimals? Double.parseDouble(reply):Math.floor(Double.parseDouble(reply));
                        if(timeInput<1){
                            p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("starting-bid-sign-deny")));
                        }
                        else
                            AuctionMaster.auctionsHandler.startingBid.put(p.getUniqueId().toString(), timeInput);
                    }catch(Exception x){
                        p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("starting-bid-sign-deny")));
                    }

                    new CreateAuctionMainMenu(p);
                    return net.wesjd.anvilgui.AnvilGUI.Response.close();
                })
                .itemLeft(paper.clone())
                .text("")
                .plugin(AuctionMaster.plugin)
                .open(p);
    }

    private void chatTrigger(Player p){
        for(String line : AuctionMaster.auctionsManagerCfg.getStringList("starting-bid-sign-message"))
            p.sendMessage(utilsAPI.chat(p, line));
        p.closeInventory();
        new ChatListener(p, (reply) -> {
            try {
                double timeInput = AuctionMaster.numberFormatHelper.useDecimals ? Double.parseDouble(reply) : Math.floor(Double.parseDouble(reply));
                if (timeInput < 1) {
                    p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("starting-bid-sign-deny")));
                } else
                    AuctionMaster.auctionsHandler.startingBid.put(p.getUniqueId().toString(), timeInput);
            } catch (Exception x) {
                p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("starting-bid-sign-deny")));
            }

            new CreateAuctionMainMenu(p);
        });
    }
}
