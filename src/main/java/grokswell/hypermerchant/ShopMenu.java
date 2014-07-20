package grokswell.hypermerchant;

//import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
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

import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.account.HyperPlayer;
import grokswell.hypermerchant.ShopTransactions;
import grokswell.util.EnchantIcons;
 
public class ShopMenu implements Listener, MerchantMenu {
 
    private String shopname; //name of the shop
    private int size;
    int page_number; //the current page the player is viewing
    int item_count; //number of items in this shop
    int last_page; //the last_page number in the menu
    int sort_by; //sort-by 0=item name, 1=item type, 2=item price, 3=item quantity
    int display_zero_stock; //toggle displaying items with zero stock
    private HyperMerchantPlugin plugin;
    private Player player;
    private String inventory_name;
    private Inventory inventory;
    private InventoryView inventory_view;
    private String[] optionNames;
    private ItemStack[] optionIcons;
	ShopTransactions shop_trans;
	private ItemStack sorting_icon;
	String economy_name;
	
	public ShopStock shopstock;
	
	NPC npc;
	double commission;
	HyperAPI hyperAPI = new HyperAPI();

	HyperPlayer hp;
	
	
    public ShopMenu(String name, int size, HyperMerchantPlugin plgn,CommandSender sender, Player plyr, NPC npc) {
    	this.sort_by=0;
    	this.display_zero_stock=1;
    	this.shopname = name;
        this.size = size;
        this.plugin = plgn;
        this.optionNames = new String[size];
        this.page_number=0;
        this.optionIcons = new ItemStack[size];
        this.player=plyr;
        this.npc = npc;
        if (this.npc!=null) {
        	this.commission = npc.getTrait(HyperMerchantTrait.class).comission*.01;
        } else {
        	this.commission = 0.0;
        }
    	this.shop_trans = new ShopTransactions(player, this.shopname, this.plugin, this);
    	this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    	
    	//this.sorting_icon = plugin.menuButtonData.help5.clone();
    	UpdateSortingIcon();
    	
        String iname = (this.shopname+"<>"+player.getName());
        if (iname.length()>32) {
        	this.inventory_name = iname.substring(0, 27)+this.plugin.uniquifier.uniquify();
        } else {
        	this.inventory_name = iname;
        }

	    //out.println("inventory name: "+ this.inventory_name);
        this.inventory = Bukkit.createInventory(player, size, this.inventory_name);

    	hp = hyperAPI.getHyperPlayer(player.getName());
    	
        economy_name = hyperAPI.getShop(this.shopname).getEconomy();
        
		this.shopstock = new ShopStock(sender, this.player, this.shopname, this.plugin, "trade");
        //shopstock.SortStock(1);
		this.item_count=shopstock.items_count;
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
    
    //Set 1 menu button. Gets called for every menu button.
    public ShopMenu setOption(int position, ItemStack icon) {
    	this.optionNames[position] = icon.getItemMeta().getDisplayName();
		try {
			this.optionIcons[position] = icon;
		}
		catch (Exception e){	
		}
        return this;
    }
    
    
    public void loadPage() {
    	this.optionIcons = null;
    	this.optionIcons = new ItemStack[size];
    	this.optionNames = null;
    	this.optionNames = new String[size];
    	
    	// Populate interface button inventory slots
    	this.setOption(46, plugin.menuButtonData.back)
	    .setOption(45, plugin.menuButtonData.first_page)
	    .setOption(52, plugin.menuButtonData.forward.clone())
	    .setOption(53, plugin.menuButtonData.last_page.clone())
	    .setOption(47, plugin.menuButtonData.help1)
	    .setOption(48, plugin.menuButtonData.help2.clone())
	    .setOption(49, plugin.menuButtonData.help3.clone())
	    .setOption(50, plugin.menuButtonData.help4.clone())
	    .setOption(51, sorting_icon);
    	int count = 0;
		ArrayList<String> page=(ArrayList<String>) shopstock.pages.get(this.page_number);
		
		//Populate the shop stock slots for current page
		for (String item_name : page) {
	        // Loop through all items on this page
			double cost = 0.0;
	        double value = 0.0;
	        double stock = 0.0;
	        ItemStack stack;
	        
        	HyperObject ho = hyperAPI.getHyperObject(item_name, economy_name, hyperAPI.getShop(shopname));
	        
	        if (ho.getType()==HyperObjectType.ITEM) {
	        	stock = ho.getStock();
				stack = ho.getItemStack();
				
				hp.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
				value = ho.getSellPriceWithTax(1.0, hp);
				cost = ho.getBuyPriceWithTax(1.0);
				
			} else if (ho.getType()==HyperObjectType.ENCHANTMENT) {
				stock = ho.getStock();
				
				hp.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
				value = ho.getSellPriceWithTax(1.0, hp);
				cost = ho.getBuyPriceWithTax(1.0);

				stack = (new EnchantIcons()).getIcon(ho.getDisplayName(), ho.getEnchantmentLevel());

				
			} else if (ho.getType()==HyperObjectType.EXPERIENCE) {
				stock = ho.getStock();
				
				hp.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
				value = ho.getSellPriceWithTax(1.0, hp);
				cost = ho.getBuyPriceWithTax(1.0);

				stack = new ItemStack(Material.POTION, 1, (short) 0);
				
				
			} else {
				stack = new ItemStack(Material.AIR, 1, (short) 0);
			}
	        
	        String status = "";
	        if (ho.getStatus()!=null){
	        	status = ChatColor.WHITE+"Status: "+ChatColor.DARK_PURPLE+ho.getStatus().name().toLowerCase();
	        }
			this.setOption(count, stack, ho.getDisplayName().replaceAll("_", " "), 
					ChatColor.WHITE+"Price: "+ChatColor.DARK_PURPLE+String.format("%.2f", cost),
					ChatColor.WHITE+"Sell: "+ChatColor.DARK_PURPLE+String.format("%.2f", value), 
					ChatColor.WHITE+"Stock: "+ChatColor.DARK_PURPLE+String.valueOf((int) stock),
	    			status );
	        count++;
		}
		
		ItemStack stack;
	    while (count < size-9) {
			stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.SILVER.getData());
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
    
    
    public int itemOnCurrentPage(HyperObject ho) {
		int count = 0;
    	for (String item_name:shopstock.pages.get(this.page_number)){
    		if (item_name.equals(ho.getName())){
    			return count; 
    		}
        	count = count+1;
		}
    	return -1;
    }
    
    private void UpdateSortingIcon() {
    	this.sorting_icon = plugin.menuButtonData.help5.clone();
    	ItemMeta im;
    	List<String> lore;
    	
    	if (sorting_icon.hasItemMeta()){
    		im = sorting_icon.getItemMeta();
    	} else {
    	    im=Bukkit.getItemFactory().getItemMeta(sorting_icon.getType());
    		
    	}
    	
	    if (im.hasLore()){
	    	lore=im.getLore();
	    } else {
	    	lore = new ArrayList<String>();
	    }
	    
    	lore.add(" ");
    	
    	if (sort_by == 0){
    		lore.add(ChatColor.DARK_PURPLE+"sorting: "+ChatColor.RED+"Item Name");
    	} else if (sort_by == 1){
    		lore.add(ChatColor.DARK_PURPLE+"sorting: "+ChatColor.RED+"Material Name");
    	} else if (sort_by == 2){
    		lore.add(ChatColor.DARK_PURPLE+"sorting: "+ChatColor.RED+"Purchase Price");
    	} else if (sort_by == 3){
    		lore.add(ChatColor.DARK_PURPLE+"sorting: "+ChatColor.RED+"Sell Price");
    	} else if (sort_by == 4){
    		lore.add(ChatColor.DARK_PURPLE+"sorting: "+ChatColor.RED+"Stock Amount");
    	}
    	
    	lore.add(" ");
    	
    	if (display_zero_stock == 0){
    		lore.add(ChatColor.DARK_PURPLE+"show zero stock: "+ChatColor.RED+"No");
    	} else {
    		lore.add(ChatColor.DARK_PURPLE+"show zero stock: "+ChatColor.RED+"Yes");
    	}
    	
    	im.setLore(lore);
    	sorting_icon.setItemMeta(im);
    }
    
    public void ToggleZeroStock() {
		if (display_zero_stock==0){
			display_zero_stock=1;
		} else display_zero_stock=0;
		
		UpdateSortingIcon();
		shopstock.Refresh(sort_by, display_zero_stock);
		this.item_count=shopstock.items_count;
        double maxpages = this.item_count/45;
        this.last_page = (int) maxpages;
		firstPage();	
    }
    
    public void Sort() {
		if (sort_by < 4){
			sort_by = sort_by+1;
		} else sort_by = 0;
		
		UpdateSortingIcon();
		shopstock.Refresh(sort_by, display_zero_stock);
		this.item_count=shopstock.items_count;
        double maxpages = this.item_count/45;
        this.last_page = (int) maxpages;
		firstPage();	
    }

    
    public void itemRefresh(int slot, HyperObject ho) {
    	hp.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());

        String status = "";
        if (ho.getStatus()!=null){
        	status = ChatColor.WHITE+"Status: "+ChatColor.DARK_PURPLE+ho.getStatus().name().toLowerCase();
        }
    	this.setOption(slot, ho.getItemStack(), ho.getDisplayName().replaceAll("_", " "), 
    			ChatColor.WHITE+"Price: "+ChatColor.DARK_PURPLE+String.format("%.2f", ho.getBuyPriceWithTax(1.0)),
    			ChatColor.WHITE+"Sell: "+ChatColor.DARK_PURPLE+String.format("%.2f", ho.getSellPriceWithTax(1.0, hp)), 
    			ChatColor.WHITE+"Stock: "+ChatColor.DARK_PURPLE+String.valueOf((int) ho.getStock()),
    			status );
    	this.inventory.setItem(slot, this.optionIcons[slot]);

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
    
    
    public ShopStock getShopStock() {
        return this.shopstock;
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
            
            //IF CLICKING WITH EMPTY CURSOR
            if (slot_num < size-9 && slot_num >= 0 && (item_in_hand.getType() == Material.AIR)) {
        		if (this.optionNames[slot_num] != null && this.optionNames[slot_num] != " ") {
                    if (event.isLeftClick()){
                    	if (event.isShiftClick()){
                    		HyperObject ho2 = this.shop_trans.Buy(this.optionNames[slot_num], 8, commission);
	                        if (ho2 != null) {
	                        	this.itemRefresh(slot_num, ho2);
	                        }
                    	}
                    	else {
                    		HyperObject ho2 = this.shop_trans.Buy(this.optionNames[slot_num], 1, commission);
	                        if (ho2 != null) {
	                        	this.itemRefresh(slot_num, ho2);
	                        }
                    	}
                    }
                    else if (event.isRightClick() && event.isShiftClick()) {
                		HyperObject ho2 = this.shop_trans.Buy(this.optionNames[slot_num], this.optionIcons[slot_num].getMaxStackSize(), commission);
	        			int slot = this.itemOnCurrentPage(ho2);
                		if (ho2 != null) {
                        	this.itemRefresh(slot_num, ho2);
                        }
                    }        
        		}
            }
            
            // IF CLICKING WITH ITEM IN CURSOR
        	else if (slot_num < size && slot_num >= 0 && (item_in_hand.getType() != Material.AIR)){
        		if (!(hp.hasSellPermission(hyperAPI.getShop(this.shopname)))) {
        			player.sendMessage("You cannot sell to this shop.");
        		}
        		else {
        			ArrayList<String> enchants = new ArrayList<String>();
	        		
	        		//SELLING ENCHANTED BOOK
	        		if (item_in_hand.getType()==Material.ENCHANTED_BOOK) {
	        			
	        			//make list of hyperconomy enchantment names from book enchants
	        			for (HyperObject hob : hyperAPI.getEnchantmentHyperObjects(item_in_hand, player.getName())) {
	        				enchants.add(hob.getDisplayName());
	        			}
	        			
	        			if (!enchants.isEmpty()) {
	        				for (String e : enchants) {
	        					if (shopstock.display_names.contains(e.toLowerCase())) {
	    	                		ItemStack item_holding = player.getItemInHand().clone();
	    	                		player.setItemInHand(player.getItemOnCursor().clone());

	    	    	        		HyperObject he = this.shop_trans.Sell(e);
	    	    	                if (he != null) {
	    	            				player.setItemOnCursor(player.getItemInHand());
	    	            				player.setItemInHand(item_holding);	
	    			        			int slot = this.itemOnCurrentPage(he);
	    			        			if (slot > -1) {
	    			        				this.itemRefresh(slot, he);
	    			        			}
	    			        			
	    	                		} else {
	    	                    		player.setItemInHand(item_holding);
	    	                		}

	    	                	} else {
	    	                		player.sendMessage(ChatColor.YELLOW+"This shop doesn't want the enchantment: "+e);
	    	                	}
	        				}
	        				return;
	        			}

	        		//MAKE LISTS OF ITEM ON CURSOR ENCHANMENTS	
	        		} else {
	        			//make list of hyperconomy enchantment names from book enchants
	        			for (HyperObject hob : hyperAPI.getEnchantmentHyperObjects(item_in_hand, player.getName())) {
	        				enchants.add(hob.getDisplayName());
	        			}
	        		}
	        		
	        		// SELLING ENCHANTS
	        		if (!enchants.isEmpty()) {
	                	String display_name = this.optionIcons[slot_num].getItemMeta().getDisplayName().replace("§6", "");
            			String enchant_name = display_name.replace(" ", "_");
	                	if (enchants.contains(enchant_name)) {
	                		
	                		ItemStack item_holding = player.getItemInHand().clone();
	                		player.setItemInHand(player.getItemOnCursor().clone());
	                		
	    	        		HyperObject he = this.shop_trans.Sell(enchant_name);
	    	                if (he != null) {
	            				player.setItemOnCursor(player.getItemInHand());
	            				player.setItemInHand(item_holding);
			        			int slot = this.itemOnCurrentPage(he);
			        			if (slot > -1) {
			        				this.itemRefresh(slot, he);
			        			}
	            				return;
	                		} else {
	
	                    		player.setItemInHand(item_holding);
	                    		return;
	                		}
	                		
	                	// TRY SELLING ENCHANTED ITEM
	                	} 

    	        		HyperObject ho = this.shop_trans.Sell(item_in_hand);
    	                if (ho != null) {
	    	        		this.inventory_view.setCursor(new ItemStack(Material.AIR));
		        			int slot = this.itemOnCurrentPage(ho);
		        			if (slot > -1) {
		        				this.itemRefresh(slot, ho);
		        			}
		        			return;
	    	        			
	                	} else {
	                		player.sendMessage(ChatColor.YELLOW+"This shop doesn't want your enchanted item");
	                		return;
	                	}
	                }
	                
	                // SELLING ITEMS
	        		HyperObject ho2 = this.shop_trans.Sell(item_in_hand);
	                if (ho2 != null) {
	        			this.inventory_view.setCursor(new ItemStack(Material.AIR));
	        			int slot = this.itemOnCurrentPage(ho2);
	        			if (slot > -1) {
	        				this.itemRefresh(slot, ho2);
	        			}
	        			return;
	        			
	        		//THE SHOP WONT BUY IT	
	        		} else {
	        			player.sendMessage(ChatColor.YELLOW+"This shop does not deal in "+
								item_in_hand.getType().name().toLowerCase()+".");
	        			player.getInventory().addItem(item_in_hand);
	        			this.inventory_view.setCursor(new ItemStack(Material.AIR));
	        			return;
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
            else if (slot_num == 51){
            	if (event.isRightClick()) {
            		this.ToggleZeroStock();
            	} else this.Sort();
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