package me.rayzr522.tntlimiter;

import me.rayzr522.tntlimiter.command.CommandTntLimiter;
import me.rayzr522.tntlimiter.utils.MessageHandler;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Rayzr
 */
public class TntLimiter extends JavaPlugin implements Listener {
    private static TntLimiter instance;
    private MessageHandler messages = new MessageHandler();

    /**
     * @return The current instance of TntLimiter.
     */
    public static TntLimiter getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Load configs
        reload();

        // Set up commands
        getCommand("tntlimiter").setExecutor(new CommandTntLimiter(this));

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent e) {
        if (e.getEntityType() != EntityType.PRIMED_TNT) {
            return;
        }

        e.blockList().removeIf(block -> block.getType() == Material.TNT);
    }

    /**
     * (Re)loads all configs from the disk
     */
    public void reload() {
        saveDefaultConfig();
        reloadConfig();

        messages.load(getConfig("messages.yml"));
    }

    /**
     * If the file is not found and there is a default file in the JAR, it saves the default file to the plugin data folder first
     *
     * @param path The path to the config file (relative to the plugin data folder)
     * @return The {@link YamlConfiguration}
     */
    public YamlConfiguration getConfig(String path) {
        if (!getFile(path).exists() && getResource(path) != null) {
            saveResource(path, true);
        }
        return YamlConfiguration.loadConfiguration(getFile(path));
    }

    /**
     * Attempts to save a {@link YamlConfiguration} to the disk, with any {@link IOException}s being printed to the console.
     *
     * @param config The config to save
     * @param path   The path to save the config file to (relative to the plugin data folder)
     */
    public void saveConfig(YamlConfiguration config, String path) {
        try {
            config.save(getFile(path));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save config", e);
        }
    }

    /**
     * @param path The path of the file (relative to the plugin data folder)
     * @return The {@link File}
     */
    public File getFile(String path) {
        return new File(getDataFolder(), path.replace('/', File.separatorChar));
    }

    /**
     * Translates a message from the language file.
     *
     * @param key     The key of the message to translate
     * @param objects The formatting objects to use
     * @return The formatted message
     */
    public String tr(String key, Object... objects) {
        return messages.tr(key, objects);
    }

    /**
     * Checks a target {@link CommandSender} for a given permission, and optionally sends a message if they don't.
     * <br>
     * This will automatically prefix any permission with the name of the plugin.
     *
     * @param target      The target {@link CommandSender} to check
     * @param permission  The permission to check, excluding the permission base (which is the plugin name)
     * @param sendMessage Whether or not to send a no-permission message to the target
     * @return Whether or not the target has the given permission
     */
    public boolean checkPermission(CommandSender target, String permission, boolean sendMessage) {
        String fullPermission = String.format("%s.%s", getName(), permission);

        if (!target.hasPermission(fullPermission)) {
            if (sendMessage) {
                target.sendMessage(tr("no-permission", fullPermission));
            }

            return false;
        }

        return true;
    }

    /**
     * @return The {@link MessageHandler} instance for this plugin
     */
    public MessageHandler getMessages() {
        return messages;
    }

}
