package grokswell.hypermerchant;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.hyperobject.HyperObject;

public interface MerchantMenu {
	public ShopStock getShopStock();
	
	public MerchantMenu setOption(int position, ItemStack icon, String name, String... info);
	public MerchantMenu setOption(int position, ItemStack icon);
	public void loadPage();
	public void nextPage();
	public void previousPage();
	public void lastPage();
	public void firstPage();
	public int itemOnCurrentPage(HyperObject ho);
	public void ToggleZeroStock();
	public void Sort();
	public void itemRefresh(int slot, HyperObject ho);
	public void menuRefresh();
	public void openMenu(Player player);
	public void destroy();
}
