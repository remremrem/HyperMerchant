package grokswell.util;

//import static java.lang.System.out;
import grokswell.hypermerchant.HyperMerchantPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;



import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	private static File dataFolder;
	static private HyperMerchantPlugin plugin;
    
    //Defaults
	static Boolean ENABLE_COMMAND;
	static Boolean ENABLE_NPCS;
	static Boolean OFFDUTY;
	static Boolean UUID_SUPPORT;
	static String LANGUAGE;
	static String WELCOME;
	static String FAREWELL;
	static String DENIAL;
	static String CLOSED;
	static String FOR_HIRE_MSG;
	static String RENTAL_MSG;
	static String DEFAULT_RENTAL_OWNER;
	static Boolean NPC_FOR_HIRE;
	static Double NPC_COMMISSION;
	static Double NPC_RENTAL_PRICE;
	static Boolean RIGHT_CLICK_PLAYER_SHOP;
	static Boolean ONDUTY_IN_SHOP_ONLY;
	static Boolean NPC_IN_SHOP_ONLY;
	static int MAX_NPCS_PER_PLAYER;
	static Boolean MANAGE_IN_SHOP_ONLY;


	public Settings(HyperMerchantPlugin plgn) {
        plugin = plgn;
		dataFolder = plugin.getDataFolder();
		if (!dataFolder.isDirectory()) dataFolder.mkdir();
		
	    //Defaults
		ENABLE_COMMAND = true;
		ENABLE_NPCS = true;
		OFFDUTY = false;
		UUID_SUPPORT = true;
		LANGUAGE = "en_us";
		WELCOME = "Welcome to my little shop.";
		FAREWELL = "I thank you for your continued patronage.";
		DENIAL = "I'm afraid you are not a shop member. " +
	    		"I am not authorized to do business with you.";
		CLOSED = "I am sorry, I am closed for business at this time.";
		FOR_HIRE_MSG = "I am ready to work!";
		RENTAL_MSG = "Rent this shop space, and I work as your clerk.";
		DEFAULT_RENTAL_OWNER = "server";
		NPC_FOR_HIRE = true;
		NPC_COMMISSION = 10.0;
		NPC_RENTAL_PRICE = 100.0;
		RIGHT_CLICK_PLAYER_SHOP = true;
		ONDUTY_IN_SHOP_ONLY = true;
		NPC_IN_SHOP_ONLY = true;
		MANAGE_IN_SHOP_ONLY = false;
		MAX_NPCS_PER_PLAYER = 2;
		
        loadConfig();
        saveConfig();
    }
    	  
    private static void loadConfig() {
		File configFile = null;
		//InputStream defConfigStream = null;
		YamlConfiguration defConfig = null;
		YamlConfiguration config = null;
		InputStreamReader defConfigStream = null;
		//defConfigStream = new InputStreamReader(this.getResource("customConfig.yml"), "UTF8");
	
		defConfigStream = new InputStreamReader(plugin.getResource("config.yml"));
    	configFile = new File(dataFolder, "config.yml");
	    config = YamlConfiguration.loadConfiguration(configFile);
	 
		// Look for defaults in the jar
	    if (defConfigStream != null) {
	        defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        defConfigStream = null;
	    }
	    if (defConfig != null) {
	    	config.setDefaults(defConfig);
	    }

        if (config.contains("Messages.enable-command")){
        	ENABLE_COMMAND = config.getBoolean("Main.enable-command");
        }
        if (config.contains("Messages.enable-npcs")){
        	ENABLE_NPCS = config.getBoolean("Main.enable-npcs");
        }
        if (config.contains("Messages.offduty")){
        	OFFDUTY = config.getBoolean("Main.offduty");
        }
        if (config.contains("Messages.uuid-support")){
        	UUID_SUPPORT = config.getBoolean("Main.uuid-support");
        }
        if (config.contains("Messages.language")){
        	LANGUAGE = config.getString("Messages.language");
        }
        if (config.contains("Messages.welcome")){
        	WELCOME = config.getString("Messages.welcome");
        }
        if (config.contains("Messages.farewell")){
        	FAREWELL = config.getString("Messages.farewell");
        }
        if (config.contains("Messages.denial")){
        	DENIAL = config.getString("Messages.denial");
        }
        if (config.contains("Messages.closed")){
        	CLOSED = config.getString("Messages.closed");
        }
        if (config.contains("Messages.forHireMsg")){
        	FOR_HIRE_MSG = config.getString("Messages.forHireMsg");
        }
        if (config.contains("Messages.rentalMsg")){
        	RENTAL_MSG = config.getString("Messages.rentalMsg");
        }
        if (config.contains("PlayerShops.rentalMsg")){
        	DEFAULT_RENTAL_OWNER = config.getString("PlayerShops.default-rental-owner");
        }
        if (config.contains("PlayerShops.npc-for-hire")){
        	NPC_FOR_HIRE = config.getBoolean("PlayerShops.npc-for-hire");
        }
        if (config.contains("PlayerShops.npc-commission")){
        	NPC_COMMISSION = config.getDouble("PlayerShops.npc-commission");
        }
        if (config.contains("PlayerShops.npc-commission")){
        	NPC_RENTAL_PRICE = config.getDouble("PlayerShops.npc-rental-price");
        }
        if (config.contains("PlayerShops.right-click-player-shop")){
        	RIGHT_CLICK_PLAYER_SHOP = config.getBoolean("PlayerShops.right-click-player-shop");
        }
        if (config.contains("PlayerShops.onduty-in-shop-only")){
        	ONDUTY_IN_SHOP_ONLY = config.getBoolean("PlayerShops.onduty-in-shop-only");
        }
        if (config.contains("PlayerShops.npc-in-shop-only")){
        	NPC_IN_SHOP_ONLY = config.getBoolean("PlayerShops.npc-in-shop-only");
        }
        if (config.contains("PlayerShops.manage-in-shop-only")){
        	MANAGE_IN_SHOP_ONLY = config.getBoolean("PlayerShops.manage-in-shop-only");
        }
        if (config.contains("PlayerShops.max-npcs-per-player")){
        	MAX_NPCS_PER_PLAYER = config.getInt("PlayerShops.max-npcs-per-player");
        }
    }
	  
	public static void saveConfig() {
		File configFile = null;
		YamlConfiguration config = null;
		
		configFile = new File(dataFolder, "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);

	    config.set("Main.enable-command", ENABLE_COMMAND);
	    config.set("Main.enable-npcs", ENABLE_NPCS);
	    config.set("Main.offduty", OFFDUTY);
	    config.set("Main.uuid-support", UUID_SUPPORT);
	    config.set("Main.language", LANGUAGE);
	    config.set("Messages.welcome", WELCOME);
	    config.set("Messages.farewell", FAREWELL);
	    config.set("Messages.denial", DENIAL);
	    config.set("Messages.closed", CLOSED);
	    config.set("Messages.forHireMsg", FOR_HIRE_MSG);
	    config.set("Messages.rentalMsg", RENTAL_MSG);
	    config.set("PlayerShops.default-rental-owner", DEFAULT_RENTAL_OWNER);
	    config.set("PlayerShops.npc-for-hire", NPC_FOR_HIRE);
	    config.set("PlayerShops.npc-commission", NPC_COMMISSION);
	    config.set("PlayerShops.npc-rental-price", NPC_RENTAL_PRICE);
	    config.set("PlayerShops.right-click-player-shop", RIGHT_CLICK_PLAYER_SHOP);
	    config.set("PlayerShops.onduty-in-shop-only", ONDUTY_IN_SHOP_ONLY);
	    config.set("PlayerShops.npc-in-shop-only", NPC_IN_SHOP_ONLY);
	    config.set("PlayerShops.manage-in-shop-only", MANAGE_IN_SHOP_ONLY);
	    config.set("PlayerShops.max-npcs-per-player", MAX_NPCS_PER_PLAYER);
	    
		try {
			config.save(configFile);
		}
		catch(IOException ex) {
			plugin.getLogger().severe("Cannot save to config.yml");
		}
	}
    
    
    public Boolean getENABLE_COMMAND() {
		return ENABLE_COMMAND;
	}

	public void setENABLE_COMMAND(Boolean eNABLE_COMMAND) {
		ENABLE_COMMAND = eNABLE_COMMAND;
	}

	public Boolean getENABLE_NPCS() {
		return ENABLE_NPCS;
	}

	public void setENABLE_NPCS(Boolean eNABLE_NPCS) {
		ENABLE_NPCS = eNABLE_NPCS;
	}

	public Boolean getOFFDUTY() {
		return OFFDUTY;
	}

	public void setOFFDUTY(Boolean oFFDUTY) {
		OFFDUTY = oFFDUTY;
	}

	public Boolean getUUID_SUPPORT() {
		return UUID_SUPPORT;
	}

	public void setUUID_SUPPORT(Boolean uUID_SUPPORT) {
		UUID_SUPPORT = uUID_SUPPORT;
	}


	public String getLANGUAGE() {
		return LANGUAGE;
	}

	public void setLANGUAGE(String lANGUAGE) {
		LANGUAGE = lANGUAGE;
	}

	public String getWELCOME() {
		return WELCOME;
	}

	public void setWELCOME(String wELCOME) {
		WELCOME = wELCOME;
	}

	public String getFAREWELL() {
		return FAREWELL;
	}

	public void setFAREWELL(String fAREWELL) {
		FAREWELL = fAREWELL;
	}

	public String getDENIAL() {
		return DENIAL;
	}

	public void setDENIAL(String dENIAL) {
		DENIAL = dENIAL;
	}

	public String getCLOSED() {
		return CLOSED;
	}

	public void setCLOSED(String cLOSED) {
		CLOSED = cLOSED;
	}

	public String getFOR_HIRE_MSG() {
		return FOR_HIRE_MSG;
	}

	public void setFOR_HIRE_MSG(String fOR_HIRE_MSG) {
		FOR_HIRE_MSG = fOR_HIRE_MSG;
	}

	public String getRENTAL_MSG() {
		return RENTAL_MSG;
	}

	public void setRENTAL_MSG(String rENTAL_MSG) {
		RENTAL_MSG = rENTAL_MSG;
	}

	public String getDEFAULT_RENTAL_OWNER() {
		return DEFAULT_RENTAL_OWNER;
	}

	public void setDEFAULT_RENTAL_OWNER(String dEFAULT_RENTAL_OWNER) {
		DEFAULT_RENTAL_OWNER = dEFAULT_RENTAL_OWNER;
	}

	public Boolean getNPC_FOR_HIRE() {
		return NPC_FOR_HIRE;
	}

	public void setNPC_FOR_HIRE(Boolean nPC_FOR_HIRE) {
		NPC_FOR_HIRE = nPC_FOR_HIRE;
	}

	public Double getNPC_COMMISSION() {
		return NPC_COMMISSION;
	}

	public void setNPC_COMMISSION(Double nPC_COMMISSION) {
		NPC_COMMISSION = nPC_COMMISSION;
	}

	public Double getNPC_RENTAL_PRICE() {
		return NPC_RENTAL_PRICE;
	}

	public void setNPC_RENTAL_PRICE(Double nPC_RENTAL_PRICE) {
		NPC_RENTAL_PRICE = nPC_RENTAL_PRICE;
	}

	public Boolean getRIGHT_CLICK_PLAYER_SHOP() {
		return RIGHT_CLICK_PLAYER_SHOP;
	}

	public void setRIGHT_CLICK_PLAYER_SHOP(Boolean rIGHT_CLICK_PLAYER_SHOP) {
		RIGHT_CLICK_PLAYER_SHOP = rIGHT_CLICK_PLAYER_SHOP;
	}

	public Boolean getONDUTY_IN_SHOP_ONLY() {
		return ONDUTY_IN_SHOP_ONLY;
	}

	public void setONDUTY_IN_SHOP_ONLY(Boolean oNDUTY_IN_SHOP_ONLY) {
		ONDUTY_IN_SHOP_ONLY = oNDUTY_IN_SHOP_ONLY;
	}

	public Boolean getNPC_IN_SHOP_ONLY() {
		return NPC_IN_SHOP_ONLY;
	}

	public void setNPC_IN_SHOP_ONLY(Boolean nPC_IN_SHOP_ONLY) {
		NPC_IN_SHOP_ONLY = nPC_IN_SHOP_ONLY;
	}

	public int getMAX_NPCS_PER_PLAYER() {
		return MAX_NPCS_PER_PLAYER;
	}

	public void setMAX_NPCS_PER_PLAYER(int mAX_NPCS_PER_PLAYER) {
		MAX_NPCS_PER_PLAYER = mAX_NPCS_PER_PLAYER;
	}

	public static Boolean getMANAGE_IN_SHOP_ONLY() {
		return MANAGE_IN_SHOP_ONLY;
	}

	public static void setMANAGE_IN_SHOP_ONLY(Boolean mANAGE_IN_SHOP_ONLY) {
		MANAGE_IN_SHOP_ONLY = mANAGE_IN_SHOP_ONLY;
	}
}
