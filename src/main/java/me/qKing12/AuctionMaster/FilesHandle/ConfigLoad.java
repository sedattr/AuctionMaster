package me.qKing12.AuctionMaster.FilesHandle;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.qKing12.AuctionMaster.AuctionMaster.adminCfg;
import static me.qKing12.AuctionMaster.AuctionMaster.buyItNowCfg;

public class ConfigLoad {

    private final ArrayList<ItemStack> blacklistIds;
    private final ArrayList<String> blacklistNames;
    private final ArrayList<ArrayList<String>> blacklistLore;
    public boolean isBlacklisted(Player player, ItemStack item){
        String itemName= Utils.getDisplayName(item);
        for (String name : blacklistNames) {
            String newName = name
                    .replace("%name%", player.getName())
                    .replace("%displayname%", player.getDisplayName());

            if (newName.equalsIgnoreCase(itemName))
                return true;
        }

        ArrayList<String> toCheck = (ArrayList<String>)item.getItemMeta().getLore();
        if(toCheck!=null)
            for(ArrayList<String> lore : blacklistLore){
                if(lore.containsAll(toCheck))
                    return true;
            }

        if(AuctionMaster.upperVersion){
            for(ItemStack item2 : blacklistIds)
                if(item.getType().equals(item2.getType()))
                    return true;
        }
        else{
            for(ItemStack item2 : blacklistIds) {
                if (item.getType().equals(item2.getType()) && item.getData().getData() == item2.getData().getData())
                    return true;
            }
        }

        return false;
    }

    public boolean useBackgoundGlass;
    public ItemStack backgroundGlass;

    public String mainMenuName;
    public int mainMenuSize;

    public String closeMenuName;
    public ArrayList<String> closeMenuLore;
    public ItemStack closeMenuMaterial;

    public String shulkerNotifierName;
    public ArrayList<String> shulkerNotifierLore;
    public String goBackName;
    public String goNextName;
    public ArrayList<String> goBackLore;
    public ArrayList<String> goNextLore;
    public ItemStack goBackMaterial;
    public ItemStack shulkerNotifierMaterial;
    public ItemStack goNextMaterial;

    public String nextPageName;
    public ArrayList<String> nextPageLore;
    public ItemStack nextPageMaterial;

    public String previousPageName;
    public ArrayList<String> previousPageLore;
    public ItemStack previousPageMaterial;

    public String browsingMenuItemName;
    public ArrayList<String> browsingMenuItemLore;
    public ItemStack browsingMenuItemMaterial;

    public String viewBidsMenuItemName;
    public ArrayList<String> viewBidsMenuItemLoreWithBids;
    public ArrayList<String> viewBidsMenuItemLoreWithoutBids;
    public ItemStack viewBidsMenuItemMaterial;

    public String manageAuctionsItemName;
    public ArrayList<String> manageAuctionsItemLoreWithAuctions;
    public ArrayList<String> manageAuctionsItemLoreWithoutAuctions;
    public ItemStack manageAuctionsItemMaterial;

    public String deliveryMenuName;

    public String deliveryItemYesName;
    public String deliveryItemNoName;
    public ArrayList<String> deliveryItemYesLore;
    public ArrayList<String> deliveryItemNoLore;

    public String mainMenuDeliveryName;
    public ItemStack mainMenuDeliveryItem;
    public ArrayList<String> mainMenuDeliveryLore;

    public String createAuctionMenuName;
    public int createAuctionMenuSize;

    public String createAuctionConfirmMenuName;
    public int createAuctionConfirmMenuSize;

    public String createAuctionPreviewNameNoItem;
    public String createAuctionPreviewNameYesItem;
    public ArrayList<String> createAuctionPreviewLoreNoItem;
    public String createAuctionPreviewItemSelectedLoreAdd;
    public ItemStack createAuctionPreviewMaterial;

    public String createAuctionConfirmYesName;
    public ArrayList<String> createAuctionConfirmYesLore;
    public ItemStack createAuctionConfirmYesMaterial;

    public String createAuctionConfirmNoName;
    public ArrayList<String> createAuctionConfirmNoLore;
    public ItemStack createAuctionConfirmNoMaterial;

    public String startingBidItemName;
    public ArrayList<String> startingBidItemLore;
    public ItemStack startingBidItemMaterial;

    public String durationItemName;
    public ArrayList<String> durationItemLore;
    public ItemStack durationItemMaterial;

    public String confirmItemName;
    public ItemStack confirmItemMaterial;
    public ArrayList<String> confirmItemLore;

    public String cancelItemName;
    public ItemStack cancelItemMaterial;
    public ArrayList<String> cancelItemLore;

    public String switchToBinName;
    public ArrayList<String> switchToBinLore;
    public ItemStack switchToBinMaterial;

    public String switchToAuctionName;
    public ArrayList<String> switchToAuctionLore;
    public ItemStack switchToAuctionMaterial;

    public String editBINPriceName;
    public ArrayList<String> editBINPriceLore;
    public ItemStack editBINPriceMaterial;

    public int browsingGoBackSlot;
    public int browsingSearchSlot;
    public int browsingBinFilter;
    public int browsingSortFilter;
    public int browsingPreviousPage;
    public int browsingNextPage;

    public int maxAuctionPerPlayer;

    public Double durationFeeCalculator(int hours) {
        if (hours == 0)
            return minutesFee;
        try {
            return hours*0.50;
        } catch (Exception x) {
            x.printStackTrace();
            return 0d;
        }
    }

    public double minutesFee;
    public double startingBidFee;
    public double startingBidBINFee;

    public ItemStack firstTimeItem;
    public ItemStack secondTimeItem;
    public ItemStack thirdTimeItem;
    public ItemStack forthTimeItem;
    public ItemStack fifthTimeItem;
    public ItemStack customTimeItem;

    public String second;
    public String seconds;
    public String minute;
    public String minutes;
    public String hour;
    public String hours;
    public String day;
    public String days;
    public String short_second;
    public String short_minute;
    public String short_hour;
    public String short_day;

    public long defaultDuration;
    public double defaultStartingBid;

    public ItemStack collectAllMaterial;
    public String collectAllName;
    public ArrayList<String> collectAllLoreOwnAuctions;
    public ArrayList<String> collectAllLoreBids;

    public String searchItemName;
    public ArrayList<String> searchItemLore;
    public ArrayList<String> searchItemLoreIfSet;
    public ItemStack searchItemMaterial;

    public String manageOwnAuctionsMenuName;
    public String manageOwnBidsMenuName;

    public String browsingMenuName;

    public int durationMenuSize;

    public String viewAuctionMenuName;
    public String viewShulkerContentMenuName;
    public int viewAuctionMenuSize;

    public ItemStack bidHistoryDefaultItem;
    public ArrayList<String> bidHistoryItemLoreStructure;

    public ItemStack editBidMaterial;
    public String editBidName;
    public ArrayList<String> editBidLore;

    public ItemStack cantAffordSubmitBidMaterial;
    public String cantAffordSubmitBidName;
    public ArrayList<String> cantAffordSubmitBidLore;

    public ItemStack cantAffordSubmitBuyMaterial;
    public ArrayList<String> cantAffordSubmitBuyLore;

    public ItemStack submitBidMaterial;
    public String submitBidName;
    public ArrayList<String> submitBidLoreNoPreviousBids;
    public ArrayList<String> submitBidLoreWithPreviousBids;

    public ItemStack submitBuyMaterial;
    public String submitBuyName;
    public ArrayList<String> submitBuyLore;

    public ItemStack collectAuctionMaterial;
    public String collectAuctionName;
    public ArrayList<String> collectAuctionCoins;
    public ArrayList<String> collectAuctionItem;

    public ArrayList<String> outbidMessage;

    public String viewPlayerAuctionsMenuName;

    public ItemStack adminNpcCreate;
    public ItemStack adminDebugNames;
    public ItemStack adminManageEndedAuctions;
    public ItemStack adminDelivery;
    public ItemStack adminHelp;
    public ItemStack adminDeliverySendDelivery;
    public ItemStack adminDeliverySetDelivery;
    public ItemStack adminDeliveryRemoveDelivery;
    public ItemStack adminDeliveryCopyDelivery;
    public ItemStack adminDeliveryConfirmDelivery;
    public ItemStack adminDeliveryCancelDelivery;
    public ItemStack adminDeleteWithDelivery;
    public ItemStack adminDeleteWithoutDelivery;
    public ItemStack adminCopyAuction;
    public ItemStack adminForceEndAuction;
    public ItemStack adminEditDurationAuction;

    public Boolean debug;
    public void loadAdminItems(){
        adminNpcCreate = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = adminNpcCreate.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("npccreate-item-name")));
        ArrayList<String> lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("npccreate-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminNpcCreate.setItemMeta(meta);

        adminDebugNames = new ItemStack(Material.FEATHER, 1);
        meta = adminDebugNames.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("npc-debug-item-name")));
        lore=new ArrayList<>();
        for(String line: adminCfg.getStringList("npc-debug-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDebugNames.setItemMeta(meta);

        adminManageEndedAuctions = new ItemStack(Material.REDSTONE_BLOCK, 1);
        meta = adminManageEndedAuctions.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("manage-ended-auctions-item-name")));
        lore=new ArrayList<>();
        for(String line: adminCfg.getStringList("manage-ended-auctions-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminManageEndedAuctions.setItemMeta(meta);

        adminDelivery = new ItemStack(Material.ENDER_CHEST, 1);
        meta = adminDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("delivery-item-name")));
        lore=new ArrayList<>();
        for(String line: adminCfg.getStringList("delivery-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDelivery.setItemMeta(meta);

        adminHelp = new ItemStack(Material.PAPER, 1);
        meta = adminHelp.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("help-item-name")));
        lore=new ArrayList<>();
        for(String line: adminCfg.getStringList("help-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminHelp.setItemMeta(meta);

        adminDeliverySendDelivery = new ItemStack(Material.HOPPER, 1);
        meta = adminDeliverySendDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("delivery-send-item-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("delivery-send-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDeliverySendDelivery.setItemMeta(meta);

        adminDeliverySetDelivery = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        meta = adminDeliverySetDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("delivery-set-item-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("delivery-set-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDeliverySetDelivery.setItemMeta(meta);

        adminDeliveryRemoveDelivery = new ItemStack(Material.REDSTONE, 2);
        meta = adminDeliveryRemoveDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("delivery-remove-item-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("delivery-remove-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDeliveryRemoveDelivery.setItemMeta(meta);

        adminDeliveryCopyDelivery = new ItemStack(Material.DRAGON_EGG, 1);
        meta = adminDeliveryCopyDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("delivery-item-clone-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("delivery-item-clone-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDeliveryCopyDelivery.setItemMeta(meta);

        adminDeliveryConfirmDelivery = confirmItemMaterial.clone();
        meta = adminDeliveryConfirmDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("confirm-action-item-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("confirm-action-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDeliveryConfirmDelivery.setItemMeta(meta);

        adminDeliveryCancelDelivery = cancelItemMaterial.clone();
        meta = adminDeliveryCancelDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("cancel-action-item-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("cancel-action-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDeliveryCancelDelivery.setItemMeta(meta);

        adminDeleteWithDelivery = new ItemStack(Material.CHEST, 2);
        meta = adminDeleteWithDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("delete-auction-item.with-delivery-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("delete-auction-item.with-delivery-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDeleteWithDelivery.setItemMeta(meta);

        adminDeleteWithoutDelivery = new ItemStack(Material.ENDER_CHEST, 2);
        meta = adminDeleteWithoutDelivery.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("delete-auction-item.without-delivery-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("delete-auction-item.without-delivery-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminDeleteWithoutDelivery.setItemMeta(meta);

        adminCopyAuction = new ItemStack(Material.DRAGON_EGG, 1);
        meta = adminCopyAuction.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("get-item-copy-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("get-item-copy-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminCopyAuction.setItemMeta(meta);

        adminForceEndAuction = new ItemStack(Material.COMPASS, 2);
        meta = adminForceEndAuction.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("force-end-auction-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("force-end-auction-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminForceEndAuction.setItemMeta(meta);

        adminEditDurationAuction = new ItemStack(customTimeItem.getType(), 1);
        meta = adminEditDurationAuction.getItemMeta();
        meta.setDisplayName(Utils.chat(adminCfg.getString("edit-duration-item-name")));
        lore = new ArrayList<>();
        for(String line : adminCfg.getStringList("edit-duration-item-lore"))
            lore.add(Utils.chat(line));
        meta.setLore(lore);
        adminEditDurationAuction.setItemMeta(meta);
    }

    public boolean endOwnAuction;

    public boolean defaultBuyItNow;
    public boolean onlyBuyItNow;
    public boolean BinTimer;

    public ConfigLoad(){
    	BinTimer=buyItNowCfg.getBoolean("use-bin-timer");
        defaultBuyItNow=buyItNowCfg.getBoolean("use-buy-it-now-as-default");
        onlyBuyItNow=buyItNowCfg.getBoolean("use-only-buy-it-now");

        endOwnAuction= AuctionMaster.plugin.getConfig().getBoolean("use-end-own-auction");

        backgroundGlass= AuctionMaster.itemConstructor.getItem("160:"+ AuctionMaster.plugin.getConfig().getInt("background-color"),  " ", null);

        mainMenuName= AuctionMaster.plugin.getConfig().getString("starting-menu-name");
        mainMenuSize= AuctionMaster.menusCfg.getInt("main-menu.size");

        useBackgoundGlass= AuctionMaster.plugin.getConfig().getBoolean("use-background-glass");

        goBackName= AuctionMaster.plugin.getConfig().getString("go-back-item-name");
        goNextName= AuctionMaster.plugin.getConfig().getString("go-next-item-name");
        goBackLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("go-back-item-lore");
        goNextLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("go-next-item-lore");
        goBackMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("go-back-item"));
        goNextMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("go-next-item"));

        shulkerNotifierName= AuctionMaster.menusCfg.getString("view-auction-menu.if-shulker.name");
        shulkerNotifierLore=(ArrayList<String>) AuctionMaster.menusCfg.getStringList("view-auction-menu.if-shulker.lore");
        shulkerNotifierMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("view-auction-menu.if-shulker.showcase-item"));

        previousPageName= AuctionMaster.plugin.getConfig().getString("previous-page-item-name");
        previousPageLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("previous-page-item-lore");
        previousPageMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("previous-page-item"));

        nextPageName= AuctionMaster.plugin.getConfig().getString("next-page-item-name");
        nextPageLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("next-page-item-lore");
        nextPageMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("next-page-item"));

        closeMenuName= AuctionMaster.plugin.getConfig().getString("close-menu-item-name");
        closeMenuLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("close-menu-item-lore");
        closeMenuMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("close-menu-item"));

        browsingMenuItemName= AuctionMaster.plugin.getConfig().getString("browsing-menu-item-name");
        browsingMenuItemLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("browsing-menu-item-lore");
        browsingMenuItemMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("browsing-menu-item"));

        viewBidsMenuItemName= AuctionMaster.plugin.getConfig().getString("view-bids-menu-item-name");
        viewBidsMenuItemLoreWithBids=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("view-bids-menu-item-lore.with-bids");
        viewBidsMenuItemLoreWithoutBids=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("view-bids-menu-item-lore.without-bids");
        viewBidsMenuItemMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("view-bids-menu-item"));

        manageAuctionsItemName= AuctionMaster.auctionsManagerCfg.getString("manage-menu-item-name");
        manageAuctionsItemLoreWithAuctions=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("manage-menu-item-lore.manage-with-own-auctions");
        manageAuctionsItemLoreWithoutAuctions=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("manage-menu-item-lore.manage-no-own-auctions");
        manageAuctionsItemMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("manage-menu-item"));

        deliveryMenuName= AuctionMaster.plugin.getConfig().getString("delivery-menu-name");

        deliveryItemNoName= AuctionMaster.plugin.getConfig().getString("delivery-item.no-delivery-name");
        deliveryItemYesName= AuctionMaster.plugin.getConfig().getString("delivery-item.yes-delivery-name");
        deliveryItemNoLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("delivery-item.no-delivery-lore");
        deliveryItemYesLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("delivery-item.yes-delivery-lore");

        mainMenuDeliveryName= AuctionMaster.plugin.getConfig().getString("delivery-menu-item-name");
        mainMenuDeliveryLore=(ArrayList<String>) AuctionMaster.plugin.getConfig().getStringList("delivery-menu-item-lore");
        mainMenuDeliveryItem= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.plugin.getConfig().getString("delivery-menu-item"));

        createAuctionMenuName = AuctionMaster.auctionsManagerCfg.getString("create-menu-name");
        createAuctionMenuSize = AuctionMaster.menusCfg.getInt("create-auction-menu.size");

        createAuctionConfirmMenuName = AuctionMaster.auctionsManagerCfg.getString("auction-confirm-menu-name");
        createAuctionConfirmMenuSize = AuctionMaster.menusCfg.getInt("create-auction-confirm-menu.size");

        createAuctionPreviewNameNoItem = AuctionMaster.auctionsManagerCfg.getString("preview-no-item-selected-name");
        createAuctionPreviewLoreNoItem = (ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("preview-no-item-selected-lore");
        createAuctionPreviewItemSelectedLoreAdd = AuctionMaster.auctionsManagerCfg.getString("preview-selected-item-take-back");
        createAuctionPreviewNameYesItem = AuctionMaster.auctionsManagerCfg.getString("preview-selected-item-name");
        createAuctionPreviewMaterial = AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("preview-no-item-selected"));

        createAuctionConfirmNoName = AuctionMaster.auctionsManagerCfg.getString("create-auction-item.no-item-selected-name");
        createAuctionConfirmNoLore = (ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("create-auction-item.no-item-selected-lore");
        createAuctionConfirmNoMaterial = AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("create-auction-item.no-item-selected-material"));

        createAuctionConfirmYesName = AuctionMaster.auctionsManagerCfg.getString("create-auction-item.item-selected-name");
        createAuctionConfirmYesLore = (ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("create-auction-item.item-selected-lore");
        createAuctionConfirmYesMaterial = AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("create-auction-item.item-selected-material"));

        startingBidItemName= AuctionMaster.auctionsManagerCfg.getString("starting-bid-item-name");
        startingBidItemLore=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("starting-bid-item-lore");
        startingBidItemMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("starting-bid-item"));

        durationItemName= AuctionMaster.auctionsManagerCfg.getString("duration-item-name");
        durationItemLore=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("duration-item-lore");
        durationItemMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("duration-item"));

        confirmItemName= AuctionMaster.auctionsManagerCfg.getString("auction-confirm-item-name");
        confirmItemLore=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("auction-confirm-item-lore");
        confirmItemMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("auction-confirm-item-material"));

        cancelItemName= AuctionMaster.auctionsManagerCfg.getString("auction-cancel-item-name");
        cancelItemLore=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("auction-cancel-item-lore");
        cancelItemMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("auction-cancel-item-material"));

        second = AuctionMaster.auctionsManagerCfg.getString("second");
        seconds = AuctionMaster.auctionsManagerCfg.getString("seconds");
        minute = AuctionMaster.auctionsManagerCfg.getString("minute");
        minutes = AuctionMaster.auctionsManagerCfg.getString("minutes");
        hour = AuctionMaster.auctionsManagerCfg.getString("hour");
        hours = AuctionMaster.auctionsManagerCfg.getString("hours");
        day = AuctionMaster.auctionsManagerCfg.getString("day");
        days = AuctionMaster.auctionsManagerCfg.getString("days");
        short_second = AuctionMaster.auctionsManagerCfg.getString("short_second");
        short_minute = AuctionMaster.auctionsManagerCfg.getString("short_minute");
        short_hour = AuctionMaster.auctionsManagerCfg.getString("short_hour");
        short_day = AuctionMaster.auctionsManagerCfg.getString("short_day");

        defaultDuration= Utils.toMiliseconds(AuctionMaster.auctionsManagerCfg.getString("default-starting-duration"));
        defaultStartingBid=Double.parseDouble(AuctionMaster.auctionsManagerCfg.getString("default-starting-bid"));

        minutesFee=Double.parseDouble(AuctionMaster.auctionsManagerCfg.getString("extra-fee-minutes"));
        startingBidFee=Double.parseDouble(AuctionMaster.auctionsManagerCfg.getString("starting-bid-fee-procent"));
        startingBidBINFee=buyItNowCfg.getDouble("buy-it-now-fee");

        double time = AuctionMaster.menusCfg.getDouble("duration-select-menu.first-item.hours");
        ArrayList<String> lore = new ArrayList<>();
        if(time<1){
            double fractional = time - Math.floor(time);
            fractional*=100;
            int minutes = (int) fractional;
            double firstTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(minutesFee) : minutesFee;
            if(firstTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(firstTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            firstTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.first-item.material")), Utils.chat("&a")+minutes+" "+(minutes==1?minute:this.minutes), lore);
        }
        else{
            int hours=(int)time;
            double firstTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(durationFeeCalculator(hours)) : durationFeeCalculator(hours);
            if(firstTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(firstTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            firstTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.first-item.material")), Utils.chat("&a")+hours+" "+(hours==1?hour:this.hours), lore);
        }

        time = AuctionMaster.menusCfg.getDouble("duration-select-menu.second-item.hours");
        lore = new ArrayList<>();
        if(time<1){
            double fractional = time - Math.floor(time);
            fractional*=100;
            int minutes = (int) fractional;
            double secondTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(minutesFee) : minutesFee;
            if(secondTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(secondTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            secondTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.second-item.material")), Utils.chat("&a")+minutes+" "+(minutes==1?minute:this.minutes), lore);
        }
        else{
            int hours=(int)time;
            double secondTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(durationFeeCalculator(hours)) : durationFeeCalculator(hours);
            if(secondTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(secondTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            secondTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.second-item.material")), Utils.chat("&a")+hours+" "+(hours==1?hour:this.hours), lore);
        }

        time = AuctionMaster.menusCfg.getDouble("duration-select-menu.third-item.hours");
        lore = new ArrayList<>();
        if(time<1){
            double fractional = time - Math.floor(time);
            fractional*=100;
            int minutes = (int) fractional;
            double thirdTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(minutesFee) : minutesFee;
            if(thirdTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(thirdTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            thirdTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.third-item.material")), Utils.chat("&a")+minutes+" "+(minutes==1?minute:this.minutes), lore);
        }
        else{
            int hours=(int)time;
            double thirdTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(durationFeeCalculator(hours)) : durationFeeCalculator(hours);
            if(thirdTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(thirdTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            thirdTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.third-item.material")), Utils.chat("&a")+hours+" "+(hours==1?hour:this.hours), lore);
        }

        time = AuctionMaster.menusCfg.getDouble("duration-select-menu.forth-item.hours");
        lore = new ArrayList<>();
        if(time<1){
            double fractional = time - Math.floor(time);
            fractional*=100;
            int minutes = (int) fractional;
            double forthTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(minutesFee) : minutesFee;
            if(forthTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(forthTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            forthTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.forth-item.material")), Utils.chat("&a")+minutes+" "+(minutes==1?minute:this.minutes), lore);
        }
        else{
            int hours=(int)time;
            double forthTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(durationFeeCalculator(hours)) : durationFeeCalculator(hours);
            if(forthTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(forthTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            forthTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.forth-item.material")), Utils.chat("&a")+hours+" "+(hours==1?hour:this.hours), lore);
        }

        time = AuctionMaster.menusCfg.getDouble("duration-select-menu.fifth-item.hours");
        lore = new ArrayList<>();
        if(time<1){
            double fractional = time - Math.floor(time);
            fractional*=100;
            int minutes = (int) fractional;
            double fifthTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(minutesFee) : minutesFee;
            if(fifthTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(fifthTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            fifthTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.fifth-item.material")), Utils.chat("&a")+minutes+" "+(minutes==1?minute:this.minutes), lore);
        }
        else{
            int hours=(int)time;
            double fifthTimeFee = AuctionMaster.numberFormatHelper.useDecimals? Math.floor(durationFeeCalculator(hours)) : durationFeeCalculator(hours);
            if(fifthTimeFee!=0)
                lore.add(Utils.chat("&7"+ AuctionMaster.auctionsManagerCfg.getString("extra-fee-message"))+ AuctionMaster.numberFormatHelper.formatNumber(fifthTimeFee));
            lore.add("");
            lore.add(Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-selection-lore")));
            fifthTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.fifth-item.material")), Utils.chat("&a")+hours+" "+(hours==1?hour:this.hours), lore);
        }

        lore=new ArrayList<>();
        for(String line : AuctionMaster.auctionsManagerCfg.getStringList("duration-select-item-lore")){
            lore.add(Utils.chat(line));
        }
        customTimeItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.menusCfg.getString("duration-select-menu.custom-time-item-material")), Utils.chat(AuctionMaster.auctionsManagerCfg.getString("duration-select-item-name")), lore);

        collectAllMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("collect-all-item"));
        collectAllName= AuctionMaster.auctionsManagerCfg.getString("collect-all-item-name");
        collectAllLoreOwnAuctions=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("collect-all-item-lore.own-auctions");
        collectAllLoreBids=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("collect-all-item-lore.own-bids");

        manageOwnAuctionsMenuName= AuctionMaster.auctionsManagerCfg.getString("manage-own-auctions-menu-name");
        manageOwnBidsMenuName= AuctionMaster.plugin.getConfig().getString("view-bids-menu-name");
        browsingMenuName= AuctionMaster.plugin.getConfig().getString("browsing-menu-name");

        searchItemName= AuctionMaster.auctionsManagerCfg.getString("search-auction-item-name");
        searchItemLore=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("search-auction-item-lore");
        searchItemLoreIfSet=(ArrayList<String>) AuctionMaster.auctionsManagerCfg.getStringList("search-auction-item-lore-if-set");
        searchItemMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.auctionsManagerCfg.getString("search-auction-item"));

        viewAuctionMenuName= AuctionMaster.bidsRelatedCfg.getString("bids-menu-name");
        viewShulkerContentMenuName= AuctionMaster.bidsRelatedCfg.getString("view-shulker-menu-name");
        viewAuctionMenuSize= AuctionMaster.menusCfg.getInt("view-auction-menu.size");

        editBidMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.bidsRelatedCfg.getString("edit-bid-item"));
        editBidName= AuctionMaster.bidsRelatedCfg.getString("edit-bid-item-name");
        editBidLore=(ArrayList<String>) AuctionMaster.bidsRelatedCfg.getStringList("edit-bid-item-lore");

        cantAffordSubmitBidMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.bidsRelatedCfg.getString("submit-bid-cannot-afford-item"));
        cantAffordSubmitBidName= AuctionMaster.bidsRelatedCfg.getString("submit-bid-cannot-afford-item-name");
        cantAffordSubmitBidLore=(ArrayList<String>) AuctionMaster.bidsRelatedCfg.getStringList("submit-bid-cannot-afford-item-lore");

        submitBidMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.bidsRelatedCfg.getString("submit-bid-item"));
        submitBidName= AuctionMaster.bidsRelatedCfg.getString("submit-bid-item-name");
        submitBidLoreNoPreviousBids=(ArrayList<String>) AuctionMaster.bidsRelatedCfg.getStringList("submit-bid-item-lore.no-previous-bids");
        submitBidLoreWithPreviousBids=(ArrayList<String>) AuctionMaster.bidsRelatedCfg.getStringList("submit-bid-item-lore.with-previous-bids");

        collectAuctionMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.bidsRelatedCfg.getString("collect-bid-item"));
        collectAuctionName= AuctionMaster.bidsRelatedCfg.getString("collect-bid-item-name");
        collectAuctionCoins=(ArrayList<String>) AuctionMaster.bidsRelatedCfg.getStringList("collect-bid-item-lore.collect-coins");
        collectAuctionItem=(ArrayList<String>) AuctionMaster.bidsRelatedCfg.getStringList("collect-bid-item-lore.collect-item");

        lore=new ArrayList<>();
        for(String line : AuctionMaster.bidsRelatedCfg.getStringList("bid-history-item-lore.no-bids"))
            lore.add(Utils.chat(line));
        bidHistoryDefaultItem= AuctionMaster.itemConstructor.getItem(AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.bidsRelatedCfg.getString("bid-history-item")), Utils.chat(AuctionMaster.bidsRelatedCfg.getString("bid-history-item-name")), lore);
        bidHistoryItemLoreStructure=(ArrayList<String>) AuctionMaster.bidsRelatedCfg.getStringList("bid-history-item-lore.bid-structure");

        outbidMessage=(ArrayList<String>) AuctionMaster.bidsRelatedCfg.getStringList("outbid-message");

        viewPlayerAuctionsMenuName= AuctionMaster.plugin.getConfig().getString("player-auction-menu-name");

        blacklistIds=new ArrayList<>();
        for(String item : AuctionMaster.plugin.getConfig().getStringList("blacklist-item-id")){
            blacklistIds.add(AuctionMaster.itemConstructor.getItemFromMaterial(item));
        }

        blacklistNames=new ArrayList<>();
        for(String name : AuctionMaster.plugin.getConfig().getStringList("blacklist-item-name"))
            blacklistNames.add(Utils.chat(name));

        blacklistLore=new ArrayList<>();
        for(String key : AuctionMaster.plugin.getConfig().getConfigurationSection("blacklist-item-lore").getKeys(false)) {
            ArrayList<String> loreToBlacklist = new ArrayList<>();
            for (String line : AuctionMaster.plugin.getConfig().getStringList("blacklist-item-lore." + key))
                loreToBlacklist.add(line);
            blacklistLore.add(loreToBlacklist);
        }

        loadAdminItems();

        cantAffordSubmitBuyLore=(ArrayList<String>) AuctionMaster.buyItNowCfg.getStringList("view-auction-buy-now-button.cannot-afford.lore");
        cantAffordSubmitBuyMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.buyItNowCfg.getString("view-auction-buy-now-button.cannot-afford.material"));

        submitBuyName= AuctionMaster.buyItNowCfg.getString("view-auction-buy-now-button.name");
        submitBuyMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.buyItNowCfg.getString("view-auction-buy-now-button.click-to-buy.material"));
        submitBuyLore=(ArrayList<String>) AuctionMaster.buyItNowCfg.getStringList("view-auction-buy-now-button.click-to-buy.lore");

        switchToAuctionName= AuctionMaster.buyItNowCfg.getString("switch-button.switch-to-auction.name");
        switchToAuctionLore=(ArrayList<String>) AuctionMaster.buyItNowCfg.getStringList("switch-button.switch-to-auction.lore");
        switchToAuctionMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.buyItNowCfg.getString("switch-button.switch-to-auction.material"));

        switchToBinName= AuctionMaster.buyItNowCfg.getString("switch-button.switch-to-bin.name");
        switchToBinLore=(ArrayList<String>) AuctionMaster.buyItNowCfg.getStringList("switch-button.switch-to-bin.lore");
        switchToBinMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.buyItNowCfg.getString("switch-button.switch-to-bin.material"));

        editBINPriceName= AuctionMaster.buyItNowCfg.getString("buy-it-now-select-price.name");
        editBINPriceLore=(ArrayList<String>) AuctionMaster.buyItNowCfg.getStringList("buy-it-now-select-price.lore");
        editBINPriceMaterial= AuctionMaster.itemConstructor.getItemFromMaterial(AuctionMaster.buyItNowCfg.getString("buy-it-now-select-price.item-id"));

        durationMenuSize=AuctionMaster.menusCfg.getInt("duration-select-menu.size");

        browsingGoBackSlot=AuctionMaster.menusCfg.getInt("browsing-menu.go-back-slot");
        browsingBinFilter=AuctionMaster.menusCfg.getInt("browsing-menu.bin-filter-slot");
        browsingSearchSlot=AuctionMaster.menusCfg.getInt("browsing-menu.search-slot");
        browsingSortFilter=AuctionMaster.menusCfg.getInt("browsing-menu.sort-filter-slot");
        browsingPreviousPage=AuctionMaster.menusCfg.getInt("browsing-menu.previous-page-slot");
        browsingNextPage=AuctionMaster.menusCfg.getInt("browsing-menu.next-page-slot");

        maxAuctionPerPlayer = AuctionMaster.auctionsManagerCfg.getInt("max-auction-per-player");
    }
}
