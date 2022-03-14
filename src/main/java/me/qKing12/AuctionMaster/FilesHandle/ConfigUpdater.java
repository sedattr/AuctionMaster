package me.qKing12.AuctionMaster.FilesHandle;

import me.qKing12.AuctionMaster.AuctionMaster;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class ConfigUpdater {

    public static void generateFiles(){
        if (!AuctionMaster.plugin.getDataFolder().exists()) {
            AuctionMaster.plugin.getDataFolder().mkdir();
        }

        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("bids_related.yml");
        fileNames.add("auctions_manager.yml");
        fileNames.add("currency.yml");
        fileNames.add("admin_config.yml");
        fileNames.add("sounds.yml");
        fileNames.add("menus.yml");
        fileNames.add("buyItNow.yml");

        for(String file : fileNames) {
            File toCreate = new File(AuctionMaster.plugin.getDataFolder(), file);
            if (!toCreate.exists()) {
                try {
                    toCreate.createNewFile();
                    InputStream input = AuctionMaster.plugin.getClass().getResourceAsStream("/"+file);
                    OutputStream output = new FileOutputStream(toCreate);
                    int realLength;
                    byte[] buffer = new byte[1024];

                    while (input != null && (realLength = input.read(buffer)) > 0) {
                        output.write(buffer, 0, realLength);
                    }
                    output.flush();
                    output.close();
                    AuctionMaster.plugin.getLogger().info("Loading "+file+" config...");
                } catch (IOException e) {
                    AuctionMaster.plugin.getLogger().info("Could not create the "+file+" config.");
                }
            }
        }

        ArrayList<String> menuFileNames = new ArrayList<>();
        menuFileNames.add("armor.yml");
        menuFileNames.add("blocks.yml");
        menuFileNames.add("consumables.yml");
        menuFileNames.add("others.yml");
        menuFileNames.add("tools.yml");
        menuFileNames.add("weapons.yml");

        File directory = new File(AuctionMaster.plugin.getDataFolder(), "menus");
        if(!directory.exists())
            directory.mkdir();

        for(String file : menuFileNames) {
            File toCreate = new File(AuctionMaster.plugin.getDataFolder(), "menus/"+file);
            if (!toCreate.exists()) {
                try {
                    toCreate.createNewFile();
                    InputStream input = AuctionMaster.plugin.getClass().getResourceAsStream("/"+file);
                    OutputStream output = new FileOutputStream(toCreate);
                    int realLength;
                    byte[] buffer = new byte[1024];

                    while (input != null && (realLength = input.read(buffer)) > 0) {
                        output.write(buffer, 0, realLength);
                    }
                    output.flush();
                    output.close();
                    AuctionMaster.plugin.getLogger().info("Loading "+file+" config...");
                } catch (IOException e) {
                    AuctionMaster.plugin.getLogger().info("Could not create the "+file+" config.");
                }
            }
        }
    }

    public ConfigUpdater(AuctionMaster plugin){
        File inFile = new File(plugin.getDataFolder(), "config.yml");
        File outFile = new File(plugin.getDataFolder(), "$$$$$$$$.tmp");

        try {
            // input
            FileInputStream fis = new FileInputStream(inFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            // output
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintWriter out = new PrintWriter(fos);

            Set<String> keys = plugin.getConfig().getKeys(false);

            String thisLine = "";
            while ((thisLine = in.readLine()) != null) {
                if (thisLine.startsWith("version:"))
                    out.println("version: 3.23");
                else {
                    out.println(thisLine);
                    if(thisLine.startsWith("delivery-menu-name:")){
                      if(!keys.contains("use-delivery-system"))
                          out.println("use-delivery-system: true");
                    }
                    else if(thisLine.startsWith("delivery-menu-item-name:")){
                        if(!keys.contains("delivery-menu-item"))
                            out.println("delivery-menu-item: '54'");
                    }
                    else if(thisLine.startsWith("  line-1:")){
                        if(!keys.contains("auction-npc-title.line-1-height")){
                            out.println("  line-1-height: 2.05");
                        }
                    }
                    else if (thisLine.startsWith("background-color:")) {
                        if(!keys.contains("use-background-glass"))
                            out.println("use-background-glass: true");
                        if(!keys.contains("use-decimals")){
                            out.println("");
                            out.println("use-decimals: false");
                        }
                        if (!keys.contains("add-time-to-auction")) {
                            out.println("");
                            out.println("#How much time you want to add to an auction");
                            out.println("#when a player bids:");
                            out.println("#Time in seconds");
                            out.println("add-time-to-auction: 15");
                        }
                        if (!keys.contains("use-end-own-auction")) {
                            out.println("");
                            out.println("#This option allows players to end their");
                            out.println("#auctions at any time they want");
                            out.println("use-end-own-auction: false");
                            out.println("#If you want just some players to have this option");
                            out.println("use-end-own-auction-permission: 'none'");
                            out.println("end-own-auction-fee: 500");
                            out.println("end-own-auction-item: '347'");
                            out.println("end-own-auction-name: '&aEnd Auction Now'");
                            out.println("end-own-auction-lore:");
                            out.println("  - '&7End this auction'");
                            out.println("  - '&7right now.'");
                            out.println("  - ''");
                            out.println("  - '&7Ending Cost: &6500 coins'");
                            out.println("end-own-auction-message: '&aYour auction was ended!'");
                            out.println("end-own-auction-no-money-message: '&cYou don''t have enough coins to end this auction.'");
                        }
                    } else if (thisLine.startsWith("use-anvil-instead-sign:")) {
                        if (!keys.contains("use-chat-instead-sign")) {
                            out.println("");
                            out.println("#If you want to use the chat instead of the sign gui");
                            out.println("#set the option bellow to true");
                            out.println("#Sign lines will become the message sent in chat");
                            out.println("#The player has 10 seconds to give a price, afterwards");
                            out.println("#the listener closes");
                            out.println("listener-expire-message: '&cYou took too long.. Your chat listener expired.'");
                            out.println("use-chat-instead-sign: false");
                        }
                    } else if (thisLine.startsWith("broadcast-new-auction-message:")) {
                        if(!keys.contains("broadcast-new-auction-permission"))
                            out.println("broadcast-new-auction-permission: 'none'");
                        if (!keys.contains("broadcast-commands")) {
                            out.println("#If you want commands to execute make a list in the setting broadcast-commands");
                            out.println("#If you want to use only commands and cancel the auction-message set it to 'none'");
                            out.println("broadcast-commands: []");
                        }
                    }
                }
            }
            out.flush();
            out.close();
            in.close();

            inFile.delete();
            outFile.renameTo(inFile);

            //menus.yml update

            inFile = new File(plugin.getDataFolder(), "menus.yml");
            outFile = new File(plugin.getDataFolder(), "$$$$$$$$.tmp");

            // input
            fis = new FileInputStream(inFile);
            in = new BufferedReader(new InputStreamReader(fis));

            // output
            fos = new FileOutputStream(outFile);
            out = new PrintWriter(fos);

            keys = YamlConfiguration.loadConfiguration(inFile).getKeys(true);

            while ((thisLine = in.readLine()) != null) {
                out.println(thisLine);
                if (thisLine.startsWith("  global-slot:")) {
                    if (!keys.contains("browsing-menu.go-back-slot")) {
                        out.println("  go-back-slot: 49");
                        out.println("  search-slot: 48");
                        out.println("  bin-filter-slot: 51");
                        out.println("  sort-filter-slot: 52");
                        out.println("  previous-page-slot: 46");
                        out.println("  next-page-slot: 53");
                    }
                }
            }
            out.flush();
            out.close();
            in.close();

            inFile.delete();
            outFile.renameTo(inFile);

            //auctions_manager.yml update

            inFile = new File(plugin.getDataFolder(), "auctions_manager.yml");
            outFile = new File(plugin.getDataFolder(), "$$$$$$$$.tmp");

            // input
            fis = new FileInputStream(inFile);
            in = new BufferedReader(new InputStreamReader(fis));

            // output
            fos = new FileOutputStream(outFile);
            out = new PrintWriter(fos);

            keys = YamlConfiguration.loadConfiguration(inFile).getKeys(false);

            while ((thisLine = in.readLine()) != null) {
                out.println(thisLine);
                if (thisLine.startsWith("days:")) {
                    if (!keys.contains("short_second")) {
                        out.println("");
                        out.println("#Time units but short version:");
                        out.println("short_second: 's'");
                        out.println("short_minute: 'm'");
                        out.println("short_hour: 'h'");
                        out.println("short_day: 'd'");
                    }
                }
                else if(thisLine.startsWith("not-enough-inventory-space:")){
                    if(!keys.contains("collect-all-message"))
                        out.println("collect-all-message: '&aYou just collected all your auctions!'");
                }
            }
            out.flush();
            out.close();
            in.close();

            inFile.delete();
            outFile.renameTo(inFile);

            //bids_related.yml update

            inFile = new File(plugin.getDataFolder(), "bids_related.yml");
            outFile = new File(plugin.getDataFolder(), "$$$$$$$$.tmp");

            // input
            fis = new FileInputStream(inFile);
            in = new BufferedReader(new InputStreamReader(fis));

            // output
            fos = new FileOutputStream(outFile);
            out = new PrintWriter(fos);

            keys = YamlConfiguration.loadConfiguration(inFile).getKeys(false);

            while ((thisLine = in.readLine()) != null) {
                if (thisLine.startsWith("edit-bid-item:")) {
                    if (!keys.contains("bid-step")) {
                        out.println("#What amount of the previous bid is needed for a new bid");
                        out.println("#Default Example: to bid over a 100 coins bid you need '100 + 15% from 100' minimum");
                        out.println("#If you remove the % it will just take 15 coins instead of a percent");
                        out.println("bid-step: '15%'");
                        out.println("");
                    }
                    if(!keys.contains("bid-jump")){
                        out.println("#When a bid is lower than this amount");
                        out.println("#the next bid will jump here");
                        out.println("#Set it to 0 to disable it but make sure you know");
                        out.println("#how to handle it");
                        out.println("bid-jump: 20");
                        out.println("");
                    }
                }
                out.println(thisLine);
            }
            out.flush();
            out.close();
            in.close();

            inFile.delete();
            outFile.renameTo(inFile);

            //buyItNow.yml update

            inFile = new File(plugin.getDataFolder(), "buyItNow.yml");
            outFile = new File(plugin.getDataFolder(), "$$$$$$$$.tmp");

            // input
            fis = new FileInputStream(inFile);
            in = new BufferedReader(new InputStreamReader(fis));

            // output
            fos = new FileOutputStream(outFile);
            out = new PrintWriter(fos);

            out.println(in.readLine());

            keys = YamlConfiguration.loadConfiguration(inFile).getKeys(false);
            if(!keys.contains("use-buy-it-now-as-default")){
                out.println("#This will make the buy-it-now auction to be the default one");
                out.println("#in the create auction menu (use-buy-it-now needs to be enabled)");
                out.println("use-buy-it-now-as-default: false");
                out.println("#This will disable normal auctions");
                out.println("use-only-buy-it-now: false");
            }
            if(!keys.contains("use-bin-timer")){
                out.println("use-bin-timer: true");
            }

            while ((thisLine = in.readLine()) != null) {
                out.println(thisLine);
            }
            out.flush();
            out.close();
            in.close();

            inFile.delete();
            outFile.renameTo(inFile);

            ArrayList<String> files = new ArrayList<>();
            files.add("armor.yml");
            files.add("blocks.yml");
            files.add("consumables.yml");
            files.add("others.yml");
            files.add("tools.yml");
            files.add("weapons.yml");
            //menu files.yml update
            for(String file : files) {
                inFile = new File(plugin.getDataFolder(), "menus/"+file);
                outFile = new File(plugin.getDataFolder(), "menus/$$$$$$$$.tmp");

                // input
                fis = new FileInputStream(inFile);
                in = new BufferedReader(new InputStreamReader(fis));

                // output
                fos = new FileOutputStream(outFile);
                out = new PrintWriter(fos);

                keys = YamlConfiguration.loadConfiguration(inFile).getKeys(false);

                while ((thisLine = in.readLine()) != null) {
                    out.println(thisLine);
                }
                if (!keys.contains("custom-item-ids")) {
                    out.println("");
                    out.println("custom-item-ids:");
                    out.println("  - '2266'");
                }
                out.flush();
                out.close();
                in.close();

                inFile.delete();
                outFile.renameTo(inFile);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
