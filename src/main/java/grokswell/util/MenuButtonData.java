package grokswell.util;

//import static java.lang.System.out;
import grokswell.hypermerchant.HyperMerchantPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class MenuButtonData {
	private static File dataFolder;
	private HyperMerchantPlugin plugin;
	YamlConfiguration menuButtonData;
	public ItemStack first_page;
	public ItemStack back;
	public ItemStack help;
	public ItemStack forward;
	public ItemStack last_page;
  
    public MenuButtonData(HyperMerchantPlugin plgn) {
        plugin = plgn;
	    dataFolder = plugin.getDataFolder();
	    menuButtonData = new YamlConfiguration();
  	    if ( !dataFolder.isDirectory() )  dataFolder.mkdir();
  	    loadMenuButtonData();
  	    setMenuButtonItemStacks();
    }
  
  
    public YamlConfiguration getPlayerData() {
  	  return this.menuButtonData;
    }
  
    public void saveMenuButtonData(String path, Object value) {
	  File menuButtonFile = null;
	  menuButtonData.set(path, value);
	  menuButtonFile = new File(dataFolder, "menubuttons.yml");
	  try {
		  menuButtonData.save(menuButtonFile);
	  }
  	  catch(IOException ex) {
		  plugin.getLogger().severe("Cannot save to menubuttons.yml");
  	  }
    }
  
    private void loadMenuButtonData() {
		File menuButtonFile = null;
		menuButtonFile = new File(dataFolder, "menubuttons.yml");
	
		try {
			if (menuButtonFile.exists()) {
				menuButtonData.load(menuButtonFile);
			} else {
				saveMenuButtonData("first_page.material","STATIONARY_LAVA");
				saveMenuButtonData("first_page.data","0");

				saveMenuButtonData("back.material","STATIONARY_WATER");
				saveMenuButtonData("back.data","0");

				saveMenuButtonData("help.material","PAPER");
				saveMenuButtonData("help.data","0");

				saveMenuButtonData("forward.material","STATIONARY_WATER");
				saveMenuButtonData("forward.data","0");

				saveMenuButtonData("last_page.material","STATIONARY_LAVA");
				saveMenuButtonData("last_page.data","0");
			}
		}
		catch (InvalidConfigurationException e) {
			plugin.getLogger().severe("Invalid menubuttons.yml file");
		} 
	  	catch(IOException ex) {
				plugin.getLogger().severe("Cannot load menubuttons.yml");
	  	}
    }
    
    private void setMenuButtonItemStacks() {
    	first_page = new ItemStack(Material.getMaterial(menuButtonData.getString("first_page.material")));
    	first_page.setDurability(Short.parseShort(menuButtonData.getString("first_page.data")));

    	back = new ItemStack(Material.getMaterial(menuButtonData.getString("back.material")));
    	back.setDurability(Short.parseShort(menuButtonData.getString("back.data")));

    	help = new ItemStack(Material.getMaterial(menuButtonData.getString("help.material")));
    	help.setDurability(Short.parseShort(menuButtonData.getString("help.data")));

    	forward = new ItemStack(Material.getMaterial(menuButtonData.getString("forward.material")));
    	forward.setDurability(Short.parseShort(menuButtonData.getString("forward.data")));

    	last_page = new ItemStack(Material.getMaterial(menuButtonData.getString("last_page.material")));
    	last_page.setDurability(Short.parseShort(menuButtonData.getString("last_page.data")));
    }
}

