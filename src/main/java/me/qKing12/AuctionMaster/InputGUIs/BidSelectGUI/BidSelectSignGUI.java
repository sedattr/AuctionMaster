package me.qKing12.AuctionMaster.InputGUIs.BidSelectGUI;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.ViewAuctionMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class BidSelectSignGUI {

    private PacketAdapter packetListener;
    private final Player p;
    private Sign sign;
    private final LeaveListener listener = new LeaveListener();
    private final double minimumBid;
    private final Auction auction;
    private final String goBackTo;

    public BidSelectSignGUI(Player p, Auction auction, String goBackTo, double minimumBid) {
        this.p=p;
        this.auction=auction;
        this.goBackTo=goBackTo;
        this.minimumBid=minimumBid;
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

        ArrayList<String> lines = (ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("starting-bid-sign-message");
        sign.setLine(1, utilsAPI.chat(p, lines.get(0)));
        sign.setLine(2, utilsAPI.chat(p, lines.get(1)));
        sign.setLine(3, utilsAPI.chat(p, lines.get(2)));

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
                            double bidSelect = AuctionMaster.numberFormatHelper.useDecimals? Double.parseDouble(input):Math.floor(Double.parseDouble(input));
                            if(bidSelect>=minimumBid)
                                new ViewAuctionMenu(p, auction, goBackTo, bidSelect);
                            else
                                new ViewAuctionMenu(p, auction, goBackTo, 0);
                        }catch(Exception x){
                            p.sendMessage(utilsAPI.chat(p, AuctionMaster.auctionsManagerCfg.getString("edit-bid-deny-message")));
                            new ViewAuctionMenu(p, auction, goBackTo, 0);
                        }

                        manager.removePacketListener(this);
                        HandlerList.unregisterAll(listener);

                        sign.getBlock().setType(Material.AIR);
                    });
                }
            }
        };

        manager.addPacketListener(packetListener);
    }
}
