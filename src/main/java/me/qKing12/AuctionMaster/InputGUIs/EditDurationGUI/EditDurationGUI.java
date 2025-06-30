package me.qKing12.AuctionMaster.InputGUIs.EditDurationGUI;

import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.InputGUIs.ChatListener;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.AdminMenus.ViewAuctionAdminMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class EditDurationGUI {
    private ItemStack paper;

    public interface EditDuration{
        void openGUI(Player p, Auction auction, String goBackTo, boolean rightClick);
    }

    public static EditDuration editDuration;

    public EditDurationGUI(){
        switch (AuctionMaster.inputType) {
            case "chat":
                editDuration=this::chatTrigger;
                break;
            case "anvil":
                paper = new ItemStack(Material.PAPER);
                ArrayList<String> lore=new ArrayList<>();
                lore.add(Utils.chat("&fEnter minutes"));
                lore.add(Utils.chat("&fExamples: 20"));
                lore.add(Utils.chat("&for -20 to speed"));
                paper= AuctionMaster.itemConstructor.getItem(paper, " ", lore);
                editDuration =this::anvilTrigger;
                break;
            case "sign":
                editDuration=this::signTrigger;
                break;
        }
    }

    private void signTrigger(Player p, Auction auction, String goBackTo, boolean rightClick){
        try {
            new EditDurationSignGUI(p, auction, goBackTo, rightClick);
        } catch (Exception e) {
            paper = new ItemStack(Material.PAPER);
            ArrayList<String> lore=new ArrayList<>();
            lore.add(Utils.chat("&fEnter minutes"));
            lore.add(Utils.chat("&fExamples: 20"));
            lore.add(Utils.chat("&for -20 to speed"));
            paper= AuctionMaster.itemConstructor.getItem(paper, " ", lore);
            editDuration =this::anvilTrigger;
        }
    }

    private void anvilTrigger(Player p, Auction auction, String goBackTo, boolean rightClick){
        new net.wesjd.anvilgui.AnvilGUI.Builder()
                .onClick((target, reply) -> {
                    try{
                        int timeInput = Integer.parseInt(reply.getText());
                        if(rightClick)
                            auction.addMinutesToAuction(timeInput);
                        else
                            auction.setEndingDate(ZonedDateTime.now().toInstant().toEpochMilli()+timeInput* 60000L);
                    }catch(Exception x){
                        p.sendMessage(utilsAPI.chat(p, "&cInvalid number."));
                    }
                    new ViewAuctionAdminMenu(p, auction, goBackTo);
                    return Collections.emptyList();
                })
                .itemLeft(paper.clone())
                .text("")
                .plugin(AuctionMaster.plugin)
                .open(p);
    }

    private void chatTrigger(Player p, Auction auction, String goBackTo, boolean rightClick){
        p.sendMessage(utilsAPI.chat(p, "Enter minutes"));
        p.sendMessage(utilsAPI.chat(p, "Examples: 20"));
        p.sendMessage(utilsAPI.chat(p, "or -20 to speed"));
        p.closeInventory();
        new ChatListener(p, (reply) -> {
            try{
                int timeInput = Integer.parseInt(reply);
                if(rightClick)
                    auction.addMinutesToAuction(timeInput);
                else
                    auction.setEndingDate(ZonedDateTime.now().toInstant().toEpochMilli()+timeInput* 60000L);
            }catch(Exception x){
                p.sendMessage(utilsAPI.chat(p, "&cInvalid number."));
            }
            new ViewAuctionAdminMenu(p, auction, goBackTo);
        });
    }
}
