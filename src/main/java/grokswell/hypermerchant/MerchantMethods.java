package grokswell.hypermerchant;

import static java.lang.System.out;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.NPCSelector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import regalowl.hyperconomy.HyperAPI;


public class MerchantMethods {
	NPCRegistry npcReg = CitizensAPI.getNPCRegistry();
	NPCSelector npcSel = CitizensAPI.getDefaultNPCSelector();
	HyperAPI hyperAPI = new HyperAPI();

	public String ListMerchants(Player player) {
		String message = "";
		if (player == null) {
			for (NPC npc: npcReg) {
				if (npc.hasTrait(HyperMerchantTrait.class)) {
					if (npc.getTrait(HyperMerchantTrait.class).offduty) {
						message.concat(ChatColor.YELLOW+npc.getName()+" is OFFDUTY\n");
					} else {
						message.concat(ChatColor.YELLOW+npc.getName()+" is ONDUTY\n");
					}
					message.concat(ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n");
				}
			}
		} else {
			for (NPC npc: npcReg) {
				if (npc.hasTrait(HyperMerchantTrait.class)) {
					if (npc.data().get("owner") == player.getName()){
						if (npc.getTrait(HyperMerchantTrait.class).offduty) {
							message.concat(ChatColor.YELLOW+npc.getName()+" is OFFDUTY\n");
						} else {
							message.concat(ChatColor.YELLOW+npc.getName()+" is ONDUTY\n");
						}
						message.concat(ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n");
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
			message.concat(ChatColor.YELLOW+npc.getName()+" is OFFDUTY\n");
		} else {
			message.concat(ChatColor.YELLOW+npc.getName()+" is ONDUTY\n");
		}
		
		message.concat(ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n");
		return message;
	}
	

	public String GetEmployer(int id) {
		NPC npc = npcReg.getById(id);
		if (npc == null) {
			return null;
		}
		
		String ownerName = null;
		out.println("ID: "+id);
		String shopName = GetShop(id);
		out.println("shopname: "+shopName);
		for (String name : hyperAPI.getPlayerShopList()) {
			out.println("name: "+name);
			if (name.equals(shopName) ) {
				ownerName = hyperAPI.getPlayerShop(name).getOwner().getName();
				out.println("shop: "+hyperAPI.getPlayerShop(name));
				out.println("owner: "+hyperAPI.getPlayerShop(name).getOwner());
				out.println("ownername: "+ownerName);
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
		out.println(npc_type);
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

	
	public boolean Select(String npcname, Player player) {
		player.performCommand("npc select "+npcname);
		return true;
	}

	
	public boolean Select(int id, Player player) {
		player.performCommand("npc select "+Integer.toString(id));
		return true;
	}

	
	public String SetShop(int id, String shopname) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		

		if (shopname.isEmpty()) {
			message.concat(ChatColor.YELLOW+"You must specify a shop name, or be standing " +
								"inside of a shop to use the command "+ChatColor.RED+"setshop.");
			
		} else if (hyperAPI.getServerShopList().contains(shopname) || hyperAPI.getPlayerShopList().contains(shopname)) {
			this_npc.getTrait(HyperMerchantTrait.class).shop_name = shopname;
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" has been assigned to shop "+
								this_npc.getTrait(HyperMerchantTrait.class).shop_name);
			
		} else {
			message.concat(ChatColor.YELLOW+"You must provide a valid shop name. " +
					"Use "+ChatColor.RED+"/remoteshoplist "+ChatColor.YELLOW+ 
							"or "+ChatColor.RED+"/rslist "+ChatColor.YELLOW+
							"for valid shop names. Use exact spelling.");	
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


	public String SetComission(int id, double percentage) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).comission = percentage;
		message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" now recieves a "+percentage+"% comission");
		
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
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now off duty.");
		} else {
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now on duty.");
		}
		return message;
	}

	
	public String SetGreeting(int id, String greeting) {
		String message = "";
		NPC this_npc = npcReg.getById(id);
		this_npc.getTrait(HyperMerchantTrait.class).welcomeMsg = greeting;
		
		if (greeting=="") {
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a greeting to customers.");
		} else {
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" greeting message has been updated.");
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
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a farewell to customers.");
		} else {
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" farewell message has been updated.");
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
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a closed message to customers.");
		} else {
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" closed message has been updated.");
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
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a denial message to customers.");
		} else {
			message.concat(ChatColor.YELLOW+"NPC "+this_npc.getName()+" denial message has been updated.");
		}
		
		if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
			this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
		}
		
		return message;
	}
		
}
