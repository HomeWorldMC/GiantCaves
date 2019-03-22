package com.ryanmichela.giantcaves;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;


import java.util.WeakHashMap;
import java.util.logging.Logger;

/**
 * Copyright 2013 Ryan Michela
 */
public class GCWaterHandler implements Listener {
    // Keep a map of previous random generators, one per chunk.
    // WeakHashMap is used to ensure that chunk unloading is not inhibited. This map won't
    // prevent a chunk from being unloaded and garbage collected.
    private final WeakHashMap<Chunk, GCRandom> randoms = new WeakHashMap<>();

    private final Config config;
    private final Logger log;

    public GCWaterHandler(Config config) {
        this.config = config;
        log = Bukkit.getLogger();
    }

    @EventHandler
    public void FromToHandler(BlockFromToEvent event) {
        // During chunk generation, nms.World.d is set to true. While true, liquids
        // flow continuously instead tick-by-tick. See nms.WorldGenLiquids line 59.
        Block b = event.getBlock();
        if (b.getType() == Material.WATER || b.getType() == Material.LAVA) {
            //boolean continuousFlowMode = ReflectionUtil.getProtectedValue(((CraftWorld) event.getBlock().getWorld()).getHandle(), "d");
            boolean continuousFlowMode = isContinuousFlowMode((CraftWorld) event.getBlock().getWorld());
            if (continuousFlowMode) {
                Chunk c = b.getChunk();
                if (!randoms.containsKey(c)) {
                    randoms.put(c, new GCRandom(c, config));
                }
                GCRandom r = randoms.get(c);

                if (r.isInGiantCave(b.getX(), b.getY(), b.getZ())) {
                    if (b.getData() == 0) { // data value of 0 means source block
                            event.setCancelled(true);
                    }
                }
            }
        }
    }

    boolean isContinuousFlowMode(CraftWorld world) {
        Object handle = ReflectionUtil.getProtectedValue(world, "world");
        Object d = ReflectionUtil.getProtectedValue(handle, "d");
        
        if (handle instanceof ObjectLinkedOpenHashSet) {
	        for (Object o : (ObjectLinkedOpenHashSet<?>) handle) {        	
	        	Class<?> c = o.getClass();
	        	c.getName();
	        	System.out.println("Handle Class name : " + c.getName().toString());
	        	
	        	//log.info("testing my logger..." + c.getName().toString());
	        }
	        
	        for (Object o : (ObjectLinkedOpenHashSet<?>) d) {        	
	        	Class<?> c = o.getClass();
	        	c.getName();
	        	System.out.println("d Class name : " + c.getName().toString());
	        }
    	}
        //return (boolean) d;
        return false;
    }
}
