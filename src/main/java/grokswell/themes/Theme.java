package grokswell.themes;

//import static java.lang.System.out;
import grokswell.hypermerchant.HyperMerchantPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

public class Theme {
	static private File themeFolder;
	static private HyperMerchantPlugin plugin;
	private static String themePath;

    public String sitemname;
    public String sbuy;
    public String sbuyprice;
    public String ssell;
    public String ssellprice;
    public String sstock;
    public String sstockamount;
    public String mitemname;
    public String mbuy;
    public String mbuyprice;
    public String mbuymode;
    public String msell;
    public String msellprice;
    public String msellmode;
    public String mstock;
    public String mstockamount;
    public String mstatus;
    public String mstatusvalue;

	
	public Theme(HyperMerchantPlugin plgn) {
        plugin = plgn;
        themeFolder = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+
        		"themes");
		if (!themeFolder.isDirectory()) themeFolder.mkdir();

		//loadTheme("default");
    }
    	  
    public void loadTheme(String theme_name) {
		File configFile = null;
		InputStream defThemeStream = null;
		//YamlConfiguration defConfig = null;
		YamlConfiguration theme = null;

        themePath= (plugin.getDataFolder()+File.separator+
        		"themes"+File.separator+theme_name+".yml");
		defThemeStream = plugin.getResource(themePath);
		configFile = new File(themePath);
		//HashMap<String, String> theme = new HashMap<String, String>();
		
	 
		// Look for defaults in the jar
	    if (defThemeStream != null) {
	       //defConfig = YamlConfiguration.loadConfiguration(defLanguageStream);
	    	defThemeStream = null;
	    }
    	try {
			if (configFile.createNewFile()) {
				plugin.saveResource("themes"+File.separator+plugin.settings.getLANGUAGE()+".yml", true);
			}
		} catch (IOException ioe) {
			
		}
		theme = YamlConfiguration.loadConfiguration(configFile);
		

        sitemname = theme.getString("sitemname");
	    sbuy = theme.getString("sbuy");
	    sbuyprice = theme.getString("sbuyprice");
	    ssell = theme.getString("ssell");
	    ssellprice = theme.getString("ssellprice");
	    sstock = theme.getString("sstock");
	    sstockamount = theme.getString("sstockamount");
	    mitemname = theme.getString("mitemname");
	    mbuy = theme.getString("mbuy");
	    mbuyprice = theme.getString("mbuyprice");
	    mbuymode = theme.getString("mbuymode");
	    msell = theme.getString("msell");
	    msellprice = theme.getString("msellprice");
	    msellmode = theme.getString("msellmode");
	    mstock = theme.getString("mstock");
	    mstockamount = theme.getString("mstockamount");
	    mstatus = theme.getString("mstatus");
	    mstatusvalue = theme.getString("mstatusvalue");
		
    }
}

