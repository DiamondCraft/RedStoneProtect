/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.tjs238.plugins.potionprotect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author tjs238
 */
public class Config {
    private Potionprotect plugin;
    public String cprefix;
    public Config (Potionprotect plugin) {
        this.plugin = plugin;
    }
    public  FileConfiguration config;
    public void loadConfig() {
        File configFile = new File("plugins/Potionprotect", "config.yml");
        config = plugin.getConfig();
        if (!configFile.exists()) {
            plugin.log("No config found, creating a fresh one!");
            config.set("prefix", "[RP]");
            config.set("version", 1.3);
            config.set("region.sizes.1", "10");
            config.set("region.sizes.2", "20");
            config.set("region.sizes.3", "40");
            try {
                config.save(configFile);
                plugin.log("Config Created!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            plugin.log("Config Loaded!");
            cprefix = config.getString("prefix");
        }
    }
}
