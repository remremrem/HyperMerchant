package grokswell.hypermerchant;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataHandler;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.ShopFactory;


public class ShopStock {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	int items_count;
	private String shopname;
	private CommandSender sender;
	
	ShopStock(CommandSender snder, Player player, String sname, HyperMerchantPlugin hmp) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory hc_factory = hc.getShopFactory();
		DataHandler hc_functions = hc.getDataFunctions();
		shopname = sname;
		sender = snder;

		try {
    		String nameshop = hc_factory.getShop(shopname).getName();
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
			sender.sendMessage("Error, cannot open shop inventory");
		}
	}
}
