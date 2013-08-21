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

import regalowl.hyperconomy.DataHandler;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperPlayer;
import regalowl.hyperconomy.ShopFactory;

import grokswell.hypermerchant.Settings.Setting;

public class HyperMerchantTrait extends Trait {
	HyperAPI hyperAPI = new HyperAPI();
	String shop_name = hyperAPI.getGlobalShopAccount();
	final HyperMerchantPlugin plugin;

    
	String farewellMsg = Setting.FAREWELL.asString();
	String welcomeMsg = Setting.WELCOME.asString();
	String denialMsg = Setting.DENIAL.asString();
	String closedMsg = Setting.CLOSED.asString();
	boolean offduty = Setting.OFFDUTY.asBoolean();
	
	public DataKey trait_key;

	public HyperMerchantTrait() {
		super("hypermerchant");
		plugin = (HyperMerchantPlugin) Bukkit.getServer().getPluginManager().getPlugin("HyperMerchant");
	}

	@Override
	public void load(DataKey key) {
		this.trait_key = key;
		this.shop_name = key.getString("shop_name");

		// Override defaults if they exist

		if (key.keyExists("welcome.default"))
			this.welcomeMsg = key.getString("welcome.default");
		if (key.keyExists("farewell.default"))
			this.farewellMsg = key.getString("farewell.default");
		if (key.keyExists("denial.default"))
			this.denialMsg = key.getString("denial.default");
		if (key.keyExists("closed.default"))
			this.closedMsg = key.getString("closed.default");
		if (key.keyExists("offduty.default"))
			this.offduty = key.getBoolean("offduty.default");

	}

	@EventHandler
	public void onRightClick(net.citizensnpcs.api.event.NPCRightClickEvent event) {
		
		if(this.npc!=event.getNPC()) return;
		
		Player player = event.getClicker();
		if (!player.hasPermission("hypermerchant.npc")) {
			if (!this.denialMsg.isEmpty()) {
				SpeechContext message = new SpeechContext(this.npc, this.denialMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
			}
			return;
		}
		
		HyperConomy hc;
		hc = HyperConomy.hc;
		DataHandler dh = hc.getDataFunctions();
		ShopFactory sf = hc.getShopFactory();
		HyperPlayer hp = dh.getHyperPlayer(player);
			
		if (!hp.hasBuyPermission(sf.getShop(this.shop_name))) {
			if (!this.denialMsg.isEmpty()) {
				SpeechContext message = new SpeechContext(this.npc, this.denialMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
			}
			return;
			
		} else if (this.offduty) {
			if (!this.closedMsg.isEmpty()) {
				SpeechContext message = new SpeechContext(this.npc, this.closedMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
			}
			return;
			
		} else {
			ArrayList<String> shoplist = sf.listShops();
			if (shoplist.contains(this.shop_name)) {
				if  (!this.welcomeMsg.isEmpty()) {
					SpeechContext message = new SpeechContext(this.npc, this.welcomeMsg, player);
					new SimpleSpeechController(this.npc).speak(message);
				}
				//shopstock.pages is ArrayList<ArrayList<String>> shopstock.items_count is int
				new ShopMenu(this.shop_name, 54, plugin, player, player, this.npc);
				sf=null;
				hc=null;
				return;
				
			} else {
				if  (!this.closedMsg.isEmpty()) {
					SpeechContext message = new SpeechContext(this.npc, this.closedMsg, player);
					new SimpleSpeechController(this.npc).speak(message);
				}
				plugin.getLogger().info("npc #"+this.npc.getId()+" is assigned to a shop named "+
						shop_name+". This shop does not exist.");
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
		key.setString("shop_name", this.shop_name);
		key.setString("farewell.default", this.farewellMsg);
		key.setString("denial.default", this.denialMsg);
		key.setString("welcome.default", this.welcomeMsg);
		key.setString("closed.default", this.closedMsg);
		key.setBoolean("offduty.default", this.offduty);
		
	}
	
	@Override
	public void onAttach() {

	}
	
	public void onFarewell(Player player) {
		if (!this.farewellMsg.isEmpty()) {
			SpeechContext message = new SpeechContext(this.npc, this.farewellMsg, player);
			new SimpleSpeechController(this.npc).speak(message);
		}
	}

}