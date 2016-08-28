package grokswell.hypermerchant;

//import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.account.HyperPlayer;

import grokswell.hypermerchant.ShopTransactions;
import grokswell.util.EnchantIcons;
import grokswell.util.HyperToBukkit;
import grokswell.util.Language;
import grokswell.util.Utils;
 
public class ManageMenu implements Listener, MerchantMenu {
 
    private String shopname; //name of the shop
    private int size;
    int page_number; //the current page the player is viewing
    int item_count; //number of items in this shop
    int last_page; //the last_page number in the menu
    int sort_by; //sort-by 0=item name, 1=item type, 2=item price, 3=item quantity
    int display_zero_stock; //toggle displaying items with zero stock
    private HyperMerchantPlugin HMP;
    private Player player;
    private String inventory_name;
    private Inventory inventory;
    private InventoryView inventory_view;
    private String[] optionNames;
    private ItemStack[] optionIcons;
	private ShopTransactions shop_trans;
	private ItemStack sorting_icon;
	private HyperConomy hc;
	ArrayList<String> status_list;
	String economy_name;
	HyperToBukkit hypBuk;
	
	Language L;
	public ShopStock shopstock;
	
	NPC npc;
	double commission;
	
	HyperAPI hyperAPI;
	HyperPlayer hyplay;
	
	int edit_slot;
	String edit_value;
	String edit_mode;
	boolean edit_in_progress;
	EditCooldown editcooldown;
	
	
    public ManageMenu(String name, int size, HyperMerchantPlugin plgn,CommandSender sender, Player plyr, NPC npc) {
    	edit_value=null;
    	edit_mode=null;
    	edit_slot=-1;
    	edit_in_progress = false;
    	status_list = new ArrayList<String>();
    	status_list.add("buy");
    	status_list.add("sell");
    	status_list.add("trade");
    	status_list.add("none");

    	L = plgn.language;
    	hyperAPI = plgn.hyperAPI;
    	hypBuk = new HyperToBukkit();
    	this.editcooldown=null;
    	this.sort_by=0;
    	this.display_zero_stock=1;
    	this.shopname = name;
        this.size = size;
        this.HMP = plgn;
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
    	this.shop_trans = new ShopTransactions(player, this.shopname, this.HMP, this);
    	this.HMP.getServer().getPluginManager().registerEvents(this, this.HMP);
    	
    	UpdateSortingIcon();
    	
        String iname = (this.shopname+"<>"+player.getName());
        if (iname.length()>32) {
        	this.inventory_name = iname.substring(0, 27)+this.HMP.uniquifier.uniquify();
        } else {
        	this.inventory_name = iname;
        }

        this.inventory = Bukkit.createInventory(player, size, this.inventory_name);

    	hyplay = hyperAPI.getHyperPlayer(player.getName());
		hc = HMP.hc;
    	
        economy_name = hyperAPI.getShop(this.shopname).getEconomy();
        
		this.shopstock = new ShopStock(sender, this.player, this.shopname, this.HMP, "manage");
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
			e.printStackTrace();
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
    
    
    @SuppressWarnings("static-access")
	public void loadPage() {
    	this.optionIcons = null;
    	this.optionIcons = new ItemStack[size];
    	this.optionNames = null;
    	this.optionNames = new String[size];
    	
    	// Populate interface button inventory slots
    	this.setOption(46, HMP.menuButtonData.back)
	    .setOption(45, HMP.menuButtonData.first_page)
	    .setOption(52, HMP.menuButtonData.forward.clone())
	    .setOption(53, HMP.menuButtonData.last_page.clone())
	    .setOption(47, HMP.menuButtonData.sell_price.clone())
	    .setOption(48, HMP.menuButtonData.buy_price.clone())
	    .setOption(49, HMP.menuButtonData.status.clone())
	    .setOption(50, HMP.menuButtonData.manage_help_1)
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
	        
	        TradeObject ho = hyperAPI.getHyperObject(item_name, economy_name, hyperAPI.getShop(shopname));
	        if (ho == null) {
	        	stock=0;
	        	stack=new ItemStack(Material.AIR, 1, (short) 0);
	        	value=0;
	        	cost=0;
	        	
	        } else if (ho.getType()==TradeObjectType.ITEM) {
	        	stock = ho.getStock();
				stack = hypBuk.getItemStack(ho.getItemStack(1));
				
				hyplay.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
				value = ho.getSellPriceWithTax(1.0, hyplay);
				cost = ho.getBuyPriceWithTax(1.0);
				
			} else if (ho.getType()==TradeObjectType.ENCHANTMENT) {
				stock = ho.getStock();
				
				hyplay.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
				value = ho.getSellPriceWithTax(1.0, hyplay);
				cost = ho.getBuyPriceWithTax(1.0);

				stack = (new EnchantIcons()).getIcon(ho.getDisplayName(), ho.getEnchantmentLevel());

				
			} else if (ho.getType()==TradeObjectType.EXPERIENCE) {
				stock = ho.getStock();
				
				hyplay.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
				value = ho.getSellPriceWithTax(1.0, hyplay);
				cost = ho.getBuyPriceWithTax(1.0);

				stack = new ItemStack(Material.POTION, 1, (short) 0);
				
			} else {
				stack = new ItemStack(Material.AIR, 1, (short) 0);
			}
	        
	        String buy_dynamic = Utils.formatText(L.II_DYNAMIC, null);
	        String sell_dynamic = Utils.formatText(L.II_DYNAMIC, null);
	        if (ho != null){
		        if (ho.getShopObjectBuyPrice() > 0.0) {
		        	sell_dynamic = Utils.formatText(L.II_STATIC, null);
		        }
		        if (ho.getShopObjectSellPrice() > 0.0) {
		        	buy_dynamic = Utils.formatText(L.II_STATIC, null);
		        }
		        
		        HashMap<String, String> keywords = new HashMap<String, String>();
				keywords.put("<buyprice>",  String.format("%.2f", value));
				keywords.put("<buytype>",  buy_dynamic);
				keywords.put("<sellprice>",  String.format("%.2f", cost));
				keywords.put("<selltype>",  sell_dynamic);
				keywords.put("<stock>",  String.valueOf((int) stock));
				keywords.put("<status>",  ho.getShopObjectStatus().name().toLowerCase());
				keywords.put("<static>",  L.II_STATIC);
				keywords.put("<dynamic>",  L.II_DYNAMIC);
		    	this.setOption(count, stack, ho.getDisplayName().replaceAll("_", " "),
		    			Utils.formatText(L.II_MANAGE_SELL, keywords),
		    			Utils.formatText(L.II_MANAGE_BUY, keywords),
		    			Utils.formatText(L.II_STOCK, keywords),
		    			Utils.formatText(L.II_STATUS, keywords));
		    	
	        } else {
	        	this.setOption(count, stack,"","");
	        }
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
    
    public void refreshPage() {
		shopstock.Refresh(sort_by, display_zero_stock);
		this.inventory.clear();
		this.loadPage();
    	this.menuRefresh();
    }
    
    
    public int itemOnCurrentPage(TradeObject ho) {
		int count = 0;
    	for (String item_name:shopstock.pages.get(this.page_number)){
    		if (item_name.equals(ho.getName())){
    			return count; 
    		}
        	count = count+1;
		}
    	return -1;
    }
    
    
    @SuppressWarnings("static-access")
	private void UpdateSortingIcon() {
    	this.sorting_icon = HMP.menuButtonData.help5.clone();
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
    		lore.add(L.MM_SORTING+": "+L.MM_ITEM_NAME);
    	} else if (sort_by == 1){
    		lore.add(L.MM_SORTING+": "+L.MM_MATERIAL_NAME);
    	} else if (sort_by == 2){
    		lore.add(L.MM_SORTING+": "+L.MM_PURCHASE_PRICE);
    	} else if (sort_by == 3){
    		lore.add(L.MM_SORTING+": "+L.MM_SELL_PRICE_2);
    	} else if (sort_by == 4){
    		lore.add(L.MM_SORTING+": "+L.MM_STOCK_AMOUNT);
    	}
    	
    	lore.add(" ");
    	
    	if (display_zero_stock == 0){
    		lore.add(L.MM_SHOW_ZERO_STOCK+": "+L.G_NO);
    	} else {
    		lore.add(L.MM_SHOW_ZERO_STOCK+": "+L.G_YES);
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

    
    @SuppressWarnings("static-access")
	public void itemRefresh(int slot, TradeObject ho) {
    	hyplay.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());

        String sell_dynamic = Utils.formatText(L.II_DYNAMIC, null);
        String buy_dynamic = Utils.formatText(L.II_DYNAMIC, null);
        if (ho.getShopObjectBuyPrice() > 0.0) {
        	sell_dynamic = Utils.formatText(L.II_STATIC, null);
        }
        //if (ho.getSellPrice(1) > 0.0) {
        if (ho.getShopObjectSellPrice() > 0.0) {
        	buy_dynamic = Utils.formatText(L.II_STATIC, null);
        }
        
        
        ItemStack stack;
        if (ho.getType()==TradeObjectType.ENCHANTMENT) {
        	stack = (new EnchantIcons()).getIcon(ho.getDisplayName(), ho.getEnchantmentLevel());
        }
        else if (ho.getType()==TradeObjectType.EXPERIENCE) {
			stack = new ItemStack(Material.POTION, 1, (short) 0);
        }
        else {
        	stack = hypBuk.getItemStack(ho.getItemStack(1));
        }

		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<buyprice>",  String.format("%.2f", ho.getSellPriceWithTax(1.0, hyplay)));
		keywords.put("<buytype>",  buy_dynamic);
		keywords.put("<sellprice>",  String.format("%.2f", ho.getBuyPriceWithTax(1.0)));
		keywords.put("<selltype>",  sell_dynamic);
		keywords.put("<stock>",  String.valueOf((int) ho.getStock()));
		keywords.put("<status>",  ho.getShopObjectStatus().name().toLowerCase());
		keywords.put("<static>",  L.II_STATIC);
		keywords.put("<dynamic>",  L.II_DYNAMIC);
    	this.setOption(slot, stack, ho.getDisplayName().replaceAll("_", " "),
    			Utils.formatText(L.II_MANAGE_SELL, keywords),
    			Utils.formatText(L.II_MANAGE_BUY, keywords),
    			Utils.formatText(L.II_STOCK, keywords),
    			Utils.formatText(L.II_STATUS, keywords));
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
    
    @SuppressWarnings("static-access")
	ItemStack setActiveMeta(ItemStack item) {
    	ItemMeta im = item.getItemMeta();
    	List<String> lore = im.getLore();
    	
    	lore.add(0, Utils.formatText(L.MM_ACTIVATED, null));
    	
    	im.setLore(lore);
    	item.setItemMeta(im);
    	item.addUnsafeEnchantment(HMP.active_enchant, 1);
    	return item;
    }
    
    void toggleEditMode(String mode, int slot_num){
    	if (mode==null) {
    		if (this.edit_mode=="SellPrice") {
    			this.inventory.setItem(47, HMP.menuButtonData.sell_price.clone());
    		}
    		else if (this.edit_mode=="BuyPrice") {
    			this.inventory.setItem(48, HMP.menuButtonData.buy_price.clone());
    		} 
    		else if (this.edit_mode=="Status") {
    			this.inventory.setItem(49, HMP.menuButtonData.status.clone());
    		}
    		this.edit_mode=null;
    	}
    	else if (mode.equals("BuyPrice")) {
    		this.edit_mode="BuyPrice";
			ItemStack im = setActiveMeta(HMP.menuButtonData.buy_price.clone());
			this.inventory.setItem(slot_num, im);
    	}
    	else if (mode.equals("SellPrice")) {
    		this.edit_mode="SellPrice";
			ItemStack im = setActiveMeta(HMP.menuButtonData.sell_price.clone());
			this.inventory.setItem(slot_num, im);
    	}
    	else if (mode.equals("Status")) {
    		this.edit_mode="Status";
			ItemStack im = setActiveMeta(HMP.menuButtonData.status.clone());
			this.inventory.setItem(slot_num, im);
    	}
    }
    
    void setPriceCancel() {
		this.editcooldown.cancel();
		if (this.edit_mode=="SellPrice"){
			this.optionIcons[47].removeEnchantment(HMP.active_enchant);
			this.inventory.setItem(47, this.optionIcons[47]);
    		this.edit_in_progress=false;
		}
		else if (this.edit_mode=="BuyPrice"){
			this.optionIcons[48].removeEnchantment(HMP.active_enchant);
			this.inventory.setItem(48, this.optionIcons[48]);
    		this.edit_in_progress=false;
		}
		this.inventory_view=this.player.openInventory(this.inventory);
    	return;
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
    	TradeObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
    							hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
    	String status = ho.getShopObjectStatus().name();
		TradeObjectStatus newstatus = TradeObjectStatus.fromString(this.changeStatus(click_type, status));
		ho.setShopObjectStatus(newstatus);
		this.itemRefresh(slot_num, ho);
    	return;
    }
    
    void setBuyDynamic(int slot_num){
    	TradeObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
    							hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		ho.setShopObjectSellPrice(0.0);
		this.itemRefresh(slot_num, ho);
    	return;
    }
    
    @SuppressWarnings("static-access")
	void setBuyPrice(int slot_num, Double price){
    	TradeObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
    							hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		ho.setShopObjectSellPrice(price);
		this.editcooldown.cancel();
		
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<cost>", String.format("%.2f", ho.getSellPriceWithTax(1.0, hyplay)));
		keywords.put("<item>", ho.getDisplayName());
		player.sendMessage(Utils.formatText(L.MM_BUY_PRICE, keywords));
		
		this.inventory_view=this.player.openInventory(this.inventory);
		this.itemRefresh(slot_num, ho);
		this.edit_in_progress=false;
    	return;
    }
    
    @SuppressWarnings("static-access")
	void askBuyPrice(int slot_num) {
    	this.player.setItemOnCursor(new ItemStack(Material.AIR));
		this.edit_in_progress=true;
		this.edit_slot = slot_num;
		this.inventory_view.close();
		hyplay.setEconomy(hyperAPI.getShop(this.shopname).getEconomy());
		TradeObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
								hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<cost>", String.format("%.2f", ho.getSellPriceWithTax(1.0, hyplay)));
		keywords.put("<item>", ho.getDisplayName());
		player.sendMessage(Utils.formatText(L.MM_YOUPAY_1, keywords));
		
		return;
    }
    
    void setSellDynamic(int slot_num){
    	TradeObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
    							hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
		ho.setShopObjectBuyPrice(0.0);
		this.itemRefresh(slot_num, ho);
    	return;
    }
    
    @SuppressWarnings("static-access")
	void setSellPrice(int slot_num, Double price){
    	TradeObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));
    	ho.setShopObjectBuyPrice(price);
		this.editcooldown.cancel();
		
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<cost>", String.format("%.2f", price));
		keywords.put("<item>", ho.getDisplayName());
		player.sendMessage(Utils.formatText(L.MM_SELL_PRICE, keywords));
		
		this.inventory_view=this.player.openInventory(this.inventory);
		this.itemRefresh(slot_num, ho);
		this.edit_in_progress=false;
    	return;
    }
    
    @SuppressWarnings("static-access")
	void askSellPrice(int slot_num) {
    	this.player.setItemOnCursor(new ItemStack(Material.AIR));
		this.edit_in_progress=true;
		this.edit_slot = slot_num;
		this.inventory_view.close();
		TradeObject ho = hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname));

		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<cost>", String.format("%.2f", ho.getBuyPriceWithTax(1.0)));
		keywords.put("<item>", ho.getDisplayName());
		player.sendMessage(Utils.formatText(L.MM_THEYPAY_1, keywords));
		
		return;
    }
    
    void handleMenuCommand(int slot_num, InventoryClickEvent event){
        if (this.edit_mode!=null) {
        	event.setCancelled(true);
        	this.toggleEditMode(null, slot_num);
        	return;
        }
        if (event.getCursor().getType()!=Material.AIR) {
        	event.setCancelled(true);
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
        		this.toggleEditMode("SellPrice", slot_num);
        	}
        }
        else if (slot_num == 48){
        	if (event.isRightClick()) {
        		return;
        	} else {
        		this.toggleEditMode("BuyPrice", slot_num);
        	}
        }
        else if (slot_num == 49){
        	if (event.isRightClick()) {
        		return;
        	} else {
        		this.toggleEditMode("Status", slot_num);
        	}
        }
    }
    
    
    
    void handlePlayerInventory(int slot_num, InventoryClickEvent event){
        if (this.edit_mode!=null) {
        	event.setCancelled(true);
        	this.toggleEditMode(null, slot_num);
        	return;
        }
        
        
        if (event.isShiftClick()) {
        	event.setCancelled(true);
        	return;
        }
    }
    
    
    void handleMenuItem(int slot_num, InventoryClickEvent event){
        if (this.edit_mode!=null) {
			if (hyperAPI.getHyperObject(this.optionNames[slot_num].replaceAll(" ", "_"), 
						hyperAPI.getShop(shopname).getEconomy(), hyperAPI.getShop(shopname)) == null) {
				return;
			}

        	if (this.edit_mode == "SellPrice") {
				if (event.getClick().isLeftClick()) {
					this.askSellPrice(slot_num);
				}
				else if (event.getClick().isRightClick()) {
					this.setSellDynamic(slot_num);
				}
			}
			else if (this.edit_mode == "BuyPrice") {
				if (event.getClick().isLeftClick()) {
					this.askBuyPrice(slot_num);
				}
				else if (event.getClick().isRightClick()) {
					this.setBuyDynamic(slot_num);
				}
			}
			else if (this.edit_mode == "Status") {
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
        //IF THE PLAYER IS TAKING SOMETHING FROM THE SHOP INVENTORY
        if (item_in_hand.getType() == Material.AIR) {
    		int qty = 1;
    		if (this.optionNames[slot_num] != null && this.optionNames[slot_num] != " ") {
                if (event.isLeftClick()){
                	if (event.isShiftClick()){
                		qty=8;
                	}
                	else {
                		qty=1;
                	}
                }
                else if (event.isRightClick() && event.isShiftClick()) {
            		qty=this.optionIcons[slot_num].getMaxStackSize();
                    }
                TradeObject ho2 = this.shop_trans.Remove(this.optionNames[slot_num], qty);
                if (ho2 != null) {
	            	if (ho2.getStock()<1){
	            		this.refreshPage();
	            	} else {
	            		this.itemRefresh(slot_num, ho2);
	            	}
	            }
			}
        }
        
        //IF THE PLAYER IS ADDING SOMETHING TO THE SHOP INVENTORY
        else if (item_in_hand.getType() != Material.AIR) {

            // ADDING ITEMS
    		PlayerShop pshop=hyperAPI.getPlayerShop(this.shopname);
    		TradeObject ho = hyplay.getHyperEconomy().getTradeObject(item_in_hand.toString());
    		if (ho != null) {
    			ho = pshop.getPlayerShopObject(ho);
    		}
    		ItemStack stack = this.shop_trans.AddItemStack(item_in_hand);
            if (ho != null && stack != null) {
    			this.inventory_view.setCursor(new ItemStack(hypBuk.getItemStack(ho.getItemStack(1)).getType()));
				if ((int) ho.getStock()==item_in_hand.getAmount()) {
					this.refreshPage();
				} else {
	    			int slot = this.itemOnCurrentPage(ho);
	    			if (slot > -1) {
	    				this.itemRefresh(slot, ho);
	    			}
				}
				player.setItemOnCursor(new ItemStack(Material.AIR));
				player.getInventory().addItem(stack);
				return;
            }
			player.setItemOnCursor(new ItemStack(Material.AIR));
			player.getInventory().addItem(stack);
			return;

    	}
    }
    
    
    @SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	void onPlayerChat(AsyncPlayerChatEvent event) {

		if (event.getPlayer().equals(player)){
			this.edit_value = event.getMessage();
			if (this.edit_in_progress) {
				event.setCancelled(true);
				try {
					if (edit_value.toLowerCase().equals("c")) {
						this.setPriceCancel();
						this.edit_slot = -1;
						this.edit_value = null;
						return;
					} 
					else if (this.edit_mode.equals("SellPrice")) {
						this.setSellPrice(this.edit_slot, Double.valueOf(edit_value));
						this.edit_slot = -1;
						this.edit_value = null;
						return;
					} 
					else if (this.edit_mode.equals("BuyPrice")) {
						this.setBuyPrice(this.edit_slot, Double.valueOf(edit_value));
						this.edit_slot = -1;
						this.edit_value = null;
						return;
					} 
				} catch (Exception e) {
					e.printStackTrace();
					player.sendMessage(Utils.formatText(L.MM_CHANGE_PRICE_FAIL, null));
					this.edit_slot = -1;
					this.edit_mode = null;
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
	    		if (this.edit_slot == -1 || this.edit_mode==null){
	    			player.setItemOnCursor(new ItemStack(Material.AIR));
	    			this.edit_in_progress=false;
	    			this.destroy();
	    			return;
	    		} else {
	    		this.editcooldown = new EditCooldown(this);
	    		editcooldown.runTaskLater(this.HMP, 160);
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
        this.HMP = null;
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