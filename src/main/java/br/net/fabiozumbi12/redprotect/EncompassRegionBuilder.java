package br.net.fabiozumbi12.redprotect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class EncompassRegionBuilder extends RegionBuilder{

    public EncompassRegionBuilder(ChangeSignEvent e) { 	
        String owner1 = RPUtil.PlayerToUUID(e.getText().asList().get(2).toPlain());
        String owner2 = RPUtil.PlayerToUUID(e.getText().asList().get(3).toPlain());
        World w = e.getTargetTile().getLocation().getExtent();
        BlockSnapshot b = w.createSnapshot(e.getTargetTile().getLocation().getBlockPosition());
        Player p = e.getCause().first(Player.class).get();
        Sign sign = e.getTargetTile();        
        String pName = RPUtil.PlayerToUUID(p.getName());
        BlockSnapshot last = b;
        BlockSnapshot current = b;
        BlockSnapshot next = null;
        BlockSnapshot first = null;
        String regionName = e.getText().asList().get(1).toPlain();
        LinkedList<Integer> px = new LinkedList<Integer>();
        LinkedList<Integer> pz = new LinkedList<Integer>();
        BlockSnapshot bFirst1 = null;
        BlockSnapshot bFirst2 = null;
        List<BlockSnapshot> blocks = new LinkedList<BlockSnapshot>();
        int oldFacing = 0;
        int curFacing = 0;
        
        if (!RedProtect.cfgs.isAllowedWorld(p)){
        	this.setErrorSign(e, RPLang.get("regionbuilder.region.worldnotallowed"));
            return;
        }                
        
        if (regionName == null || regionName.equals("")) {
        	regionName = RPUtil.nameGen(p.getName(), p.getWorld().getName());
        	if (regionName.length() > 16) {
                this.setErrorSign(e, RPLang.get("regionbuilder.autoname.error"));
                return;
            }
        }
        
        if (RedProtect.rm.getRegion(regionName, w) != null) {
            this.setErrorSign(e, RPLang.get("regionbuilder.regionname.existis"));
            return;
        }
        if (regionName.length() < 2 || regionName.length() > 16) {
            this.setErrorSign(e, RPLang.get("regionbuilder.regionname.invalid"));
            return;
        }
        if (regionName.contains(" ")) {
            this.setErrorSign(e, RPLang.get("regionbuilder.regionname.spaces"));
            return;
        }
            	
        for (int i = 0; i < RedProtect.cfgs.getInt("region-settings.max-scan"); ++i) {        	
            int nearbyCount = 0;
            int x = current.getLocation().get().getBlockX();
            int y = current.getLocation().get().getBlockY();
            int z = current.getLocation().get().getBlockZ();
            
            BlockSnapshot[] block = new BlockSnapshot[4];    
            
            block[0] = w.createSnapshot(x + 1, y, z);
            block[1] = w.createSnapshot(x - 1, y, z);
            block[2] = w.createSnapshot(x, y, z + 1);
            block[3] = w.createSnapshot(x, y, z - 1);           
            
            for (int bi = 0; bi < block.length; ++bi) {
            	
            	boolean validBlock = false;            	
                
                validBlock = (block[bi].getState().getType().getName().contains(RedProtect.cfgs.getString("region-settings.block-id").toLowerCase())); 
                if (validBlock && !block[bi].getLocation().equals(last.getLocation())) {                
                	++nearbyCount;
                    next = block[bi];
                    curFacing = bi % 4;
                    if (i == 1) {
                        if (nearbyCount == 1) {
                            bFirst1 = block[bi];
                        }
                        if (nearbyCount == 2) {
                            bFirst2 = block[bi];
                        }
                    } 
                }
            }
            if (nearbyCount == 1) {
                if (i != 0) {
                    blocks.add(current);
                    if (current.equals(first)) {
                    	LinkedList<String> owners = new LinkedList<String>();
                        owners.add(pName);
                            if (owner1 == null) {
                            	sign.offer(e.getText().set(sign.getValue(Keys.SIGN_LINES).get().set(2, RPUtil.toText("--"))));
                            	
                            } else if (pName.equals(owner1)) {
                            	sign.offer(e.getText().set(sign.getValue(Keys.SIGN_LINES).get().set(2, RPUtil.toText("--"))));
                            	RPLang.sendMessage(p, "regionbuilder.sign.dontneed.name");
                            	
                            } else {
                                owners.add(owner1);
                            } 
                                    
                            
                            if (owner2 == null) {
                            	sign.offer(e.getText().set(sign.getValue(Keys.SIGN_LINES).get().set(3, RPUtil.toText("--"))));
                            } else if (pName.equals(owner2)) {
                            	sign.offer(e.getText().set(sign.getValue(Keys.SIGN_LINES).get().set(3, RPUtil.toText("--"))));
                            	RPLang.sendMessage(p, "regionbuilder.sign.dontneed.name");
                                
                            } else {
                            	owners.add(owner2);                                
                            }
                                                        
                        
                        int[] rx = new int[px.size()];
                        int[] rz = new int[pz.size()];
                        int bl = 0;
                        for (int bx : px) {
                            rx[bl] = bx;
                            ++bl;
                        }
                        bl = 0;
                        for (int bz : pz) {
                            rz[bl] = bz;
                            ++bl;
                        }
                        
                        
                        Region region = new Region(regionName, owners, new LinkedList<String>(), owners.get(0), rx, rz, 0, 256, 0, w.getName(), RPUtil.DateNow(), RedProtect.cfgs.getDefFlagsValues(), "", 0, null);
                        
                        List<String> othersName = new ArrayList<String>();
                        Region otherrg = null;
                                   
                        for (Region r:RedProtect.rm.getRegionsByWorld(w)){
                        	if (r.getMaxMbrX() <= region.getMaxMbrX() && r.getMaxY() <= region.getMaxY() && r.getMaxMbrZ() <= region.getMaxMbrZ() && r.getMinMbrX() >= region.getMinMbrX() && r.getMinY() >= region.getMinY() && r.getMinMbrZ() >= region.getMinMbrZ()){
                        		if (!r.isOwner(p) && !p.hasPermission("redprotect.admin")){
                            		this.setErrorSign(e, RPLang.get("regionbuilder.region.overlapping").replace("{location}", "x: " + r.getCenterX() + ", z: " + r.getCenterZ()).replace("{player}", RPUtil.UUIDtoPlayer(r.getCreator())));
                                    return;
                            	}
                        		if (!othersName.contains(r.getName())){
                            		othersName.add(r.getName());
                            	}
                        	}
                        }
                        
                        for (Location<World> loc:region.getLimitLocs(b.getLocation().get().getBlockY())){
                        	otherrg = RedProtect.rm.getTopRegion(loc);
                        	
                        	RedProtect.logger.debug("default","protection Block is: " + loc.getBlock().getType().getName());
                        	
                        	if (!loc.getBlock().getType().getName().contains(RedProtect.cfgs.getString("region-settings.block-id"))){
                        		this.setErrorSign(e, RPLang.get("regionbuilder.neeberetangle"));
                        		return;
                        	}
                        	
                    		if (otherrg != null){                    			
                            	if (!otherrg.isOwner(p) && !p.hasPermission("redprotect.admin")){
                            		this.setErrorSign(e, RPLang.get("regionbuilder.region.overlapping").replace("{location}", "x: " + region.getCenterX() + ", z: " + region.getCenterZ()).replace("{player}", RPUtil.UUIDtoPlayer(region.getCreator())));
                                    return;
                            	}
                            	if (!othersName.contains(otherrg.getName())){
                            		othersName.add(otherrg.getName());
                            	}
                            }
                        }                                
                        
                        region.setPrior(RPUtil.getUpdatedPrior(region));
                        
                        int claimLimit = RedProtect.ph.getPlayerClaimLimit(p);
                        int claimused = RedProtect.rm.getRegions(RPUtil.PlayerToUUID(p.getName()),w).size();
                        boolean claimUnlimited = RedProtect.ph.hasPerm(p, "redprotect.limit.claim.unlimited");
                        if (claimused >= claimLimit && claimLimit != -1) {
                            this.setErrorSign(e, RPLang.get("regionbuilder.claim.limit"));
                            return;
                        }
                        
                        int pLimit = RedProtect.ph.getPlayerLimit(p);
                        boolean areaUnlimited = RedProtect.ph.hasPerm(p, "redprotect.limit.blocks.unlimited");
                        int totalArea = RedProtect.rm.getTotalRegionSize(pName);
                        if (pLimit >= 0 && totalArea + region.getArea() > pLimit && !areaUnlimited) {
                            this.setErrorSign(e, RPLang.get("regionbuilder.reach.limit"));
                            return;
                        }
                        p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------------------------------"));
                        p.sendMessage(RPUtil.toText(RPLang.get("regionbuilder.claim.left") + (claimused+1) + RPLang.get("general.color") + "/" + (claimUnlimited ? RPLang.get("regionbuilder.area.unlimited") : claimLimit)));
                        p.sendMessage(RPUtil.toText(RPLang.get("regionbuilder.area.used") + " " + (totalArea + region.getArea()) + "\n" + 
                        RPLang.get("regionbuilder.area.left") + " " + (areaUnlimited ? RPLang.get("regionbuilder.area.unlimited") : (pLimit - (totalArea + region.getArea())))));
                        p.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.priority.set").replace("{region}", region.getName()) + " " + region.getPrior()));
                        
                        if (othersName.size() > 0){
                        	p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------------------------------"));
                        	p.sendMessage(RPUtil.toText(RPLang.get("regionbuilder.overlapping")));
                        	p.sendMessage(RPUtil.toText(RPLang.get("region.regions") + " " + othersName));
                        }
                        
                        if (RedProtect.cfgs.getDropType("region-settings.drop-type").equals(RedProtect.DROP_TYPE.drop)) {
                            w.digBlock(b.getLocation().get().getBlockX(), b.getLocation().get().getBlockY(), b.getLocation().get().getBlockZ(), Cause.of(NamedCause.simulated(p)));
                            for (BlockSnapshot rb : blocks) {
                                w.digBlock(rb.getLocation().get().getBlockX(), rb.getLocation().get().getBlockY(), rb.getLocation().get().getBlockZ(), Cause.of(NamedCause.simulated(p)));
                            }
                        }
                        else if (RedProtect.cfgs.getDropType("region-settings.drop-type").equals(RedProtect.DROP_TYPE.remove)) {
                            w.digBlock(b.getLocation().get().getBlockX(), b.getLocation().get().getBlockY(), b.getLocation().get().getBlockZ(), Cause.of(NamedCause.simulated(p)));
                            for (BlockSnapshot rb : blocks) {
                                w.setBlockType(rb.getLocation().get().getBlockX(), rb.getLocation().get().getBlockY(), rb.getLocation().get().getBlockZ(), BlockTypes.AIR, false);
                            }
                        }                        
                        
                        if (RedProtect.rm.getRegions(RPUtil.PlayerToUUID(p.getName()), p.getWorld()).size() == 0){
                        	p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------------------------------"));
                        	p.sendMessage(RPUtil.toText(RPLang.get("cmdmanager.region.firstwarning")));                        	
                        }                        
                        p.sendMessage(RPUtil.toText(RPLang.get("general.color") + "------------------------------------"));
                        
                        
                        this.r = region;
                        RedProtect.logger.addLog("(World "+region.getWorld()+") Player "+p.getName()+" CREATED region "+region.getName());
                        return;
                    }
                }
            }
            else if (i == 1 && nearbyCount == 2) {
            	//check other regions on blocks
            	Region rcurrent = RedProtect.rm.getTopRegion(current.getLocation().get());
            	if (rcurrent != null && !rcurrent.canBuild(p)){
            		this.setErrorSign(e, RPLang.get("regionbuilder.region.overlapping").replace("{location}", "x: " + rcurrent.getCenterX() + ", z: " + rcurrent.getCenterZ()).replace("{player}", RPUtil.UUIDtoPlayer(rcurrent.getCreator())));
            		return;
            	}
                blocks.add(current);
                first = current;
                int x2 = bFirst1.getLocation().get().getBlockX();
                int z2 = bFirst1.getLocation().get().getBlockZ();
                int x3 = bFirst2.getLocation().get().getBlockX();
                int z3 = bFirst2.getLocation().get().getBlockZ();
                int distx = Math.abs(x2 - x3);
                int distz = Math.abs(z2 - z3);
                if ((distx != 2 || distz != 0) && (distz != 2 || distx != 0)) {
                    px.add(current.getLocation().get().getBlockX());
                    pz.add(current.getLocation().get().getBlockZ());
                }
            }
            else if (i != 0) {
                this.setErrorSign(e, RPLang.get("regionbuilder.area.error").replace("{area}", "(x: " + current.getLocation().get().getBlockX() + ", y: " + current.getLocation().get().getBlockY() + ", z: " + current.getLocation().get().getBlockZ() + ")"));
                return;
            }
            if (oldFacing != curFacing && i > 1) {
                px.add(current.getLocation().get().getBlockX());
                pz.add(current.getLocation().get().getBlockZ());
            }
            last = current;
            if (next == null) {
                this.setErrorSign(e, RPLang.get("regionbuilder.area.next"));
                return;
            }
            current = next;
            oldFacing = curFacing;
        }
        String maxsize = String.valueOf(RedProtect.cfgs.getInt("region-settings.max-scan")/2);
        this.setErrorSign(e, RPLang.get("regionbuilder.area.toobig").replace("{maxsize}", maxsize + "x" + maxsize));
    }
}
