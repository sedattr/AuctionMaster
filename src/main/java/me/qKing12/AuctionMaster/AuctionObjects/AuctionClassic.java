package me.qKing12.AuctionMaster.AuctionObjects;

import me.qKing12.AuctionMaster.API.Events.*;
import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.ZonedDateTime;
import java.util.*;

import static me.qKing12.AuctionMaster.AuctionMaster.utilsAPI;

public class AuctionClassic implements Auction{
    String id;
    private double coins;
    private long endingDate;
    private String sellerDisplayName;
    private String sellerName;
    private String sellerUUID;
    private ItemStack item;
    private String displayName;
    private Bids bids;

    public boolean isBIN(){
        return false;
    }

    private boolean sellerClaimed=false;

    public boolean checkForDeletion(){
        if(sellerClaimed && bids.allBidsClaimed()){
            AuctionMaster.auctionsDatabase.deleteAuction(id);
            AuctionMaster.auctionsHandler.auctions.remove(id);
            return true;
        }
        return false;
    }

    public boolean forceEnd() {
        if (isEnded())
            return false;
        endingDate = ZonedDateTime.now().toInstant().toEpochMilli();
        HashMap<String, String> toChange = new HashMap<>();
        toChange.put("ending", String.valueOf(endingDate));
        AuctionMaster.auctionsDatabase.updateAuctionField(id, toChange);
        return true;
    }

    public void addMinutesToAuction(int minutes){
        endingDate+=minutes* 60000L;
        HashMap<String, String> toChange = new HashMap<>();
        toChange.put("ending", String.valueOf(endingDate));
        AuctionMaster.auctionsDatabase.updateAuctionField(id, toChange);
    }

    public void setEndingDate(long date){
        endingDate=date;
        HashMap<String, String> toChange = new HashMap<>();
        toChange.put("ending", String.valueOf(endingDate));
        AuctionMaster.auctionsDatabase.updateAuctionField(id, toChange);
    }

    public void adminRemoveAuction(boolean withDeliveries){
        if(withDeliveries && !sellerClaimed) {
            if (bids.getNumberOfBids() == 0 || !isEnded())
                AuctionMaster.deliveries.addItem(sellerUUID, item);
            else
                AuctionMaster.deliveries.addCoins(sellerUUID, coins);
        }
        HashMap<String, Double> cache = new HashMap<>();
        for(Bids.Bid bid : bids.getBidList()){
            if(!bid.isClaimed() && !cache.containsKey(bid.getBidderUUID())) {
                try {
                    AuctionMaster.auctionsHandler.bidAuctions.get(bid.getBidderUUID()).remove(this);
                    if (AuctionMaster.auctionsHandler.bidAuctions.get(bid.getBidderUUID()).isEmpty())
                        AuctionMaster.auctionsHandler.bidAuctions.remove(bid.getBidderUUID());
                }catch(Exception x){
                    AuctionMaster.plugin.getLogger().info("Tried to remove auction from own bids for player "+bid.getBidderUUID()+" but it was not there!");
                }
            }
            cache.put(bid.getBidderUUID(), bid.getCoins());
        }
        for(String uuid : cache.keySet()){
            AuctionMaster.auctionsDatabase.removeFromOwnBids(uuid, id);
        }
        if(withDeliveries){
            if(bids.getNumberOfBids()!=0 && isEnded())
                cache.put(bids.getTopBidUUID(), -1d);
            for(Map.Entry<String, Double> entry : cache.entrySet()){
                if(entry.getValue()==-1)
                    AuctionMaster.deliveries.addItem(entry.getKey(), item);
                AuctionMaster.deliveries.addCoins(entry.getKey(), entry.getValue());
            }
        }

        if(!sellerClaimed) {
            AuctionMaster.auctionsDatabase.removeFromOwnAuctions(sellerUUID, id);
            try {
                AuctionMaster.auctionsHandler.ownAuctions.get(sellerUUID).remove(this);
                if (AuctionMaster.auctionsHandler.ownAuctions.get(sellerUUID).isEmpty()) {
                    AuctionMaster.auctionsHandler.ownAuctions.remove(sellerUUID);
                }
            }catch(Exception x){
                AuctionMaster.plugin.getLogger().info("Tried to remove auction from own auctions for player "+sellerUUID+" but it was not there!");
            }
        }

        AuctionMaster.auctionsDatabase.deleteAuction(id);
        AuctionMaster.auctionsHandler.removeAuctionFromBrowse(this);
        AuctionMaster.auctionsHandler.auctions.remove(id);
        bids=null;
        sellerDisplayName=null;
        sellerName=null;
        sellerUUID=null;
        item=null;
        displayName=null;
    }

    public AuctionClassic(String id, double coins, long endingDate, String sellerDisplayName, String sellerName, String sellerUUID, String item, String displayName, String bids, boolean sellerClaimed){
        this.item= Utils.itemFromBase64(item);
        if(this.item==null)
            return;
        this.id=id;
        this.bids=new Bids(bids, id);
        this.coins=coins;
        this.endingDate=endingDate;
        this.sellerDisplayName=sellerDisplayName;
        this.sellerName=sellerName;
        this.sellerUUID=sellerUUID;
        this.displayName=displayName;
        this.sellerClaimed=sellerClaimed;
    }

    public AuctionClassic(Player seller, double startingBid, long duration, ItemStack item){
        id=UUID.randomUUID().toString();
        coins=startingBid;
        sellerUUID=seller.getUniqueId().toString();
        sellerName=seller.getName();
        sellerDisplayName=seller.getDisplayName();
        endingDate= ZonedDateTime.now().toInstant().toEpochMilli()+duration;
        this.item=item;
        displayName= Utils.getDisplayName(item);
        bids = new Bids(id);
        Utils.injectToLog("[Auction Created] "+seller.getName()+" created an classic auction for "+displayName+" with ID="+id);
        Utils.injectToLog("[Advanced Base64 Item] ^"+seller.getName()+": "+ Utils.itemToBase64(item).replace("\r\n", "%nll%").replace("\n", "%nll%"));
    }

    public boolean sellerClaim(Player player){
        if (sellerClaimed)
            return false;
        if (player == null)
            return false;
        sellerClaimed=true;

        String uuid = player.getUniqueId().toString();
        if (bids.getNumberOfBids() == 0) {
            if (Utils.getEmptySlots(player) == 0) {
                this.singleClick = false;

                player.sendMessage(utilsAPI.chat(player, AuctionMaster.auctionsManagerCfg.getString("not-enough-inventory-space")));
                return false;
            }

            if (!AuctionMaster.auctionsDatabase.removeFromOwnAuctions(uuid, id)) {
                sellerClaimed = false;
                return false;
            }

            SellerClaimExpiredAuctionEvent event = new SellerClaimExpiredAuctionEvent(player, this, item);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                sellerClaimed = false;
                return false;
            }

            player.getInventory().addItem(item);
            Utils.playSound(player, "claim-item");
            player.updateInventory();
        }
        else {
            if (!AuctionMaster.auctionsDatabase.removeFromOwnAuctions(uuid, id)) {
                sellerClaimed = false;
                return false;
            }

            SellerClaimEndedAuctionEvent event = new SellerClaimEndedAuctionEvent(player, this, this.coins);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                sellerClaimed=false;
                return false;
            }

            AuctionMaster.economy.addMoney(player, coins);
            Utils.playSound(player, "claim-money");
        }

        AuctionMaster.auctionsHandler.ownAuctions.getOrDefault(uuid, new ArrayList<>()).remove(this);
        if (AuctionMaster.auctionsHandler.ownAuctions.getOrDefault(uuid, new ArrayList<>()).isEmpty())
            AuctionMaster.auctionsHandler.ownAuctions.remove(uuid);

        if (!checkForDeletion()) {
            HashMap<String, String> toChange = new HashMap<>();
            toChange.put("sellerClaimed", "1");
            AuctionMaster.auctionsDatabase.updateAuctionField(id, toChange);
        }

        AuctionDeleteEvent event = new AuctionDeleteEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);

        player.closeInventory();
        return true;
    }

    public void claimBid(Player player){
        if (bids.getTopBidUUID().equalsIgnoreCase(player.getUniqueId().toString())) {
            topBidClaim(player);
            Utils.playSound(player, "claim-item");
        }
        else {
            Utils.playSound(player, "claim-money");
            normalBidClaim(player);
        }
    }

    public void topBidClaim(Player player){
        if (singleClick)
            return;
        singleClick = true;

        if (bids.getBidList().get(bids.getBidList().size()-1).isClaimed()) {
            singleClick = false;
            return;
        }

        String uuid = player.getUniqueId().toString();
        if (Utils.getEmptySlots(player) == 0) {
            singleClick = false;

            player.sendMessage(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("not-enough-inventory-space")));
            return;
        }

        if (!AuctionMaster.auctionsDatabase.removeFromOwnBids(uuid, id)) {
            singleClick = false;
            return;
        }

        AuctionMaster.auctionsHandler.bidAuctions.getOrDefault(uuid, new ArrayList<>()).remove(this);
        if(AuctionMaster.auctionsHandler.bidAuctions.getOrDefault(uuid, new ArrayList<>()).isEmpty())
            AuctionMaster.auctionsHandler.bidAuctions.remove(uuid);

        bids.claimBid(player);
        player.getInventory().addItem(item);
        Bukkit.getPluginManager().callEvent(new TopBidClaim(player, this, item));

        if (!checkForDeletion()) {
            HashMap<String, String> toChange = new HashMap<>();
            toChange.put("bids", "'"+bids.getBidsAsString()+"'");
            AuctionMaster.auctionsDatabase.updateAuctionField(id, toChange);
        }
        player.closeInventory();
        singleClick = false;
    }

    private boolean singleClick;
    public void normalBidClaim(Player player) {
        if (singleClick)
            return;
        singleClick = true;

        double coins = bids.claimBid(player);
        Bukkit.getScheduler().runTask(AuctionMaster.plugin, () -> Bukkit.getPluginManager().callEvent(new LostBidClaim(player, this, coins)));
        AuctionMaster.economy.addMoney(player, coins);

        AuctionMaster.auctionsDatabase.removeFromOwnBids(player.getUniqueId().toString(), id);
        String uuid = player.getUniqueId().toString();
        AuctionMaster.auctionsHandler.bidAuctions.getOrDefault(uuid, new ArrayList<>()).remove(this);

        if (AuctionMaster.auctionsHandler.bidAuctions.get(uuid).isEmpty())
            AuctionMaster.auctionsHandler.bidAuctions.remove(uuid);

        if (!checkForDeletion()) {
            HashMap<String, String> toChange = new HashMap<>();
            toChange.put("bids", "'"+bids.getBidsAsString()+"'");
            AuctionMaster.auctionsDatabase.updateAuctionField(id, toChange);
        }

        player.closeInventory();
        singleClick = false;
    }

    public boolean placeBid(Player player, double amount, int cacheBids){
        if(isEnded() || bids==null || cacheBids!=bids.getNumberOfBids()) {
            return false;
        }
        Utils.injectToLog("[Bid Place] "+player.getName()+" placed a bid of "+amount+"on classic auction with ID="+id);

        endingDate+= AuctionMaster.plugin.getConfig().getInt("add-time-to-auction")* 1000L;
        coins=amount;
        if(AuctionMaster.auctionsHandler.bidAuctions.containsKey(player.getUniqueId().toString())){
            if(getLastBid(player.getUniqueId().toString())==null) {
                AuctionMaster.auctionsHandler.bidAuctions.get(player.getUniqueId().toString()).add(this);
                AuctionMaster.auctionsDatabase.addToOwnBids(player.getUniqueId().toString(), id);
            }
        }
        else{
            ArrayList<Auction> auctions = new ArrayList<>();
            auctions.add(this);
            AuctionMaster.auctionsHandler.bidAuctions.put(player.getUniqueId().toString(), auctions);
            AuctionMaster.auctionsDatabase.addToOwnBids(player.getUniqueId().toString(), id);
        }
        bids.placeBids(player, amount);

        HashMap<String, String> fields = new HashMap<>();
        fields.put("coins", String.valueOf(coins));
        fields.put("ending", String.valueOf(endingDate));
        fields.put("bids", "'"+bids.getBidsAsString()+"'");
        AuctionMaster.auctionsDatabase.updateAuctionField(id, fields);

        Player seller = Bukkit.getPlayer(UUID.fromString(sellerUUID));
        if(seller!=null){
            seller.sendMessage(Utils.chat(AuctionMaster.bidsRelatedCfg.getString("bid-message")).replace("%bidder%", player.getDisplayName()).replace("%bid-amount%", AuctionMaster.numberFormatHelper.formatNumber(amount)).replace("%bid-item%", displayName));
        }

        ArrayList<Player> outbidPlayers = new ArrayList<>();
        for(Bids.Bid bid : bids.getBidList()){
            Player bidder = Bukkit.getPlayer(UUID.fromString(bid.getBidderUUID()));
            if(bidder!=null && !outbidPlayers.contains(bidder) && !bidder.equals(player))
                outbidPlayers.add(bidder);
        }
        String outbidMsg = "";
        for (String msj : AuctionMaster.configLoad.outbidMessage)
            outbidMsg=outbidMsg+ Utils.chat(msj).replace("%outbid-player-display-name%", player.getDisplayName()).replace("%top-bid%", AuctionMaster.numberFormatHelper.formatNumber(coins)).replace("%bid-item%", displayName)+"\n";
        TextComponent clickMess = new TextComponent();
        clickMess.setText(outbidMsg.substring(0, outbidMsg.length()-1));
        clickMess.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/@ahview "+id));
        for(Player p : outbidPlayers){
            Utils.playSound(p, "outbid");
            p.spigot().sendMessage(clickMess);
        }

        return true;
    }

    public Bids.Bid getLastBid(String uuid){
        ListIterator<Bids.Bid> bidIterator = bids.getBidList().listIterator(bids.getBidList().size());
        while(bidIterator.hasPrevious()){
            Bids.Bid bid = bidIterator.previous();
            if(bid.getBidderUUID().equals(uuid))
                return bid;
        }
        return null;
    }

    public boolean isEnded(){
        return endingDate<=ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public ItemStack getBidHistory(){
        ItemStack toReturn= AuctionMaster.configLoad.bidHistoryDefaultItem.clone();

        int numberOfBids=bids.getNumberOfBids();
        if(numberOfBids!=0){
            ArrayList<String> lore = new ArrayList<>();
            int toSkip=0;
            if(numberOfBids>7)
                toSkip=numberOfBids-7;
            Iterator<Bids.Bid> bidsIterator = bids.getBidList().iterator();
            for(int i=0;i<toSkip;i++)
                bidsIterator.next();

            while(bidsIterator.hasNext()){
                Bids.Bid bid = bidsIterator.next();
                for(String line : AuctionMaster.configLoad.bidHistoryItemLoreStructure){
                    lore.add(Utils.chat(line
                            .replace("%bid-amount%", AuctionMaster.numberFormatHelper.formatNumber(bid.getCoins()))
                            .replace("%bidder-display-name%", bid.getBidderDisplayName())
                            .replace("%when-bidded%", Utils.fromMiliseconds((int)(ZonedDateTime.now().toInstant().toEpochMilli()-bid.getBidDate())))
                    ));
                }
            }
            ItemMeta meta = toReturn.getItemMeta();
            meta.setLore(lore);
            toReturn.setItemMeta(meta);
        }

        return toReturn;
    }

    public ItemStack generateEndedDisplay(){
        ItemStack display = item.clone();
        ItemMeta meta = display.getItemMeta();
        meta.setDisplayName(displayName);
        ArrayList<String> lore = new ArrayList<>();
        if(meta.getLore()!=null)
            lore.addAll(meta.getLore());
        durationLine=lore.size();
        if(bids.getNumberOfBids()==0){
            for(String line : AuctionMaster.auctionsManagerCfg.getStringList("auction-item-lore.auction-item-expired")) {
                lore.add(Utils.chat(line
                        .replace("%display-name-seller%", sellerDisplayName)
                        .replace("%starting-bid%", AuctionMaster.numberFormatHelper.formatNumber(coins))
                ));
            }
        }
        else{
            for(String line : AuctionMaster.auctionsManagerCfg.getStringList("auction-item-lore.auction-item-ended")) {
                lore.add(Utils.chat(line
                        .replace("%display-name-seller%", sellerDisplayName)
                        .replace("%starting-bid%", AuctionMaster.numberFormatHelper.formatNumber(coins))
                        .replace("%top-bid%", AuctionMaster.numberFormatHelper.formatNumber(bids.getTopBidCoins()))
                        .replace("%top-bid-player%", bids.getTopBid())
                        .replace("%bids%", String.valueOf(bids.getNumberOfBids()))
                ));
            }
        }
        meta.setLore(lore);
        display.setItemMeta(meta);
        endedDisplay=display;
        return display;
    }

    public ItemStack generateDisplay(){
        ItemStack display = item.clone();
        ItemMeta meta = display.getItemMeta();
        meta.setDisplayName(displayName);
        ArrayList<String> lore = new ArrayList<>();
        if(meta.getLore()!=null)
            lore.addAll(meta.getLore());
        durationLine=lore.size();
        if(bids.getNumberOfBids()==0){
            int index=0;
            for(String line : AuctionMaster.auctionsManagerCfg.getStringList("auction-item-lore.auction-item-no-bid")) {
                lore.add(Utils.chat(line
                        .replace("%display-name-seller%", sellerDisplayName)
                        .replace("%starting-bid%", AuctionMaster.numberFormatHelper.formatNumber(coins))
                        .replace("%duration%", Utils.fromMilisecondsAuction(endingDate-ZonedDateTime.now().toInstant().toEpochMilli()))
                ));
                if(line.contains("%duration%")){
                    durationLine+=index;
                    durationLineString= Utils.chat(line);
                }
                index++;
            }
        }
        else{
            int index=0;
            for(String line : AuctionMaster.auctionsManagerCfg.getStringList("auction-item-lore.auction-item-with-bids")) {
                lore.add(Utils.chat(line
                        .replace("%display-name-seller%", sellerDisplayName)
                        .replace("%starting-bid%", AuctionMaster.numberFormatHelper.formatNumber(coins))
                        .replace("%duration%", Utils.fromMilisecondsAuction(endingDate-ZonedDateTime.now().toInstant().toEpochMilli()))
                        .replace("%top-bid%", AuctionMaster.numberFormatHelper.formatNumber(bids.getTopBidCoins()))
                        .replace("%top-bid-player%", bids.getTopBid())
                        .replace("%bids%", String.valueOf(bids.getNumberOfBids()))
                ));
                if(line.contains("%duration%")){
                    durationLine+=index;
                    durationLineString= Utils.chat(line);
                }
                index++;
            }
        }
        this.lore=lore;
        meta.setLore(lore);
        display.setItemMeta(meta);
        this.cacheBids=bids.getNumberOfBids();
        return display;
    }

    private ArrayList<String> lore;
    private String durationLineString;
    private int durationLine;
    private int cacheBids=0;
    private ItemStack endedDisplay;
    public ItemStack getUpdatedDisplay(){
        if(endedDisplay!=null){
            return endedDisplay.clone();
        }
        else if(ZonedDateTime.now().toInstant().toEpochMilli()>=endingDate){
            return generateEndedDisplay().clone();
        }
        else if(cacheBids!=bids.getNumberOfBids() || this.lore==null) {
            return generateDisplay().clone();
        }
        else {
            try {
                ItemStack updated = item.clone();
                ItemMeta meta = updated.getItemMeta();
                meta.setDisplayName(displayName);
                ArrayList<String> lore;
                lore = (ArrayList<String>) this.lore.clone();
                lore.set(durationLine, durationLineString.replace("%duration%", Utils.fromMilisecondsAuction(endingDate - ZonedDateTime.now().toInstant().toEpochMilli())));
                meta.setLore(lore);
                updated.setItemMeta(meta);
                return updated;
            }catch(Exception x){
                return generateDisplay().clone();
            }
        }
    }

    public String getSellerUUID(){
        return sellerUUID;
    }

    public String getSellerDisplayName() {
        return sellerDisplayName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getId() {
        return id;
    }

    public Bids getBids(){
        return bids;
    }

    public long getEndingDate() {
        return endingDate;
    }

    public ItemStack getItemStack() {
        return item.clone();
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCoins() {
        return coins;
    }
}