package grokswell.hypermerchant;

import org.bukkit.Bukkit;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class HyperMerchantTrait extends Trait{
	private final HyperMerchantPlugin plugin;
	public HyperMerchantTrait() {
		super("hypermerchant");
		plugin = (HyperMerchantPlugin) Bukkit.getServer().getPluginManager().getPlugin("HyperMerchant");

	}
}
