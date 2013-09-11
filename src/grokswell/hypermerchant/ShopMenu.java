package grokswell.hypermerchant;

import java.util.ArrayList;
import java.util.Arrays;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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

import regalowl.hyperconomy.Calculation;
import regalowl.hyperconomy.DataHandler;
import regalowl.hyperconomy.EnchantmentClass;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperObject;
import regalowl.hyperconomy.LanguageFile;
import regalowl.hyperconomy.ShopFactory;

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
	NPC npc;
	ArrayList<ArrayList<String>> pages;
	HyperAPI hyperAPI = new HyperAPI();
    
    
	HyperConomy hc = HyperConomy.hc;
	ShopFactory hc_factory = hc.getShopFactory();
	DataHandler hc_functions = hc.getDataFunctions();
	Calculation hc_calc = hc.getCalculation();
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
    	this.shop_trans = new ShopTransactions(player, this.name, this.plugin);
    	this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.inventory_name = this.name+"<>"+player.getName();
        this.inventory = Bukkit.createInventory(player, size, this.inventory_name);
        
		ShopStock shopstock = new ShopStock(sender, this.player, this.name, this.plugin);
        shopstock.SortStock(2);
		this.item_count=shopstock.items_count;
        pages = shopstock.pages;
        double maxpages = this.item_count/45;
        this.last_page = (int) maxpages;
		this.loadPage();
		this.open(this.player);
		
		
    }
   
    public ShopMenu setOption(int position, ItemStack icon, String name, String... info) {
    	this.optionNames[position] = name;
		try {
			this.optionIcons[position] = setItemNameAndLore(icon, name, info);
		}
		catch (Exception e){
			this.optionIcons[position] = setItemNameAndLore(new ItemStack(Material.getMaterial(1), 1), name, info);
			
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
	    .setOption(47, new ItemStack(Material.PAPER, 1), "Left-Click", "Purchase 1 item")
	    .setOption(48, new ItemStack(Material.PAPER, 1), "Shift+Left-Click", "Purchase 8 items")
	    .setOption(49, new ItemStack(Material.PAPER, 1), "Shift+Right-Click", "Purchase 1 Stack")
	    .setOption(50, new ItemStack(Material.PAPER, 1), "To Sell:", "Place items in shop inventory")
	    .setOption(51, new ItemStack(Material.PAPER, 1), "Enchantments:","Have target item in hand");
    	int count = 0;
		ArrayList<String> page=(ArrayList<String>) pages.get(this.page_number);
		
		for (String item : page) {
	        Double cost = 0.0;
	        double stock = 0;
			HyperObject ho = hc_functions.getHyperObject(item, "default");
	        if (hc_functions.itemTest(item)) {
				cost = ho.getCost(1);
				double taxpaid = ho.getPurchaseTax(cost);
				cost = hc_calc.twoDecimals(cost + taxpaid);
				stock = hc_functions.getHyperObject(item, "default").getStock();
			} else if (hc_functions.enchantTest(item)) {
				cost = ho.getCost(EnchantmentClass.DIAMOND);
				cost = cost + ho.getPurchaseTax(cost);
				stock = hc_functions.getHyperObject(item, "default").getStock();
			}
			ItemStack stack;
			if (item.equals("xp")) {
				stack = new ItemStack(Material.STONE, 1);
			}
			else {
				stack = new ItemStack(Material.getMaterial(ho.getId()), 1, (short) ho.getData());
			}
			this.setOption(count, stack, item, "Stock: "+stock+"  Price: "+cost);
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
    
    public void open(Player player) {
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
    
    //@EventHandler(priority=EventPriority.HIGHEST)
    //void onInventoryCreative(InventoryClickEvent event) {
    //	if (player.getGameMode().compareTo(GameMode.CREATIVE) == 0) {
    //		onInventoryClickOrCreative(event);
    //	} else {
    //		event.setCancelled(true);
    //	}
    //}
    
    void onInventoryClickOrCreative(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(this.inventory_name)) {
    		int slot_num = event.getRawSlot();
            if (slot_num < size) {
            	event.setCancelled(true);
            }
            ItemStack item_in_hand = event.getCursor();
            if (slot_num < size-9 && slot_num >= 0 && (item_in_hand.getTypeId() == 0)) {
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
        	else if (slot_num < size && slot_num >= 0 && (item_in_hand.getTypeId() > 0)){
        		if (this.shop_trans.Sell(item_in_hand)) {
        			this.inventory_view.setCursor(new ItemStack(Material.getMaterial(0)));
        			//return;
        		} else if (item_in_hand.getDurability() < item_in_hand.getType().getMaxDurability()){
        			player.sendMessage(ChatColor.YELLOW+"This shop will not purchase a damaged "+
							Material.getMaterial(item_in_hand.getTypeId()).name().toLowerCase()+".");
        			player.getInventory().addItem(item_in_hand);
        			this.inventory_view.setCursor(new ItemStack(Material.getMaterial(0)));
        			//return;
        		} else {
        			player.sendMessage(ChatColor.YELLOW+"This shop does not deal in "+
							Material.getMaterial(item_in_hand.getTypeId()).name().toLowerCase()+".");
        			player.getInventory().addItem(item_in_hand);
        			this.inventory_view.setCursor(new ItemStack(Material.getMaterial(0)));
        			//return;
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
   
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
}