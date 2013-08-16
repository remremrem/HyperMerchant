package grokswell.hypermerchant;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.MemoryDataKey;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.ai.speech.SimpleSpeechController;

import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.ShopFactory;

import grokswell.hypermerchant.Settings.Setting;

public class HyperMerchantTrait extends Trait {
	HyperAPI hyperAPI = new HyperAPI();
	String store_name = hyperAPI.getGlobalShopAccount();
	final HyperMerchantPlugin plugin;

    
	private String farewellMsg = Setting.FAREWELL.asString();
	private String welcomeMsg = Setting.WELCOME.asString();
	private String denialMsg = Setting.DENIAL.asString();
	private String closedMsg = Setting.CLOSED.asString();
	
	public DataKey trait_key;

	public HyperMerchantTrait() {
		super("hypermerchant");
		plugin = (HyperMerchantPlugin) Bukkit.getServer().getPluginManager().getPlugin("HyperMerchant");
	}

	@Override
	public void load(DataKey key) {
		this.trait_key = key;
		store_name = key.getString("store_name");

		// Override defaults if they exist

		if (key.keyExists("welcome.default"))
			welcomeMsg = key.getString("welcome.default");
		if (key.keyExists("farewell.default"))
			farewellMsg = key.getString("farewell.default");
		if (key.keyExists("denial.default"))
			denialMsg = key.getString("denial.default");
		if (key.keyExists("closed.default"))
			closedMsg = key.getString("closed.default");

	}

	@EventHandler
	public void onRightClick(net.citizensnpcs.api.event.NPCRightClickEvent event) {
		this.plugin.getLogger().info("a: "+this.npc.data().has("traits"));
		
		if(this.npc!=event.getNPC()) return;

		Player player = event.getClicker();
		if (!player.hasPermission("hypermerchant.npc")){
			player.sendMessage(denialMsg);
			return;

		} else {
			HyperConomy hc = HyperConomy.hc;
			ShopFactory sf = hc.getShopFactory();
			ArrayList<String> shoplist = sf.listShops();
			String name;

			name=store_name;
			if (shoplist.contains(name) || name.equals(hyperAPI.getGlobalShopAccount())) {
				ShopStock shopstock = new ShopStock(player, player, name, plugin);
				//shopstock.pages is ArrayList<ArrayList<String>> shopstock.items_count is int
				new ShopMenu(name, 54, plugin, shopstock.pages, player, shopstock.items_count);
				return;
			} else {
				SpeechContext message = new SpeechContext(this.npc, closedMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
				plugin.getLogger().info("npc #"+this.npc.getId()+" is assigned to a store named "+
						store_name+". This store does not exist.");
				return;
			}
		}
	}
	
	@EventHandler
	public void onLeftClick(net.citizensnpcs.api.event.NPCLeftClickEvent event) {
		DataKey dk = new MemoryDataKey();
		this.plugin.getLogger().info("dk: "+dk.toString());
	}

	@Override
	public void save(DataKey key) {
		key.setString("store_name", store_name);
		key.setString("farewell.default", farewellMsg);
		key.setString("denial.default", denialMsg);
		key.setString("welcome.default", welcomeMsg);
		key.setString("closed.default", closedMsg);
		
	}
	
	@Override
	public void onAttach() {
		//plugin.getServer().getLogger().info("hypermerchant trait has been assigned to "+this.npc.getName());
		//key.setString("store_name", store_name);
	}

}