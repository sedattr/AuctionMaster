package me.qKing12.AuctionMaster.InputGUIs.DeliveryGUI;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.InputGUIs.ChatListener;
import me.qKing12.AuctionMaster.Menus.AdminMenus.DeliveryAdminMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DeliveryGUI {
    private ItemStack paper;

    public interface DeliveryInstance {
        void openGUI(Player p);
    }

    public static DeliveryInstance deliveryInstance;

    public DeliveryGUI(){
        if(AuctionMaster.plugin.getConfig().getBoolean("use-chat-instead-sign")){
            deliveryInstance =this::chatTrigger;
        }
        else if(AuctionMaster.plugin.getConfig().getBoolean("use-anvil-instead-sign") || !AuctionMaster.hasProtocolLib){
            paper = new ItemStack(Material.PAPER);
            ArrayList<String> lore=new ArrayList<>();
            lore.add(Utils.chat("&7^^^^^^^^^^^^^^^"));
            lore.add(Utils.chat("&fPlease enter the player's"));
            lore.add(Utils.chat("&fname whose deliveries you"));
            lore.add(Utils.chat("&fwant to manage."));
            paper= AuctionMaster.itemConstructor.getItem(paper, " ", lore);
            deliveryInstance =this::anvilTrigger;
        }
        else{
            deliveryInstance =this::signTrigger;
        }
    }

    private void signTrigger(Player p){
        new DeliverySignGUI(p);
    }

    private void anvilTrigger(Player p){
        new net.wesjd.anvilgui.AnvilGUI.Builder()
                .onComplete((target, reply) -> {
                    try{
                        new DeliveryAdminMenu(p, reply.replace(" ", "").equals("") ? null : reply);

                    }catch(Exception ignored){
                    }

                    return net.wesjd.anvilgui.AnvilGUI.Response.close();
                })
                .itemLeft(paper)
                .text("")
                .plugin(AuctionMaster.plugin)
                .open(p);
    }

    private void chatTrigger(Player p){
        p.sendMessage(Utils.chat("&7&m----------------"));
        p.sendMessage(Utils.chat("&fPlease enter the player's"));
        p.sendMessage(Utils.chat("&fname whose deliveries you"));
        p.sendMessage(Utils.chat("&fwant to manage."));
        p.closeInventory();
        new ChatListener(p, (reply) -> new DeliveryAdminMenu(p, reply.replace(" ", "").equals("")?null:reply));
    }

}
