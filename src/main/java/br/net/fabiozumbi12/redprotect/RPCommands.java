package br.net.fabiozumbi12.redprotect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


public class RPCommands implements CommandCallable {
    
	public RPCommands(){
		RedProtect.logger.debug("Loaded RPCommands...");
	}
	
    private static void sendNotInRegionMessage(Player p) {
        RPLang.sendMessage(p, "cmdmanager.region.todo.that");
    }
    
    private static void sendNoPermissionMessage(Player p) {
        RPLang.sendMessage(p, "no.permission");
    }
    
    /*
    @Override
    public List onTabComplete(CommandSource e, String args) throws CommandException {
    	//List<String> SotTab = new ArrayList<String>();    
    	//SortedSet<String> tab = new TreeSet<String>();  
    	
    	if (e.getCause().containsType(Player.class)) {
    		Player p = e.getCause().first(Player.class).get();
    		List<String> cmds = Arrays.asList("border", "expand-vert", "setminy", "setmaxy", "value", "buy", "sell", "cancelbuy", "tutorial", "limit", "claimlimit", "list", "delete", "info", "flag", "addmember", "addowner", "removemember", "removeowner", "rename", "welcome", "priority", "near", "panel");
    		List<String> admcmds = Arrays.asList("wand", "tp", "claim", "define", "redefine", "setconfig", "reload", "copyflag", "setcreator", "save-all", "reload-all");
    		
    		String[] args = e.getArguments().split(" ");
    		
    		if (args.length == 1){
    			for (String command:cmds){
    				if (p.hasPermission("redprotect.user") && command.startsWith(args[0]) && !tab.contains(command)){
    					tab.add(command);
    					
    				}
    			}
    			for (String command:admcmds){
    				if (p.hasPermission("redprotect.admin") && command.startsWith(args[0]) && !tab.contains(command)){
    					tab.add(command);
    				}
    			}
    			SotTab.addAll(tab);
    			return SotTab;
    		}
    		if (args.length == 2){
        		if (args[0].equalsIgnoreCase("flag")){
        			for (String flag:RedProtect.cfgs.getDefFlags()){
        				if (flag.startsWith(args[1]) && p.hasPermission("redprotect.flag."+ flag) && !tab.contains(flag)){
        					tab.add(flag);
        				}
        			} 
        			for (String flag:RedProtect.cfgs.AdminFlags){
        				if (flag.startsWith(args[1]) && p.hasPermission("redprotect.admin.flag."+ flag) && !tab.contains(flag)){
        					tab.add(flag);
        				}
        			}
        			SotTab.addAll(tab);
        			return SotTab;
        		}
        	}
    	} else if (e.getCause().containsType(Player.class)){
    		return;
    	}
    	   	
    	
    	if (sender instanceof Player){
    		
    	} else {
    		List<String> consolecmds = Arrays.asList("setconfig", "flag", "tp", "ymlTomysql", "setconfig", "reload", "save-all", "reload-all", "limit", "claimlimit", "list-all");
    		for (String command:consolecmds){
				if (command.startsWith(args[0])){
					tab.add(command);
				}
			}
    		SotTab.addAll(tab);
			return SotTab;
    	}
		return null;    	
    }
    */
    
    public CommandResult process(CommandSource sender, String arguments) throws CommandException {
    	CommandResult cmdr = CommandResult.success();
    	
    	
    	
		String[] args = arguments.split(" ");
				
        if (!(sender instanceof Player)) {        	
        	if (args.length == 1) {        		
        		/*if (args[0].equalsIgnoreCase("ymlToMysql")) {
        			try {
						if (!RPUtil.ymlToMysql()){
							RedProtect.logger.severe("ERROR: Check if your 'file-type' configuration is set to 'yml' before convert from YML to Mysql.");
							return cmdr;
						} else {
							RedProtect.cfgs.setConfig("file-type", (String)"mysql");
							RedProtect.cfgs.save();
							server.getPluginManager().disablePlugin((Plugin)RedProtect.plugin);
		        			RedProtect.plugin.getServer().getPluginManager().enablePlugin((Plugin)RedProtect.plugin);
		        			RedProtect.logger.sucess("RedProtect reloaded with Mysql as database! Ready to use!");
		        			return cmdr;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return cmdr;
					}
        		}
        		if (args[0].equalsIgnoreCase("gpTorp")) {
        			if (!RedProtect.GP){
        				RedProtect.logger.sucess("The plugin GriefPrevention is not installed or is disabled");
        				return cmdr;
        			}
        			if (RPUtil.convertFromGP() == 0){
						RedProtect.logger.severe("No region converted from GriefPrevention.");
						return cmdr;
					} else {
						RedProtect.rm.saveAll();
						RedProtect.logger.info(TextColors.AQUA + "[" + RPUtil.convertFromGP() + "] regions converted from GriefPrevention with success");
						RedProtect.plugin.getServer().getPluginManager().disablePlugin((Plugin)RedProtect.plugin);
	        			RedProtect.plugin.getServer().getPluginManager().enablePlugin((Plugin)RedProtect.plugin);						
	        			return cmdr;
					}
        		}
        		if (args[0].equalsIgnoreCase("update")) {
        			if (RedProtect.Update){
            			RedProtect.logger.info(TextColors.AQUA + "Starting download update...");
            			new Updater(RedProtect.plugin, 87463, RedProtect.JarFile, Updater.UpdateType.NO_VERSION_CHECK, true);
            			RedProtect.logger.sucess("Download completed! Restart your server to use the new version.");
            			return cmdr;
            		} else {
            			RedProtect.logger.info(TextColors.AQUA + "No updates to download!");
            			return cmdr;
            		}
        		}        		
        		*/
        		
        		if (args[0].isEmpty()) {
        			sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"---------------- "+RedProtect.plugin.getName()+" ----------------"));
                    sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"Developed by &eFabioZumbi12"+RPLang.get("general.color")+"."));
                    sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"For more information about the commands, type [&e/rp ?"+RPLang.get("general.color")+"]."));
                    sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"For a tutorial, type [&e/rp tutorial"+RPLang.get("general.color")+"]."));
                    sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"---------------------------------------------------"));
                    return cmdr;
        		}
        		                
        		if (args[0].equalsIgnoreCase("list-all")) {
        			int total = 0;
        			for (Region r:RedProtect.rm.getAllRegions()){
        				RedProtect.logger.info("&a[" + total + "]" + "Region: " + r.getName() + "&3 | &World: " + r.getWorld() +"&r");
        				total ++;
        			}
        			RedProtect.logger.sucess(total + " regions for " + Sponge.getServer().getWorlds().size() + " worlds.");
        			return cmdr;
        		}
        		        		
        		if (args[0].equalsIgnoreCase("save-all")) {            
        			RedProtect.rm.saveAll();
        			RedProtect.logger.SaveLogs();
            		RedProtect.logger.sucess(RedProtect.rm.getAllRegions().size() + " regions saved with success!");  
            		return cmdr;
            	}
        		if (args[0].equalsIgnoreCase("load-all")) {            
        			RedProtect.rm.clearDB();
        			try {
						RedProtect.rm.loadAll();
					} catch (Exception e) {
						RedProtect.logger.severe("Error on load all regions from database files:");
						e.printStackTrace();
					}
            		RedProtect.logger.sucess(RedProtect.rm.getAllRegions().size() + " regions has been loaded from database files!");  
            		return cmdr;
            	}
        		
        		/*
        		if (args[0].equalsIgnoreCase("reload")) {
        			RedProtect.plugin.getServer().getPluginManager().disablePlugin((Plugin)RedProtect.plugin);
        			RedProtect.plugin.getServer().getPluginManager().enablePlugin((Plugin)RedProtect.plugin);
            		RedProtect.logger.sucess("RedProtect Plus reloaded!");
            		return cmdr;
            	}   
            	*/       		
        	} 
        	
        	if(args.length == 2){
        		        		
        		//rp clamilimit player
        		if  (args[0].equalsIgnoreCase("claimlimit") || args[0].equalsIgnoreCase("climit")  || args[0].equalsIgnoreCase("cl")){ 
        			Player offp = RedProtect.serv.getPlayer(args[1]).get();
                	if (offp == null){
                		sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", args[1])));
                		return cmdr;
                	}
                	int limit = RedProtect.ph.getPlayerClaimLimit(offp);
                    if (limit < 0 || RedProtect.ph.hasPerm(offp, "redprotect.limit.claim.unlimited")) {
                    	sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.nolimit")));
                        return cmdr;
                    }
                    
                    int currentUsed = RedProtect.rm.getRegions(RPUtil.PlayerToUUID(offp.getName()), offp.getWorld()).size();
                    sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.yourclaims").toString() + currentUsed + RPLang.get("general.color") + "/&e" + limit + RPLang.get("general.color")));
                    return cmdr;
        		}
        		
        		if (args[0].equalsIgnoreCase("limit") || args[0].equalsIgnoreCase("limitremaining") || args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("l")) {
        			Player offp = RedProtect.serv.getPlayer(args[1]).get();
                	if (offp == null){
                		sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", args[1])));
                		return cmdr;
                	}
                	int limit = RedProtect.ph.getPlayerLimit(offp);
                    if (limit < 0 || RedProtect.ph.hasPerm(offp, "redprotect.limit.blocks.unlimited")) {
                    	sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.nolimit")));
                        return cmdr;
                    }
                    
                    int currentUsed = RedProtect.rm.getTotalRegionSize(RPUtil.PlayerToUUID(offp.getName()));
                    sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.yourarea").toString() + currentUsed + RPLang.get("general.color") + "/&e" + limit + RPLang.get("general.color")));
                    return cmdr;
        		}
        		
        	}
               
        	if (args.length == 3){
        		//rp clamilimit player world
        		if  (args[0].equalsIgnoreCase("claimlimit") || args[0].equalsIgnoreCase("climit")  || args[0].equalsIgnoreCase("cl")){ 
        			Player offp = RedProtect.serv.getPlayer(args[1]).get();
        			World w = RedProtect.serv.getWorld(args[2]).get();
                	if (offp == null){
                		sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", args[1])));
                		return cmdr;
                	}
                	int limit = RedProtect.ph.getPlayerClaimLimit(offp);
                    if (limit < 0 || RedProtect.ph.hasPerm(offp, "redprotect.limit.claim.unlimited")) {
                    	sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.nolimit")));
                        return cmdr;
                    }
                    
                    if (w == null){
                    	sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.invalidworld")));
                    	return cmdr;
                    }
                    
                    int currentUsed = RedProtect.rm.getRegions(RPUtil.PlayerToUUID(offp.getName()), w).size();
                    sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.yourclaims").toString() + currentUsed + RPLang.get("general.color") + "/&e" + limit + RPLang.get("general.color")));
                    return cmdr;
        		}
        		 
        		
        		if  (args[0].equalsIgnoreCase("setconfig")){
        			if (args[1].contains("debug-messages") || args[1].contains("file-type")){
        				Object from = RedProtect.cfgs.getObject(args[1]); 
            			if (args[2].equals("true") || args[2].equals("false")){
            				RedProtect.cfgs.setConfig(args[1], Boolean.parseBoolean(args[2]));
            			} else {
            				try {
                				int value = Integer.parseInt(args[2]);
                				RedProtect.cfgs.setConfig(args[1], value);
                		    } catch(NumberFormatException ex){
                		    	RedProtect.cfgs.setConfig(args[1], args[2]);
                		    }
            			}
            			sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.configset")+" "+from.toString()+" > "+args[2]));
            			RedProtect.cfgs.save();
            			return cmdr;
            		} else {
            			sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.confignotset")+" "+args[1]));
            			return cmdr;
            		}
        		}
        		
        		
        		//rp info <region> <world>
        		if (args[0].equalsIgnoreCase("info")) {
        			if (Sponge.getServer().getWorld(args[2]).isPresent()){
        				Region r = RedProtect.rm.getRegion(args[1], Sponge.getServer().getWorld(args[2]).get());
        				if (r != null){
        					sender.sendMessage(RPUtil.toText(RPLang.get("general.color") + "-----------------------------------------"));
        					sender.sendMessage(r.info());
        					sender.sendMessage(RPUtil.toText(RPLang.get("general.color") + "-----------------------------------------"));
        				} else {
        					sender.sendMessage(RPUtil.toText(RPLang.get("correct.usage") + "&eInvalid region: " + args[1]));
        				}
        			} else {
        				sender.sendMessage(RPUtil.toText(RPLang.get("correct.usage") + " " + "&eInvalid World: " + args[2]));
        			}
                    return cmdr;
                }
        	}
        	        	
        	if (args.length == 4) {
        		if (args[0].equalsIgnoreCase("tp")){
        			//rp tp <player> <region> <world>
                	Player play = RedProtect.serv.getPlayer(args[1]).get();
                	if (play != null){                		
                		World w = RedProtect.serv.getWorld(args[3]).get();                		
                		if (w == null) {
                            sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.invalidworld")));
                            return cmdr;
                        }
                    	Region region = RedProtect.rm.getRegion(args[2], w);
                    	if (region == null) {
                    		sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.doesntexist") + ": " + args[2]));
                            return cmdr;
                        } 
                    	
                    	Location<World> loc = null;
                    	if (region.getTPPoint() != null){
                			loc = new Location<World>(w, region.getTPPoint().getBlockX()+0.500, region.getTPPoint().getBlockY(), region.getTPPoint().getBlockZ()+0.500);
                    	} else {
                    		int limit = 256;
                        	if (w.getDimension().equals(DimensionTypes.NETHER)){
                        		limit = 124;
                        	}
                        	for (int i = limit; i > 0; i--){
                        		BlockType mat = new Location<World>(w, region.getCenterX(), i, region.getCenterZ()).getBlockType();
                        		BlockType mat1 = new Location<World>(w, region.getCenterX(), i+1, region.getCenterZ()).getBlockType();
                        		BlockType mat2 = new Location<World>(w, region.getCenterX(), i+2, region.getCenterZ()).getBlockType();
                        		if (!mat.equals(BlockTypes.LAVA) && !mat.equals(BlockTypes.AIR) && mat1.equals(BlockTypes.AIR) && mat2.equals(BlockTypes.AIR)){
                        			loc = new Location<World>(w, region.getCenterX()+0.500, i+1, region.getCenterZ()+0.500);            			
                        			break;
                        		}
                        	}
                    	}               		
                    	                    	
                    	play.setLocation(loc);
            			RPLang.sendMessage(play,RPLang.get("cmdmanager.region.tp") + " " + args[2]);     
            			sender.sendMessage(RPUtil.toText("&3Player teleported to " + args[2]));
                		return cmdr;
                	} else {
                		sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", args[1])));
                		HandleHelPage(sender, 1);
                		return cmdr;
                	}
        		}          
        		
        		//rp flag info <region> <world>
        		if (args[0].equalsIgnoreCase("flag") && args[1].equalsIgnoreCase("info") ) {
        			if (Sponge.getServer().getWorld(args[3]).isPresent()){
        				Region r = RedProtect.rm.getRegion(args[2], Sponge.getServer().getWorld(args[3]).get());
        				if (r != null){
        					sender.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------[" + RPLang.get("cmdmanager.region.flag.values") + "]------------"));
        					sender.sendMessage(r.getFlagInfo());
                            sender.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------------------------------"));
        				} else {
        					sender.sendMessage(RPUtil.toText(RPLang.get("correct.usage") + "&eInvalid region: " + args[2]));
        				}
        			} else {
        				sender.sendMessage(RPUtil.toText(RPLang.get("correct.usage") + "&eInvalid World: " + args[3]));
        			}
                    return cmdr;
                }
            }
        	
    		if (args.length == 5){
    			/*/rp flag <regionName> <flag> <value> <world>*/
    			if  (args[0].equalsIgnoreCase("flag")){
    				World w = RedProtect.serv.getWorld(args[4]).get();
        			if (w == null){
        				sender.sendMessage(RPUtil.toText(RPLang.get("correct.usage").toString() + "&e rp flag <regionName> <flag> <value> <world>"));
        				return cmdr;
        			}
        			Region r = RedProtect.rm.getRegion(args[1], w);
        			if (r != null && (RedProtect.cfgs.getDefFlags().contains(args[2]) || RedProtect.cfgs.AdminFlags.contains(args[2]))){
        				Object objflag = RPUtil.parseObject(args[3]);
        				r.setFlag(args[2], objflag);
        				sender.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.flag.set").toString().replace("{flag}", "'"+args[2]+"'") + " " + r.getFlagString(args[2])));
        				RedProtect.logger.addLog("Console changed flag "+args[2]+" to "+r.getFlagString(args[2]));
        				return cmdr;
        			}
    			}    			
    		}    		
        	HandleHelPage(sender, 1);
            return cmdr;            
        }
        
        //commands as player
        final Player player = (Player)sender;
        
        if (args.length == 1) {
        	/*
        	if (args[0].equalsIgnoreCase("update") && player.hasPermission("redprotect.update")){
        		if (RedProtect.Update){
            		RPLang.sendMessage(player, TextColors.CYAN + "Starting download update...");
        			new Updater(RedProtect.plugin, 87463, RedProtect.JarFile, Updater.UpdateType.NO_VERSION_CHECK, true);
        			RPLang.sendMessage(player, TextColors.CYAN + "Update downloaded! Will take effect on next server reboot.");
        			return cmdr;
        		} else {
        			RPLang.sendMessage(player, TextColors.CYAN + "No updates to download!");
        			return cmdr;
        		}
        	}
        	*/
        	
        	if (args[0].isEmpty()) {
    			sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"---------------- "+RedProtect.plugin.getName()+" ----------------"));
                sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"Developed by &eFabioZumbi12"+RPLang.get("general.color")+"."));
                sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"For more information about the commands, type [&e/rp ?"+RPLang.get("general.color")+"]."));
                sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"For a tutorial, type [&e/rp tutorial"+RPLang.get("general.color")+"]."));
                sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"---------------------------------------------------"));
                return cmdr;
    		}
        	
        	if (args[0].equalsIgnoreCase("settp") && RedProtect.ph.hasHelpPerm(player, "settp")){
        		Region r = RedProtect.rm.getTopRegion(player.getLocation());
        		if (r != null){
        			r.setTPPoint(player.getLocation());
        			RPLang.sendMessage(player, "cmdmanager.region.settp.ok");
        			return cmdr;
        		} else {
    				RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    				return cmdr;    
        		}
        	}
        	
        	if (args[0].equalsIgnoreCase("deltp") && RedProtect.ph.hasHelpPerm(player, "settp")){
        		Region r = RedProtect.rm.getTopRegion(player.getLocation());
        		if (r != null){
        			r.setTPPoint(null);
        			RPLang.sendMessage(player, "cmdmanager.region.settp.removed");
        			return cmdr;
        		} else {
    				RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    				return cmdr;    
        		}
        	}
        	
        	if (args[0].equalsIgnoreCase("border") && RedProtect.ph.hasHelpPerm(player, "border")){
        		Region r = RedProtect.rm.getTopRegion(player.getLocation());
        		if (r != null){
        			RPUtil.addBorder(player, r);
        			return cmdr;
        		} else {
    				RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    				return cmdr;    
        		}
        	}
        	/*
        	if (args[0].equalsIgnoreCase("cancelbuy") && player.hasPermission("redprotect.eco.cancelbuy")){
        		if (!RedProtect.Vault){
        			return cmdr;
        		}
        		Region r = RedProtect.rm.getTopRegion(player.getLocation());
        		if (r == null){
        			RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    				return cmdr;
        		}
        		
        		if (r.getCreator().equalsIgnoreCase(RPUtil.PlayerToUUID(player.getName()))){
        			if (r.isForSale()){
            			r.setFlag("for-sale", false);        			
            			r.setWelcome("");
            			if (r.getCreator() == null){
            				if (RedProtect.cfgs.getEcoBool("rename-region")){
            					RedProtect.rm.renameRegion(RPUtil.nameGen(player.getName(),r.getWorld()), r);
            				}
            				r.setCreator(RPUtil.PlayerToUUID(player.getName()));        		
            				r.addOwner(r.getCreator());
            			} else {
            				if (RedProtect.cfgs.getEcoBool("rename-region")){
            					RedProtect.rm.renameRegion(RPUtil.nameGen(RPUtil.UUIDtoPlayer(r.getCreator()),r.getWorld()),r);
            				}
            				r.addOwner(r.getCreator());
            			}        			
            			RPLang.sendMessage(player, "economy.region.cancelbuy");
            			RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" cancelled buy stat of region "+r.getName());
        				return cmdr;
            		} else {
            			RPLang.sendMessage(player, "economy.region.buy.notforsale");
            			return cmdr;
            		}
        		} else {
        			RPLang.sendMessage(player, "economy.region.sell.own");
        			return cmdr;
        		}
        	}
        	        	
        	if (args[0].equalsIgnoreCase("value") && RedProtect.ph.hasPerm(player, "redprotect.admin.value")){
        		Region r = RedProtect.rm.getTopRegion(player.getLocation());
        		if (r != null){
        			if (r.getArea() < RedProtect.cfgs.getEcoInt("max-area-toget-value")){
        				RPLang.sendMessage(player, RPLang.get("cmdmanager.value.is").replace("{value}", RPEconomy.getFormatted(RPEconomy.getRegionValue(r)) + " " +RedProtect.cfgs.getEcoString("economy-name")));
        				RedProtect.logger.debug("Region Value: "+RPEconomy.getRegionValue(r));
            			return cmdr;
        			} else {
        				RPLang.sendMessage(player, RPLang.get("cmdmanager.value.areabig").replace("{maxarea}", RedProtect.cfgs.getEcoInt("max-area-toget-value").toString()));
        				return cmdr;
        			}
        		} else {
    				RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    				return cmdr;
    			} 
        	}        	
        	*/
        	if (args[0].equalsIgnoreCase("save-all")) {   
        		if (player.hasPermission("redprotect.admin.save-all")) {
        			RedProtect.rm.saveAll();
        			RedProtect.logger.SaveLogs();
        			RPLang.sendMessage(player,"&a" + RedProtect.rm.getAllRegions().size() + " regions saved with success!");
        			return cmdr;
        		}    			            		       		
        	}
        	if (args[0].equalsIgnoreCase("load-all")) {   
        		if (player.hasPermission("redprotect.admin.load-all")) {
        			RedProtect.rm.clearDB();
        			try {
    					RedProtect.rm.loadAll();
    				} catch (Exception e) {
    					RPLang.sendMessage(player, "Error on load all regions from database files:");
    					e.printStackTrace();
    				}
        			RPLang.sendMessage(player,"&a" + RedProtect.rm.getAllRegions().size() + " regions has been loaded from database files!");  
            		return cmdr;
        		}    			
        	}
        	if (args[0].equalsIgnoreCase("define")){
        		if (!player.hasPermission("redprotect.admin.define")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
        		String serverName = RedProtect.cfgs.getString("region-settings.default-owner");
                String name = RPUtil.nameGen(serverName, player.getWorld().getName());
                
                RegionBuilder rb2 = new DefineRegionBuilder(player, RedProtect.firstLocationSelections.get(player), RedProtect.secondLocationSelections.get(player), name, serverName, new ArrayList<String>());
                if (rb2.ready()) {
                    Region r2 = rb2.build();
                    RPLang.sendMessage(player,RPLang.get("cmdmanager.region.created") + " " + r2.getName() + ".");
                    RedProtect.rm.add(r2, player.getWorld());
                    RedProtect.logger.addLog("(World "+r2.getWorld()+") Player "+player.getName()+" DEFINED region "+r2.getName());
                }
                return cmdr;
        	}
        	
        	//rp claim
        	if (args[0].equalsIgnoreCase("claim")){
        		if (!player.hasPermission("redprotect.admin.claim")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
                String name = RPUtil.nameGen(player.getName(), player.getWorld().getName());
                String creator = player.getUniqueId().toString();
                if (!RedProtect.OnlineMode){
                	creator = player.getName().toLowerCase();
            	}
                RegionBuilder rb2 = new DefineRegionBuilder(player, RedProtect.firstLocationSelections.get(player), RedProtect.secondLocationSelections.get(player), name, creator, new ArrayList<String>());
                if (rb2.ready()) {
                    Region r2 = rb2.build();
                    RPLang.sendMessage(player,RPLang.get("cmdmanager.region.created") + " " + r2.getName() + ".");
                    RedProtect.rm.add(r2, player.getWorld());
                    RedProtect.logger.addLog("(World "+r2.getWorld()+") Player "+player.getName()+" CLAIMED region "+r2.getName());
                }
                return cmdr;
        	}
        	/*
        	if (args[0].equalsIgnoreCase("reload") && player.hasPermission("redprotect.admin.reload")) {
        		RedProtect.plugin.getServer().getPluginManager().disablePlugin((Plugin)RedProtect.plugin);
    			RedProtect.plugin.getServer().getPluginManager().enablePlugin((Plugin)RedProtect.plugin);
        		RPLang.sendMessage(player, "cmdmanager.reloaded");
        		return cmdr;
        	}
        	*/
        	if (args[0].equalsIgnoreCase("wand") && player.hasPermission("redprotect.magicwand")) {
        		Inventory inv = player.getInventory();
        		ItemType mat = RPUtil.getItemType(RedProtect.cfgs.getString("wands.adminWandID"));
        		ItemStack item = ItemStack.of(mat, 1);
        		Iterable<Slot> slotIter = player.getInventory().slots();
        		if (!inv.contains(mat)){
        			for (Slot slot:slotIter) {
        			    if (slot.isEmpty()) {
        			        slot.set(item);
        			        RPLang.sendMessage(player,RPLang.get("cmdmanager.wand.given").toString().replace("{item}", mat.getName()));
        			        break;
        			   } 
        			}
        		}        		
                RPLang.sendMessage(player,RPLang.get("cmdmanager.wand.nospace").toString().replace("{item}", mat.getName()));
        		return cmdr;
        	}
        	
            if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
                HandleHelPage(sender, 1);
                return cmdr;
            }   
            
            if (args[0].equalsIgnoreCase("tutorial") || args[0].equalsIgnoreCase("tut")) {
                RPLang.sendMessage(player,"cmdmanager.tutorial");
                RPLang.sendMessage(player,"cmdmanager.tutorial1");
                RPLang.sendMessage(player,"cmdmanager.tutorial2");
                RPLang.sendMessage(player,"cmdmanager.tutorial3");
                RPLang.sendMessage(player,"cmdmanager.tutorial4");
                RPLang.sendMessage(player,"cmdmanager.tutorial5");
                return cmdr;
            }
            if (args[0].equalsIgnoreCase("near") || args[0].equalsIgnoreCase("nr")) {
                if (RedProtect.ph.hasPerm(player, "redprotect.near")) {
                    Set<Region> regions = RedProtect.rm.getRegionsNear(player, 60, player.getWorld());
                    if (regions.size() == 0) {
                        RPLang.sendMessage(player, "cmdmanager.noregions.nearby");
                    }
                    else {
                        Iterator<Region> i = regions.iterator();
                        RPLang.sendMessage(player,RPLang.get("general.color") + "------------------------------------");
                        RPLang.sendMessage(player,RPLang.get("cmdmanager.regionwith40").toString());
                        RPLang.sendMessage(player,RPLang.get("general.color") + "------------------------------------");
                        while (i.hasNext()) {
                            Region r = i.next();
                            RPLang.sendMessage(player,RPLang.get("cmdmanager.region.name") + r.getName() + RPLang.get("general.color") + RPUtil.toText(" | Center (&6X,Z"+RPLang.get("general.color")+"): &6") +  r.getCenterX() + ", "  + r.getCenterZ());
                            RPLang.sendMessage(player,RPLang.get("region.regions") + " " + regions.size());
                        }
                        RPLang.sendMessage(player,RPLang.get("general.color") + "------------------------------------");
                    }
                }
                else {
                    RPLang.sendMessage(player, "no.permission");
                }
                return cmdr;
            }
            
            /*
            if (args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("fl")) {
            	if (player.hasPermission("redprotect.own.flaggui")) {
        			Region r = RedProtect.rm.getTopRegion(player.getLocation());
        			if (r != null){
        				if (r.isOwner(player) || player.hasPermission("redprotect.admin.flaggui")){
        					if (r.getName().length() > 16){
        						RPGui gui = new RPGui(RPLang.get("gui.invflag").replace("{region}", r.getName().substring(0, 16)), player, r, RedProtect.plugin, false, RedProtect.cfgs.getGuiMaxSlot());
        						gui.open();
        					} else {
        						RPGui gui = new RPGui(RPLang.get("gui.invflag").replace("{region}", r.getName()), player, r, RedProtect.plugin, false, RedProtect.cfgs.getGuiMaxSlot());
        						gui.open();
        					}
                			return cmdr;
        				} else {
        					sendNoPermissionMessage(player);
        					return cmdr;
        				}
        			} else {
        				RPLang.sendMessage(player, "cmdmanager.region.todo.that");
        				return cmdr;
        			}    
        		} 
            }
            */
        }
        
        if (args.length == 2) {      
        	/*
        	if ((args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("fl")) && args[1].equalsIgnoreCase("gui-edit")) {
        		if (player.hasPermission("redprotect.gui.edit")){
        			Region r = RedProtect.rm.getTopRegion(player.getLocation());
        			if (r != null){
        				RPGui gui = new RPGui(RPLang.get("gui.editflag"), player, r, RedProtect.plugin, true, RedProtect.cfgs.getGuiMaxSlot());
    					gui.open();
        			} else {
        				RPLang.sendMessage(player, "cmdmanager.region.todo.that");
        			}
        			return cmdr;
        		}        		
        	}
        	*/
        	
        	if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
        		try{
        			int page = Integer.parseInt(args[1]);
                    HandleHelPage(sender, page);
        		} catch (NumberFormatException e){
        			RPLang.sendMessage(player,RPLang.get("correct.usage") + "&e/rp ? [page]");
        		}
                return cmdr;
            }        	
        	        	
        	if (args[0].equalsIgnoreCase("define")){
        		if (!player.hasPermission("redprotect.admin.define")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
        		String serverName = RedProtect.cfgs.getString("region-settings.default-owner");
                String name = args[1];
                
                RegionBuilder rb2 = new DefineRegionBuilder(player, RedProtect.firstLocationSelections.get(player), RedProtect.secondLocationSelections.get(player), name, serverName, new ArrayList<String>());
                if (rb2.ready()) {
                    Region r2 = rb2.build();
                    RPLang.sendMessage(player,RPLang.get("cmdmanager.region.created") + " " + r2.getName() + ".");
                    RedProtect.rm.add(r2, player.getWorld());
                    RedProtect.logger.addLog("(World "+r2.getWorld()+") Player "+player.getName()+" DEFINED region "+r2.getName());
                }
                return cmdr;
        	}
        	
        	//rp claim [nameOfRegion]
        	if (args[0].equalsIgnoreCase("claim")){
        		if (!player.hasPermission("redprotect.admin.claim")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
                String name = args[1];
                String creator = player.getUniqueId().toString();
                if (!RedProtect.OnlineMode){
                	creator = player.getName().toLowerCase();
            	}
                RegionBuilder rb2 = new DefineRegionBuilder(player, RedProtect.firstLocationSelections.get(player), RedProtect.secondLocationSelections.get(player), name, creator, new ArrayList<String>());
                if (rb2.ready()) {
                    Region r2 = rb2.build();
                    RPLang.sendMessage(player,RPLang.get("cmdmanager.region.created") + " " + r2.getName() + ".");
                    RedProtect.rm.add(r2, player.getWorld());
                    RedProtect.logger.addLog("(World "+r2.getWorld()+") Player "+player.getName()+" CLAIMED region "+r2.getName());
                }
                return cmdr;
        	}
        	
            if (args[0].equalsIgnoreCase("redefine")) {
                if (!player.hasPermission("redprotect.admin.redefine")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
                
                Region oldRect = RedProtect.rm.getRegion(args[1], player.getWorld());
                if (oldRect == null) {
                    RPLang.sendMessage(player, RPLang.get("cmdmanager.region.doesntexist") + ": " + args[1]);
                    return cmdr;
                }
                RedefineRegionBuilder rb = new RedefineRegionBuilder(player, oldRect, RedProtect.firstLocationSelections.get(player), RedProtect.secondLocationSelections.get(player));
                if (rb.ready()) {
                    Region r2 = rb.build();
                    RPLang.sendMessage(player,RPLang.get("cmdmanager.region.redefined") + " " + r2.getName() + ".");
                    RedProtect.rm.remove(oldRect);
                    RedProtect.rm.add(r2, player.getWorld());
                    RedProtect.logger.addLog("(World "+r2.getWorld()+") Player "+player.getName()+" REDEFINED region "+r2.getName());
                }
                return cmdr;
            }
                
            /*
            if  (args[0].equalsIgnoreCase("setconfig") && args[1].equalsIgnoreCase("list")){
        		if (!player.hasPermission("redprotect.admin.setconfig")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
        		
    			RPLang.sendMessage(player,TextColors.AQUA + "=========== Config Sections: ===========");
        		for (String section:RedProtect.cfgs.getValues(false).keySet()){
        			if (section.contains("debug-messages") || section.contains("file-type")){
        				RPLang.sendMessage(player,TextColors.YELLOW + section + " : " + TextColors.GREEN + RedProtect.cfgs.get(section).toString());
        			}         			
        		} 
        		RPLang.sendMessage(player,TextColors.CYAN + "====================================");
        		return cmdr;
            }
            */
            
            if (args[0].equalsIgnoreCase("setcreator")) {
            	Region r = RedProtect.rm.getTopRegion(player.getLocation());
            	if (r != null && player.hasPermission("redprotect.admin.setcreator")){
            		String old = RPUtil.UUIDtoPlayer(r.getCreator());
            		r.setCreator(RPUtil.PlayerToUUID(args[1]));   
            		RPLang.sendMessage(player, RPLang.get("cmdmanager.creatorset").toString().replace("{old}", old).replace("{new}", RPUtil.UUIDtoPlayer(r.getCreator())));  
            		RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" SETCREATOR of region "+r.getName()+" from "+old+" to "+args[1]);
            	}
            	return cmdr;
        	}
        }
        
        if (args.length == 3) { 
        	/*
        	if ((args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("fl")) && args[1].equalsIgnoreCase("gui-edit")) {
        		if (player.hasPermission("redprotect.gui.edit")){
        			int MaxSlot = 0;
        			try{
        				MaxSlot = 9*Integer.parseInt(args[2]);
        				if (MaxSlot > 54 || MaxSlot < RedProtect.cfgs.getGuiMaxSlot()){
        					RPLang.sendMessage(player, "gui.edit.invalid-lines");
        					return cmdr;
        				}
        			} catch(NumberFormatException e){
        				RPLang.sendMessage(player, "cmdmanager.region.invalid.number");
        				return cmdr;
        			}
        			Region r = RedProtect.rm.getTopRegion(player.getLocation());
        			if (r != null){
        				RPGui gui = new RPGui(RPLang.get("gui.editflag"), player, r, RedProtect.plugin, true, MaxSlot);
    					gui.open();
        			} else {
        				RPLang.sendMessage(player, "cmdmanager.region.todo.that");
        			}
        			return cmdr;
        		}        		
        	}
        	*/
        	
        	//rp claim [regionName] [owner]
        	if (args[0].equalsIgnoreCase("claim")){
        		if (!player.hasPermission("redprotect.admin.claim")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
                String name = args[1];
                String creator = player.getUniqueId().toString();
                List<String> addedOwners = new ArrayList<String>();
                addedOwners.add(RPUtil.PlayerToUUID(args[2]));
                if (!RedProtect.OnlineMode){
                	creator = player.getName().toLowerCase();
            	}                
                RegionBuilder rb2 = new DefineRegionBuilder(player, RedProtect.firstLocationSelections.get(player), RedProtect.secondLocationSelections.get(player), name, creator, addedOwners);
                if (rb2.ready()) {
                    Region r2 = rb2.build();
                    RPLang.sendMessage(player,RPLang.get("cmdmanager.region.created") + " " + r2.getName() + ".");
                    RedProtect.rm.add(r2, player.getWorld());
                    RedProtect.logger.addLog("(World "+r2.getWorld()+") Player "+player.getName()+" CLAIMED region "+r2.getName());
                }
                return cmdr;
        	}
        	
        	// - /rp copyflag from to
    		if  (args[0].equalsIgnoreCase("copyflag")){
    			if (!player.hasPermission("redprotect.admin.copyflag")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
    			World w = player.getWorld();
    			Region from = RedProtect.rm.getRegion(args[1], w);
    			Region to = RedProtect.rm.getRegion(args[2], w);
    			if (from == null){    				
    				RPLang.sendMessage(player,RPLang.get("cmdmanager.region.doesntexist") + ": " + args[1]);
    				return cmdr;
    			}
    			if (to == null){    				
    				RPLang.sendMessage(player,RPLang.get("cmdmanager.region.doesntexist") + ": " + args[2]);
    				return cmdr;
    			}
    			for (String key:from.flags.keySet()){
        			to.setFlag(key, from.flags.get(key));
    			}
    			RPLang.sendMessage(player,RPLang.get("cmdmanager.region.flag.copied") + args[1] + " > " + args[2]);
    			RedProtect.logger.addLog("Player "+player.getName()+" Copied FLAGS from "+ args[1] + " to " + args[2]);
    			return cmdr;
    		}
    		
    		/*
        	if  (args[0].equalsIgnoreCase("setconfig")){
        		if (!player.hasPermission("redprotect.admin.setconfig")) {
                    RPLang.sendMessage(player, "no.permission");
                    return cmdr;
                }
        		if (RedProtect.cfgs.contains(args[1])){
        			Object from = RedProtect.cfgs.get(args[1]); 
        			if (args[2].equals("true") || args[2].equals("false")){
        				RedProtect.cfgs.set(args[1], Boolean.parseBoolean(args[2]));
        			} else {
        				try {
            				int value = Integer.parseInt(args[2]);
            				RedProtect.cfgs.set(args[1], value);
            		    } catch(NumberFormatException ex){
            		    	RedProtect.cfgs.set(args[1], args[2]);
            		    }
        			}
        			RPLang.sendMessage(player,RPLang.get("cmdmanager.configset") + " " + from.toString() + " > " + args[2]);
        			RedProtect.cfgs.save();
        			return cmdr;
        		} else {
        			RPLang.sendMessage(player,RPLang.get("cmdmanager.confignotset") + " " + args[1]);
        			return cmdr;
        		}        		
        	}
        	*/
        }   
        
        //rp expand-vert [region] [world]
        if (args[0].equalsIgnoreCase("expand-vert") || args[0].equalsIgnoreCase("ev")){
    		if (!player.hasPermission("redprotect.admin.expandvert")) {
                RPLang.sendMessage(player, "no.permission");
                return cmdr;
            }
    		Region r = null;
    		//rp expand-vert
    		if (args.length == 1){
    			r = RedProtect.rm.getTopRegion(player.getLocation());
    			if (r == null){
        			RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    				return cmdr;
        		}
    		} else 
    		//rp expand-vert [region]	
    		if (args.length == 2){
    			r = RedProtect.rm.getRegion(args[1], player.getWorld());
    			if (r == null){
        			RPLang.sendMessage(player, RPLang.get("cmdmanager.region.doesntexist") + ": " + args[1]);
    				return cmdr;
        		}	
    		} else
    		//rp expand-vert [region] [world]
    		if (args.length == 3){
                if (!Sponge.getServer().getWorld(args[2]).isPresent()){
                	RPLang.sendMessage(player, "cmdmanager.region.invalidworld");
                	return cmdr;
    			}
    			r = RedProtect.rm.getRegion(args[1], Sponge.getServer().getWorld(args[2]).get()); 
    			if (r == null){
        			RPLang.sendMessage(player, RPLang.get("cmdmanager.region.doesntexist") + ": " + args[1]);
    				return cmdr;
        		}	
    		} else {
    			RPLang.sendMessage(player, "cmdmanager.help.expandvert");
    			return cmdr;
    		}
    		
    		r.setMaxY(256);
    		r.setMinY(0);
    		RPLang.sendMessage(player, RPLang.get("cmdmanager.region.expandvert.success").toString().replace("{region}", r.getName()).replace("{miny}", String.valueOf(r.getMinY())).replace("{maxy}", String.valueOf(r.getMaxY())));
    		return cmdr;
    	}
        
        //rp setmaxy <size> [region] [world]
    	if (args[0].equalsIgnoreCase("setmaxy")){
    		if (!player.hasPermission("redprotect.admin.setmaxy")) {
                RPLang.sendMessage(player, "no.permission");
                return cmdr;
            }
    		
    		Region r = null;
    		//rp setmaxy <size>
    		if (args.length == 2){
    			r = RedProtect.rm.getTopRegion(player.getLocation()); 
    			if (r == null){
        			RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    				return cmdr;
        		}
    		} else
    		//rp setmaxy <size> [region]
    		if (args.length == 3){
    			r = RedProtect.rm.getRegion(args[2], player.getWorld()); 
    			if (r == null){
        			RPLang.sendMessage(player, RPLang.get("cmdmanager.region.doesntexist") + ": " + args[2]);
    				return cmdr;
        		}
    		} else
    		//rp setmaxy <size> [region] [world]
    		if (args.length == 4){
    			if (!Sponge.getServer().getWorld(args[3]).isPresent()){
    				RPLang.sendMessage(player, "cmdmanager.region.invalidworld");
                	return cmdr;
    			}
    			r = RedProtect.rm.getRegion(args[2], Sponge.getServer().getWorld(args[3]).get()); 
    			if (r == null){
        			RPLang.sendMessage(player, RPLang.get("cmdmanager.region.doesntexist") + ": " + args[2]);
    				return cmdr;
        		}
    		} else {
    			RPLang.sendMessage(player, "cmdmanager.help.setmaxy");
    			return cmdr;
    		}    		
    		
    		String from = String.valueOf(r.getMaxY());
    		
    		try{
    			int size = Integer.parseInt(args[1]);
    			if ((size - r.getMinY()) <= 1){
        			RPLang.sendMessage(player, "cmdmanager.region.ysiszesmatch");
        			return cmdr;
        		}
    			r.setMaxY(size);
    			RPLang.sendMessage(player, RPLang.get("cmdmanager.region.setmaxy.success").toString().replace("{region}", r.getName()).replace("{fromsize}", from).replace("{size}", String.valueOf(size)));
    			RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" SETMAXY of region "+r.getName()+" to "+args[1]);
    			return cmdr;
    		} catch (NumberFormatException e){
    			RPLang.sendMessage(player, "cmdmanager.region.invalid.number");
    			return cmdr;
    		}
    	}
    	
    	//rp setmaxy <size> [region] [world]
    	if (args[0].equalsIgnoreCase("setminy")){
    		if (!player.hasPermission("redprotect.admin.setminy")) {
                RPLang.sendMessage(player, "no.permission");
                return cmdr;
            }
    		
    		Region r = null;
    		//rp setmaxy <size>
    		if (args.length == 2){
    			r = RedProtect.rm.getTopRegion(player.getLocation()); 
    			if (r == null){
        			RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    				return cmdr;
        		}
    		} else
    		//rp setmaxy <size> [region]
    		if (args.length == 3){
    			r = RedProtect.rm.getRegion(args[2], player.getWorld()); 
    			if (r == null){
        			RPLang.sendMessage(player, RPLang.get("cmdmanager.region.doesntexist") + ": " + args[2]);
    				return cmdr;
        		}
    		} else
    		//rp setmaxy <size> [region] [world]
    		if (args.length == 4){
    			if (!Sponge.getServer().getWorld(args[3]).isPresent()){
    				RPLang.sendMessage(player, "cmdmanager.region.invalidworld");
                	return cmdr;
    			}
    			r = RedProtect.rm.getRegion(args[2], Sponge.getServer().getWorld(args[3]).get()); 
    			if (r == null){
        			RPLang.sendMessage(player, RPLang.get("cmdmanager.region.doesntexist") + ": " + args[2]);
    				return cmdr;
        		}
    		} else {
    			RPLang.sendMessage(player, "cmdmanager.help.setminy");
    			return cmdr;
    		}
    		    		
    		String from = String.valueOf(r.getMinY());
    		try{
    			int size = Integer.parseInt(args[1]);
    			if ((r.getMaxY() - size) <= 1){
        			RPLang.sendMessage(player, "cmdmanager.region.ysiszesmatch");
        			return cmdr;
        		}
    			r.setMinY(size);
        		RPLang.sendMessage(player, RPLang.get("cmdmanager.region.setminy.success").toString().replace("{region}", r.getName()).replace("{fromsize}", from).replace("{size}", String.valueOf(size)));
        		RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" SETMINY of region "+r.getName()+" to "+args[1]);
        		return cmdr;
    		} catch (NumberFormatException e){
    			RPLang.sendMessage(player, "cmdmanager.region.invalid.number");
    			return cmdr;
    		}        		
    	}
        
    	/*
    	if (args[0].equalsIgnoreCase("buy") && player.hasPermission("redprotect.eco.buy")){
    		if (!RedProtect.Vault){
    			return cmdr;
    		}    		
    		Region r = RedProtect.rm.getTopRegion(player.getLocation());
        	if (r == null){
    			RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    			return cmdr;
    		}        	
        	if (!r.isForSale()){
    			RPLang.sendMessage(player, "economy.region.buy.notforsale");
    			return cmdr;
    		} 
        	
    		if (args.length == 1){
    			buyHandler(player, r.getValue(), r);
    			RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" BUY region "+r.getName()+" for "+r.getValue());
				return cmdr;    			
    		}    				    		
    	}
        
    	
        if (args[0].equalsIgnoreCase("sell") && player.hasPermission("redprotect.eco.sell")){  
        	if (!RedProtect.Vault){
    			return cmdr;
    		}        	
        	Region r = RedProtect.rm.getTopRegion(player.getLocation());
        	if (r == null){
    			RPLang.sendMessage(player, "cmdmanager.region.todo.that");
    			return cmdr;
    		}        	
        	if (r.isForSale()){
    			RPLang.sendMessage(player, "economy.region.sell.already");
    			return cmdr;
    		} 
        	
        	if (args.length == 1){
        		sellHandler(r, player, r.getCreator(), RPEconomy.getRegionValue(r));
        		RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" SELL region "+r.getName()+" for "+RPEconomy.getRegionValue(r));
        		return cmdr;
        	}        	
        	
        	if (args.length == 2){         
        		// rp sell <value/player>
        		try {
        			long value = Long.valueOf(args[1]);
    				if (player.hasPermission("redprotect.eco.setvalue")){
    					sellHandler(r, player, RPUtil.PlayerToUUID(r.getCreator()), value);
    					RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" SELL region "+r.getName()+" for "+RPEconomy.getRegionValue(r));
    					return cmdr;
    				}    				
    			} catch (NumberFormatException e){
    				if (player.hasPermission("redprotect.eco.others")){
    					sellHandler(r, player, RPUtil.PlayerToUUID(args[1]), RPEconomy.getRegionValue(r));
    					RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" SELL region "+r.getName()+" in name of player "+args[1]+" for "+RPEconomy.getRegionValue(r));
            			return cmdr;
                	}   				
    			}
        	} 
        	
        	if (args.length == 3){   
        		// rp sell player value
        		try {
        			long value = Long.valueOf(args[2]);
    				if (player.hasPermission("redprotect.eco.setvalue")){
    					sellHandler(r, player, RPUtil.PlayerToUUID(args[1]), value);
    					RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+player.getName()+" SELL region "+r.getName()+" in name of player "+args[1]+" for "+value);
    					return cmdr;
    				}    				
    			} catch (NumberFormatException e){    
    				RPLang.sendMessage(player, "cmdmanager.eco.notdouble");
            		return cmdr;
    			}
        	}
        }
        */
    	
        if (args[0].equalsIgnoreCase("tp")) {
        	if (args.length == 1) {
        		RPLang.sendMessage(player, "cmdmanager.help.tp");
        		return cmdr;
        	}
        	
            if (args.length == 2) {
            	handletp(player, args[1], player.getWorld().getName(), null);
            	return cmdr;
        	}

            if (args.length == 3) {
            	handletp(player, args[1], args[2], null);
            	return cmdr;
            }
            
            if (args.length == 4) {
            	// /rp tp <player> <region> <world>
            	Player play = RedProtect.serv.getPlayer(args[1]).get();
            	if (play != null){
            		handletp(player, args[2], args[3], play);
            		return cmdr;
            	} else {
            		RPLang.sendMessage(player, RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", args[1]));
            		RPLang.sendMessage(player, "cmdmanager.help.tp");
            		return cmdr;
            	}
            }
        }
        
        if (args[0].equalsIgnoreCase("limit") || args[0].equalsIgnoreCase("limitremaining") || args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("l")) {
            if (!RedProtect.ph.hasPerm(player, "redprotect.own.limit")) {
                RPLang.sendMessage(player, "no.permission");
                return cmdr;
            }
            
            if (args.length == 1) {
            	int limit = RedProtect.ph.getPlayerLimit(player);
                if (limit < 0 || RedProtect.ph.hasPerm(player, "redprotect.limit.blocks.unlimited")) {
                    RPLang.sendMessage(player,"cmdmanager.nolimit");
                    return cmdr;
                }
                String uuid = player.getUniqueId().toString();
                if (!RedProtect.OnlineMode){
                	uuid = player.getName().toLowerCase();
                }
                int currentUsed = RedProtect.rm.getTotalRegionSize(uuid);
                RPLang.sendMessage(player,RPLang.get("cmdmanager.yourarea").toString() + currentUsed + RPLang.get("general.color") + "/&e" + limit + RPLang.get("general.color"));
                return cmdr;
            }            

            if (!RedProtect.ph.hasPerm(player, "redprotect.other.limit")) {
                RPLang.sendMessage(player, "no.permission");
                return cmdr;
            }
            
            if (args.length == 2) {          	
            	Player offp = Sponge.getServer().getPlayer(args[1]).get();
            	if (offp == null){
            		RPLang.sendMessage(player,RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", args[1]));
            		return cmdr;
            	}
            	int limit = RedProtect.ph.getPlayerLimit(offp);
                if (limit < 0 || RedProtect.ph.hasPerm(offp, "redprotect.limit.blocks.unlimited")) {
                    RPLang.sendMessage(player, "cmdmanager.nolimit");
                    return cmdr;
                }
                
                int currentUsed = RedProtect.rm.getTotalRegionSize(RPUtil.PlayerToUUID(offp.getName()));
                RPLang.sendMessage(player,RPLang.get("cmdmanager.yourarea").toString() + currentUsed + RPLang.get("general.color") + "/&e" + limit + RPLang.get("general.color"));
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.limit"));
            return cmdr;
        }        
        
        if (args[0].equalsIgnoreCase("claimlimit") || args[0].equalsIgnoreCase("climit")  || args[0].equalsIgnoreCase("cl")) {
            if (!RedProtect.ph.hasPerm(player, "redprotect.own.claimlimit")) {
                RPLang.sendMessage(player, "no.permission");
                return cmdr;
            }
            
            if (args.length == 1) {
            	int limit = RedProtect.ph.getPlayerClaimLimit(player);
                if (limit < 0 || RedProtect.ph.hasPerm(player, "redprotect.claimunlimited")) {
                    RPLang.sendMessage(player,"cmdmanager.nolimit");
                    return cmdr;
                }

                int currentUsed = RedProtect.rm.getRegions(RPUtil.PlayerToUUID(player.getName()), player.getWorld()).size();
                RPLang.sendMessage(player,RPLang.get("cmdmanager.yourclaims").toString() + currentUsed + RPLang.get("general.color") + "/&e" + limit + RPLang.get("general.color"));
                return cmdr;
            }            

            if (!RedProtect.ph.hasPerm(player, "redprotect.other.claimlimit")) {
                RPLang.sendMessage(player, "no.permission");
                return cmdr;
            }
            
            if (args.length == 2) {          	
            	Player offp = Sponge.getServer().getPlayer(args[1]).get();
            	if (offp == null){
            		RPLang.sendMessage(player,RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", args[1]));
            		return cmdr;
            	}
            	int limit = RedProtect.ph.getPlayerClaimLimit(offp);
                if (limit < 0 || RedProtect.ph.hasPerm(offp, "redprotect.limit.claim.unlimited")) {
                    RPLang.sendMessage(player, "cmdmanager.nolimit");
                    return cmdr;
                }
                
                int currentUsed = RedProtect.rm.getRegions(RPUtil.PlayerToUUID(offp.getName()), offp.getWorld()).size();
                RPLang.sendMessage(player,RPLang.get("cmdmanager.yourclaims").toString() + currentUsed + RPLang.get("general.color") + "/&e" + limit + RPLang.get("general.color"));
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.claimlimit"));
            return cmdr;
        }      
        
        if (args[0].equalsIgnoreCase("welcome") || args[0].equalsIgnoreCase("wel")) {
            if (args.length >= 2) {
            	String wMessage = "";
            	if (args[1].equals("off")){
            		handleWelcome(player, wMessage);
            		return cmdr;
            	} else {
            		for (int i = 1; i < args.length; i++){
                		wMessage = wMessage+args[i]+" ";
                	}
                	handleWelcome(player, wMessage);
                    return cmdr;
            	}            	
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.welcome"));
            return cmdr;
        }         
        
        if (args[0].equalsIgnoreCase("priority") || args[0].equalsIgnoreCase("prior")) {
        	int prior = 0;    	
    			
        	if (args.length == 2) {
        		try {
        			prior = Integer.parseInt(args[1]);
            	} catch (NumberFormatException e){ 
        			RPLang.sendMessage(player, "cmdmanager.region.notnumber");
        			return cmdr; 
        		} 
        		handlePriority(player, prior);
                return cmdr;                  
        	}
        	
            if (args.length == 3) {
            	try {
        			prior = Integer.parseInt(args[2]);
            	} catch (NumberFormatException e){ 
        			RPLang.sendMessage(player, "cmdmanager.region.notnumber");
        			return cmdr; 
        		} 
        		handlePrioritySingle(player, prior, args[1]);
                return cmdr;         
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.priority"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
        	//rp del [region] [world]
            if (args.length == 1) {
                handleDelete(player);
                return cmdr;
            }
            if (args.length == 2) {
                handleDeleteName(player, args[1], "");
                return cmdr;
            }
            if (args.length == 3) {
                handleDeleteName(player, args[1], args[2]);
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.delete"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("i") || args[0].equalsIgnoreCase("info")) {
        	//rp info [region] [world]
            if (args.length == 1) {
                handleInfoTop(player);
                return cmdr;
            }
            if (args.length == 2) {
                handleInfo(player, args[1], "");
                return cmdr;
            }
            if (args.length == 3) {
                handleInfo(player, args[1], args[2]);
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.info"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("am") || args[0].equalsIgnoreCase("addmember")) {
            if (args.length == 2) {
                handleAddMember(player, args[1]);
                return cmdr;
            }
            if (args.length == 3) {
                handleAddMember(player, args[1]);
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.addmember"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("ao") || args[0].equalsIgnoreCase("addowner")) {
            if (args.length == 2) {
                handleAddOwner(player, args[1]);
                return cmdr;
            }
            if (args.length == 3) {
                handleAddOwner(player, args[1]);
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.addowner"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("rm") || args[0].equalsIgnoreCase("removemember")) {
            if (args.length == 2) {
                handleRemoveMember(player, args[1]);
                return cmdr;
            }
            if (args.length == 3) {
                handleRemoveMember(player, args[1]);
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.removemember"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("ro") || args[0].equalsIgnoreCase("removeowner")) {
            if (args.length == 2) {
                handleRemoveOwner(player, args[1]);
                return cmdr;
            }
            if (args.length == 3) {
                handleRemoveOwner(player, args[1]);
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.removeowner"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("rn") || args[0].equalsIgnoreCase("rename")) {
            if (args.length == 2) {
                handleRename(player, args[1]);
                return cmdr;
            }
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.rename"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("fl") || args[0].equalsIgnoreCase("flag")) {
        	Region r = RedProtect.rm.getTopRegion(player.getLocation());
        	
            if (args.length == 2) {            	
            	if (RedProtect.cfgs.getBool("flags-configuration.change-flag-delay.enable")){
            		if (RedProtect.cfgs.getStringList("flags-configuration.change-flag-delay.flags").contains(args[1])){
            			if (!RedProtect.changeWait.contains(r.getName()+args[1])){
            				RPUtil.startFlagChanger(r.getName(), args[1], player);
            				handleFlag(player, args[1], "", r);
            				return cmdr;
            			} else {
            				RPLang.sendMessage(player,RPLang.get("gui.needwait.tochange").toString().replace("{seconds}", RedProtect.cfgs.getString("flags-configuration.change-flag-delay.seconds")));	
							return cmdr;
            			}
            		}
            	}            	
                handleFlag(player, args[1], "", r);
                return cmdr;
            }
            
            if (args.length >= 3) {
            	String text = "";
            	for (int i = 2; i < args.length; i++){
            		text = text + " " + args[i];
            	}            	
            	if (RedProtect.cfgs.getBool("flags-configuration.change-flag-delay.enable")){
            		if (RedProtect.cfgs.getStringList("flags-configuration.change-flag-delay.flags").contains(args[1])){
            			if (!RedProtect.changeWait.contains(r.getName()+args[1])){
            				RPUtil.startFlagChanger(r.getName(), args[1], player);
            				handleFlag(player, args[1], text.substring(1), r);
            				return cmdr;
            			} else {
            				RPLang.sendMessage(player,RPLang.get("gui.needwait.tochange").toString().replace("{seconds}", RedProtect.cfgs.getString("flags-configuration.change-flag-delay.seconds")));	
							return cmdr;
            			}
            		}
            	}             	
                handleFlag(player, args[1], text.substring(1), r);
                return cmdr;
            }         
            
            RPLang.sendMessage(player,RPLang.get("correct.usage") + " " + RPLang.get("cmdmanager.help.flag"));
            return cmdr;
        }
        
        if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("ls")) {
        	//rp list
            if (args.length == 1) {
                handleList(player, RPUtil.PlayerToUUID(player.getName()), 1);
                return cmdr;
            }
            //rp list [player]
            if (args.length == 2) {
                handleList(player, RPUtil.PlayerToUUID(args[1]), 1);
                return cmdr;
            }   
            //rp list [player] [page]
            if (args.length == 3) {  
            	try{
                	int Page = Integer.parseInt(args[2]);                  
                    	handleList(player, RPUtil.PlayerToUUID(args[1]), Page);
                    	return cmdr;
                	} catch(NumberFormatException  e){
                        RPLang.sendMessage(player, "cmdmanager.region.listpage.error");   
                        return cmdr;
                }                 
            }
        }
        RPLang.sendMessage(player,RPLang.get("correct.command") + " &e/rp ?");   
        return cmdr;
    }
	
    /*
	private void buyHandler(Player player, long value, Region r) {		
		       		
		if (RPUtil.PlayerToUUID(player.getName()).equalsIgnoreCase(r.getCreator())){
			RPLang.sendMessage(player, "economy.region.buy.own");
			return;
		}
		
		Double money = RedProtect.econ.getBalance(player);
		if (money >= value){
			String creator = r.getCreator();
			String rname = r.getName();
			if (RPEconomy.BuyRegion(r, RPUtil.PlayerToUUID(player.getName()))){
				RedProtect.econ.withdrawPlayer(player, value);
				OfflinePlayer offp = RedProtect.serv.getOfflinePlayer(RPUtil.UUIDtoPlayer(creator));
				if (!creator.equals("server") && offp != null){
					RedProtect.econ.depositPlayer(offp, value);
					if (offp.isOnline()){
						RPLang.sendMessage((Player) offp, RPLang.get("economy.region.buy.bought").replace("{player}", player.getName()).replace("{region}", rname).replace("{world}", r.getWorld()));
					}
				}
				RPLang.sendMessage(player, RPLang.get("economy.region.buy.success").replace("{region}", r.getName()).replace("{value}", String.valueOf(value)).replace("{ecosymbol}", RedProtect.cfgs.getEcoString("economy-name")));
				return;
			} else {
				RPLang.sendMessage(player, "economy.region.error");
				return;
			}
		} else {
			RPLang.sendMessage(player, "economy.region.buy.nomoney");
			return;
		} 		
	}
	

	private void sellHandler(Region r, Player player, String creator, long value) {       		
		
		if (r.isOwner(player) || player.hasPermission("redprotect.eco.admin")){
			if (RPEconomy.putToSell(r, creator, value)){
				RPLang.sendMessage(player, "economy.region.sell.success");
			} else {
				RPLang.sendMessage(player, "economy.region.error");
			}
		} else {
			RPLang.sendMessage(player, "economy.region.sell.own");
		}		
	} 
	*/
	
    private static void handlePrioritySingle(Player p, int prior, String region) {
    	Region r = RedProtect.rm.getRegion(region, p.getWorld());
    	if (RedProtect.ph.hasRegionPerm(p, "delete", r)) {
    		if (r != null){
    			r.setPrior(prior);
    			RPLang.sendMessage(p,RPLang.get("cmdmanager.region.priority.set").toString().replace("{region}", r.getName()) + " " + prior);
    			RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" SET PRIORITY of region "+r.getName()+" to "+prior);
    		} else {
    			RPLang.sendMessage(p, "cmdmanager.region.todo.that");
        		return;
    		}
    	}
	}
	
    private static void handlePriority(Player p, int prior) {
    	Region r = RedProtect.rm.getTopRegion(p.getLocation());
    	if (RedProtect.ph.hasRegionPerm(p, "delete", r)) {
    		if (r != null){
    			r.setPrior(prior);
    			RPLang.sendMessage(p,RPLang.get("cmdmanager.region.priority.set").toString().replace("{region}", r.getName()) + " " + prior);
    			RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" SET PRIORITY of region "+r.getName()+" to "+prior);
    		} else {
    			RPLang.sendMessage(p, "cmdmanager.region.todo.that");
        		return;
    		}
    	}		
	}

    private static void handleDelete(Player p) {
		Region r = RedProtect.rm.getTopRegion(p.getLocation());
        if (RedProtect.ph.hasRegionPerm(p, "delete", r)) {
            if (r == null) {
                sendNotInRegionMessage(p);
                return;
            }
            String rname = r.getName();
            String w = r.getWorld();
            RedProtect.rm.remove(r);
            RPLang.sendMessage(p,RPLang.get("cmdmanager.region.deleted") +" "+ rname);
            RedProtect.logger.addLog("(World "+w+") Player "+p.getName()+" REMOVED region "+rname);
        }
        else {
            sendNoPermissionMessage(p);
        }
    }
	
	private static void handleDeleteName(Player p, String rname, String world) {
		Region r = RedProtect.rm.getRegion(rname, p.getWorld());
		if (!world.equals("")){
			if (Sponge.getServer().getWorld(world).isPresent()){
				r = RedProtect.rm.getRegion(rname, Sponge.getServer().getWorld(world).get());
			} else {
				RPLang.sendMessage(p, "cmdmanager.region.invalidworld");
				return;
			}
		}
		
        if (RedProtect.ph.hasRegionPerm(p, "delete", r)) {
            if (r == null) {
            	RPLang.sendMessage(p, RPLang.get("cmdmanager.region.doesntexist") + ": " + rname);
                return;
            }
            RedProtect.rm.remove(r);
            RPLang.sendMessage(p,RPLang.get("cmdmanager.region.deleted") +" "+ rname);
            RedProtect.logger.addLog("(World "+world+") Player "+p.getName()+" REMOVED region "+rname);
        }
        else {
            sendNoPermissionMessage(p);
        }
    }
    
	private static void handleInfoTop(Player p) {  
    	Region r = RedProtect.rm.getTopRegion(p.getLocation());
    	Map<Integer, Region> groupr = RedProtect.rm.getGroupRegion(p.getLocation());
    	if (RedProtect.ph.hasRegionPerm(p, "info", r) || r.isForSale()) {
            if (r == null) {
                sendNotInRegionMessage(p);
                return;
            }
            p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "--------------- [&e" + r.getName() + RPLang.get("general.color") + "] ---------------"));
            p.sendMessage(r.info());
            p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "----------------------------------"));
            if (groupr.size() > 1){
            	p.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.moreregions")));
                for (Region regs:groupr.values()){
                	if (regs != r){
                		p.sendMessage(RPUtil.toText(RPLang.get("region.name") + " " + regs.getName() + " " + RPLang.get("region.priority") + " " + regs.getPrior()));
                	}            	
                }
            }
        }
        else {
            sendNoPermissionMessage(p);
        }
       
    }
    
	private static void handleInfo(Player p, String region, String world) {
		Region r = RedProtect.rm.getRegion(region, p.getWorld());
		if (!world.equals("")){
			if (Sponge.getServer().getWorld(world).isPresent()){
				r = RedProtect.rm.getRegion(region, Sponge.getServer().getWorld(world).get());
			} else {
				RPLang.sendMessage(p, "cmdmanager.region.invalidworld");
				return;
			}
		}
    	if (RedProtect.ph.hasRegionPerm(p, "info", r) || r.isForSale()) {
            if (r == null) {
                sendNotInRegionMessage(p);
                return;
            }
            p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "--------------- [&e" + r.getName() + RPLang.get("general.color") + "] ---------------"));
            p.sendMessage(r.info());
            p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "----------------------------------"));
        }
        else {
            sendNoPermissionMessage(p);
        }        
    }
    
	@SuppressWarnings("deprecation")
	private static void handleAddMember(Player p, String sVictim) {
    	Region r = RedProtect.rm.getTopRegion(p.getLocation());
        if (RedProtect.ph.hasRegionPerm(p, "addmember", r)) {
            if (r == null) {
                sendNotInRegionMessage(p);
                return;
            }
            String VictimUUID = RPUtil.PlayerToUUID(sVictim);
            if (RPUtil.UUIDtoPlayer(VictimUUID) == null){
            	RPLang.sendMessage(p,RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", sVictim));
            	return;
            }

            Player pVictim = RedProtect.serv.getPlayer(sVictim).get();
            
            if (r.getOwners().contains(VictimUUID)) {
                r.removeOwner(VictimUUID);
                r.addMember(VictimUUID);
                RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" ADDED MEMBER "+RPUtil.UUIDtoPlayer(VictimUUID)+" to region "+r.getName());
                RPLang.sendMessage(p,RPLang.get("general.color") + sVictim + " " + RPLang.get("cmdmanager.region.owner.demoted") + " " + r.getName());
                if (pVictim != null && pVictim.isOnline()) {
                	RPLang.sendMessage(pVictim, RPLang.get("cmdmanager.region.owner.youdemoted").toString().replace("{region}", r.getName()) + " " + p.getName());
                }
            } else if (!r.getMembers().contains(VictimUUID)) {
                r.addMember(VictimUUID);
                RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" ADDED MEMBER "+RPUtil.UUIDtoPlayer(VictimUUID)+" to region "+r.getName());
                RPLang.sendMessage(p,RPLang.get("general.color") + sVictim + " " + RPLang.get("cmdmanager.region.member.added") + " " + r.getName());
                if (pVictim != null && pVictim.isOnline() && !pVictim.equals(p)) {
                    RPLang.sendMessage(pVictim, RPLang.get("cmdmanager.region.member.youadded").toString().replace("{region}", r.getName()) + " " + p.getName());
                }
            } else {
                RPLang.sendMessage(p,"&c" + sVictim + " " + RPLang.get("cmdmanager.region.member.already"));
            }
        } else {
            sendNoPermissionMessage(p);
        }
    }
    
	@SuppressWarnings("deprecation")
	private static void handleAddOwner(Player p, String sVictim) {
    	Region r = RedProtect.rm.getTopRegion(p.getLocation());
        if (RedProtect.ph.hasRegionPerm(p, "addowner", r)) {
            if (r == null) {
                sendNotInRegionMessage(p);
                return;
            }
            
            Player pVictim = RedProtect.serv.getPlayer(sVictim).get();
            
            String VictimUUID = RPUtil.PlayerToUUID(sVictim);
            if (RPUtil.UUIDtoPlayer(VictimUUID) == null){
            	RPLang.sendMessage(p,RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", sVictim));
            	return;
            }
            if (!r.getOwners().contains(VictimUUID)) {
                r.addOwner(VictimUUID);
                RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" ADDED OWNER "+RPUtil.UUIDtoPlayer(VictimUUID)+" to region "+r.getName());
                RPLang.sendMessage(p,RPLang.get("general.color") + sVictim + " " + RPLang.get("cmdmanager.region.owner.added") + " " + r.getName());
                if (pVictim != null && pVictim.isOnline() && !pVictim.equals(p)) {
                    RPLang.sendMessage(pVictim, RPLang.get("cmdmanager.region.owner.youadded").toString().replace("{region}", r.getName()) + " " + p.getName());
                }
            }
            else {
                RPLang.sendMessage(p,"&c" + sVictim + " " + RPLang.get("cmdmanager.region.owner.already"));
            }
        }
        else {
            sendNoPermissionMessage(p);
        }
    }
    
	@SuppressWarnings("deprecation")
	private static void handleRemoveMember(Player p, String sVictim) {
    	Region r = RedProtect.rm.getTopRegion(p.getLocation());
        if (RedProtect.ph.hasRegionPerm(p, "removemember", r)) {
            if (r == null) {
                sendNotInRegionMessage(p);
                return;
            }
            
            Player pVictim = RedProtect.serv.getPlayer(sVictim).get();
            
            String VictimUUID = RPUtil.PlayerToUUID(sVictim);
            if (RPUtil.UUIDtoPlayer(VictimUUID) == null){
            	RPLang.sendMessage(p,RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", sVictim));
            	return;
            }
            if (r.getMembers().contains(VictimUUID) || r.getOwners().contains(VictimUUID)) {
                RPLang.sendMessage(p,RPLang.get("general.color") + sVictim + " " + RPLang.get("cmdmanager.region.member.removed") + " " + r.getName());
                r.removeMember(VictimUUID);
                RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" REMOVED MEMBER "+RPUtil.UUIDtoPlayer(VictimUUID)+" to region "+r.getName());
                if (pVictim != null && pVictim.isOnline() && !pVictim.equals(p)) {
                    RPLang.sendMessage(pVictim, RPLang.get("cmdmanager.region.member.youremoved").toString().replace("{region}", r.getName()) + " " + p.getName());
                }
            } else {
                RPLang.sendMessage(p,"&c" + sVictim + " " + RPLang.get("cmdmanager.region.member.notmember"));
            }
        }
        else {
            sendNoPermissionMessage(p);
        }
    }
    
	@SuppressWarnings("deprecation")
	private static void handleRemoveOwner(Player p, String sVictim) {
    	Region r = RedProtect.rm.getTopRegion(p.getLocation());
		Region rLow = RedProtect.rm.getLowRegion(p.getLocation());
		Map<Integer,Region> regions = RedProtect.rm.getGroupRegion(p.getLocation());
        if (RedProtect.ph.hasRegionPerm(p, "removeowner", r)) {
            if (r == null) {
                sendNotInRegionMessage(p);
                return;
            }
            
            Player pVictim = RedProtect.serv.getPlayer(sVictim).get();
            
            String VictimUUID = RPUtil.PlayerToUUID(sVictim);
            if (RPUtil.UUIDtoPlayer(VictimUUID) == null){
            	RPLang.sendMessage(p,RPLang.get("cmdmanager.noplayer.thisname").toString().replace("{player}", sVictim));
            	return;
            }

            if (!RedProtect.ph.hasRegionPerm(p, "removeowner", rLow) || rLow != r && (regions.size() > 1 && rLow.getOwners().contains(VictimUUID))){
        		RPLang.sendMessage(p,RPLang.get("cmdmanager.region.owner.cantremove.lowregion").toString().replace("{player}", sVictim) + " " +rLow.getName());
            	return;
        	}	  
            if (r.getOwners().contains(VictimUUID)) {
                if (r.ownersSize() > 1) {
                    RPLang.sendMessage(p,RPLang.get("general.color") + sVictim + " " + RPLang.get("cmdmanager.region.member.added") + " " +r.getName());
                    r.removeOwner(VictimUUID);
                    r.addMember(VictimUUID);
                    RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" ADDED MEMBER "+RPUtil.UUIDtoPlayer(VictimUUID)+" to region "+r.getName());
                    if (pVictim != null && pVictim.isOnline() && !pVictim.equals(p)) {
                        RPLang.sendMessage(pVictim, RPLang.get("cmdmanager.region.owner.removed").toString().replace("{region}", r.getName())+ " " + p.getName());
                    }
                }
                else {
                    RPLang.sendMessage(p,RPLang.get("cmdmanager.region.owner.cantremove").toString().replace("{player}", p.getName()));
                }
            }
            else {
                RPLang.sendMessage(p,"&c" + sVictim + " " + RPLang.get("cmdmanager.region.owner.notowner"));
            }
        }
        else {
            sendNoPermissionMessage(p);
        }
    }
    
	private static void handleRename(Player p, String newName) {
    	Region r = RedProtect.rm.getTopRegion(p.getLocation());
        if (RedProtect.ph.hasRegionPerm(p, "rename", r)) {
            if (r == null) {
                sendNotInRegionMessage(p);
                return;
            }
            if (RedProtect.rm.getRegion(newName, p.getWorld()) != null) {
                RPLang.sendMessage(p, "cmdmanager.region.rename.already");
                return;
            }
            if (newName.length() < 2 || newName.length() > 16) {
                RPLang.sendMessage(p, "cmdmanager.region.rename.invalid");
                return;
            }
            if (newName.contains(" ")) {
                RPLang.sendMessage(p, "cmdmanager.region.rename.spaces");
                return;
            }            
            String oldname = r.getName();
            RedProtect.rm.renameRegion(newName, r);
            RPLang.sendMessage(p,RPLang.get("cmdmanager.region.rename.newname") + " " + newName);
            RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" RENAMED region "+oldname+" to "+newName);
        }
        else {
            RPLang.sendMessage(p, "no.permission");
        }
    }
    
    // TODO Flag Handler
	private static void handleFlag(Player p, String flag, String value, Region r) {  	
    	if (flag.equalsIgnoreCase("?")){
    		sendFlagHelp(p); 
    		return;
    	}    	

    	if (r == null) {
            sendNotInRegionMessage(p);
            return;
        } 
    	
    	Object objflag = RPUtil.parseObject(value);
    	
    	if (RedProtect.ph.hasPerm(p, "redprotect.flag."+ flag) || flag.equalsIgnoreCase("info")) {                
            if (r.isOwner(p) || RedProtect.ph.hasPerm(p, "redprotect.admin.flag."+flag)) {
            	
            	if (flag.equalsIgnoreCase("info") || flag.equalsIgnoreCase("i")) {            
                    p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------[" + RPLang.get("cmdmanager.region.flag.values") + "]------------"));
                    p.sendMessage(r.getFlagInfo());
                    p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------------------------------"));
                    return;
                }  
            	
            	if (value.equalsIgnoreCase("remove")){
            		if (RedProtect.cfgs.AdminFlags.contains(flag) && r.flags.containsKey(flag)){
            			r.removeFlag(flag);
                        RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.removed").toString().replace("{flag}", flag).replace("{region}", r.getName())); 
                        RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" REMOVED FLAG "+flag+" of region "+r.getName());
            			return;
            		} else {
                        RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.notset").toString().replace("{flag}", flag)); 
                        return;
            		}
            	}
            	
            	if (r.flagExists("for-sale") && flag.equalsIgnoreCase("for-sale")){
            		RPLang.sendMessage(p, "cmdmanager.eco.changeflag");
            		return;
            	}
            	
            	if (!value.equals("")){
            		if (RedProtect.cfgs.getDefFlagsValues().containsKey(flag)) {
            			if (objflag instanceof Boolean){
            				r.setFlag(flag, objflag);
                            RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.set").toString().replace("{flag}", "'"+flag+"'") + " " + r.getFlagBool(flag));
                            RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" SET FLAG "+flag+" of region "+r.getName()+" to "+r.getFlagString(flag));
                            return;
            			} else {
            				RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usage") + " <true/false>");
            				return;
            			}                		
                	} 
                	
                	if (RedProtect.cfgs.AdminFlags.contains(flag)) {
                		if (!validate(flag, objflag)){
                			SendFlagUsageMessage(p, flag);               			
                			return;
                		}
                		r.setFlag(flag, objflag);
                		RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.set").toString().replace("{flag}", "'"+flag+"'") + " " + r.getFlagString(flag));
            			RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" SET FLAG "+flag+" of region "+r.getName()+" to "+r.getFlagString(flag));                     
                        return;               		
                	} 

                	
                	if (RedProtect.cfgs.AdminFlags.contains(flag)){
                		SendFlagUsageMessage(p, flag); 
            		} else {
                    	RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usage") + " <true/false>");
            		}
                	sendFlagHelp(p);
                	return; 

            	} else {
            		if (RedProtect.cfgs.getDefFlagsValues().containsKey(flag)) {
            			r.setFlag(flag, !r.getFlagBool(flag));
                        RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.set").toString().replace("{flag}", "'"+flag+"'") + " " + r.getFlagBool(flag));
            			RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" SET FLAG "+flag+" of region "+r.getName()+" to "+r.getFlagString(flag));
                        return;
            		} else {
            			if (RedProtect.cfgs.AdminFlags.contains(flag)){
            				SendFlagUsageMessage(p, flag);  
                		} else {
                        	RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usage") + " <true/false>");
                		}
                    	sendFlagHelp(p);
            		}
            	}
            	
            } else {
                RPLang.sendMessage(p,"cmdmanager.region.flag.nopermregion");
            }
        } else {
        	RPLang.sendMessage(p, "cmdmanager.region.flag.noperm");
        }                      
    }
    
	private static void SendFlagUsageMessage(Player p, String flag) {
		if (flag.equalsIgnoreCase("effects")){                				
			RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usage"+flag).toString());
		} else if (flag.equalsIgnoreCase("allow-enter-items")){                				
			RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usage"+flag).toString());   
		} else if (flag.equalsIgnoreCase("gamemode")){                				
			RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usage"+flag).toString()); 
		} else if (flag.equalsIgnoreCase("deny-enter-items")){                				
			RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usage"+flag).toString());
		} else if (flag.equalsIgnoreCase("allow-cmds") || flag.equalsIgnoreCase("deny-cmds") || flag.equalsIgnoreCase("allow-break") || flag.equalsIgnoreCase("allow-place")){                				
			RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usage"+flag).toString());
		} else {
			RPLang.sendMessage(p,RPLang.get("cmdmanager.region.flag.usagetruefalse").toString().replace("{flag}", flag));
		} 		
	}

	private static void sendFlagHelp(Player p) {
		p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "-------------[RedProtect Flags]------------"));
    	p.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.flag.list") + " " + RedProtect.cfgs.getDefFlags()));
    	p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------------------------------"));
    	if (RedProtect.ph.hasPerm(p, "redprotect.flag.special")){                		
        	p.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.flag.admlist") + " " + RedProtect.cfgs.AdminFlags));    
        	p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------------------------------"));
    	} 
		
	}

	private static boolean validate(String flag, Object value) {
		if (flag.equalsIgnoreCase("gamemode")){
			if (!(value instanceof String)){
				return false;
			}
			return Sponge.getRegistry().getType(GameMode.class, value.toString().toUpperCase()).isPresent();			
		}
		
		if ((flag.equalsIgnoreCase("can-fly") || 
				flag.equalsIgnoreCase("player-damage") || 
				flag.equalsIgnoreCase("can-hunger") || 
				flag.equalsIgnoreCase("can-projectiles") || 
				flag.equalsIgnoreCase("can-pet") || 
				flag.equalsIgnoreCase("portal-enter") || 
				flag.equalsIgnoreCase("allow-create-portal") || 
				flag.equalsIgnoreCase("allow-mod") || 
				flag.equalsIgnoreCase("portal-exit") || 
				flag.equalsIgnoreCase("enderpearl") || 
				flag.equalsIgnoreCase("can-back") || 
				flag.equalsIgnoreCase("up-skills") || 
				flag.equalsIgnoreCase("enter") || 
				flag.equalsIgnoreCase("treefarm") || 
				flag.equalsIgnoreCase("sign") || 
				flag.equalsIgnoreCase("invincible") || 
				flag.equalsIgnoreCase("minefarm")) && !(value instanceof Boolean)){
			return false;
		}
		if (flag.equalsIgnoreCase("allow-enter-items") || flag.equalsIgnoreCase("deny-enter-items") || flag.equalsIgnoreCase("allow-place") || flag.equalsIgnoreCase("allow-break")){
			String[] valida = ((String)value).replace(" ", "").split(",");
			for (String item:valida){
				if (!Sponge.getGame().getRegistry().getType(ItemType.class, item).isPresent()){
					return false;
				}
			}
		}
		if (flag.equalsIgnoreCase("allow-cmds") || flag.equalsIgnoreCase("deny-cmds")){
			if (!(value instanceof String)){
				return false;
			}
			try{
				String[] cmds = ((String)value).replace(" ", "").split(",");
				return cmds.length > 0;
			} catch (Exception e){
				return false;
			}		
		}
		if (flag.equalsIgnoreCase("effects")){
			if (!(value instanceof String)){
				return false;
			}
			String[] effects = ((String)value).split(",");
			for (String eff:effects){
				String[] effect = eff.split(" ");
				if (effect.length < 2){
					return false;
				}
				if (!Sponge.getGame().getRegistry().getType(PotionEffectType.class, effect[0]).isPresent()){
					return false;
				}
				try {
					Integer.parseInt(effect[1]);
				} catch (NumberFormatException e){
					return false;
				}
			}						
		}
		return true;
	}

	private static void handleList(Player p, String uuid, int Page) {
		String pname = RPUtil.PlayerToUUID(p.getName());
        if (RedProtect.ph.hasPerm(p, "redprotect.admin.list")) {
        	getRegionforList(p, uuid, Page);
        	return;
        } else if (RedProtect.ph.hasPerm(p, "redprotect.own.list") && pname.equalsIgnoreCase(uuid)){
        	getRegionforList(p, uuid, Page);
        	return;
        }
        RPLang.sendMessage(p, "no.permission");
    }
    
	private static void getRegionforList(Player p, String uuid, int Page){
    	Set<Region> regions = RedProtect.rm.getRegions(uuid);
    	String pname = RPUtil.UUIDtoPlayer(uuid);
        int length = regions.size();
        if (pname == null || length == 0) {
            RPLang.sendMessage(p, "cmdmanager.player.noregions");
            return;
        }
        else {
        	p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "-------------------------------------------------"));
        	RPLang.sendMessage(p,RPLang.get("cmdmanager.region.created.list") + " " +pname);
        	p.sendMessage(RPUtil.toText("-----"));        	
        	if (RedProtect.cfgs.getBool("region-settings.region-list.simple-listing")){
        		for (World w:Sponge.getServer().getWorlds()){
        			String colorChar = RedProtect.cfgs.getString("region-settings.world-colors." + w.getName());
        			Set<Region> wregions = RedProtect.rm.getRegions(uuid, w);
        			if (wregions.size() > 0){
        				Iterator<Region> it = wregions.iterator();
        				Builder worldregions = Text.builder();
        				
        				if (RedProtect.ph.hasHelpPerm(p, "teleport")){
        					boolean first = true;
                			while (it.hasNext()){
                				Region r = it.next();
                				if (first){
                					first = false;
                					worldregions.append(Text.builder()
            								.append(RPUtil.toText("&8"+r.getName()))
                            				.onHover(TextActions.showText(RPUtil.toText(RPLang.get("cmdmanager.list.hover").replace("{region}", r.getName()))))
                            				.onClick(TextActions.runCommand("/rp tp "+r.getName()+" "+r.getWorld())).build());
                				} else {
                					worldregions.append(Text.builder()
            								.append(RPUtil.toText(RPLang.get("general.color")+", &8"+r.getName()))
                            				.onHover(TextActions.showText(RPUtil.toText(RPLang.get("cmdmanager.list.hover").replace("{region}", r.getName()))))
                            				.onClick(TextActions.runCommand("/rp tp "+r.getName()+" "+r.getWorld())).build());
                				}								
                			} 
        				} else {
        					boolean first = true;
                			while (it.hasNext()){
                				Region r = it.next();
                				if (first){
                					first = false;
                					worldregions.append(Text.builder()
            								.append(RPUtil.toText("&8"+r.getName())).build());
                				} else {
                					worldregions.append(Text.builder()
            								.append(RPUtil.toText(RPLang.get("general.color")+", &8"+r.getName())).build());
                				}								
                			}
        				}
        				p.sendMessage(RPUtil.toText(RPLang.get("general.color")+RPLang.get("region.world").replace(":", "")+" "+colorChar+w.getName()+"["+wregions.size()+"]&r: ")); 
            			p.sendMessages(worldregions.build());
            			p.sendMessage(RPUtil.toText("-----"));         				           			
        			}
        		}
        	} else {       		
                Iterator<Region> i = regions.iterator();
                if (Page == 0){Page = 1;}
                int max = (10*Page);
                int min = max-10;
                int count = 0;
                int last = 0;
                while (i.hasNext()) {
                	String info = i.next().info().toString();
                	if (count >= min && count <= max){
                		p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "-------------------------------------------------"));
                        p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "["+(count+1)+"] " + info));     
                        last = count;
                        
                	}
                	count++;
                }      
                if (max > count){min = 0;}
            	p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------- "+(min+1)+"-"+(last+1)+"/"+count+" --------------"));
            	if (count > max){
                	p.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.listpage.more").toString().replace("{player}", pname + " " + (Page+1))));
                } else {
                	if (Page != 1) {p.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.listpage.nomore")));}
                }
        	}                    	
        }
        return;
    }
    
	private static void handleWelcome(Player p, String wMessage) {
    	Region r = RedProtect.rm.getTopRegion(p.getLocation());
    	if (RedProtect.ph.hasRegionPerm(p, "welcome", r)) {    		
        	if (r != null){
        		if (wMessage.equals("")){
        			r.setWelcome("");
        			RPLang.sendMessage(p, "cmdmanager.region.welcomeoff");
        		} else if (wMessage.equals("hide ")){
        			r.setWelcome(wMessage);
        			RPLang.sendMessage(p, "cmdmanager.region.welcomehide");
        		} else {
        			r.setWelcome(wMessage);
                	RPLang.sendMessage(p,RPLang.get("cmdmanager.region.welcomeset") + " "+ wMessage);                	       		
        		}
        		RedProtect.logger.addLog("(World "+r.getWorld()+") Player "+p.getName()+" SET WELCOME of region "+r.getName()+" to "+wMessage);
        		return; 
        	} else {
        		RPLang.sendMessage(p, "cmdmanager.region.todo.that");
        		return;
        	}
        } 
        RPLang.sendMessage(p, "no.permission");
    }
	
	private static void handletp(Player p, String rname, String wname, Player play){
		World w = RedProtect.serv.getWorld(wname).get();
		if (w == null) {
            RPLang.sendMessage(p, "cmdmanager.region.invalidworld");
            return;
        }
    	Region region = RedProtect.rm.getRegion(rname, w);
    	if (region == null) {
    		RPLang.sendMessage(p, RPLang.get("cmdmanager.region.doesntexist") + ": " + rname);
            return;
        }          
    	
    	if (play == null) {
    		if (!RedProtect.ph.hasRegionPerm(p, "tp", region)){
    			RPLang.sendMessage(p, "no.permission");
                return;
    		}
    	} else {
    		if (!RedProtect.ph.hasPerm(p, "redprotect.tp.other")) {
        		RPLang.sendMessage(p, "no.permission");
                return;
            }    		
        }      

    	Location<World> loc = null;
    	if (region.getTPPoint() != null){
    		loc = new Location<World>(w, region.getTPPoint().getBlockX()+0.500,region.getTPPoint().getBlockY(), region.getTPPoint().getBlockZ()+0.500);
    	} else {
    		int limit = 250;
        	if (w.getDimension().equals(DimensionTypes.NETHER)){
        		limit = 124;
        	}
        	for (int i = limit; i > 0; i--){
        		BlockType mat = w.createSnapshot(region.getCenterX(), i, region.getCenterZ()).getState().getType();
        		BlockType mat1 = w.createSnapshot(region.getCenterX(), i+1, region.getCenterZ()).getState().getType();
        		BlockType mat2 = w.createSnapshot(region.getCenterX(), i+2, region.getCenterZ()).getState().getType();
        		if (!mat.equals(BlockTypes.LAVA) && !mat.equals(BlockTypes.AIR) && mat1.equals(BlockTypes.AIR) && mat2.equals(BlockTypes.AIR)){
        			loc = new Location<World>(w, region.getCenterX()+0.500, i+1, region.getCenterZ()+0.500);            			
        			break;
        		}
        	}
    	}
    	
    	if (loc != null){
    		if (play != null){
    			play.setLocation(loc);
    			RPLang.sendMessage(play, RPLang.get("cmdmanager.region.tp") + " " + rname);   			
    			RPLang.sendMessage(p, RPLang.get("cmdmanager.region.tpother") + " " + rname);
    		} else {
    			tpWait(p, loc, rname);
    		}      		
			return;
    	}
    	return;
	}
	
	private static void tpWait(final Player p, final Location<World> loc, final String rname){
		if (p.hasPermission("redprotect.admin.tp")){
			p.setLocation(loc);
			return;
		}
		if (!RedProtect.tpWait.contains(p.getName())){
    		RedProtect.tpWait.add(p.getName());
    		RPLang.sendMessage(p, "cmdmanager.region.tpdontmove");
    		Sponge.getScheduler().createSyncExecutor(RedProtect.plugin).schedule(new Runnable(){
    			@Override
    			public void run() {
    				if (RedProtect.tpWait.contains(p.getName())){
                		RedProtect.tpWait.remove(p.getName());
                		p.setLocation(loc);
                		RPLang.sendMessage(p,RPLang.get("cmdmanager.region.tp") + " " + rname);
    				}
    			}    		
        	}, 3, TimeUnit.SECONDS);
    	} else {
    		RPLang.sendMessage(p, "cmdmanager.region.tpneedwait");
    	}
	}
	
	private static void HandleHelPage(CommandSource sender, int page){
		sender.sendMessage(RPUtil.toText(RPLang.get("_redprotect.prefix")+" "+RPLang.get("cmdmanager.available.cmds")));
		sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"------------------------------------"));
		sender.sendMessage(RPUtil.toText("&b/rp <command>|<alias> | <> = Required | [] = Optional"));        
		if (sender instanceof Player){
			
			Player player = (Player)sender;		
			int i = 0;
			for (String key:RPLang.helpStrings()){
				if (RedProtect.ph.hasHelpPerm(player, key)) {
					i++;					
					
					if (i > (page*5)-5 && i <= page*5){
						player.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.help."+key)));
					} 
					if (i > page*5){
						sender.sendMessage(RPUtil.toText(RPLang.get("general.color")+"------------------------------------"));
						player.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.page").toString().replace("{page}", ""+(page+1))));
						break;
					}
				}
			}
		} else {
			sender.sendMessage(RPUtil.toText("&6rp setconfig list &3- List all editable configs"));
			sender.sendMessage(RPUtil.toText("&6rp setconfig <Config-Section> <Value> &3- Set a config option"));
			sender.sendMessage(RPUtil.toText("&6rp info <region> <world> &3- Info about a region"));
			sender.sendMessage(RPUtil.toText("&6rp flag <regionName> <Flag> <Value> <World> &3- Set a flag on region"));
			sender.sendMessage(RPUtil.toText("&6rp flag info <region> <world> &3- Flag info for region"));
			sender.sendMessage(RPUtil.toText("&6rp tp <playerName> <regionName> <World> &3- Teleport player to a region"));			
			sender.sendMessage(RPUtil.toText("&6rp limit <playerName> &3- Area limit for player"));
			sender.sendMessage(RPUtil.toText("&6rp claimlimit <playerName> [world] &3- Claim limit for player"));
			sender.sendMessage(RPUtil.toText("&6rp list-all &3- List All regions"));			
			sender.sendMessage(RPUtil.toText("&6rp ymlTomysql &3- Convert from Yml to Mysql"));
			sender.sendMessage(RPUtil.toText("&6rp mychunktorp &3- Convert from MyChunk to RedProtect"));
			sender.sendMessage(RPUtil.toText("&6rp gpTorp &3- Convert from GriefPrevention to RedProtect"));
			sender.sendMessage(RPUtil.toText("&6rp save-all &3- Save all regions to database"));
			sender.sendMessage(RPUtil.toText("&6rp load-all &3- Load all regions from database"));
			sender.sendMessage(RPUtil.toText("&6rp reload &3- Reload the plugin"));			
		}
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments)
			throws CommandException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean testPermission(CommandSource source) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<? extends Text> getShortDescription(CommandSource source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<? extends Text> getHelp(CommandSource source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Text getUsage(CommandSource source) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
