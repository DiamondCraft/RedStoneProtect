/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.tjs238.plugins.potionprotect;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.commands.RegionCommands;
import com.sk89q.worldedit.regions.CuboidRegionSelector;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

/**
 *
 * @author tjs238
 */
public class SpawnFence {
    public static void SpawnFence(Vector pos1, Vector pos2, String size, WorldEdit we) {
        World worldf = Bukkit.getWorld("world");
        BukkitWorld BWf = new BukkitWorld(worldf);
        EditSession es = new EditSession(BWf, 2000000);
        if (size.equals("10")) {
            CuboidRegionSelector selector = new CuboidRegionSelector();
            selector.selectPrimary(pos1);
            selector.selectSecondary(pos2);
            selector.learnChanges();
            RegionCommands rc = new RegionCommands(we);
            int affected;
            BaseBlock bb = new BaseBlock(85);
            try {
                es.makeCuboidWalls(selector.getRegion(), bb);
            } catch (IncompleteRegionException ex) {
                Logger.getLogger(SpawnFence.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MaxChangedBlocksException ex) {
                Logger.getLogger(SpawnFence.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } 
    
}
