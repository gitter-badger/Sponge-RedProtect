package br.net.fabiozumbi12.redprotect.listeners;

import java.util.Map;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.Ambient;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.golem.Golem;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.vehicle.Boat;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.entity.vehicle.minecart.TNTMinecart;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import br.net.fabiozumbi12.redprotect.RedProtect;
import br.net.fabiozumbi12.redprotect.Region;

public class RPGlobalListener{
	
	public RPGlobalListener(){
		RedProtect.logger.debug("default","Loaded RPGlobalListener...");
	}
	
	@Listener	
	public void onBlockPlace(ChangeBlockEvent.Place e, @First Player p) {
		RedProtect.logger.debug("default","RPGlobalListener - Is ChangeBlockEvent event! Cancelled? " + e.isCancelled());
		
		BlockState b = e.getTransactions().get(0).getOriginal().getState();
		ItemType item = ItemTypes.NONE;
		if (p.getItemInHand().isPresent()){
			item = p.getItemInHand().get().getItem();
		}
		Region r = RedProtect.rm.getTopRegion(e.getTransactions().get(0).getOriginal().getLocation().get());
		if (r != null){
			return;
		}
		
		if (item.getName().contains("minecart") || item.getName().contains("boat")){
			if (!RedProtect.cfgs.getGlobalFlag(p.getWorld().getName(), "use-minecart") && !p.hasPermission("redprotect.bypass")){
	            e.setCancelled(true);
	            RedProtect.logger.debug("default","RPGlobalListener - Can't place minecart/boat!");
	            return;
	        }
		} else {
			if (!RedProtect.cfgs.getGlobalFlag(p.getWorld().getName(),"build") && !p.hasPermission("redprotect.bypass")){
				if (RedProtect.cfgs.getGlobalFlagList(p.getWorld().getName(), "if-build-false","place-blocks").contains(b.getType().getName())){
					return;
				}
				e.setCancelled(true);
				RedProtect.logger.debug("default","RPGlobalListener - Can't Build!");
				return;
			}
		}		
	}
	
	@Listener	
	public void onBlockBreak(ChangeBlockEvent.Break e, @First Player p) {
		RedProtect.logger.debug("default","RPGlobalListener - Is BlockBreakEvent event! Cancelled? " + e.isCancelled());
		
		BlockState b = e.getTransactions().get(0).getOriginal().getState();
		World w = e.getTargetWorld();
		Region r = RedProtect.rm.getTopRegion(e.getTransactions().get(0).getOriginal().getLocation().get());
		
		if (r == null && !RedProtect.cfgs.getGlobalFlag(w.getName(),"build") && !p.hasPermission("redprotect.bypass")){
			if (RedProtect.cfgs.getGlobalFlagList(p.getWorld().getName(), "if-build-false","break-blocks").contains(b.getType().getName())){
				return;
			}
			e.setCancelled(true);
			return;
		}
	}
	
	@Listener	
	public void onPlayerInteract(InteractEvent e, @First Player p){
		RedProtect.logger.debug("default","RPGlobalListener - Is InteractEvent event! Cancelled? " + e.isCancelled());
		if (!e.getInteractionPoint().isPresent()){
			return;
		}
		BlockSnapshot b = p.getWorld().createSnapshot(e.getInteractionPoint().get().toInt());
		String bname = b.getState().getName().toLowerCase();
		Region r = RedProtect.rm.getTopRegion(b.getLocation().get());
		if (r != null){
			return;
		}
		
		if (bname.contains("rail") || bname.contains("water")){
            if (!RedProtect.cfgs.getGlobalFlag(p.getWorld().getName(),"use-minecart") && !p.hasPermission("redprotect.bypass")){
        		e.setCancelled(true);
    			return;		
        	}
        } else {
        	if (!RedProtect.cfgs.getGlobalFlag(p.getWorld().getName(),"interact") && !p.hasPermission("redprotect.bypass")){
    			e.setCancelled(true);
    			return;
    		}
        }	
	}
	
	@Listener	
    public void onPlayerInteract(InteractEntityEvent e, @First Player p) {
		
        Entity ent = e.getTargetEntity();
        Location<World> l = ent.getLocation();
        Region r = RedProtect.rm.getTopRegion(l);
        if (r != null){
			return;
		}
        
        if (ent.getType().getName().contains("minecart") || ent.getType().getName().contains("boat")){
        	if (!RedProtect.cfgs.getGlobalFlag(ent.getWorld().getName(),"use-minecart") && !p.hasPermission("redprotect.bypass")) {
                e.setCancelled(true);
                return;
            }
        } else {
        	if (!RedProtect.cfgs.getGlobalFlag(ent.getWorld().getName(),"interact") && !p.hasPermission("redprotect.bypass") && (!(ent instanceof Player))) {
                e.setCancelled(true);
                return;
            }
        }      
	}
			
	@Listener	
	public void onBucketUse(UseItemStackEvent e, @First Player p){    	
    	Location<World> l = p.getLocation();
		Region r = RedProtect.rm.getTopRegion(l);	
		if (r != null){
			return;
		}
		
    	if (!RedProtect.cfgs.getGlobalFlag(p.getWorld().getName(),"build") && !p.hasPermission("redprotect.bypass")) {
    		e.setCancelled(true);
			return;
    	}
    }
	
	@Listener	
    public void onEntityDamageEntity(DamageEntityEvent e) {
		
        Entity e1 = e.getTargetEntity();
        Entity e2 = null;
        
        if (e.getCause().first(IndirectEntityDamageSource.class).isPresent()){
    		e2 = e.getCause().first(IndirectEntityDamageSource.class).get().getSource();
    		RedProtect.logger.debug("player","RPLayerListener: Is DamageEntityEvent event. Damager "+e2.getType().getName()); 
    	} else {
    		return;
    	}
        
        Location<World> loc = e1.getLocation();
		Region r1 = RedProtect.rm.getTopRegion(loc);
		if (r1 != null){
			return;
		}
		
		if (e2 instanceof Creeper || e2 instanceof PrimedTNT || e2 instanceof TNTMinecart) {
			if (e1 instanceof Player) {
                if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"explosion-entity-damage")) {
                    e.setCancelled(true);
                    return;
                }
            }        
        	if (e1 instanceof Animal || e1 instanceof Villager || e1 instanceof Golem || e1 instanceof Ambient) {
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"explosion-entity-damage")){
                    e.setCancelled(true);
                    return;
                }
            }
        	if (e1 instanceof Monster) {
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"explosion-entity-damage")){
                    e.setCancelled(true);
                    return;
                }
            }
        	if (e1 instanceof Hanging) {
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"build")){
                    e.setCancelled(true);
                    return;
                }
            }
		}
        
        if (e2 instanceof Player) {
        	Player p = (Player)e2;
        	/*
        	if (e.getCause().containsType(Lightning.class) || e.getCause().containsType(Explosion.class)){           	
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"entity-block-damage")){
            		e.setCancelled(true);
            		return;
            	}
            }*/
        	
        	if (e1 instanceof Player) {
                if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"pvp") && !p.hasPermission("redprotect.bypass")) {
                    e.setCancelled(true);
                    return;
                }
            }        
        	if (e1 instanceof Animal || e1 instanceof Villager || e1 instanceof Golem || e1 instanceof Ambient) {
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"player-hurt-passives") && !p.hasPermission("redprotect.bypass")){
                    e.setCancelled(true);
                    return;
                }
            }
        	if (e1 instanceof Monster) {
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"player-hurt-monsters") && !p.hasPermission("redprotect.bypass")){
                    e.setCancelled(true);
                    return;
                }
            }
        	
        	if (e1 instanceof Boat || e1 instanceof Minecart) {
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"use-minecart") && !p.hasPermission("redprotect.bypass")){
        			e.setCancelled(true);
        			return;
        		}
            }
        	if (e1 instanceof Hanging) {
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"build") && !p.hasPermission("redprotect.bypass")){
                    e.setCancelled(true);
                    return;
                }
            }
        }
        
        if (e2 instanceof Projectile) {
        	Projectile proj = (Projectile)e2;
        	if (proj.getShooter() instanceof Player){
        		Player p = (Player)proj.getShooter();  
        		
            	if (e1 instanceof Player) {
                    if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"pvp") && !p.hasPermission("redprotect.bypass")) {
                        e.setCancelled(true);
                        return;
                    }
                }        
            	if (e1 instanceof Animal || e1 instanceof Villager || e1 instanceof Golem || e1 instanceof Ambient) {
                	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"player-hurt-passives") && !p.hasPermission("redprotect.bypass")){
                        e.setCancelled(true);
                        return;
                    }
                }
            	if (e1 instanceof Monster) {
                	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"player-hurt-monsters") && !p.hasPermission("redprotect.bypass")){
                        e.setCancelled(true);
                        return;
                    }
                }
            	if (e1 instanceof Hanging) {
                	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"build") && !p.hasPermission("redprotect.bypass")){
                        e.setCancelled(true);
                        return;
                    }
                }
        	}        	
        }        
	}
	
	@Listener	
    public void onEntityExplode(ExplosionEvent.Detonate e) {
    	    	
    	World w = e.getTargetWorld();
        for (Transaction<BlockSnapshot> b:e.getTransactions()) {
        	Location<World> l = b.getOriginal().getLocation().get();
        	Region r = RedProtect.rm.getTopRegion(l);
        	if (r == null && !RedProtect.cfgs.getGlobalFlag(w.getName(),"entity-block-damage")){
        		b.setValid(false);
        		continue;
        	} 
        }
    }
	
	@Listener	
    public void onBlockBurn(ChangeBlockEvent.Modify e){
    	
    	for (Transaction<BlockSnapshot> b:e.getTransactions()){
    		Region r = RedProtect.rm.getTopRegion(b.getOriginal().getLocation().get());
        	if (r != null){
        		return;
        	}
        	
        	if (e.getCause().containsType(Monster.class)) {
                if (!RedProtect.cfgs.getGlobalFlag(b.getOriginal().getLocation().get().getExtent().getName(),"entity-block-damage")){
                	b.setValid(false);
                }
        	}
        	
    		if (b.getFinal().getState().getType().equals(BlockTypes.FIRE) && !RedProtect.cfgs.getGlobalFlag(e.getTargetWorld().getName(),"fire-block-damage")){
    			b.setValid(false);
        		return;
    		}     		
    	}    	   	
    }
	
	@Listener	
    public void onFireSpread(NotifyNeighborBlockEvent  e, @First BlockSnapshot source){
		
		Map<Direction, BlockState> dirs = e.getNeighbors();
    	
    	for (Direction dir:dirs.keySet()){
    		BlockSnapshot b = source.getLocation().get().getRelative(dir).createSnapshot();
    		BlockState bstate = source.getState();
        	Region r = RedProtect.rm.getTopRegion(b.getLocation().get());   
    		if (r != null){
        		return;
        	}
    		
    		if ((bstate.getType().equals(BlockTypes.FIRE) || bstate.getType().getName().contains("LAVA")) && 
    				!RedProtect.cfgs.getGlobalFlag(b.getLocation().get().getExtent().getName(),"fire-spread")){
    			e.setCancelled(true);
    			return;
    		}
    	}
	}
	
	@Listener	
	@IsCancelled(Tristate.FALSE)
    public void onCreatureSpawn(SpawnEntityEvent event) {
    	
        for (Entity e: event.getEntities()){
        	if (e instanceof Monster && !RedProtect.cfgs.getGlobalFlag(e.getWorld().getName(),"spawn-monsters")) {
            	Location<World> l = e.getLocation();
                Region r = RedProtect.rm.getTopRegion(l);
                if (r == null) {
                	RedProtect.logger.debug("spawn","RPGlobalListener - Cancelled spawn of Monster " + e.getType().getName());
                    event.setCancelled(true);
                    return;
                }
            }
            if ((e instanceof Animal || e instanceof Villager || e instanceof Ambient || e instanceof Golem) && !RedProtect.cfgs.getGlobalFlag(e.getWorld().getName(),"spawn-passives")) {
            	Location<World> l = e.getLocation();
                Region r = RedProtect.rm.getTopRegion(l);
                if (r == null) {
                	RedProtect.logger.debug("spawn","RPGlobalListener - Cancelled spawn of Animal " + e.getType().getName());
                    event.setCancelled(true);                    
                    return;
                }
            }
            if (e.getType() != null){
                RedProtect.logger.debug("spawn","RPGlobalListener - Spawn mob " + e.getType().getName());
            }
        } 
        
    }
}
