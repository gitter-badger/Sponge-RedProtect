package br.net.fabiozumbi12.redprotect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

class DefineRegionBuilder extends RegionBuilder{
	
    public DefineRegionBuilder(Player p, Location<World> loc1, Location<World> loc2, String regionName, String creator, LinkedList<String> owners) {  	
        String pName = p.getUniqueId().toString();
        if (!RedProtect.OnlineMode){
        	pName = p.getName().toLowerCase();
    	}
        
        String pRName = RPUtil.UUIDtoPlayer(p.getName());
        String wmsg = "";
        if (creator.equals(RedProtect.cfgs.getString("region-settings.default-owner"))){
        	pName = creator;
        	pRName = creator;
        	wmsg = "hide ";
        }
        
        if (regionName.equals("")) {
            int i = 0;            
            regionName = RPUtil.StripName(pRName)+"_"+0;            
            while (RedProtect.rm.getRegion(regionName, p.getWorld()) != null) {
            	++i;
            	regionName = RPUtil.StripName(pRName)+"_"+i;   
            }            
            if (regionName.length() > 16) {
            	RPLang.sendMessage(p, "regionbuilder.autoname.error");
                return;
            }
        }
        if (loc1 == null || loc2 == null) {
        	RPLang.sendMessage(p, "regionbuilder.selection.notset");
            return;
        }
        if (RedProtect.rm.getRegion(regionName, p.getWorld()) != null) {
        	RPLang.sendMessage(p, "regionbuilder.regionname.existis");
            return;
        }
        if (regionName.length() < 3 || regionName.length() > 16) {
        	RPLang.sendMessage(p, "regionbuilder.regionname.invalid");
            return;
        }

        owners.add(creator);
        if (!pName.equals(creator)) {
            owners.add(pName);
        }
        
        int miny = loc1.getBlockY();
        int maxy = loc2.getBlockY();
        if (RedProtect.cfgs.getBool("region-settings.autoexpandvert-ondefine")){
        	miny = 0;
        	maxy = 256;
        }
        
        Region region = new Region(regionName, owners, new LinkedList<String>(), creator, new int[] { loc1.getBlockX(), loc1.getBlockX(), loc2.getBlockX(), loc2.getBlockX() }, new int[] { loc1.getBlockZ(), loc1.getBlockZ(), loc2.getBlockZ(), loc2.getBlockZ() }, miny, maxy, 0, p.getWorld().getName(), RPUtil.DateNow(), RedProtect.cfgs.getDefFlagsValues(), wmsg, 0, null);
        
        region.setPrior(RPUtil.getUpdatedPrior(region));            
            	
        List<String> othersName = new ArrayList<String>();
        Region otherrg = null;
        
        for (int locx = region.getMinMbrX();  locx < region.getMaxMbrX(); locx++){
        	for (int locz = region.getMinMbrZ();  locz < region.getMaxMbrZ(); locz++){
        		otherrg = RedProtect.rm.getTopRegion(new Location<World>(p.getWorld(), locx, p.getLocation().getBlockY(), locz));
        		if (otherrg != null){
                	if (!otherrg.isOwner(p) && !p.hasPermission("redprotect.admin")){
                		this.setError(p, RPLang.get("regionbuilder.region.overlapping").toString().replace("{player}", RPUtil.UUIDtoPlayer(otherrg.getCreator())));
                        return;
                	}
                	if (!othersName.contains(otherrg.getName())){
                		othersName.add(otherrg.getName());
                	}
                }
        		/*
        		for (int locy = region.getMinY();  locy < region.getMaxY(); locy++){
        			 
        		}        	
        		*/	 
        	}
        } 
        
        if (othersName.size() > 0){
        	p.sendMessage(Text.of(RPLang.get("general.color") + "------------------------------------"));
        	p.sendMessage(Text.of(RPLang.get("regionbuilder.overlapping")));
        	p.sendMessage(Text.of(RPLang.get("region.regions") + " " + othersName));
        }
        
        this.r = region;
        RedProtect.logger.addLog("(World "+region.getWorld()+") Player "+p.getName()+" DEFINED region "+region.getName());
        return;
    }
}
