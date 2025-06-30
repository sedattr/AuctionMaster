package me.qKing12.AuctionMaster.InputGUIs.BidSelectGUI;

import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.InputGUIs.ChatListener;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.ViewAuctionMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.wesjd.anvilgui.AnvilGUI;

import java.util.ArrayList;
import java.util.Collections;
import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class BidSelectGUI {
    public interface UpdatedBid{
        void openGUI(Player p, Auction auction, String goBackTo, double minimumBid);
    }

    private ItemStack paper;
    public static UpdatedBid selectUpdateBid;

    public BidSelectGUI(){
        switch (AuctionMaster.inputType) {
            case "chat":
                selectUpdateBid=this::chatTrigger;
                break;
            case "anvil":
                paper = new ItemStack(Material.PAPER);
                ArrayList<String> lore=new ArrayList<>();
                for(String line : AuctionMaster.auctionsManagerCfg.getStringList("starting-bid-sign-message"))
                    lore.add(Utils.chat(line));
                paper = AuctionMaster.itemConstructor.getItem(paper, " ", lore);
                selectUpdateBid=this::anvilTrigger;
                break;
            case "sign":
                selectUpdateBid=this::signTrigger;
                break;
        }
    }

    private void signTrigger(Player p, Auction auction, String goBackTo, double minimumBid){
        try {
            new BidSelectSignGUI(p, auction, goBackTo, minimumBid);
        } catch (Exception e) {
            paper = new ItemStack(Material.PAPER);
            ArrayList<String> lore=new ArrayList<>();
            for(String line : AuctionMaster.auctionsManagerCfg.getStringList("starting-bid-sign-message"))
                lore.add(Utils.chat(line));
            paper = AuctionMaster.itemConstructor.getItem(paper, " ", lore);
            selectUpdateBid=this::anvilTrigger;
        }
    }

    private void anvilTrigger(Player p, Auction auction, String goBackTo, double minimumBid){
        new AnvilGUI.Builder()
                .onClick((target, reply) -> {
                    try{
                        double inputArg = Utils.moneyInput(reply.getText());
                        double bidSelect = AuctionMaster.numberFormatHelper.useDecimals ? inputArg : Math.floor(inputArg);
                        if(bidSelect>=minimumBid)
                            new ViewAuctionMenu(p, auction, goBackTo, bidSelect, null);
                        else
                            new ViewAuctionMenu(p, auction, goBackTo, 0, null);
                    }catch(Exception x){
                        p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("edit-bid-deny-message")));
                        new ViewAuctionMenu(p, auction, goBackTo, 0, null);
                    }

                    return Collections.emptyList();
                })
                .itemLeft(paper.clone())
                .text("")
                .plugin(AuctionMaster.plugin)
                .open(p);
    }

    private void chatTrigger(Player p, Auction auction, String goBackTo, double minimumBid){
        for(String line : AuctionMaster.auctionsManagerCfg.getStringList("starting-bid-sign-message"))
            p.sendMessage(utilsAPI.chat(p, line));
        p.closeInventory();

        new ChatListener(p, (reply) -> {
            try{
                double inputArg = Utils.moneyInput(reply);
                double bidSelect = AuctionMaster.numberFormatHelper.useDecimals ? inputArg : Math.floor(inputArg);
                if(bidSelect>=minimumBid)
                    new ViewAuctionMenu(p, auction, goBackTo, bidSelect, null);
                else
                    new ViewAuctionMenu(p, auction, goBackTo, 0, null);
            }catch(Exception x){
                p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("edit-bid-deny-message")));
                new ViewAuctionMenu(p, auction, goBackTo, 0, null);
            }

        });
    }
}
