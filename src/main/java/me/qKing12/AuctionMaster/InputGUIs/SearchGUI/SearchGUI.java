package me.qKing12.AuctionMaster.InputGUIs.SearchGUI;

import me.qKing12.AuctionMaster.InputGUIs.ChatListener;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.BrowsingAuctionsMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

public class SearchGUI {
    private ItemStack paper;

    public interface SearchFor{
        void openGUI(Player p, String category);
    }

    public static SearchFor searchFor;

    public SearchGUI(){
        switch (AuctionMaster.inputType) {
            case "chat":
                searchFor=this::chatTrigger;
                break;
            case "anvil":
                paper = new ItemStack(Material.PAPER);
                ArrayList<String> lore=new ArrayList<>();
                for(String line : AuctionMaster.auctionsManagerCfg.getStringList("search-sign-message"))
                    lore.add(Utils.chat(line));
                paper= AuctionMaster.itemConstructor.getItem(paper, " ", lore);
                searchFor=this::anvilTrigger;
                break;
            case "sign":
                searchFor=this::signTrigger;
                break;
        }
    }

    private void signTrigger(Player p, String category){
        try {
            new SearchSignGUI(p, category);
        } catch (Exception e) {
            paper = new ItemStack(Material.PAPER);
            ArrayList<String> lore=new ArrayList<>();
            for(String line : AuctionMaster.auctionsManagerCfg.getStringList("search-sign-message"))
                lore.add(Utils.chat(line));
            paper= AuctionMaster.itemConstructor.getItem(paper, " ", lore);
            searchFor=this::anvilTrigger;
        }
    }

    private void anvilTrigger(Player p, String category){
        new net.wesjd.anvilgui.AnvilGUI.Builder()
                .onClick((target, reply) -> {
                    new BrowsingAuctionsMenu(p, category, 0, reply.getText().isEmpty()?null:reply.getText());
                    return Collections.emptyList();
                })
                .itemLeft(paper.clone())
                .text("")
                .plugin(AuctionMaster.plugin)
                .open(p);
    }

    private void chatTrigger(Player p, String category){
        for(String line : AuctionMaster.auctionsManagerCfg.getStringList("search-sign-message"))
            p.sendMessage(Utils.chat(line));
        p.closeInventory();
        new ChatListener(p, (reply) -> {
            new BrowsingAuctionsMenu(p, category, 0, reply.equals("")?null:reply);
        });
    }

}
