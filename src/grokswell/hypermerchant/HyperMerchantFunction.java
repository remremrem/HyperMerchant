package grokswell.hypermerchant;

import java.util.ArrayList;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCSelector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.ShopFactory;

public class HyperMerchantFunction {
	CommandSender sender;
	HyperAPI hyperAPI = new HyperAPI();
	
	HyperMerchantFunction(CommandSender snder, String[] args, HyperMerchantPlugin HMP) {
		//HyperConomy hc = HyperConomy.hc;
		//ShopFactory hc_factory = hc.getShopFactory();
		//DataHandler hc_functions = hc.getDataFunctions();
		//String shopname = sname;
		this.sender = snder;
		Player player = (Player) snder;
		NPCSelector sel = CitizensAPI.getDefaultNPCSelector();
		
		try {
			NPC this_npc = sel.getSelected(player);
			if (this_npc.hasTrait(HyperMerchantTrait.class)) {
				
				if (args[0].equals("setshop")) {
					if (args.length>1) {
						HyperConomy hc = HyperConomy.hc;
						ShopFactory sf = hc.getShopFactory();
						ArrayList<String> shoplist = sf.listShops();
						
						if (shoplist.contains(args[1])) {
							//this_npc.getTrait(HyperMerchantTrait.class).trait_key.setString("shop_name",args[0]);
							this_npc.getTrait(HyperMerchantTrait.class).shop_name = args[1];
							this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
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
					
				} else if (args[0].equals("greeting")) {
					if (args.length>1) {
						StringBuilder buffer = new StringBuilder();
						for(int i = 1; i < args.length; i++) {
						    buffer.append(' ').append(args[i]);
						}
						this_npc.getTrait(HyperMerchantTrait.class).welcomeMsg = buffer.toString();
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" greeting message has been updated.");
					} else {
						this_npc.getTrait(HyperMerchantTrait.class).welcomeMsg = "";
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a greeting to customers.");
					}
					
				} else if (args[0].equals("farewell")) {
					if (args.length>1) {
						StringBuilder buffer = new StringBuilder();
						for(int i = 1; i < args.length; i++) {
						    buffer.append(' ').append(args[i]);
						}
						this_npc.getTrait(HyperMerchantTrait.class).farewellMsg = buffer.toString();
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" farewell message has been updated.");
					} else {
						this_npc.getTrait(HyperMerchantTrait.class).farewellMsg = "";
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer say a farewell to customers.");
					}
					
				} else if (args[0].equals("denial")) {
					if (args.length>1) {
						StringBuilder buffer = new StringBuilder();
						for(int i = 1; i < args.length; i++) {
						    buffer.append(' ').append(args[i]);
						}
						this_npc.getTrait(HyperMerchantTrait.class).denialMsg = buffer.toString();
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" denial message has been updated.");
					} else {
						this_npc.getTrait(HyperMerchantTrait.class).denialMsg = "";
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer inform customers that they are being denied service.");
					}
					
				} else if (args[0].equals("closed")) {
					if (args.length>1) {
						StringBuilder buffer = new StringBuilder();
						for(int i = 1; i < args.length; i++) {
						    buffer.append(' ').append(args[i]);
						}
						this_npc.getTrait(HyperMerchantTrait.class).closedMsg = buffer.toString();
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" closed message has been updated.");
					} else {
						this_npc.getTrait(HyperMerchantTrait.class).closedMsg = "";
						this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" will no longer inform customers that when off duty.");
					}
					
				} else if (args[0].equals("offduty")) {
					this_npc.getTrait(HyperMerchantTrait.class).offduty = !this_npc.getTrait(HyperMerchantTrait.class).offduty;
					this_npc.getTrait(HyperMerchantTrait.class).save(this_npc.getTrait(HyperMerchantTrait.class).trait_key);
					if (this_npc.getTrait(HyperMerchantTrait.class).offduty) {
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now off duty.");
					} else {
						sender.sendMessage(ChatColor.YELLOW+"NPC "+this_npc.getName()+" is now on duty.");
					}

				} else {
					sender.sendMessage(ChatColor.YELLOW+"Valid "+ChatColor.DARK_RED+"/hmerchant"+ChatColor.YELLOW+" subcommands are:\n" +
							ChatColor.RED+"setshop , offduty , greeting , farewell , denial , closed.");
					return;
				}

			} else {
				sender.sendMessage(ChatColor.YELLOW+"You must have a hypermerchant npc " +
						"selected to use the command "+ChatColor.RED+"/hmerchant.");
				return;
			}
			
		} catch (Exception e){
			sender.sendMessage(ChatColor.YELLOW+"You must have a hypermerchant npc " +
								"selected to use the command "+ChatColor.RED+"/hmerchant.");
			return;
		}
		return;		
	}
}
