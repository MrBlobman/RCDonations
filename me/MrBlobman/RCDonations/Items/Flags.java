package me.MrBlobman.RCDonations.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Flags {
	
	RANDOM_ITEM(ChatColor.YELLOW+""+ChatColor.BOLD+"Random Item"), TIME_DEPENDENT_ITEM(ChatColor.YELLOW+""+ChatColor.BOLD+"Time Dependent Item");
	
	public final ItemStack FLAG;
	
	Flags(String name){
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		this.FLAG = item;
	}
}
