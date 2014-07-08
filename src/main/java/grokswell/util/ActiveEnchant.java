package grokswell.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
	 
//Thanks to Captain Bern for this class
public class ActiveEnchant extends EnchantmentWrapper {
	 
    public ActiveEnchant(int id) {
        super(id);
    }
    
    @Override
    public boolean canEnchantItem(ItemStack item) {
        return true;
    }
 
    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }
 
    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }
 
    @Override
    public int getMaxLevel() {
        return 2;
    }
 
    @Override
    public String getName() {
        return "Active";
    }
 
    @Override
    public int getStartLevel() {
        return 1;
    }
	
}
