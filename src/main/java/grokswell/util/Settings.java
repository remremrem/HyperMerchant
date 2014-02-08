package grokswell.util;

//import static java.lang.System.out;
import grokswell.hypermerchant.HyperMerchantPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	private static File dataFolder;
	static private HyperMerchantPlugin plugin;
    
    //Defaults
	static   Boolean ENABLE_COMMAND = true;
	static   Boolean ENABLE_NPCS = true;
	static   Boolean OFFDUTY = false;
	static   String WELCOME = "Welcome to my little shop.";
	static   String FAREWELL = "I thank you for your continued patronage.";
	static   String DENIAL = "I'm afraid you are not a shop member. " +
    		"I am not authorized to do business with you.";
	static   String CLOSED = "I am sorry, I am closed for business at this time.";
	static   String FOR_HIRE_MSG = "I am ready to work!";
	static   String RENTAL_MSG = "Rent this shop space, and I work as your clerk.";
	static   Boolean NPC_FOR_HIRE = true;
	static   Double NPC_COMMISSION = 10.0;
	static   Boolean RIGHT_CLICK_PLAYER_SHOP = true;
	static   Boolean ONDUTY_IN_SHOP_ONLY = true;
	static   Boolean NPC_IN_SHOP_ONLY = true;
	static   int MAX_NPCS_PER_PLAYER = 2;


	public Settings(HyperMerchantPlugin plgn) {
        plugin = plgn;
		dataFolder = plugin.getDataFolder();
		if (!dataFolder.isDirectory()) dataFolder.mkdir();
        loadConfig();
        saveConfig();
    }
    	  
    private static void loadConfig() {
		File configFile = null;
		InputStream defConfigStream = null;
		YamlConfiguration defConfig = null;
		YamlConfiguration config = null;
		
		defConfigStream = plugin.getResource("config.yml");
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
        if (config.contains("Messages.npc-for-hire")){
        NPC_FOR_HIRE = config.getBoolean("PlayerShops.npc-for-hire");
        }
        if (config.contains("Messages.npc-commission")){
        NPC_COMMISSION = config.getDouble("PlayerShops.npc-commission");
        }
        if (config.contains("Messages.right-click-player-shop")){
        RIGHT_CLICK_PLAYER_SHOP = config.getBoolean("PlayerShops.right-click-player-shop");
        }
        if (config.contains("Messages.onduty-in-shop-only")){
        ONDUTY_IN_SHOP_ONLY = config.getBoolean("PlayerShops.onduty-in-shop-only");
        }
        if (config.contains("Messages.npc-in-shop-only")){
        NPC_IN_SHOP_ONLY = config.getBoolean("PlayerShops.npc-in-shop-only");
        }
        if (config.contains("Messages.max-npcs-per-player")){
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
	    config.set("Messages.welcome", WELCOME);
	    config.set("Messages.farewell", FAREWELL);
	    config.set("Messages.denial", DENIAL);
	    config.set("Messages.closed", CLOSED);
	    config.set("Messages.forHireMsg", FOR_HIRE_MSG);
	    config.set("Messages.rentalMsg", RENTAL_MSG);
	    config.set("PlayerShops.npc-for-hire", NPC_FOR_HIRE);
	    config.set("PlayerShops.npc-commission", NPC_COMMISSION);
	    config.set("PlayerShops.right-click-player-shop", RIGHT_CLICK_PLAYER_SHOP);
	    config.set("PlayerShops.onduty-in-shop-only", ONDUTY_IN_SHOP_ONLY);
	    config.set("PlayerShops.npc-in-shop-only", NPC_IN_SHOP_ONLY);
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
}
