package me.darkolythe.multitool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.darkolythe.multitool.MultitoolInventory.giveMultitool;

public class MultitoolCommand implements CommandExecutor {

	private Multitool main = Multitool.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("multitool.command")) {
				if (cmd.getName().equalsIgnoreCase("Multitool")) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("O")) {
							player.openInventory(main.multitoolutils.getToolInv(player));
						} else if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("T")) {
							main.multitoolutils.setToggle(player.getUniqueId(), !main.multitoolutils.getToggle(player.getUniqueId()));
							if (main.multitoolutils.getToggle(player.getUniqueId())) {
								sender.sendMessage(main.prefix + ChatColor.GREEN + "Multitool On!");
							} else {
								sender.sendMessage(main.prefix + ChatColor.GREEN + "Multitool Off!");
							}
						} else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
							giveMultitool(main, player);
						} else if (args[0].equalsIgnoreCase("reload")) {
							if (player.hasPermission("multitool.reload")) {
								main.multitoolutils.reload();
								player.sendMessage(main.prefix + ChatColor.GREEN + "The config has been reloaded");
							} else {
								sender.sendMessage(main.prefix + ChatColor.RED + "You do not have permission to do that!");
							}
						} else {
							sender.sendMessage(main.prefix + ChatColor.RED + "Invalid Arguments: /mt [open, toggle, create]");
						}
					} else if (args.length == 2 && (args[0].equalsIgnoreCase("Open") || args[0].equalsIgnoreCase("O"))) {
						if (player.hasPermission("multitool.useothers")) {
							for (Player players : Bukkit.getServer().getOnlinePlayers()) {
								if (args[1].equalsIgnoreCase(players.getName())) {
									player.openInventory(main.multitoolutils.getToolInv(players));
									return true;
								}
							}
						} else {
							sender.sendMessage(main.prefix + ChatColor.RED + "You do not have permission to do that!");
						}
						sender.sendMessage(main.prefix + ChatColor.RED + "Player is not online");
					} else {
						sender.sendMessage(main.prefix + ChatColor.RED + "Invalid Arguments: /mt [open, toggle, create]");
					}
				}
			} else {
				sender.sendMessage(main.prefix + ChatColor.RED + "You do not have permission to do that!");
			}
		}
		return true;
	}
	
}
