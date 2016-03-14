package br.net.fabiozumbi12.redprotect;

import java.util.List;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class RPContainer {

	public boolean canOpen(BlockSnapshot b, Player p) {
    	if (!RedProtect.cfgs.getBool("private.use")){
    		return true;
    	}
    	
    	String blocktype = b.getExtendedState().getType().getName();         
    	
        if (RedProtect.cfgs.getStringList("private.allowed-blocks").contains(blocktype)){
        	int x = b.getLocation().get().getBlockX();
            int y = b.getLocation().get().getBlockY();
            int z = b.getLocation().get().getBlockZ();
            World w = p.getWorld();        
            
            for (int sx = -1; sx <= 1; sx++){
            	for (int sz = -1; sz <= 1; sz++){
            		
            		BlockSnapshot bsb = w.createSnapshot(x+sx, y, z+sz);            		
            		TileEntity bs = bsb.getLocation().get().getTileEntity().get();
            		
    				if (bs instanceof Sign && !validateOpenBlock(bs, p) && getBlockRelative(bs).getExtendedState().getType().getName().equals(b.getExtendedState().getType().getName())){
    					return false;
                	}
    		        
    		        int x2 = bsb.getLocation().get().getBlockX();
    	            int y2 = bsb.getLocation().get().getBlockY();
    	            int z2 = bsb.getLocation().get().getBlockZ();
    	            
    	            String blocktype2 = bsb.getExtendedState().getType().getName(); 
    	            
    				if (RedProtect.cfgs.getStringList("private.allowed-blocks").contains(blocktype2)){    					
    					for (int ux = -1; ux <= 1; ux++){
    						for (int uz = -1; uz <= 1; uz++){

    							BlockSnapshot bub = w.createSnapshot(x2+ux, y2, z2+uz);            		
    		            		TileEntity bu = bub.getLocation().get().getTileEntity().get();
    		            		
    	        				if (bu instanceof Sign && !validateOpenBlock(bu, p) && getBlockRelative(bu).getExtendedState().getType().getName().equals(b.getExtendedState().getType().getName())){
    	        					return false;
    	                    	}
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
    	int x = b.getLocation().get().getBlockX();
        int y = b.getLocation().get().getBlockY();
        int z = b.getLocation().get().getBlockZ();
        World w = p.getWorld();

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
        } 
        return true;
    }
    
	public boolean canWorldBreak(BlockSnapshot b){		
    	if (!RedProtect.cfgs.getBool("private.use")){
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
        	RedProtect.logger.debug("Valid Sign on canWorldBreak!");
			return false;
    	}   		
           		
        String signbtype = b.getExtendedState().getType().getName();       
        
        if (RedProtect.cfgs.getStringList("private.allowed-blocks").contains(signbtype)){        	
        	for (int sx = -1; sx <= 1; sx++){
        		for (int sz = -1; sz <= 1; sz++){
        			
        			BlockSnapshot bsb = w.createSnapshot(x+sx, y, z+sz);
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
    							TileEntity bu = bub.getLocation().get().getTileEntity().get();
    	        				if (bu instanceof Sign && validatePrivateSign(bu)){
    	        					return false;
    	                    	}
    	        			}        	        		
        	        	}
    				}        				 
    			}	        		
        	}                   
        } 
        return true;
    }
	
	public static BlockSnapshot getBlockRelative(TileEntity block) {
        if (block instanceof Sign){
        	return block.getLocation().getRelative(block.getLocation().get(DirectionalData.class).get().direction().get()).createSnapshot();
        }            
        return null;
    }
	
	private boolean validatePrivateSign(TileEntity b){
		Sign s = (Sign) b;
		String line = s.get(Keys.SIGN_LINES).get().get(0).toString();
		if (line.equalsIgnoreCase("[private]") || line.equalsIgnoreCase("private") || line.equalsIgnoreCase(RPLang.get("blocklistener.container.signline").toString()) || line.equalsIgnoreCase("["+RPLang.get("blocklistener.container.signline")+"]")){
		    return true;
		}
		return false;
	}
	
	private boolean validateBreakSign(TileEntity b, Player p){
		Sign s = (Sign) b;
		String line = s.get(Keys.SIGN_LINES).get().get(0).toString();
		String line1 = s.get(Keys.SIGN_LINES).get().get(1).toString();
		if ((line.equalsIgnoreCase("[private]") || line.equalsIgnoreCase("private") || line.equalsIgnoreCase(RPLang.get("blocklistener.container.signline").toString()) || line.equalsIgnoreCase("["+RPLang.get("blocklistener.container.signline")+"]")) && 
			!line1.equals(p.getName())){
		    return false;
		}
		return true;
	}
	
	private boolean validateOpenBlock(TileEntity b, Player p){
		Sign s = (Sign) b;
		List<Text> lines = s.get(Keys.SIGN_LINES).get();
		if ((lines.get(0).toString().equalsIgnoreCase("[private]") || lines.get(0).toString().equalsIgnoreCase("private") || lines.get(0).toString().equalsIgnoreCase(RPLang.get("blocklistener.container.signline").toString()) || lines.get(0).toString().equalsIgnoreCase("["+RPLang.get("blocklistener.container.signline")+"]")) && 
			(!lines.get(1).toString().equals(p.getName()) &&
			!lines.get(2).toString().equals(p.getName()) &&
			!lines.get(3).toString().equals(p.getName()))){
		    return false;
		}
		return true;
	}
	    
	public boolean isContainer(BlockSnapshot block){    	
    	Location<World> loc = block.getLocation().get();    	    	
    	BlockSnapshot container = loc.getRelative(block.getLocation().get().get(DirectionalData.class).get().direction().get()).createSnapshot();
	    String signbtype = container.getExtendedState().getType().getName(); 
	    if (RedProtect.cfgs.getStringList("private.allowed-blocks").contains(signbtype)){
	    	return true;
	    }
	    return false;
    }  
    
}
