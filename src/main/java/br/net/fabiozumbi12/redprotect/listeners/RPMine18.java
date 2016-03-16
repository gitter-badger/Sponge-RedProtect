package br.net.fabiozumbi12.redprotect.listeners;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.entity.projectile.Egg;
import org.spongepowered.api.entity.projectile.FishHook;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.entity.projectile.explosive.fireball.SmallFireball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import br.net.fabiozumbi12.redprotect.RPContainer;
import br.net.fabiozumbi12.redprotect.RPLang;
import br.net.fabiozumbi12.redprotect.RedProtect;
import br.net.fabiozumbi12.redprotect.Region;

public class RPMine18 {
	
	public RPMine18(){
		RedProtect.logger.debug("default","Loaded RPMine18...");
	}
	
	static RPContainer cont = new RPContainer();    
    
    @Listener
    public void onAttemptInteractAS(InteractEntityEvent e, @First Player p) {
                
        Entity ent = e.getTargetEntity();
        Location<World> l = ent.getLocation();
        Region r = RedProtect.rm.getTopRegion(l);
        
        if (r == null){
        	//global flags
        	if (ent.getType().equals(EntityTypes.ARMOR_STAND)) {
                if (!RedProtect.cfgs.getGlobalFlag(l.getExtent().getName(),"build")) {
                	e.setCancelled(true);
                    return;
                }
            }
        	return;
        }
        
        if (p.getItemInHand().isPresent() && p.getItemInHand().get().getItem().getType().equals(ItemTypes.ARMOR_STAND)){
        	if (r != null && !r.canBuild(p)){
        		e.setCancelled(true);
        		RPLang.sendMessage(p, "blocklistener.region.cantbuild");
            	return;
        	}    	
    	}
        
        if (ent.getType().equals(EntityTypes.ARMOR_STAND)) {
            if (r != null && !r.canBuild(p)) {
                if (!RedProtect.ph.hasPerm(p, "redprotect.bypass")) {
                	RPLang.sendMessage(p, "playerlistener.region.cantedit");
                    e.setCancelled(true);
                    return;
                }                
            }
        }
    } 
    
	@Listener
    public void onEntityDamage(DamageEntityEvent e, @First Entity e2) {
    	    	
        Entity e1 = e.getTargetEntity();
        Location<World> loc = e1.getLocation();
        
        Player p = null;        
        if (e2 instanceof Player){
        	p = (Player)e2;
        } else if (e2 instanceof Arrow){
        	Arrow proj = (Arrow)e2;
        	if (proj.getShooter() instanceof Player){
        		p = (Player) proj.getShooter();
        	}        	
        } else if (e2 instanceof FishHook){
        	FishHook fish = (FishHook)e2;
        	if (fish.getShooter() instanceof Player){
        		p = (Player) fish.getShooter();
        	} 
        } else if (e2 instanceof Egg){
        	Egg Egg = (Egg)e2;
        	if (Egg.getShooter() instanceof Player){
        		p = (Player) Egg.getShooter();
        	} 
        } else if (e2 instanceof Snowball){
        	Snowball Snowball = (Snowball)e2;
        	if (Snowball.getShooter() instanceof Player){
        		p = (Player) Snowball.getShooter();
        	} 
        } else if (e2 instanceof Fireball){
        	Fireball Fireball = (Fireball)e2;
        	if (Fireball.getShooter() instanceof Player){
        		p = (Player) Fireball.getShooter();
        	} 
        } else if (e2 instanceof SmallFireball){
        	SmallFireball SmallFireball = (SmallFireball)e2;
        	if (SmallFireball.getShooter() instanceof Player){
        		p = (Player) SmallFireball.getShooter();
        	} 
        } else {
        	e.isCancelled();
        	return;
        }         

        if (p == null){
        	return;
        }
        
		Region r1 = RedProtect.rm.getTopRegion(loc);
		
		if (r1 == null){
			//global flags
			if (e1 instanceof ArmorStand){
            	if (e2 instanceof Player) {
                    if (!RedProtect.cfgs.getGlobalFlag(loc.getExtent().getName(),"build")){
                    	e.setCancelled(true);
                    	return;
                    }
                }                  
            }            
            return;
		} 
		
		if (e1 instanceof ArmorStand){
        	if (r1 != null && !r1.canBuild(p)){
            	e.setCancelled(true);
            	RPLang.sendMessage(p, "blocklistener.region.cantbreak");
            	return;
            }                                  
        }        
	} 
    
	@Listener
	public void onBlockExplode(ExplosionEvent.Detonate e){
		RedProtect.logger.debug("default","Is BlockListener - BlockExplodeEvent event");
		
		for (Transaction<BlockSnapshot> bex:e.getTransactions()){
			BlockSnapshot b = bex.getOriginal();
			Region r = RedProtect.rm.getTopRegion(b.getLocation().get());
			if (!cont.canWorldBreak(b)){
				bex.setValid(false);
				continue;
	    	}
			if (r != null && !r.canFire()){
				bex.setValid(false);	
				continue;
			}
		}
	}        
}
