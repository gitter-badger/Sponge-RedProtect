package br.net.fabiozumbi12.redprotect;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import br.net.fabiozumbi12.redprotect.listeners.RPBlockListener;
import br.net.fabiozumbi12.redprotect.listeners.RPEntityListener;
//import br.net.fabiozumbi12.RedProtect.listeners.RPGlobalListener;
//import br.net.fabiozumbi12.RedProtect.listeners.RPMine18;
//import br.net.fabiozumbi12.RedProtect.listeners.RPPlayerListener;
//import br.net.fabiozumbi12.RedProtect.listeners.RPWorldListener;

@Plugin(id = "br.net.fabiozumbi12.redprotect", name = "RedProtect", version = "6.4")
public class RedProtect {
	public static Game game;
	public static PluginContainer plugin;
	private UUID taskid;
	private CommandManager cmdService;
	public static boolean Update;
	public static String UptVersion;
	public static String UptLink;    
    public static RegionManager rm;
    public static List<String> changeWait = new ArrayList<String>();
    public static List<String> tpWait = new ArrayList<String>();
    public static RPPermissionHandler ph;
    public static RPLogger logger = new RPLogger();
    public static Server serv;    
    public static HashMap<Player, Location<World>> firstLocationSelections = new HashMap<Player, Location<World>>();
    public static HashMap<Player, Location<World>> secondLocationSelections = new HashMap<Player, Location<World>>();
	public static String configDir;
    public static boolean OnlineMode;
    public static RPConfig cfgs;
    
    static enum DROP_TYPE {
        drop, 
        remove, 
        keep;
    }
    
    @Listener
	public void onStopServer(GameStoppingServerEvent e) {
    	RedProtect.rm.saveAll();
        RedProtect.rm.unloadAll();
        logger.SaveLogs();
        for (Task task:Sponge.getScheduler().getScheduledTasks(this)){
        	task.cancel();
        }
        RedProtect.logger.severe(RedProtect.plugin.getName() + " disabled.");
    }
    
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        try {  
            initVars();   
            rm.loadAll();
            OnlineMode = serv.getOnlineMode();           
            
            cmdService.register(plugin, new RPCommands(), Arrays.asList("redprotect","rp","regionp","regp"));
            
            //game.getEventManager().registerListeners(plugin, new RPGlobalListener());
            game.getEventManager().registerListeners(plugin, new RPBlockListener());
            //game.getEventManager().registerListeners(plugin, new RPPlayerListener());
            game.getEventManager().registerListeners(plugin, new RPEntityListener());
            //game.getEventManager().registerListeners(plugin, new RPWorldListener());            
            //game.getEventManager().registerListeners(plugin, new RPMine18());
            
            
            if (!cfgs.getString("file-type").equalsIgnoreCase("mysql")){
            	RPUtil.ReadAllDB(rm.getAllRegions());
        	} else {
        		RedProtect.logger.info("Theres " + rm.getTotalRegionsNum() + " regions on (" + cfgs.getString("file-type") + ") database!");        		
        	}            
            
            RedProtect.logger.sucess(RedProtect.plugin.getName() + " enabled.");  
            
            if (cfgs.getString("file-type").equals("yml")){
            	AutoSaveHandler(); 
            }
        }
        catch (Exception e) {
    		e.printStackTrace();
    		RedProtect.logger.severe("Error enabling RedProtect, plugin will shut down.");
        }
    }
    
	private void AutoSaveHandler() {
		if (taskid != null){
			Sponge.getScheduler().getTaskById(taskid).get().cancel();
		}
		if (cfgs.getInt("flat-file.auto-save-interval-seconds") != 0){
			RedProtect.logger.info("Auto-save Scheduler: Saving "+cfgs.getString("file-type")+" database every " + cfgs.getInt("flat-file.auto-save-interval-seconds")/60 + " minutes!");  
			
			taskid = Sponge.getScheduler().createSyncExecutor(RedProtect.plugin).scheduleWithFixedDelay(new Runnable() { 
				public void run() {
					RedProtect.logger.debug("Auto-save Scheduler: Saving "+cfgs.getString("file-type")+" database!");
					rm.saveAll();
					} 
				},cfgs.getInt("flat-file.auto-save-interval-seconds"), cfgs.getInt("flat-file.auto-save-interval-seconds"), TimeUnit.SECONDS).getTask().getUniqueId();	
			
		} else {
        	RedProtect.logger.info("Auto-save Scheduler: Disabled");
        }
	}
	
    private void initVars() throws Exception {     	
    	game = Sponge.getGame();    	
    	plugin = Sponge.getPluginManager().getPlugin("br.net.fabiozumbi12.redprotect").get();
    	configDir = game.getConfigManager().getSharedConfig(RedProtect.plugin).getDirectory()+File.separator+"RedProtect"+File.separator;
        serv = Sponge.getServer();        
        cmdService = game.getCommandManager();
        cfgs = new RPConfig(serv);
        RPLang.init(this);
        
        ph = new RPPermissionHandler();
        rm = new RegionManager();
    }
    
}