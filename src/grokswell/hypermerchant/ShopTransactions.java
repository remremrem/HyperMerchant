package grokswell.hypermerchant;

import static java.lang.System.out;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.EconomyManager;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.HyperObject;
import regalowl.hyperconomy.HyperObjectAPI;
import regalowl.hyperconomy.HyperPlayer;
import regalowl.hyperconomy.LanguageFile;
import regalowl.hyperconomy.TransactionResponse;

public class ShopTransactions {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	int items_count;
	String shopname;
	private Player player;
	private HyperConomy hc;
	private LanguageFile hc_lang;
	private EconomyManager ecoMan;
    private HyperMerchantPlugin plugin;
    private ShopMenu shopmenu;
    HyperPlayer hp;
    HyperEconomy hEcon;
	HyperObjectAPI hyperObAPI = new HyperObjectAPI();
	HyperAPI hyperAPI = new HyperAPI();
	
	public ShopTransactions(Player plyr, String sname, HyperMerchantPlugin plgn, ShopMenu sm) {
		player=plyr;
		plugin=plgn;
		shopname=sname;
		shopmenu = sm;
		hc = HyperConomy.hc;
		ecoMan = hc.getEconomyManager();
		hp = ecoMan.getHyperPlayer(player);
	    hEcon = hp.getHyperEconomy();
		hc_lang = hc.getLanguageFile();

	}
	//SELL ENCHANT
	public boolean Sell(String enchant) {
			if (player.getGameMode() == GameMode.CREATIVE) {
				player.sendMessage(hc_lang.get("CANT_SELL_CREATIVE"));
				return false;
			}
		if ((ecoMan.getShop(shopname).has(enchant))) {
			HyperObject ho = hEcon.getHyperObject(enchant);
			TransactionResponse response = hyperObAPI.sell(player, ho, 1);
			response.sendMessages();
			return true;
		}
		return false;
	}
	//SELL ITEM
	public boolean Sell(ItemStack item_stack) {
			if (player.getGameMode() == GameMode.CREATIVE) {
				player.sendMessage(hc_lang.get("CANT_SELL_CREATIVE"));
				return false;
			}
		int item_amount = item_stack.getAmount();
		HyperObject ho = hEcon.getHyperObject(item_stack, ecoMan.getShop(shopname));
		String item_name = ho.getName().toLowerCase();
		
		if (shopmenu.shopstock.items_in_stock.contains(item_name) && (ecoMan.getShop(shopname).has(item_name))) {
			player.getInventory().addItem(item_stack);
			ho = hEcon.getHyperObject(item_name);
			TransactionResponse response = hyperObAPI.sell(player, ho, item_amount);
			response.sendMessages();
			return true;
			
		}
		return false;
	}
	
	public void Buy(String item, int qty) {
		HyperObject ho = hEcon.getHyperObject(item);
		if (!hp.hasBuyPermission(ecoMan.getShop(shopname))) {
			player.sendMessage("You cannot buy from this shop.");
			return;
		}
		TransactionResponse response = hyperObAPI.buy(player, ho, qty);
		response.sendMessages();
	}

}