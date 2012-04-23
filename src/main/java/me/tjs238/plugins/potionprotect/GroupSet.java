/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.tjs238.plugins.potionprotect;

import java.util.*;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author tjs238
 */
public class GroupSet implements CommandExecutor{
    
    private WorldsHolder worldsHolder;
    private OverloadedWorldHolder dataHolder = null;
    private Map<CommandSender, String> selectedWorlds = new HashMap<CommandSender, String>();
    private boolean validateOnlinePlayer = true;
    private Potionprotect plugin;
    
    public GroupSet (Potionprotect plugin) {
        this.plugin = plugin;
    }
    /**
	 * @return the validateOnlinePlayer
	 */
	public boolean isValidateOnlinePlayer() {

		return validateOnlinePlayer;
	}

	/**
	 * @param validateOnlinePlayer the validateOnlinePlayer to set
	 */
	public void setValidateOnlinePlayer(boolean validateOnlinePlayer) {

		this.validateOnlinePlayer = validateOnlinePlayer;
	}


    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (!sender.hasPermission("dc.promote.donors")) {
            sender.sendMessage(ChatColor.RED+"You do not have permission to promote");
            return true;
        }
        User auxUser = null;
        Group auxGroup = null;
        String auxString = null;
        List<String> match = null;
        if ((args.length != 2) && (args.length != 3)) {
            sender.sendMessage(ChatColor.RED+"Review your arguments! (/groupset <player> <group>");
            return false;
        }
        
        //Select the world
        if (args.length == 3) {
            dataHolder = worldsHolder.getWorldData(args[2]);
        }
        
        //Validate state of sender
        if (dataHolder == null) {
            if (!setDefaultWorldHandler(sender))
                return true;
        }
        if((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
            return false;
        }
        
        if (match != null) {
            auxUser = dataHolder.getUser(match.get(0));
        } else {
            auxUser = dataHolder.getUser(args[0]);
        }
        auxGroup = dataHolder.getGroup(args[1]);
        if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group not found!");
					return false;
				}
        List<String> donorGroups = new ArrayList<String>();
        donorGroups.add("VIP");
        donorGroups.add("Premium");
        donorGroups.add("Supporter");
        donorGroups.add("Executive");
        donorGroups.add("CEO");
        donorGroups.add("Elite");
        donorGroups.add("Legendary");
        donorGroups.add("Diamond");
        if (!donorGroups.contains(args[1]) && sender.hasPermission("dc.promote.donors")) {
            sender.sendMessage(ChatColor.RED+"You are not allowed to promote over Donor ranks!");
            return true;
        } else if (donorGroups.contains(args[1]) && sender.hasPermission("dc.promote.donors")) {
            auxUser.setGroup(auxGroup);
            sender.sendMessage(ChatColor.YELLOW+"You changed '"+auxUser.getName()+"' group to '"+auxGroup.getName());
            auxUser.getBukkitPlayer().sendMessage(ChatColor.YELLOW+"You have been promoted to the rank of: "+auxGroup.getName());
            return true;
        }
        
        return true;
    }
    
    private boolean setDefaultWorldHandler(CommandSender sender) {

		dataHolder = worldsHolder.getWorldData(worldsHolder.getDefaultWorld().getName());

		if ((dataHolder != null)) {
			selectedWorlds.put(sender, dataHolder.getName());
			sender.sendMessage(ChatColor.RED + "Couldn't retrieve your world. Default world '" + worldsHolder.getDefaultWorld().getName() + "' selected.");
			return true;
		}

		sender.sendMessage(ChatColor.RED + "Couldn't retrieve your world. World selection is needed.");
		sender.sendMessage(ChatColor.RED + "Use /manselect <world>");
		return false;

	}
    
    private List<String> validatePlayer(String playerName, CommandSender sender) {

		List<Player> players = new ArrayList<Player>();
		List<String> match = new ArrayList<String>();

		players = Bukkit.getServer().matchPlayer(playerName);
		if (players.isEmpty()) {
			// Check for an offline player (exact match).
			if (Arrays.asList(Bukkit.getServer().getOfflinePlayers()).contains(Bukkit.getOfflinePlayer(playerName))) {
				match.add(playerName);
			} else {
				// look for partial matches
				for (OfflinePlayer offline : Bukkit.getServer().getOfflinePlayers()) {
					if (offline.getName().toLowerCase().startsWith(playerName.toLowerCase()))
						match.add(offline.getName());
				}
			}

		} else {
			for (Player player : players) {
				match.add(player.getName());
			}
		}

		if (match.isEmpty() || match == null) {
			sender.sendMessage(ChatColor.RED + "Player not found!");
			return null;
		} else if (match.size() > 1) {
			sender.sendMessage(ChatColor.RED + "Too many matches found! (" + match.toString() + ")");
			return null;
		}

		return match;

	}


    
}
