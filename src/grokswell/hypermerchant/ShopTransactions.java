package grokswell.hypermerchant;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.DataHandler;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperObject;
import regalowl.hyperconomy.HyperObjectAPI;
import regalowl.hyperconomy.HyperPlayer;
import regalowl.hyperconomy.LanguageFile;
import regalowl.hyperconomy.ShopFactory;
import regalowl.hyperconomy.TransactionResponse;

public class ShopTransactions {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	int items_count;
	String shopname;
	private Player player;
	private HyperConomy hc;
	private DataHandler hc_functions;
	private LanguageFile hc_lang;
	private ShopFactory hc_factory;
    private HyperMerchantPlugin plugin;
	HyperObjectAPI hyperObAPI = new HyperObjectAPI();
	HyperAPI hyperAPI = new HyperAPI();
	
	public ShopTransactions(Player plyr, String sname, HyperMerchantPlugin plgn) {
		hc = HyperConomy.hc;
		hc_functions = hc.getDataFunctions();
		hc_lang = hc.getLanguageFile();
		hc_factory = hc.getShopFactory();
		player=plyr;
		plugin=plgn;
		shopname=sname;

	}
	
	public boolean Sell(ItemStack item_stack) {
		HyperPlayer hp = hc_functions.getHyperPlayer(player);
			if (player.getGameMode() == GameMode.CREATIVE && hc.s().gB("block-selling-in-creative-mode")) {
				player.sendMessage(hc_lang.get("CANT_SELL_CREATIVE"));
				return false;
			}
		String item_name="air";
		int item_amount = item_stack.getAmount();
		String item_id = Integer.toString(item_stack.getTypeId());
		String item_data = Integer.toString(item_stack.getData().getData());
		if (plugin.items_by_id.containsKey(item_id+":"+item_data)) {
			item_name=plugin.items_by_id.get(item_id+":"+item_data);
		}
		if ((!item_name.equals("air")) && (hc_factory.getShop(shopname).has(item_name))) {
			player.getInventory().addItem(item_stack);
			HyperObject ho = hc_functions.getHyperObject(item_name, hp.getEconomy());
			if (!hc.isLocked()) {
				TransactionResponse response = hyperObAPI.sell(player, ho, item_amount);
				response.sendMessages();
				return true;
			}
		}
		return false;
	}
	
	public void Buy(String item, int qty) {
		HyperPlayer hp = hc_functions.getHyperPlayer(player);
		HyperObject ho = hc_functions.getHyperObject(item, hp.getEconomy());
		if (!hc.isLocked()) {
			TransactionResponse response = hyperObAPI.buy(player, ho, qty);
			response.sendMessages();
		}
	}

}