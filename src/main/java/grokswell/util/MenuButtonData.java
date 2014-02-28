package grokswell.util;

//import static java.lang.System.out;
import grokswell.hypermerchant.HyperMerchantPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuButtonData {
	private static File dataFolder;
	private HyperMerchantPlugin plugin;
	YamlConfiguration menuButtonData;
	
	//Shop menu buttons
	public ItemStack first_page;
	public ItemStack back;
	public ItemStack help1;
	public ItemStack help2;
	public ItemStack help3;
	public ItemStack help4;
	public ItemStack help5;
	public ItemStack forward;
	public ItemStack last_page;
	
	//Manage player shop buttons
	public ItemStack buy_price;
	public ItemStack sell_price;
	public ItemStack status;
	public ItemStack manage_help_1;
	public ItemStack manage_help_2;
  
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
			if (!menuButtonFile.exists()) {
				menuButtonFile.setWritable(true);
				InputStream defConfigStream = plugin.getResource("menubuttons.yml");
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				defConfig.save(menuButtonFile);
			}
			
			menuButtonData.load(menuButtonFile);
		}
		
		catch (InvalidConfigurationException e) {
			plugin.getLogger().severe("Invalid menubuttons.yml file. An entry is missing or formatting is wrong.");
		} 
		
	  	catch(IOException ex) {
				plugin.getLogger().severe("Cannot load menubuttons.yml");
	  	}
    }
    
    
    private void setItemNameAndLore(ItemStack item, String name, String... lore) {
        ItemMeta im = item.getItemMeta();
        if (name!=null) {
            im.setDisplayName(ChatColor.GOLD+name);
        } else { 
            im.setDisplayName(" ");
        }
        if (lore!=null){
            im.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(im);
        //return item;
    }
    
    private String[] ParseLore(String lore) {
    	//if (lore.endsWith("\"")) {
    	//	lore.
    	//}
    	String[] l = null;
    	if (lore!=null){
	    	l = lore.split(",");
	    	for (String s : l){
	    		if (s.length() != 1) {
	    			s = s.trim();
	    		}
	    	}
	    	//out.println("string: "+l);
    	}
    	return l;
    }

    private void setMenuButtonItemStacks() {
    	first_page = new ItemStack(Material.getMaterial(menuButtonData.getString("first_page.material")));
    	first_page.setDurability(Short.parseShort(menuButtonData.getString("first_page.data")));
    	setItemNameAndLore(first_page, menuButtonData.getString("first_page.name"), ParseLore(menuButtonData.getString("first_page.lore")));

    	back = new ItemStack(Material.getMaterial(menuButtonData.getString("back.material")));
    	back.setDurability(Short.parseShort(menuButtonData.getString("back.data")));
    	setItemNameAndLore(back, menuButtonData.getString("back.name"), ParseLore(menuButtonData.getString("back.lore")));

    	help1 = new ItemStack(Material.getMaterial(menuButtonData.getString("help1.material")));
    	help1.setDurability(Short.parseShort(menuButtonData.getString("help1.data")));
    	setItemNameAndLore(help1, menuButtonData.getString("help1.name"), ParseLore(menuButtonData.getString("help1.lore")));

    	help2 = new ItemStack(Material.getMaterial(menuButtonData.getString("help2.material")));
    	help2.setDurability(Short.parseShort(menuButtonData.getString("help2.data")));
    	setItemNameAndLore(help2, menuButtonData.getString("help2.name"), ParseLore(menuButtonData.getString("help2.lore")));

    	help3 = new ItemStack(Material.getMaterial(menuButtonData.getString("help3.material")));
    	help3.setDurability(Short.parseShort(menuButtonData.getString("help3.data")));
    	setItemNameAndLore(help3, menuButtonData.getString("help3.name"), ParseLore(menuButtonData.getString("help3.lore")));

    	help4 = new ItemStack(Material.getMaterial(menuButtonData.getString("help4.material")));
    	help4.setDurability(Short.parseShort(menuButtonData.getString("help4.data")));
    	setItemNameAndLore(help4, menuButtonData.getString("help4.name"), ParseLore(menuButtonData.getString("help4.lore")));

    	help5 = new ItemStack(Material.getMaterial(menuButtonData.getString("help5.material")));
    	help5.setDurability(Short.parseShort(menuButtonData.getString("help5.data")));
    	setItemNameAndLore(help5, menuButtonData.getString("help5.name"), ParseLore(menuButtonData.getString("help5.lore")));

    	forward = new ItemStack(Material.getMaterial(menuButtonData.getString("forward.material")));
    	forward.setDurability(Short.parseShort(menuButtonData.getString("forward.data")));
    	setItemNameAndLore(forward, menuButtonData.getString("forward.name"), ParseLore(menuButtonData.getString("forward.lore")));

    	last_page = new ItemStack(Material.getMaterial(menuButtonData.getString("last_page.material")));
    	last_page.setDurability(Short.parseShort(menuButtonData.getString("last_page.data")));
    	setItemNameAndLore(last_page, menuButtonData.getString("last_page.name"), ParseLore(menuButtonData.getString("last_page.lore")));

    	buy_price = new ItemStack(Material.getMaterial(menuButtonData.getString("buy_price.material")));
    	buy_price.setDurability(Short.parseShort(menuButtonData.getString("buy_price.data")));
    	setItemNameAndLore(buy_price, menuButtonData.getString("buy_price.name"), ParseLore(menuButtonData.getString("buy_price.lore")));

    	sell_price = new ItemStack(Material.getMaterial(menuButtonData.getString("sell_price.material")));
    	sell_price.setDurability(Short.parseShort(menuButtonData.getString("sell_price.data")));
    	setItemNameAndLore(sell_price, menuButtonData.getString("sell_price.name"), ParseLore(menuButtonData.getString("sell_price.lore")));

    	status = new ItemStack(Material.getMaterial(menuButtonData.getString("status.material")));
    	status.setDurability(Short.parseShort(menuButtonData.getString("status.data")));
    	setItemNameAndLore(status, menuButtonData.getString("status.name"), ParseLore(menuButtonData.getString("status.lore")));

    	manage_help_1 = new ItemStack(Material.getMaterial(menuButtonData.getString("manage_help_1.material")));
    	manage_help_1.setDurability(Short.parseShort(menuButtonData.getString("manage_help_1.data")));
    	setItemNameAndLore(manage_help_1, menuButtonData.getString("manage_help_1.name"), ParseLore(menuButtonData.getString("manage_help_1.lore")));

    	manage_help_2 = new ItemStack(Material.getMaterial(menuButtonData.getString("manage_help_2.material")));
    	manage_help_2.setDurability(Short.parseShort(menuButtonData.getString("manage_help_2.data")));
    	setItemNameAndLore(manage_help_2, menuButtonData.getString("manage_help_2.name"), ParseLore(menuButtonData.getString("manage_help_2.lore")));

    }
}

