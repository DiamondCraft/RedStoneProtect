package me.tjs238.plugins.potionprotect;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.conversations.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Potionprotect extends JavaPlugin implements Listener, ConversationAbandonedListener {
    public Server server = Bukkit.getServer();
    private Logger log;
    private PluginDescriptionFile description;
    private String prefix;
    private ConversationFactory conversationFactory;
    
    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }
    
    @Override
    public void onEnable() {
        log = Logger.getLogger("Minecraft");
        description = getDescription();
        prefix = "["+description.getName()+"] ";
        log("Starting up...");
        getWorldGuard();
        getVault();
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    public Potionprotect() {
        this.conversationFactory = new ConversationFactory(this)
                .withModality(true)
                .withPrefix(new RegionSpawningPrefix())
                .withFirstPrompt(new WhichSizePrompt())
                .withEscapeSequence("/quit")
                .withTimeout(10)
                .thatExcludesNonPlayersWithMessage("Players Only")
                .addConversationAbandonedListener(this);
                
    }

    public void conversationAbandoned(ConversationAbandonedEvent cae) {
        if (cae.gracefulExit()) {
            cae.getContext().getForWhom().sendRawMessage(ChatColor.AQUA+"Closing Builder");
        } else {
            cae.getContext().getForWhom().sendRawMessage(ChatColor.AQUA+"Builder Closed Forcably!");
        }
    }
    
    private class WhichSizePrompt extends FixedSetPrompt {
        public WhichSizePrompt() {
            super("10", "20", "30", "None");
        }
        public String getPromptText(ConversationContext context) {
            return "What size region would you like to create? " + formatFixedSet();
        }
        
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            if (s.equals("None")) {
                return Prompt.END_OF_CONVERSATION;
            }
            context.setSessionData("type", s);
            return new AreYouSurePrompt();
        }
    }
    
    private class AreYouSurePrompt extends FixedSetPrompt {
        public AreYouSurePrompt() {
            super("Yes", "No", "yes", "no");
        }
        
        public String getPromptText(ConversationContext context) {
            return "Are you sure you want to make this region? [Yes, No]";
        }
        
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            if (s.equals("No") || s.equals("no")) {
                return Prompt.END_OF_CONVERSATION;
            }
            context.setSessionData("agreed", s);
            return new ForWhomPrompt(context.getPlugin());
        }
    }
    
    private class ForWhomPrompt extends PlayerNamePrompt {
        public ForWhomPrompt(Plugin plugin) {
            super(plugin);
        }

        public String getPromptText(ConversationContext context) {
            return "Who should receive your region?";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Player player) {
            context.setSessionData("who", player);
            return new SpawnRegionPrompt();
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return invalidInput + " is not online!";
        }
    }
    
    private class SpawnRegionPrompt extends MessagePrompt {
        public String getPromptText(ConversationContext context) {
            Player player = (Player)context.getSessionData("who");
            String type = (String)context.getSessionData("type");
            try {
                if (player != null)
                createRegion(player, type);
                else
                Logger.getLogger(Potionprotect.class.getName()).log(Level.SEVERE, null, "Player is null!");
            } catch (RegionOperationException ex) {
                Logger.getLogger(Potionprotect.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IncompleteRegionException ex) {
                Logger.getLogger(Potionprotect.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "Still working on this!!!";
        }
        
        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }
    
    private class RegionSpawningPrefix implements ConversationPrefix {
        public String getPrefix(ConversationContext context) {
            String what = (String)context.getSessionData("type");
            Player who = (Player)context.getSessionData("who");
            
            if (what != null && who == null) {
                return ChatColor.AQUA+"[DCR]Region-Size:"+what+": ";
            }
            if (what != null && who != null) {
                return ChatColor.AQUA+"[DCR]Region-Size:"+what+" Player: "+who.getDisplayName()+": ";
            }
            return ChatColor.AQUA+"[DCR] ";
        }
    }
    
    public WorldGuardPlugin getWorldGuard()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin)))
        {
            return null; //throws a NullPointerException, telling the Admin that WG is not loaded.
        }
        return (WorldGuardPlugin)plugin;
    }
    
    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if ((plugin == null) || (!(plugin instanceof WorldEditPlugin))) {
            return null;
        }
        return (WorldEditPlugin)plugin;
    }
    
    public Vault getVault() {
        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if ((vault == null) || (!(vault instanceof Vault))) {
            return null;
        }
        return (Vault)vault;
    }
    
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        
        if(block.getType().equals(Material.REDSTONE_ORE)) {
            if (player instanceof Conversable && player.hasPermission("dc.protect.block")) {
                player.sendMessage(ChatColor.AQUA+"[DCR]Starting Region Build...");
                conversationFactory.buildConversation((Conversable)player).begin();
                event.setCancelled(true);
                ItemStack redstone = new ItemStack(Material.REDSTONE_ORE, 1);
                Inventory inv = player.getInventory();
                inv.remove(redstone);
            } else {
                player.sendMessage(ChatColor.RED+"Error: 1908");
                player.sendMessage(ChatColor.RED+"Please contact Staff!");
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    public void createRegion(Player player, String size) throws RegionOperationException, IncompleteRegionException {
        Vector vector = new Vector();
        //WorldEditPlugin worldEdit = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
        //WorldGuardPlugin worldGuard = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
        log("Getting WorldGuard");
        WorldGuardPlugin worldGuard = getWorldGuard();
        log("Getting WorldEdit");
        WorldEditPlugin worldEdit = getWorldEdit();
        CuboidRegionSelector selector = new CuboidRegionSelector();
        log("Setting the points");
        Location pos1 = player.getLocation().add(10, 0 ,10);
        Location pos2 = player.getLocation().subtract(10, 0, 10);
        log("Setting the vectors");
        Vector vpos1 = vector.add(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ());
        Vector vpos2 = vector.add(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ());
        log("Selecting primary");
        selector.selectPrimary(vpos1);
        log("Selecting secondary");
        selector.selectSecondary(vpos2);
        log("Learning changes");
        selector.learnChanges();
        log("Getting the selection above");
        if (player == null) {
            return;
        }
            log("Making it top to bottom");
                selector.getRegion().expand(
                            new Vector(0, (player.getWorld().getMaxHeight() +1), 0),
                            new Vector(0, (player.getWorld().getMaxHeight() - 1), 0)
                            );
                log("Learning that");
                selector.learnChanges();
            
                log("Setting block vectors again");
        BlockVector min = selector.getRegion().getPos1().toBlockVector();
        BlockVector max = selector.getRegion().getPos2().toBlockVector();
        RegionManager rm = worldGuard.getGlobalRegionManager().get(player.getWorld());
        log("Getting random number");
        Random rand = new Random();
        ProtectedRegion region;
        int range1 = rand.nextInt(20);
        if (player.hasPermission("dc.protect")) {
            log("Setting region");
            String rname = player.getName()+"_"+range1;
            region = new ProtectedCuboidRegion(rname, min, max);
            rm.addRegion(region);
            log("Region created with name: "+rname);
        }
        
    }

    /*@EventHandler
    public void onItemDrop(PlayerDropItemEvent event) throws IncompleteRegionException {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        WorldGuardPlugin worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        WorldEditPlugin worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        
        if (item.equals(Material.POTION)) {
            event.setCancelled(true);
            Vector vector = new Vector();
            CuboidRegionSelector selector = new CuboidRegionSelector();
            Location pos1 = player.getLocation().add(10, 0, 10);
            Location pos2 = player.getLocation().subtract(10, 0, 10);
            Vector vpos1 = vector.add(pos1.getX(), pos1.getY(), pos1.getZ());
            Vector vpos2 = vector.add(pos2.getX(), pos2.getY(), pos2.getZ());
            selector.selectPrimary(vpos1);
            selector.selectSecondary(vpos2);
            
            Selection sel = worldEdit.getSelection(player);
            int oldSize = sel.getArea();
            Region region = sel.getRegionSelector().getRegion();
            try {
                sel.getRegionSelector().getRegion().expand(
                        new Vector(0, (player.getWorld().getMaxHeight() + 1), 0),
                        new Vector(0, (player.getWorld().getMaxHeight() + 1), 0));
                
                // TODO: Add World Guard Dependancy
            } catch (RegionOperationException ex) {
                Logger.getLogger(Potionprotect.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            BlockVector min = sel.getNativeMinimumPoint().toBlockVector();
            BlockVector max = sel.getNativeMaximumPoint().toBlockVector();
            ProtectedRegion pr;
            Random generator2 = new Random( 19580427 );
            if (player.hasPermission("dc.protect")) {
                pr = new ProtectedCuboidRegion(player.getName()+generator2, min, max);
            }
            
        }
    }*/
    
    public void log(String message){
        log.info(prefix+message);
    }
}

