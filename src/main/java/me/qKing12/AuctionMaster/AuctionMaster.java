package me.qKing12.AuctionMaster;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.qKing12.AuctionMaster.API.API;
import me.qKing12.AuctionMaster.AuctionObjects.AuctionsHandler;
import me.qKing12.AuctionMaster.Currency.*;
import me.qKing12.AuctionMaster.FilesHandle.ConfigLoad;
import me.qKing12.AuctionMaster.FilesHandle.ConfigUpdater;
import me.qKing12.AuctionMaster.FilesHandle.Deliveries;
import me.qKing12.AuctionMaster.InputGUIs.BidSelectGUI.BidSelectGUI;
import me.qKing12.AuctionMaster.InputGUIs.DeliveryCoinsGUI.DeliveryCoinsGUI;
import me.qKing12.AuctionMaster.InputGUIs.DeliveryGUI.DeliveryGUI;
import me.qKing12.AuctionMaster.InputGUIs.DurationSelectGUI.SelectDurationGUI;
import me.qKing12.AuctionMaster.InputGUIs.EditDurationGUI.EditDurationGUI;
import me.qKing12.AuctionMaster.InputGUIs.SearchGUI.SearchGUI;
import me.qKing12.AuctionMaster.InputGUIs.StartingBidGUI.StartingBidGUI;
import me.qKing12.AuctionMaster.ItemConstructor.ItemConstructor;
import me.qKing12.AuctionMaster.ItemConstructor.ItemConstructorLegacy;
import me.qKing12.AuctionMaster.ItemConstructor.ItemConstructorNew;
import me.qKing12.AuctionMaster.PlaceholderAPISupport.PlaceholderAPISupport;
import me.qKing12.AuctionMaster.PlaceholderAPISupport.PlaceholderAPISupportNo;
import me.qKing12.AuctionMaster.PlaceholderAPISupport.PlaceholderAPISupportYes;
import me.qKing12.AuctionMaster.PlaceholderAPISupport.PlaceholderRegister;
import me.qKing12.AuctionMaster.Utils.AuctionNPCHandle;
import me.qKing12.AuctionMaster.Utils.NumberFormatHelper;
import me.qKing12.AuctionMaster.bStats.MetricsLite;
import me.qKing12.AuctionMaster.database.DatabaseHandler;
import me.qKing12.AuctionMaster.database.MySQLDatabase;
import me.qKing12.AuctionMaster.database.SQLiteDatabase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class AuctionMaster extends JavaPlugin{

    public static AuctionMaster plugin;
    public static me.qKing12.AuctionMaster.API.API API;

    public static FileConfiguration adminCfg;
    public static FileConfiguration armorCfg;
    public static FileConfiguration auctionsManagerCfg;
    public static FileConfiguration bidsRelatedCfg;
    public static FileConfiguration blocksCfg;
    public static FileConfiguration consumablesCfg;
    public static FileConfiguration currencyCfg;
    public static FileConfiguration othersCfg;
    public static FileConfiguration soundsCfg;
    public static FileConfiguration toolsCfg;
    public static FileConfiguration weaponsCfg;
    public static FileConfiguration menusCfg;
    public static FileConfiguration buyItNowCfg;

    public static boolean upperVersion;
    public static boolean pluginDisable;

    public static ConfigLoad configLoad;
    public static ItemConstructor itemConstructor;
    public static PlaceholderAPISupport utilsAPI;
    public static NumberFormatHelper numberFormatHelper;
    public static Currency economy;
    public static AuctionNPCHandle auctionNPC;

    public static AuctionsHandler auctionsHandler;
    public static Deliveries deliveries;
    public static DatabaseHandler auctionsDatabase;
    public static String inputType = "chat";
    public static Long serverCloseDate = 0L;

    private void inputSetup() {
        if (AuctionMaster.plugin.getConfig().getBoolean("use-anvil-instead-sign"))
            inputType = "anvil";
        else if (!AuctionMaster.plugin.getConfig().getBoolean("use-chat-instead-sign"))
            inputType = "sign";
    }

    private void currencySetup(){
        String currency = currencyCfg.getString("currency-type");
        if(currency.equalsIgnoreCase("Vault")){
            economy=new VaultImpl();
        } else if (currency.equalsIgnoreCase("CustomEconomy-Balance")) {
            economy = new CustomEconomyBalance();
        } else if (currency.equalsIgnoreCase("CustomEconomy-Tokens")) {
            economy = new CustomEconomyTokens();
        } else if (currency.equalsIgnoreCase("PlayerPoints")) {
            economy = new PlayerPointsImpl();
        } else if (currency.equalsIgnoreCase("TokenManager")) {
            economy = new TokenManagerImpl();
        } else if (currency.equalsIgnoreCase("Skript")) {
            economy = new SkriptImpl();
        }
    }

    private void loadPlaceholderAPISupport(){
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            utilsAPI = new PlaceholderAPISupportYes();
            new PlaceholderRegister().register();
        }
        else
            utilsAPI=new PlaceholderAPISupportNo();
    }

    private void setupItemConstructor(){

        if(!upperVersion) {
            itemConstructor = new ItemConstructorLegacy();
        }
        else {
            itemConstructor = new ItemConstructorNew();
        }
    }

    private void setupAuctionNPC(){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getConfig().getBoolean("auction-npc-use"))
                    if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
                        auctionNPC = new AuctionNPCHandle();
                        auctionNPC.debugHolos();
                    }
            }
        }.runTaskLater(this, 100L);
    }

    public class DeliveryAlert implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                ArrayList<ItemStack> items = deliveries.getDeliveryItems(e.getPlayer().getUniqueId().toString());
                double coins = deliveries.getCoins(e.getPlayer().getUniqueId().toString());
                if (coins != 0 || !items.isEmpty()) {
                    e.getPlayer().sendMessage(utilsAPI.chat(e.getPlayer(), getConfig().getString("delivery-alert-join-message")));
                }
            });
        }
    }

    public static boolean hasProtocolLib=false;

    @Override
    public void onEnable() {
        plugin = this;
        pluginDisable = false;
        saveDefaultConfig();
        ConfigUpdater.generateFiles();

        new MetricsLite(this, 8726);

        if (this.getConfig().getDouble("version") < 3.23) {
            new ConfigUpdater(this);
            saveDefaultConfig();
        }

        File database = new File(AuctionMaster.plugin.getDataFolder(), "database");
        if (!database.exists())
            database.mkdir();

        File file = new File(this.getDataFolder(), "database/data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            serverCloseDate = configuration.getLong("serverCloseDate", 0L);
        }

        numberFormatHelper=new NumberFormatHelper();

        String version = Bukkit.getVersion();
        upperVersion= !version.contains("1.8") && !version.contains("1.9") && !version.contains("1.10") && !version.contains("1.11") && !version.contains("1.12");

        setupItemConstructor();

        adminCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "admin_config.yml"));
        buyItNowCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "buyItNow.yml"));
        armorCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "menus/armor.yml"));
        auctionsManagerCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "auctions_manager.yml"));
        bidsRelatedCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "bids_related.yml"));
        menusCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "menus.yml"));
        blocksCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "menus/blocks.yml"));
        consumablesCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "menus/consumables.yml"));
        currencyCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "currency.yml"));
        othersCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "menus/others.yml"));
        soundsCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "sounds.yml"));
        toolsCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "menus/tools.yml"));
        weaponsCfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "menus/weapons.yml"));

        if(Bukkit.getPluginManager().getPlugin("HeadDatabase") != null)
            new HeadDatabaseAPI();

        configLoad=new ConfigLoad();
        loadPlaceholderAPISupport();
        auctionsHandler=new AuctionsHandler();
        if(getConfig().getBoolean("use-delivery-system")) {
            deliveries = new Deliveries();
            new DeliveryGUI();
            new DeliveryCoinsGUI();
            Bukkit.getPluginManager().registerEvents(new DeliveryAlert(), this);
        }

        if ("mysql".equals(getConfig().getString("database.type"))) {
            auctionsDatabase = new MySQLDatabase();
        } else {
            auctionsDatabase = new SQLiteDatabase();
        }

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            hasProtocolLib=true;

        currencySetup();

        setupAuctionNPC();

        inputSetup();
        new SelectDurationGUI();
        new StartingBidGUI();
        new SearchGUI();
        new BidSelectGUI();
        new EditDurationGUI();

        new Commands();
        API=new API();
    }

    @Override
    public void onDisable(){
        for (HumanEntity player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }

        pluginDisable = true;
        File file = new File(this.getDataFolder(), "database/data.yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("serverCloseDate", ZonedDateTime.now().toInstant().toEpochMilli());
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
