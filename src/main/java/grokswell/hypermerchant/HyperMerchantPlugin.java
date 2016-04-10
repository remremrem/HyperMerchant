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
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.api.HEconomyProvider;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.bukkit.BukkitConnector;
import regalowl.hyperconomy.shop.HyperShopManager;
import regalowl.hyperconomy.shop.PlayerShop;

import grokswell.hypermerchant.HyperMerchantTrait;
import grokswell.hypermerchant.ShopMenu;
import grokswell.util.ActiveEnchant;
import grokswell.util.Blacklist;
import grokswell.util.Language;
import grokswell.util.MenuButtonData;
import grokswell.util.Players;
import grokswell.util.Settings;
import grokswell.util.Uniquifier;
import grokswell.util.Utils;

public class HyperMerchantPlugin extends JavaPlugin implements Listener {
	HyperAPI hyperAPI = null;
	HEconomyProvider ecoAPI;
	HyperConomy hc;
	BukkitConnector bukCon;
	Uniquifier uniquifier = new Uniquifier();
	public Settings settings;
	public Language language;
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
	
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		
		//REMOTEMENU 
		if (cmd.getName().equalsIgnoreCase("remotemenu")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(language.G_PLAYER_COMMAND_ONLY +ChatColor.RED+" /remotemenu");
				return true;
			}
			
			Player player = (Player) sender;
			//out.println("PLAYER: "+player);
			
			if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
				(!player.hasPermission("creative.hypermerchant"))) {
					player.sendMessage(ChatColor.YELLOW+language.G_NO_CREATIVE);
					return true;
			} 
			
			HyperShopManager shopManager = hc.getHyperShopManager();
			ArrayList<String> shoplist = shopManager.listShops();
			
			if (args.length != 1) {
				sender.sendMessage(ChatColor.YELLOW+language.G_MISSING_SHOP_NAME+
									ChatColor.RED+" /remotemenu DonutShop");
				sender.sendMessage(ChatColor.YELLOW+"Use "+ChatColor.RED+"/remoteshoplist or "+
									ChatColor.RED+"/rslist "+ChatColor.YELLOW+"for valid shop names.");
				return true;
				
			} else {
				if (shoplist.contains(args[0])) {
					new ShopMenu(args[0], 54, this, sender, player, null);
					return true;
					
				} else {
					sender.sendMessage(ChatColor.YELLOW+language.G_INVALID_SHOP_NAME+" Use "+
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
				sender.sendMessage(ChatColor.YELLOW+language.G_PLAYER_COMMAND_ONLY+
						ChatColor.RED+" /shopmenu");
				return true;
			}
			
			Player player = (Player) sender;
			
			if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
					(!player.hasPermission("creative.hypermerchant"))) {
						player.sendMessage(ChatColor.YELLOW+language.G_NO_CREATIVE);
						return true;
				} 

			String name=hyperAPI.getPlayerShop(hyperAPI.getHyperPlayer(player.getName()));
			if (name.isEmpty()) {
				sender.sendMessage(ChatColor.YELLOW+language.G_MUST_BE_IN_SHOP+ChatColor.RED+" /shopmenu.");
				return true;
				
			} else {
				new ShopMenu(name, 54, this, sender, player, null);
				return true;
			}
		}
		
		
			// MANAGEMENU 
				else if (cmd.getName().equalsIgnoreCase("managemenu")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.YELLOW+language.G_PLAYER_COMMAND_ONLY+
								ChatColor.RED+" /managemenu");
						return true;
					}
					
					Player player = (Player) sender;
					
					if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
							(!player.hasPermission("creative.hypermerchant"))) {
								player.sendMessage(ChatColor.YELLOW+language.G_NO_CREATIVE);
								return true;
						} 
					
					String name=hyperAPI.getPlayerShop(hyperAPI.getHyperPlayer(player.getName()));
					
					if (name.isEmpty()) {
						sender.sendMessage(ChatColor.YELLOW+language.G_MUST_BE_IN_SHOP+
								ChatColor.RED+" /managemenu.");
						return true;
						
					} else {
						PlayerShop ps = hyperAPI.getPlayerShop(name);
						if (ps == null) {
							sender.sendMessage(ChatColor.YELLOW+language.G_MUST_BE_IN_PLAYER_SHOP+
									ChatColor.RED+" /managemenu");
							return true;
						}
						if (ps.isAllowed(hyperAPI.getHyperPlayer(player.getName()))) {
							new ManageMenu(name, 54, this, sender, player, null);
							return true;
						}
						else {
							sender.sendMessage(ChatColor.YELLOW+language.G_NO_MANAGE+" "+name);
							return true;
						}
					}
				}
		
		
		// RMANAGE 
			else if (cmd.getName().equalsIgnoreCase("rmanage")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.YELLOW+language.G_PLAYER_COMMAND_ONLY+
							ChatColor.RED+"/rmanage");
					return true;
				}
				
				Player player = (Player) sender;
				
				if ((player.getGameMode().compareTo(GameMode.CREATIVE) == 0) && 
						(!player.hasPermission("creative.hypermerchant"))) {
							player.sendMessage(ChatColor.YELLOW+language.G_NO_CREATIVE);
							return true;
					} 
				
				String name="";
				if (args.length>0) {
					name=args[0];
				}
				if (name.isEmpty()) {
					sender.sendMessage(ChatColor.YELLOW+language.G_MISSING_SHOP_NAME_2+ChatColor.RED+" /rmanage.");
					return true;
					
				} else {
					PlayerShop pshop = hyperAPI.getPlayerShop(name);
					if (pshop == null) {
						sender.sendMessage(ChatColor.RED+ name +ChatColor.YELLOW+
								language.G_INVALID_PLAYER_SHOP_NAME+" Use the command "+
								ChatColor.RED+"/ms list"+ChatColor.YELLOW+" to see valid names.");
						return true;
					}
					if (pshop.isAllowed(hyperAPI.getHyperPlayer(player.getName()))) {
						new ManageMenu(name, 54, this, sender, player, null);
						return true;
					}
					else {
						sender.sendMessage(ChatColor.YELLOW+language.G_NO_MANAGE+" "+name);
						return true;
					}
				}
			}
		
		
		//REMOTESHOPLIST
		else if (cmd.getName().equalsIgnoreCase("remoteshoplist")) {
			sender.sendMessage(Utils.formatText(language.G_VALID_SHOP_NAMES, null));
			String shopList = "";
			for (String shop:hyperAPI.getPlayerShopList()) {
				//out.println("ps name: "+shop);
				shopList += shop + ",";
			}
			for (String shop:hyperAPI.getServerShopList()) {
				//out.println("ss name: "+shop);
				shopList += shop + ",";
			}

			if (shopList.length() == 0) {
				sender.sendMessage(Utils.formatText(language.G_NO_SHOPS_EXIST, null));
			} else {
				shopList = shopList.substring(0, shopList.length() - 1);
				sender.sendMessage("§e"+shopList);
			}
			return true;
		}
		
		
		//ONDUTY
		else if (cmd.getName().equalsIgnoreCase("onduty")) {
			if (!(sender instanceof Player)) {
				HashMap<String, String> keywords = new HashMap<String, String>();
				keywords.put("<command>",  "/remotemenu");
				sender.sendMessage(Utils.formatText(language.G_PLAYER_COMMAND_ONLY, keywords));
				return true;
			}
			
			String playerName = ((Player) sender).getName();
			Boolean onduty = false;
			onduty = playerData.getPlayerData().getBoolean(playerName+".onduty");
			
			if (onduty){
				playerData.savePlayerData(playerName+".onduty", false);
				sender.sendMessage(Utils.formatText(language.G_PLAYER_OFFDUTY, null));
			} else {
				playerData.savePlayerData(playerName+".onduty", true);
				sender.sendMessage(Utils.formatText(language.G_PLAYER_ONDUTY, null));
			}
			
			return true;
		}

		
		//HYPERMERCHANT
		else if (cmd.getName().equalsIgnoreCase("hmerchant")) {
			if (!this.citizens_is_loaded) {
				sender.sendMessage(Utils.formatText(language.G_CITIZENS_NOT_LOADED, null));
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
				sender.sendMessage(Utils.formatText(language.G_CITIZENS_NOT_LOADED, null));
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
				sender.sendMessage(Utils.formatText(language.G_CITIZENS_NOT_LOADED, null));
				return true;
			} else if (args.length > 0) {
				return false;	
			} else {
				String message = merchmeth.FireClerk((Player) sender);
				sender.sendMessage(message);
				return true;
			}
			
		}
		
		
		//CLOSESHOP
		else if (cmd.getName().equalsIgnoreCase("closeshop")) {
			if (!this.citizens_is_loaded) {
				sender.sendMessage(Utils.formatText(language.G_CITIZENS_NOT_LOADED, null));
				return true;
			}
			if (args.length == 1) {
				if (args[0].equals("confirm")) {
					String message = merchmeth.CloseShop((Player) sender);
					sender.sendMessage(message);
				}

				return true;
			} else {				
				sender.sendMessage(Utils.formatText(language.G_CLOSE_SHOP_REMINDER, null));
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

	@SuppressWarnings("static-access")
	@Override
	public void onEnable() {
		Plugin hcPlugin = getServer().getPluginManager().getPlugin("HyperConomy");
		bukCon = (BukkitConnector)hcPlugin;
		hc = bukCon.getHC();
		hyperAPI = (HyperAPI) hc.getAPI();
		ecoAPI = hc.getEconomyAPI();
		
		getServer().getPluginManager().registerEvents(this, this);

		settings = new Settings(this);
		language = new Language(this);
		playerData = new Players(this);
		blacklist = new Blacklist(this);
		this.name_blacklist = blacklist.getNameBlacklist();
		this.type_blacklist = blacklist.getTypesBlacklist();
		
		Plugin p = Bukkit.getPluginManager().getPlugin("Citizens");
		CitizensPlugin cp = null;
		try {
			cp = (CitizensPlugin) p;
		} catch (NoClassDefFoundError e) {
			cp = null;
		}
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
			this.getLogger().info(language.G_CITIZENS_NOT_FOUND);
		}
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
		
		if (!event.getRightClicked().getType().equals(EntityType.PLAYER) ||
				(hyperAPI.getPlayerShopList() == null) ||
				hyperAPI.getPlayerShopList().isEmpty()) return;
		
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
			
			player.sendMessage(ChatColor.YELLOW+language.G_NO_CREATIVE);
			return;
    	} 

		//if shop owners are required to be in their shops to trade..
		if (settings.getONDUTY_IN_SHOP_ONLY()) {
			//.. return, if the shop owner is not in shop
			if (hyperAPI.getPlayerShop(hyperAPI.getHyperPlayer(owner.getName())) != shopname) return;
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
