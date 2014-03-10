package grokswell.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantIcons {
	
	public ItemStack getIcon(String ename) {
		ItemStack stack = null;
		if (ename.contains("feather_falling")) {
			stack = new ItemStack(Material.LEATHER_BOOTS);
			stack.addEnchantment(Enchantment.PROTECTION_FALL, 1);
		}
		
		else if (ename.contains("unbreaking")) {
			stack = new ItemStack(Material.IRON_HOE);
			stack.addEnchantment(Enchantment.DURABILITY, 1);
		}
		
		else if (ename.contains("thorns")) {
			stack = new ItemStack(Material.LEATHER_CHESTPLATE);
			stack.addEnchantment(Enchantment.THORNS, 1);
		}
		
		else if (ename.contains("efficiency")) {
			stack = new ItemStack(Material.IRON_SPADE);
			stack.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
		}
		
		else if (ename.contains("aqua_affinity") || ename.contains("respiration")) {
			stack = new ItemStack(Material.LEATHER_HELMET);
			stack.addEnchantment(Enchantment.OXYGEN, 1);
		}
		
		else if (ename.contains("silktouch") || ename.contains("fortune")) {
			stack = new ItemStack(Material.SHEARS);
			stack.addEnchantment(Enchantment.SILK_TOUCH, 1);
		}
		
		else if (ename.contains("luck_of_the_sea") || ename.contains("lure")) {
			stack = new ItemStack(Material.FISHING_ROD);
			stack.addEnchantment(Enchantment.LURE, 1);
		}
		
		else if (ename.contains("bane_of_arthropods") || ename.contains("sharpness") || 
				ename.contains("smite")) {
			stack = new ItemStack(Material.IRON_AXE);
			stack.addEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1);
		}
		
		else if (ename.contains("fire_aspect") || ename.contains("knockback") || 
				ename.contains("looting")) {
			stack = new ItemStack(Material.IRON_SWORD);
			stack.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		}
		
		else if (ename.contains("blast_protection") || ename.contains("fire_protection") || 
				ename.contains("protection") || ename.contains("projectile_protection")) {
			stack = new ItemStack(Material.LEATHER_LEGGINGS);
			stack.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
		}
		
		else if (ename.contains("flame") || ename.contains("infinity") || 
				ename.contains("power") || ename.contains("punch")) {
			stack = new ItemStack(Material.BOW);
			stack.addEnchantment(Enchantment.ARROW_FIRE, 1);
		}
		
		return stack;
	}
	
	
	
}
