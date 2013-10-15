package grokswell.hypermerchant;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
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
    HyperPlayer hp;
    HyperEconomy hEcon;
	HyperObjectAPI hyperObAPI = new HyperObjectAPI();
	HyperAPI hyperAPI = new HyperAPI();
	
	public ShopTransactions(Player plyr, String sname, HyperMerchantPlugin plgn) {
		player=plyr;
		plugin=plgn;
		shopname=sname;
		hc = HyperConomy.hc;
		ecoMan = hc.getEconomyManager();
		hp = ecoMan.getHyperPlayer(player);
	    hEcon = hp.getHyperEconomy();
		hc_lang = hc.getLanguageFile();

	}
	//SELL ENCHANT
	public boolean Sell(String enchant) {
			if (player.getGameMode() == GameMode.CREATIVE && hc.s().gB("block-selling-in-creative-mode")) {
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
			if (player.getGameMode() == GameMode.CREATIVE && hc.s().gB("block-selling-in-creative-mode")) {
				player.sendMessage(hc_lang.get("CANT_SELL_CREATIVE"));
				return false;
			}
		String item_name="air";
		int item_amount = item_stack.getAmount();
		String item_id = Integer.toString(item_stack.getTypeId());
		String item_data = Integer.toString(item_stack.getData().getData());
		//if item is a tool, in toollist..
		if (plugin.items_by_id.containsKey(item_id+":"+item_data)) {
			item_name=plugin.items_by_id.get(item_id+":"+item_data);
		}
		if ((!item_name.equals("air")) && (ecoMan.getShop(shopname).has(item_name))) {
			player.getInventory().addItem(item_stack);
			HyperObject ho = hEcon.getHyperObject(item_name);
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