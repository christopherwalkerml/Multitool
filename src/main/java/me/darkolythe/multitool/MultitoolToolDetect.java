package me.darkolythe.multitool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MultitoolToolDetect implements Listener {
	
	private Multitool main;
	
	public MultitoolToolDetect(Multitool plugin) {
		this.main = plugin; // set it equal to an instance of main
	}
	
	private ToolMap map = new ToolMap();

	private int getToolType(Material material) {
		String mat = material.toString();

		if (map.map.containsKey(mat)) {
			return map.map.get(mat);
		}

		return 6;
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (event.getEntity() instanceof LivingEntity) {
				setItem(player, null, true);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		Action action = event.getAction();
		
		if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();

			if (action.equals(Action.LEFT_CLICK_AIR) || block.getType().toString().contains("BAMBOO") || block.getType() == Material.COBWEB) {
				setItem(player, block, true);
				return;
			}
			setItem(player, block, false);
		}
	}

	private void setItem(Player player, Block block, boolean isSword) {
		if (player.hasPermission("multitool.use")) { //If the player has permission, continue

			if (main.multitoolutils.getToggle(player.getUniqueId())) {
				//Get item in player"s hand
				ItemStack handitem = player.getInventory().getItemInMainHand();

				//Get the meta values of the item in their hand
				ItemMeta handmeta = handitem.getItemMeta();

				if (handitem.getType() != Material.AIR && handitem.getAmount() > 0) {
					if (isItem(handmeta, player)) {
						ItemStack givestack = new ItemStack(Material.AIR, 0);
						setMTItem(player, true);
						if (isSword) { //if the block is air, make it a sword, else, continue
							giveSword(player);
						} else {
							//if the air was not clicked, continue down the checklist
							//Check what material it is, and change the tool
							if (block != null) {
								Material blocktype = block.getType();

								if (blocktype != main.multitoolutils.getLastBlock(player.getUniqueId())) {

									int tooltype = getToolType(blocktype);

									if (tooltype != 6) {
										if (main.multitoolutils.getToolInv(player).getItem(tooltype) != null) {
											if (main.multitoolutils.getToolInv(player).getItem(tooltype).getType() != Material.GRAY_STAINED_GLASS_PANE) {

												givestack = main.multitoolutils.getToolInv(player).getItem(tooltype).clone();
												main.lastblock.put(player.getUniqueId(), blocktype); //change the last block hit, if the tool was able to be changed
												giveStack(givestack, player);
												return;
											}
										}
									}
								} else {
									givestack.setType(Material.AIR);
									giveStack(givestack, player);
								}
							}
						}
					}
				}
			} else {
				//Get item in player"s hand
				ItemStack handitem = player.getInventory().getItemInMainHand();

				//Get the meta values of the item in their hand
				ItemMeta handmeta = handitem.getItemMeta();
				if (isItem(handmeta, player)) {
					setMTItem(player, false);
				}
			}
		}
	}

	public boolean giveSword(Player player) {
		if (main.multitoolutils.getToolInv(player).getItem(0) != null) {
			if (main.multitoolutils.getToolInv(player).getItem(0).getType() != Material.GRAY_STAINED_GLASS_PANE) {
				if (Material.AIR != main.multitoolutils.getLastBlock(player.getUniqueId())) {

					ItemStack givestack = main.multitoolutils.getToolInv(player).getItem(0).clone();
					main.lastblock.put(player.getUniqueId(), Material.AIR);
					giveStack(givestack, player);
					return true;
				}
			}
		}
		return false;
	}
	
	public void giveStack(ItemStack givestack, Player player) {
		if (givestack.getType() != Material.AIR) { //if the block being hit changed, update the held item
			ItemMeta givemeta = givestack.getItemMeta();
			givemeta.setLore(main.multitoolinventory.addLore(givemeta, main.toollore, false));
			givestack.setItemMeta(givemeta);
			player.getInventory().setItemInMainHand(givestack);
		}
	}

	public boolean isItem(ItemMeta handmeta, Player player) {
		if (handmeta != null) {
			if (handmeta.hasLore()) {
				for (String l : handmeta.getLore()) {
					if (l.equals(main.toollore)) {
						if (main.toolinv.containsKey(player.getUniqueId())) { //if the player's mt inv exists
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void setMTItem(Player player, boolean changeitem) {
		for (int i = 0; i < 5; i++) { //this loops through the mt inv, checks which index the current item being used is in, and then updates it
			if (main.multitoolutils.getToolInv(player).getItem(i) != null) {
				if (main.multitoolutils.getToolInv(player).getItem(i).getType() == player.getInventory().getItemInMainHand().getType()) {
					Inventory mtinv = main.multitoolutils.getToolInv(player); //create inventory of mtinv
					ItemStack handstack = player.getInventory().getItemInMainHand().clone();
					ItemMeta stackmeta = handstack.getItemMeta();
					List<String> lore = new ArrayList<>();
					for (String line : stackmeta.getLore()) {
						if (!line.equals(main.toollore)) {
							lore.add(line);
						}
					}
					stackmeta.setLore(lore);
					handstack.setItemMeta(stackmeta);
					mtinv.setItem(i, handstack); //replace old item with used item
					if (changeitem) {
						main.toolinv.put(player.getUniqueId(), mtinv); //replace old inv with new inv
					}
				}
			}
		}
	}
}
