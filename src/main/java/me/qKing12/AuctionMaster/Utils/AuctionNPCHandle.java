package me.qKing12.AuctionMaster.Utils;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Menus.MainAuctionMenu;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class AuctionNPCHandle implements Listener {

    private ArrayList<Location> holoLocations = new ArrayList<>();
    private final String line1;
    private final String line2;
    private final double holoHeight;

    public AuctionNPCHandle(){
        Bukkit.getPluginManager().registerEvents(this, AuctionMaster.plugin);
        line1= Utils.chat(AuctionMaster.plugin.getConfig().getString("auction-npc-title.line-1"));
        line2= AuctionMaster.plugin.getConfig().getString("auction-npc-title.line-2");
        holoHeight= AuctionMaster.plugin.getConfig().getDouble("auction-npc-title.line-1-height");
    }

    public void unloadHolos(){
        if (holoLocations.isEmpty())
            return;

        for (Location loc : holoLocations) {
            ArmorStand am = getHologram(loc);
            if (am == null || am.isDead())
                continue;

            am.remove();
        }

        holoLocations = new ArrayList<>();
    }

    public void debugHolos() {
        unloadHolos();

        NPCRegistry npcs = CitizensAPI.getNPCRegistry();
        if (npcs == null)
            return;

        for (NPC debug : npcs) {
            if (debug == null || debug.getName() == null)
                continue;
            if (!debug.getName().equalsIgnoreCase(Utils.chat("&r" + line2)))
                continue;

            Location asLoc = debug.getStoredLocation();
            if (asLoc == null)
                continue;

            asLoc.setY(asLoc.getY() + holoHeight);
            asLoc.setYaw((float) 0.0);

            holoLocations.add(asLoc);
            createHologram(debug);
        }
    }

    private void createHologram(NPC npc){
        Location loc=npc.getStoredLocation();
        if (loc == null)
            return;

        boolean loaded = loc.getChunk().isLoaded() || loc.getChunk().load();
        if (!loaded)
            return;

        loc.add(0, holoHeight, 0);
        loc.setYaw((float) 0.0);
        ArmorStand am = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        am.setMarker(true);
        am.setVisible(false);
        am.setSmall(true);
        am.setArms(true);
        am.setCustomNameVisible(true);
        am.setGravity(false);
        am.setRemoveWhenFarAway(false);
        am.setCustomName(line1);
        holoLocations.add(am.getLocation());
    }

    private ArmorStand getHologram(Location loc){
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 3, 1)) {
            if (entity instanceof ArmorStand)
                return (ArmorStand) entity;
        }
        return null;
    }

    public void createNpc(Player p){
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Utils.chat("&r" + line2));
        npc.setProtected(true);
        npc.spawn(p.getLocation());
        SkinnableEntity entity = (SkinnableEntity) npc.getEntity();
        if (AuctionMaster.plugin.getConfig().getBoolean("auction-npc-skin-texture")) {
            String signature = AuctionMaster.plugin.getConfig().getString("auction-npc-skin-signature");
            String data = AuctionMaster.plugin.getConfig().getString("auction-npc-skin-data");
            entity.setSkinPersistent("AuctionMaster", signature, data);
        } else
            entity.setSkinName(AuctionMaster.plugin.getConfig().getString("auction-npc-skin-name"));

        createHologram(npc);
    }

    @EventHandler
    public void onNpcClick(NPCRightClickEvent event){
        NPC npc = event.getNPC();
        if(npc.getName().equals(Utils.chat("&r"+line2))) {
            if(!AuctionMaster.plugin.getConfig().getString("auction-use-permission").equals("none"))
                if(!event.getClicker().hasPermission(AuctionMaster.plugin.getConfig().getString("auction-use-permission"))){
                    event.getClicker().sendMessage(Utils.chat(AuctionMaster.plugin.getConfig().getString("auction-no-permission")));
                    return;
                }
            Utils.playSound(event.getClicker(), "ah-npc-click");
            new MainAuctionMenu(event.getClicker());
            
        }
    }

    @EventHandler
    public void onNpcClick(NPCLeftClickEvent event){
        NPC npc = event.getNPC();
        if(npc.getName().equals(Utils.chat("&r"+line2))) {
            if(!AuctionMaster.plugin.getConfig().getString("auction-use-permission").equals("none"))
                if(!event.getClicker().hasPermission(AuctionMaster.plugin.getConfig().getString("auction-use-permission"))){
                    event.getClicker().sendMessage(Utils.chat(AuctionMaster.plugin.getConfig().getString("auction-no-permission")));
                    return;
                }
            Utils.playSound(event.getClicker(), "ah-npc-click");
            new MainAuctionMenu(event.getClicker());
        }
    }

    @EventHandler
    public void onNpcRemove(NPCRemoveEvent event){
        NPC npc = event.getNPC();
        if(npc.getName().equals(Utils.chat("&r"+line2))) {
            ArmorStand am = getHologram(event.getNPC().getStoredLocation());
            if(am!=null) {
                holoLocations.remove(am.getLocation());
                am.remove();
            }
        }
    }

    @EventHandler
    public void onNpcMove(NPCTeleportEvent event){
        NPC npc = event.getNPC();
        if(npc.getName().equals(Utils.chat("&r"+line2))) {
            ArmorStand am = getHologram(event.getFrom());
            if(am!=null){
                Location loc = am.getLocation().clone();
                holoLocations.remove(loc);
                loc=event.getTo().clone();
                loc.add(0, holoHeight, 0);
                loc.setYaw((float) 0.0);
                holoLocations.add(loc);
                am.teleport(loc);
            }
        }
    }

}
