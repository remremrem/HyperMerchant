package grokswell.hypermerchant;


//import java.util.ArrayList;
import grokswell.util.Language;
import grokswell.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCSelector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.account.HyperPlayer;

public class HyperMerchantCommand {
	CommandSender sender;
	HyperAPI hyperAPI;
	HyperPlayer hyplay;
	MerchantMethods merchmeth;
	int IDarg;
	Language L;
	
	@SuppressWarnings("static-access")
	HyperMerchantCommand(CommandSender snder, String[] args, HyperMerchantPlugin HMP) {
		IDarg=-1;
		merchmeth = new MerchantMethods();
		this.sender = snder;
		hyperAPI = HMP.hyperAPI;
		Player player = null;
		NPCSelector sel = CitizensAPI.getDefaultNPCSelector(); 
		List<String> argslist = Arrays.asList(args);
		L = HMP.language;
		
		if (!(sender instanceof Player)) {
			if ((!argslist.contains("--id")) && (!args[0].equals("list"))) {
				sender.sendMessage(ChatColor.YELLOW+L.CC_NO_NPC_SELECTED);
				return;
			}
		} else {
			player = (Player) snder;
			hyplay = hyperAPI.getHyperPlayer(player.getName());
		}
		
		try {
			NPC this_npc;
			StringBuilder buffer = new StringBuilder();
			String shopname = null;
			

			if (args[0].equals("list")) {
				String message = merchmeth.ListMerchants(null);
				sender.sendMessage(message);
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
					if (args.length>1){
						shopname = args[1];
					}
				}
				for(int i = 1; i < args.length; i++) {
				    buffer.append(' ').append(args[i]);
				}
			}
				
				
			//HMERCH INFO
		    if (args[0].equals("info")) {
				String message = merchmeth.GetInfo(sender, this_npc.getId());
				sender.sendMessage(message);
					
					
			//HMERCH SETSHOP
			} else if (args[0].equals("setshop")) {
				if (args.length>1) {
					String message = merchmeth.SetShop(this_npc.getId(), shopname);
					sender.sendMessage(message);
					return;

				} else {
					String shop_name=hyperAPI.getPlayerShop(hyplay);
					String message = merchmeth.SetShop(this_npc.getId(), shop_name);
					sender.sendMessage(message);
					return;
				}
			
				
			//HMERCH COMMISSION
			} else if (args[0].equals("commission")) {
				if (args.length>1) {
					try {
						String message = merchmeth.SetCommission(this_npc.getId(), Double.valueOf(args[1]));
						sender.sendMessage(message);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.YELLOW+args[1]+" "+L.MC_INVALID_COMMISSION);

					}
				} else {
					sender.sendMessage(ChatColor.YELLOW+L.MC_COMMISSION+" "+String.valueOf( this_npc.getTrait(HyperMerchantTrait.class).comission )+" %.");
				}
				return;
				
				
			//HMERCH RENTALPRICE
			} else if (args[0].equals("rentalprice")) {
				if (args.length>1) {
					try {
						String message = merchmeth.SetRentalPrice(this_npc.getId(), Double.valueOf(args[1]));
						sender.sendMessage(message);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.YELLOW+args[1]+" "+L.MC_INVALID_RENTAL_PRICE);

					}
				} else {
					sender.sendMessage(ChatColor.YELLOW+L.MC_RENTAL_PRICE+" "+String.valueOf( this_npc.getTrait(HyperMerchantTrait.class).rental_price ));
				}
				return;
			

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

			
			//HMERCH OFFDUTY
			} else if (args[0].equals("offduty") || args[0].equals("onduty")) {
				String message = merchmeth.ToggleOffduty(this_npc.getId());
					sender.sendMessage(message);

					
			//HMERCH FORHIRE
			} else if (args[0].equals("forhire")) {
				String message = merchmeth.ToggleForHire(this_npc.getId());
					sender.sendMessage(message);

							
			//HMERCH RENTAL
			} else if (args[0].equals("rental")) {
				String message = merchmeth.ToggleRental(this_npc.getId());
					sender.sendMessage(message);

					
				//ANY OTHER ARGUMENTS THAT ARE INVALID
			}else {
				sender.sendMessage(Utils.formatText(L.MC_SUBCOMMNADS, null));
				return;
			}
			return;
				

		//for any other exception not explicitly checked for
		} catch (Exception e){
			String subcommand = "";
			if (args.length>0) {
				subcommand = args[0];
			}
			HMP.getLogger().info("/hypermerchant "+subcommand+" call threw exception "+e);
			e.printStackTrace();

	        HashMap<String, String> keywords = new HashMap<String, String>();
			keywords.put("<command>",  "/hypermerchant");
			sender.sendMessage(Utils.formatText(L.CC_NO_NPC_SELECTED_2, keywords));
			return;
		}	
	}
}
