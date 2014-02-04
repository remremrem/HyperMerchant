package grokswell.hypermerchant;

//import static java.lang.System.out;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	private static File dataFolder;
	static private HyperMerchantPlugin plugin;
    
    //Defaults
	static Boolean ENABLE_COMMAND = true;
    static Boolean ENABLE_NPCS = true;
    static Boolean OFFDUTY = false;
    static String WELCOME = "Welcome to my little shop.";
    static String FAREWELL = "I thank you for your continued patronage.";
    static String DENIAL = "I'm afraid you are not a shop member. " +
    		"I am not authorized to do business with you.";
    static String CLOSED = "I am sorry, I am closed for business at this time.";
    static String FOR_HIRE_MSG = "I am ready to work!";
    static String RENTAL_MSG = "Rent this shop space, and I work as your clerk.";
    static Boolean NPC_FOR_HIRE = true;
    static Double NPC_COMMISSION = 10.0;
    static Boolean RIGHT_CLICK_PLAYER_SHOP = true;
    static Boolean ONDUTY_IN_SHOP_ONLY = true;
    static Boolean NPC_IN_SHOP_ONLY = true;
    static int MAX_NPCS_PER_PLAYER = 2;
    
    
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
}
