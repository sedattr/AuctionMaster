package me.qKing12.AuctionMaster.FilesHandle;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;

public class Deliveries {

    private final String url;

    private ArrayList<ItemStack> getItems(String stringArray){
        ArrayList<ItemStack> items = new ArrayList<>();
        for(String item : stringArray.split(","))
            try {
                ItemStack item2 = Utils.itemFromBase64(item);
                if(item2!=null)
                    items.add(item2);
            }catch(Exception x){
                x.printStackTrace();
            }
        return items;
    }

    private String getStringItems(ArrayList<ItemStack> items){
        if(items.isEmpty())
            return "";
        String stringItems="";
        for(ItemStack item : items){
            stringItems=stringItems.concat(","+ Utils.itemToBase64(item));
        }
        return stringItems.substring(1);
    }

    private void loadDeliveryFile() {

        try(
                Connection Deliveries = DriverManager.getConnection(url);
                PreparedStatement stmt = Deliveries.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Deliveries " +
                        "(id VARCHAR(36) not NULL, " +
                        " coins DOUBLE(25, 0), " +
                        " items MEDIUMTEXT, " +
                        " PRIMARY KEY ( id ))")
                ) {
            stmt.execute();
            AuctionMaster.plugin.getLogger().info("Succesfully connected to the Deliveries SQL.");
        }catch(Exception x){
            AuctionMaster.plugin.getLogger().info("Failed to connect to Deliveries SQL.");
            x.printStackTrace();
        }
    }

    public Deliveries(){
        try{
            Class.forName("org.sqlite.JDBC");
        }catch(Exception x){
            x.printStackTrace();
        }
        url = "jdbc:sqlite:"+ AuctionMaster.plugin.getDataFolder()+"/database/deliveries.db";
        try{
            Connection Deliveries = DriverManager.getConnection(url);
            loadDeliveryFile();
            Deliveries.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ItemStack> getDeliveryItems(String uuid){
        try(
                Connection Deliveries = DriverManager.getConnection(url);
                ResultSet resultSet = Deliveries.prepareStatement("SELECT items FROM Deliveries WHERE id = '"+uuid+"'").executeQuery()
        ){
            if(resultSet.next()){
                return getItems(resultSet.getString(1));
            }
        }catch(Exception x){
            x.printStackTrace();
        }
        return new ArrayList<>();
    }

    public double getCoins(String uuid){
        try(
                Connection Deliveries = DriverManager.getConnection(url);
                ResultSet resultSet = Deliveries.prepareStatement("SELECT coins FROM Deliveries WHERE id = '"+uuid+"'").executeQuery()
        ){
            if(resultSet.next()){
                return resultSet.getDouble(1);
            }
        }catch(Exception x){
            x.printStackTrace();
        }
        return 0;
    }

    public void removeDelivery(String uuid){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Deliveries = DriverManager.getConnection(url);
                    PreparedStatement stmt = Deliveries.prepareStatement("DELETE FROM Deliveries WHERE id = ?")
            ) {
                stmt.setString(1, uuid);
                stmt.executeUpdate();
                Utils.injectToLog("[Delivery Removed] All deliveries were removed for player with UUID=" + uuid);
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> removeDelivery(uuid), 7);
                } else
                    x.printStackTrace();
            }
        });
    }

    public void setCoinsAndItems(String uuid, ArrayList<ItemStack> items, double coins){
        String toSet = getStringItems(items);
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Deliveries = DriverManager.getConnection(url);
                    PreparedStatement stmt1 = Deliveries.prepareStatement("UPDATE Deliveries SET items = ?,coins=? WHERE id = ?");
                    PreparedStatement stmt2 = Deliveries.prepareStatement("INSERT INTO Deliveries VALUES (?, ?, ?)")
            ) {
                stmt1.setString(1, toSet);
                stmt1.setDouble(2, coins);
                stmt1.setString(3, uuid);
                int needInsert = stmt1.executeUpdate();
                if (needInsert == 0) {
                    stmt2.setString(1, uuid);
                    stmt2.setDouble(2, coins);
                    stmt2.setString(3, toSet);
                    stmt2.executeUpdate();
                }
                Utils.injectToLog("[Delivery Updated] Deliveries were set to " + items.size() + " items and " + coins + " coins for player with UUID=" + uuid);
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> setCoinsAndItems(uuid, items, coins), 7);
                } else
                    x.printStackTrace();
            }
        });
    }

    public void setItems(String uuid, ArrayList<ItemStack> items){
        String toSet = getStringItems(items);
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Deliveries = DriverManager.getConnection(url);
                    PreparedStatement stmt1 = Deliveries.prepareStatement("UPDATE Deliveries SET items = ? WHERE id = ?");
                    PreparedStatement stmt2 = Deliveries.prepareStatement("INSERT INTO Deliveries VALUES (?, 0, ?)")
            ) {
                stmt1.setString(1, toSet);
                stmt1.setString(2, uuid);
                int needInsert = stmt1.executeUpdate();
                if (needInsert == 0) {
                    stmt2.setString(1, uuid);
                    stmt2.setString(2, toSet);
                    stmt2.executeUpdate();
                }
                Utils.injectToLog("[Delivery Updated Items] Deliveries were set to " + items.size() + " items for player with UUID=" + uuid);
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> setItems(uuid, items), 7);
                } else
                    x.printStackTrace();
            }
        });
    }

    public void addItem(String uuid, ItemStack item){
        String toAdd = Utils.itemToBase64(item);
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Deliveries = DriverManager.getConnection(url);
                    PreparedStatement stmt1 = Deliveries.prepareStatement("UPDATE Deliveries SET items = items || (CASE WHEN items = '' THEN '" + toAdd + "' ELSE '," + toAdd + "' END) WHERE id = ?");
                    PreparedStatement stmt2 = Deliveries.prepareStatement("INSERT INTO Deliveries VALUES (?, 0, ?)")
            ) {
                stmt1.setString(1, uuid);
                int needInsert = stmt1.executeUpdate();
                if (needInsert == 0) {
                    stmt2.setString(1, uuid);
                    stmt2.setString(2, toAdd);
                    stmt2.executeUpdate();
                }
                Utils.injectToLog("[Delivery Updated Item Added] Sent one item to player with UUID=" + uuid);
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> addItem(uuid, item), 7);
                } else
                    x.printStackTrace();
            }
        });
    }

    public void setCoins(String uuid, double coins){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (
                    Connection Deliveries = DriverManager.getConnection(url);
                    PreparedStatement stmt1 = Deliveries.prepareStatement("UPDATE Deliveries SET coins = ? WHERE id = ?");
                    PreparedStatement stmt2 = Deliveries.prepareStatement("INSERT INTO Deliveries VALUES (?, ?, '')")
            ) {
                stmt1.setDouble(1, coins);
                stmt1.setString(2, uuid);
                int needInsert = stmt1.executeUpdate();
                if (needInsert == 0) {
                    stmt2.setString(1, uuid);
                    stmt2.setDouble(2, coins);
                    stmt2.executeUpdate();
                }
                Utils.injectToLog("[Delivery Updated Coins] Deliveries were set to " + coins + " coins for player with UUID=" + uuid);
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> setCoins(uuid, coins), 7);
                } else
                    x.printStackTrace();
            }
        });
    }

    public void addCoins(String uuid, double coins){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            try (Connection Deliveries = DriverManager.getConnection(url);
                 PreparedStatement stmt1 = Deliveries.prepareStatement("UPDATE Deliveries SET coins = coins+? WHERE id = ?");
                 PreparedStatement stmt2 = Deliveries.prepareStatement("INSERT INTO Deliveries VALUES (?, ?, '')")
            ) {
                stmt1.setDouble(1, coins);
                stmt1.setString(2, uuid);
                int needInsert = stmt1.executeUpdate();
                if (needInsert == 0) {
                    stmt2.setString(1, uuid);
                    stmt2.setDouble(2, coins);
                    stmt2.executeUpdate();
                }
                Utils.injectToLog("[Delivery Updated Coins Added] Sent " + coins + " coins to player with UUID=" + uuid);
            } catch (Exception x) {
                if (x.getMessage().startsWith("[SQLITE_BUSY]")) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionMaster.plugin, () -> addCoins(uuid, coins), 7);
                } else
                    x.printStackTrace();
            }
        });
    }

}
