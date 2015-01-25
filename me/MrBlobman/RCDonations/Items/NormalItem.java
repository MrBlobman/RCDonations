package me.MrBlobman.RCDonations.Items;

import me.MrBlobman.RCDonations.Utils.ItemParser;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class NormalItem implements KitItem{

	private ItemStack item;
	
	public NormalItem(){ }
	
	/**
	 * @return itemstack represented by this NormalItem or itemstack of type Material.AIR if null
	 */
	@Override
	public ItemStack getItem() {
		if (this.item == null){
			return new ItemStack(Material.AIR);
		}else{
			return this.item;
		}
	}

	@Override
	public KitItem readItem(ConfigurationSection itemSection) {
		this.item = ItemParser.parseItem(itemSection);
		return this;
	}

	@Override
	public ConfigurationSection asWriteable() {
		return ItemParser.buildConfigSection(item);
	}

}
