package grokswell.hypermerchant;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import regalowl.hyperconomy.Calculation;
import regalowl.hyperconomy.DataHandler;
import regalowl.hyperconomy.EnchantmentClass;
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
    private Inventory inventory;
    private InventoryView inventory_view;
    private String[] optionNames;
    private ItemStack[] optionIcons;
	private ShopTransactions shop_trans;
	ArrayList<ArrayList<String>> pages;
    
    
	HyperConomy hc = HyperConomy.hc;
	ShopFactory hc_factory = hc.getShopFactory();
	DataHandler hc_functions = hc.getDataFunctions();
	Calculation hc_calc = hc.getCalculation();
	LanguageFile hc_lang = hc.getLanguageFile();
   
    public ShopMenu(String name, int size, HyperMerchantPlugin plgn, ArrayList<ArrayList<String>> pgs, Player plyr,int itemcount) {
    	this.name = name;
        this.size = size;
        plugin = plgn;
        this.optionNames = new String[size];
        this.page_number=0;
        this.item_count=itemcount;
        this.optionIcons = new ItemStack[size];
        this.player=plyr;
        pages = pgs;
    	shop_trans = new ShopTransactions(player, name, plugin);
        double maxpages = this.item_count/45;
        this.last_page = (int) maxpages;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        inventory = Bukkit.createInventory(player, size, name);

		this.loadPage();
		this.open(this.player);
    }
   
    public ShopMenu setOption(int position, ItemStack icon, String name, String... info) {
        optionNames[position] = name;
		try {
	        optionIcons[position] = setItemNameAndLore(icon, name, info);
		}
		catch (Exception e){
	        optionIcons[position] = setItemNameAndLore(new ItemStack(Material.getMaterial(1), 1), name, info);
			
		}
        return this;
    }

    public void loadPage() {
    	optionIcons = null;
    	this.optionIcons = new ItemStack[size];
    	this.setOption(46, new ItemStack(Material.getMaterial(9), 1), "Back 1", "Go back to the previous page.")
	    .setOption(45, new ItemStack(Material.getMaterial(10), 1), "First Page", "Go to the first page.")
	    .setOption(52, new ItemStack(Material.getMaterial(9), 1), "Forward 1", "Go to the next page.")
	    .setOption(53, new ItemStack(Material.getMaterial(10), 1), "Last page", "Go to the last page.")
	    .setOption(48, new ItemStack(Material.getMaterial(339), 1), "Left-Click to purchase 1 item", "Shift+Left-Click: purchase 8 items")
	    .setOption(50, new ItemStack(Material.getMaterial(339), 1), "Shift+Right-Click: purchase a stack", "Sell: place item in store inventory");
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
				stack = new ItemStack(Material.getMaterial(1), 1);
			}
			else {
				stack = new ItemStack(Material.getMaterial(ho.getId()), 1, (short) ho.getData());
			}
			this.setOption(count, stack, item, "Stock: "+stock+"  Price: "+cost);
	        count++;
		}
    }
    
    public void nextPage() {
    	if (this.page_number < this.last_page) {
    		this.page_number++;
    		inventory.clear();
    		this.loadPage();
        	this.menuRefresh();
    	}
    }
    
    public void previousPage() {
    	if (this.page_number > 0) {
    		this.page_number--;
            inventory.clear();
    		this.loadPage();
        	this.menuRefresh();
    	}
    }
    
    public void lastPage() {
		this.page_number = this.last_page;
        inventory.clear();
		this.loadPage();
    	this.menuRefresh();
    }    
    
    public void firstPage() {
		this.page_number = 0;
        inventory.clear();
		this.loadPage();
    	this.menuRefresh();
    }

    
    public void menuRefresh() {
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
    }
    
    public void open(Player player) {
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        inventory_view=player.openInventory(inventory);
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(name)) {
    		int slot_num = event.getRawSlot();
            if (slot_num < size) {
            	event.setCancelled(true);
            }
            ItemStack item_in_hand = event.getCursor();
            if (slot_num < size-9 && slot_num >= 0 && (item_in_hand.getTypeId() == 0)) {
        		if (optionNames[slot_num] != null) {
                    if (event.isLeftClick()){
                    	if (event.isShiftClick()){
                    		shop_trans.Buy(optionNames[slot_num], 8);
                    	}
                    	else {
                    		shop_trans.Buy(optionNames[slot_num], 1);
                    	}
                    }
                    else if (event.isRightClick() && event.isShiftClick()) {
                    	shop_trans.Buy(optionNames[slot_num], 16);
                    }        
        		}
            }
        	else if (slot_num < size && slot_num >= 0 && (item_in_hand.getTypeId() > 0)){
        		player.getInventory().addItem(item_in_hand);
        		inventory_view.setCursor(new ItemStack(Material.getMaterial(0)));
        		shop_trans.Sell(item_in_hand);
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
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClose(InventoryCloseEvent event) {
    	this.destroy();
    }
    
    public void destroy() {
        HandlerList.unregisterAll(this);
        plugin = null;
        optionNames = null;
        optionIcons = null;
    }
   
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
}