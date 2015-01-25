package me.MrBlobman.RCDonations.Items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface KitItem {
	
	//Items in config will be 
	//Items:
	//  '1':
	//     Type: KitItemImplementation
	//     Item: This section is up to the implementation of the type above
	public ItemStack getItem();
	public ConfigurationSection asWriteable();
	public KitItem readItem(ConfigurationSection itemSection);
	
}
