package me.rayzr522.tntlimiter.command;

import me.rayzr522.tntlimiter.TntLimiter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandTntLimiter implements CommandExecutor {
    private final TntLimiter plugin;

    public CommandTntLimiter(TntLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if (!(sender instanceof Player)) {
//            sender.sendMessage(plugin.tr("command.fail.only-players"));
//            return true;
//        }
//
//        Player player = (Player) sender;

        if (!plugin.checkPermission(sender, "use", true)) {
            return true;
        }

        if (args.length < 1) {
            showUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "version":
                sender.sendMessage(plugin.tr("command.tntlimiter.version", plugin.getName(), plugin.getDescription().getVersion()));
                break;
            case "reload":
                plugin.reload();
                sender.sendMessage(plugin.tr("command.tntlimiter.reloaded"));
                break;
            case "help":
            case "?":
            default:
                showUsage(sender);
        }

        return true;
    }

    private void showUsage(CommandSender sender) {
        sender.sendMessage(plugin.tr("command.tntlimiter.help.message"));
    }
}