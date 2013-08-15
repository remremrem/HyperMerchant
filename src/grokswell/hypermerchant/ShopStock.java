package grokswell.hypermerchant;

import java.util.ArrayList;
import java.util.Collections;
import static java.lang.System.out;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataHandler;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.LanguageFile;
import regalowl.hyperconomy.ShopFactory;


public class ShopStock {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	int items_count;
	private String shopname;
	private CommandSender sender;
	
	ShopStock(String args[], CommandSender snder, Player player, String sname, HyperMerchantPlugin hmp) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory hc_factory = hc.getShopFactory();
		DataHandler hc_functions = hc.getDataFunctions();
		LanguageFile hc_lang = hc.getLanguageFile();
		ArrayList<String> aargs = new ArrayList<String>();
		
		shopname = sname;
		sender = snder;
		for (int i = 0; i < args.length; i++) {
			aargs.add(args[i]);
		}
		try {
			
    		String nameshop = null;
    		if (player != null) {
    			if (!hc_factory.inAnyShop(player)) {
    				nameshop = null;
    			} else {
    				nameshop = hc_factory.getShop(shopname).getName();
    			}		
    		}
			ArrayList<String> names = hc_functions.getNames();
			ArrayList<String> items_in_stock = new ArrayList<String>();
			int i = 0;
			while(i < names.size()) {
				String cname = names.get(i);
				if (nameshop == null || hc_factory.getShop(nameshop).has(cname)) {
					items_in_stock.add(cname);
				}
				i++;
			}
			Collections.sort(items_in_stock, String.CASE_INSENSITIVE_ORDER);

			int number_per_page = 45;
			int count = 0;
			int item_index=0;
			int page = 0;
			items_count  = items_in_stock.size();

			out.println("this shop has "+ items_count +" items:");
			
			double maxpages = items_count/number_per_page;
			maxpages = Math.ceil(maxpages);
			while (page <= maxpages) {
				pages.add(new ArrayList<String>());
				while (count < number_per_page) {
					if (item_index < items_count) {
						String item_name = hc_functions.fixName(items_in_stock.get(item_index));
						pages.get(page).add(item_name);
					}
					count++;
					item_index++;
				}
				count=0;
				page++;
			}
		} catch (Exception e) {
			sender.sendMessage(hc_lang.get("BROWSE_SHOP_INVALID"));
		}
	}
}
