package me.darkolythe.multitool;

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

import java.util.UUID;

public class MultitoolUtils implements Listener {

    private Multitool main;

    public MultitoolUtils(Multitool plugin) {
        this.main = plugin; // set it equal to an instance of main
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerLoad(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        playerSave(event.getPlayer());
    }


    public void playerLoad(Player player) {

        Inventory inv = Bukkit.getServer().createInventory(player, InventoryType.HOPPER, ChatColor.BLUE + "Multitools"); //create the mv inv

        if (main.getConfig().contains("toolinv." + player.getUniqueId())) {
            int index = 0;
            for (String item : main.getConfig().getConfigurationSection("toolinv." + player.getUniqueId()).getKeys(false)) { //load all the itemstacks from config.yml
                if (main.getConfig().getConfigurationSection("toolinv." + player.getUniqueId() + "." + item) != null) {
                    inv.setItem(index , loadItem(main.getConfig().getConfigurationSection("toolinv." + player.getUniqueId() + "." + item)));
                }
                if (inv.getItem(index) == null) { //if air is in the inventory, put the glass panes as placeholders
                    inv.setItem(index, main.placeholders.get(index));
                }
                index += 1;
            }
        } else {
            for (int index = 0; index < 5; index++) {
                inv.setItem(index, main.placeholders.get(index)); //if the player data is empty, set placeholders until the inv is saved
            }
        }
        main.lastblock.put(player.getUniqueId(), null); //set the default value for last block hit upon player join
        main.toolinv.put(player.getUniqueId(), inv);
    }


    public void playerSave(Player player) {
        if (!main.getConfig().contains("toolinv." + player.getUniqueId())) {
            main.getConfig().createSection("toolinv." + player.getUniqueId()); //if the player's mt inv doesnt exist in config.yml ,create it
        }

        main.getConfig().set("toolinv." + player.getUniqueId(), null);

        char c = 'a';
        if (main.toolinv.containsKey(player.getUniqueId())) {
            for (ItemStack itemstack : main.toolinv.get(player.getUniqueId())) { //save the player's mt inventory
                if (itemstack != null) {
                    saveItem(main.getConfig().createSection("toolinv." + player.getUniqueId() + "." + c++), itemstack);
                } else {
                    ItemStack airstack = new ItemStack(Material.AIR, 0);
                    saveItem(main.getConfig().createSection("toolinv." + player.getUniqueId() + "." + c++), airstack); //if there's nothing in a slot, save it as air
                }
            }
        }
        main.saveConfig();
    }


    private void saveItem(ConfigurationSection section, ItemStack itemstack) {/////////////////////////////////////////////Save Load Inv
        section.set("itemstack", itemstack);
    }

    private ItemStack loadItem(ConfigurationSection section) {
        return new ItemStack(section.getItemStack("itemstack"));
    }

    public void reload() {
        main.dropondeath = main.getConfig().getBoolean("droptoolsondeath");
    }




    public Boolean getToggle(UUID uuid) {
        if (!main.toggle.containsKey(uuid)) {
            main.toggle.put(uuid, true);
        }
        return main.toggle.get(uuid);
    }

    public void setToggle(UUID uuid, Boolean bool) {
        main.toggle.put(uuid, bool);
    }

    public Material getLastBlock(UUID uuid) {
        if (!main.lastblock.containsKey(uuid)) {
            main.lastblock.put(uuid, null);
        }
        return main.lastblock.get(uuid);
    }

    public Inventory getToolInv(Player player) {
        if (!main.toolinv.containsKey(player.getUniqueId())) {
            Inventory inv = Bukkit.getServer().createInventory(player, InventoryType.HOPPER, ChatColor.BLUE + "Multitools"); //create the mv inv
            for (int index = 0; index < 5; index++) {
                inv.setItem(index, main.placeholders.get(index)); //if the player data is empty, set placeholders until the inv is saved
            }
            main.toolinv.put(player.getUniqueId(), inv);
        }
        return main.toolinv.get(player.getUniqueId());
    }

    public boolean isTool(ItemStack item) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                for (String line : meta.getLore()) {
                    if (line.equals(main.toollore)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addPlaceholders() {
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
                phmet.setLore(main.multitoolinventory.addLore(phmet, lore, true));
            }
            ph.setItemMeta(phmet);
            main.placeholders.add(ph); //add all the items to a list with place holder glass panes
        }
    }

}
