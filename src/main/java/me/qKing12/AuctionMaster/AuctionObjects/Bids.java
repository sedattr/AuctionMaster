package me.qKing12.AuctionMaster.AuctionObjects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Bids {

    private final ArrayList<Bid> bids = new ArrayList<>();
    private String topBid="";
    private double topBidCoins=0;
    private final String id;

    public String getBidsAsString(){
        String toReturn=" ";
        for(Bid bid : bids){
            toReturn=toReturn.concat(","+bid.getBidAsString());
        }
        if(toReturn.length()>1)
            toReturn=toReturn.substring(2);
        toReturn=topBid+" "+topBidCoins+",,,"+toReturn;
        return toReturn;
    }

    public boolean allBidsClaimed(){
        for(Bid bid : bids){
            if(!bid.claimed)
                return false;
        }
        return true;
    }

    public Bids(String bids, String id){
        this.id=id;
        String[] bidsTopData=bids.split(",,,")[0].split(" ");
        if(bidsTopData.length==2) {
            this.topBid = bidsTopData[0];
            this.topBidCoins = Double.parseDouble(bidsTopData[1]);
        }
        else{
            String coins=bidsTopData[bidsTopData.length-1];
            this.topBid=bids.split(",,,")[0].replace(" "+coins, "");
            this.topBidCoins = Double.parseDouble(coins);
        }
        bids=bids.split(",,,")[1];
        if(bids.equals(" "))
            return;
        for(String bid : bids.split(",")){
            String[] bidData = bid.split("#-#");
            this.bids.add(new Bid(Long.parseLong(bidData[0]), Double.parseDouble(bidData[1]), bidData[2], bidData[3], bidData[4], Boolean.parseBoolean(bidData[5])));
        }
    }

    public Bids(String id){
        this.id=id;
    }

    public void placeBids(Player player, double amount){
        bids.add(new Bid(player, amount));
        topBid=player.getDisplayName();
        topBidCoins=amount;
    }

    public double claimBid(Player player){
        String uuid = player.getUniqueId().toString();
        double toReturn=0;
        for(Bid bid : bids){
            if(bid.bidderUUID.equals(uuid)){
                bid.claimed=true;
                toReturn=bid.coins;
            }
        }
        return toReturn;
    }

    public int getNumberOfBids(){
        return !bids.isEmpty() ? bids.size() : 0;
    }

    public double getTopBidCoins(){
        return topBidCoins;
    }

    public String getTopBid(){
        return topBid;
    }

    public String getTopBidUUID() {
        if(bids.isEmpty())
            return "";
        return bids.get(bids.size()-1).getBidderUUID();
    }

    public ArrayList<Bid> getBidList(){
        return bids;
    }

    public class Bid{
        private final long bidDate;
        private final double coins;
        private final String bidderDisplayName;
        private final String bidderName;
        private final String bidderUUID;
        private boolean claimed=false;

        public String getBidAsString(){
            return bidDate+"#-#"+coins+"#-#"+bidderDisplayName+"#-#"+bidderName+"#-#"+bidderUUID+"#-#"+claimed;
        }

        public Bid(long bidDate, double coins, String bidderDisplayName, String bidderName, String bidderUUID, boolean claimed){
            this.bidDate=bidDate;
            this.coins=coins;
            this.bidderDisplayName=bidderDisplayName;
            this.bidderName=bidderName;
            this.bidderUUID=bidderUUID;
            this.claimed=claimed;
        }

        public Bid(Player player, double amount){
            bidDate= ZonedDateTime.now().toInstant().toEpochMilli();
            coins=amount;
            bidderDisplayName=player.getDisplayName();
            bidderName=player.getName();
            bidderUUID=player.getUniqueId().toString();
        }

        public String getBidderUUID(){
            return bidderUUID;
        }

        public String getBidderName(){
            return bidderName;
        }

        public long getBidDate() {
            return bidDate;
        }

        public double getCoins(){
            return coins;
        }

        public boolean isClaimed() {
            return claimed;
        }

        public String getBidderDisplayName(){
            return bidderDisplayName;
        }
    }
}