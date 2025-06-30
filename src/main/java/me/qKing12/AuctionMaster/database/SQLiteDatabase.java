package me.qKing12.AuctionMaster.database;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.AuctionObjects.Auction;
import me.qKing12.AuctionMaster.AuctionObjects.AuctionBIN;
import me.qKing12.AuctionMaster.AuctionObjects.AuctionClassic;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLiteDatabase implements DatabaseHandler {
    private Connection connection;
    private final String url;

    public SQLiteDatabase() {
        this.url = "jdbc:sqlite:" + AuctionMaster.plugin.getDataFolder() + "/database/auctionsData.db";
        this.connection = getConnection();

        if (this.connection == null) {
            AuctionMaster.plugin.getLogger().warning("There is a problem in SQLite database!");
            return;
        }

        loadAuctionsFile();
        loadAuctionsDataFromFile();
    }

    public Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed())
                return this.connection;

            Class.forName("org.sqlite.JDBC");
            return this.connection = DriverManager.getConnection(this.url);
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
            AuctionMaster.plugin.getLogger().warning("There is a problem in SQLite database!");
        }

        return null;
    }

    private void loadAuctionsFile() {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Auctions " +
                                "(id VARCHAR(36) not NULL, " +
                                " coins DOUBLE(25, 0), " +
                                " ending BIGINT(15), " +
                                " sellerDisplayName VARCHAR(50), " +
                                " sellerName VARCHAR(16), " +
                                " sellerUUID VARCHAR(36), " +
                                " item MEDIUMTEXT, " +
                                " displayName VARCHAR(40), " +
                                " bids MEDIUMTEXT, " +
                                " sellerClaimed BOOL, "+
                                " PRIMARY KEY ( id ))"
                )
        ) {
            stmt.execute();
            AuctionMaster.plugin.getLogger().warning("Auctions database is ready!");
        } catch (Exception x) {
            AuctionMaster.plugin.getLogger().warning("There is a problem in Auctions database!");
            x.printStackTrace();
        }

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS AuctionLists " +
                                "(id VARCHAR(36) not NULL, " +
                                " ownAuctions MEDIUMTEXT, " +
                                " ownBids MEDIUMTEXT, " +
                                " PRIMARY KEY ( id ))")
        ) {
            stmt.execute();
            AuctionMaster.plugin.getLogger().warning("AuctionLists database is ready!");
        } catch (Exception x) {
            AuctionMaster.plugin.getLogger().warning("There is a problem in AuctionLists database!");
            x.printStackTrace();
        }

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS PreviewData " +
                                "(id VARCHAR(36) not NULL, " +
                                " item MEDIUMTEXT, " +
                                " PRIMARY KEY ( id ))")
        ) {
            stmt.execute();
            AuctionMaster.plugin.getLogger().warning("PreviewData database is ready!");
        } catch (Exception x) {
            AuctionMaster.plugin.getLogger().warning("There is a problem in PreviewData database!");
            x.printStackTrace();
        }
    }

    public void loadPreviewItems() {
        try (
                Connection Auctions = getConnection();
                PreparedStatement select = Auctions.prepareStatement("SELECT * FROM PreviewData")
        ) {
            ResultSet resultSet = select.executeQuery();

            while (resultSet.next()) {
                String uuid = resultSet.getString(1);
                if (uuid.equalsIgnoreCase("serverCloseDate"))
                    continue;

                ItemStack result = Utils.itemFromBase64(resultSet.getString(2));

                if (result == null) {
                    PreparedStatement stmt = Auctions.prepareStatement("DELETE FROM PreviewData WHERE id = ?");
                    stmt.setString(1, uuid);
                    stmt.executeUpdate();
                    Bukkit.getLogger().warning(uuid + " removed from auctionPreviewItems, itemData: " + resultSet.getString(2));
                    continue;
                }

                AuctionMaster.auctionsHandler.previewItems.put(uuid, result);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public void deletePreviewItems(String id) {
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = getConnection();
                    PreparedStatement stmt1 = Auctions.prepareStatement("DELETE FROM PreviewData WHERE id = ?;")
            ) {
                stmt1.setString(1, id);
                stmt1.executeUpdate();
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]"))
                    try {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> deletePreviewItems(id), 7);
                    } catch (Exception e) {
                        Bukkit.getScheduler().runTaskLater(AuctionMaster.plugin, () -> deletePreviewItems(id), 7);
                        e.printStackTrace();
                    }
                else
                    x.printStackTrace();
            }
        });
    }

    public void registerPreviewItem(String player, String item) {
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = getConnection();
                    PreparedStatement stmt1 = Auctions.prepareStatement("REPLACE INTO PreviewData (item, id) VALUES (?, ?);")

            ) {
                stmt1.setString(1, item);
                stmt1.setString(2, player);
                stmt1.executeUpdate();
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    try {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> registerPreviewItem(player, item), 7);
                    } catch (Exception e) {
                        Bukkit.getScheduler().runTaskLater(AuctionMaster.plugin, () -> registerPreviewItem(player, item), 7);
                        e.printStackTrace();
                    }
                } else {
                    x.printStackTrace();
                }
            }
        });
    }

    public void removePreviewItem(String player){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt = Auctions.prepareStatement("DELETE FROM PreviewData WHERE id = ?")
            ) {
                stmt.setString(1, player);
                stmt.executeUpdate();
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    try {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> removePreviewItem(player), 7);
                    } catch (Exception e) {
                        Bukkit.getScheduler().runTaskLater(AuctionMaster.plugin, () -> removePreviewItem(player), 7);
                        e.printStackTrace();
                    }
                } else {
                    x.printStackTrace();
                }
            }
        });
    }

    public void insertAuction(Auction auction){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try(
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt = Auctions.prepareStatement("INSERT INTO Auctions VALUES (?, ?, ?, ?, ?, ?, ?, ?, '"+(auction.isBIN()?"BIN":"")+" 0,,, ', 0)")
            ){
                stmt.setString(1, auction.getId());
                stmt.setDouble(2, auction.getCoins());
                stmt.setLong(3, auction.getEndingDate());
                stmt.setString(4, auction.getSellerDisplayName());
                stmt.setString(5, auction.getSellerName());
                stmt.setString(6, auction.getSellerUUID());
                stmt.setString(7, Utils.itemToBase64(auction.getItemStack()));
                stmt.setString(8, auction.getDisplayName());
                stmt.executeUpdate();
            } catch(Exception x){
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    try {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> insertAuction(auction), 7);
                    } catch (Exception e) {
                        Bukkit.getScheduler().runTaskLater(AuctionMaster.plugin, () -> insertAuction(auction), 7);
                        e.printStackTrace();
                    }
                } else {
                    x.printStackTrace();
                }
            }
        });
    }

    public void updateAuctionField(String id, HashMap<String, String> toUpdate){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            String toSet = "";
            for(Map.Entry<String, String> entry : toUpdate.entrySet())
                toSet=toSet.concat(","+entry.getKey()+"="+entry.getValue());

            toSet=toSet.substring(1);
            try(
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt = Auctions.prepareStatement("UPDATE Auctions SET "+toSet+" WHERE id = ?")
            ){
                stmt.setString(1, id);
                stmt.executeUpdate();
            }catch(Exception x){
                if(x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    try {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> updateAuctionField(id, toUpdate), 7);
                    } catch(Exception e) {
                        Bukkit.getScheduler().runTaskLater(AuctionMaster.plugin, () -> updateAuctionField(id, toUpdate), 7);
                        e.printStackTrace();
                    }
                } else {
                    x.printStackTrace();
                }
                AuctionMaster.plugin.getLogger().info(toSet);
            }
        });
    }

    public boolean deleteAuction(String id){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt = Auctions.prepareStatement("DELETE FROM Auctions WHERE id = ?")
            ) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    try {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> deleteAuction(id), 7);
                    } catch (Exception e) {
                        Bukkit.getScheduler().runTaskLater(AuctionMaster.plugin, () -> deleteAuction(id), 7);
                        e.printStackTrace();
                    }
                } else {
                    x.printStackTrace();
                }
            }
        });

        return true;
    }

    public void addToOwnBids(String player, String toAdd){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt2 = Auctions.prepareStatement("INSERT INTO AuctionLists VALUES(?, '', ?)");
                    PreparedStatement stmt1 = Auctions.prepareStatement("UPDATE AuctionLists SET ownBids = ownBids || (CASE WHEN ownBids = '' THEN '" + toAdd + "' ELSE '." + toAdd + "' END) WHERE id = ?")
            ) {
                stmt1.setString(1, player);
                int updated = stmt1.executeUpdate();
                if (updated == 0) {
                    stmt2.setString(1, player);
                    stmt2.setString(2, toAdd);
                    stmt2.executeUpdate();
                }
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]"))
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> addToOwnBids(player, toAdd), 7);
                else
                    x.printStackTrace();
            }
        });
    }

    public boolean removeFromOwnBids(String player, String toRemove){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt = Auctions.prepareStatement("UPDATE AuctionLists SET ownBids = REPLACE(REPLACE(REPLACE(ownBids, '" + toRemove + ".', ''), '." + toRemove + "', ''), '" + toRemove + "', '') WHERE id = ?")
            ) {
                stmt.setString(1, player);
                stmt.executeUpdate();
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]"))
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> removeFromOwnBids(player, toRemove), 7);
                else
                    x.printStackTrace();
            }
        });

        return true;
    }

    public void resetOwnBids(String player){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt = Auctions.prepareStatement("UPDATE AuctionLists SET ownBids = '' WHERE id = ?")
            ) {
                stmt.setString(1, player);
                stmt.executeUpdate();
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]"))
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> resetOwnBids(player), 7);
                else
                    x.printStackTrace();
            }
        });
    }

    public boolean removeFromOwnAuctions(String player, String toRemove){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt = Auctions.prepareStatement("UPDATE AuctionLists SET ownAuctions = REPLACE(REPLACE(REPLACE(ownAuctions, '" + toRemove + ".', ''), '." + toRemove + "', ''), '" + toRemove + "', '') WHERE id = ?")
            ) {
                stmt.setString(1, player);
                stmt.executeUpdate();
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]"))
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> removeFromOwnAuctions(player, toRemove), 7);
                else
                    x.printStackTrace();
            }
        });

        return true;
    }

    public void resetOwnAuctions(String player){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt = Auctions.prepareStatement("UPDATE AuctionLists SET ownAuctions = '' WHERE id = ?")
            ) {
                stmt.setString(1, player);
                stmt.executeUpdate();
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]"))
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> resetOwnAuctions(player), 7);
                else
                    x.printStackTrace();
            }
        });
    }

    public void addToOwnAuctions(String player, String toAdd){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Auctions = DriverManager.getConnection(url);
                    PreparedStatement stmt2 = Auctions.prepareStatement("INSERT INTO AuctionLists VALUES(?, ?, '')");
                    PreparedStatement stmt1 = Auctions.prepareStatement("UPDATE AuctionLists SET ownAuctions = ownAuctions || (CASE WHEN ownAuctions = '' THEN '" + toAdd + "' ELSE '." + toAdd + "' END) WHERE id = ?")
            ) {
                stmt1.setString(1, player);

                int updated = stmt1.executeUpdate();
                if (updated == 0) {
                    stmt2.setString(1, player);
                    stmt2.setString(2, toAdd);
                    stmt2.executeUpdate();
                }
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]"))
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> addToOwnAuctions(player, toAdd), 7);
                else
                    x.printStackTrace();
            }
        });
    }

    public void adjustAuctionTimers(long toAdd) {
        try (
                Connection Auctions = getConnection();
                PreparedStatement stmt = Auctions.prepareStatement("UPDATE Auctions SET ending=ending+?")
        ) {
            stmt.setLong(1, toAdd);
            stmt.executeUpdate();
        } catch (Exception x) {
            if (x.getMessage().startsWith("[SQLITE_BUSY]"))
                Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> adjustAuctionTimers(toAdd), 7);
            else
                x.printStackTrace();
        }
    }

    public void addAllToBrowse(){
        for (Auction auction : AuctionMaster.auctionsHandler.auctions.values())
            if (!auction.isEnded())
                AuctionMaster.auctionsHandler.addToBrowse(auction);
    }

    public void loadAuctionsDataFromFile() {
        long toAdd = AuctionMaster.serverCloseDate;
        if (toAdd != 0L)
            adjustAuctionTimers(ZonedDateTime.now().toInstant().toEpochMilli() - toAdd);

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Auctions")
        ) {
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                String id = set.getString(1);
                if (AuctionMaster.auctionsHandler.auctions.containsKey(id))
                    continue;

                Auction auction = set.getString(9).startsWith("BIN") ?
                        new AuctionBIN(set.getString(1), set.getDouble(2), set.getLong(3), set.getString(4), set.getString(5), set.getString(6), set.getString(7), set.getString(8), set.getString(9))
                        : new AuctionClassic(set.getString(1), set.getDouble(2), set.getLong(3), set.getString(4), set.getString(5), set.getString(6), set.getString(7), set.getString(8), set.getString(9), set.getBoolean(10));

                AuctionMaster.auctionsHandler.auctions.put(id, auction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM AuctionLists WHERE ownAuctions='' and ownBids=''")
        ) {
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM AuctionLists")
        ) {
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                ArrayList<Auction> list = new ArrayList<>();
                String id = set.getString(1);
                String ownAuctions = set.getString(2);
                String ownBids = set.getString(3);

                for (String auctionID : ownAuctions.split("\\.")) {
                    if (auctionID.equals(""))
                        continue;

                    Auction auction = AuctionMaster.auctionsHandler.auctions.get(auctionID);
                    if (auction == null)
                        continue;

                    list.add(auction);
                }

                if (!list.isEmpty())
                    AuctionMaster.auctionsHandler.ownAuctions.put(id, list);
                list = new ArrayList<>();

                for (String bidID : ownBids.split("\\.")) {
                    if (bidID.equals(""))
                        continue;

                    Auction auction = AuctionMaster.auctionsHandler.auctions.get(bidID);
                    if (auction == null)
                        continue;

                    list.add(auction);
                }

                if (!list.isEmpty())
                    AuctionMaster.auctionsHandler.bidAuctions.put(id, list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addAllToBrowse();
        loadPreviewItems();
    }
}