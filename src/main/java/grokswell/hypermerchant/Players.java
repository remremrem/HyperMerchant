package grokswell.hypermerchant;

//import static java.lang.System.out;
import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Players {
	private static File dataFolder;
	private HyperMerchantPlugin plugin;
	YamlConfiguration playerData;
    
    public Players(HyperMerchantPlugin plgn) {
        plugin = plgn;
		dataFolder = plugin.getDataFolder();
		playerData = new YamlConfiguration();
		if ( !dataFolder.isDirectory() )  dataFolder.mkdir();
		loadPlayerData();
    }
    
    
    public YamlConfiguration getPlayerData() {
    	return this.playerData;
    }
    
    public void savePlayerData(String path, Object value) {
		File playerFile = null;
    	playerData.set(path, value);
		playerFile = new File(dataFolder, "players.yml");
		try {
				playerData.save(playerFile);
		}
    	catch(IOException ex) {
			plugin.getLogger().severe("Cannot save to players.yml");
    	}
    }
    
    
    private void loadPlayerData() {
		File playerFile = null;
		playerFile = new File(dataFolder, "players.yml");

		try {

			if (playerFile.exists()) {
				playerData.load(playerFile);
			}
		}
		catch (InvalidConfigurationException e) {
			plugin.getLogger().severe("Invalid players.yml file");
		} 
    	catch(IOException ex) {
			plugin.getLogger().severe("Cannot load players.yml");
    	}
    }
}

