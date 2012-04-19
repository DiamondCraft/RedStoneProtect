/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.tjs238.plugins.potionprotect;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.entity.Player;


/**
 *
 * @author tjs238
 */
public class RegionHandler {
    ProtectedRegion pr;
    Potionprotect plugin;
    public RegionHandler(WorldGuardPlugin wg, BlockVector min, BlockVector max, String pname, Player player, Potionprotect plugin) {
        this.plugin = plugin;
        if (wg == null) {
            return;
        }
        RegionManager rm = wg.getGlobalRegionManager().get(player.getWorld());
        pr = new ProtectedCuboidRegion(pname, min, max);
        if (rm.hasRegion(pname)) {
            Random rand = new Random();
            int range1 = rand.nextInt(200);
            String rname = player.getName()+"_"+range1;
            pr = new ProtectedCuboidRegion(rname,min,max);
        }
        plugin.log("Creating the region from another class");
        rm.addRegion(pr);
    }
}
