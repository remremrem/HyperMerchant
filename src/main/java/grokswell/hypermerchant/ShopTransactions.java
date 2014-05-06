package grokswell.hypermerchant;

//import static java.lang.System.out;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.api.HyperEconAPI;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.transaction.TransactionResponse;

public class ShopTransactions {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	int items_count;
	String shopname;
	private Player player;
	private HyperConomy hc;
	private LanguageFile hc_lang;
    private ShopMenu shopmenu;
    HyperPlayer hp;
	HyperAPI hyperAPI = new HyperAPI();
	HyperEconAPI heAPI = new HyperEconAPI();
	
	public ShopTransactions(Player plyr, String sname, HyperMerchantPlugin plgn, ShopMenu sm) {
		player=plyr;
		shopname=sname;
		shopmenu = sm;
		hc = HyperConomy.hc;
		hp = hyperAPI.getHyperPlayer(player.getName());
		hc_lang = hc.getLanguageFile();

	}
	
	//PLAYER SELLS ENCHANT TO SHOP
	public boolean Sell(String enchant) {
			if (player.getGameMode() == GameMode.CREATIVE) {
				player.sendMessage(hc_lang.get("CANT_SELL_CREATIVE"));
				return false;
			}

		HyperObject ho = hyperAPI.getHyperObject(enchant,hyperAPI.getShop(shopname).getEconomy());
		if ((hyperAPI.getShop(shopname).isTradeable(ho))) {
			TransactionResponse response = hyperAPI.sell(player, ho, 1, hyperAPI.getShop(shopname));
			response.sendMessages();
			return response.successful();
		}
		
		
		return false;
	}
	
	//PLAYER SELLS ITEM TO SHOP
	public boolean Sell(ItemStack item_stack) {
		if (player.getGameMode() == GameMode.CREATIVE) {
			player.sendMessage(hc_lang.get("CANT_SELL_CREATIVE"));
			return false;
		}
			
		int item_amount = item_stack.getAmount();
		HyperObject ho = hyperAPI.getHyperObject(item_stack, hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		if (ho==null) {
			return false;
		}
		String item_name = ho.getDisplayName().toLowerCase();
		ho = hyperAPI.getHyperObject(item_name, hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		
		if (shopmenu.shopstock.items_in_stock.contains(item_name) && (hyperAPI.getShop(shopname).isTradeable(ho))) {
			player.getInventory().addItem(item_stack);
			TransactionResponse response = hyperAPI.sell(player, ho, item_amount, hyperAPI.getShop(shopname));
			response.sendMessages();
			return true;
		}
		
		
		return false;
	}
	
	//PLAYER BUYS ITEM FROM SHOP
	public void Buy(String item, int qty, double commission) {
		HyperObject ho = hyperAPI.getHyperObject(item.replaceAll(" ", "_"), hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		if (!hp.hasBuyPermission(hyperAPI.getShop(shopname))) {
			player.sendMessage("You cannot buy from this shop.");
			return;
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
		
		response.sendMessages();
	}

}