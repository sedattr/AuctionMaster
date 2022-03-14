package me.qKing12.AuctionMaster.InputGUIs.DeliveryCoinsGUI;

import me.qKing12.AuctionMaster.InputGUIs.ChatListener;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.AdminMenus.DeliveryHandleMenu;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DeliveryCoinsGUI {
    private ItemStack paper;

    public interface DeliveryInstance {
        void openGUI(Player p, double deliveryCoins, ArrayList<ItemStack> deliveryItems, String targetPlayerUUID, boolean send, Inventory inventory);
    }

    public static DeliveryInstance deliveryInstance;

    public DeliveryCoinsGUI(){
        if(AuctionMaster.plugin.getConfig().getBoolean("use-chat-instead-sign")){
            deliveryInstance =this::chatTrigger;
        }
        else if(AuctionMaster.plugin.getConfig().getBoolean("use-anvil-instead-sign") || !AuctionMaster.hasProtocolLib){
            paper = new ItemStack(Material.PAPER);
            ArrayList<String> lore=new ArrayList<>();
            lore.add(Utils.chat("&7^^^^^^^^^^^^^^^"));
            lore.add(Utils.chat("&fEnter the amount of"));
            lore.add(Utils.chat("&fcoins to deliver."));
            paper= AuctionMaster.itemConstructor.getItem(paper, " ", lore);
            deliveryInstance =this::anvilTrigger;
        }
        else{
            deliveryInstance =this::signTrigger;
        }
    }

    private void signTrigger(Player p, double deliveryCoins, ArrayList<ItemStack> deliveryItems, String targetPlayerUUID, boolean send, Inventory inventory){
        new DeliveryCoinsSignGUI(p, deliveryCoins, deliveryItems, targetPlayerUUID, send, inventory);
    }

    private void anvilTrigger(Player p, double deliveryCoins, ArrayList<ItemStack> deliveryItems, String targetPlayerUUID, boolean send, Inventory inventory){
        new net.wesjd.anvilgui.AnvilGUI.Builder()
                .onComplete((target, reply) -> {
                    try {
                        new DeliveryHandleMenu(p, targetPlayerUUID, Double.parseDouble(reply), deliveryItems, send, inventory);
                    }catch(Exception x){
                        p.sendMessage(Utils.chat("&cInvalid number!"));
                        new DeliveryHandleMenu(p, targetPlayerUUID, deliveryCoins, deliveryItems, send, inventory);
                    }

                    return net.wesjd.anvilgui.AnvilGUI.Response.close();
                })
                .itemLeft(paper.clone())
                .text("")
                .plugin(AuctionMaster.plugin)
                .open(p);
    }

    private void chatTrigger(Player p, double deliveryCoins, ArrayList<ItemStack> deliveryItems, String targetPlayerUUID, boolean send, Inventory inventory){
        p.sendMessage(Utils.chat("&7&m----------------"));
        p.sendMessage(Utils.chat("&fEnter the amount of coins"));
        p.sendMessage(Utils.chat("&fyou want to deliver."));
        p.closeInventory();
        new ChatListener(p, (reply) -> {
            try {
                new DeliveryHandleMenu(p, targetPlayerUUID, Double.parseDouble(reply), deliveryItems, send, inventory);
            }catch(Exception x){
                p.sendMessage(Utils.chat("&cInvalid number!"));
                new DeliveryHandleMenu(p, targetPlayerUUID, deliveryCoins, deliveryItems, send, inventory);
            }
        });
    }

}
