package grokswell.hypermerchant;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.NPCSelector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperAPI;

public class MerchantMethods {
	CommandSender sender;
	HyperAPI hyperAPI = new HyperAPI();
	ArrayList<String> shopnames;
	NPCRegistry npcReg;
	NPCSelector npcSel;
	
	MerchantMethods(CommandSender snder, String[] args, HyperMerchantPlugin HMP) {
		//HyperConomy hc = HyperConomy.hc;
		//ShopFactory hc_factory = hc.getShopFactory();
		//DataHandler hc_functions = hc.getDataFunctions();
		//String shopname = sname;
		this.sender = snder;
		Player player = null;
		

		npcSel = CitizensAPI.getDefaultNPCSelector();
		npcReg = CitizensAPI.getNPCRegistry();

		List<String> argslist = Arrays.asList(args);
		
		if (!(sender instanceof Player)) {
			if ((!argslist.contains("--id")) && (!args[0].equals("list"))) {
				sender.sendMessage(ChatColor.YELLOW+"You must have an NPC selected or use the --id flag to execute this commnad");
				return;
			}
		} else {
			//if a player sent the command, cast snder to player and make a list of shops this player owns.
			player = (Player) snder;
			shopnames = new ArrayList<String>();
			for (String sn : hyperAPI.getPlayerShopList()){
				if ( hyperAPI.getPlayerShop(sn).getOwner().getName().equals(player.getName()) ) {
					shopnames.add(sn);
				}
			}
		}
		
		try {
			NPC this_npc;
			StringBuilder buffer = new StringBuilder();
			String shopname = null;
			
			
			//CLERK LIST
			if (args[0].equals("list")) {
				for (NPC npc: CitizensAPI.getNPCRegistry()) {
					if (npc.hasTrait(HyperMerchantTrait.class)) {
						if (npc.data().get("owner") == player.getName()){
							if (npc.getTrait(HyperMerchantTrait.class).offduty) {
								sender.sendMessage(ChatColor.YELLOW+npc.getName()+" is OFFDUTY");
							} else {
								sender.sendMessage(ChatColor.YELLOW+npc.getName()+" is ONDUTY");
							}
							sender.sendMessage(ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n");
						}
					}
				}
				return;
				
			// CLERK HIRE	
			} else if (args[0].equals("hire")) {
				if (args.length < 2) {
					sender.sendMessage(ChatColor.YELLOW+"You must specify a name for your new clerk");
					return;
				}
				String clerk_name = args[1];
				String clerk_type = "player";
				String shop_name = null;

				if (args.length > 2) {
					if (args[2] == "-s") {
						shop_name = args[3];
					} else {
						clerk_type = args[2];
					}
				}
				if (args.length > 4) {
					if (args[3] == "-s") {
						shop_name = args[4];
					}
				}

				this_npc = npcReg.createNPC(EntityType.valueOf(clerk_type.toUpperCase()), clerk_name); 
				this_npc.addTrait(HyperMerchantTrait.class);
				
				
				String player_in_shop_name=hyperAPI.getPlayerShop(player);
				if (shop_name == null) {
					shop_name = player_in_shop_name;
				}
				
				
				if (shop_name == null) {
					sender.sendMessage(ChatColor.YELLOW+"You must specify a shop name, or be standing " +
										"inside of a shop to use the command "+ChatColor.RED+"/clerk hire <npc name> [npc type] -s [shop name].");
					return;
						
				} else if (shopnames.contains(shop_name)){
					this_npc.getTrait(HyperMerchantTrait.class).shop_name = shopname;
					if (shop_name == player_in_shop_name ) {
						this_npc.spawn(player.getLocation());
					} else {
						this_npc.spawn(hyperAPI.getShop(shop_name).getLocation1());
					}

				}
				
				return;
			
					
			// CLERK --id
			} else if (argslist.contains("--id")) {
				int id_index = argslist.indexOf("--id") + 1;
				this_npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[id_index]));
				
				if (id_index > 2) {
					if (args[0].equals("setshop")) {
						shopname = args[1];
					}
					for(int i = 1; i < id_index-1; i++) {
					    buffer.append(' ').append(args[i]);
					} 
				} else {
					if (args[0].equals("setshop")) {
						shopname = args[id_index+1];
					}
					for(int i = id_index+1; i < args.length; i++) {
					    buffer.append(' ').append(args[i]);
					}
				}

			}  else {
			
				this_npc = npcSel.getSelected(player);
				if (args[0].equals("setshop")) {
					shopname = args[1];
				}
				for(int i = 1; i < args.length; i++) {
				    buffer.append(' ').append(args[i]);
				}
			}
			
			
			//
			if (this_npc.hasTrait(HyperMerchantTrait.class)) {
				
				
				//Clerk INFO
				if (args[0].equals("info")) {
						if (this_npc.getTrait(HyperMerchantTrait.class).offduty) {
							sender.sendMessage("\n"+ChatColor.YELLOW+this_npc.getName()+" is OFFDUTY");
						} else {
							sender.sendMessage("\n"+ChatColor.YELLOW+this_npc.getName()+" is ONDUTY");
						}
						sender.sendMessage(ChatColor.YELLOW+"ID: " + String.valueOf(this_npc.getId()) + " , SHOP: "+ this_npc.getTrait(HyperMerchantTrait.class).shop_name +"\n");

						
						
					//Clerk SETSHOP
				} else if (args[0].equals("setshop")) {
					if (args.length>1) {
						
						if (shopnames.contains(shopname)) {
							if (hyperAPI.getPlayerShopList().contains(shopname)) {
								new HyperMerchantCommand(sender, args, HMP);
								return;
							}
						}
						
					} else {
						String in_shop_name=hyperAPI.getPlayerShop(player);
						if (in_shop_name.isEmpty()) {
							sender.sendMessage(ChatColor.YELLOW+"You must specify a shop name, or be standing " +
												"inside of a shop to use the command "+ChatColor.RED+"/clerk setshop.");
							return;
								
						} else if (shopnames.contains(in_shop_name)){
							new HyperMerchantCommand(sender, args, HMP);
							return;
						}
					}
				
					
					//Clerk TYPE
				} else if (args[0].equals("type")) {
					if (args.length == 2) {
						try {
							this_npc.setBukkitEntityType(EntityType.valueOf(args[1].toUpperCase()));
						} catch  (Exception e){
							sender.sendMessage(ChatColor.YELLOW+"Invalid Clerk type");
						}
						
					}
					return;
				
					
					//Clerk FIRE
				} else if (args[0].equals("fire")) {
					this_npc.destroy();
					return;
				
					
					//Clerk GREETING
				} else if (args[0].equals("greeting")) {
					new HyperMerchantCommand(sender, args, HMP);
					return;
					
					//Clerk FAREWELL
				} else if (args[0].equals("farewell")) {
					new HyperMerchantCommand(sender, args, HMP);
					return;
					
					//Clerk DENIAL
				} else if (args[0].equals("denial")) {
					new HyperMerchantCommand(sender, args, HMP);
					return;
					
					//Clerk CLOSED
				} else if (args[0].equals("closed")) {
					new HyperMerchantCommand(sender, args, HMP);
					return;
					
					//Clerk OFFDUTY
				} else if (args[0].equals("offduty") || args[0].equals("onduty")) {
					new HyperMerchantCommand(sender, args, HMP);
					return;
				
				
				//if the player doesn't have a hypermerchant npc selected
				} else {
					sender.sendMessage(ChatColor.YELLOW+"You must have one of your clerk NPCs " +
							"selected to use the command "+ChatColor.RED+"/clerk"+ChatColor.YELLOW+
							" without the --id flag");
					return;
				}
			}
			
			//for any other exception not explicitly checked for
		} catch (Exception e){
			HMP.getLogger().info("/clerk call threw exception "+e);
			sender.sendMessage(ChatColor.YELLOW+"You must have a hypermerchant NPC " +
								"selected to use the command "+ChatColor.RED+"/clerk.");
			return;
		}	
	}
}
