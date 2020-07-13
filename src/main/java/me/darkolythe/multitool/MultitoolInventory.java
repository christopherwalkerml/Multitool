package me.darkolythe.multitool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MultitoolInventory implements Listener {
	
	private Multitool main;
	
	public MultitoolInventory(Multitool plugin) {
		this.main = plugin; // set it equal to an instance of main
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (player.hasPermission("multitool.command")) {
			InventoryView view = player.getOpenInventory();
			Inventory inv = player.getOpenInventory().getTopInventory();

			Map<String, Integer> toolMap = new HashMap<>();
			toolMap.put("SWORD", 0);
			toolMap.put("PICKAXE", 1);
			toolMap.put("AXE", 2);
			toolMap.put("SHOVEL", 3);

			if (event.getClickedInventory() != null) { //if the user clicks an inventory
				if (event.getClickedInventory() != player.getInventory()) {
					if (view.getTitle().equals(ChatColor.BLUE + "Multitools")) {
						if (player.getItemOnCursor().getType() != Material.AIR) { //if the cursor has an item in it
							Material cursorstack = player.getItemOnCursor().getType();
							if (event.getCurrentItem() != null) {
								ItemStack clickstack = event.getCurrentItem().clone();
								if (clickstack.getType() == Material.GRAY_STAINED_GLASS_PANE) { //if the clicked item is a glass pane

									String type = cursorstack.toString();
									for (String s : toolMap.keySet()) {
										if (type.contains(s) && inv.getItem(toolMap.get(s)).getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
											inv.setItem(toolMap.get(s), player.getItemOnCursor());
											player.setItemOnCursor(null);
											break;
										}
									}
								}
							}
							event.setCancelled(true);
						} else {
							if (event.getCurrentItem() != null) {
								ItemStack clickstack = event.getCurrentItem().clone();
								boolean removemt = false;
								String type = clickstack.getType().toString();
								for (String s : toolMap.keySet()) {
									if (type.contains(s)) {
										inv.setItem(toolMap.get(s), main.placeholders.get(toolMap.get(s)));
										player.setItemOnCursor(clickstack);
										removemt = true;
										break;
									}
								}
								if (type.contains("FEATHER")) {
									boolean forloop = false;
									ItemStack genstack = null;
									for (int i = 0; i < 5; i++) { //this loops through the mt inv, and gives the player the first multitool that shows up
										if (main.toolinv.get(player.getUniqueId()).getItem(i) != null) {
											Material curmat = main.toolinv.get(player.getUniqueId()).getItem(i).getType();
											forloop = false;
											if (curmat != Material.GRAY_STAINED_GLASS_PANE && curmat != Material.FEATHER) {
												genstack = main.toolinv.get(player.getUniqueId()).getItem(i).clone();
												ItemMeta genmeta = genstack.getItemMeta();
												genmeta.setLore(addLore(genmeta, main.toollore, false));
												genstack.setItemMeta(genmeta);
												forloop = true; //this means a tool has been found, and will be given to the player if they have space
												break;
											}
										}
									}
									if (!forloop) {
										player.sendMessage(main.prefix + ChatColor.RED + "The Multitool is empty!");
									} else {
										Inventory plrinv = player.getInventory();
										boolean hasitem = false;
										for (ItemStack i : plrinv) {
											if (i != null) {
												if (i.getItemMeta() != null) {
													if (main.multitoolutils.isTool(i)) {
														hasitem = true;
													}
												}
											}
										}

										boolean giveitem;
										if (plrinv.firstEmpty() == -1) {
											giveitem = false;
										} else {
											giveitem = true;
										}

										if (giveitem && !hasitem) {
											plrinv.addItem(genstack);
											player.sendMessage(main.prefix + ChatColor.GREEN + "You have been given your Multitool!");
											main.lastblock.put(player.getUniqueId(), Material.AIR);
										} else if (!hasitem) {
											player.sendMessage(main.prefix + ChatColor.RED + "There's no space in your inventory!");
										} else {
											player.sendMessage(main.prefix + ChatColor.RED + "You already have your multitool!");
										}
									}
									event.setCancelled(true);
									player.closeInventory();
								}
								if (removemt) {
									Inventory plrinv = player.getInventory(); //this removes the multitool from the player's inventory if a tool is removed from the list
									for (ItemStack i : plrinv) {
										if (i != null) {
											if (i.getItemMeta() != null) {
												if (main.multitoolutils.isTool(i)) {
													plrinv.remove(i);
													player.sendMessage(main.prefix + ChatColor.RED + "You removed a tool from your Multitool!");
													event.setCancelled(true);
													return;
												}
											}
										}
									}
								}
							}
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	public List<String> addLore(ItemMeta meta, String line, boolean top) {
		List<String> newlore = new ArrayList<>();
		if (!top) {
			if (meta.hasLore() && meta.getLore() != null) {
				newlore = meta.getLore();
			} else {
				newlore = new ArrayList<>();
			}
			newlore.add(line);
		} else {
			if (meta.hasLore()) {
				List<String> lore = meta.getLore();
				newlore.add(line);
				for (String str : lore) {
					newlore.add(str);
				}
			} else {
				newlore.add(line);
			}
		}
		return newlore;
	}
}
