package grokswell.hypermerchant;

import static java.lang.System.out;
import grokswell.util.Utils;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.NPCSelector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;

import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.api.HyperEconAPI;
import regalowl.hyperconomy.api.HyperObjectAPI;


public class MerchantMethods {
	NPCRegistry npcReg = CitizensAPI.getNPCRegistry();
	NPCSelector npcSel = CitizensAPI.getDefaultNPCSelector();
	HyperAPI hyperAPI = new HyperAPI();
	HyperEconAPI heAPI = new HyperEconAPI();
	HyperObjectAPI hoAPI = new HyperObjectAPI();
	Utils utils = new Utils();

	public String ListMerchants(Player player) {
		String message = "";
		if (player == null) {
			for (NPC npc: npcReg) {
				if (npc.hasTrait(HyperMerchantTrait.class)) {
					if (npc.getTrait(HyperMerchantTrait.class).offduty) {
						message=message+ChatColor.YELLOW+npc.getName()+" is OFFDUTY\n";
					} else {
						message=message+ChatColor.YELLOW+npc.getName()+" is ONDUTY\n";
					}
					message=message+ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n";
				}
			}
			
		} else {
			for (NPC npc: npcReg) {
				if (npc.hasTrait(HyperMerchantTrait.class)) {
					if (npc.data().get("owner") == player.getName()){
						if (npc.getTrait(HyperMerchantTrait.class).offduty) {
							message=message+ChatColor.YELLOW+npc.getName()+" is OFFDUTY\n";
						} else {
							message=message+ChatColor.YELLOW+npc.getName()+" is ONDUTY\n";
						}
						message=message+ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n";
					}
				}
			}
		}
		
		
		return message;
	}
	

	public String GetInfo(CommandSender sender, int id) {
		String message = "";
		NPC npc = npcReg.getById(id);
		if (npc == null) {
			return null;
		}
		
		
		if (npc.getTrait(HyperMerchantTrait.class).offduty) {
			message=message+ChatColor.YELLOW+npc.getName()+" is OFFDUTY.\n";
		} else {
			message=message+ChatColor.YELLOW+npc.getName()+" is ONDUTY.\n";
		}
		
		
		if (npc.getTrait(HyperMerchantTrait.class).forhire) {
			message=message+ChatColor.YELLOW+npc.getName()+" is for hire.\n";
		} else {
			message=message+ChatColor.YELLOW+npc.getName()+" is not for hire.\n";
		}
		
		
		if (npc.getTrait(HyperMerchantTrait.class).rental) {
			message=message+ChatColor.YELLOW+npc.getName()+" has a shop for rent.\n";
		} else {
			message=message+ChatColor.YELLOW+npc.getName()+" does not have a shop for rent.\n";
		}
		
		
		message=message+ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n";
		return message;
	}
	
	
	public int GetClerkCount(Player player) {
		int clerkcount = 0;
		for (NPC npc : npcReg) {
			if (npc.hasTrait(HyperMerchantTrait.class)) {
				if (GetEmployer(npc.getId()) != null) {
					if (GetEmployer(npc.getId()).equals(player.getName())) {
						clerkcount = clerkcount+1;
					}
				}
			}
		}
		
		return clerkcount;
	}
	
	
	public String GetEmployer(int id) {
		NPC npc = npcReg.getById(id);
		if (npc == null) {
			return null;
		}
		
		String ownerName = null;
		String shopName = GetShop(id);
		
		for (String name : hyperAPI.getPlayerShopList()) {
			if (name.equals(shopName) ) {
				ownerName = hyperAPI.getPlayerShop(name).getOwner().getName();
			}
		}
		
		return ownerName;
	}
	

	public String GetShop(int id) {
		NPC npc = npcReg.getById(id);
		if (npc == null) {
			return null;
		}
		
		return npc.getTrait(HyperMerchantTrait.class).shop_name;
	}

	
	public boolean Teleport(int id, Location location) {
		NPC this_npc = npcReg.getById(id); 
		this_npc.teleport(location, TeleportCause.PLUGIN);
		return true;
	}

	
	public int Hire(String npcname, String npctype, String shopname, Location location) {
		NPC this_npc;
		String npc_type = npctype;
		int id = -1;
		//out.println(npc_type);
		if (npc_type == null){
			npc_type = "PLAYER";
		}

		this_npc = npcReg.createNPC(EntityType.valueOf(npc_type.toUpperCase()), npcname); 
		this_npc.addTrait(HyperMerchantTrait.class);

		this_npc.getTrait(HyperMerchantTrait.class).shop_name = shopname;
		this_npc.spawn(location);

		id = this_npc.getId();
		
		return id;
	}

	
	public boolean Fire(int id) {
		NPC npc = npcReg.getById(id);
		npc.destroy();
		return true;
	}

	
	public boolean Select(int id, Player player,Plugin plugin) {
		if (player.hasPermission("citizens.npc.select")) {
			player.performCommand("npc select "+Integer.toString(id));
		} else {
		    player.addAttachment(plugin, "citizens.npc.select", true);
			player.performCommand("npc select "+Integer.toString(id));
			player.addAttachment(plugin, "citizens.npc.select", false);
		}
		return true;
	}

	
	public boolean Equip(int id, Player player,Plugin plugin) {
		if (player.hasPermission("citizens.npc.edit.equip")) {
			player.performCommand("npc equip");
		} else {
		    player.addAttachment(plugin, "citizens.npc.edit.equip", true);
			player.performCommand("npc equip");
			player.addAttachment(plugin, "citizens.npc.edit.equip", false);
		}
		return true;
	}

	
	public String SetShop(int id, String shopname) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		

		if (shopname.isEmpty()) {
			message=message+ChatColor.YELLOW+"You must specify a shop name, or be standing " +
								"inside of a shop to use the command "+ChatColor.RED+"setshop.";
			
		} else if (hyperAPI.getServerShopList().contains(shopname) || hyperAPI.getPlayerShopList().contains(shopname)) {
			this_npc.getTrait(HyperMerchantTrait.class).shop_name = shopname;
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" has been assigned to shop "+
								this_npc.getTrait(HyperMerchantTrait.class).shop_name;
			
		} else {
			message=message+ChatColor.YELLOW+"You must provide a valid shop name. " +
					"Use "+ChatColor.RED+"/remoteshoplist "+ChatColor.YELLOW+ 
							"or "+ChatColor.RED+"/rslist "+ChatColor.YELLOW+
							"for valid shop names. Use exact spelling.";	
		}	
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		return message;
	}

	
	public boolean SetType(int id, String npctype) {
		NPC this_npc = npcReg.getById(id);
        try {
        	this_npc.setBukkitEntityType(EntityType.valueOf(npctype.toUpperCase()));
    		return true;
        } catch (Exception e) {
			out.println("EXCEPTION: "+e);
        	return false;
        }
	}


	public String SetCommission(int id, double percentage) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).comission = percentage;
		message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" now recieves a "+percentage+"% comission";
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		return message;
	}


	public String SetRentalPrice(int id, double price) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).rental_price = price;
		message=message+ChatColor.YELLOW+"It now costs "+price+" to rent a shop from "+this_npc.getName();
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		return message;
	}
	
	
	public String ToggleOffduty(int id) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).offduty = !this_npc.getTrait(HyperMerchantTrait.class).offduty;
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).offduty) {
			message=ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now off duty.";
		} else {
			message=ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now on duty.";
		}
		return message;
	}
	
	
	public String ToggleForHire(int id) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).forhire = !this_npc.getTrait(HyperMerchantTrait.class).forhire;
		this_npc.getTrait(HyperMerchantTrait.class).rental = false;
		this_npc.getTrait(HyperMerchantTrait.class).rented = false;
		this_npc.getTrait(HyperMerchantTrait.class).hired = false;
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		if (!this_npc.getTrait(HyperMerchantTrait.class).forhire) {
			message=ChatColor.YELLOW+"NPC "+this_npc.getName()+" is no longer for hire.";
		} else {
			message=ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now for hire.";
		}
		
		
		return message;
	}
	
	
	public String ToggleRental(int id) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).rental = !this_npc.getTrait(HyperMerchantTrait.class).rental;
		this_npc.getTrait(HyperMerchantTrait.class).forhire = false;
		this_npc.getTrait(HyperMerchantTrait.class).rented = false;
		this_npc.getTrait(HyperMerchantTrait.class).hired = false;
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		if (!this_npc.getTrait(HyperMerchantTrait.class).rental) {
			message=ChatColor.YELLOW+"NPC "+this_npc.getName()+"'s shop is no longer for rent.";
		} else {
			message=ChatColor.YELLOW+"NPC "+this_npc.getName()+"'s shop is now for rent.";
		}
		
		
		return message;
	}

	
	public String SetGreeting(int id, String greeting) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).welcomeMsg = greeting;
		
		if (greeting=="") {
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a greeting to customers.";
		} else {
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" greeting message has been updated.";
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	public String SetFarewell(int id, String farewell) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).farewellMsg = farewell;
		
		if (farewell=="") {
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a farewell to customers.";
		} else {
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" farewell message has been updated.";
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	public String SetClosed(int id, String closed) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).closedMsg = closed;
		
		if (closed=="") {
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a closed message to customers.";
		} else {
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" closed message has been updated.";
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	public String SetDenial(int id, String denial) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).denialMsg = denial;
		
		if (denial=="") {
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a denial message to customers.";
		} else {
			message=message+ChatColor.YELLOW+"NPC "+this_npc.getName()+" denial message has been updated.";
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	public String FireClerk(Player player) {
		String message = "";
		NPC npc = npcSel.getSelected(player);
		if (npc == null) {
			message ="You must select a clerk that works for you.";
			return message;
		}
		String shopname = npc.getTrait(HyperMerchantTrait.class).shop_name;
		
		if (npc.getTrait(HyperMerchantTrait.class).hired) {
			if (hyperAPI.getPlayerShop(shopname).getOwner().getName() == player.getName()) {
				npc.getTrait(HyperMerchantTrait.class).hired = false;
				npc.getTrait(HyperMerchantTrait.class).forhire = true;
				Teleport(npc.getId(), utils.StringToLoc(npc.getTrait(HyperMerchantTrait.class).location));
				npc.getTrait(HyperMerchantTrait.class).shop_name = null;
				npc.getTrait(HyperMerchantTrait.class).location = null;
				message = npc.getName()+" no longer works for you.";
			} else {
				message ="You must select a clerk that works for you.";
				return message;
			}
			
		} else {
			message = "You cannot use the /fireclerk command on the selected npc, "+npc.getName()+".";
			return message;
		}
		
		
		if (npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			npc.getTrait(HyperMerchantTrait.class).save(npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	public String CloseShop(Player player) {
		String message = "";
		NPC npc = npcSel.getSelected(player);
		if (npc == null) {
			message ="You must select a clerk that works for you.";
			return message;
		}
		String shopname = npc.getTrait(HyperMerchantTrait.class).shop_name;
		
		
		if (npc.getTrait(HyperMerchantTrait.class).rented) {
			if (hyperAPI.getPlayerShop(shopname).getOwner().getName() == player.getName()) {
				npc.getTrait(HyperMerchantTrait.class).rented = false;
				npc.getTrait(HyperMerchantTrait.class).rental = true;
				Teleport(npc.getId(), utils.StringToLoc(npc.getTrait(HyperMerchantTrait.class).location));
				npc.getTrait(HyperMerchantTrait.class).location = null;
				
				HyperMerchantPlugin plugin = (HyperMerchantPlugin) Bukkit.getServer().getPluginManager().getPlugin("HyperMerchant");
				hyperAPI.getPlayerShop(shopname).setOwner(hoAPI.getHyperPlayer(plugin.settings.getDEFAULT_RENTAL_OWNER()));
				//hyperAPI.getPlayerShop(shopname).setOwner(hoAPI.getHyperPlayer("GREG"));
				message = "The shop "+shopname+" is now closed.";
				
			} else {
				message ="You must select a clerk that works for you.";
				return message;
			}
			
		} else {
			message = "You cannot use the /closeshop command on the selected npc.";
			return message;
		}
		
		
		if (npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			npc.getTrait(HyperMerchantTrait.class).save(npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}
		
}
