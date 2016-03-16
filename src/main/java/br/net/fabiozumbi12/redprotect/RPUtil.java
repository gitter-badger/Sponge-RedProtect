package br.net.fabiozumbi12.redprotect;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@SuppressWarnings("deprecation")
public class RPUtil {
    static int backup = 0; 
    public static HashMap<Player, HashMap<Location<World>, BlockState>> pBorders = new HashMap<Player, HashMap<Location<World>, BlockState>>();
        
    public static Text toText(String str){
    	return TextSerializers.FORMATTING_CODE.deserialize(str);
    }
        
    public static boolean isBukkitBlock(BlockState b){
    	RedProtect.logger.debug("default","BlockType: "+b.getType().getName());
    	return b.getType().getName().startsWith("minecraft:");
    }
    
    public static boolean isBukkitEntity(Entity e){
    	RedProtect.logger.debug("default","EntityType: "+e.getType().getName());
    	return Sponge.getGame().getRegistry().getType(EntityType.class, e.getType().getName()).isPresent();
    }
    
    static void saveToZipFile(File file, String ZippedFile, CommentedConfigurationNode conf){
    	try{
    		final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            ZipEntry e = new ZipEntry(ZippedFile);
            out.putNextEntry(e);

            byte[] data = conf.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
            out.close();
    	} catch (Exception e){
    		e.printStackTrace();
    	}    	
    }
    
    static void SaveToZipSB(File file, String ZippedFile, StringBuilder sb){
    	try{
    		final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            ZipEntry e = new ZipEntry(ZippedFile);
            out.putNextEntry(e);

            byte[] data = sb.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
            out.close();
    	} catch (Exception e){
    		e.printStackTrace();
    	}    	
    }
    
    
    static File genFileName(String Path, Boolean isBackup){
    	int count = 1;
		String date = DateNow().replace("/", "-");
    	File logfile = new File(Path+date+"-"+count+".zip");
    	File files[] = new File(Path).listFiles();
		HashMap<Long, File> keyFiles = new HashMap<Long, File>();
    	if (files.length >= RedProtect.cfgs.getInt("flat-file.max-backups") && isBackup){
    		for (File key:files){
    			keyFiles.put(key.lastModified(), key);
    		}
    		keyFiles.get(Collections.min(keyFiles.keySet())).delete();    		 
    	}
    	
    	while(logfile.exists()){     		
    		count++;
    		logfile = new File(Path+date+"-"+count+".zip");
    	}
    	
    	return logfile;
    }
    
    /**Generate a friendly and unique name for a region based on player name.
     * 
     * @param p Player
     * @param World World
     * @return Name of region
     */
    static String nameGen(String p, String World){
    	String rname = "";
    	World w = RedProtect.serv.getWorld(World).get();    	
            int i = 0;
            while (true) {
            	int is = String.valueOf(i).length();
                if (p.length() > 13) {
                	rname = p.substring(0, 14-is) + "_" + i;
                }
                else {
                	rname = p + "_" + i;
                }
                if (RedProtect.rm.getRegion(rname, w) == null) {
                    break;
                }
                ++i;
            }           
        return rname;
    }
    
    static String formatName(String name) {
        String s = name.substring(1).toLowerCase();
        String fs = name.substring(0, 1).toUpperCase();
        String ret = String.valueOf(fs) + s;
        ret = ret.replace("_", " ");
        return ret;
    }
    
    static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer e : list) {
            ret[i++] = e;
        }
        return ret;
    }
    
    public static String DateNow(){
    	DateFormat df = new SimpleDateFormat(RedProtect.cfgs.getString("region-settings.date-format"));
        Date today = Calendar.getInstance().getTime(); 
        String now = df.format(today);
		return now;    	
    }
    
    static String HourNow(){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int sec = Calendar.getInstance().get(Calendar.SECOND);        
		return "["+hour+":"+min+":"+sec+"]";    	
    }
    
    static void fixWorld(String regionname){
    	for (World w:RedProtect.serv.getWorlds()){
    		Region r = RedProtect.rm.getRegion(regionname, w);
    		if (r != null){
    			r.setWorld(w.getName());
    		}
    	}
    }
    
    static void ReadAllDB(Set<Region> regions){     	
    	RedProtect.logger.info("Loaded " + regions.size() + " regions (" + RedProtect.cfgs.getString("file-type") + ")");
    	int i = 0;
    	int pls = 0;
    	int origupdt = 0;
    	int purged = 0;
    	int sell = 0;
    	int dateint = 0;
    	Date now = null;    	 
    	SimpleDateFormat dateformat = new SimpleDateFormat(RedProtect.cfgs.getString("region-settings.date-format"));

		try {
			now = dateformat.parse(DateNow());
		} catch (ParseException e1) {
			RedProtect.logger.severe("The 'date-format' don't match with date 'now'!!");
		}
		
        for (Region r:regions){
        	
        	//purge regions
        	if (RedProtect.cfgs.getBool("purge.enabled")){
        		Date regiondate = null;
            	try {
    				regiondate = dateformat.parse(r.getDate());
    			} catch (ParseException e) {
    				RedProtect.logger.severe("The 'date-format' don't match with region date!!");
    				e.printStackTrace();
    			}
            	Long days = TimeUnit.DAYS.convert(now.getTime() - regiondate.getTime(), TimeUnit.MILLISECONDS);
            	
            	List<String> players = new ArrayList<String>();
            	for (String play:RedProtect.cfgs.getStringList("purge.ignore-regions-from-players")){
            		players.add(RPUtil.PlayerToUUID(play));
    			}           	
            	
            	if (days > RedProtect.cfgs.getInt("purge.remove-oldest") && !players.contains(UUIDtoPlayer(r.getCreator()))){        
                	RedProtect.logger.warning("Purging" + r.getName() + " - Days: " + days);
            		r.delete();
            		purged++;
            		continue;
            	}
        	}    
        	
        	/*
        	//sell rergions
        	if (RedProtect.cfgs.getBool("sell.enabled")){
        		Date regiondate = null;
            	try {
    				regiondate = dateformat.parse(r.getDate());
    			} catch (ParseException e) {
    				RedProtect.logger.severe("The 'date-format' don't match with region date!!");
    				e.printStackTrace();
    			}
            	Long days = TimeUnit.DAYS.convert(now.getTime() - regiondate.getTime(), TimeUnit.MILLISECONDS);
            	
            	List<String> players = new ArrayList<String>();
            	for (String play:RedProtect.cfgs.getStringList("sell.ignore-regions-from-players")){
            		players.add(RPUtil.PlayerToUUID(play));
    			}           	
            	
            	if (days > RedProtect.cfgs.getInt("sell.sell-oldest") && !players.contains(UUIDtoPlayer(r.getCreator()))){        
                	RedProtect.logger.warning("Selling " + r.getName() + " - Days: " + days);
            		RPEconomy.putToSell(r, "server", RPEconomy.getRegionValue(r));
            		sell++;
            	}
        	}
        	*/
        	
        	//Update player names
        	if (RedProtect.OnlineMode && !r.isForSale() && r.getCreator().equalsIgnoreCase(RedProtect.cfgs.getString(""))){
        		if (!isUUID(r.getCreator()) && r.getCreator() != null){
            		RedProtect.logger.warning("Creator from: " + r.getCreator());
            		RedProtect.logger.warning("To UUID: " + PlayerToUUID(r.getCreator()));
            		r.setCreator(PlayerToUUID(r.getCreator()));      
            		origupdt++;
            	}
            	
        		LinkedList<String> ownersl = r.getOwners();
        		LinkedList<String> membersl = r.getMembers();        	
            	for (int o = 0; o < ownersl.size(); o++){
            		String pname = ownersl.get(o);
            		if (!isUUID(pname) && pname != null){
                		RedProtect.logger.warning("Owner from: " + pname);
            			ownersl.remove(o);
                		ownersl.add(o, PlayerToUUID(pname));
                		RedProtect.logger.warning("To UUID: " + PlayerToUUID(pname));
                		origupdt++;
            		}             		
            	}        	
            	for (int m = 0; m < membersl.size(); m++){
            		String pname = membersl.get(m);     		
            		if (!isUUID(pname) && pname != null){
                		RedProtect.logger.warning("Member from: " + pname);   
            			membersl.remove(m);
                		membersl.add(m, PlayerToUUID(pname));
                		RedProtect.logger.warning("To UUID: " + PlayerToUUID(pname));  
                		origupdt++;
            		}              		
            	}
            	r.setOwners(ownersl);
            	r.setMembers(membersl);
            	if (origupdt > 0){
            		pls++;
            	}            	
        	}  
        	     	
        	if (pls > 0){
        		RedProtect.logger.sucess("["+pls+"]Region updated &6&l" + r.getName() + "&a&l. Owner &6&l" + r.getCreator());
            }        	
        }     
        
        if (dateint > 0){
			RedProtect.logger.info("Updated "+ dateint +" last visit users!");
			RedProtect.rm.saveAll();
    	}
                   	        
        if (i > 0 || pls > 0){
        	if (i > pls){
            	RedProtect.logger.sucess("Updated a total of &6&l" + (i-pls) + "&a&l regions!");
        	} else {
            	RedProtect.logger.sucess("Updated a total of &6&l" + (pls-i) + "&a&l regions!");
        	}
        	RedProtect.rm.saveAll();        	
        	RedProtect.logger.sucess("Regions saved!");  
        	pls = 0;
        	i = 0;
        }
        
        if (purged > 0){
        	RedProtect.logger.warning("Purged a total of &6&l" + purged + "&a&l regions!");
        	purged = 0;
        }
        
        if (sell > 0){
        	RedProtect.logger.warning("Put to sell a total of &6&l" + sell + "&a&l regions!");
        	sell = 0;
        }
        regions.clear();   
	}
      
    
    public static String PlayerToUUID(String PlayerName){
    	if (PlayerName == null || PlayerName.equals("")){
    		return null;
    	}
    	
    	//check if is already UUID
    	if (isUUID(PlayerName) || RedProtect.cfgs.getString("region-settings.default-owner").equalsIgnoreCase(PlayerName)){
    		return PlayerName;
    	}
    	
    	String uuid = PlayerName;

    	if (!RedProtect.OnlineMode){
    		uuid = uuid.toLowerCase();
    		return uuid;
    	}
    	
    	try{
    		Player offp = RedProtect.serv.getPlayer(PlayerName).get();    		
    		uuid = offp.getUniqueId().toString();
		} catch (IllegalArgumentException e){	
	    	Player onp = RedProtect.serv.getPlayer(PlayerName).get();
	    	if (onp != null){
	    		uuid = onp.getUniqueId().toString();
	    	}
		}
		return uuid;    	
    }
    
	public static String UUIDtoPlayer(String uuid){
    	if (uuid == null){
    		return null;
    	}
    	
    	//check if is UUID
    	if (!isUUID(uuid)){
    		return uuid;
    	}
    	
    	String PlayerName = null;
    	UUID uuids = null;
    	
    	if (!RedProtect.OnlineMode){
	    	PlayerName = uuid.toLowerCase();	    	
    		return PlayerName;
    	}
    	
    	try{
    		uuids = UUID.fromString(uuid);
    		Player offp = RedProtect.serv.getPlayer(uuids).get();
    		PlayerName = offp.getName();
		} catch (IllegalArgumentException e){	
			Player onp = RedProtect.serv.getPlayer(uuid).get();
	    	if (onp != null){
	    		PlayerName = onp.getName();
	    	}
		}
    	
		return PlayerName;    	
    }
    
	private static boolean isUUID(String uuid){
    	if (uuid == null){
    		return false;
    	}
    	try{
    		UUID.fromString(uuid);
    		return true;
    	} catch (IllegalArgumentException e){
    		return false;
    	}		
    }
    
    static void addRegion(List<Region> regions, World w){    	
    	for (int i = 0; i < regions.size(); i++){
    		if (!RedProtect.rm.getRegionsByWorld(w).contains(regions.get(i))){
    			RedProtect.logger.warning("["+(i+1)+"/"+regions.size()+"]Adding regions to database! This may take some time...");
        		RedProtect.rm.add(regions.get(i), w);       		                		
    		}
		}	 
    	regions.clear();
    }
    
    static Object parseObject(String value){
    	Object obj = value;
    	try {
    		obj = Integer.parseInt(value);
    	} catch(NumberFormatException e){
    		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
    			obj = Boolean.parseBoolean(value);
        	}
    	}
    	return obj;
    }
    
	static boolean fileToMysql() throws Exception{
		if (!RedProtect.cfgs.getString("file-type").equalsIgnoreCase("file")){
			return false;
		}
		
		initMysql();//Create tables
		int counter = 1;
		
		for (World world:Sponge.getServer().getWorlds()){
			
			String dbname = RedProtect.cfgs.getString("mysql.db-name") + "_" + world.getName();
		    String url = "jdbc:mysql://"+RedProtect.cfgs.getString("mysql.host")+"/";
		    
			Connection dbcon = DriverManager.getConnection(url + dbname, RedProtect.cfgs.getString("mysql.user-name"), RedProtect.cfgs.getString("mysql.user-pass"));
			
			for (Region r:RedProtect.rm.getRegionsByWorld(world)){
				if (!regionExists(r.getName(),dbname)) {
		            try {                
		                Statement st = null;
		                for (String flag:r.flags.keySet()){
		                	st = dbcon.createStatement();       
		                	st.executeUpdate("INSERT INTO region_flags (region,flag,value) VALUES ('" + r.getName() + "', '" + flag + "', '" + r.flags.get(flag).toString()+"')");
		                	st.close();
		                }          
		                st = dbcon.createStatement();
		                RedProtect.logger.debug("default","Region info - Region: "+ r.getName() +" | Creator:" + r.getCreator() + "(Size: "+r.getArea()+")");
		                st.executeUpdate("INSERT INTO region (name,creator,owners,members,maxMbrX,minMbrX,maxMbrZ,minMbrZ,maxy,miny,centerX,centerZ,date,wel,prior,value,world) VALUES "
		                		+ "('" +r.getName() + "', '" + 
		                		r.getCreator().toString() + "', '" + 
		                		r.getOwners().toString().replace("[", "").replace("]", "")  + "', '" + 
		                		r.getMembers().toString().replace("[", "").replace("]", "") + "', '" + 
		                		r.getMaxMbrX() + "', '" + 
		                		r.getMinMbrX() + "', '" + 
		                		r.getMaxMbrZ() + "', '" + 
		                		r.getMinMbrZ() + "', '" + 
		                		r.getMaxY() + "', '" + 
		                		r.getMinY() + "', '" + 
		                		r.getCenterX() + "', '" + 
		                		r.getCenterZ() + "', '" + 
		                		r.getDate() + "', '" +
		                		r.getWelcome() + "', '" + 
		                		r.getPrior() + "', '" + 
		                		r.getValue() + "', '" + 
		                		r.getWorld()+"')");                    
		                st.close();
		                RedProtect.logger.sucess("["+counter+"]Converted region to Mysql: " + r.getName());
		                counter++;
		            }
		            catch (SQLException e) {
		                e.printStackTrace();
		            }
		        } else {
		        	//if exists jump
		        	continue;
		        }
			}
			dbcon.close();
		}		
		if (counter > 0){
			RedProtect.logger.sucess((counter-1) + " regions converted to Mysql with sucess!");
		}
		return true;		
	}
	
	private static void initMysql() throws Exception{
		for (World world:Sponge.getServer().getWorlds()){
			
		    String dbname = RedProtect.cfgs.getString("mysql.db-name") + "_" + world.getName();
		    String url = "jdbc:mysql://"+RedProtect.cfgs.getString("mysql.host")+"/";
		    String reconnect = "?autoReconnect=true";
		    
	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	        }
	        catch (ClassNotFoundException e2) {
	            RedProtect.logger.severe("Couldn't find the driver for MySQL! com.mysql.jdbc.Driver.");
	            return;
	        }
	        Statement st = null;
	        
	        try {
	        	if (!checkDBExists(dbname)) {
	                Connection con = DriverManager.getConnection(url, RedProtect.cfgs.getString("mysql.user-name"), RedProtect.cfgs.getString("mysql.user-pass"));
	                st = con.createStatement();
	                st.executeUpdate("CREATE DATABASE " + dbname);
	                RedProtect.logger.info("Created database '" + dbname + "'!");
	                st.close();
	                st = null;
	                con = DriverManager.getConnection(url + dbname + reconnect, RedProtect.cfgs.getString("mysql.user-name"), RedProtect.cfgs.getString("mysql.user-pass"));
	                st = con.createStatement();
	                st.executeUpdate("CREATE TABLE region(name varchar(20) PRIMARY KEY NOT NULL, creator varchar(36), owners varchar(255), members varchar(255), maxMbrX int, minMbrX int, maxMbrZ int, minMbrZ int, centerX int, centerZ int, minY int, maxY int, date varchar(10), wel varchar(64), prior int, world varchar(16), value Long not null, tppoint varchar(16))");
	                st.close();
	                st = null;
	                RedProtect.logger.info("Created table: 'Region'!");    
	                st = con.createStatement();
	                st.executeUpdate("CREATE TABLE region_flags(region varchar(20) NOT NULL, flag varchar(20) NOT NULL, value varchar(255) NOT NULL)");
	                st.close();
	                st = null;
	                RedProtect.logger.info("Created table: 'Region Flags'!"); 
	                con.close();
	            }
	        	ConnectDB(url,dbname,reconnect);
	        }
	        catch (SQLException e) {
	            e.printStackTrace();
	            RedProtect.logger.severe("There was an error while parsing SQL, redProtect will still with actual DB setting until you change the connection options or check if a Mysql service is running. Use /rp reload to try again");
	        }
	        finally {
	            if (st != null) {
	                st.close();
	            }
	        }
		}
	    
	}
		
	private static boolean ConnectDB(final String url,final String dbname,final String reconnect) {
    	try {
    		@SuppressWarnings("unused")
			Connection dbcon = DriverManager.getConnection(url + dbname+ reconnect, RedProtect.cfgs.getString("mysql.user-name"), RedProtect.cfgs.getString("mysql.user-pass"));
			RedProtect.logger.info("Conected to "+dbname+" via Mysql!");
			return true;
		} catch (SQLException e) {			
			e.printStackTrace();
			RedProtect.logger.severe("["+dbname+"] Theres was an error while connecting to Mysql database! RedProtect will try to connect again in 15 seconds. If still not connecting, check the DB configurations and reload.");
			return false;
		}		
	}
	
	private static boolean regionExists(String name, String dbname) {
        int total = 0;
        String reconnect = "?autoReconnect=true";
        try {
        	Connection dbcon = DriverManager.getConnection("jdbc:mysql://"+RedProtect.cfgs.getString("mysql.host")+"/"+dbname+reconnect,RedProtect.cfgs.getString("mysql.user-name"), RedProtect.cfgs.getString("mysql.user-pass"));
            Statement st = dbcon.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM region WHERE name='"+name+"'");
            if (rs.next()) {
                total = rs.getInt("COUNT(*)");
            }
            st.close();
            rs.close();
            dbcon.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return total > 0;
    }		
	
	private static boolean checkDBExists(String dbname) throws SQLException {
        try {
        	RedProtect.logger.debug("default","Checking if database exists... " + dbname);
        	Connection con = DriverManager.getConnection("jdbc:mysql://"+RedProtect.cfgs.getString("mysql.host")+"/",RedProtect.cfgs.getString("mysql.user-name"), RedProtect.cfgs.getString("mysql.user-pass"));
            DatabaseMetaData meta = con.getMetaData();
            ResultSet rs = meta.getCatalogs();
            while (rs.next()) {
                String listOfDatabases = rs.getString("TABLE_CAT");
                if (listOfDatabases.equalsIgnoreCase(dbname)) {
                	con.close();
                	rs.close();
                    return true;
                }
            }
            rs.close();
            con.close();
        } catch (SQLException e){
        	e.printStackTrace();
        }        
        return false;
    }
	
	public static void startFlagChanger(final String r, final String flag, final Player p){
		RedProtect.changeWait.add(r+flag);
		Sponge.getScheduler().createSyncExecutor(RedProtect.plugin).schedule(new Runnable() { 
			public void run() {
				if (RedProtect.changeWait.contains(r+flag)){
					/*if (p != null && p.isOnline()){
						RPLang.sendMessage(p, RPLang.get("gui.needwait.ready").replace("{flag}", flag));
					}*/
					RedProtect.changeWait.remove(r+flag);				
				} 
			}
			}, RedProtect.cfgs.getInt("flags-configuration.change-flag-delay.seconds"), TimeUnit.SECONDS);
	}
	
	public static int getUpdatedPrior(Region region) {
		int regionarea = region.getArea();  
		int prior = region.getPrior();
        Region topRegion = RedProtect.rm.getTopRegion(RedProtect.serv.getWorld(region.getWorld()).get(), region.getCenterX(), region.getCenterY(), region.getCenterZ());
        Region lowRegion = RedProtect.rm.getLowRegion(RedProtect.serv.getWorld(region.getWorld()).get(), region.getCenterX(), region.getCenterY(), region.getCenterZ());
        
        if (lowRegion != null){
        	if (regionarea > lowRegion.getArea()){
        		prior = lowRegion.getPrior() - 1;
        	} else if (regionarea < lowRegion.getArea() && regionarea < topRegion.getArea() ){
        		prior = topRegion.getPrior() + 1;
        	} else if (regionarea < topRegion.getArea()){
        		prior = topRegion.getPrior() + 1;
        	} 
        }
		return prior;
	}
	
	
	/** Show the border of region for defined seconds.
	 * @param p
	 * @param loc1
	 * @param loc2
	 */
	public static void addBorder(final Player p, Region r) {		
		if (pBorders.containsKey(p)){
			RPLang.sendMessage(p, "cmdmanager.showingborder");
			return;
		}
		
		final World w = p.getWorld();
		final HashMap<Location<World>, BlockState> borderBlocks = new HashMap<Location<World>, BlockState>();				
		
		for (Location<World> loc:r.get4Points(p.getLocation().getBlockY())){
			loc = new Location<World>(w, loc.getBlockX(), p.getLocation().getBlockY(), loc.getBlockZ());
			BlockState b = w.getBlock(loc.getBlockPosition());
        	if (b.getType().equals(BlockTypes.AIR) || b.getType().equals(BlockTypes.WATER)){
        		borderBlocks.put(loc, b);
        		w.setBlockType(loc.getBlockPosition(), RedProtect.cfgs.getMaterial("region-settings.border.material"));
        	} 
		}		
		if (borderBlocks.isEmpty()){
			RPLang.sendMessage(p, "cmdmanager.bordernospace");
		} else {
			RPLang.sendMessage(p, "cmdmanager.addingborder");
			pBorders.put(p, borderBlocks);
			Sponge.getScheduler().createSyncExecutor(RedProtect.plugin).schedule(new Runnable(){
				@Override
				public void run() {
					if (pBorders.containsKey(p)){
	            		for (Location<World> loc:pBorders.get(p).keySet()){
	            			loc.setBlock(pBorders.get(p).get(loc));            			
	            		}
	            		pBorders.remove(p);
	            		RPLang.sendMessage(p, "cmdmanager.removingborder");
					}
				}    		
	    	}, RedProtect.cfgs.getInt("region-settings.border.time-showing"), TimeUnit.SECONDS); 
		}
		
    }		
	
	public static String StripName(String pRName) {
        String regionName;
		if (pRName.length() > 13) {
            regionName = pRName.substring(0, 13);
        } else {
        	regionName = pRName;
        } 
		return regionName;
	}

	public static boolean RemoveGuiItem(ItemStack item) {    	
    	if (item.get(Keys.ITEM_LORE).isPresent()){
    		try{
    			String lore = item.get(Keys.ITEM_LORE).get().get(1).toString();
    			if (RedProtect.cfgs.getDefFlags().contains(lore.replace("�0", "")) || lore.equals(RedProtect.cfgs.getGuiString("separator"))){
    				return true;
    			}
    		} catch (IndexOutOfBoundsException ex){    			
    		}    		
    	}
    	return false;
	}
	
	public static GameMode getGameMode(String gm){
		return Sponge.getGame().getRegistry().getType(GameMode.class, gm).get();
	}
	
	public static ItemType getItemType(String it){
		return Sponge.getGame().getRegistry().getType(ItemType.class, it).get();
	}
	
	public static PotionEffectType getPotType(String pot){
		return Sponge.getGame().getRegistry().getType(PotionEffectType.class, pot).get();
	}
}
