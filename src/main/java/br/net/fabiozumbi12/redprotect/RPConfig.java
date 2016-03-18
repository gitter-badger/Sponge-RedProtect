package br.net.fabiozumbi12.redprotect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.Server;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.World;

import br.net.fabiozumbi12.redprotect.RedProtect.DROP_TYPE;

import com.google.common.reflect.TypeToken;

public class RPConfig{
	
	private HashMap<String, DROP_TYPE> DropType = new HashMap<String, DROP_TYPE>();
	public List<String> AdminFlags = Arrays.asList("can-fly", "gamemode", "player-damage", "can-hunger", "can-projectiles", "allow-place", "allow-break", "can-pet", "allow-cmds", "deny-cmds", "allow-create-portal", "portal-exit", "portal-enter", "allow-mod", "allow-enter-items", "deny-enter-items", "pvparena", "player-enter-command", "server-enter-command", "player-exit-command", "server-exit-command", "invincible", "effects", "treefarm", "minefarm", "pvp", "sign","enderpearl", "enter", "up-skills", "can-back", "for-sale");	
	
	
	private File defConfig = new File(RedProtect.configDir+"config.conf");
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	
	private File guiConfig = new File(RedProtect.configDir+"guiconfig.conf");
	private ConfigurationLoader<CommentedConfigurationNode> guiManager;
	
	private File gFlagsConfig = new File(RedProtect.configDir+"globalflags.conf");	
	private ConfigurationLoader<CommentedConfigurationNode> gFlagsManager;	
	
	private CommentedConfigurationNode config;
	private CommentedConfigurationNode tempConfig;
	private CommentedConfigurationNode gui;
	private CommentedConfigurationNode gflags;
	
	
	//getters	
	public CommentedConfigurationNode configs(){
		return config;
	}
	
	private CommentedConfigurationNode updateFromIn(CommentedConfigurationNode temp, CommentedConfigurationNode out){
		for (Object key:temp.getChildrenMap().keySet()){          	
        	if (temp.getNode(key).hasMapChildren()){        		
        		for (Object key2:temp.getNode(key).getChildrenMap().keySet()){          			
        			if (temp.getNode(key,key2).hasMapChildren()){		        				
		        		for (Object key3:temp.getNode(key,key2).getChildrenMap().keySet()){  
		        			out.getNode(key,key2,key3).setValue(temp.getNode(key,key2,key3).getValue());  
		        			continue;
		        		}				        		
		        	}	        			
        			out.getNode(key,key2).setValue(temp.getNode(key,key2).getValue());  
        			continue;
        		}
        	}
        	out.getNode(key).setValue(temp.getNode(key).getValue());    	            	   	            	
        }
		return out;
	}
	
	private CommentedConfigurationNode updateFromOut(CommentedConfigurationNode temp, CommentedConfigurationNode out){
		for (Object key:out.getChildrenMap().keySet()){          	
        	if (out.getNode(key).hasMapChildren()){        		
        		for (Object key2:out.getNode(key).getChildrenMap().keySet()){          			
        			if (out.getNode(key,key2).hasMapChildren()){		        				
		        		for (Object key3:out.getNode(key,key2).getChildrenMap().keySet()){  
		        			out.getNode(key,key2,key3).setValue(temp.getNode(key,key2,key3).getValue(out.getNode(key,key2,key3).getValue()));  
		        			continue;
		        		}				        		
		        	}	        			
        			out.getNode(key,key2).setValue(temp.getNode(key,key2).getValue(out.getNode(key,key2).getValue()));  
        			continue;
        		}
        	}
        	out.getNode(key).setValue(temp.getNode(key).getValue(out.getNode(key).getValue()));    	            	   	            	
        }
		return out;
	}
	
	//init
	RPConfig(Server server) {		
		try {			
			if (!new File(RedProtect.configDir).exists()){
				new File(RedProtect.configDir).mkdir();
			}
			if (!new File(RedProtect.configDir+"data").exists()){
            	new File(RedProtect.configDir+"data").mkdir();
            } 
			
			if (!defConfig.exists()) {
		         defConfig.createNewFile();
		         configManager = HoconConfigurationLoader.builder().setURL(this.getClass().getResource("config.conf")).build();
		         config = configManager.load();
		         configManager = HoconConfigurationLoader.builder().setFile(defConfig).build();
		         configManager.save(config);
		     }
			
		 	 if (!guiConfig.exists()) {
			 	 guiConfig.createNewFile();
				 guiManager = HoconConfigurationLoader.builder().setURL(this.getClass().getResource("guiconfig.conf")).build();
				 gui = guiManager.load();
				 guiManager = HoconConfigurationLoader.builder().setFile(guiConfig).build();
				 guiManager.save(gui);
		     }
		 	 
		 	if (!gFlagsConfig.exists()) {
		 		gFlagsConfig.createNewFile();
		     }		 	
		 	 
		} catch (IOException e1) {			
			RedProtect.logger.severe("The default configuration could not be loaded or created!");
			e1.printStackTrace();
		}
		
		
		        //load configs
		        try {
		        	//tempconfig
		        	configManager = HoconConfigurationLoader.builder().setURL(this.getClass().getResource("config.conf")).build();
		        	tempConfig = configManager.load();
		        	
		        	configManager = HoconConfigurationLoader.builder().setPath(defConfig.toPath()).build();
		        	config = configManager.load();
					
					guiManager = HoconConfigurationLoader.builder().setPath(guiConfig.toPath()).build();
					gui = guiManager.load();
					
					gFlagsManager = HoconConfigurationLoader.builder().setPath(gFlagsConfig.toPath()).build();
					gflags = gFlagsManager.load();
					
				} catch (IOException e1) {
					RedProtect.logger.severe("The default configuration could not be loaded or created!");
					e1.printStackTrace();
				}
    	            
				
    	            //------------------------------ Add default Values ----------------------------//
		        
		        config = updateFromIn(tempConfig, config); 
		        		        
		        try {
		        	configManager = HoconConfigurationLoader.builder().setPath(defConfig.toPath()).build();
					tempConfig = configManager.load();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

		        config = updateFromOut(tempConfig, config); 
		                        
		        /*
    	            if (!temp.contains("config-version")){
    	            	RedProtect.logger.severe("Old config file detected and copied to 'configBKP.yml'. Remember to check your old config file and set the new as you want!");
    	            	File bkpfile = new File(RedProtect.pathMain + File.separator + "configBKP.yml");
    	            	FileUtil.copy(config, bkpfile);
    	            	plugin.saveResource("config.yml", true);  
    	            	RedProtect.plugin.getConfig();
    	            } else {
    	            	try {
    						RedProtect.plugin.getConfig().load(config);
    					} catch (IOException | InvalidConfigurationException e) {
    						e.printStackTrace();
    					}
    	            }
    	            
    	            configs = inputLoader(plugin.getResource("config.yml"));  
                    for (String key:configs.getKeys(true)){                        	
    	            	configs.set(key, RedProtect.plugin.getConfig().get(key));    	            	   	            	
    	            }                        
                    for (String key:configs.getKeys(false)){    
                    	RedProtect.plugin.getConfig().set(key, configs.get(key));
                    	RedProtect.logger.debug("Set key: "+key);
                    }  
                    //--------------------------------------------------------------------------//
                    */
				
                    RedProtect.logger.info("Server version: " + RedProtect.game.getPlatform().getMinecraftVersion());
                    
                    /*Disabled dua dont have a getop method on API
                    //add op to ignore list fro purge
                    if (getNodes("purge.ignore-regions-from-players").getList(TypeToken.of(String.class)).size() <= 0){     
                    	List<String> ops = getNodes("purge.ignore-regions-from-players").getList(TypeToken.of(String.class));
                        for (OfflinePlayer play:RedProtect.serv.getOperators()){
                        	ops.add(play.getName());
                        }
                        RedProtect.plugin.getConfig().set("purge.ignore-regions-from-players", ops);
                    }
                    
                    //add op to ignore list foo sell
                    if (RedProtect.plugin.getConfig().getStringList("sell.ignore-regions-from-players").size() <= 0){      
                    	List<String> ops = RedProtect.plugin.getConfig().getStringList("sell.ignore-regions-from-players");
                        for (OfflinePlayer play:RedProtect.serv.getOperators()){
                        	ops.add(play.getName());
                        }
                        RedProtect.plugin.getConfig().set("sell.ignore-regions-from-players", ops);
                    }
                    */
                    
                                        
                    //drop type
    	            if (getNodes("region-settings.drop-type").getString() != null) {
    	                if (getNodes("region-settings.drop-type").getString().equalsIgnoreCase("keep")) {
    	                    DropType.put("region-settings.drop-type", DROP_TYPE.keep);
    	                }
    	                else if (getNodes("region-settings.drop-type").getString().equalsIgnoreCase("remove")) {
    	                	DropType.put("region-settings.drop-type", DROP_TYPE.remove);
    	                }
    	                else if (getNodes("region-settings.drop-type").getString().equalsIgnoreCase("drop")) {
    	                	DropType.put("region-settings.drop-type", DROP_TYPE.drop);
    	                }
    	                else {
    	                	DropType.put("region-settings.drop-type", DROP_TYPE.keep);
    	                    RedProtect.logger.warning("There is an error in your configuration: drop-type! Defaulting to 'Keep'.");
    	                }
    	            } 
    	                	            
    	            //add allowed claim worlds to config
    	            try {
						if (getNodes("allowed-claim-worlds").getList(TypeToken.of(String.class)).isEmpty()) {
							List<String> worlds = new ArrayList<String>();
							for (World w:RedProtect.serv.getWorlds()){
								worlds.add(w.getName());
								RedProtect.logger.warning("Added world to claim list " + w.getName());
							}
							worlds.remove("example_world");
							getNodes("allowed-claim-worlds").setValue(worlds);
						}
					} catch (ObjectMappingException e) {
						e.printStackTrace();
					}    
    	                	            
                    /*------------- ---- Add default config for not updateable configs ------------------*/
                    
                    //update new player flags according version
        			if (getNodes("config-version").getDouble() != 6.8D){
        				getNodes("config-version").setValue(6.8D);
        				
						try {
							List<String> flags = new LinkedList<String>(Arrays.asList());
							flags.addAll(getNodes("flags-configuration.enabled-flags").getList(TypeToken.of(String.class)));
							if (!flags.contains("smart-door")){
	        					flags.add("smart-door");
	        				}
	        				if (!flags.contains("allow-potions")){
	        					flags.add("allow-potions");            				
	        				}
	        				if (!flags.contains("mob-loot")){
	        					flags.add("mob-loot");            				
	        				}
	        				if (!flags.contains("flow-damage")){
	        					flags.add("flow-damage");            				
	        				}
	        				getNodes("flags-configuration.enabled-flags").setValue(flags);   
	        				RedProtect.logger.warning("Configuration UPDATE! We added new flags to &lflags-configuration > enabled-flags&r!");
						} catch (ObjectMappingException e) {
							e.printStackTrace();
						}        				
        			}
        			
        			/*---------------------------------------- Global Flags for worlds loaded --------------------------------------------*/
        			
        			for (World w:server.getWorlds()){
        				this.loadPerWorlds(w);
        			}
                    
                    /*------------------------------------------ Gui Items ------------------------------------------*/
                    
                    gui.getNode("gui-strings","value").setValue(gui.getNode("gui-strings","value").getString("&bValue: "));   
                    gui.getNode("gui-strings","true").setValue(gui.getNode("gui-strings","true").getString("&atrue")); 
                    gui.getNode("gui-strings","false").setValue(gui.getNode("gui-strings","false").getString("&cfalse")); 
                    gui.getNode("gui-strings","separator").setValue(gui.getNode("gui-strings","separator").getString("&7|")); 
                    
                    gui.getNode("gui-separator","material").setValue(gui.getNode("gui-separator","material").getString("this_glass")); 
                    gui.getNode("gui-separator","data").setValue(gui.getNode("gui-separator","data").getInt(0)); 
                    
                    for (String key:getDefFlagsValues().keySet()){
                    	gui.getNode("gui-flags",key,"slot").setValue(gui.getNode("gui-flags",key,"slot").setValue(gui.getNode("gui-flags",key,"slot").getInt(getDefFlagsValues().size())));
                    	gui.getNode("gui-flags",key,"material").setValue(gui.getNode("gui-flags",key,"material").setValue(gui.getNode("gui-flags",key,"material").getString("GOLDEN_APPLE")));
                    	gui.getNode("gui-flags",key,"name").setValue(gui.getNode("gui-flags",key,"name").setValue(gui.getNode("gui-flags",key,"name", "&e"+key)));                    	
                    	gui.getNode("gui-flags",key,"description").setValue(gui.getNode("gui-flags",key,"description").setValue(gui.getNode("gui-flags",key,"description").getString("&bDescription: &2Add a flag description here.")));
                    	gui.getNode("gui-flags",key,"description1").setValue(gui.getNode("gui-flags",key,"description1").setValue(gui.getNode("gui-flags",key,"description1").getString("")));
                    	gui.getNode("gui-flags",key,"description2").setValue(gui.getNode("gui-flags",key,"description2").setValue(gui.getNode("gui-flags",key,"description2").getString("")));
                    }
                    
                    /*
                    //load blockvalues file
                    try {
                    	EconomyConfig.load(bvalues);
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					}
                    
                    RPYaml tempEco = inputLoader(plugin.getResource("economy.yml"));
                    for (String key:tempEco.getKeys(false)){
                    	if (EconomyConfig.get(key) == null){
                    		EconomyConfig.set(key, tempEco.get(key));
                    	}
                    }
                    
                    for (Material mat:Material.values()){
                    	if (EconomyConfig.getString("items.values."+mat.name()) == null){
                    		EconomyConfig.set("items.values."+mat.name(), 0.0);                		
                    	}
                    }                    
                    for (Enchantment ench:Enchantment.values()){
                    	if (EconomyConfig.getString("enchantments.values."+ench.getName()) == null){
                    		EconomyConfig.set("enchantments.values."+ench.getName(), 0.0);                		
                    	}
                    }
                    */
                    
                    //////////////////////
                    
        			//create logs folder
        			File logs = new File(RedProtect.configDir+"logs");
        			if(getBool("log-actions") && !logs.exists()){
        				logs.mkdir();
    	                RedProtect.logger.info("Created folder: " + RedProtect.configDir+"logs");        	    		
        	    	}
        			
        			save();        			
    	            RedProtect.logger.info("All configurations loaded!");
    	            
	}
    
	public void loadPerWorlds(World w) {
		
		if (getNodes("region-settings.world-colors."+w.getName()).getString("").equals("")) {
			if (w.getDimension().getType().equals(DimensionTypes.OVERWORLD)){
				getNodes("region-settings.world-colors."+w.getName()).setValue("&a&l");			            		
			} else
			if (w.getDimension().getType().equals(DimensionTypes.NETHER)){
				getNodes("region-settings.world-colors."+w.getName()).setValue("&c&l");			            		
			} else
			if (w.getDimension().getType().equals(DimensionTypes.THE_END)){
				getNodes("region-settings.world-colors."+w.getName()).setValue("&5&l");			            		
			}
			RedProtect.logger.warning("Added world to color list " + w.getName());
		}
		
		try {
			//RedProtect.logger.debug("default","Writing global flags for world "+ w.getName() + "...");
        	gflags.getNode(w.getName(),"build").setValue(gflags.getNode(w.getName(),"build").getBoolean(true));
        	gflags.getNode(w.getName(),"if-build-false","break-blocks").setValue(gflags.getNode(w.getName(),"if-build-false","break-blocks").getList(TypeToken.of(String.class)));
        	gflags.getNode(w.getName(),"if-build-false","place-blocks").setValue(gflags.getNode(w.getName(),"if-build-false","place-blocks").getList(TypeToken.of(String.class)));
        	gflags.getNode(w.getName(),"pvp").setValue(gflags.getNode(w.getName(),"pvp", true));
        	gflags.getNode(w.getName(),"interact").setValue(gflags.getNode(w.getName(),"interact").getBoolean(true));
        	gflags.getNode(w.getName(),"use-minecart").setValue(gflags.getNode(w.getName(),"use-minecart").getBoolean(true));
        	gflags.getNode(w.getName(),"entity-block-damage").setValue(gflags.getNode(w.getName(),"entity-block-damage").getBoolean(false));
        	gflags.getNode(w.getName(),"explosion-entity-damage").setValue(gflags.getNode(w.getName(),"explosion-entity-damage").getBoolean(true));
        	gflags.getNode(w.getName(),"fire-block-damage").setValue(gflags.getNode(w.getName(),"fire-block-damage").getBoolean(false));
        	gflags.getNode(w.getName(),"fire-spread").setValue(gflags.getNode(w.getName(),"fire-spread").getBoolean(false));
        	gflags.getNode(w.getName(),"player-hurt-monsters").setValue(gflags.getNode(w.getName(),"player-hurt-monsters").getBoolean(true));
        	gflags.getNode(w.getName(),"player-hurt-passives").setValue(gflags.getNode(w.getName(),"player-hurt-passives").getBoolean(true));
        	gflags.getNode(w.getName(),"spawn-monsters").setValue(gflags.getNode(w.getName(),"spawn-monsters").getBoolean(true));
        	gflags.getNode(w.getName(),"spawn-passives").setValue(gflags.getNode(w.getName(),"spawn-passives").getBoolean(true));
        	gflags.getNode(w.getName(),"remove-entities-not-allowed-to-spawn").setValue(gflags.getNode(w.getName(),"remove-entities-not-allowed-to-spawn").getBoolean(false));
        	gflags.getNode(w.getName(),"allow-weather").setValue(gflags.getNode(w.getName(),"allow-weather").getBoolean(true));
        	//Disabled due API implementation
        	//w.setSpawnFlags(gFlags.getNode(w.getName()+".spawn-monsters").setValue(gFlags.getNode(w.getName()+".spawn-passives")).getBoolean(true));
        	//RedProtect.logger.debug("Spawn Animals: " + w.getAllowAnimals() + " | " + "Spawn Monsters: " + w.getAllowMonsters());                    
            //write gflags to gflags file
            gFlagsManager.save(gflags);
		} catch (IOException | ObjectMappingException e) {
			e.printStackTrace();
		} 
	}
	
    public Boolean getGlobalFlag(String world, String action){		
		return this.gflags.getNode(world,action).getBoolean();
	}
    
    public List<String> getGlobalFlagList(String world, String action){		
		try {
			return this.gflags.getNode(world, action).getList(TypeToken.of(String.class));
		} catch (ObjectMappingException e) {			
			e.printStackTrace();
			return null;
		}
	}
    
    public List<String> getGlobalFlagList(String world, String action1, String action2){		
		try {
			return this.gflags.getNode(world, action1, action2).getList(TypeToken.of(String.class));
		} catch (ObjectMappingException e) {			
			e.printStackTrace();
			return null;
		}
	}
    
    public ItemStack getGuiItemStack(String key){
    	RedProtect.logger.debug("default","Gui Material to get: " + key);
    	RedProtect.logger.debug("default","Result: " + gui.getNode("gui-flags",key,"material").getString());
    	return ItemStack.of(RPUtil.getItemType(gui.getNode("gui-flags",key,"material").getString()), 1);
    }
    
    public String getGuiFlagString(String flag, String option){
    	if (this.gui.getNode("gui-flags",flag,option).getString() == null){
    		return "";
    	}
    	return RPUtil.toText(gui.getNode("gui-flags",flag,option).getString()).toPlain();
    }
    
    public String getGuiString(String string) {
		return RPUtil.toText(gui.getNode("gui-strings",string).getString()).toPlain();
	}
    
    public int getGuiSlot(String flag) {
		return this.gui.getNode("gui-flags",flag,"slot").getInt();
	}
    
    public void setGuiSlot(/*String mat, */String flag, int slot) {
    	this.gui.getNode("gui-flags",flag,"slot").setValue(slot);
		//GuiItems.set("gui-flags."+flag+".material", mat);
		
	}
    /*
    public ItemStack getGuiSeparator() {
    	BlockState sep = RPUtil.getItemType(guiItems.getNode("gui-separator.material").getString()).getBlock().get().getDefaultState();
    	ItemStack separator = RedProtect.game.getRegistry().createBuilder(builderClass);//new ItemStack(Material.getMaterial(guiItems.getString("gui-separator.material")), 1, (short)guiItems.getInt("gui-separator.data"));
    	ItemMeta meta = separator.getItemMeta();
    	meta.setDisplayName(getGuiString("separator"));
    	meta.setLore(Arrays.asList("", getGuiString("separator")));
    	separator.setItemMeta(meta);
		return separator;
	}
    
    public static int getGuiMaxSlot() {
    	SortedSet<Integer> slots = new TreeSet<Integer>(new ArrayList<Integer>());
    	for (String key:guiItems.getKeys(true)){
    		if (key.contains(".slot")){
    			slots.add(guiItems.getInt(key));
    		}    		
    	}
		return Collections.max(slots);
	}
    */
    public Boolean getBool(String key){
		return getNodes(key).getBoolean(false);
	}
    
    public void setConfig(String key, Object value){
    	getNodes(key).setValue(value);
    }
    
    public HashMap<String, Object> getDefFlagsValues(){
    	HashMap<String,Object> flags = new HashMap<String,Object>();
    	for (Object oflag:getNodes("flags").getChildrenMap().keySet()/*getList(TypeToken.of(String.class)*/){
    		if (oflag instanceof String && isFlagEnabled(((String)oflag).replace("flags.", ""))){
    			String flag = (String)oflag;
    			try {
					if (flag.equals("pvp") && !getNodes("flags-configuration.enabled-flags").getList(TypeToken.of(String.class)).contains("pvp")){
						continue;
					}
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
    			
    			flags.put(flag, getNodes("flags."+flag).getValue());
    			
    			/*
    			if (RedProtect.plugin.getConfig().get(flag) == null){
    				flags.put(flag.replace("flags.", ""), " ");
    			} else {
    				flags.put(flag.replace("flags.", ""), RedProtect.plugin.getConfig().get(flag));
    			}*/		
    		}
    	}    	
		return flags;
	}
    
    public boolean isFlagEnabled(String flag){    	
    	try {
			return getNodes("flags-configuration.enabled-flags").getList(TypeToken.of(String.class)).contains(flag) || AdminFlags.contains(flag);
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    public SortedSet<String> getDefFlags(){
    	SortedSet<String> values = new TreeSet<String>(getDefFlagsValues().keySet());
		return values;    	
    }
    
    public String getString(String key){
		return getNodes(key).getString();
	}
    
    public Integer getInt(String key){	
		return getNodes(key).getInt();
	}
    
    public Object getObject(String key) {		
		return getNodes(key).getValue();
	}
    
    private CommentedConfigurationNode getNodes(String key){    	
    	String[] args = key.split("\\.");
    	if (args.length == 1){
    		return config.getNode(args[0]);
    	}
    	if (args.length == 2){
    		return config.getNode(args[0],args[1]);
    	}
    	if (args.length == 3){
    		return config.getNode(args[0],args[1],args[2]);
    	}
    	if (args.length == 4){
    		return config.getNode(args[0],args[1],args[2],args[3]);
    	}
    	if (args.length == 5){
    		return config.getNode(args[0],args[1],args[2],args[3],args[4]);
    	}
    	return null;
    }
    
    public List<String> getStringList(String key){
		try {
			return getNodes(key).getList(TypeToken.of(String.class));
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		return null;
	}
    
    public DROP_TYPE getDropType(String key){		
		return DropType.get(key);
	}
    
    public BlockType getMaterial(String key){
    	return BlockTypes.GLOWSTONE;//Material.getMaterial(RedProtect.plugin.getConfig().getString(key));
    }
    
    public void save(){
    	try {
			configManager.save(config);	
			gFlagsManager.save(gflags);
		} catch (IOException e) {
			RedProtect.logger.severe("Problems during save file:");
			e.printStackTrace();
		}
    }
    
    public void saveGui(){ 
    	try {
    		guiManager.save(gui);
		} catch (IOException e) {
			RedProtect.logger.severe("Problems during save gui file:");
			e.printStackTrace();
		}
    }    
    	
    public boolean isAllowedWorld(Player p) {
		try {
			return getNodes("allowed-claim-worlds").getList(TypeToken.of(String.class)).contains(p.getWorld().getName()) || p.hasPermission("redprotect.admin");
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		return false;
	}

	public SortedSet<String> getAllFlags() {
		SortedSet<String> values = new TreeSet<String>(getDefFlagsValues().keySet());
		values.addAll(new TreeSet<String>(AdminFlags));
		return values;
	}
	
	/*
	public int getBlockCost(String itemName) {
		return EconomyConfig.getInt("items.values."+itemName);
	}
	
	public int getEnchantCost(String enchantment) {
		return EconomyConfig.getInt("enchantments.values."+enchantment);
	}
	
	public String getEcoString(String key){
		return EconomyConfig.getString(key);
	}
	
	public Integer getEcoInt(String key){
		return EconomyConfig.getInt(key);
	}

	public boolean getEcoBool(String key) {
		return EconomyConfig.getBoolean(key);
	}
	*/
}
   
