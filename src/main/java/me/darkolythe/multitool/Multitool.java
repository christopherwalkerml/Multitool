package me.darkolythe.multitool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Multitool extends JavaPlugin implements Listener {

	public HashMap<UUID, Inventory> toolinv = new HashMap<>();
	public List<ItemStack> placeholders = new ArrayList<>();
	public HashMap<UUID, Boolean> toggle = new HashMap<>();
	public HashMap<UUID, Material> lastblock = new HashMap<>();
	public String prefix = new String(ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.BLUE.toString() + "Multitool" + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "] ");
	public String toollore = ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "Multitool";

	public boolean dropondeath;

	public static Multitool plugin;
	public MultitoolInventory multitoolinventory;
	public MultitoolEvents multitoolevents;
	public MultitoolToolDetect multitooltooldetect;
	
	public void onEnable() {///////////////////////////////////////////////////////////////////////////////////////////////Enable Disable
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
		
		multitoolevents = new MultitoolEvents(plugin);
		multitoolinventory = new MultitoolInventory(plugin);
		multitooltooldetect = new MultitoolToolDetect(plugin);
		getCommand("multitool").setExecutor(new MultitoolCommand());

		saveDefaultConfig();

		dropondeath = getConfig().getBoolean("droptoolsondeath");

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(multitoolevents, this);
		getServer().getPluginManager().registerEvents(multitoolinventory, this);
		getServer().getPluginManager().registerEvents(multitooltooldetect, this);


		String[] names = new String[]{ChatColor.GREEN + "Put Sword Here", ChatColor.GREEN + "Put Pickaxe Here", ChatColor.GREEN + "Put Axe Here",
				ChatColor.GREEN + "Put Shovel Here", ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Multitool"};
		String lore = ChatColor.AQUA + "Click this feather to generate your Multitool.";

		for (int i = 0; i < 5; i++) {
			ItemStack ph = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1); //create gray stained glass
			ItemMeta phmet = ph.getItemMeta();
			phmet.setUnbreakable(true); //make them unbreakable so that players cant replicate them in their inventory
			phmet.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			phmet.setDisplayName(names[i]); //give them their display names
			if (i == 4) {
				ph.setType(Material.FEATHER); //if the item is a feather, give it lores
				phmet.setLore(multitoolinventory.addLore(phmet, lore, true));
			}
			ph.setItemMeta(phmet);
			placeholders.add(ph); //add all the items to a list with place holder glass panes
		}

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			playerLoad(player); //load all the players on the server (on boot, there will be none, on reload, this is necessary)
		}

		System.out.println(prefix + ChatColor.GREEN + "Diverse Multitool enabled!");
	}
	
	public void onDisable() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			playerSave(player); //this saves all the player mt inv information if the server is reloading
		}

		saveDefaultConfig();
		
		System.out.println(prefix + ChatColor.RED + "Diverse Multitool disabled!");
	}
	
	
	public static Multitool getInstance() {
		return plugin;
	}
	
	
	@EventHandler//////////////////////////////////////////////////////////////////////////////////////////////////////////Player leave and join
	public void onPlayerJoin(PlayerJoinEvent event) {
		playerLoad(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		playerSave(event.getPlayer());
	}
	
	
	private void playerLoad(Player player) {
		
		Inventory inv = Bukkit.getServer().createInventory(player, InventoryType.HOPPER, ChatColor.GREEN + "Multitools"); //create the mv inv
		
		if (getConfig().contains("toolinv." + player.getUniqueId())) {
			int index = 0;
			for (String item : getConfig().getConfigurationSection("toolinv." + player.getUniqueId()).getKeys(false)) { //load all the itemstacks from config.yml
				if (getConfig().getConfigurationSection("toolinv." + player.getUniqueId() + "." + item) != null) {
					inv.setItem(index , loadItem(getConfig().getConfigurationSection("toolinv." + player.getUniqueId() + "." + item)));
				}
				if (inv.getItem(index) == null) { //if air is in the inventory, put the glass panes as placeholders
					inv.setItem(index, placeholders.get(index));
				}
				index += 1;
			}
		} else {
			for (int index = 0; index < 5; index++) {
				inv.setItem(index, placeholders.get(index)); //if the player data is empty, set placeholders until the inv is saved
			}
		}
		lastblock.put(player.getUniqueId(), null); //set the default value for last block hit upon player join
		toolinv.put(player.getUniqueId(), inv);
	}
	
	
	public void playerSave(Player player) {
		if (!getConfig().contains("toolinv." + player.getUniqueId())) {
			getConfig().createSection("toolinv." + player.getUniqueId()); //if the player's mt inv doesnt exist in config.yml ,create it
		}
		
		getConfig().set("toolinv." + player.getUniqueId(), null);
		
		char c = 'a';
		if (toolinv.containsKey(player.getUniqueId())) {
			for (ItemStack itemstack : toolinv.get(player.getUniqueId())) { //save the player's mt inventory
				if (itemstack != null) {
					saveItem(getConfig().createSection("toolinv." + player.getUniqueId() + "." + c++), itemstack);
				} else {
					ItemStack airstack = new ItemStack(Material.AIR, 0);
					saveItem(getConfig().createSection("toolinv." + player.getUniqueId() + "." + c++), airstack); //if there's nothing in a slot, save it as air
				}
			}
		}
		saveConfig();
	}

	
	
	
	
	private void saveItem(ConfigurationSection section, ItemStack itemstack) {/////////////////////////////////////////////Save Load Inv
		section.set("itemstack", itemstack);
	}
	
	private ItemStack loadItem(ConfigurationSection section) {
		ItemStack itemstack = new ItemStack(section.getItemStack("itemstack"));
		
		return itemstack;
	}
	
	
	
	
	public Boolean getToggle(UUID uuid) {
		if (!toggle.containsKey(uuid)) {
			toggle.put(uuid, true);
		}
		return toggle.get(uuid);
	}
	
	public void setToggle(UUID uuid, Boolean bool) {
		toggle.put(uuid, bool);
	}
	
	public Material getLastBlock(UUID uuid) {
		if (!lastblock.containsKey(uuid)) {
			lastblock.put(uuid, null);
		}
		return lastblock.get(uuid);
	}
	
	public Inventory getToolInv(Player player) {
		if (!toolinv.containsKey(player.getUniqueId())) {
			Inventory inv = Bukkit.getServer().createInventory(player, InventoryType.HOPPER, ChatColor.GREEN + "Multitools"); //create the mv inv
			for (int index = 0; index < 5; index++) {
				inv.setItem(index, placeholders.get(index)); //if the player data is empty, set placeholders until the inv is saved
			}
			toolinv.put(player.getUniqueId(), inv);
		}
		return toolinv.get(player.getUniqueId());
	}

	public boolean isTool(ItemStack item) {
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null && meta.hasLore()) {
				for (String line : meta.getLore()) {
					if (line.equals(toollore)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
