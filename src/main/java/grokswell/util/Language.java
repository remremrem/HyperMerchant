package grokswell.util;

//import static java.lang.System.out;
import grokswell.hypermerchant.HyperMerchantPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;

public class Language {
	static private File languageFolder;
	static private HyperMerchantPlugin plugin;
	private static String languagePath;

	//itemicons
	static public String II_PRICE;
	static public String II_BUY;
	static public String II_SELL;
	static public String II_MANAGE_BUY;
	static public String II_MANAGE_SELL;
	static public String II_STOCK;
	static public String II_STATUS;
	static public String II_STATIC;
	static public String II_DYNAMIC;
	
	//global
	static public String G_CITIZENS_NOT_FOUND;
	static public String G_CITIZENS_NOT_LOADED;
	static public String G_ID;
	static public String G_SHOP;
	static public String G_NO;
	static public String G_YES;
	static public String G_PERCENT;
	static public String G_PLAYER_COMMAND_ONLY;
	static public String G_NO_CREATIVE;
	static public String G_MISSING_SHOP_NAME;
	static public String G_MISSING_SHOP_NAME_2;
	static public String G_INVALID_SHOP_NAME;
	static public String G_INVALID_PLAYER_SHOP_NAME;
	static public String G_VALID_SHOP_NAMES;
	static public String G_NEED_VALID_SHOP_NAME;
	static public String G_NO_SHOPS_EXIST;
	static public String G_MUST_BE_IN_SHOP;
	static public String G_MUST_BE_IN_OWN_SHOP;
	static public String G_MUST_BE_IN_PLAYER_SHOP;
	static public String G_NO_MANAGE;
	static public String G_PLAYER_OFFDUTY;
	static public String G_PLAYER_ONDUTY;
	static public String G_CLOSE_SHOP_REMINDER;
	static public String G_SHOP_OPEN_FAIL;
	
	//clerk command
	static public String CC_NO_NPC_SELECTED;
	static public String CC_NO_NPC_SELECTED_2;
	static public String CC_OFFDUTY;
	static public String CC_ONDUTY;
	static public String CC_FIRED;
	static public String CC_REMOVE_GREETING;
	static public String CC_SET_GREETING;
	static public String CC_REMOVE_DENIAL;
	static public String CC_SET_DENIAL;
	static public String CC_REMOVE_FAREWELL;
	static public String CC_SET_FAREWELL;
	static public String CC_REMOVE_CLOSED;
	static public String CC_SET_CLOSED;
	static public String CC_SPECIFY_CLERK_NAME;
	static public String CC_SPECIFY_CLERK_NAME_2;
	static public String CC_CLERK_LIMIT_REACHED;
	static public String CC_NO_SHOPS_OWNED;
	static public String CC_SHOP_NOT_OWNED;
	static public String CC_NO_CLERK_SELECTED;
	static public String CC_NAME_REQUIRED;
	static public String CC_DISALLOWED_NAME;
	static public String CC_DISALLOWED_TYPE;
	static public String CC_NOT_YOUR_NPC;
	static public String CC_SPECIFY_NPC_TYPE;
	static public String CC_SUBCOMMANDS;
	static public String CC_CLOSE_SHOP;
	static public String CC_COMMAND_INCOMPATIBLE;
	
	//merchant command
	static public String MC_INVALID_COMMISSION;
	static public String MC_COMMISSION;
	static public String MC_INVALID_RENTAL_PRICE;
	static public String MC_RENTAL_PRICE;
	static public String MC_FOR_HIRE;
	static public String MC_NOT_FOR_HIRE;
	static public String MC_FOR_RENT;
	static public String MC_NOT_FOR_RENT;
	static public String MC_MISSING_SHOP_NAME;
	static public String MC_SUBCOMMNADS;
	
	//trait command
	static public String TC_HIRED;
	static public String TC_SHOPNAME_PROMPT;
	static public String TC_SHIFTCLICK_REMIND;
	static public String TC_SHIFTCLICK_REMIND_2;
	static public String TC_NPC_NO_SHOP;
	static public String TC_NPC_NO_PLAYERSHOP;
	static public String TC_NOW_RENTING;
	static public String TC_IT_WILL_COST;
	static public String TC_YOU_WILL_PAY;
	static public String TC_SALES_MADE;
	static public String TC_NPC_NULL_SHOP;
	static public String TC_SHOP_NO_EXIST;
	
	//manage menu
	static public String MM_BUY_PRICE;
	static public String MM_SELL_PRICE;
	static public String MM_IS_NOW;
	static public String MM_ACTIVATED;
	static public String MM_YOUPAY_1;
	static public String MM_YOUPAY_2;
	static public String MM_YOUPAY_3;
	static public String MM_CHANGE_PRICE_FAIL;
	static public String MM_THEYPAY_1;
	static public String MM_SORTING;
	static public String MM_ITEM_NAME;
	static public String MM_MATERIAL_NAME;
	static public String MM_PURCHASE_PRICE;
	static public String MM_SELL_PRICE_2;
	static public String MM_STOCK_AMOUNT;
	static public String MM_SHOW_ZERO_STOCK;
	
	//shop transactions:
	static public String ST_NO_SELL_PERMISSION;
	static public String ST_NO_SELL_PERMISSION2;
	static public String ST_NO_BUY_PERMISSION;
	static public String ST_NO_BUY_PERMISSION2;
	static public String ST_NO_BUY_ENCHANT;
	static public String ST_CANT_SELL_ENCHANT;
	static public String ST_ITEM_SOLD;
	static public String ST_ITEM_BOUGHT;
	static public String ST_ITEM_ADDED;
	static public String ST_NO_WANT_ENCHANTS;
	static public String ST_INVENTORY_FULL;
	static public String ST_INVENTORY_TOO_FULL;
	static public String ST_NEED_BOOK;
	static public String ST_CANT_STOCK_ITEM;
	static public String ST_MAX_STOCK;
	


	public Language(HyperMerchantPlugin plgn) {
        plugin = plgn;
        languagePath= (plugin.getDataFolder()+File.separator+
        		"language"+File.separator+plugin.settings.getLANGUAGE()+".yml");
		languageFolder = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+
        		"language");
		if (!languageFolder.isDirectory()) languageFolder.mkdir();

        loadLanguage();
    }
    	  
    private static void loadLanguage() {
		File configFile = null;
		InputStream defLanguageStream = null;
		//YamlConfiguration defConfig = null;
		YamlConfiguration config = null;
		
		defLanguageStream = plugin.getResource(languagePath);
		configFile = new File(languagePath);
		
	 
		// Look for defaults in the jar
	    if (defLanguageStream != null) {
	       //defConfig = YamlConfiguration.loadConfiguration(defLanguageStream);
	       defLanguageStream = null;
	    }
    	try {
			if (configFile.createNewFile()) {
				plugin.saveResource("language"+File.separator+plugin.settings.getLANGUAGE()+".yml", true);
			}
		} catch (IOException ioe) {
			
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		//ITEMICONS
		II_PRICE = config.getString("ItemIcons.II_PRICE");
		II_BUY = config.getString("ItemIcons.II_BUY");
		II_SELL = config.getString("ItemIcons.II_SELL");
		II_MANAGE_BUY = config.getString("ItemIcons.II_MANAGE_BUY");
		II_MANAGE_SELL = config.getString("ItemIcons.II_MANAGE_SELL");
		II_STOCK = config.getString("ItemIcons.II_STOCK");
		II_STATUS = config.getString("ItemIcons.II_STATUS");
		II_STATIC = config.getString("ItemIcons.II_STATIC");
		II_DYNAMIC = config.getString("ItemIcons.II_DYNAMIC");
		
	    //GLOBALS
	    G_CITIZENS_NOT_FOUND = config.getString("Global.G_CITIZENS_NOT_FOUND");
	    G_CITIZENS_NOT_LOADED = config.getString("Global.G_CITIZENS_NOT_LOADED");
	    G_ID = config.getString("Global.G_ID");
	    G_SHOP = config.getString("Global.G_SHOP");
	    G_NO = config.getString("Global.G_NO");
	    G_YES = config.getString("Global.G_YES");
	    G_PERCENT = config.getString("Global.G_PERCENT");
	    G_PLAYER_COMMAND_ONLY = config.getString("Global.G_PLAYER_COMMAND_ONLY");
	    G_NO_CREATIVE = config.getString("Global.G_NO_CREATIVE");
	    G_MISSING_SHOP_NAME = config.getString("Global.G_MISSING_SHOP_NAME");
	    G_MISSING_SHOP_NAME_2 = config.getString("Global.G_MISSING_SHOP_NAME_2");
	    G_INVALID_SHOP_NAME = config.getString("Global.G_INVALID_SHOP_NAME");
	    G_INVALID_PLAYER_SHOP_NAME = config.getString("Global.G_INVALID_PLAYER_SHOP_NAME");
	    G_VALID_SHOP_NAMES = config.getString("Global.G_VALID_SHOP_NAMES");
	    G_NEED_VALID_SHOP_NAME = config.getString("Global.G_NEED_VALID_SHOP_NAME");
	    G_NO_SHOPS_EXIST = config.getString("Global.G_NO_SHOPS_EXIST");
	    G_MUST_BE_IN_SHOP = config.getString("Global.G_MUST_BE_IN_SHOP");
	    G_MUST_BE_IN_OWN_SHOP = config.getString("Global.G_MUST_BE_IN_OWN_SHOP");
	    G_MUST_BE_IN_PLAYER_SHOP = config.getString("Global.G_MUST_BE_IN_PLAYER_SHOP");
	    G_NO_MANAGE = config.getString("Global.G_NO_MANAGE");
	    G_PLAYER_OFFDUTY = config.getString("Global.G_PLAYER_OFFDUTY");
	    G_PLAYER_ONDUTY = config.getString("Global.G_PLAYER_ONDUTY");
	    G_CLOSE_SHOP_REMINDER = config.getString("Global.G_CLOSE_SHOP_REMINDER");
	    G_SHOP_OPEN_FAIL = config.getString("Global.G_SHOP_OPEN_FAIL");
	    
	    //CLERK COMMAND
	    CC_NO_NPC_SELECTED = config.getString("ClerkCommand.CC_NO_NPC_SELECTED");
	    CC_NO_NPC_SELECTED_2 = config.getString("ClerkCommand.CC_NO_NPC_SELECTED_2");
	    CC_OFFDUTY = config.getString("ClerkCommand.CC_OFFDUTY");
	    CC_ONDUTY = config.getString("ClerkCommand.CC_ONDUTY");
	    CC_FIRED = config.getString("ClerkCommand.CC_FIRED");
	    CC_REMOVE_GREETING = config.getString("ClerkCommand.CC_REMOVE_GREETING");
	    CC_SET_GREETING = config.getString("ClerkCommand.CC_SET_GREETING");
	    CC_REMOVE_DENIAL = config.getString("ClerkCommand.CC_REMOVE_DENIAL");
	    CC_SET_DENIAL = config.getString("ClerkCommand.CC_SET_DENIAL");
	    CC_REMOVE_FAREWELL = config.getString("ClerkCommand.CC_REMOVE_FAREWELL");
	    CC_SET_FAREWELL = config.getString("ClerkCommand.CC_SET_FAREWELL");
	    CC_REMOVE_CLOSED = config.getString("ClerkCommand.CC_REMOVE_CLOSED");
	    CC_SET_CLOSED = config.getString("ClerkCommand.CC_SET_CLOSED");
	    CC_SPECIFY_CLERK_NAME = config.getString("ClerkCommand.CC_SPECIFY_CLERK_NAME");
	    CC_SPECIFY_CLERK_NAME_2 = config.getString("ClerkCommand.CC_SPECIFY_CLERK_NAME_2");
	    CC_CLERK_LIMIT_REACHED = config.getString("ClerkCommand.CC_CLERK_LIMIT_REACHED");
	    CC_NO_SHOPS_OWNED = config.getString("ClerkCommand.CC_NO_SHOPS_OWNED");
	    CC_SHOP_NOT_OWNED = config.getString("ClerkCommand.CC_SHOP_NOT_OWNED");
	    CC_NO_CLERK_SELECTED = config.getString("ClerkCommand.CC_NO_CLERK_SELECTED");
	    CC_NAME_REQUIRED = config.getString("ClerkCommand.CC_NAME_REQUIRED");
	    CC_DISALLOWED_NAME = config.getString("ClerkCommand.CC_DISALLOWED_NAME");
	    CC_DISALLOWED_TYPE = config.getString("ClerkCommand.CC_DISALLOWED_TYPE");
	    CC_NOT_YOUR_NPC = config.getString("ClerkCommand.CC_NOT_YOUR_NPC");
	    CC_SPECIFY_NPC_TYPE = config.getString("ClerkCommand.CC_SPECIFY_NPC_TYPE");
	    CC_SUBCOMMANDS = config.getString("ClerkCommand.CC_SUBCOMMANDS");
	    CC_CLOSE_SHOP = config.getString("ClerkCommand.CC_CLOSE_SHOP");
	    CC_COMMAND_INCOMPATIBLE = config.getString("ClerkCommand.CC_COMMAND_INCOMPATIBLE");
	    
	    //MERCHANT COMMAND
	    MC_INVALID_COMMISSION = config.getString("MerchantCommand.MC_INVALID_COMMISSION");
	    MC_COMMISSION = config.getString("MerchantCommand.MC_COMMISSION");
	    MC_INVALID_RENTAL_PRICE = config.getString("MerchantCommand.MC_INVALID_RENTAL_PRICE");
	    MC_RENTAL_PRICE = config.getString("MerchantCommand.MC_RENTAL_PRICE");
	    MC_FOR_HIRE = config.getString("MerchantCommand.MC_FOR_HIRE");
	    MC_NOT_FOR_HIRE = config.getString("MerchantCommand.MC_NOT_FOR_HIRE");
	    MC_FOR_RENT = config.getString("MerchantCommand.MC_FOR_RENT");
	    MC_NOT_FOR_RENT = config.getString("MerchantCommand.MC_NOT_FOR_RENT");
	    MC_MISSING_SHOP_NAME = config.getString("MerchantCommand.MC_MISSING_SHOP_NAME");
	    MC_SUBCOMMNADS = config.getString("MerchantCommand.MC_SUBCOMMNADS");
	    
	    //TRAIT COMMAND
	    TC_HIRED = config.getString("TraitCommand.TC_HIRED");
	    TC_SHOPNAME_PROMPT = config.getString("TraitCommand.TC_SHOPNAME_PROMPT");
	    TC_SHIFTCLICK_REMIND = config.getString("TraitCommand.TC_SHIFTCLICK_REMIND");
	    TC_SHIFTCLICK_REMIND_2 = config.getString("TraitCommand.TC_SHIFTCLICK_REMIND_2");
	    TC_NPC_NO_SHOP = config.getString("TraitCommand.TC_NPC_NO_SHOP");
	    TC_NPC_NO_PLAYERSHOP = config.getString("TraitCommand.TC_NPC_NO_PLAYERSHOP");
	    TC_NOW_RENTING = config.getString("TraitCommand.TC_NOW_RENTING");
	    TC_IT_WILL_COST = config.getString("TraitCommand.TC_IT_WILL_COST");
	    TC_YOU_WILL_PAY = config.getString("TraitCommand.TC_YOU_WILL_PAY");
	    TC_SALES_MADE = config.getString("TraitCommand.TC_SALES_MADE");
	    TC_NPC_NULL_SHOP = config.getString("TraitCommand.TC_NPC_NULL_SHOP");
	    TC_SHOP_NO_EXIST = config.getString("TraitCommand.TC_SHOP_NO_EXIST");
	    
	    //MANAGE MENU
	    MM_BUY_PRICE = config.getString("ManageMenu.MM_BUY_PRICE");
	    MM_SELL_PRICE = config.getString("ManageMenu.MM_SELL_PRICE");
	    MM_IS_NOW = config.getString("ManageMenu.MM_IS_NOW");
	    MM_ACTIVATED = config.getString("ManageMenu.MM_ACTIVATED");
	    MM_YOUPAY_1 = config.getString("ManageMenu.MM_YOUPAY_1");
	    MM_YOUPAY_2 = config.getString("ManageMenu.MM_YOUPAY_2");
	    MM_YOUPAY_3 = config.getString("ManageMenu.MM_YOUPAY_3");
	    MM_CHANGE_PRICE_FAIL = config.getString("ManageMenu.MM_CHANGE_PRICE_FAIL");
	    MM_THEYPAY_1 = config.getString("ManageMenu.MM_THEYPAY_1");
	    MM_SORTING = config.getString("ManageMenu.MM_SORTING");
	    MM_ITEM_NAME = config.getString("ManageMenu.MM_ITEM_NAME");
	    MM_MATERIAL_NAME = config.getString("ManageMenu.MM_MATERIAL_NAME");
	    MM_PURCHASE_PRICE = config.getString("ManageMenu.MM_PURCHASE_PRICE");
	    MM_SELL_PRICE_2 = config.getString("ManageMenu.MM_SELL_PRICE_2");
	    MM_STOCK_AMOUNT = config.getString("ManageMenu.MM_STOCK_AMOUNT");
	    MM_SHOW_ZERO_STOCK = config.getString("ManageMenu.MM_SHOW_ZERO_STOCK");
	    
	    //SHOP TRANSACTIONS
		ST_NO_SELL_PERMISSION = config.getString("ShopTransactions.ST_NO_SELL_PERMISSION");
		ST_NO_SELL_PERMISSION2 = config.getString("ShopTransactions.ST_NO_SELL_PERMISSION2");
		ST_NO_BUY_PERMISSION = config.getString("ShopTransactions.ST_NO_BUY_PERMISSION");
		ST_NO_BUY_PERMISSION2 = config.getString("ShopTransactions.ST_NO_BUY_PERMISSION2");
		ST_NO_BUY_ENCHANT = config.getString("ShopTransactions.ST_NO_BUY_ENCHANT");
		ST_CANT_SELL_ENCHANT = config.getString("ShopTransactions.ST_CANT_SELL_ENCHANT");
		ST_ITEM_SOLD = config.getString("ShopTransactions.ST_ITEM_SOLD");
		ST_ITEM_BOUGHT = config.getString("ShopTransactions.ST_ITEM_BOUGHT");
		ST_ITEM_ADDED = config.getString("ShopTransactions.ST_ITEM_ADDED");
		ST_NO_WANT_ENCHANTS = config.getString("ShopTransactions.ST_NO_WANT_ENCHANTS");
		ST_INVENTORY_FULL = config.getString("ShopTransactions.ST_INVENTORY_FULL");
		ST_INVENTORY_TOO_FULL = config.getString("ShopTransactions.ST_INVENTORY_TOO_FULL");
		ST_NEED_BOOK = config.getString("ShopTransactions.ST_NEED_BOOK");
		ST_CANT_STOCK_ITEM = config.getString("ShopTransactions.ST_CANT_STOCK_ITEM");
		ST_MAX_STOCK = config.getString("ShopTransactions.ST_MAX_STOCK");

    }
}

