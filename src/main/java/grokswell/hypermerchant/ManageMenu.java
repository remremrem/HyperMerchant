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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectStatus;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.account.HyperPlayer;
import grokswell.hypermerchant.ShopTransactions;
import grokswell.util.EnchantIcons;
 
public class ManageMenu implements Listener, MerchantMenu {
 
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
	private ShopTransactions shop_trans;
	private ItemStack sorting_icon;
	ArrayList<String> status_list;
	String economy_name;
	
	public ShopStock shopstock;
	
	NPC npc;
	double commission;
	HyperAPI hyperAPI = new HyperAPI();

	HyperPlayer hp;
	
	int edit_slot;
	String edit_value;
	String edit_type;
	boolean edit_in_progress;
	EditCooldown editcooldown;
	
	
    public ManageMenu(String name, int size, HyperMerchantPlugin plgn,CommandSender sender, Player plyr, NPC npc) {
    	edit_value=null;
    	edit_type=null;
    	edit_slot=-1;
    	edit_in_progress = false;
    	status_list = new ArrayList<String>();
    	status_list.add("buy");
    	status_list.add("sell");
    	status_list.add("trade");
    	status_list.add("none");
    	this.editcooldown=null;
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
    	
    	UpdateSortingIcon();
    	
        String iname = (this.shopname+"<>"+player.getName());
        if (iname.length()>32) {
        	this.inventory_name = iname.substring(0, 27)+this.plugin.uniquifier.uniquify();
        } else {
        	this.inventory_name = iname;
        }

        this.inventory = Bukkit.createInventory(player, size, this.inventory_name);

    	hp = hyperAPI.getHyperPlayer(player.getName());
    	
        economy_name = hyperAPI.getShop(this.shopname).getEconomy();
        
		this.shopstock = new ShopStock(sender, this.player, this.shopname, this.plugin, "manage");
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
    public ManageMenu setOption(int position, ItemStack icon, String name, String... info) {
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
    public ManageMenu setOption(int position, ItemStack icon) {
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
	    .setOption(47, plugin.menuButtonData.sell_price)
	    .setOption(48, plugin.menuButtonData.buy_price)
	    .setOption(49, plugin.menuButtonData.status)
	    .setOption(50, plugin.menuButtonData.manage_help_1)
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
				value = ho.getSellPriceWithTax(1, hp);
				cost = ho.getBuyPriceWithTax(1);
				
			} else if (ho.getType()==HyperObjectType.ENCHANTMENT) {
				stock = ho.getStock();
				
				hp.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
				value = ho.getSellPriceWithTax(1, hp);
				cost = ho.getBuyPriceWithTax(1);

				stack = (new EnchantIcons()).getIcon(ho.getDisplayName());

				
			} else if (ho.getType()==HyperObjectType.EXPERIENCE) {
				stock = ho.getStock();
				
				hp.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
				value = ho.getSellPriceWithTax(1, hp);
				cost = ho.getBuyPriceWithTax(1);

				stack = new ItemStack(Material.POTION, 1, (short) 0);
				
				
			} else {
				stack = new ItemStack(Material.AIR, 1, (short) 0);
			}
	        
			this.setOption(count, stack, ho.getDisplayName().replaceAll("_", " "), 
					ChatColor.WHITE+"Sell: "+ChatColor.DARK_PURPLE+String.format("%.2f", cost),
					ChatColor.WHITE+"Buy: "+ChatColor.DARK_PURPLE+String.format("%.2f", value), 
					ChatColor.WHITE+"Stock: "+ChatColor.DARK_PURPLE+String.valueOf((int) stock),
	    			ChatColor.WHITE+"Status: "+ChatColor.DARK_PURPLE+ho.getStatus().name().toLowerCase() );
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
    	this.setOption(slot, ho.getItemStack(), ho.getDisplayName().replaceAll("_", " "), 
    			ChatColor.WHITE+"Sell: "+ChatColor.DARK_PURPLE+String.format("%.2f", ho.getBuyPriceWithTax(1)),
    			ChatColor.WHITE+"Buy: "+ChatColor.DARK_PURPLE+String.format("%.2f", ho.getSellPriceWithTax(1, hp)), 
    			ChatColor.WHITE+"Stock: "+ChatColor.DARK_PURPLE+String.valueOf((int) ho.getStock()),
    			ChatColor.WHITE+"Status: "+ChatColor.DARK_PURPLE+ho.getStatus().name().toLowerCase() );
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

    String changeStatus(int click_type, String status) {
    	int max=status_list.size()-1;
    	int index=status_list.indexOf(status.toLowerCase());
    	if (click_type == 1) {
    		if (index < max) {
    			return status_list.get(index+1);
    		} else return status_list.get(0);
    	} 
    	else {
    		if (index > 0) {
    			return status_list.get(index-1);
    		} else return status_list.get(max);
    	}
    }
    
    void setStatus(int slot_num, int click_type){
    	HyperObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
    							hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
    	String status = ho.getStatus().name();
		HyperObjectStatus newstatus = HyperObjectStatus.fromString(this.changeStatus(click_type, status));
		ho.setStatus(newstatus);
		player.sendMessage(" ");
		player.sendMessage(ChatColor.YELLOW+ho.getDisplayName());
		player.sendMessage(ChatColor.YELLOW+"status: "+newstatus);
		this.itemRefresh(slot_num, ho);
    	return;
    }
    
    void setSellDynamic(int slot_num){
    	HyperObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
    							hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		ho.setSellPrice(0.0);
		this.itemRefresh(slot_num, ho);
    	return;
    }
    
    void setSellPrice(int slot_num, Double price){
    	HyperObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
    							hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		ho.setSellPrice(price);
		this.editcooldown.cancel();
		player.sendMessage(ChatColor.YELLOW+"The buy price for "+ho.getDisplayName()+" is now "+price.toString());
		this.inventory_view=this.player.openInventory(this.inventory);
		this.itemRefresh(slot_num, ho);
		player.setItemOnCursor(this.inventory.getItem(48));
    	return;
    }
    
    void askSellPrice(int slot_num) {
    	this.player.setItemOnCursor(new ItemStack(Material.AIR));
		this.edit_type = "SellPrice";
		this.edit_slot = slot_num;
		this.inventory_view.close();
		hp.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
		HyperObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
								hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		player.sendMessage(ChatColor.YELLOW+"Currently you pay other players "+ho.getSellPriceWithTax(1, hp)+" for "+ho.getDisplayName());
		player.sendMessage(ChatColor.YELLOW+"You have 8 seconds to enter the new price you will pay.");
		return;
    }
    
    void setBuyDynamic(int slot_num){
    	HyperObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
    							hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		ho.setBuyPrice(0.0);
		this.itemRefresh(slot_num, ho);
    	return;
    }
    
    void setBuyPrice(int slot_num, Double price){
    	//out.println("NEW PRICE: "+price);
    	HyperObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
    	ho.setBuyPrice(price);
		this.editcooldown.cancel();
		player.sendMessage(ChatColor.YELLOW+"The sell price for "+ho.getDisplayName()+" is now "+price.toString());
		this.inventory_view=this.player.openInventory(this.inventory);
		this.itemRefresh(slot_num, ho);
		player.setItemOnCursor(this.inventory.getItem(47));
    	return;
    }
    
    void askBuyPrice(int slot_num) {
    	this.player.setItemOnCursor(new ItemStack(Material.AIR));
		this.edit_type = "BuyPrice";
		this.edit_slot = slot_num;
		this.inventory_view.close();
		HyperObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		player.sendMessage(ChatColor.YELLOW+"Currently other players pay you "+ho.getBuyPriceWithTax(1)+" for "+ho.getDisplayName());
		player.sendMessage(ChatColor.YELLOW+"You have 8 seconds to enter the new price you will be paid.");
		return;
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
	void onPlayerChat(AsyncPlayerChatEvent event) {
    	if (event.getPlayer().getName().equals(this.player.getName())){
    		this.edit_value = event.getMessage();
			if (this.edit_in_progress) {
				event.setCancelled(true);
				try {
					if (this.edit_type.equals("BuyPrice")) {
						this.setBuyPrice(this.edit_slot, Double.valueOf(edit_value));
						this.edit_slot = -1;
						this.edit_value = null;
						return;
					} 
					else if (this.edit_type.equals("SellPrice")) {
						this.setSellPrice(this.edit_slot, Double.valueOf(edit_value));
						this.edit_slot = -1;
						this.edit_value = null;
						return;
					} 
				} catch (Exception e) {
					player.sendMessage(ChatColor.YELLOW+"Couldn't change price..");
					this.edit_slot = -1;
					this.edit_type = null;
					this.edit_value = null;
					return;
				} finally {
					this.edit_slot = -1;
					//this.edit_type = null;
					this.edit_value = null;
				}
			}
		}
	}
    
    
    void handleMenuCommand(int slot_num, InventoryClickEvent event){
        if (this.edit_in_progress) {
        	event.setCancelled(true);
        	this.edit_in_progress = false;
        	player.setItemOnCursor(new ItemStack(Material.AIR));
        	return;
        }
        
        if (slot_num == 46){
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
        else if (slot_num == 47){
        	if (event.isRightClick()) {
        		return;
        	} else {
        		this.edit_in_progress=true;
        		this.edit_type="BuyPrice";
        		player.setItemOnCursor(this.inventory.getItem(slot_num));
        	}
        }
        else if (slot_num == 48){
        	if (event.isRightClick()) {
        		return;
        	} else {
        		this.edit_in_progress=true;
        		this.edit_type="SellPrice";
        		player.setItemOnCursor(this.inventory.getItem(slot_num));
        	}
        }
        else if (slot_num == 49){
        	if (event.isRightClick()) {
        		return;
        	} else {
        		this.edit_in_progress=true;
        		this.edit_type="Status";
        		player.setItemOnCursor(this.inventory.getItem(slot_num));
        	}
        }
    }
    
    
    
    void handlePlayerInventory(int slot_num, InventoryClickEvent event){
        if (this.edit_in_progress) {
        	event.setCancelled(true);
        	this.edit_in_progress = false;
        	player.setItemOnCursor(new ItemStack(Material.AIR));
        	return;
        }
        
        //ItemStack item_in_hand = event.getCursor();
        
        if (event.isShiftClick()) {
        	event.setCancelled(true);
        	return;
        }
    }
    
    
    void handleMenuItem(int slot_num, InventoryClickEvent event){
        if (this.edit_in_progress) {
			if (this.edit_type == "BuyPrice") {
				if (event.getClick().isLeftClick()) {
					this.askBuyPrice(slot_num);
				}
				else if (event.getClick().isRightClick()) {
					this.setBuyDynamic(slot_num);
				}
			}
			else if (this.edit_type == "SellPrice") {
				if (event.getClick().isLeftClick()) {
					this.askSellPrice(slot_num);
				}
				else if (event.getClick().isRightClick()) {
					this.setSellDynamic(slot_num);
				}
			}
			else if (this.edit_type == "Status") {
				if (event.getClick().isLeftClick()) {
					this.setStatus(slot_num, -1);
				}
				else if (event.getClick().isRightClick()) {
					this.setStatus(slot_num, 1);
				}
			}
			return;
		}
        
        ItemStack item_in_hand = event.getCursor();
        if (item_in_hand.getType() == Material.AIR) {
    		if (this.optionNames[slot_num] != null && this.optionNames[slot_num] != " ") {
                if (event.isLeftClick()){
                	if (event.isShiftClick()){
                		HyperObject ho2 = this.shop_trans.Remove(this.optionNames[slot_num], 8);
                        if (ho2 != null) {
                        	this.itemRefresh(slot_num, ho2);
                        }
                	}
                	else {
                		HyperObject ho2 = this.shop_trans.Remove(this.optionNames[slot_num], 1);
                        if (ho2 != null) {
                        	this.itemRefresh(slot_num, ho2);
                        }
                	}
                }
                else if (event.isRightClick() && event.isShiftClick()) {
            		HyperObject ho2 = this.shop_trans.Remove(this.optionNames[slot_num], this.optionIcons[slot_num].getMaxStackSize());
                    if (ho2 != null) {
                    	this.itemRefresh(slot_num, ho2);
                    }
                }        
    		}
        }
        
        else if (item_in_hand.getType() != Material.AIR) {
			ArrayList<String> enchants = new ArrayList<String>();

    		//ADDING ENCHANTED BOOK
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

	                    	HyperObject ho2 = this.shop_trans.Add(e);
	                        if (ho2 != null) {
	            				player.setItemOnCursor(player.getItemInHand());
	            				player.setItemInHand(item_holding);		
	                			int slot = this.itemOnCurrentPage(ho2);
	                			if (slot > -1) {
	                				this.itemRefresh(slot, ho2);
	                			}
	                		} else {
	                    		player.setItemInHand(item_holding);
	                		}

	                	} else {
	                		player.sendMessage(ChatColor.YELLOW+"This shop doesn't want your enchantments");
	                	}
    				}
    			}

    		//MAKE LISTS OF ITEM ON CURSOR ENCHANMENTS	
    		} else {
    			//make list of hyperconomy enchantment names from book enchants
    			for (HyperObject hob : hyperAPI.getEnchantmentHyperObjects(item_in_hand, player.getName())) {
    				enchants.add(hob.getDisplayName());
    			}
    		}
    		
    		// ADDING ENCHANTS
    		if (!enchants.isEmpty()) {
            	String display_name = this.optionIcons[slot_num].getItemMeta().getDisplayName().replace("§6", "");
    			String enchant_name = display_name.replace(" ", "_");
            	if (enchants.contains(enchant_name)) {
            		
            		ItemStack item_holding = player.getItemInHand().clone();
            		player.setItemInHand(player.getItemOnCursor().clone());
            		
                	HyperObject ho2 = this.shop_trans.Add(enchant_name);
                    if (ho2 != null) {
        				player.setItemOnCursor(player.getItemInHand());
        				player.setItemInHand(item_holding);
            			int slot = this.itemOnCurrentPage(ho2);
            			if (slot > -1) {
            				this.itemRefresh(slot, ho2);
            			}
        				return;
            		} else {

                		player.setItemInHand(item_holding);
                		return;
            		}
            	}
            	
            	// TRY ADDING ENCHANTED ITEM
            	HyperObject ho2 = this.shop_trans.Add(item_in_hand);
                if (ho2 != null) {
	        		this.inventory_view.setCursor(new ItemStack(Material.AIR));
        			int slot = this.itemOnCurrentPage(ho2);
        			if (slot > -1) {
        				this.itemRefresh(slot, ho2);
        			}
	        			
            	} else {
            		player.sendMessage(ChatColor.YELLOW+"This shop doesn't want your enchanted item");
            	}
            }
            
            // ADDING ITEMS
    		HyperObject ho2 = this.shop_trans.Add(item_in_hand);
            if (ho2 != null) {
    			this.inventory_view.setCursor(new ItemStack(Material.AIR));
    			int slot = this.itemOnCurrentPage(ho2);
    			if (slot > -1) {
    				this.itemRefresh(slot, ho2);
    			}
    			
    		//THE SHOP WONT TAKE IT	
    		} else {
    			player.sendMessage(ChatColor.YELLOW+"This shop does not deal in "+
						item_in_hand.getType().name().toLowerCase()+".");
    			player.getInventory().addItem(item_in_hand);
    			this.inventory_view.setCursor(new ItemStack(Material.AIR));
    		}
    	}
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
            
            if (slot_num < size-9 && slot_num >= 0){
            	this.handleMenuItem(slot_num, event);
            }
            else if (slot_num >= size-9 && slot_num < size) {
            	this.handleMenuCommand(slot_num, event);
            }
            else if (slot_num >= size) {
            	this.handlePlayerInventory(slot_num, event);
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
	    	if (this.edit_in_progress){
	    		if (this.edit_slot == -1 || this.edit_type==null){
	    			player.setItemOnCursor(new ItemStack(Material.AIR));
	    			this.edit_in_progress=false;
	    			this.destroy();
	    			return;
	    		} else {
	    		this.editcooldown = new EditCooldown(this);
	    		editcooldown.runTaskLater(this.plugin, 160);
	    		}
	    	}
	    	else {
	    		this.destroy();
	    	}
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

    class EditCooldown extends BukkitRunnable {
    	ManageMenu menu;
        public EditCooldown(ManageMenu mm) {
        	menu=mm;
        }
        public void run() {
            try {
            	if (edit_in_progress){
            		menu.inventory_view=menu.player.openInventory(menu.inventory);
            	}
            		
            } catch (Exception e) {
            	//do nothing
            }
        }
    }
}