package me.darkolythe.multitool;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MultitoolEvents implements Listener {

	private Multitool main;
	public MultitoolEvents(Multitool plugin) {
		this.main = plugin; // set it equal to an instance of main
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Make sure player isnt removing MT from inv
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Item dropitem = event.getItemDrop();
		ItemStack dropstack = dropitem.getItemStack();
		if (main.multitoolutils.isTool(dropstack)) {
			dropitem.remove();
			event.getPlayer().sendMessage(main.prefix + ChatColor.RED + "You dropped your Multitool!");
		}
	}
	
	@EventHandler
	public void onItemBreak(PlayerItemBreakEvent event) {
		ItemStack brokenitem = event.getBrokenItem();
		if (main.multitoolutils.isTool(brokenitem)) {
			Inventory mtinv = main.multitoolutils.getToolInv(event.getPlayer()); //create inventory of mtinv
			for (int i = 0; i < 4; i++) {
				if (mtinv.getItem(i).getType() == brokenitem.getType()) {
					mtinv.setItem(i, main.placeholders.get(i));
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryCheck(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if ((event.getClickedInventory() != player.getInventory()) || (event.isShiftClick())) {
			if ((player.getItemOnCursor().getType() != Material.AIR)) {
				ItemStack cursorstack = player.getItemOnCursor();
				if (main.multitoolutils.isTool(cursorstack)) {
					player.setItemOnCursor(null);
					event.setCancelled(true);
					player.sendMessage(main.prefix + ChatColor.RED + "You removed your Multitool!");
				}
			} else if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				ItemStack clickstack = event.getCurrentItem();
				if (main.multitoolutils.isTool(clickstack)) {
					event.setCurrentItem(null);
					event.setCancelled(true);
					player.sendMessage(main.prefix + ChatColor.RED + "You removed your Multitool!");
				}
			} else if (event.getClick() == ClickType.NUMBER_KEY) {
				ItemStack item = player.getInventory().getItem(event.getHotbarButton());
				if (item != null && item.getType() != Material.AIR) {
					if (main.multitoolutils.isTool(item)) {
						player.getInventory().setItem(event.getHotbarButton(), null);
						event.setCancelled(true);
						player.sendMessage(main.prefix + ChatColor.RED + "You removed your Multitool!");
					}
				}
			} else if (main.multitoolutils.isTool(event.getCurrentItem())) {
				event.setCurrentItem(null);
				event.setCancelled(true);
				player.sendMessage(main.prefix + ChatColor.RED + "Your Multitool was outside your inventory. It has been removed!");
			}
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getOldCursor().getType() != Material.AIR) {
			if (event.getInventory().getType() != InventoryType.PLAYER) {
				ItemStack clickstack = event.getOldCursor();
				if (main.multitoolutils.isTool(clickstack)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (main.dropondeath) {
			if (main.toolinv.containsKey(event.getEntity().getUniqueId())) {
				if (!event.getKeepInventory()) {
					for (ItemStack i : main.toolinv.get(event.getEntity().getUniqueId()).getContents()) {
						if (i.getType() != Material.FEATHER && i.getType() != Material.GRAY_STAINED_GLASS_PANE) {
							event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), i);
						}
					}
					main.toolinv.remove(event.getEntity().getUniqueId());
					main.multitoolutils.getToolInv(event.getEntity());
				}
			}
		}
		List<ItemStack> drops = event.getDrops();
		for (ItemStack i : drops) {
			ItemMeta dropmeta = i.getItemMeta();
			if (dropmeta != null) {
				if (main.multitoolutils.isTool(i)) {
					i.setType(Material.AIR);
					if (!main.dropondeath) {
						event.getEntity().sendMessage(main.prefix + ChatColor.RED + "You died, so your Multitool was put away!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		ItemStack handitem = player.getInventory().getItemInMainHand();
		if (main.multitoolutils.isTool(handitem)) {
			Entity ent = event.getRightClicked();
			if (ent.getType() == EntityType.ITEM_FRAME) {
				if (((ItemFrame)ent).getItem().getType() == Material.AIR) {
					event.setCancelled(true);
					player.sendMessage(main.prefix + ChatColor.RED + "You cannot give your multitool to item frames!");
				}
			} else if (ent.getType() == EntityType.ARMOR_STAND) {
				if (((ArmorStand)ent).getItemInHand().getType() == Material.AIR) {
					event.setCancelled(true);
					player.sendMessage(main.prefix + ChatColor.RED + "You cannot give your multitool to armour stands!");
				}
			}
		}
	}
}
