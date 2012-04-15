package me.tjs238.plugins.potionprotect;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Potionprotect extends JavaPlugin implements Listener {
    public Server server = Bukkit.getServer();
    private Logger log;
    private PluginDescriptionFile description;
    private String prefix;
    
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public void onEnable() {
        log = Logger.getLogger("Minecraft");
        description = getDescription();
        prefix = "["+description.getName()+"] ";
        log("Starting up...");
        getServer().getPluginManager().registerEvents(this, this);
        
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        
        if (item.equals(Material.POTION)) {
            event.setCancelled(true);
            Location init = player.getLocation();
            double x1 = init.getX();
            double z1 = init.getZ();
            double y = init.getY();
            Location pos1 = init.add(x1 + 10, y, z1 + 10);
            Location pos2 = init.add(x1 - 10, y, z1 + 10);
            // TODO: Add World Guard Dependancy
        }
    }
    
    public void log(String message){
        log.info(prefix+message);
    }
}

