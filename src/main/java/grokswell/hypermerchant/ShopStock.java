package grokswell.hypermerchant;

//import static java.lang.System.out;

import grokswell.util.HyperToBukkit;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.javatuples.Pair;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.HyperAPI;


public class ShopStock {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	ArrayList<String> object_names;
	ArrayList<String> display_names;
	
	
	//Pair<Integer,String> pair1= Pair.with(42, "lalala"); <-- example of Pair usage
	int items_count;
    int display_zero_stock; //toggle displaying items with zero stock
	private String shopname;
	private String economy_name;
	private CommandSender sender;
	private HyperPlayer hp;
	private String menu_type;
	HyperToBukkit hypBuk;

	HyperAPI hyperAPI;
	
	ShopStock(CommandSender snder, Player plyer, String sname, HyperMerchantPlugin hmp, String menutype) {
		hyperAPI = hmp.hyperAPI;
		hypBuk = new HyperToBukkit();
		menu_type = menutype;
		hp = hyperAPI.getHyperPlayer(plyer.getName());
		shopname = sname;
		economy_name = hyperAPI.getShop(this.shopname).getEconomy();
		sender = snder;
		object_names = new ArrayList<String>();
		display_names = new ArrayList<String>();
		
		display_zero_stock=1;
		Refresh(0,1);
	}
	
	public void Refresh(Integer sort_by, Integer dzs) {
		display_zero_stock=dzs;
		object_names.clear();
		display_names.clear();
		ArrayList<TradeObject> available_objects = new ArrayList<TradeObject>();
		if (menu_type == "trade") {
			available_objects = hyperAPI.getAvailableObjects(shopname);
		}
		else if (menu_type == "manage") {
			try {
				available_objects = hyperAPI.getPlayerShop(this.shopname).getShopObjects();
			} catch (Exception e) {
				return;
			}
		}
		for (TradeObject ho:available_objects) {
			
			TradeObject ho1 = hyperAPI.getHyperObject(ho.getName(), hyperAPI.getShop(this.shopname).getEconomy(), hyperAPI.getShop(this.shopname));
			if (ho1==null) {
				//out.println("bad object name: "+ho.getName());
				continue;
			}
			if (display_zero_stock==0) {
				if (ho.getStock()>=1) {
					object_names.add(ho.getName());
					display_names.add(ho.getDisplayName().toLowerCase());
				}
			} else {
				object_names.add(ho.getName());
				display_names.add(ho.getDisplayName().toLowerCase());
			}
		}
		
		Collections.sort(object_names, String.CASE_INSENSITIVE_ORDER);
		ArrayList<String> sorted_items = Sort(sort_by);
		if (sorted_items!=null){
			LayoutPages(sorted_items);
		}
	}
	
	
	public ArrayList<String> Sort(Integer sort_by) {		
		ArrayList<String> sorted_items = new ArrayList<String>();
		try {
			int i = 0;

			if (sort_by==0) { //sort by item name
				while(i < object_names.size()) {
					String cname = object_names.get(i);
					sorted_items.add(cname);
					i++;
				}
				Collections.sort(sorted_items, String.CASE_INSENSITIVE_ORDER);
				
			} else if (sort_by==1) { //sort by item type/material
				ArrayList<Pair<String,String>> items_by_material = new ArrayList<Pair<String,String>>();
				while(i < object_names.size()) {
					String cname = object_names.get(i);
					TradeObject ho = hyperAPI.getHyperObject(cname, economy_name, hyperAPI.getShop(shopname));
					if (ho == null) {
						i++;
						continue;
					}
					if (ho.getType() == TradeObjectType.ITEM) {
						String mtrl = hypBuk.getItemStack(ho.getItemStack(1)).getType().toString().toLowerCase();
						items_by_material.add(Pair.with(mtrl+cname,cname));
						
					} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						items_by_material.add(Pair.with("enchantment"+cname,cname));
						
					} else if (ho.getType() == TradeObjectType.EXPERIENCE){
						items_by_material.add(Pair.with("xp"+cname,cname));
					}
					i++;
				}
				
				Collections.sort(items_by_material);
				for (Pair<String,String> pair : items_by_material){
					sorted_items.add(pair.getValue1());
				}
				
			} else if (sort_by==2) { //sort by item purchase price
				ArrayList<Pair<Double,String>> items_by_price = new ArrayList<Pair<Double,String>>();
				while(i < object_names.size()) {
					String cname = object_names.get(i);
					TradeObject ho = hyperAPI.getHyperObject(cname, economy_name, hyperAPI.getShop(shopname));
					if (ho == null) {
						i++;
						continue;
					}
					items_by_price.add(Pair.with(ho.getBuyPriceWithTax(1.0),cname));
					i++;
				}
				
				Collections.sort(items_by_price);
				for (Pair<Double,String> pair : items_by_price){
					sorted_items.add(pair.getValue1());
				}
				
			} else if (sort_by==3) { //sort by item sell price
				ArrayList<Pair<Double,String>> items_by_price = new ArrayList<Pair<Double,String>>();
				while(i < object_names.size()) {
					String cname = object_names.get(i);
					TradeObject ho = hyperAPI.getHyperObject(cname, economy_name, hyperAPI.getShop(shopname));
					if (ho == null) {
						i++;
						continue;
					}
					hp.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
					items_by_price.add(Pair.with(ho.getSellPriceWithTax(1.0, hp),cname));
					i++;
				}
				
				Collections.sort(items_by_price);
				for (Pair<Double,String> pair : items_by_price){
					sorted_items.add(pair.getValue1());
				}
				
				
			} else if (sort_by==4) { //sort by item quantity
				ArrayList<Pair<Double,String>> items_by_qty = new ArrayList<Pair<Double,String>>();
				while(i < object_names.size()) {
					String cname = object_names.get(i);
					TradeObject ho = hyperAPI.getHyperObject(cname, economy_name, hyperAPI.getShop(shopname));
					if (ho == null) {
						i++;
						continue;
					}
					items_by_qty.add(Pair.with(ho.getStock(),cname));
					i++;
				}
				
				Collections.sort(items_by_qty);
				for (Pair<Double,String> pair : items_by_qty){
					sorted_items.add(pair.getValue1());
				}
			}

			return sorted_items;
		} 
		
		catch (Exception e) {
			sender.sendMessage("Error, cannot open shop inventory");
			return null;
		}
	}
		
		
	public void LayoutPages(ArrayList<String> sorted_items){
		pages.clear();
		int count = 0;
		int item_index=0;
		int page = 0;
		items_count  = object_names.size();
		
		int number_per_page = 45;
		double maxpages = items_count/number_per_page;
		maxpages = Math.ceil(maxpages);  //number of pages to contain all items in this shop
		
		while (page <= maxpages) {
			pages.add(new ArrayList<String>());
			while (count < number_per_page) {
				if (item_index < items_count) {
					String item_name = sorted_items.get(item_index);
					pages.get(page).add(item_name);
				}
				count++;
				item_index++;
			}
			count=0;
			page++;
		}
		
	}
}
