package grokswell.hypermerchant;

//import static java.lang.System.out;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.NPCSelector;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.trait.LookClose;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.shop.PlayerShop;

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
		NPCRegistry npcReg = CitizensAPI.getNPCRegistry();
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
						String shop_name = npc.getTrait(HyperMerchantTrait.class).shop_name;
						//MAKE SURE THE NPC WORKS FOR THIS PLAYER
						if (hyperAPI.getPlayerShopList().contains(shop_name)){
							if (hyperAPI.getPlayerShop(shop_name).getOwner().getName().equals(player.getName()) ) {
								if (npc.getTrait(HyperMerchantTrait.class).offduty) {
									sender.sendMessage(ChatColor.YELLOW+npc.getName()+" is OFFDUTY");
								} else {
									sender.sendMessage(ChatColor.YELLOW+npc.getName()+" is ONDUTY");
								}
								sender.sendMessage(ChatColor.YELLOW+"ID: " + String.valueOf(npc.getId()) + " , SHOP: "+ npc.getTrait(HyperMerchantTrait.class).shop_name +"\n");
							}
						}
					}
				}
				return;
				
			//CLERK SELECT
			} else if (args[0].equals("select")) {
				if (args.length > 1){
					int id;
					try {
						id = Integer.parseInt(args[1]);
						NPC npc = CitizensAPI.getNPCRegistry().getById(id);
						String shop_name = npc.getTrait(HyperMerchantTrait.class).shop_name;
						//MAKE SURE THE NPC WORKS FOR THIS PLAYER
						if (hyperAPI.getPlayerShop(shop_name).getOwner().getName().equals(player.getName()) ) {
							merchmeth.Select(id, player,HMP);
						}
						return;
					} catch (Exception e) {
						String npcname = args[1];
						for (NPC npc : npcReg) {
							if (npc.hasTrait(HyperMerchantTrait.class) && npc.getName().toLowerCase().equals(npcname.toLowerCase())) {
								String shop_name = npc.getTrait(HyperMerchantTrait.class).shop_name;
								try {
									if (hyperAPI.getPlayerShop(shop_name).getOwner().getName().equals(player.getName()) ) {
										merchmeth.Select(npc.getId(), player,HMP);
										return;
									}
								} catch (Exception NullPointerException) {
									//do nothing
								}
							}
						}
					}
				}
				sender.sendMessage(ChatColor.YELLOW+"You must specify one of your clerks by ID or name. Use "+ChatColor.RED+
						"/clerk list "+ChatColor.YELLOW+"to view your clerk's names and ID numbers.");
				return;
				
			//CLERK HIRE
			} else if (args[0].equals("hire")) {
				
				int clerkcount = merchmeth.GetClerkCount(player);
				if (clerkcount >= HMP.settings.getMAX_NPCS_PER_PLAYER()) {
					sender.sendMessage(ChatColor.YELLOW+"You already have the maximum number of clerks you may hire.");
					return;
				}
				ArrayList<String> shopnames = ListPlayersShops(player.getName());
				if (shopnames.size() == 0) {
					sender.sendMessage(ChatColor.YELLOW+"It seems you may not own any shops.");
					return;
				}
				
				String npctype;
				if (args.length > 3 && args[2] == "-s") {
					shopname = args[3];
					if (!shopnames.contains(shopname)) {
						sender.sendMessage(ChatColor.YELLOW+"You do not seem to own a shop named "+shopname);
						return;
					}
					npctype="PLAYER";
				} else if (args.length > 4 && args[3] == "-s") {
					shopname = args[4];
					if (!shopnames.contains(shopname)) {
						sender.sendMessage(ChatColor.YELLOW+"You do not seem to own a shop named "+shopname);
						return;
					}
					npctype=args[2].toUpperCase();
				} else if (args.length == 3) {
					npctype=args[2].toUpperCase();
					shopname = hyperAPI.getPlayerShop(player);
					if (!shopnames.contains(shopname)) {
						shopname = shopnames.get(0);
					}
				} else {
					npctype="PLAYER";
					shopname = hyperAPI.getPlayerShop(player);
					if (!shopnames.contains(shopname)) {
						shopname = shopnames.get(0);
					}
				}
				
				//if (shopname.equals("")){
				//	sender.sendMessage(ChatColor.YELLOW+"It seems you may not own any shops.");
				//	return;
				//}
				//out.println("shopname: "+shopname);
				if (args.length < 2) {
					sender.sendMessage(ChatColor.YELLOW+"You must provide a name for your new clerk.");
					return;
				}
				
				//check if name is blacklisted
				if (HMP.name_blacklist.contains(args[1])){
					sender.sendMessage(ChatColor.YELLOW+"You may not use the name "+args[1]);
					return;
				}
				
				//check if npc type is blacklisted
				if (HMP.type_blacklist.contains(npctype.toUpperCase())){
					sender.sendMessage(ChatColor.YELLOW+"You may not use the npc type "+npctype);
					return;
				}

				int npcid = merchmeth.Hire(args[1], npctype, shopname, hyperAPI.getPlayerShop(shopname).getLocation1());
				if ( npcid != -1 ) {
					int clerk_count = HMP.playerData.getPlayerData().getInt(player.getName()+".clerkcount");
					HMP.playerData.savePlayerData(player.getName()+".clerkcount", clerk_count+1);
					player.performCommand("npc select "+npcid);
					NPC npc = npcReg.getById(npcid);
					npc.getTrait(Owner.class).setOwner(player.getName());
					npc.getTrait(LookClose.class).lookClose(true);
				}

				return;
			//END CLERK HIRE


			//check if the npc is being specified by it's citizens id number.
			} else if (argslist.contains("--id")) {
				int id_index = argslist.indexOf("--id") + 1;
				IDarg = Integer.parseInt(args[id_index]);
				this_npc = CitizensAPI.getNPCRegistry().getById(IDarg);
				//MAKE SURE THIS NPC WORKS FOR THIS PLAYER
				if ( merchmeth.GetEmployer(IDarg)==null ){
					sender.sendMessage(ChatColor.YELLOW+"You may only perform this command on an NPC that works for you.");
					return;
				}
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

				//out.println("selected npc: "+sel.getSelected(player));
				if (this_npc == null) {
					sender.sendMessage(ChatColor.YELLOW+"You must have a clerk selected or specify one using the [--id] flag.");
					return;
				}
				//MAKE SURE THIS NPC WORKS FOR THIS PLAYER
				if (this_npc.hasTrait(HyperMerchantTrait.class)) {
					if (merchmeth.GetEmployer(this_npc.getId())==null) {
						sender.sendMessage(ChatColor.YELLOW+"You may only perform this command on a clerk that works for you.");
						return;
					}
					else if ( !merchmeth.GetEmployer(this_npc.getId()).equals(player.getName()) ) {
						sender.sendMessage(ChatColor.YELLOW+"You may only perform this command on a clerk that works for you.");
						return;
					}
				}
				
//				if (args[0].equals("setshop")) {
//					if (args.length == 2) {
//						shopname = args[1];
//					} else {
//						shopname = hyperAPI.getPlayerShop(player);
//					}
//				}
				for(int i = 1; i < args.length; i++) {
				    buffer.append(' ').append(args[i]);
				}
			}

			//CLERK FIRE
			if (args[0].equals("fire")) {
				String npc_name = this_npc.getName();
				if (this_npc.getTrait(HyperMerchantTrait.class).rented) {
					String message = merchmeth.FireClerk(player);
					player.sendMessage(message);
				}
				else if (merchmeth.Fire(this_npc.getId())){
					int clerk_count = HMP.playerData.getPlayerData().getInt(player.getName()+".clerkcount");
					HMP.playerData.savePlayerData(player.getName()+".clerkcount", clerk_count-1);
					
					sender.sendMessage(ChatColor.YELLOW+npc_name+" has been fired!");
				}
				return;
				
	
			} else if (this_npc.hasTrait(HyperMerchantTrait.class)) {
				
				//CLERK INFO
			    //if (args[0].equals("info")) {
				//	String message = merchmeth.GetInfo(sender, this_npc.getId());
				//	sender.sendMessage(message);
					
				//CLERK TP
			    if (args[0].equals("tp")) {
			    
					if (!HMP.settings.getNPC_IN_SHOP_ONLY())  {
						merchmeth.Teleport(this_npc.getId(), player.getLocation());
					} else {
						String player_in_shop = hyperAPI.getPlayerShop(player);
						//out.println("player in shop: "+player_in_shop);
						if (!player_in_shop.equals("")) {
							if ( hyperAPI.getPlayerShop(player_in_shop).getOwner().getName().equals(player.getName()) ) {
								merchmeth.Teleport(this_npc.getId(), player.getLocation());
							} else {
								sender.sendMessage(ChatColor.YELLOW+"You must be standing inside of a shop that you own.");
							}
						} else {
							sender.sendMessage(ChatColor.YELLOW+"You must be standing inside of a shop that you own.");
						}
					}
					return;	
					
				//CLERK SETSHOP
				} else if (args[0].equals("setshop")) {
					if (args.length>1) {
						String message = merchmeth.SetShop(this_npc.getId(), args[1]);
						sender.sendMessage(message);
						return;

					} else {
						String shop_name=hyperAPI.getPlayerShop(player);
						String message = merchmeth.SetShop(this_npc.getId(), shop_name);
						sender.sendMessage(message);
						return;
					}

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

						
				//CLERK TYPE
				} else if (args[0].equals("type")) {
					
					if (args.length>1) {

						//check if npc type is blacklisted
						if (HMP.type_blacklist.contains(args[1].toUpperCase())){
							sender.sendMessage(ChatColor.YELLOW+"You may not use the npc type "+args[1]);
							return;
						}
						merchmeth.SetType(this_npc.getId(), args[1]);
						
					} else {
						sender.sendMessage(ChatColor.YELLOW+"You must specify a valid npc type.");
					}

					
				//CLERK OUTFIT
				} else if (args[0].equals("outfit")) {
					merchmeth.Equip(this_npc.getId(), player,HMP);

						
					//ANY OTHER ARGUMENTS THAT ARE INVALID
				}else {
					sender.sendMessage(ChatColor.YELLOW+"Valid "+ChatColor.DARK_RED+"/clerk"+ChatColor.YELLOW+" subcommands are:\n" +
							ChatColor.RED+"hire, fire, info, select, setshop , offduty , greeting , farewell , denial , closed, outfit, type.");
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
	
	private ArrayList<String> ListPlayersShops(String playername){
		ArrayList<String> shopnames = new ArrayList<String>();
		for (String sname : hyperAPI.getPlayerShopList()) {
			if (hyperAPI.getPlayerShop(sname).getOwner().getName().equals(playername)){
				shopnames.add(sname);
			}
		}
		return shopnames;
	}
}
