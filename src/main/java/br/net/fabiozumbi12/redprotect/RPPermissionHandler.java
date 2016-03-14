package br.net.fabiozumbi12.redprotect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.SubjectData;

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
    			Map<String, Boolean> perms = p.getSubjectData().getPermissions(SubjectData.GLOBAL_CONTEXT);
    			for (String perm:perms.keySet()){
        			if (perm.startsWith("redprotect.limit.blocks.") && perms.get(perm)){
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
    			Map<String, Boolean> perms = p.getSubjectData().getPermissions(SubjectData.GLOBAL_CONTEXT);
    			for (String perm:perms.keySet()){
        			if (perm.startsWith("redprotect.limit.claim.") && perms.get(perm)){
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
