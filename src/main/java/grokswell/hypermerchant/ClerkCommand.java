package grokswell.hypermerchant;

import static java.lang.System.out;

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

public class ClerkCommand {
	CommandSender sender;
	HyperAPI hyperAPI = new HyperAPI();
	MerchantMethods merchmeth;
	int IDarg;
	
	ClerkCommand(CommandSender snder, String[] args, HyperMerchantPlugin HMP) {
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
		
		//try {
			NPC this_npc;
			StringBuilder buffer = new StringBuilder();
			String shopname = null;
			
			
			//CLERK LIST
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
				
			//CLERK SELECT
			} else if (args[0].equals("select")) {
				if (IDarg != -1) {
					merchmeth.Select(IDarg, player);
					return;
				} else if (args.length > 1){
					
					try {
						int id = Integer.parseInt(args[1]);
						NPC npc = CitizensAPI.getNPCRegistry().getById(id);
						String shop_name = npc.getTrait(HyperMerchantTrait.class).shop_name;
						//MAKE SURE THE NPC WORKS FOR THIS PLAYER
						if (hyperAPI.getPlayerShop(shop_name).getOwner().getName() == player.getName()) {
							merchmeth.Select(id, player);
						}
						return;
						
					} catch (Exception e) {
						//do nothing on exception
					}
					sender.sendMessage(ChatColor.YELLOW+"You must specify one of your clerks by ID. Use "+ChatColor.RED+
								"/clerk list "+ChatColor.YELLOW+"to your clerk's ID numbers.");
					return;
				}
				return;
			//CLERK HIRE
			} else if (args[0].equals("hire")) {
				String npctype;
				if (args.length > 3 && args[2] == "-s") {
					shopname = args[3];
					npctype="PLAYER";
				} else if (args.length > 4 && args[3] == "-s") {
					shopname = args[4];
					npctype=args[2].toUpperCase();
				} else if (args.length == 3) {
					npctype=args[2].toUpperCase();
					shopname = hyperAPI.getPlayerShop(player);
				} else {
					npctype="PLAYER";
					shopname = hyperAPI.getPlayerShop(player);
				}
				out.println("shopname: "+shopname);
				if (args.length < 2) {
					sender.sendMessage(ChatColor.YELLOW+"You must provide a name for your new clerk.");
					return;
				}
				int npcid = merchmeth.Hire(args[1], npctype, shopname, hyperAPI.getPlayerShop(shopname).getLocation1());
				if ( npcid != -1 ) {
					int clerk_count = HMP.playerData.getPlayerData().getInt(player.getName()+".clerkcount");
					HMP.playerData.savePlayerData(player.getName()+".clerkcount", clerk_count+1);
					player.performCommand("npc select "+npcid);
				}
				//sender.sendMessage(ChatColor.YELLOW+"You are now off duty. Other players cannot click on you to trade with your shop.");
				return;
			//END CLERK HIRE


			//check if the npc is being spcified by it's citizens id number.
			} else if (argslist.contains("--id")) {
				int id_index = argslist.indexOf("--id") + 1;
				IDarg = Integer.parseInt(args[id_index]);
				this_npc = CitizensAPI.getNPCRegistry().getById(IDarg);
				
				//MAKE SURE THIS NPC WORKS FOR THIS PLAYER
				out.println("OWNER: "+merchmeth.GetEmployer(IDarg));
				if ( !merchmeth.GetEmployer(IDarg).equals(player.getName()) ) {
					sender.sendMessage(ChatColor.YELLOW+"You may only perform this command on an NPC that works for you.");
					return;
				}
				
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
	
			//CHECK IF PLAYER HAS A CLERK SELECTED
			}  else {
			
				this_npc = sel.getSelected(player);

				//out.println("OWNER2: "+merchmeth.GetEmployer(this_npc.getId()));
				//MAKE SURE THIS NPC WORKS FOR THIS PLAYER
				if ( !merchmeth.GetEmployer(this_npc.getId()).equals(player.getName()) ) {
					sender.sendMessage(ChatColor.YELLOW+"You may only perform this command on an NPC that works for you.");
					return;
				}
				
				
				if (args[0].equals("setshop")) {
					shopname = args[1];
				}
				for(int i = 1; i < args.length; i++) {
				    buffer.append(' ').append(args[i]);
				}
			}

			//CLERK FIRE
			if (args[0].equals("fire")) {
				String npc_name = this_npc.getName();
				if (merchmeth.Fire(this_npc.getId())){
					int clerk_count = HMP.playerData.getPlayerData().getInt(player.getName()+".clerkcount");
					HMP.playerData.savePlayerData(player.getName()+".clerkcount", clerk_count-1);
					
					sender.sendMessage(ChatColor.YELLOW+npc_name+" has been fired!");
				}
				return;
				
	
			} else if (this_npc.hasTrait(HyperMerchantTrait.class)) {
				
					//CLERK INFO
			    if (args[0].equals("info")) {
					String message = merchmeth.GetInfo(sender, this_npc.getId());
					sender.sendMessage(message);
					
					//CLERK TP
			    } else if (args[0].equals("tp")) {
			    	String player_in_shop = hyperAPI.getPlayerShop(player);
			    
					if (player_in_shop != null) {
						if (hyperAPI.getPlayerShop(player_in_shop).getOwner().getName() 
								== player.getName()) {
							
							merchmeth.Teleport(this_npc.getId(), player.getLocation());
						}
					}
						
					//CLERK SETSHOP
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
				
					
					//CLERK COMISSION
				//} else if (args[0].equals("comission")) {
				//	if (args.length>1) {
				//		String message = merchmeth.SetComission(this_npc.getId(), Double.valueOf(args[1]));
				//		sender.sendMessage(message);
				//	}
				
					
					//CLERK GREETING
				} else if (args[0].equals("greeting")) {
					String greeting = "";
					if (args.length>1) {
						greeting = buffer.toString();
					}
					String message = merchmeth.SetGreeting(this_npc.getId(), greeting);
					sender.sendMessage(message);

				
					//CLERK FAREWELL
				} else if (args[0].equals("farewell")) {
					String farewell = "";
					if (args.length>1) {
						farewell = buffer.toString();
					}
					String message = merchmeth.SetFarewell(this_npc.getId(), farewell);
					sender.sendMessage(message);

				
					//CLERK DENIAL
				} else if (args[0].equals("denial")) {
					String denial = "";
					if (args.length>1) {
						denial = buffer.toString();
					}
					String message = merchmeth.SetDenial(this_npc.getId(), denial);
					sender.sendMessage(message);

				
					//CLERK CLOSED
				} else if (args[0].equals("closed")) {
					String closed = "";
					if (args.length>1) {
						closed = buffer.toString();
					}
					String message = merchmeth.SetClosed(this_npc.getId(), closed);
					sender.sendMessage(message);

				
					//CLERK OFFUDTY
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
		//} catch (Exception e){
		//	HMP.getLogger().info("/hypermerchant call threw exception "+e);
		//	sender.sendMessage(ChatColor.YELLOW+"You must have a hypermerchant npc " +
		//						"selected to use the command "+ChatColor.RED+"/hmerchant.");
		//	return;
		//}					
	}
}
