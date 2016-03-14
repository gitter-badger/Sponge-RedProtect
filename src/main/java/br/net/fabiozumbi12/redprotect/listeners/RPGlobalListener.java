package br.net.fabiozumbi12.redprotect.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.Ambient;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.golem.Golem;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.entity.projectile.FishHook;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.entity.projectile.explosive.fireball.SmallFireball;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import br.net.fabiozumbi12.redprotect.RedProtect;
import br.net.fabiozumbi12.redprotect.Region;

@SuppressWarnings("deprecation")
public class RPGlobalListener{
	
	public RPGlobalListener(){
		RedProtect.logger.debug("Loaded RPGlobalListener...");
	}
	
	@Listener
	public void onBlockPlace(ChangeBlockEvent.Place e) {
		RedProtect.logger.debug("RPGlobalListener - Is ChangeBlockEvent event! Cancelled? " + e.isCancelled());
		if (e.isCancelled() || !e.getCause().first(Player.class).isPresent()) {
            return;
        }

		BlockState b = e.getTransactions().get(0).getOriginal().getState();
		Player p = e.getCause().first(Player.class).get();
		ItemType item = p.getItemInHand().get().getItem();
		Region r = RedProtect.rm.getTopRegion(e.getTransactions().get(0).getOriginal().getLocation().get());
		if (r != null){
			return;
		}
		
		if (item.getName().contains("minecart") || item.getName().contains("boat")){
			if (!RedProtect.cfgs.getGlobalFlag(p.getWorld().getName(), "use-minecart") && !p.hasPermission("redprotect.bypass")){
	            e.setCancelled(true);
	            RedProtect.logger.debug("RPGlobalListener - Can't place minecart/boat!");
	            return;
	        }
		} else {
			if (!RedProtect.cfgs.getGlobalFlag(p.getWorld().getName(),"build") && !p.hasPermission("redprotect.bypass")){
				if (RedProtect.cfgs.getGlobalFlagList(p.getWorld().getName(), "if-build-false","place-blocks").contains(b.getType().getName())){
					return;
				}
				e.setCancelled(true);
				RedProtect.logger.debug("RPGlobalListener - Can't Build!");
				return;
			}
		}		
	}
	
	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break e) {
		RedProtect.logger.debug("RPGlobalListener - Is BlockBreakEvent event! Cancelled? " + e.isCancelled());
		if (e.isCancelled() || !e.getCause().first(Player.class).isPresent()) {
            return;
        }

		BlockState b = e.getTransactions().get(0).getOriginal().getState();
		Player p = e.getCause().first(Player.class).get();
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
	public void onPlayerInteract(InteractEvent e){
		RedProtect.logger.debug("RPGlobalListener - Is InteractEvent event! Cancelled? " + e.isCancelled());
    	if (e.isCancelled() || !e.getCause().first(Player.class).isPresent()) {
            return;
        }
		Player p = e.getCause().first(Player.class).get();	
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
    public void onPlayerInteract(InteractEntityEvent e) {
    	if (e.isCancelled() || !e.getCause().first(Player.class).isPresent()) {
            return;
        }
    	
    	Player p = e.getCause().first(Player.class).get();
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
    public void onHangingDamaged(DamageEntityEvent e) {
    	if (e.isCancelled() || !(e.getTargetEntity() instanceof Hanging)) {
            return;
        }
    	
        Entity ent = e.getTargetEntity();
        Location<World> loc = ent.getLocation();
        Region r = RedProtect.rm.getTopRegion(loc);
        if (r != null){
			return;
		}
        
        if (e.getCause().first(Player.class).isPresent()) { 
        	Player p = e.getCause().first(Player.class).get();
            if (!RedProtect.cfgs.getGlobalFlag(ent.getWorld().getName(),"build") && !p.hasPermission("redprotect.bypass")) {
                e.setCancelled(true);
            }
        }
        
        if (e.getCause().containsType(Explosion.class) || e.getCause().containsType(Living.class)) {
    		if (!RedProtect.cfgs.getGlobalFlag(ent.getWorld().getName(),"entity-block-damage")){
    			e.setCancelled(true);
        		return;
    		}
        }
    }
	
	@Listener
	public void onBucketUse(UseItemStackEvent e){
    	if (e.isCancelled() || !e.getCause().first(Player.class).isPresent()) {
            return;
        }

    	Player p = e.getCause().first(Player.class).get();
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
		if (e.isCancelled() || !e.getCause().first(Entity.class).isPresent()) {
            return;
        }
		
        Entity e1 = e.getTargetEntity();
        Entity e2 = e.getCause().first(Entity.class).get();
        
        Location<World> loc = e1.getLocation();
		Region r1 = RedProtect.rm.getTopRegion(loc);
		if (r1 != null){
			return;
		}
		
		if (e2 instanceof Creeper || e2.getType().equals(EntityTypes.PRIMED_TNT) || e2.getType().equals(EntityTypes.TNT_MINECART)) {
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
		}
        
        if (e2 instanceof Player) {
        	Player p = (Player)e2;
        	
        	if (e.getCause().containsType(Lightning.class) || e.getCause().containsType(Explosion.class)){           	
            	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"entity-block-damage")){
            		e.setCancelled(true);
            		return;
            	}
            }
        	if ((e1.getType().getName().contains("minecart") || e1.getType().getName().contains("boat")) && !RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),"use-minecart") && !p.hasPermission("redprotect.bypass")){
                e.setCancelled(true);
            	return;
            }
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
        }
        
        if (e2 instanceof SmallFireball) {
        	SmallFireball proj = (SmallFireball)e2;
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
        	}        	
        }
        
        if (e2 instanceof Fireball) {
        	Fireball proj = (Fireball)e2;
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
        	}        	
        }
        
        if (e2 instanceof Snowball) {
        	Snowball proj = (Snowball)e2;
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
        	}        	
        }
        
        if (e2 instanceof Arrow) {
        	Arrow proj = (Arrow)e2;
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
        	}        	
        }
        
        if (e2 instanceof FishHook) {
        	FishHook fish = (FishHook)e2;
        	if (fish.getShooter() instanceof Player){
        		Player p = (Player)fish.getShooter();  
        		
            	if (e1 instanceof Player) {
                    if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),".pvp") && !p.hasPermission("redprotect.bypass")) {
                        e.setCancelled(true);
                        return;
                    }
                }        
            	if (e1 instanceof Animal || e1 instanceof Villager || e1 instanceof Golem || e1 instanceof Ambient) {
                	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),".player-hurt-passives") && !p.hasPermission("redprotect.bypass")){
                        e.setCancelled(true);
                        return;
                    }
                }
            	if (e1 instanceof Monster) {
                	if (!RedProtect.cfgs.getGlobalFlag(e1.getWorld().getName(),".player-hurt-monsters") && !p.hasPermission("redprotect.bypass")){
                        e.setCancelled(true);
                        return;
                    }
                }             
        	}
        }		
	}
	
	@Listener
    public void onEntityExplode(ExplosionEvent.Detonate e) {
    	if (e.isCancelled()){
    		return;
    	}
    	
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
    public void onBlockBurn(BlockBurnEvent e){
    	if (e.isCancelled()){
    		return;
    	}
    	Block b = e.getBlock();
    	Region r = RedProtect.rm.getTopRegion(b.getLocation());
    	if (r != null){
    		return;
    	}
    	
		if (!RedProtect.cfgs.getGlobalFlag(b.getWorld().getName(),"fire-block-damage")){
			e.setCancelled(true);
    		return;
		}    	
    }
	
	@Listener
    public void onFireSpread(BlockSpreadEvent  e){
		if (e.isCancelled()){
    		return;
    	}
		Block b = e.getSource();
		Region r = RedProtect.rm.getTopRegion(b.getLocation());
		if (r != null){
    		return;
    	}
		
		if ((b.getType().equals(Material.FIRE) || b.getType().getName().contains("LAVA")) && !RedProtect.cfgs.getGlobalFlag(b.getWorld().getName(),"fire-spread")){
			e.setCancelled(true);
			return;
		}
	}
	
	@Listener
    public void onCreatureSpawn(CreatureSpawnEvent event) {
    	if (event.isCancelled()) {
            return;
        }
        Entity e = (Entity)event.getEntity();
        if (e == null) {
            return;
        }
        if (e instanceof Monster && !RedProtect.cfgs.getGlobalFlag(e.getWorld().getName(),"spawn-monsters")) {
        	Location l = event.getLocation();
            Region r = RedProtect.rm.getTopRegion(l);
            if (r == null && 
            		(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)
                    		|| event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)
                    		|| event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CHUNK_GEN)
                    		|| event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.DEFAULT))) {
                event.setCancelled(true);
            }
        }
        if ((e instanceof Animals || e instanceof Villager) && !RedProtect.cfgs.getGlobalFlag(e.getWorld().getName(),"spawn-passives")) {
        	Location l = event.getLocation();
            Region r = RedProtect.rm.getTopRegion(l);
            if (r == null && 
            		(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)
                    		|| event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)
                    		|| event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CHUNK_GEN)
                    		|| event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.DEFAULT))) {
                event.setCancelled(true);
            }
        }
    }
	
	@Listener
	public void onVehicleBreak(VehicleDestroyEvent e){
		if (e.isCancelled()){
    		return;
    	}
		if (!(e.getAttacker() instanceof Player)){
			return;
		}
		
		Vehicle cart = e.getVehicle();
		Player p = (Player) e.getAttacker();
		Region r = RedProtect.rm.getTopRegion(cart.getLocation());
		if (r != null){
			return;
		}
		
		if (!RedProtect.cfgs.getGlobalFlag(p.getWorld().getName(),"use-minecart") && !p.hasPermission("redprotect.bypass")){
			e.setCancelled(true);
			return;
		}
	}
	
    @Listener
    public void onBlockStartBurn(BlockIgniteEvent e){
    	if (e.isCancelled()){
    		return;
    	}
    	
    	Block b = e.getBlock();
    	Block bignit = e.getIgnitingBlock(); 
    	if ( b == null || bignit == null){
    		return;
    	}
    	RedProtect.logger.debug("Is BlockIgniteEvent event from global-listener");
    	Region r = RedProtect.rm.getTopRegion(b.getLocation());
    	if (r != null){
    		return;
    	}
    	if ((bignit.getType().equals(Material.FIRE) || bignit.getType().getName().contains("LAVA")) && !RedProtect.cfgs.getGlobalFlag(b.getWorld().getName(),"fire-spread")){
			e.setCancelled(true);
    		return;
		}
    	return;
    }
    
    @Listener
    public void MonsterBlockBreak(EntityChangeBlockEvent event) {
    	if (event.isCancelled()) {
            return;
        }
    	
    	RedProtect.logger.debug("Is EntityChangeBlockEvent event");
    	Entity e = event.getEntity();    	
    	if (e instanceof Monster) {
            Region r = RedProtect.rm.getTopRegion(event.getBlock().getLocation());
            if (r != null){
         	   return;
            }
            if (!RedProtect.cfgs.getGlobalFlag(e.getWorld().getName(),"entity-block-damage")){
            	event.setCancelled(true);
            }
    	}
    }
}
