package br.net.fabiozumbi12.redprotect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class RPContainer {

	public boolean canOpen(BlockSnapshot b, Player p) {
    	if (!RedProtect.cfgs.getBool("private.use")){
    		return true;
    	}
    	
    	List<Direction> dirs = Arrays.asList(Direction.EAST,Direction.NORTH,Direction.SOUTH,Direction.WEST);
    	String blocktype = b.getState().getType().getName();
    	Location<World> loc = b.getLocation().get();
    	World w = loc.getExtent();
    	List<String> blocks = RedProtect.cfgs.getStringList("private.allowed-blocks");
    	
        if (blocks.contains(blocktype)){        	
        	for (Direction dir:dirs){        		
        		Location<World> loc1 = getRelative(loc, dir);        		
        		if (loc1.getBlockType().getName().contains("sign")){        		
        			BlockSnapshot sign1 = w.createSnapshot(loc1.getBlockPosition());
            		if (!validateOpenBlock(sign1, p)){
            			return false;
            		}
            	} 
        		        		
        		if (blocks.contains(loc1.getBlockType().getName()) && loc1.getBlockType().equals(b.getState().getType())){        			
        			for (Direction dir2:dirs){
        				Location<World> loc3 = getRelative(loc1, dir2);              			
            			if (loc3.getBlockType().getName().contains("sign")){        		
            				BlockSnapshot sign2 = w.createSnapshot(loc3.getBlockPosition());
            				if (!validateOpenBlock(sign2, p)){
                    			return false;
                    		}
                    	}
        			}
        		}        		
        	}           
        }
		return true;        
    }
	
	public boolean canBreak(Player p, BlockSnapshot b){
    	if (!RedProtect.cfgs.getBool("private.use")){
    		return true;
    	}
    	
    	
    	Region reg = RedProtect.rm.getTopRegion(b.getLocation().get());
    	if (reg == null && !RedProtect.cfgs.getBool("private.allow-outside")){
    		return true;
    	}
    	
    	List<Direction> dirs = Arrays.asList(Direction.EAST,Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.UP,Direction.DOWN);
    	String blocktype = b.getState().getType().getName();
    	Location<World> loc = b.getLocation().get();
    	World w = loc.getExtent();
    	List<String> blocks = RedProtect.cfgs.getStringList("private.allowed-blocks");
    	
        if (blocks.contains(blocktype)){        	
        	for (Direction dir:dirs){        		
        		Location<World> loc1 = getRelative(loc, dir);        		
        		if (loc1.getBlockType().getName().contains("sign")){        		
        			BlockSnapshot sign1 = w.createSnapshot(loc1.getBlockPosition());
            		if (!validateBreakSign(sign1, p)){
            			return false;
            		}
            	} 
        		        		
        		if (blocks.contains(loc1.getBlockType().getName()) && loc1.getBlockType().equals(b.getState().getType())){        			
        			for (Direction dir2:dirs){
        				Location<World> loc3 = getRelative(loc1, dir2);              			
            			if (loc3.getBlockType().getName().contains("sign")){        		
            				BlockSnapshot sign2 = w.createSnapshot(loc3.getBlockPosition());
            				if (!validateBreakSign(sign2, p)){
                    			return false;
                    		}
                    	}
        			}
        		}        		
        	}           
        }
        
        /*
    	int x = b.getLocation().get().getBlockX();
        int y = b.getLocation().get().getBlockY();
        int z = b.getLocation().get().getBlockZ();
        World w = p.getWorld();

        if (!b.getLocation().get().getTileEntity().isPresent()){
			return true;
		} 
        TileEntity b1 = b.getLocation().get().getTileEntity().get();   
        
        if (b1 instanceof Sign && !validateBreakSign(b1, p)){
			return false;
    	}   		
           		
        String signbtype = b.getExtendedState().getType().getName(); 
        
        if (RedProtect.cfgs.getStringList("private.allowed-blocks").contains(signbtype)){
        	for (int sx = -1; sx <= 1; sx++){
        		for (int sy = -1; sy <= 1; sy++){
        			for (int sz = -1; sz <= 1; sz++){
        				
        				BlockSnapshot bsb = w.createSnapshot(x+sx, y, z+sz);   
        				
        				if (!bsb.getLocation().get().getTileEntity().isPresent()){
	            			return true;
	            		} 
                		TileEntity bs = bsb.getLocation().get().getTileEntity().get();
                		
        				if (bs instanceof Sign && !validateBreakSign(bs, p) && getBlockRelative(bs).getExtendedState().getType().getName().equals(b.getExtendedState().getType().getName())){
        					return false;
                    	}
        				
        				String blocktype2 = b.getExtendedState().getType().getName();       	            
        				
        				int x2 = bsb.getLocation().get().getBlockX();
        	            int y2 = bsb.getLocation().get().getBlockY();
        	            int z2 = bsb.getLocation().get().getBlockZ();
        	            
        				if (RedProtect.cfgs.getStringList("private.allowed-blocks").contains(blocktype2)){
        					for (int ux = -1; ux <= 1; ux++){
            	        		for (int uy = -1; uy <= 1; uy++){
            	        			for (int uz = -1; uz <= 1; uz++){

            	        				BlockSnapshot bub = w.createSnapshot(x2+sx, y2, z2+sz);   
            	        				
            	        				if (!bub.getLocation().get().getTileEntity().isPresent()){
            		            			return true;
            		            		} 
            	                		TileEntity bu = bub.getLocation().get().getTileEntity().get();
            	                		
            	        				if (bu instanceof Sign && !validateBreakSign(bu, p) && getBlockRelative(bu).getExtendedState().getType().getName().equals(b.getExtendedState().getType().getName())){
            	        					return false;
            	                    	}
            	        			}
            	        		}
            	        	}
        				}        				 
        			}
        		}
        	}                   
        } */
        return true;
    }
    
	public boolean canWorldBreak(BlockSnapshot b){		
    	/*if (!RedProtect.cfgs.getBool("private.use")){
    		return true;
    	}
    	Region reg = RedProtect.rm.getTopRegion(b.getLocation().get());
    	if (reg == null && !RedProtect.cfgs.getBool("private.allow-outside")){
    		return true;
    	}
    	int x = b.getLocation().get().getBlockX();
        int y = b.getLocation().get().getBlockY();
        int z = b.getLocation().get().getBlockZ();
        World w = b.getLocation().get().getExtent();

        if (!b.getLocation().get().getTileEntity().isPresent()){
        	return true;
        }
        TileEntity b1 = b.getLocation().get().getTileEntity().get(); 
        
        if (b1 instanceof Sign && validatePrivateSign(b1)){
        	RedProtect.logger.debug("default","Valid Sign on canWorldBreak!");
			return false;
    	}   		
           		
        String signbtype = b.getExtendedState().getType().getName();       
        
        if (RedProtect.cfgs.getStringList("private.allowed-blocks").contains(signbtype)){        	
        	for (int sx = -1; sx <= 1; sx++){
        		for (int sz = -1; sz <= 1; sz++){
        			
        			BlockSnapshot bsb = w.createSnapshot(x+sx, y, z+sz);
        			
        			if (!bsb.getLocation().get().getTileEntity().isPresent()){
            			return true;
            		} 
        			TileEntity bs = bsb.getLocation().get().getTileEntity().get();
        			
    				if (bs instanceof Sign && validatePrivateSign(bs)){
    					return false;
                	}
    				
    				String blocktype2 = b.getExtendedState().getType().getName();
    	            
    				
    				int x2 = bsb.getLocation().get().getBlockX();
    	            int y2 = bsb.getLocation().get().getBlockY();
    	            int z2 = bsb.getLocation().get().getBlockZ();
    	            
    				if (RedProtect.cfgs.getStringList("private.allowed-blocks").contains(blocktype2)){
    					for (int ux = -1; ux <= 1; ux++){
    						for (int uz = -1; uz <= 1; uz++){
    							
    							BlockSnapshot bub = w.createSnapshot(x2+ux, y2, z2+uz);
    							
    							if (!bub.getLocation().get().getTileEntity().isPresent()){
    		            			return true;
    		            		} 
    							TileEntity bu = bub.getLocation().get().getTileEntity().get();
    	        				if (bu instanceof Sign && validatePrivateSign(bu)){
    	        					return false;
    	                    	}
    	        			}        	        		
        	        	}
    				}        				 
    			}	        		
        	}                   
        } */
        return true;
    }
	
	public static BlockSnapshot getBlockRelative(TileEntity block) {
        if (block.getType().equals(TileEntityTypes.SIGN)){
        	return block.getLocation().getRelative(block.getLocation().get(DirectionalData.class).get().direction().get()).createSnapshot();
        }            
        return null;
    }
	
	private boolean validatePrivateSign(BlockSnapshot b){
		if (!b.getState().getType().getName().contains("sign")){
			return true;
		}
		String line = b.get(Keys.SIGN_LINES).get().get(0).toPlain();
		if (line.equalsIgnoreCase("[private]") || line.equalsIgnoreCase("private") || line.equalsIgnoreCase(RPLang.get("blocklistener.container.signline")) || line.equalsIgnoreCase("["+RPLang.get("blocklistener.container.signline")+"]")){
		    return true;
		}
		return false;
	}
	
	private boolean validateBreakSign(BlockSnapshot b, Player p){
		if (!b.getState().getType().getName().contains("sign")){
			return true;
		}
		String line = b.get(Keys.SIGN_LINES).get().get(0).toPlain();
		String line1 = b.get(Keys.SIGN_LINES).get().get(1).toPlain();
		if ((line.equalsIgnoreCase("[private]") || line.equalsIgnoreCase("private") || line.equalsIgnoreCase(RPLang.get("blocklistener.container.signline")) || line.equalsIgnoreCase("["+RPLang.get("blocklistener.container.signline")+"]")) && 
			!line1.equals(p.getName())){
		    return false;
		}
		return true;
	}
	
	private boolean validateOpenBlock(BlockSnapshot b, Player p){
		if (!b.getState().getType().getName().contains("sign")){
			return true;
		}
		List<Text> lines = b.get(Keys.SIGN_LINES).get();
		if ((lines.get(0).toPlain().equalsIgnoreCase("[private]") || lines.get(0).toPlain().equalsIgnoreCase("private") || lines.get(0).toPlain().equalsIgnoreCase(RPLang.get("blocklistener.container.signline")) || lines.get(0).toPlain().equalsIgnoreCase("["+RPLang.get("blocklistener.container.signline")+"]")) && 
			(!lines.get(1).toPlain().equals(p.getName()) &&
			!lines.get(2).toPlain().equals(p.getName()) &&
			!lines.get(3).toPlain().equals(p.getName()))){
		    return false;
		}
		return true;
	}
	    
	public boolean isContainer(BlockSnapshot block){
		Location<World> loc = block.getLocation().get();
		List<String> blocks = RedProtect.cfgs.getStringList("private.allowed-blocks");
	    if (blocks.contains(getRelative(loc,Direction.DOWN).getBlockType().getName()) ||
	    		blocks.contains(getRelative(loc,Direction.UP).getBlockType().getName()) ||
	    		blocks.contains(getRelative(loc,Direction.EAST).getBlockType().getName()) ||
	    		blocks.contains(getRelative(loc,Direction.NORTH).getBlockType().getName()) ||
	    		blocks.contains(getRelative(loc,Direction.SOUTH).getBlockType().getName()) ||
	    		blocks.contains(getRelative(loc,Direction.WEST).getBlockType().getName())){
	    	return true;
	    }	    
	    return false;
    }  
	    
	private Location<World> getRelative(Location<World> loc,Direction dir){
		return loc.getRelative(dir);
	}
}
