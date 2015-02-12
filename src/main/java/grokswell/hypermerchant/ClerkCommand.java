package grokswell.hypermerchant;

//import static java.lang.System.out;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import regalowl.hyperconomy.HyperAPI;

import grokswell.util.HyperToBukkit;
import grokswell.util.Language;
import grokswell.util.Utils;

public class ClerkCommand {
	CommandSender sender;
	HyperAPI hyperAPI;
	HyperPlayer hyplay;
	MerchantMethods merchmeth;
	int IDarg;
	HyperToBukkit hypBuk;
	Language L;
	
	@SuppressWarnings("static-access")
	ClerkCommand(CommandSender snder, String[] args, HyperMerchantPlugin HMP) {
		IDarg=-1;
		hypBuk=new HyperToBukkit();
		merchmeth = new MerchantMethods();
		this.sender = snder;
		hyperAPI = HMP.hyperAPI;
		Player player = null;
		NPCSelector sel = CitizensAPI.getDefaultNPCSelector();
		NPCRegistry npcReg = CitizensAPI.getNPCRegistry();
		List<String> argslist = Arrays.asList(args);
		L=HMP.language;
		
		if (!(sender instanceof Player)) {
			if ((!argslist.contains("--id")) && (!args[0].equals("list"))) {
				sender.sendMessage(Utils.formatText(L.CC_NO_NPC_SELECTED, null));
				return;
			}
		} else {
			player = (Player) snder;
			hyplay = hyperAPI.getHyperPlayer(player.getName());
		}
		
			NPC this_npc;
			StringBuilder buffer = new StringBuilder();
			String shopname = null;
			
			//CLERK LIST
			if (args[0].equals("list")) {
				String message = merchmeth.ListMerchants(player);
				sender.sendMessage(message);
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
				sender.sendMessage(Utils.formatText(L.CC_SPECIFY_CLERK_NAME, null));
				return;
				
			//CLERK HIRE
			} else if (args[0].equals("hire")) {
				
				int clerkcount = merchmeth.GetClerkCount(player);
				if (clerkcount >= HMP.settings.getMAX_NPCS_PER_PLAYER()) {
					sender.sendMessage(Utils.formatText(L.CC_CLERK_LIMIT_REACHED, null));
					return;
				}
				ArrayList<String> shopnames = ListPlayersShops(player.getName());
				if (shopnames.size() == 0) {
					sender.sendMessage(Utils.formatText(L.CC_NO_SHOPS_OWNED, null));
					return;
				}
				
				String npctype;
				if (args.length > 3 && args[2] == "-s") {
					shopname = args[3];
					if (!shopnames.contains(shopname)) {
						HashMap<String, String> keywords = new HashMap<String, String>();
						keywords.put("<shop>",  shopname);
						sender.sendMessage(Utils.formatText(L.CC_SHOP_NOT_OWNED, keywords));
						return;
					}
					npctype="PLAYER";
				} else if (args.length > 4 && args[3] == "-s") {
					shopname = args[4];
					if (!shopnames.contains(shopname)) {
						HashMap<String, String> keywords = new HashMap<String, String>();
						keywords.put("<shop>",  shopname);
						sender.sendMessage(Utils.formatText(L.CC_SHOP_NOT_OWNED, keywords));
						return;
					}
					npctype=args[2].toUpperCase();
				} else if (args.length == 3) {
					npctype=args[2].toUpperCase();
					shopname = hyperAPI.getPlayerShop(hyplay);
					if (!shopnames.contains(shopname)) {
						shopname = shopnames.get(0);
					}
				} else {
					npctype="PLAYER";
					shopname = hyperAPI.getPlayerShop(hyplay);
					if (!shopnames.contains(shopname)) {
						shopname = shopnames.get(0);
					}
				}

				if (args.length < 2) {
					sender.sendMessage(Utils.formatText(L.CC_NAME_REQUIRED, null));
					return;
				}
				
				//check if name is blacklisted
				if (HMP.name_blacklist.contains(args[1])){
					HashMap<String, String> keywords = new HashMap<String, String>();
					keywords.put("<name>",  args[1]);
					sender.sendMessage(Utils.formatText(L.CC_DISALLOWED_NAME, keywords));
					return;
				}
				
				//check if npc type is blacklisted
				if (HMP.type_blacklist.contains(npctype.toUpperCase())){
					HashMap<String, String> keywords = new HashMap<String, String>();
					keywords.put("<type>",  npctype);
					sender.sendMessage(Utils.formatText(L.CC_DISALLOWED_TYPE, keywords));
					return;
				}

				int npcid = merchmeth.Hire(args[1], npctype, shopname, hypBuk.getLocation(hyperAPI.getPlayerShop(shopname).getLocation1()));
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
					sender.sendMessage(Utils.formatText(L.CC_NOT_YOUR_NPC, null));
					return;
				}
				if ( !merchmeth.GetEmployer(IDarg).equals(player.getName()) ) {
					sender.sendMessage(Utils.formatText(L.CC_NOT_YOUR_NPC, null));
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
					sender.sendMessage(Utils.formatText(L.CC_NO_NPC_SELECTED, null));
					return;
				}
				//MAKE SURE THIS NPC WORKS FOR THIS PLAYER
				if (this_npc.hasTrait(HyperMerchantTrait.class)) {
					if (merchmeth.GetEmployer(this_npc.getId())==null) {
						sender.sendMessage(Utils.formatText(L.CC_NOT_YOUR_NPC, null));
						return;
					}
					else if ( !merchmeth.GetEmployer(this_npc.getId()).equals(player.getName()) ) {
						sender.sendMessage(Utils.formatText(L.CC_NOT_YOUR_NPC, null));
						return;
					}
				}
				

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
					
					HashMap<String, String> keywords = new HashMap<String, String>();
					keywords.put("<npc>",  npc_name);
					sender.sendMessage(Utils.formatText(L.CC_FIRED, keywords));
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
						String player_in_shop = hyperAPI.getPlayerShop(hyplay);
						//out.println("player in shop: "+player_in_shop);
						if (!player_in_shop.equals("")) {
							if ( hyperAPI.getPlayerShop(player_in_shop).getOwner().getName().equals(player.getName()) ) {
								merchmeth.Teleport(this_npc.getId(), player.getLocation());
							} else {
								sender.sendMessage(Utils.formatText(L.G_MUST_BE_IN_OWN_SHOP, null));
							}
						} else {
							sender.sendMessage(Utils.formatText(L.G_MUST_BE_IN_OWN_SHOP, null));
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
						String shop_name=hyperAPI.getPlayerShop(hyplay);
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
							
							HashMap<String, String> keywords = new HashMap<String, String>();
							keywords.put("<type>",  args[1]);
							sender.sendMessage(Utils.formatText(L.CC_DISALLOWED_TYPE, keywords));
							return;
						}
						merchmeth.SetType(this_npc.getId(), args[1]);
						
					} else {
						sender.sendMessage(Utils.formatText(L.CC_SPECIFY_NPC_TYPE, null));
					}

					
				//CLERK OUTFIT
				} else if (args[0].equals("outfit")) {
					merchmeth.Equip(this_npc.getId(), player,HMP);

						
					//ANY OTHER ARGUMENTS THAT ARE INVALID
				}else {
					sender.sendMessage(Utils.formatText(L.CC_SUBCOMMANDS, null));
					return;
				}
				return;
				
				
				//if the player doesn't have a hypermerchant npc selected
			} else {
				HashMap<String, String> keywords = new HashMap<String, String>();
				keywords.put("<command>",  "/hmerchant");
				sender.sendMessage(Utils.formatText(L.CC_NO_NPC_SELECTED_2, keywords));
				return;
			}				
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
