package grokswell.hypermerchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataHandler;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperObjectAPI;
import regalowl.hyperconomy.ShopFactory;


public class ShopStock {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	ArrayList<String> items_in_stock = new ArrayList<String>();
	ArrayList<String> item_nums_sorted = new ArrayList<String>();
	ArrayList<String> item_materials_sorted = new ArrayList<String>();
	HashMap<String,String> items_by_num = new HashMap<String,String>();
	HashMap<String,String> items_by_material = new HashMap<String,String>();
	int items_count;
	private String shopname;
	private CommandSender sender;
	public HyperConomy hc;
	public ShopFactory hc_factory;
	public DataHandler hc_functions;
	private HyperObjectAPI hoa;
	
	ShopStock(CommandSender snder, Player player, String sname, HyperMerchantPlugin hmp) {
		hc = HyperConomy.hc;
		hc_factory = hc.getShopFactory();
		hc_functions = hc.getDataFunctions();
		hoa = new HyperObjectAPI();
		shopname = sname;
		sender = snder;

		try {
    		String nameshop = hc_factory.getShop(shopname).getName();
			ArrayList<String> names = hc_functions.getNames();
			int i = 0;
			while(i < names.size()) {
				String cname = names.get(i);
				if (nameshop == null || hc_factory.getShop(nameshop).has(cname)) {
					items_in_stock.add(cname);
					item_nums_sorted.add(String.valueOf(hoa.getType(cname, "default").name() + hoa.getId(cname, "default")) + cname);
					//item_materials_sorted.add(hoa.getType(cname, "default").name() + hoa.getMaterial(cname, "default") + cname);
					items_by_num.put(String.valueOf(hoa.getType(cname, "default").name() + hoa.getId(cname, "default")) + cname, cname);
					//items_by_material.put(hoa.getType(cname, "default").name() + hoa.getMaterial(cname, "default") + cname, cname);
				}
				i++;
			}			
			Collections.sort(items_in_stock, String.CASE_INSENSITIVE_ORDER);
			//Collections.sort(item_materials_sorted, String.CASE_INSENSITIVE_ORDER);
			Collections.sort(item_nums_sorted);
			
		} catch (Exception e) {
			sender.sendMessage("Error, cannot open shop inventory");
		}
	}

	public void SortStock (Integer sort_by) {		
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
					if (sort_by == 0){
						String item_name = this.hc_functions.fixName(this.items_in_stock.get(item_index));
						pages.get(page).add(item_name);
					}
					//else if (sort_by == 1){
					//	String item_name = this.hc_functions.fixName(this.items_by_material.get(this.item_materials_sorted.get(item_index)));
					//	pages.get(page).add(item_name);
					//}
					else if (sort_by == 2){
						String item_name = this.hc_functions.fixName(this.items_by_num.get(this.item_nums_sorted.get(item_index)));
						pages.get(page).add(item_name);
					}
				}
				count++;
				item_index++;
			}
			count=0;
			page++;
		}
	}
}
