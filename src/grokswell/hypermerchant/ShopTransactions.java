package grokswell.hypermerchant;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.DataHandler;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperObject;
import regalowl.hyperconomy.HyperObjectAPI;
import regalowl.hyperconomy.HyperPlayer;
import regalowl.hyperconomy.LanguageFile;
import regalowl.hyperconomy.TransactionResponse;

public class ShopTransactions {
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	int items_count;
	private Player player;
	private HyperConomy hc;
	private DataHandler hc_functions;
	private LanguageFile hc_lang;
    private HyperMerchantPlugin plugin;
	HyperObjectAPI hyperAPI = new HyperObjectAPI();
	
	public ShopTransactions(Player plyr, String sname, HyperMerchantPlugin plgn) {
		hc = HyperConomy.hc;
		hc_functions = hc.getDataFunctions();
		hc_lang = hc.getLanguageFile();
		player=plyr;
		plugin=plgn;

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
		if (!item_name.equals("air")) {
			HyperObject ho = hc_functions.getHyperObject(item_name, hp.getEconomy());
			if (!hc.isLocked()) {
				TransactionResponse response = hyperAPI.sell(player, ho, item_amount);
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
			TransactionResponse response = hyperAPI.buy(player, ho, qty);
			response.sendMessages();
		}
	}

}