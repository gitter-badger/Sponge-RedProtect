package br.net.fabiozumbi12.redprotect.listeners;

import java.util.List;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.Ambient;
import org.spongepowered.api.entity.living.Aquatic;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.animal.Horse;
import org.spongepowered.api.entity.living.golem.Golem;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.ThrownPotion;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import br.net.fabiozumbi12.redprotect.RPContainer;
import br.net.fabiozumbi12.redprotect.RPLang;
import br.net.fabiozumbi12.redprotect.RedProtect;
import br.net.fabiozumbi12.redprotect.Region;

public class RPEntityListener {
	
	public RPEntityListener(){
		RedProtect.logger.debug("default","Loaded RPEntityListener...");
	}
	
    static RPContainer cont = new RPContainer();     
        
    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onCreatureSpawn(SpawnEntityEvent event) {
    	
    	for (Entity e:event.getEntities()){ 
                        
            if (!(e instanceof Living)){
            	return;
            }
            
            if (e instanceof Monster) {
            	Location<World> l = e.getLocation();
                Region r = RedProtect.rm.getTopRegion(l);
                if (r != null && !r.canSpawnMonsters()){
                	RedProtect.logger.debug("spawn","Cancelled spawn of monster " +  e.getType().getName());
                    event.setCancelled(true);
                    return;
                }
            }
            if (e instanceof Animal || e instanceof Golem || e instanceof Ambient || e instanceof Aquatic) {
            	Location<World> l = e.getLocation();
                Region r = RedProtect.rm.getTopRegion(l);
                if (r != null && !r.canSpawnPassives()) {
                	RedProtect.logger.debug("spawn","Cancelled spawn of animal " + e.getType().getName());
                    event.setCancelled(true);
                    return;
                }
            }
            RedProtect.logger.debug("spawn","RPEntityListener - Spawn mob " + e.getType().getName());
    	}    	
    }
    
    @Listener
    public void onEntityDamage(DamageEntityEvent e) {
    	
        //victim
        Entity e1 = e.getTargetEntity();
        RedProtect.logger.debug("default","RPEntityListener - DamageEntityEvent entity target "+e1.getType().getName());
        Region r = RedProtect.rm.getTopRegion(e1.getLocation());
        if (e1 instanceof Player){        	
        	if (r != null && r.flagExists("invincible")){
        		if (r.getFlagBool("invincible")){
        			e.setCancelled(true);        			
        		}
        	}        	
        }
        
        if (e1 instanceof Animal || e1 instanceof Villager || e1 instanceof Golem || e1 instanceof Ambient) {
        	if (r != null && r.flagExists("invincible")){
        		if (r.getFlagBool("invincible")){
        			if (e1 instanceof Animal){
        				((Animal)e1).setTarget(null);
        			}
        			e.setCancelled(true);        			
        		}
        	}
        }

        //damager
        if (!e.getCause().first(Living.class).isPresent()) {
            return;
        }
        Entity e2 = e.getCause().first(Living.class).get();
        RedProtect.logger.debug("default","RPEntityListener - DamageEntityEvent damager "+e2.getType().getName()); 
        
        if (e2 instanceof Projectile) {
        	Projectile a = (Projectile)e2;                
            if (a.getShooter() instanceof Entity){
            	e2 = (Entity)a.getShooter(); 
            }
            a = null;
            if (e2 == null) {
                return;
            }
        }            
        
        Region r1 = RedProtect.rm.getTopRegion(e1.getLocation());
        Region r2 = RedProtect.rm.getTopRegion(e2.getLocation());
                    
        if (e.getCause().containsType(Lightning.class) || 
        		e.getCause().containsType(Explosive.class) || 
        		e.getCause().containsType(Fireball.class) || 
        		e.getCause().containsType(Explosion.class)){           	
        	if (r1 != null && !r1.canFire() && !(e2 instanceof Player)){
        		e.setCancelled(true);
        		return;
        	}
        } 
        
        if (e1 instanceof Player) {
            if (e2 instanceof Player) {                	
                Player p2 = (Player)e2; 
                if (r1 != null) {
                	if (p2.getItemInHand().get().getItem().getType().equals(ItemTypes.EGG) && !r1.canProtectiles(p2)){
                		e.setCancelled(true);
                		RPLang.sendMessage(p2, "playerlistener.region.cantuse");
                        return;
                	}
                    if (r2 != null) {
                    	if (p2.getItemInHand().get().getItem().getType().equals(ItemTypes.EGG) && !r2.canProtectiles(p2)){
                    		e.setCancelled(true);
                    		RPLang.sendMessage(p2, "playerlistener.region.cantuse");
                            return;
                    	}
                        if ((r1.flagExists("pvp") && !r1.canPVP(p2)) || (r1.flagExists("pvp") && !r2.canPVP(p2))) {
                            e.setCancelled(true);
                            RPLang.sendMessage(p2, "entitylistener.region.cantpvp");
                            return;
                        }
                    }
                    else if (r1.flagExists("pvp") && !r1.canPVP(p2)) {
                        e.setCancelled(true);
                        RPLang.sendMessage(p2, "entitylistener.region.cantpvp");
                        return;
                    }
                }
                else if (r2 != null && r2.flagExists("pvp") && !r2.canPVP(p2)) {
                    e.setCancelled(true);
                    RPLang.sendMessage(p2, "entitylistener.region.cantpvp");
                    return;
                }
            }                
        }
        else if (e1 instanceof Animal || e1 instanceof Villager || e1 instanceof Golem || e instanceof Ambient) {
        	if (r1 != null && e2 instanceof Player) {
                Player p2 = (Player)e2;
                if (!r1.canInteractPassives(p2)) {
                    e.setCancelled(true);
                    RPLang.sendMessage(p2, "entitylistener.region.cantpassive");
                    return;
                }
            }                
        } 
        else if ((e1 instanceof Hanging) && e2 instanceof Player){
        	Player p2 = (Player)e2;
        	if (r1 != null && !r1.canBuild(p2)){
        		e.setCancelled(true);
        		RPLang.sendMessage(p2, "playerlistener.region.cantuse");
                return;
        	}                
            if (r2 != null && !r2.canBuild(p2)){
            	e.setCancelled(true);
            	RPLang.sendMessage(p2, "playerlistener.region.cantuse");
                return;
            }                
        } 
        else if ((e1 instanceof Hanging) && e2 instanceof Monster){
        	if (r1 != null || r2 != null){
        		RedProtect.logger.debug("default","Cancelled ItemFrame drop Item");
        		e.setCancelled(true);
                return;
        	}
        }
        else if ((e1 instanceof Explosive)){
        	if ((r1 != null && !r1.canFire()) || (r2 != null && !r2.canFire())){
        		e.setCancelled(true);
                return;
        	}
        }
    }
        
    @Listener
    public void onPotionSplash(LaunchProjectileEvent event) {
    	
    	if (event.getTargetEntity() instanceof ThrownPotion){
    		ThrownPotion potion = (ThrownPotion) event.getTargetEntity();
    		ProjectileSource thrower = potion.getShooter();    		
    		
    		RedProtect.logger.debug("default","RPEntityListener - LaunchProjectileEvent entity "+event.getTargetEntity().getType().getName()); 
    		
    		List<PotionEffect> pottypes = potion.get(Keys.POTION_EFFECTS).get();
    		for (PotionEffect t:pottypes){
    			if (!t.getType().equals(PotionEffectTypes.BLINDNESS) && 
    					!t.equals(PotionEffectTypes.WEAKNESS) && 
    					!t.equals(PotionEffectTypes.NAUSEA) && 
    					!t.equals(PotionEffectTypes.HUNGER) && 
    					!t.equals(PotionEffectTypes.POISON) && 
    					!t.equals(PotionEffectTypes.MINING_FATIGUE) && 
    					!t.equals(PotionEffectTypes.HASTE) &&
    					!t.equals(PotionEffectTypes.SLOWNESS) &&
    					!t.equals(PotionEffectTypes.WITHER)) {
                    return;
                }
    		}
            
            
            Player shooter;
            if (thrower instanceof Player) {
                shooter = (Player)thrower;
            } else {
                return;
            }
            
            RedProtect.logger.debug("default","RPEntityListener - LaunchProjectileEvent shooter "+shooter.getName()); 
            
            Entity e2 = event.getTargetEntity();
            Region r = RedProtect.rm.getTopRegion(e2.getLocation());
            if (e2 instanceof Player){
            	if (r != null && r.flagExists("pvp") && !r.canPVP(shooter)) {
                    event.setCancelled(true);
                    return;
                }
            } else {
            	if (r != null && !r.canInteractPassives(shooter)) {
                    event.setCancelled(true);
                    return;
                }
            }
    	} 
    }
    
    @Listener
	public void onInteractEvent(InteractEntityEvent.Secondary e, @First Player p){
		Entity et = e.getTargetEntity();
		Location<World> l = et.getLocation();
		Region r = RedProtect.rm.getTopRegion(l);	
		
		RedProtect.logger.debug("default","RPEntityListener - InteractEntityEvent.Secondary entity "+et.getType().getName()); 
		
		if (r != null && !r.canInteractPassives(p) && (et instanceof Animal || et instanceof Villager || et instanceof Golem || et instanceof Ambient)) {
			if (et instanceof Horse && ((Horse)et).getHorseData().get(Keys.TAMED_OWNER).isPresent()){
				Horse tam = (Horse) et;
				Player owner = RedProtect.serv.getPlayer(tam.getHorseData().get(Keys.TAMED_OWNER).get().get()).get();
				if (owner != null && owner.getName().equals(p.getName())){
					return;
				}
			}
		    e.setCancelled(true);
			RPLang.sendMessage(p, "entitylistener.region.cantinteract");
		}
	}
      
    @Listener
    public void WitherBlockBreak(ChangeBlockEvent.Break event, @First Entity e) {    	    	
    	if (e instanceof Monster) {
    		for (Transaction<BlockSnapshot> bt:event.getTransactions()){
    			BlockSnapshot b = bt.getOriginal();
    			RedProtect.logger.debug("default","RPEntityListener - Is EntityChangeBlockEvent event! Block "+b.getState().getType().getName());
    			Region r = RedProtect.rm.getTopRegion(b.getLocation().get());
                if (!cont.canWorldBreak(b)){        		        		
            		event.setCancelled(true);
            		return;
            	} 
                if (r != null && !r.canMobLoot()){
             	   event.setCancelled(true);
                }
    		}
            
    	}
    }
    
    /*
    @Listener
    public void onEntityExplode(EntityExplodeEvent e) {
    	if (e.isCancelled()){
    		return;
    	}
    	List<Block> toRemove = new ArrayList<Block>();
        for (Block b:e.blockList()) {
        	Location l = b.getLocation();
        	Region r = RedProtect.rm.getTopRegion(l);
        	if (r != null && !r.canFire()){
        		toRemove.add(b);
        		continue;
        	}        	
        }
        if (!toRemove.isEmpty()){
        	e.blockList().removeAll(toRemove);
        }
    }
    */
}
