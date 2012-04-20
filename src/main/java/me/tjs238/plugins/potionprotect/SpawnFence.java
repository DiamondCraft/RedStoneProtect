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
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author tjs238
 */
public class SpawnFence {
    public static void SpawnFence(Vector pos1, Vector pos2, String size, WorldEdit we, Player player) throws IncompleteRegionException, RegionOperationException {
        World worldf = Bukkit.getWorld("world");
        BukkitWorld BWf = new BukkitWorld(worldf);
        EditSession es = new EditSession(BWf, 2000000);
            CuboidRegionSelector selector = new CuboidRegionSelector();
            Vector vpos1 = new Vector(pos1.getBlockX(), player.getLocation().getBlockY(), pos1.getBlockZ());
            Vector vpos2 = new Vector(pos2.getBlockX(), player.getLocation().getBlockY(), pos2.getBlockZ());
            RegionCommands rc = new RegionCommands(we);
            selector.selectPrimary(vpos1);
            selector.selectSecondary(vpos2);
            selector.learnChanges();
            BaseBlock bb = new BaseBlock(85);
            try {
                Region region = selector.getRegion();
                es.makeCuboidWalls(selector.getRegion(), bb);
            } catch (IncompleteRegionException ex) {
                Logger.getLogger(SpawnFence.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MaxChangedBlocksException ex) {
                Logger.getLogger(SpawnFence.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
}
