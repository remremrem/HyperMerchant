package grokswell.util;

//import static java.lang.System.out;
import grokswell.hypermerchant.HyperMerchantPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Blacklist {
	private static File dataFolder;
	private HyperMerchantPlugin plugin;
	YamlConfiguration blacklistData;
  
  public Blacklist(HyperMerchantPlugin plgn) {
      plugin = plgn;
		dataFolder = plugin.getDataFolder();
		blacklistData = new YamlConfiguration();
		if ( !dataFolder.isDirectory() )  dataFolder.mkdir();
		loadBlacklist();
  }
  
  
  public YamlConfiguration getPlayerData() {
  	return this.blacklistData;
  }
  
//  public void savePlayerData(String path, Object value) {
//		File playerFile = null;
//  	blacklistData.set(path, value);
//		playerFile = new File(dataFolder, "npcblacklist.yml");
//		try {
//				blacklistData.save(playerFile);
//		}
//  	catch(IOException ex) {
//			plugin.getLogger().severe("Cannot save to npcblacklist.yml");
//  	}
//  }
  
  
  private void loadBlacklist() {
		File blacklistFile = null;
		blacklistFile = new File(dataFolder, "npcblacklist.yml");

		try {

			if (!blacklistFile.exists()) {
				blacklistFile.setWritable(true);
				InputStream defConfigStream = plugin.getResource("npcblacklist.yml");
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				defConfig.save(blacklistFile);
			}
			blacklistData.load(blacklistFile);
		}
		catch (InvalidConfigurationException e) {
			plugin.getLogger().severe("Invalid npcblacklist.yml file. An entry is missing or formatting is wrong.");
		} 
		catch(IOException ex) {
			plugin.getLogger().severe("Cannot load npcblacklist.yml");
		}
  }
  
  public ArrayList<String> getNameBlacklist() {
	  ArrayList<String> names_list = new ArrayList<String>();
	  for (String n : blacklistData.getString("names").split(",")) {
		  names_list.add(n.trim());
	  }
	  return names_list;
  }
  
  public ArrayList<String> getTypesBlacklist() {
	  ArrayList<String> types_list = new ArrayList<String>();
	  for (String n : blacklistData.getString("types").split(",")) {
		  types_list.add(n.trim().toUpperCase());
	  }
	  return types_list;
  }
}

