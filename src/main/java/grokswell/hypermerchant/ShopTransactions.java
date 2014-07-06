package grokswell.hypermerchant;

import static java.lang.System.out;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.api.HyperEconAPI;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.transaction.TransactionResponse;

public class ShopTransactions {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	int items_count;
	String shopname;
	private Player player;
	private HyperConomy hc;
	private LanguageFile hc_lang;
    MerchantMenu shopmenu;
    HyperPlayer hp;
	HyperAPI hyperAPI = new HyperAPI();
	HyperEconAPI heAPI = new HyperEconAPI();
	
	public ShopTransactions(Player plyr, String sname, HyperMerchantPlugin plgn, MerchantMenu sm) {
		player=plyr;
		shopname=sname;
		shopmenu = sm;
		hc = HyperConomy.hc;
		hp = hyperAPI.getHyperPlayer(player.getName());
		hc_lang = hc.getLanguageFile();

	}
	
	//PLAYER SELLS ENCHANT TO SHOP
	public HyperObject Sell(String enchant) {

		HyperObject ho = hyperAPI.getHyperObject(enchant,hyperAPI.getShop(shopname).getEconomy());
		if ((hyperAPI.getShop(shopname).isTradeable(ho))) {
			TransactionResponse response = hyperAPI.sell(player, ho, 1, hyperAPI.getShop(shopname));
			response.sendMessages();
			if (response.successful()){
				return ho;
			}
		}
		
		
		return null;
	}
	
	//PLAYER SELLS ITEM TO SHOP
	public HyperObject Sell(ItemStack item_stack) {
			
		int item_amount = item_stack.getAmount();
		HyperObject ho = hyperAPI.getHyperObject(item_stack, hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		if (ho==null) {
			return null;
		}
		String item_name = ho.getDisplayName().toLowerCase();
		ho = hyperAPI.getHyperObject(item_name, hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		
		if (shopmenu.getShopStock().display_names.contains(item_name) && (hyperAPI.getShop(shopname).isTradeable(ho))) {
			player.getInventory().addItem(item_stack);
			TransactionResponse response = hyperAPI.sell(player, ho, item_amount, hyperAPI.getShop(shopname));
			response.sendMessages();
			return ho;
		}
		
		
		return null;
	}
	
	//PLAYER BUYS ITEM FROM SHOP
	public HyperObject Buy(String item, int qty, double commission) {
		HyperObject ho = hyperAPI.getHyperObject(item.replaceAll(" ", "_"), hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		if (!hp.hasBuyPermission(hyperAPI.getShop(shopname))) {
			player.sendMessage("You cannot buy from this shop.");
			return null;
		}
		
		TransactionResponse response = hyperAPI.buy(player, ho, qty, hyperAPI.getShop(shopname));
		
		if (hyperAPI.getPlayerShopList().contains(shopname) && commission > 0.0) {
		    if (ho.isShopObject()){
			    double amount = ho.getBuyPrice()*commission;
			    String owner_name = hyperAPI.getShop(shopname).getOwner().getName();
				heAPI.withdraw(amount, Bukkit.getPlayer(owner_name));
				heAPI.depositAccount(amount, owner_name);
		    }
		}

		return ho;
	}
	
	
	//PLAYER-MANAGER REMOVES ITEMS FROM SHOP
	public HyperObject Remove(String item, int qty) {
		HyperObject ho = hyperAPI.getHyperObject(item.replaceAll(" ", "_"), hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		int space = ho.getAvailableSpace(player.getInventory());
		if (space < qty) {
			player.sendMessage("You haven't got enough space in your inventory to take "+qty+" "+ho.getDisplayName());
			return null;
		}
		if (ho.getStock() < 1) {
			return ho;
		}
		if (1 <= ho.getStock() && ho.getStock() < qty) {
			qty=(int) ho.getStock();
		}
		
		ho.add(qty, player.getInventory());
		ho.setStock(ho.getStock() - qty);
		return ho;
	}	
	
	//PLAYER-MANAGER ADDS ITEMS TO SHOP
	public HyperObject Add(ItemStack item_stack) {
		PlayerShop pshop=hyperAPI.getPlayerShop(this.shopname);
		HyperEconomy he = hc.getDataManager().getEconomy(pshop.getEconomy());
		HyperObject ho = hp.getHyperEconomy().getHyperObject(item_stack);
		int amount = item_stack.getAmount();
		int globalMaxStock = hc.getConf().getInt("shop.max-stock-per-item-in-playershops");
		HyperObject ho2 = he.getHyperObject(ho.getName(), pshop);
		if (ho2.getStock() + amount > globalMaxStock) {
			player.sendMessage("CANT_ADD_MORE_STOCK");
			return null;
		}
		if (ho2.getType() == HyperObjectType.ITEM) {
			ho2.setStock(ho2.getStock() + amount);
			player.sendMessage("You added "+amount+" "+ho2.getDisplayName()+" to the shop "+this.shopname);
			return ho2;
		}
		return null;
	}
	
	//PLAYER-MANAGER ADDS ENCHANT TO SHOP
		public HyperObject Add(String enchant) {

			HyperObject ho = hyperAPI.getHyperObject(enchant,hyperAPI.getShop(shopname).getEconomy());
			int globalMaxStock = hc.getConf().getInt("shop.max-stock-per-item-in-playershops");
			if (ho.getStock() + 1 > globalMaxStock) {
				player.sendMessage("CANT_ADD_MORE_STOCK");
				return null;
			} else {

				ho.setStock(ho.getStock() + 1);
				return ho;
			}
		}

}