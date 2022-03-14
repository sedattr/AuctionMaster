package me.qKing12.AuctionMaster.InputGUIs.EditDurationGUI;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.AdminMenus.ViewAuctionAdminMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.ZonedDateTime;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class EditDurationSignGUI {

    private PacketAdapter packetListener;
    private final Player p;
    private Sign sign;
    private final LeaveListener listener = new LeaveListener();
    private final Auction auction;
    private final String goBackTo;
    private final boolean rightClick;

    public EditDurationSignGUI(Player p, Auction auction, String goBackTo, boolean rightClick) {
        this.p=p;
        this.auction=auction;
        this.goBackTo=goBackTo;
        this.rightClick=rightClick;
        int x_start = p.getLocation().getBlockX();
        int y_start = 255;
        int z_start = p.getLocation().getBlockZ();

        Material material = Material.getMaterial("WALL_SIGN");
        if (material == null)
            material = Material.OAK_WALL_SIGN;

        while (!p.getWorld().getBlockAt(x_start, y_start, z_start).getType().equals(Material.AIR) && !p.getWorld().getBlockAt(x_start, y_start, z_start).getType().equals(material)) {
            y_start--;
            if (y_start == 1)
                return;
        }

        p.getWorld().getBlockAt(x_start, y_start, z_start).setType(material);
        sign = (Sign) p.getWorld().getBlockAt(x_start, y_start, z_start).getState();

        sign.setLine(1, utilsAPI.chat(p, "Enter minutes"));
        sign.setLine(2, utilsAPI.chat(p, "Examples: 20"));
        sign.setLine(3, utilsAPI.chat(p, "or -20 to speed"));

        sign.update(false, false);

        PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        BlockPosition position = new BlockPosition(x_start, y_start, z_start);
        openSign.getBlockPositionModifier().write(0, position);

        Bukkit.getScheduler().runTaskLater(AuctionMaster.plugin, () -> {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, openSign);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 3L);

        Bukkit.getPluginManager().registerEvents(listener, AuctionMaster.plugin);
        registerSignUpdateListener();
    }

    private class LeaveListener implements Listener {
        @EventHandler
        public void onLeave(PlayerQuitEvent e){
            if(e.getPlayer().equals(p)){
                ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
                HandlerList.unregisterAll(this);
                sign.getBlock().setType(Material.AIR);
            }
        }
    }

    private void registerSignUpdateListener() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        packetListener = new PacketAdapter(AuctionMaster.plugin, PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPlayer().equals(p)) {
                    String input;
                    if(Bukkit.getVersion().contains("1.8"))
                        input = event.getPacket().getChatComponentArrays().read(0)[0].getJson().replaceAll("\"", "");
                    else
                        input = event.getPacket().getStringArrays().read(0)[0];

                    Bukkit.getScheduler().runTask(AuctionMaster.plugin, () -> {
                        try{
                            int timeInput = Integer.parseInt(input);
                            if(rightClick)
                                auction.addMinutesToAuction(timeInput);
                            else
                                auction.setEndingDate(ZonedDateTime.now().toInstant().toEpochMilli()+timeInput*60000);
                        }catch(Exception x){
                            p.sendMessage(utilsAPI.chat(p, "&cInvalid number."));
                        }

                        manager.removePacketListener(this);
                        HandlerList.unregisterAll(listener);

                        sign.getBlock().setType(Material.AIR);
                        new ViewAuctionAdminMenu(p, auction, goBackTo);
                    });
                }
            }
        };

        manager.addPacketListener(packetListener);
    }
}
