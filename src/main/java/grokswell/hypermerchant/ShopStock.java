package grokswell.hypermerchant;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.hyperobject.HyperObjectType;


public class ShopStock {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	ArrayList<String> items_in_stock = new ArrayList<String>();
	ArrayList<String> item_types_sorted = new ArrayList<String>();
	ArrayList<String> item_materials_sorted = new ArrayList<String>();
	HashMap<String,String> items_by_type = new HashMap<String,String>();
	HashMap<String,String> items_by_material = new HashMap<String,String>();
	ArrayList<String> object_names;
	int items_count;
	private String shopname;
	private CommandSender sender;

	HyperAPI hyperAPI;
	
	ShopStock(CommandSender snder, Player player, String sname, HyperMerchantPlugin hmp) {
		hyperAPI = new HyperAPI();
		
		shopname = sname;
		sender = snder;
		object_names = new ArrayList<String>();

		try {
    		ArrayList<HyperObject> available_objects = hyperAPI.getAvailableObjects(shopname);
			for (HyperObject ho:available_objects) {
				object_names.add(ho.getDisplayName().toLowerCase());
			}
			//Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
			Collections.sort(object_names, String.CASE_INSENSITIVE_ORDER);
			//out.println("object_names: "+ object_names);
			//out.println("heCon names: "+ names);
			int i = 0;
			
			while(i < object_names.size()) {
				String cname = object_names.get(i);
				items_in_stock.add(cname);
				HyperObject ho=hyperAPI.getHyperObject(cname, "default");
				if (ho.getType() == HyperObjectType.ITEM) {
					String mtrl = ho.getItemStack().getType().toString().toLowerCase();
					item_types_sorted.add(mtrl+cname);
					items_by_type.put(mtrl+cname,cname);
					
				} else if (ho.getType() == HyperObjectType.ITEM) {
					item_types_sorted.add(String.valueOf("enchantment"+cname));
					items_by_type.put("enchantment"+cname, cname);	
					
				} else {
					item_types_sorted.add(String.valueOf("xp"+cname));
					items_by_type.put("xp"+cname, cname);	
				}
				
				i++;
			}			
			
			Collections.sort(items_in_stock, String.CASE_INSENSITIVE_ORDER);
			Collections.sort(item_types_sorted);
			//out.println(items_in_stock);
		} 
		catch (Exception e) {
			sender.sendMessage("Error, cannot open shop inventory");
			out.println(e.toString());
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
						String item_name = this.items_in_stock.get(item_index);
						pages.get(page).add(item_name);
					}
					else if (sort_by == 2){
						String item_name = this.items_by_type.get(this.item_types_sorted.get(item_index));
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
