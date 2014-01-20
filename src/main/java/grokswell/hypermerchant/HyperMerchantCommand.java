package grokswell.hypermerchant;


//import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCSelector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperAPI;

public class HyperMerchantCommand {
	CommandSender sender;
	HyperAPI hyperAPI = new HyperAPI();
	MerchantMethods merchmeth;
	int IDarg;
	
	HyperMerchantCommand(CommandSender snder, String[] args, HyperMerchantPlugin HMP) {
		IDarg=-1;
		merchmeth = new MerchantMethods();
		this.sender = snder;
		Player player = null;
		NPCSelector sel = CitizensAPI.getDefaultNPCSelector(); 
		List<String> argslist = Arrays.asList(args);
		
		if (!(sender instanceof Player)) {
			if ((!argslist.contains("--id")) && (!args[0].equals("list"))) {
				sender.sendMessage(ChatColor.YELLOW+"You must have an NPC selected or use the --id flag to execute this commnad");
				return;
			}
		} else {
			player = (Player) snder;
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
				IDarg = Integer.parseInt(args[id_index]);
				this_npc = CitizensAPI.getNPCRegistry().getById(IDarg);
				
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
			
				this_npc = sel.getSelected(player);
				if (args[0].equals("setshop")) {
					shopname = args[1];
				}
				for(int i = 1; i < args.length; i++) {
				    buffer.append(' ').append(args[i]);
				}
			}
			
				//HMERCH SELECT
			if (this_npc.hasTrait(HyperMerchantTrait.class)) {
				if (args[0].equals("select")) {
					if (IDarg != -1) {
						merchmeth.Select(IDarg, player);
						
					} else if (args.length > 1){
						
						try {
							int id = Integer.parseInt(args[1]);
							merchmeth.Select(id, player);
							
						} catch (Exception e) {
							merchmeth.Select(args[1], player);
						}
					}
				
				
					//HMERCH INFO
			    } else if (args[0].equals("info")) {
					String message = merchmeth.GetInfo(sender, this_npc.getId());
					sender.sendMessage(message);
						
						
					//HMERCH SETSHOP
				} else if (args[0].equals("setshop")) {
					if (args.length>1) {
						String message = merchmeth.SetShop(this_npc.getId(), shopname);
						sender.sendMessage(message);
						return;

					} else {
						String shop_name=hyperAPI.getPlayerShop(player);
						String message = merchmeth.SetShop(this_npc.getId(), shop_name);
						sender.sendMessage(message);
						return;
					}
				
					
					//HMERCH COMISSION
				} else if (args[0].equals("comission")) {
					if (args.length>1) {
						String message = merchmeth.SetComission(this_npc.getId(), Double.valueOf(args[1]));
						sender.sendMessage(message);
					}
				
					
					//HMERCH GREETING
				} else if (args[0].equals("greeting")) {
					String greeting = "";
					if (args.length>1) {
						greeting = buffer.toString();
					}
					String message = merchmeth.SetGreeting(this_npc.getId(), greeting);
					sender.sendMessage(message);

				
					//HMERCH FAREWELL
				} else if (args[0].equals("farewell")) {
					String farewell = "";
					if (args.length>1) {
						farewell = buffer.toString();
					}
					String message = merchmeth.SetFarewell(this_npc.getId(), farewell);
					sender.sendMessage(message);

				
					//HMERCH DENIAL
				} else if (args[0].equals("denial")) {
					String denial = "";
					if (args.length>1) {
						denial = buffer.toString();
					}
					String message = merchmeth.SetDenial(this_npc.getId(), denial);
					sender.sendMessage(message);

				
					//HMERCH CLOSED
				} else if (args[0].equals("closed")) {
					String closed = "";
					if (args.length>1) {
						closed = buffer.toString();
					}
					String message = merchmeth.SetClosed(this_npc.getId(), closed);
					sender.sendMessage(message);

				
					//HMERCH OFFUDTY
				} else if (args[0].equals("offduty") || args[0].equals("onduty")) {
					String message = merchmeth.ToggleOffduty(this_npc.getId());
						sender.sendMessage(message);

						
					//ANY OTHER ARGUMENTS THAT ARE INVALID
				}else {
					sender.sendMessage(ChatColor.YELLOW+"Valid "+ChatColor.DARK_RED+"/hmerchant"+ChatColor.YELLOW+" subcommands are:\n" +
							ChatColor.RED+"setshop , offduty , greeting , farewell , denial , closed.");
					return;
				}
				return;
				
				
				//if the player doesn't have a hypermerchant npc selected
			} else {
				sender.sendMessage(ChatColor.YELLOW+"You must have a hypermerchant npc " +
						"selected to use the command "+ChatColor.RED+"/hmerchant.");
				return;
			}
			
			//for any other exception not explicitly checked for
		} catch (Exception e){
			HMP.getLogger().info("/hypermerchant call threw exception "+e);
			sender.sendMessage(ChatColor.YELLOW+"You must have a hypermerchant npc " +
								"selected to use the command "+ChatColor.RED+"/hmerchant.");
			return;
		}	
	}
}
