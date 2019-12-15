package me.darkolythe.multitool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Multitool extends JavaPlugin implements Listener {

	public HashMap<UUID, Inventory> toolinv = new HashMap<>();
	public List<ItemStack> placeholders = new ArrayList<>();
	public HashMap<UUID, Boolean> toggle = new HashMap<>();
	public HashMap<UUID, Material> lastblock = new HashMap<>();
	public String prefix = ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.BLUE.toString() + "Multitool" + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "] ";
	public String toollore = ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "Multitool";

	public boolean dropondeath;

	public static Multitool plugin;
	public MultitoolInventory multitoolinventory;
	public MultitoolEvents multitoolevents;
	public MultitoolToolDetect multitooltooldetect;
	public MultitoolUtils multitoolutils;
	
	public void onEnable() {///////////////////////////////////////////////////////////////////////////////////////////////Enable Disable
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
		
		multitoolevents = new MultitoolEvents(plugin);
		multitoolinventory = new MultitoolInventory(plugin);
		multitooltooldetect = new MultitoolToolDetect(plugin);
		multitoolutils = new MultitoolUtils(plugin);
		getCommand("multitool").setExecutor(new MultitoolCommand());

		saveDefaultConfig();

		dropondeath = getConfig().getBoolean("droptoolsondeath");

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(multitoolevents, this);
		getServer().getPluginManager().registerEvents(multitoolinventory, this);
		getServer().getPluginManager().registerEvents(multitooltooldetect, this);
		getServer().getPluginManager().registerEvents(multitoolutils, this);

		multitoolutils.addPlaceholders();

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			multitoolutils.playerLoad(player); //load all the players on the server (on boot, there will be none, on reload, this is necessary)
		}

		Metrics metrics = new Metrics(plugin);

		System.out.println(prefix + ChatColor.GREEN + "Multitool Plus enabled!");
	}
	
	public void onDisable() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			multitoolutils.playerSave(player); //this saves all the player mt inv information if the server is reloading
		}

		saveDefaultConfig();
		
		System.out.println(prefix + ChatColor.RED + "Diverse Multitool disabled!");
	}
	
	
	public static Multitool getInstance() {
		return plugin;
	}
}
