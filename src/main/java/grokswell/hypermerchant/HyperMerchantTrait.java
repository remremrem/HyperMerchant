package grokswell.hypermerchant;

import static java.lang.System.out;

import grokswell.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.ai.speech.SimpleSpeechController;
import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.api.HyperEconAPI;
import regalowl.hyperconomy.account.HyperPlayer;


public class HyperMerchantTrait extends Trait {
	HyperAPI hyperAPI = new HyperAPI();
	HyperEconAPI heAPI = new HyperEconAPI();
	static ArrayList<String> shoplist;
	HashMap<String,ShopMenu> customer_menus = new HashMap<String,ShopMenu>();
	ArrayList<String> hire_cooldown;
	ArrayList<String> rental_cooldown;
	MerchantMethods merchmeth;
	Utils utils;
	final HyperMerchantPlugin plugin;
	
	public DataKey trait_key;
	
	String farewellMsg;
	String welcomeMsg;
	String denialMsg;
	String closedMsg;
	String forHireMsg;
	String rentalMsg;
	double comission;
	double rental_price;
	boolean offduty;
	boolean forhire;
	boolean rental;
	boolean hired;
	boolean rented;
	String location;
	String shop_name;

	public HyperMerchantTrait() {
		super("hypermerchant");
		plugin = (HyperMerchantPlugin) Bukkit.getServer().getPluginManager().getPlugin("HyperMerchant");
		
		hire_cooldown = new ArrayList<String>();
		rental_cooldown = new ArrayList<String>();
		merchmeth = new MerchantMethods();
		utils=new Utils();

		farewellMsg = plugin.settings.getFAREWELL();
		welcomeMsg = plugin.settings.getWELCOME();
		denialMsg = plugin.settings.getDENIAL();
		closedMsg = plugin.settings.getCLOSED();
		forHireMsg = plugin.settings.getFOR_HIRE_MSG();
		rentalMsg = plugin.settings.getRENTAL_MSG();
		comission = plugin.settings.getNPC_COMMISSION();
		rental_price = plugin.settings.getNPC_RENTAL_PRICE();
		offduty = plugin.settings.getOFFDUTY();
		location = null;
		forhire = false;
		rental = false;
		hired = false;
		rented = false;
		shop_name = hyperAPI.getDefaultServerShopAccountName();
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
		if (key.keyExists("forHireMsg.default"))
			this.forHireMsg = key.getString("forHireMsg.default");
		if (key.keyExists("rentalMsg.default"))
			this.rentalMsg = key.getString("rentalMsg.default");
		if (key.keyExists("comission.default"))
			this.comission = key.getDouble("comission.default");
		if (key.keyExists("rental_price.default"))
			this.rental_price = key.getDouble("rental_price.default");
		if (key.keyExists("offduty.default"))
			this.offduty = key.getBoolean("offduty.default");
		if (key.keyExists("forhire.default"))
			this.forhire = key.getBoolean("forhire.default");
		if (key.keyExists("rental.default"))
			this.rental = key.getBoolean("rental.default");
		if (key.keyExists("hired.default"))
			this.hired = key.getBoolean("hired.default");
		if (key.keyExists("rented.default"))
			this.rented = key.getBoolean("rented.default");
		if (key.keyExists("location.default"))
			this.location = key.getString("location.default");

	}
	
    class RemoveCustomerCooldown extends BukkitRunnable {
    	String playername;
        public RemoveCustomerCooldown(String plynam) {
        	playername = plynam;
        }
        public void run() {
            // What you want to schedule goes here
            plugin.customer_cooldowns.remove(playername);
        }
    }
	
    class RemoveRentalCooldown extends BukkitRunnable {
    	String playername;
        public RemoveRentalCooldown(String plynam) {
        	playername = plynam;
        }
        public void run() {
            try {
            	rental_cooldown.remove(playername);
            } catch (Exception e) {
            	//do nothing on exception
            }
        }
    }
	
    class RemoveHireCooldown extends BukkitRunnable {
    	String playername;
        public RemoveHireCooldown(String plynam) {
        	playername = plynam;
        }
        public void run() {
            try {
            	hire_cooldown.remove(playername);
    			plugin.hire_cooldowns.remove(playername);
            } catch (Exception e) {
            	//do nothing
            }
        }
    }
    
    public void Hire(String shopname, Player player){
		if (hyperAPI.getPlayerShopList().contains(shopname)){
			if (hyperAPI.getPlayerShop(shopname).getOwner().getName().equals(player.getName())) {
		    	if (!this.npc.isSpawned()){
		    		this.npc.spawn(this.npc.getStoredLocation());
		    	}
		    	merchmeth.SetShop(this.npc.getId(), shopname);
		    	this.hired = true;
		    	this.rented = false;
		    	this.forhire = false;
		    	this.rental = false;
		    	this.offduty = false;
		    	this.location = utils.LocToString(this.npc.getEntity().getLocation());
		        hire_cooldown.remove(player.getName());
		        plugin.hire_cooldowns.remove(player.getName());
		        player.sendMessage(ChatColor.YELLOW+this.npc.getName()+" Now works in your shop, "+shopname);
				merchmeth.Teleport(this.npc.getId(), hyperAPI.getPlayerShop(shopname).getLocation1());
		    }
		}
    }
    
    
	@EventHandler
	public void onRightClick(net.citizensnpcs.api.event.NPCRightClickEvent event) {
		if(this.npc!=event.getNPC()) return;
		
		Player player = event.getClicker();
		boolean shift_click = player.isSneaking();
		
		//return if player has clicked on a hypermerchant npc in the last second.
		if (plugin.customer_cooldowns.contains(player.getName())){
			event.setCancelled(true);
			return;
		}
		//add player to 3 second cooldown list to prevent spam clicking merchants.
		if (!this.rental){
			plugin.customer_cooldowns.add(player.getName());
			new RemoveCustomerCooldown(player.getName()).runTaskLater(this.plugin, 60);
		}
		
		
		// return if player is in creative mode without permission to use merchants in creative.
		if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
		   (!player.hasPermission("creative.hypermerchant"))) {
			
			event.setCancelled(true);
			player.sendMessage(ChatColor.YELLOW+"You may not interact with merchants while in creative mode.");
			return;
    	} 
		
		
		//return if player doesnt have permission to interact with hypermerchant npcs.
		if (!player.hasPermission("hypermerchant.npc")) {
			if (!this.denialMsg.isEmpty()) {
				SpeechContext message = new SpeechContext(this.npc, this.denialMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
			}
			return;
		}
		
		
		HyperPlayer hp = hyperAPI.getHyperPlayer(player.getName());
		
		
		//Check if this merchant is "for hire"
		if (this.forhire) {
			
			//add player to 10 second cooldown list for hire confirmation.
			hire_cooldown.add(player.getName());
			plugin.hire_cooldowns.put(player.getName(), this);
			if (this.comission > 0.0) {
				player.sendMessage(ChatColor.YELLOW+"If you hire "+this.npc.getName()+", you will pay "+this.npc.getName()+" "+this.comission+" percent of all sales made by "+this.npc.getName()+".");
			}
			player.sendMessage(ChatColor.YELLOW+"Within 10 seconds, enter the name of the shop you would like this clerk to work at:");
			
			int x=0;
			for (String shopname : hyperAPI.getPlayerShopList()) { //List shops owned by clicking player
				if (hyperAPI.getPlayerShop(shopname).getOwner().getName().equals(player.getName())){
					player.sendMessage(ChatColor.YELLOW+shopname);
					x=1;
				}
			}
			if (x==0){
				player.sendMessage(ChatColor.YELLOW+"It seems you do not own any shops.");
			}
			
			new RemoveHireCooldown(player.getName()).runTaskLater(this.plugin, 200);
			return;
		}
		
		
		//Check if this merchant is "for rent"
		if (this.rental) {

			//set player ownership of shop this merchant works for 
			//if player has clicked on this hypermerchant npc in the last 8 seconds.
			if (rental_cooldown.contains(player.getName())){
				
				if (!shift_click) {
		            player.sendMessage(ChatColor.YELLOW+"You must hold shift and click "+npc.getName()+" to rent this shop.");
		            return;
				}
				
				if (this.shop_name.equals("null") || this.shop_name.equals("") || this.shop_name == null) {
		            player.sendMessage(ChatColor.YELLOW+"This merchant isn't assigned to a shop.");
		            return;
				}
				
				if (!hyperAPI.getPlayerShopList().contains(shop_name)) {
					player.sendMessage(ChatColor.YELLOW+"This merchant isn't assigned to a player shop.");
		            return;
				}

				
				hyperAPI.getPlayerShop(this.shop_name).setOwner(hyperAPI.getHyperPlayer(player.getName()));
	            rental_cooldown.remove(player.getName());
	            
	            if (rental_price>0){
	            	hyperAPI.getHyperPlayer(player.getName()).withdraw(rental_price);
		            if (plugin.settings.getDEFAULT_RENTAL_OWNER().equals("server")) {
		            	heAPI.depositAccount(rental_price, "hyperconomy");
		            } else {
		            	heAPI.depositAccount(rental_price, plugin.settings.getDEFAULT_RENTAL_OWNER());
		            }
	            }
	            
	            player.sendMessage(ChatColor.YELLOW+"You are now renting the shop named "+this.shop_name+".");
	            this.location = utils.LocToString(this.npc.getEntity().getLocation());
	            this.rental = false;
	            this.rented = true;
	            this.offduty = false;
	            this.forhire = false;
				return;
			}
			
			//add player to 8 second cooldown list for rental or hire confirmation.
			rental_cooldown.add(player.getName());

			if  (!this.rentalMsg.isEmpty()) {
				SpeechContext message = new SpeechContext(this.npc, this.rentalMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
			}
			
			player.sendMessage(ChatColor.YELLOW+"ShiftClick this clerk again within 8 seconds to rent this shop.");
			
			if (this.rental_price > 0.0) {
				player.sendMessage(ChatColor.YELLOW+"It will cost you "+rental_price+" and");
			}
			if (this.comission > 0.0) {
				player.sendMessage(ChatColor.YELLOW+"You will pay "+this.npc.getName()+" "+this.comission+" percent of all sales made by "+this.npc.getName()+".");
			}
			
			
			new RemoveRentalCooldown(player.getName()).runTaskLater(this.plugin, 160);
			return;
		}
		
		
		//return if player has no permission to buy or sell from this shop.
		if (!hp.hasBuyPermission(hyperAPI.getShop(this.shop_name)) && !hp.hasSellPermission(hyperAPI.getShop(this.shop_name))) {
			if (!this.denialMsg.isEmpty()) {
				SpeechContext message = new SpeechContext(this.npc, this.denialMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
			}
			
			return;
			
		}

		//return if this hypermerchant is offduty.
		if (this.offduty) {
			if (!this.closedMsg.isEmpty()) {
				SpeechContext message = new SpeechContext(this.npc, this.closedMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
			}
			
			return;
		}
		
		
		shoplist = hyperAPI.getPlayerShopList();
		shoplist.addAll(hyperAPI.getServerShopList());
		if (shoplist.contains(this.shop_name)) {
			if  (!this.welcomeMsg.isEmpty()) {
				SpeechContext message = new SpeechContext(this.npc, this.welcomeMsg, player);
				new SimpleSpeechController(this.npc).speak(message);
			}
			
			this.customer_menus.put(player.getName(), new ShopMenu(this.shop_name, 54, plugin, player, player, this.npc));
			return;
		
		//if this npc isnt asigned to a shop that exists, tell player closed message and return.
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
	

	@Override
	public void save(DataKey key) {
		key.setString("shop_name", this.shop_name);
		key.setString("farewell.default", this.farewellMsg);
		key.setString("denial.default", this.denialMsg);
		key.setString("welcome.default", this.welcomeMsg);
		key.setString("closed.default", this.closedMsg);
		key.setString("forHireMsg.default", this.forHireMsg);
		key.setString("rentalMsg.default", this.rentalMsg);
		key.setDouble("comission.default", this.comission);
		key.setDouble("rental_price.default", this.rental_price);
		key.setBoolean("offduty.default", this.offduty);
		key.setBoolean("forhire.default", this.forhire);
		key.setBoolean("rental.default", this.rental);
		key.setBoolean("hired.default", this.hired);
		key.setBoolean("rented.default", this.rented);
		key.setString("location.default", this.location);
		
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