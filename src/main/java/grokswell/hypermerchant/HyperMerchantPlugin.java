package grokswell.hypermerchant;

//import static java.lang.System.out;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperShopManager;
import regalowl.hyperconomy.api.HyperEconAPI;
import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.PlayerShop;

import grokswell.hypermerchant.HyperMerchantTrait;
import grokswell.hypermerchant.ShopMenu;
import grokswell.util.ActiveEnchant;
import grokswell.util.Blacklist;
import grokswell.util.MenuButtonData;
import grokswell.util.Players;
import grokswell.util.Settings;
import grokswell.util.Uniquifier;

public class HyperMerchantPlugin extends JavaPlugin implements Listener {
	HyperEconAPI economyAPI = new HyperEconAPI();
	HyperAPI hyperAPI = new HyperAPI();
	Uniquifier uniquifier = new Uniquifier();
	Settings settings;
	Players playerData;
	Blacklist blacklist;
	ArrayList<String> name_blacklist;
	ArrayList<String> type_blacklist;
	MenuButtonData menuButtonData;
	ArrayList<String> customer_cooldowns = new ArrayList<String>();
	HashMap<String,HyperMerchantTrait> hire_cooldowns = new HashMap<String,HyperMerchantTrait>();
	Boolean citizens_is_loaded = false;
	MerchantMethods merchmeth;
	ActiveEnchant active_enchant;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		
		//REMOTEMENU 
		if (cmd.getName().equalsIgnoreCase("remotemenu")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use the command "+ChatColor.RED+"/remotemenu");
				return true;
			}
			
			Player player = (Player) sender;
			//out.println("PLAYER: "+player);
			
			if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
				(!player.hasPermission("creative.hypermerchant"))) {
					player.sendMessage(ChatColor.YELLOW+"You may not interact with shops while in creative mode.");
					return true;
			} 
			
			HyperConomy hc = HyperConomy.hc;
			HyperShopManager shopManager = hc.getHyperShopManager();
			ArrayList<String> shoplist = shopManager.listShops();
			
			if (args.length != 1) {
				sender.sendMessage(ChatColor.YELLOW+"You must specify one shop name. Example: "+
									ChatColor.RED+"/remotemenu DonutShop");
				sender.sendMessage(ChatColor.YELLOW+"Use "+ChatColor.RED+"/remoteshoplist or "+
									ChatColor.RED+"/rslist "+ChatColor.YELLOW+"for valid shop names.");
				return true;
				
			} else {
				if (shoplist.contains(args[0])) {
					new ShopMenu(args[0], 54, this, sender, player, null);
					return true;
					
				} else {
					sender.sendMessage(ChatColor.YELLOW+"Shop name not recognized. Use "+
										ChatColor.RED+"/remoteshoplist "+ChatColor.YELLOW+ 
										"or "+ChatColor.RED+"/rslist "+ChatColor.YELLOW+
										"for valid shop names. Use exact spelling.");
					return true;
				}
			}
		}
		
		
		// SHOPMENU 
		else if (cmd.getName().equalsIgnoreCase("shopmenu")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use the command "+ChatColor.RED+"/shopmenu");
				return true;
			}
			
			Player player = (Player) sender;
			
			if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
					(!player.hasPermission("creative.hypermerchant"))) {
						player.sendMessage(ChatColor.YELLOW+"You may not interact with shops while in creative mode.");
						return true;
				} 
			
			String name=hyperAPI.getPlayerShop(player);
			if (name.isEmpty()) {
				sender.sendMessage(ChatColor.YELLOW+"You must be standing inside " +
									"of a shop to use the command "+ChatColor.RED+"/shopmenu.");
				return true;
				
			} else {
				new ShopMenu(name, 54, this, sender, player, null);
				return true;
			}
		}
		
		
			// MANAGEMENU 
				else if (cmd.getName().equalsIgnoreCase("managemenu")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("Only players can use the command "+ChatColor.RED+"/managemenu");
						return true;
					}
					
					Player player = (Player) sender;
					
					if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
							(!player.hasPermission("creative.hypermerchant"))) {
								player.sendMessage(ChatColor.YELLOW+"You may not interact with shops while in creative mode.");
								return true;
						} 
					
					String name=hyperAPI.getPlayerShop(player);
					
					if (name.isEmpty()) {
						sender.sendMessage(ChatColor.YELLOW+"You must be standing inside " +
											"of a shop to use the command "+ChatColor.RED+"/managemenu.");
						return true;
						
					} else {
						PlayerShop ps = hyperAPI.getPlayerShop(name);
						if (ps == null) {
							sender.sendMessage(ChatColor.YELLOW+"You must be standing in a player shop to use the command /managemenu");
							return true;
						}
						if (ps.isAllowed(hyperAPI.getHyperPlayer(player.getName()))) {
							new ManageMenu(name, 54, this, sender, player, null);
							return true;
						}
						else {
							sender.sendMessage(ChatColor.YELLOW+"You are not allowed to manage the shop known as "+name);
							return true;
						}
					}
				}
		
		
		// RMANAGE 
			else if (cmd.getName().equalsIgnoreCase("rmanage")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("Only players can use the command "+ChatColor.RED+"/rmanage");
					return true;
				}
				
				Player player = (Player) sender;
				
				if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
						(!player.hasPermission("creative.hypermerchant"))) {
							player.sendMessage(ChatColor.YELLOW+"You may not interact with shops while in creative mode.");
							return true;
					} 
				
				String name="";
				if (args.length>0) {
					name=args[0];
				}
				if (name.isEmpty()) {
					sender.sendMessage(ChatColor.YELLOW+"You must specify a shop name, to use the command "+ChatColor.RED+"/rmanage.");
					return true;
					
				} else {
					if (hyperAPI.getPlayerShop(name).isAllowed(hyperAPI.getHyperPlayer(player.getName()))) {
						new ManageMenu(name, 54, this, sender, player, null);
						return true;
					}
					else {
						sender.sendMessage(ChatColor.YELLOW+"You are not allowed to manage the shop known as "+name);
						return true;
					}
				}
			}
		
		
		//REMOTESHOPLIST
		else if (cmd.getName().equalsIgnoreCase("remoteshoplist")) {
			sender.sendMessage(ChatColor.YELLOW+"Valid shop names to use with command /remotemenu:");
			String shopList = "";
			for (String shop:hyperAPI.getPlayerShopList()) {
				shopList += shop + ",";
			}
			for (String shop:hyperAPI.getServerShopList()) {
				shopList += shop + ",";
			}
			shopList = shopList.substring(0, shopList.length() - 1);
			sender.sendMessage(shopList);
			return true;
		}
		
		
		//ONDUTY
		else if (cmd.getName().equalsIgnoreCase("onduty")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use the command "+ChatColor.RED+"/remotemenu");
				return true;
			}
			
			String playerName = ((Player) sender).getName();
			Boolean onduty = false;
			onduty = playerData.getPlayerData().getBoolean(playerName+".onduty");
			
			if (onduty){
				playerData.savePlayerData(playerName+".onduty", false);
				sender.sendMessage(ChatColor.YELLOW+"You are now off duty. Other players cannot click on you to trade with your shop.");
			} else {
				playerData.savePlayerData(playerName+".onduty", true);
				sender.sendMessage(ChatColor.YELLOW+"You are now on duty. Other players may click on you to trade with your shop.");
			}
			
			return true;
		}

		
		//HYPERMERCHANT
		else if (cmd.getName().equalsIgnoreCase("hmerchant")) {
			if (!this.citizens_is_loaded) {
				sender.sendMessage(ChatColor.RED+"Citizens is not loaded. NPCs are unavailable at this time.");
				return true;
			} else if (args.length < 1) {
				return false;	
			} else {
				new HyperMerchantCommand(sender, args, this);
				return true;
			}
		
		}
		
		
		//CLERK
		else if (cmd.getName().equalsIgnoreCase("clerk")) {
			if (!this.citizens_is_loaded) {
				sender.sendMessage(ChatColor.RED+"Citizens is not loaded. NPCs are unavailable at this time.");
				return true;
			} else if (args.length < 1) {
				return false;	
			} else {
				new ClerkCommand(sender, args, this);
				return true;
			}
			
		}
		
		
		//FIRECLERK
		else if (cmd.getName().equalsIgnoreCase("fireclerk")) {
			if (!this.citizens_is_loaded) {
				sender.sendMessage(ChatColor.RED+"Citizens is not loaded. NPCs are unavailable at this time.");
				return true;
			} else if (args.length > 0) {
				return false;	
			} else {
				String message = merchmeth.FireClerk((Player) sender);
				sender.sendMessage(ChatColor.YELLOW+message);
				return true;
			}
			
		}
		
		
		//CLOSESHOP
		else if (cmd.getName().equalsIgnoreCase("closeshop")) {
			if (!this.citizens_is_loaded) {
				sender.sendMessage(ChatColor.RED+"Citizens is not loaded. NPCs are unavailable at this time.");
				return true;
			}
			if (args.length == 1) {
				if (args[0].equals("confirm")) {
					String message = merchmeth.CloseShop((Player) sender);
					sender.sendMessage(ChatColor.YELLOW+message);
				}

				return true;
			} else {				
				sender.sendMessage(ChatColor.YELLOW+"Before you close the shop, make sure you take anything you want to keep. Type \"/closeshop confirm\"");
				return true;
			}
			
			
					
		} else {
			return true;
		}
	}
	
	

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		Plugin p = Bukkit.getPluginManager().getPlugin("Citizens");
		
		CitizensPlugin cp = (CitizensPlugin) p;
		//CitizensAPI.setImplementation(cp);
		if (cp != null) {
			try {
				CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(HyperMerchantTrait.class).withName("hypermerchant"));
				this.citizens_is_loaded = true;
				merchmeth = new MerchantMethods();
			} catch (IllegalArgumentException e) {
				this.citizens_is_loaded = true;
				//out.println("EXCEPTION: "+e);
			}
		} else {
			this.getLogger().info("Citizens not found. NPC hypermerchants will be disabled.");
		}
		
		settings = new Settings(this);
		playerData = new Players(this);
		blacklist = new Blacklist(this);
		this.name_blacklist = blacklist.getNameBlacklist();
		this.type_blacklist = blacklist.getTypesBlacklist();
		//out.println("names: "+this.name_blacklist);
		//out.println("types: "+this.type_blacklist);
		
		//Thanks to Captain Bern for the ActiveEnchant wrapper
		try{
    		try {
	    		Field f = Enchantment.class.getDeclaredField("acceptingNew");
	    		f.setAccessible(true);
	    		f.set(null, true);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		try {
	    		active_enchant = new ActiveEnchant(111); //< this is your custom wrapper (a class that extends an EnchantmentWrapper with the needed stuff and returns, 69 is the id I choosed (lawl)
	    		EnchantmentWrapper.registerEnchantment(active_enchant); //<this is used to register the enchantment.
	    	} catch (IllegalArgumentException e){
    		 
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		
		menuButtonData = new MenuButtonData(this);
	}
	
	class RemoveCustomerCooldown extends BukkitRunnable {
    	String playername;
        public RemoveCustomerCooldown(String plynam) {
        	playername = plynam;
        }
        
        public void run() {
            // What you want to schedule goes here
            customer_cooldowns.remove(playername);
        }
    }
	
	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent event) {
		//out.println("onRightClick playerinteractentityevent");
		
		if(!event.getRightClicked().getType().equals(EntityType.PLAYER)) return;
		Player owner = (Player) event.getRightClicked();
		Player player = event.getPlayer();
		String shopname = "";
		for (String sn : hyperAPI.getPlayerShopList()){
			
			if ( hyperAPI.getPlayerShop(sn).getOwner().getName().equals(owner.getName()) ) {
				shopname = sn;
				break;
			}
		}
		
		//return if the player who was clicked does not own a shop.
		if ("" == shopname) return;

		//return if the customer has already clicked to open the menu in the last few seconds
		if (this.customer_cooldowns.contains(player.getName())) return;
		
		//add this player's name to the cooldown list to prevent them from click-spamming and glitching the menu
		this.customer_cooldowns.add(player.getName());
		new RemoveCustomerCooldown(player.getName()).runTaskLater(this, 60);
		
		//make sure player is not in creative mode without permission for shopping in creative.
		if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
		   (!player.hasPermission("creative.hypermerchant"))) {
			
			player.sendMessage(ChatColor.YELLOW+"You may not interact with merchants while in creative mode.");
			return;
    	} 

		//if shop owners are required to be in their shops to trade..
		if (settings.getONDUTY_IN_SHOP_ONLY()) {
			//.. return, if the shop owner is not in shop
			if (hyperAPI.getPlayerShop(owner) != shopname) return;
		}
		
		PlayerShop shop = hyperAPI.getPlayerShop(shopname);
		HyperPlayer hp = hyperAPI.getHyperPlayer(player.getName());
		
		//return if the player who clicked does not have permission to trade with this shop
		if (!hp.hasBuyPermission(shop)) return;
		
		//return if the shop owner has set themselves to off-duty
		if ( !playerData.getPlayerData().getBoolean(owner.getName()+".onduty") ) return;
        
		//if nothing has returned to this point, open a shopmenu for the player who clicked.
		new ShopMenu(shopname, 54, this, player, player, null);

		return;

	}
	

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (this.hire_cooldowns.containsKey(player.getName())) {
			event.setCancelled(true);
			String shopname = event.getMessage();
			this.hire_cooldowns.get(player.getName()).Hire(shopname, player);
		}
	}
}
