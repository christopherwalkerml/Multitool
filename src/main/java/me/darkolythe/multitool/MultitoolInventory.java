package me.darkolythe.multitool;

import java.util.ArrayList;
import java.util.List;

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
			if (event.getClickedInventory() != null) { //if the user clicks an inventory
				if (event.getClickedInventory() != player.getInventory()) {
					if (view.getTitle().equals(ChatColor.GREEN + "Multitools")) {
						if (player.getItemOnCursor().getType() != Material.AIR) { //if the cursor has an item in it
							Material cursorstack = player.getItemOnCursor().getType();
							if (event.getCurrentItem() != null) {
								ItemStack clickstack = event.getCurrentItem().clone();
								if (clickstack.getType() == Material.GRAY_STAINED_GLASS_PANE) { //if the clicked item is a glass pane
									if (clickstack.getItemMeta().isUnbreakable()) { //if its unbreakable (making sure its the right one)
										if (clickstack.getItemMeta().getDisplayName().contains("Sword")) {
											switch (cursorstack) {
												case DIAMOND_SWORD:
												case IRON_SWORD:
												case STONE_SWORD:
												case WOODEN_SWORD:
												case GOLDEN_SWORD:
													inv.setItem(0, player.getItemOnCursor());
													player.setItemOnCursor(null);
													break;
												default:
													break;
											}
										} else if (clickstack.getItemMeta().getDisplayName().contains("Pickaxe")) {
											switch (cursorstack) {
												case DIAMOND_PICKAXE:
												case IRON_PICKAXE:
												case STONE_PICKAXE:
												case WOODEN_PICKAXE:
												case GOLDEN_PICKAXE:
													inv.setItem(1, player.getItemOnCursor());
													player.setItemOnCursor(null);
													break;
												default:
													break;
											}
										} else if (clickstack.getItemMeta().getDisplayName().contains("Axe")) {
											switch (cursorstack) {
												case DIAMOND_AXE:
												case IRON_AXE:
												case STONE_AXE:
												case WOODEN_AXE:
												case GOLDEN_AXE:
													inv.setItem(2, player.getItemOnCursor());
													player.setItemOnCursor(null);
													break;
												default:
													break;
											}
										} else if (clickstack.getItemMeta().getDisplayName().contains("Shovel")) {
											switch (cursorstack) {
												case DIAMOND_SHOVEL:
												case IRON_SHOVEL:
												case STONE_SHOVEL:
												case WOODEN_SHOVEL:
												case GOLDEN_SHOVEL:
													inv.setItem(3, player.getItemOnCursor());
													player.setItemOnCursor(null);
													break;
												default:
													break;
											}
										}
									}
								}
								event.setCancelled(true);
							}
						} else {
							if (event.getCurrentItem() != null) {
								ItemStack clickstack = event.getCurrentItem().clone();
								boolean removemt = false;
								switch (clickstack.getType()) {
									case DIAMOND_SWORD:
									case IRON_SWORD:
									case STONE_SWORD:
									case WOODEN_SWORD:
									case GOLDEN_SWORD:
										inv.setItem(0, main.placeholders.get(0));
										player.setItemOnCursor(clickstack);
										removemt = true;
										break;
									case DIAMOND_PICKAXE:
									case IRON_PICKAXE:
									case STONE_PICKAXE:
									case WOODEN_PICKAXE:
									case GOLDEN_PICKAXE:
										inv.setItem(1, main.placeholders.get(1));
										player.setItemOnCursor(clickstack);
										removemt = true;
										break;
									case DIAMOND_AXE:
									case IRON_AXE:
									case STONE_AXE:
									case WOODEN_AXE:
									case GOLDEN_AXE:
										inv.setItem(2, main.placeholders.get(2));
										player.setItemOnCursor(clickstack);
										removemt = true;
										break;
									case DIAMOND_SHOVEL:
									case IRON_SHOVEL:
									case STONE_SHOVEL:
									case WOODEN_SHOVEL:
									case GOLDEN_SHOVEL:
										inv.setItem(3, main.placeholders.get(3));
										player.setItemOnCursor(clickstack);
										removemt = true;
										break;
									case FEATHER:

										boolean forloop = false;
										ItemStack genstack = null;
										for (int i = 0; i < 5; i++) { //this loops through the mt inv, and gives the player the first multitool that shows up
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
										if (!forloop) {
											player.sendMessage(main.prefix + ChatColor.RED + "The Multitool is empty!");
										} else {
											Inventory plrinv = player.getInventory();
											boolean hasitem = false;
											for (ItemStack i : plrinv) {
												if (i != null) {
													if (i.getItemMeta() != null) {
														if (main.isTool(i)) {
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
										break;
									default:
										break;
								}
								if (removemt) {
									Inventory plrinv = player.getInventory(); //this removes the multitool from the player's inventory if a tool is removed from the list
									for (ItemStack i : plrinv) {
										if (i != null) {
											if (i.getItemMeta() != null) {
												if (main.isTool(i)) {
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
