package grokswell.hypermerchant;

import static java.lang.System.out;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
    private YamlConfiguration config;
	private static File dataFolder;
	private HyperMerchantPlugin plugin;
    
    //Defaults
	static Boolean ENABLE_COMMAND = true;
    static Boolean ENABLE_NPCS = true;
    static Boolean OFFDUTY = false;
    static String WELCOME = "Welcome to my little shop.";
    static String FAREWELL = "I thank you for your continued patronage.";
    static String DENIAL = "I'm afraid you are not a shop member. " +
    		"I am not authorized to do business with you.";
    static String CLOSED = "I am sorry, I am closed for business at this time.";
    static Boolean NPC_FOR_HIRE = true;
    static Double NPC_COMMISSION = 0.10;
    static Boolean RIGHT_CLICK_PLAYER_SHOP = true;
    
    public Settings(HyperMerchantPlugin plgn) {
        plugin = plgn;
		dataFolder = plugin.getDataFolder();
		if (!dataFolder.isDirectory()) dataFolder.mkdir();
        loadConfig(plugin);
        //config = new YamlStorage(new File(plugin.getDataFolder() + File.separator + "config.yml"), "HyperMerchant Configuration");
    }
    	  
    private static void loadConfig(HyperMerchantPlugin plugin) {
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

        ENABLE_COMMAND = config.getBoolean("Main.enable-command");
        ENABLE_NPCS = config.getBoolean("Main.enable-npcs");
        OFFDUTY = config.getBoolean("Main.offduty");
        WELCOME = config.getString("Messages.welcome");
        FAREWELL = config.getString("Messages.farewell");
        DENIAL = config.getString("Messages.denial");
        CLOSED = config.getString("Messages.closed");
        NPC_FOR_HIRE = config.getBoolean("PlayerShops.npc-for-hire");
        NPC_COMMISSION = config.getDouble("PlayerShops.npc-commission");
        RIGHT_CLICK_PLAYER_SHOP = config.getBoolean("PlayerShops.right-click-player-shop");
		

		// Update file in resource folder.
		FileConfiguration cleanConfig = new YamlConfiguration();
		//Map<String, Object> configValues = config.getDefaults().getValues(false);
		out.println("config");
		//for (Map.Entry<String, Object> configEntry : configValues.entrySet()) {
		//out.println(configEntry);
		//out.println(configEntry.getValue());
		
		cleanConfig.set("Main.enable-command", "true");
		cleanConfig.set("Main.enable-npcs", "true");
		cleanConfig.set("Main.offduty", "false");
		cleanConfig.set("Messages.welcome", "Welcome to my little shop.");
		cleanConfig.set("Messages.farewell", "I thank you for your continued patronage.");
		cleanConfig.set("Messages.denial", ("I'm afraid you are not a shop member. " +
        		"I am not authorized to do business with you."));
		cleanConfig.set("Messages.closed", "I am sorry, I am closed for business at this time.");
		cleanConfig.set("PlayerShops.npc-for-hire", "true");
		cleanConfig.set("PlayerShops.npc-commission", "0.10");
		cleanConfig.set("PlayerShops.right-click-player-shop", "true");
		
		//}

		try {
			cleanConfig.save(configFile);
		} catch(IOException ex) {
			plugin.getLogger().severe("Cannot save config.yml");
		}
    }
}

//    public enum Setting {
//        ENABLE_COMMAND("enable_command.default", 1),
//        ENABLE_NPC("enable_npc.default", 1),
//        WELCOME("welcome.default", "Welcome to my little shop."),
//        FAREWELL("farewell.default", "I thank you for your continued patronage."),
//        DENIAL("denial.default", "I'm afraid you are not a shop member. " +
//        		"I am not authorized to do business with you."),
//        CLOSED("closed.default", "I am sorry, I am closed for business at this time."),
//        OFFDUTY("offduty.default", false);
//
//        private String path;
//        private Object value;
//
//        Setting(String path, Object value) {
//            this.path = path;
//            this.value = value;
//        }
//
//        public boolean asBoolean() {
//            return (Boolean) value;
//        }
//
//        public double asDouble() {
//            if (value instanceof String)
//                return Double.valueOf((String) value);
//            if (value instanceof Integer)
//                return (Integer) value;
//            return (Double) value;
//        }
//
//        public int asInt() {
//            return (Integer) value;
//        }
//
//        public String asString() {
//            return value.toString();
//        }
//
//        private Object get() {
//            return value;
//        }
//
//        private void set(Object value) {
//            this.value = value;
//        }
//    }

