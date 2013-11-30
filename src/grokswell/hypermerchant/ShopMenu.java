package grokswell.hypermerchant;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import regalowl.hyperconomy.EconomyManager;
import regalowl.hyperconomy.EnchantmentClass;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.HyperEnchant;
import regalowl.hyperconomy.HyperItem;
import regalowl.hyperconomy.HyperObjectAPI;
import regalowl.hyperconomy.HyperPlayer;
import regalowl.hyperconomy.LanguageFile;

import grokswell.hypermerchant.ShopTransactions;
 
public class ShopMenu implements Listener {
 
    private String name; //name of the shop
    private int size;
    int page_number; //the current page the player is viewing
    int item_count; //number of items in this shop
    int last_page; //the last_page number in the menu
    private HyperMerchantPlugin plugin;
    private Player player;
    private String inventory_name;
    private Inventory inventory;
    private InventoryView inventory_view;
    private String[] optionNames;
    private ItemStack[] optionIcons;
	private ShopTransactions shop_trans;
	ShopStock shopstock;
	NPC npc;
	ArrayList<ArrayList<String>> pages;
	HyperAPI hyperAPI = new HyperAPI();
	HyperObjectAPI hoa = new HyperObjectAPI();
    
	HyperConomy hc = HyperConomy.hc;
	EconomyManager ecoMan = hc.getEconomyManager();
	HyperPlayer hp;
    HyperEconomy hEcon;
	LanguageFile hc_lang = hc.getLanguageFile();
	
	
    public ShopMenu(String name, int size, HyperMerchantPlugin plgn,CommandSender sender, Player plyr, NPC npc) {
    	this.name = name;
        this.size = size;
        this.plugin = plgn;
        this.optionNames = new String[size];
        this.page_number=0;
        this.optionIcons = new ItemStack[size];
        this.player=plyr;
        this.npc = npc;
    	this.shop_trans = new ShopTransactions(player, this.name, this.plugin, this);
    	this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.inventory_name = this.name+"<>"+player.getName();
        this.inventory = Bukkit.createInventory(player, size, this.inventory_name);

    	hp = ecoMan.getHyperPlayer(player);
        hEcon = hp.getHyperEconomy();
        
		shopstock = new ShopStock(sender, this.player, this.name, this.plugin);
        shopstock.SortStock(2);
		this.item_count=shopstock.items_count;
        pages = shopstock.pages;
        double maxpages = this.item_count/45;
        this.last_page = (int) maxpages;
		this.loadPage();
		this.openMenu(this.player);
    }
    
    
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
            im.setDisplayName(ChatColor.GOLD+name);
            im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
    
    
    //Set 1 menu option. Gets called for every menu item.
    public ShopMenu setOption(int position, ItemStack icon, String name, String... info) {
    	this.optionNames[position] = name;
		try {
			this.optionIcons[position] = setItemNameAndLore(icon, name, info);
		}
		catch (Exception e){
			this.optionIcons[position] = setItemNameAndLore(new ItemStack(Material.STONE, 1), name, info);
			
		}
        return this;
    }
    
    
    public void loadPage() {
    	this.optionIcons = null;
    	this.optionIcons = new ItemStack[size];
    	this.optionNames = null;
    	this.optionNames = new String[size];
    	this.setOption(46, new ItemStack(Material.STATIONARY_WATER, 1), "Back 1", "Go back to the previous page.")
	    .setOption(45, new ItemStack(Material.STATIONARY_LAVA, 1), "First Page", "Go to the first page.")
	    .setOption(52, new ItemStack(Material.STATIONARY_WATER, 1), "Forward 1", "Go to the next page.")
	    .setOption(53, new ItemStack(Material.STATIONARY_LAVA, 1), "Last page", "Go to the last page.")
	    .setOption(47, new ItemStack(Material.PAPER, 1), "", "Left-Click:", "Purchase 1 item", "", "Shift+Left-Click:", "Purchase 8 items")
	    .setOption(48, new ItemStack(Material.PAPER, 1), "", "Shift+Right-Click:", "Purchase 1 Stack", "", "To Sell Items:", "Drag items to shop inventory")
	    .setOption(49, new ItemStack(Material.PAPER, 1), "To Buy enchantment:", "Be holding target item in your hand")
	    .setOption(50, new ItemStack(Material.PAPER, 1), "To Sell Enchantment:", "Left-click enchanted item", "on matching store enchantment")
	    .setOption(51, new ItemStack(Material.PAPER, 1), "","");
    	int count = 0;
		ArrayList<String> page=(ArrayList<String>) pages.get(this.page_number);
		
		for (String item : page) {
	        // Loop through all items on this page
			double cost = 0.0;
	        double value = 0.0;
	        double stock = 0.0;

	        ItemStack stack;
	        if (hEcon.itemTest(item)) {
				HyperItem ho = hEcon.getHyperItem(item);
				stock = hEcon.getHyperObject(item).getStock();
				stack = new ItemStack(ho.getMaterialEnum(), 1);
				stack.setDurability((short)ho.getDurability());
				value = hoa.getTrueSaleValue(ho, hp, EnchantmentClass.DIAMOND, 1);
				//out.println("getTrueSaleValue: "+ hoa.getTrueSaleValue(ho, hp, EnchantmentClass.DIAMOND, 1));
				cost = hoa.getTruePurchasePrice(ho, EnchantmentClass.DIAMOND, 1);
			} else if (hEcon.enchantTest(item)) {
				HyperEnchant he = hEcon.getHyperEnchant(item);
				cost = hoa.getTruePurchasePrice(he, EnchantmentClass.DIAMOND, 1);
				
				stock = hEcon.getHyperObject(item).getStock();
				value = hoa.getTrueSaleValue(he, hp, EnchantmentClass.DIAMOND, 1);
				value = value-he.getSalesTaxEstimate(value);
				stack = new ItemStack(Material.STONE, 1, (short) 0);
			} else {
				stack = new ItemStack(Material.AIR, 1, (short) 0);
			}
	        
			
			//if (item.equals("xp")) {
			//	stack = new ItemStack(Material.STONE, 1);
			//}

			
			this.setOption(count, stack, item, ChatColor.WHITE+"Price: "+ChatColor.DARK_PURPLE+String.format("%.2f", cost),
					ChatColor.WHITE+"Sell: "+ChatColor.DARK_PURPLE+String.format("%.2f", value), 
					ChatColor.WHITE+"Stock: "+ChatColor.DARK_PURPLE+String.valueOf((int) stock) );
	        count++;
		}
		
		ItemStack stack;
	    while (count < size-9) {
			stack = new ItemStack(Material.CAULDRON, 1);
	    	this.setOption(count, stack, " ", " ");
	    	count++;
	    }
	    return;
    }
    
    
    public void nextPage() {
    	if (this.page_number < this.last_page) {
    		this.page_number++;
    		this.inventory.clear();
    		this.loadPage();
        	this.menuRefresh();
    	}
    }
    
    
    public void previousPage() {
    	if (this.page_number > 0) {
    		this.page_number--;
    		this.inventory.clear();
    		this.loadPage();
        	this.menuRefresh();
    	}
    }
    
    
    public void lastPage() {
		this.page_number = this.last_page;
		this.inventory.clear();
		this.loadPage();
    	this.menuRefresh();
    }    
    
    
    public void firstPage() {
		this.page_number = 0;
		this.inventory.clear();
		this.loadPage();
    	this.menuRefresh();
    }

    
    public void menuRefresh() {
        for (int i = 0; i < this.optionIcons.length; i++) {
            if (this.optionIcons[i] != null) {
            	this.inventory.setItem(i, this.optionIcons[i]);
            }
        }
    }
    
    
    public void openMenu(Player player) {
        for (int i = 0; i < this.optionIcons.length; i++) {
            if (this.optionIcons[i] != null) {
            	this.inventory.setItem(i, this.optionIcons[i]);
            }
        }
        this.inventory_view=player.openInventory(this.inventory);
    }
    
    
    @EventHandler(priority=EventPriority.HIGHEST)
    void onInventoryClick(InventoryClickEvent event) {
    	if (player.getGameMode().compareTo(GameMode.CREATIVE) != 0) {
    		onInventoryClickOrCreative(event);
    	} 
    	else if (player.hasPermission("creative.hypermerchant")){
    		onInventoryClickOrCreative(event);
    	} else {
    		event.setCancelled(true);
    	}
    }
    
    
    void onInventoryClickOrCreative(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(this.inventory_name)) {
    		int slot_num = event.getRawSlot();
            if (slot_num < size) {
            	event.setCancelled(true);
            }
            ItemStack item_in_hand = event.getCursor();
            if (slot_num < size-9 && slot_num >= 0 && (item_in_hand.getType() == Material.AIR)) {
        		if (this.optionNames[slot_num] != null && this.optionNames[slot_num] != " ") {
                    if (event.isLeftClick()){
                    	if (event.isShiftClick()){
                    		this.shop_trans.Buy(this.optionNames[slot_num], 8);
                    	}
                    	else {
                    		this.shop_trans.Buy(this.optionNames[slot_num], 1);
                    	}
                    }
                    else if (event.isRightClick() && event.isShiftClick()) {
                    	this.shop_trans.Buy(this.optionNames[slot_num], this.optionIcons[slot_num].getMaxStackSize());
                    	//return;
                    }        
        		}
            }
        	else if (slot_num < size && slot_num >= 0 && (item_in_hand.getType() != Material.AIR)){
        		if (!(hp.hasSellPermission(ecoMan.getShop(this.name)))) {
        			player.sendMessage("You cannot sell to this shop.");
        		} 
        		else {
	        		HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
	        		for (Enchantment ench : item_in_hand.getEnchantments().keySet()) {
	        			enchants.put(ench.getId(),item_in_hand.getEnchantments().get(ench));
	        		}
	        		
	        		// SELLING ENCHANTS
	        		if (!enchants.isEmpty()) {
	                	String display_name = this.optionIcons[slot_num].getItemMeta().getDisplayName().replace("§6", "");
	                	if (shopstock.items_in_stock.contains(display_name)) {
	                		ItemStack item_holding = player.getItemInHand().clone();
	                		player.setItemInHand(player.getItemOnCursor().clone());
	                		if (this.shop_trans.Sell(display_name)) {
	            				player.setItemOnCursor(player.getItemInHand());
	            				player.setItemInHand(item_holding);
	                		} else {
	                			player.sendMessage(ChatColor.YELLOW+"Your "+player.getItemInHand().getItemMeta().getDisplayName()+ 
	                					" does not possess enchantment "+display_name+".");
	
	                    		player.setItemInHand(item_holding);
	                		}
	                	} else {
	                		player.sendMessage(ChatColor.YELLOW+"This shop doesn't want your enchanted item");
	                	}
	                }
	                
	                // SELLING ITEMS
	                else if (this.shop_trans.Sell(item_in_hand)) {
	        			this.inventory_view.setCursor(new ItemStack(Material.AIR));
	        			
	        		} else if (item_in_hand.getDurability() < item_in_hand.getType().getMaxDurability()){
	        			player.sendMessage(ChatColor.YELLOW+"This shop will not purchase a damaged "+
								item_in_hand.getType().name().toLowerCase()+".");
	        			player.getInventory().addItem(item_in_hand);
	        			this.inventory_view.setCursor(new ItemStack(Material.AIR));
	        			
	        		} else {
	        			player.sendMessage(ChatColor.YELLOW+"This shop does not deal in "+
								item_in_hand.getType().name().toLowerCase()+".");
	        			player.getInventory().addItem(item_in_hand);
	        			this.inventory_view.setCursor(new ItemStack(Material.AIR));
	        		}
        		}
        	}
            else if (slot_num == 46){
	            this.previousPage();
            }
            else if (slot_num == 45){
            	this.firstPage();
            }
            else if (slot_num == 52){
            	this.nextPage();
            }
            else if (slot_num == 53){
            	this.lastPage();
            }
            else if ((event.getRawSlot() >= 54) && (event.getRawSlot() <= 89)){
            	if (event.isShiftClick()) {
            		event.setCancelled(true);
            	}
            }
        }
    }
    
    
    @EventHandler(priority=EventPriority.HIGHEST)
    void onInventoryClose(InventoryCloseEvent event) {
    	if (event.getPlayer().equals(player)) {
	    	if (this.npc != null) {
	    		this.npc.getTrait(HyperMerchantTrait.class).customer_menus.put(player.getName(), null);
	    		this.npc.getTrait(HyperMerchantTrait.class).customer_menus.remove(player.getName());
	    		this.npc.getTrait(HyperMerchantTrait.class).onFarewell(player);
	    	}
	    	this.destroy();
    	}
    }
    
    
    @EventHandler(priority=EventPriority.HIGHEST)
    void onInventoryMoveItem(InventoryMoveItemEvent event) {
    	if (event.getSource().equals(this.inventory)) {
    		event.setCancelled(true);
    	}
    }
    
    
    @EventHandler(priority=EventPriority.HIGHEST)
    void onInventoryDrag(InventoryDragEvent event) {
    	if (event.getInventory().equals(this.inventory)) {
    		event.setCancelled(true);
    	}
    }
    
    
    public void destroy() {
        HandlerList.unregisterAll(this);
        this.plugin = null;
        this.optionNames = null;
        this.optionIcons = null;
        this.inventory = null;
        this.inventory_view = null;
        this.shop_trans = null;
        this.inventory_name = null;
    }
}