package grokswell.hypermerchant;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCSelector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.EconomyManager;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;

public class HyperMerchantCommand {
	CommandSender sender;
	HyperAPI hyperAPI = new HyperAPI();
	
	HyperMerchantCommand(CommandSender snder, String[] args, HyperMerchantPlugin HMP) {
		//HyperConomy hc = HyperConomy.hc;
		//ShopFactory hc_factory = hc.getShopFactory();
		//DataHandler hc_functions = hc.getDataFunctions();
		//String shopname = sname;
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
			
				this_npc = sel.getSelected(player);
				if (args[0].equals("setshop")) {
					shopname = args[1];
				}
				for(int i = 1; i < args.length; i++) {
				    buffer.append(' ').append(args[i]);
				}
			}
			
			if (this_npc.hasTrait(HyperMerchantTrait.class)) {
				
				
					//HMERCH INFO
				if (args[0].equals("info")) {
						if (this_npc.getTrait(HyperMerchantTrait.class).offduty) {
							sender.sendMessage("\n"+ChatColor.YELLOW+this_npc.getName()+" is OFFDUTY");
						} else {
							sender.sendMessage("\n"+ChatColor.YELLOW+this_npc.getName()+" is ONDUTY");
						}
						sender.sendMessage(ChatColor.YELLOW+"ID: " + String.valueOf(this_npc.getId()) + " , SHOP: "+ this_npc.getTrait(HyperMerchantTrait.class).shop_name +"\n");

						
						
					//HMERCH SETSHOP
				} else if (args[0].equals("setshop")) {
					if (args.length>1) {
						
						if (hyperAPI.getServerShopList().contains(shopname) || hyperAPI.getPlayerShopList().contains(shopname)) {
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
				
					
					//HMERCH GREETING
				} else if (args[0].equals("greeting")) {
					if (args.length>1) {
						this_npc.getTrait(HyperMerchantTrait.class).welcomeMsg = buffer.toString();
						if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						}
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" greeting message has been updated.");
					} else {
						this_npc.getTrait(HyperMerchantTrait.class).welcomeMsg = "";
						if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						}
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a greeting to customers.");
					}

				
					
					//HMERCH FAREWELL
				} else if (args[0].equals("farewell")) {
					if (args.length>1) {
						this_npc.getTrait(HyperMerchantTrait.class).farewellMsg = buffer.toString();
						if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						}
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" farewell message has been updated.");
					} else {
						this_npc.getTrait(HyperMerchantTrait.class).farewellMsg = "";
						if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						}
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a farewell to customers.");
					}

				
					
					//HMERCH DENIAL
				} else if (args[0].equals("denial")) {
					if (args.length>1) {
						this_npc.getTrait(HyperMerchantTrait.class).denialMsg = buffer.toString();
						if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						}
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" denial message has been updated.");
					} else {
						this_npc.getTrait(HyperMerchantTrait.class).denialMsg = "";
						if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						}
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer inform customers that they are being denied service.");
					}

				
					
					//HMERCH CLOSED
				} else if (args[0].equals("closed")) {
					if (args.length>1) {
						this_npc.getTrait(HyperMerchantTrait.class).closedMsg = buffer.toString();
						if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						}
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" closed message has been updated.");
					} else {
						this_npc.getTrait(HyperMerchantTrait.class).closedMsg = "";
						if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						}
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer inform customers that when off duty.");
					}

				
					
					//HMERCH OFFUDTY
				} else if (args[0].equals("offduty") || args[0].equals("onduty")) {
					this_npc.getTrait(HyperMerchantTrait.class).offduty = !this_npc.getTrait(HyperMerchantTrait.class).offduty;
					if (this_npc.getTrait(HyperMerchantTrait.class).trait_key != null) {
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
					}
					if (this_npc.getTrait(HyperMerchantTrait.class).offduty) {
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now off duty.");
					} else {
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now on duty.");
					}

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
