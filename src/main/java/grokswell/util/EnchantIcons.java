package grokswell.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantIcons {
	
	public ItemStack getIcon(String ename, int elevel) {
		ItemStack stack = null;
		if (ename.contains("feather_falling")) {
			stack = new ItemStack(Material.LEATHER_BOOTS);
			stack.addEnchantment(Enchantment.PROTECTION_FALL, elevel);
		}
		
		else if (ename.contains("unbreaking")) {
			stack = new ItemStack(Material.IRON_HOE);
			stack.addEnchantment(Enchantment.DURABILITY, elevel);
		}
		
		else if (ename.contains("thorns")) {
			stack = new ItemStack(Material.LEATHER_CHESTPLATE);
			stack.addEnchantment(Enchantment.THORNS, elevel);
		}
		
		else if (ename.contains("efficiency")) {
			stack = new ItemStack(Material.IRON_SPADE);
			stack.addEnchantment(Enchantment.DIG_SPEED, elevel);
		}
		
		else if (ename.contains("aqua_affinity")) {
			stack = new ItemStack(Material.LEATHER_HELMET);
			stack.addEnchantment(Enchantment.WATER_WORKER, elevel);
		}
		
		else if (ename.contains("respiration")) {
			stack = new ItemStack(Material.LEATHER_HELMET);
			stack.addEnchantment(Enchantment.OXYGEN, elevel);
		}
		
		else if (ename.contains("silktouch") ) {
			stack = new ItemStack(Material.SHEARS);
			stack.addEnchantment(Enchantment.SILK_TOUCH, elevel);
		}
		
		else if (ename.contains("fortune")) {
			stack = new ItemStack(Material.IRON_SPADE);
			stack.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, elevel);
		}
		
		else if (ename.contains("luck_of_the_sea")) {
			stack = new ItemStack(Material.FISHING_ROD);
			stack.addEnchantment(Enchantment.LUCK, elevel);
		}
		
		else if (ename.contains("lure")) {
			stack = new ItemStack(Material.FISHING_ROD);
			stack.addEnchantment(Enchantment.LURE, elevel);
		}
		
		else if (ename.contains("bane_of_arthropods")) {
			stack = new ItemStack(Material.IRON_AXE);
			stack.addEnchantment(Enchantment.DAMAGE_ARTHROPODS, elevel);
		}
		
		else if (ename.contains("sharpness")) {
			stack = new ItemStack(Material.IRON_AXE);
			stack.addEnchantment(Enchantment.DAMAGE_ALL, elevel);
		}
		
		else if (ename.contains("smite")) {
			stack = new ItemStack(Material.IRON_AXE);
			stack.addEnchantment(Enchantment.DAMAGE_UNDEAD, elevel);
		}
		
		else if (ename.contains("fire_aspect")) {
			stack = new ItemStack(Material.IRON_SWORD);
			stack.addEnchantment(Enchantment.FIRE_ASPECT, elevel);
		}
		
		else if (ename.contains("knockback")) {
			stack = new ItemStack(Material.IRON_SWORD);
			stack.addEnchantment(Enchantment.KNOCKBACK, elevel);
		}
		
		else if (ename.contains("looting")) {
			stack = new ItemStack(Material.IRON_SWORD);
			stack.addEnchantment(Enchantment.LOOT_BONUS_MOBS, elevel);
		}
		
		else if (ename.contains("blast_protection")) {
			stack = new ItemStack(Material.LEATHER_LEGGINGS);
			stack.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, elevel);
		}
		
		else if (ename.contains("fire_protection")) {
			stack = new ItemStack(Material.LEATHER_LEGGINGS);
			stack.addEnchantment(Enchantment.PROTECTION_FIRE, elevel);
		}
		
		else if (ename.contains("protection")) {
			stack = new ItemStack(Material.LEATHER_LEGGINGS);
			stack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, elevel);
		}
		
		else if (ename.contains("projectile_protection")) {
			stack = new ItemStack(Material.LEATHER_LEGGINGS);
			stack.addEnchantment(Enchantment.PROTECTION_PROJECTILE, elevel);
		}
		
		else if (ename.contains("flame")) {
			stack = new ItemStack(Material.BOW);
			stack.addEnchantment(Enchantment.ARROW_FIRE, elevel);
		}
		
		else if (ename.contains("infinity")) {
			stack = new ItemStack(Material.BOW);
			stack.addEnchantment(Enchantment.ARROW_INFINITE, elevel);
		}
		
		else if (ename.contains("power")) {
			stack = new ItemStack(Material.BOW);
			stack.addEnchantment(Enchantment.ARROW_DAMAGE, elevel);
		}
		
		else if (ename.contains("punch")) {
			stack = new ItemStack(Material.BOW);
			stack.addEnchantment(Enchantment.ARROW_KNOCKBACK, elevel);
		}
		
		return stack;
	}
	
	
	
}
