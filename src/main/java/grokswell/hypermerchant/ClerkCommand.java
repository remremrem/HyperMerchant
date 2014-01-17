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

import regalowl.hyperconomy.EconomyManager;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;

//#Hire a clerk to work in the shop you are standing in
///clerk hire <NAME> -t <TYPE> -s <SHOPNAME>
//
//#Teleport the selected clerk to the location you are standing
///clerk tp
//
//#Select a clerk by name
///clerk select [NAME or ID]
//
//#fire the currently selected clerk, or by name.
///clerk fire [NAME or ID]
//
//#toggle a clerk to offduty
///clerk offduty --id
//
//#move your clerk to a different shop that you own
///clerk setshop <SHOPNAME> --id
//
//#List your clerks by ID and name
///clerk list
//
//#set clerk's greeting message
///clerk greeting <MESSAGE> --id
//
//#set clerk's farewell message
///clerk farewell <MESSAGE> --id
//
//#set clerk's closed message
///clerk closed <MESSAGE> --id
//
//#set clerk's denial message
///clerk denial <MESSAGE> --id

public class ClerkCommand {
	CommandSender sender;
	HyperAPI hyperAPI = new HyperAPI();
	ArrayList<String> shopnames;
	NPCRegistry npcReg;
	NPCSelector npcSel;
	
	ClerkCommand(CommandSender snder, String[] args, HyperMerchantPlugin HMP) {
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
			
			
			
			if (args[0].equals("list")) {
				for (NPC npc: CitizensAPI.getNPCRegistry()) {
					if (npc.hasTrait(HyperMerchantTrait.class)) {
						if (npc.getTrait(HyperMerchantTrait.class).offduty) {
							sender.sendMessage(ChatColor.YELLOW+npc.getName()+" is OFFDUTY");
						} else {
							sender.sendMessage(ChatColor.YELLOW+npc.getName()+" is ONDUTY");
						}
						sender.sendMessage(ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n");
					}
				}
				return;
			
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
						HyperConomy hc = HyperConomy.hc;
						EconomyManager ecoMan = hc.getEconomyManager();
						ArrayList<String> shoplist = ecoMan.listShops();
						
						if (shoplist.contains(shopname)) {
							//this_npc.getTrait(HyperMerchantTrait.class).trait_key.setString("shop_name",args[0]);
							this_npc.getTrait(HyperMerchantTrait.class).shop_name = shopname;
							if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
								this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
							}
							sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" has been assigned to shop "+
												this_npc.getTrait(HyperMerchantTrait.class).shop_name);
							return;
							
						} else {
							sender.sendMessage(ChatColor.YELLOW+"You must provide one valid shop name. " +
									"Use "+ChatColor.RED+"/remoteshoplist "+ChatColor.YELLOW+ 
											"or "+ChatColor.RED+"/rslist "+ChatColor.YELLOW+
											"for valid shop names. Use exact spelling.");
							return;
						}
						
					} else {
						String name=hyperAPI.getPlayerShop(player);
						if (name.isEmpty()) {
							sender.sendMessage(ChatColor.YELLOW+"You must specify a shop name, or be standing " +
												"inside of a shop to use the command "+ChatColor.RED+"/setshop.");
							return;
								
						} else {
							this_npc.getTrait(HyperMerchantTrait.class).shop_name = name;
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
							sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" has been assigned to shop "+
												this_npc.getTrait(HyperMerchantTrait.class).shop_name);
							return;
						}
					}
				
					
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
							"selected to use the command "+ChatColor.RED+"/clerk.");
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
