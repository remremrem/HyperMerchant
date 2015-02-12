package grokswell.hypermerchant;

//import static java.lang.System.out;
import java.util.HashMap;

import grokswell.util.Language;
import grokswell.util.Settings;
import grokswell.util.Utils;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.NPCSelector;
import net.citizensnpcs.api.trait.trait.Owner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;

import regalowl.hyperconomy.HyperAPI;


public class MerchantMethods {
	NPCRegistry npcReg = CitizensAPI.getNPCRegistry();
	NPCSelector npcSel = CitizensAPI.getDefaultNPCSelector();
	Utils utils = new Utils();
	
	private HyperAPI hyperAPI() {
		Plugin p = Bukkit.getPluginManager().getPlugin("HyperMerchant");
		HyperMerchantPlugin cp = (HyperMerchantPlugin) p;
		return cp.hyperAPI;
	}
	
	private Language getL() {
		Plugin p = Bukkit.getPluginManager().getPlugin("HyperMerchant");
		HyperMerchantPlugin cp = (HyperMerchantPlugin) p;
		return cp.language;
	}
	
	private Settings getSettings() {
		Plugin p = Bukkit.getPluginManager().getPlugin("HyperMerchant");
		HyperMerchantPlugin cp = (HyperMerchantPlugin) p;
		return cp.settings;
	}
	
	Language L = getL();
	Settings settings = getSettings();
	
	@SuppressWarnings("static-access")
	public String ListMerchants(Player player) {
		String message = "";
		if (player == null) { //if command is from console
			for (NPC npc: npcReg) {
				if (npc.hasTrait(HyperMerchantTrait.class)) {
					if (npc.getTrait(HyperMerchantTrait.class).offduty) {
						HashMap<String, String> keywords = new HashMap<String, String>();
						keywords.put("<npc>",  npc.getName());
						message=message+Utils.formatText(L.CC_OFFDUTY, keywords)+"\n";
					} else {
						HashMap<String, String> keywords = new HashMap<String, String>();
						keywords.put("<npc>",  npc.getName());
						message=message+Utils.formatText(L.CC_ONDUTY, keywords)+"\n";
					}
					message=message+L.G_ID+": " + String.valueOf(npc.getId()) + ", "+
							L.G_SHOP+": "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n";
				}
			}
			
		} else { //if command is from player
			String player_id = null;
			if (settings.getUUID_SUPPORT() == true) {
				player_id = player.getUniqueId().toString();
			} else {
				player_id = player.getName();
			}
			
			for (NPC npc: npcReg) {
				if (npc.hasTrait(HyperMerchantTrait.class)) {
					Owner owner = npc.getTrait(Owner.class);
					
					String owner_id = null;
					if (settings.getUUID_SUPPORT() == true) {
						if (owner.getOwnerId() != null) {
							owner_id = owner.getOwnerId().toString();
						}
					} else {
						owner_id = owner.getName();
					}
					
					if (owner_id != null) {
						if (owner_id.equals(player_id)){
							if (npc.getTrait(HyperMerchantTrait.class).offduty) {
								HashMap<String, String> keywords = new HashMap<String, String>();
								keywords.put("<npc>",  npc.getName());
								message=message+Utils.formatText(L.CC_OFFDUTY, keywords)+"\n";
							} else {
								HashMap<String, String> keywords = new HashMap<String, String>();
								keywords.put("<npc>",  npc.getName());
								message=message+Utils.formatText(L.CC_ONDUTY, keywords)+"\n";
							}
							message=message+L.G_ID+": " + String.valueOf(npc.getId()) + ", "+
									L.G_SHOP+": "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n";
						}
					}
				}
			}
		}
		
		return message;
	}
	

	@SuppressWarnings("static-access")
	public String GetInfo(CommandSender sender, int id) {
		String message = "";
		NPC npc = npcReg.getById(id);
		if (npc == null) {
			return null;
		}
		
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  npc.getName());
		
		if (npc.getTrait(HyperMerchantTrait.class).offduty) {
			message=message+Utils.formatText(L.CC_OFFDUTY, keywords)+"\n";
		} else {
			message=message+Utils.formatText(L.CC_ONDUTY, keywords)+"\n";
		}
		
		
		if (npc.getTrait(HyperMerchantTrait.class).forhire) {
			message=message+Utils.formatText(L.MC_FOR_HIRE, keywords)+"\n";
		} else {
			message=message+Utils.formatText(L.MC_NOT_FOR_HIRE, keywords)+"\n";
		}
		
		
		if (npc.getTrait(HyperMerchantTrait.class).rental) {
			message=message+Utils.formatText(L.MC_FOR_RENT, keywords)+"\n";
		} else {
			message=message+Utils.formatText(L.MC_NOT_FOR_RENT, keywords)+"\n";
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
		
		for (String name : hyperAPI().getPlayerShopList()) {
			if (name.equals(shopName) ) {
				ownerName = hyperAPI().getPlayerShop(name).getOwner().getName();
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
		Location l = location;
		int id = -1;
		//out.println(npc_type);
		if (npc_type == null){
			npc_type = "PLAYER";
		}
		
		if (l.getBlock().getType() != Material.AIR) {
			l = utils.getFirstBlockAboveGround(l);
		}

		this_npc = npcReg.createNPC(EntityType.valueOf(npc_type.toUpperCase()), npcname); 
		this_npc.addTrait(HyperMerchantTrait.class);

		this_npc.getTrait(HyperMerchantTrait.class).shop_name = shopname;
		this_npc.spawn(l);

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

	
	@SuppressWarnings("static-access")
	public String SetShop(int id, String shopname) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		
		if (shopname.isEmpty()) {
			HashMap<String, String> keywords = new HashMap<String, String>();
			keywords.put("<command>",  "setshop");
			message=message+Utils.formatText(L.MC_MISSING_SHOP_NAME, keywords);
			
		} else if (hyperAPI().getServerShopList().contains(shopname) || hyperAPI().getPlayerShopList().contains(shopname)) {
			this_npc.getTrait(HyperMerchantTrait.class).shop_name = shopname;
			
			HashMap<String, String> keywords = new HashMap<String, String>();
			keywords.put("<npc>",  this_npc.getName());
			keywords.put("<shop>",  this_npc.getTrait(HyperMerchantTrait.class).shop_name);
			message=message+Utils.formatText(L.TC_NPC_NULL_SHOP, keywords);
			
		} else {
			message=message+Utils.formatText(L.G_NEED_VALID_SHOP_NAME, null);
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
			//out.println("EXCEPTION: "+e);
        	return false;
        }
	}


	@SuppressWarnings("static-access")
	public String SetCommission(int id, double percentage) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).comission = percentage;

		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  this_npc.getName());
		keywords.put("<commission>",  String.valueOf(percentage));
		keywords.put("<percent>",  L.G_PERCENT);
		message=message+Utils.formatText(L.MC_COMMISSION, keywords);
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		return message;
	}


	@SuppressWarnings("static-access")
	public String SetRentalPrice(int id, double price) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).rental_price = price;

		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<rental>",  String.valueOf(price));
		message=message+Utils.formatText(L.MC_RENTAL_PRICE, keywords);
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		return message;
	}
	
	
	@SuppressWarnings("static-access")
	public String ToggleOffduty(int id) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).offduty = !this_npc.getTrait(HyperMerchantTrait.class).offduty;
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}

		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  String.valueOf(this_npc.getName()));
		
		if (this_npc.getTrait(HyperMerchantTrait.class).offduty) {
			message=Utils.formatText(L.CC_OFFDUTY, keywords);
		} else {
			message=Utils.formatText(L.CC_ONDUTY, keywords);
		}
		return message;
	}
	
	
	@SuppressWarnings("static-access")
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

		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  String.valueOf(this_npc.getName()));
		
		if (!this_npc.getTrait(HyperMerchantTrait.class).forhire) {
			message=Utils.formatText(L.MC_NOT_FOR_HIRE, keywords);
		} else {
			message=Utils.formatText(L.MC_FOR_HIRE, keywords);
		}
		
		
		return message;
	}
	
	
	@SuppressWarnings("static-access")
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

		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  String.valueOf(this_npc.getName()));
		
		if (!this_npc.getTrait(HyperMerchantTrait.class).rental) {
			message=Utils.formatText(L.MC_NOT_FOR_RENT, keywords);
		} else {
			message=Utils.formatText(L.MC_FOR_RENT, keywords);
		}
		
		
		return message;
	}

	
	@SuppressWarnings("static-access")
	public String SetGreeting(int id, String greeting) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).welcomeMsg = greeting;
		
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  String.valueOf(this_npc.getName()));
		
		if (greeting=="") {
			message=Utils.formatText(L.CC_REMOVE_GREETING, keywords);
		} else {
			message=Utils.formatText(L.CC_SET_GREETING, keywords);
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	@SuppressWarnings("static-access")
	public String SetFarewell(int id, String farewell) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).farewellMsg = farewell;
		
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  String.valueOf(this_npc.getName()));
		
		if (farewell=="") {
			message=Utils.formatText(L.CC_REMOVE_FAREWELL, keywords);
		} else {
			message=Utils.formatText(L.CC_SET_FAREWELL, keywords);
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	@SuppressWarnings("static-access")
	public String SetClosed(int id, String closed) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).closedMsg = closed;
		
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  String.valueOf(this_npc.getName()));
		
		if (closed=="") {
			message=Utils.formatText(L.CC_REMOVE_CLOSED, keywords);
		} else {
			message=Utils.formatText(L.CC_SET_CLOSED, keywords);
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	@SuppressWarnings("static-access")
	public String SetDenial(int id, String denial) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).denialMsg = denial;
		
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("<npc>",  String.valueOf(this_npc.getName()));
		
		if (denial=="") {
			message=Utils.formatText(L.CC_REMOVE_DENIAL, keywords);
		} else {
			message=Utils.formatText(L.CC_SET_DENIAL, keywords);
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}

	
	@SuppressWarnings("static-access")
	public String FireClerk(Player player) {
		String message = "";
		NPC npc = npcSel.getSelected(player);
		if (npc == null) {
			message =Utils.formatText(L.CC_NO_CLERK_SELECTED, null);
			return message;
		}
		String shopname = npc.getTrait(HyperMerchantTrait.class).shop_name;
		
		if (npc.getTrait(HyperMerchantTrait.class).hired) {
			if (hyperAPI().getPlayerShop(shopname).getOwner().getName().equals(player.getName())) {
				npc.getTrait(HyperMerchantTrait.class).hired = false;
				npc.getTrait(HyperMerchantTrait.class).forhire = true;
				Teleport(npc.getId(), utils.StringToLoc(npc.getTrait(HyperMerchantTrait.class).location));
				npc.getTrait(HyperMerchantTrait.class).shop_name = null;
				npc.getTrait(HyperMerchantTrait.class).location = null;

				HashMap<String, String> keywords = new HashMap<String, String>();
				keywords.put("<npc>",  String.valueOf(npc.getName()));
				
				message = Utils.formatText(L.CC_FIRED, keywords);
			} else {
				message =Utils.formatText(L.CC_NO_CLERK_SELECTED, null);
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

	
	@SuppressWarnings("static-access")
	public String CloseShop(Player player) {
		String message = "";
		NPC npc = npcSel.getSelected(player);
		if (npc == null) {
			message=Utils.formatText(L.CC_NO_CLERK_SELECTED, null);
			return message;
		}
		String shopname = npc.getTrait(HyperMerchantTrait.class).shop_name;
		
		
		if (npc.getTrait(HyperMerchantTrait.class).rented) {
			if (hyperAPI().getPlayerShop(shopname).getOwner().getName().equals(player.getName())) {
				npc.getTrait(HyperMerchantTrait.class).rented = false;
				npc.getTrait(HyperMerchantTrait.class).rental = true;
				Teleport(npc.getId(), utils.StringToLoc(npc.getTrait(HyperMerchantTrait.class).location));
				npc.getTrait(HyperMerchantTrait.class).location = null;
				
				HyperMerchantPlugin plugin = (HyperMerchantPlugin) Bukkit.getServer().getPluginManager().getPlugin("HyperMerchant");
				hyperAPI().getPlayerShop(shopname).setOwner(hyperAPI().getHyperPlayer(plugin.settings.getDEFAULT_RENTAL_OWNER()));
				
				HashMap<String, String> keywords = new HashMap<String, String>();
				keywords.put("<shop>",  shopname);
				message = Utils.formatText(L.CC_CLOSE_SHOP, keywords);
				
			} else {
				message = Utils.formatText(L.CC_NO_CLERK_SELECTED, null);
				return message;
			}
			
		} else {
			
			HashMap<String, String> keywords = new HashMap<String, String>();
			keywords.put("<command>",  "/closeshop");
			message = Utils.formatText(L.CC_COMMAND_INCOMPATIBLE, keywords);
			return message;
		}
		
		
		if (npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			npc.getTrait(HyperMerchantTrait.class).save(npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		
		return message;
	}
		
}
