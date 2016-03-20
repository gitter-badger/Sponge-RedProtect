package br.net.fabiozumbi12.redprotect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spongepowered.api.entity.living.player.Player;

public class RPPermissionHandler{
	
    public boolean hasPerm(Player p, String perm) {
        return p != null && (p.hasPermission(perm) || p.hasPermission("redprotect.admin"));
    }
    
    public boolean hasRegionPerm(Player p, String s, Region poly) {
        return regionPermHandler(p, s, poly);
    }
    
    public boolean hasHelpPerm(Player p, String s) {
        return HelpPermHandler(p, s);
    }

    public int getPlayerLimit(Player p) {
        return LimitHandler(p);
    }
    
    public int getPlayerClaimLimit(Player p) {
        return ClaimLimitHandler(p);
    }
    
    private int LimitHandler(Player p){
    	int limit = RedProtect.cfgs.getInt("region-settings.limit-amount");
    	List<Integer> limits = new ArrayList<Integer>();    	
    	if (limit > 0){
    		if (!p.hasPermission("redprotect.limit.blocks.unlimited")){
    			for (String perm:RedProtect.cfgs.getStringList("permissions-limits.permissions.blocks")){
    				RedProtect.logger.debug("default","Perm: "+perm);
    				if (p.hasPermission(perm)){
    					RedProtect.logger.debug("default","Has block perm: "+perm);
    					limits.add(Integer.parseInt(perm.replaceAll("[^-?0-9]+", ""))); 
    				}
    			}
    		} else {
    			return -1;
    		}
    	}
    	if (limits.size() > 0){
    		limit = Collections.max(limits);
    	} 
		return limit;
    }
    
    private int ClaimLimitHandler(Player p){
    	int limit = RedProtect.cfgs.getInt("region-settings.claim-amount");  
    	List<Integer> limits = new ArrayList<Integer>();
    	if (limit > 0){
    		if (!p.hasPermission("redprotect.limit.claim.unlimited")){
    			for (String perm:RedProtect.cfgs.getStringList("permissions-limits.permissions.claims")){
    				RedProtect.logger.severe("Perm: "+perm);
    				if (p.hasPermission(perm)){
    					RedProtect.logger.debug("default","Has claim perm: "+perm);
    					limits.add(Integer.parseInt(perm.replaceAll("[^-?0-9]+", ""))); 
    				}
    			}
    		} else {
    			return -1;
    		}  		
    	}
    	if (limits.size() > 0){
    		limit = Collections.max(limits);
    	}     	
		return limit;
    }
    
    private boolean regionPermHandler(Player p, String s, Region poly){
    	String adminperm = "redprotect.admin." + s;
        String userperm = "redprotect.own." + s;
        if (poly == null) {
            return this.hasPerm(p, adminperm) || this.hasPerm(p, userperm);
        }
        return this.hasPerm(p, adminperm) || (this.hasPerm(p, userperm) && (poly.isOwner(p)));
    }
    
    private boolean HelpPermHandler(Player p, String s) {
        String adminperm = "redprotect.admin." + s;
        String userperm = "redprotect.own." + s;
        String normalperm = "redprotect." + s;
        return this.hasPerm(p, adminperm) || this.hasPerm(p, userperm) || this.hasPerm(p, normalperm);
    }
}
