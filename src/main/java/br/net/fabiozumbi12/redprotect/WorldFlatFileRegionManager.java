package br.net.fabiozumbi12.redprotect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("deprecation")
class WorldFlatFileRegionManager implements WorldRegionManager{

    HashMap<String, Region> regions;
    World world;
    
    public WorldFlatFileRegionManager(World world) {
        super();
        this.regions = new HashMap<String, Region>();
        this.world = world;
    }
    
    @Override
    public void add(Region r) {
        this.regions.put(r.getName(), r);
    }
    
    @Override
    public void remove(Region r) {
        if (this.regions.containsKey(r.getName())){
        	this.regions.remove(r.getName());
        }
    }
        
    @Override
    public Set<Region> getRegions(String pname) {
    	Set<Region> regionsp = new HashSet<Region>();
		for (Region r:regions.values()){
			if (r.getCreator() != null && r.getCreator().equals(pname)){
				regionsp.add(r);
			}
		}
		return regionsp;
    }
    
    @Override
    public Set<Region> getMemberRegions(String uuid) {
    	Set<Region> regionsp = new HashSet<Region>();
		for (Region r:regions.values()){
			if (r.getMembers().contains(uuid) || r.getOwners().contains(uuid)){
				regionsp.add(r);
			}
		}
		return regionsp;
    }
        
    @Override
    public Region getRegion(String rname) {
    	return regions.get(rname);
    }
    
    @Override
    public void save() {
        try {
            RedProtect.logger.debug("default","RegionManager.Save(): File type is " + RedProtect.cfgs.getString("file-type"));
            String world = this.getWorld().getName();
                  
            if (RedProtect.cfgs.getString("file-type").equals("file")) {            	
            	
            	File tempRegionFile = new File(RedProtect.configDir+"data", "data_" + world + ".conf");    
            	if (!tempRegionFile.exists()) {
            		tempRegionFile.createNewFile();
            	}
            	
            	ConfigurationLoader<CommentedConfigurationNode> regionManager = HoconConfigurationLoader.builder().setPath(tempRegionFile.toPath()).build();
            	CommentedConfigurationNode region = regionManager.createEmptyNode();
        		
            	for (Region r:regions.values()){
        			if (r.getName() == null){
        				continue;
        			}
        			String rname = r.getName().replace(".", "-");
        			region.getNode(rname,"name").setValue(rname);
        			region.getNode(rname,"lastvisit").setValue(r.getDate());
        			region.getNode(rname,"owners").setValue(r.getOwners());
        			region.getNode(rname,"members").setValue(r.getMembers());
        			region.getNode(rname,"creator").setValue(r.getCreator());
        			region.getNode(rname,"priority").setValue(r.getPrior());
        			region.getNode(rname,"welcome").setValue(r.getWelcome());
        			region.getNode(rname,"world").setValue(r.getWorld());
        			region.getNode(rname,"maxX").setValue(r.getMaxMbrX());
        			region.getNode(rname,"maxZ").setValue(r.getMaxMbrZ());
        			region.getNode(rname,"minX").setValue(r.getMinMbrX());
        			region.getNode(rname,"minZ").setValue(r.getMinMbrZ());	
        			region.getNode(rname,"maxY").setValue(r.getMaxY());
        			region.getNode(rname,"minY").setValue(r.getMinY());        			
        			for (String flag:r.flags.keySet()){
        				region.getNode(rname,"flags",flag).setValue(r.flags.get(flag));	
        			}
        			region.getNode(rname,"value").setValue(r.getValue());
        			
        			Location<World> loc = r.getTPPoint();
        			if (loc != null){
        				int x = loc.getBlockX();
            	    	int y = loc.getBlockY();
            	    	int z = loc.getBlockZ();
            	    	//float yaw = loc.getYaw();
            	    	//float pitch = loc.getPitch();
            	    	region.getNode(rname,"tppoint").setValue(x+","+y+","+z/*+","+yaw+","+pitch*/);
        			} else {
        				region.getNode(rname,"tppoint").setValue("");
        			}        			
        		}	 

        		try {
        			this.backupRegions(region);
        			regionManager.save(region);
        		} catch (IOException e) {
        			RedProtect.logger.severe("Error during save database file for world " + world + ": ");
        			e.printStackTrace();
        		} 
            }     
        }
        catch (Exception e4) {
            e4.printStackTrace();
        }
    }
    
    private void backupRegions(CommentedConfigurationNode fileDB) {
        if (!RedProtect.cfgs.getBool("flat-file.backup")) {
            return;
        }
        
        File bfolder = new File(RedProtect.configDir+"backups"+File.separator);
        if (!bfolder.exists()){
        	bfolder.mkdir();
        }
        
        File folder = new File(RedProtect.configDir+"backups"+File.separator+this.world.getName()+File.separator);
        if (!folder.exists()){
        	folder.mkdir();
        	RedProtect.logger.info("Created folder: " + folder.getPath()); 
        }
        
        //Save backup
        if (RPUtil.genFileName(folder.getPath()+File.separator, true) != null){
        	RPUtil.saveToZipFile(RPUtil.genFileName(folder.getPath()+File.separator, true), "data_" + this.world.getName() + ".conf", fileDB); 
        }
		       
    }
    
    @Override
    public int getTotalRegionSize(String uuid) {
		Set<Region> regionslist = new HashSet<Region>();
		for (Region r:regions.values()){
			if (r.getCreator().equalsIgnoreCase(uuid)){
				regionslist.add(r);
			}
		}
		int total = 0;
		for (Region r2 : regionslist) {
        	total += r2.getArea();
        }
		return total;
    }
    
    @Override
    public void load() {   
    	try {
            String world = this.getWorld().getName();
            if (RedProtect.cfgs.getString("file-type").equals("file")) {        	
            	File oldf = new File(RedProtect.configDir+"data"+File.separator+world + ".conf");
            	File newf = new File(RedProtect.configDir+"data"+File.separator+"data_" + world + ".conf");
                if (oldf.exists()){
                	oldf.renameTo(newf);
                }            
                this.load(RedProtect.configDir+"data"+File.separator+"data_" + world + ".conf");        	
            }
			} catch (FileNotFoundException | ClassNotFoundException e) {
				e.printStackTrace();
			} 
    }
    
	private void load(String path) throws FileNotFoundException, ClassNotFoundException {
        String world = this.getWorld().getName();        

        if (RedProtect.cfgs.getString("file-type").equals("file")) {        	
        	RedProtect.logger.debug("default","Load world " + this.world.getName() + ". File type: conf");
        	
        	try {
        		File tempRegionFile = new File(path);    
            	if (!tempRegionFile.exists()) {            		
    				tempRegionFile.createNewFile();    				       		
            	}
            	
            	ConfigurationLoader<CommentedConfigurationNode> regionManager = HoconConfigurationLoader.builder().setPath(tempRegionFile.toPath()).build();
            	CommentedConfigurationNode region = regionManager.load();
            	        	
            	for (Object key:region.getChildrenMap().keySet()){
            		
            		String rname = key.toString();
            		if (!region.getNode(rname).hasMapChildren()){
            			continue;
            		}
            		int maxX = region.getNode(rname,"maxX").getInt();
            		int maxZ = region.getNode(rname,"maxZ").getInt();
            		int minX = region.getNode(rname,"minX").getInt();
            		int minZ = region.getNode(rname,"minZ").getInt();
        	    	int maxY = region.getNode(rname,"maxY").getInt(255);
        	    	int minY = region.getNode(rname,"minY").getInt(0);
        	    	LinkedList<String> owners = new LinkedList<String>();
        	    	owners.addAll(region.getNode(rname,"owners").getList(TypeToken.of(String.class)));
        	    	
        	    	LinkedList<String> members = new LinkedList<String>();
        	    	members.addAll(region.getNode(rname,"members").getList(TypeToken.of(String.class)));
        	    	
        	    	String creator = region.getNode(rname,"creator").getString();	    	  
        	    	String welcome = region.getNode(rname,"welcome").getString();
        	    	int prior = region.getNode(rname,"priority").getInt();
        	    	String date = region.getNode(rname,"lastvisit").getString();
        	    	long value = region.getNode(rname,"value").getLong();
        	    	if (owners.size() == 0){
        	    		owners.add(creator);
        	    	}			    	
        	    	
        	    	Location<World> tppoint = null;
                    if (!region.getNode(rname,"tppoint").getString().equalsIgnoreCase("")){
                    	String tpstring[] = region.getNode(rname,"tppoint").getString().split(",");
                        tppoint = new Location<World>(Sponge.getServer().getWorld(world).get(), Double.parseDouble(tpstring[0]), Double.parseDouble(tpstring[1]), Double.parseDouble(tpstring[2]));
                    }
                        	    	
      	    	    Region newr = new Region(rname, owners, members, creator, new int[] {minX,minX,maxX,maxX}, new int[] {minZ,minZ,maxZ,maxZ}, minY, maxY, prior, world, date, RedProtect.cfgs.getDefFlagsValues(), welcome, value, tppoint);
        	    	for (String flag:RedProtect.cfgs.getDefFlags()){
        	    		if (region.getNode(rname,"flags",flag) != null){
      	    			    newr.flags.put(flag,region.getNode(rname,"flags",flag).getBoolean()); 
      	    		    } else {
      	    			    newr.flags.put(flag,RedProtect.cfgs.getDefFlagsValues().get(flag)); 
      	    		    }    	    		
      	    	    } 
        	    	for (String flag:RedProtect.cfgs.AdminFlags){
        	    		if (region.getNode(rname,"flags",flag).getString() != null){
        	    			newr.flags.put(flag,region.getNode(rname,"flags",flag).getValue());
        	    		}
        	    	}
            	    regions.put(rname,newr);
            	}
        	} catch (IOException e) {
				e.printStackTrace();
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			} 
        }
    }
        
    @Override
    public Set<Region> getRegionsNear(Player player, int radius) {
    	int px = player.getLocation().getBlockX();
        int pz = player.getLocation().getBlockZ();
        Set<Region> ret = new HashSet<Region>();
        
		for (Region r:regions.values()){
			RedProtect.logger.debug("default","Radius: " + radius);
			RedProtect.logger.debug("default","X radius: " + Math.abs(r.getCenterX() - px) + " - Z radius: " + Math.abs(r.getCenterZ() - pz));
			if (Math.abs(r.getCenterX() - px) <= radius && Math.abs(r.getCenterZ() - pz) <= radius){
				ret.add(r);
			}
		}
        return ret;
    }
    
    /*
    @Override
    public boolean regionExists(Region region) {
    	if (regions.containsValue(region)){
			return true;
		}
		return false;
    }
    */
    
    public World getWorld() {
        return this.world;
    }       
    
	@Override
	public Set<Region> getRegions(int x, int y, int z) {
		Set<Region> regionl = new HashSet<Region>();
		for (Region r:regions.values()){
			if (x <= r.getMaxMbrX() && x >= r.getMinMbrX() && y <= r.getMaxY() && y >= r.getMinY() && z <= r.getMaxMbrZ() && z >= r.getMinMbrZ()){
				regionl.add(r);
			}
		}
		return regionl;
	}

	@Override
	public Region getTopRegion(int x, int y, int z) {
		Map<Integer,Region> regionlist = new HashMap<Integer,Region>();
		int max = 0;
		for (Region r:regions.values()){
			if (x <= r.getMaxMbrX() && x >= r.getMinMbrX() && y <= r.getMaxY() && y >= r.getMinY() && z <= r.getMaxMbrZ() && z >= r.getMinMbrZ()){
				if (regionlist.containsKey(r.getPrior())){
					Region reg1 = regionlist.get(r.getPrior());
					int Prior = r.getPrior();
					if (reg1.getArea() >= r.getArea()){
						r.setPrior(Prior+1);
					} else {
						reg1.setPrior(Prior+1);
					}					
				}
				regionlist.put(r.getPrior(), r);
			}
		}
		if (regionlist.size() > 0){
			max = Collections.max(regionlist.keySet());
        }
		return regionlist.get(max);
	}
	
	@Override
	public Region getLowRegion(int x, int y ,int z) {
		Map<Integer,Region> regionlist = new HashMap<Integer,Region>();
		int min = 0;
		for (Region r:regions.values()){
			if (x <= r.getMaxMbrX() && x >= r.getMinMbrX() && y <= r.getMaxY() && y >= r.getMinY() && z <= r.getMaxMbrZ() && z >= r.getMinMbrZ()){
				if (regionlist.containsKey(r.getPrior())){
					Region reg1 = regionlist.get(r.getPrior());
					int Prior = r.getPrior();
					if (reg1.getArea() >= r.getArea()){
						r.setPrior(Prior+1);
					} else {
						reg1.setPrior(Prior+1);
					}					
				}
				regionlist.put(r.getPrior(), r);
			}
		}
		if (regionlist.size() > 0){
			min = Collections.min(regionlist.keySet());
        }
		return regionlist.get(min);
	}
	
	@Override
	public Map<Integer,Region> getGroupRegion(int x, int y, int z) {
		Map<Integer,Region> regionlist = new HashMap<Integer,Region>();
		for (Region r:regions.values()){
			if (x <= r.getMaxMbrX() && x >= r.getMinMbrX() && y <= r.getMaxY() && y >= r.getMinY() && z <= r.getMaxMbrZ() && z >= r.getMinMbrZ()){
				if (regionlist.containsKey(r.getPrior())){
					Region reg1 = regionlist.get(r.getPrior());
					int Prior = r.getPrior();
					if (reg1.getArea() >= r.getArea()){
						r.setPrior(Prior+1);
					} else {
						reg1.setPrior(Prior+1);
					}					
				}
				regionlist.put(r.getPrior(), r);
			}
		}
		return regionlist;
	}

	@Override
	public Set<Region> getAllRegions() {
		Set<Region> allregions = new HashSet<Region>();
		allregions.addAll(regions.values());
		return allregions;
	}

	@Override
	public void clearRegions() {
		regions.clear();		
	}

	@Override
	public void updateLiveRegion(String rname, String columm, String value) {}

	@Override
	public void closeConn() {
	}

	@Override
	public int getTotalRegionNum() {
		return 0;
	}

	@Override
	public void updateLiveFlags(String rname, String flag, String value) {}

	@Override
	public void removeLiveFlags(String rname, String flag) {}
	
}
