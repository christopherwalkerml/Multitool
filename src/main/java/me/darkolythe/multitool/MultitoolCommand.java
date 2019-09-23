package me.darkolythe.multitool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultitoolCommand implements CommandExecutor {

	private Multitool main = Multitool.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("multitool.command")) {
				if (cmd.getName().equalsIgnoreCase("Multitool")) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("Open") || args[0].equalsIgnoreCase("O")) {
							player.openInventory(main.multitoolutils.getToolInv(player));
						} else if (args[0].equalsIgnoreCase("Toggle") || args[0].equalsIgnoreCase("T")) {
							main.multitoolutils.setToggle(player.getUniqueId(), !main.multitoolutils.getToggle(player.getUniqueId()));
							if (main.multitoolutils.getToggle(player.getUniqueId())) {
								sender.sendMessage(main.prefix + ChatColor.GREEN + "Multitool On!");
							} else {
								sender.sendMessage(main.prefix + ChatColor.GREEN + "Multitool Off!");
							}
						} else {
							sender.sendMessage(main.prefix + ChatColor.RED + "Invalid Arguments: /mt [open, toggle]");
						}
					} else if (args.length == 2 && (args[0].equalsIgnoreCase("Open") || args[0].equalsIgnoreCase("O"))) {

						for (Player players : Bukkit.getServer().getOnlinePlayers()) {
							if (args[1].equalsIgnoreCase(players.getName())) {
								player.openInventory(main.multitoolutils.getToolInv(players));
								return true;
							}
						}
						sender.sendMessage(main.prefix + ChatColor.RED + "Player is not online");
					} else {
						sender.sendMessage(main.prefix + ChatColor.RED + "Invalid Arguments: /mt [open, toggle]");
					}
				}
			}
		}
		return true;
	}
	
}
