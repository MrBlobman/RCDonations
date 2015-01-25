package me.MrBlobman.RCDonations.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.MrBlobman.RCDonations.Utils.ItemParser;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class RandomItem implements KitItem{

	Random r = new Random();
	HashMap<ItemStack, Integer> itemAmountInBag = new HashMap<ItemStack, Integer>();
	ArrayList<ItemStack> grabBag = new ArrayList<ItemStack>();
	
	public RandomItem(){ }
	
	/**
	 * @return an ItemStack to be given to the player which if empty is Material.AIR
	 */
	@Override
	public ItemStack getItem() {
		if (grabBag.isEmpty()){
			return new ItemStack(Material.AIR);
		}
		return grabBag.get(r.nextInt(grabBag.size()));
	}
	
	/**
	 * 
	 * @param item = the item to add to the bag
	 * @param amt = the amount of times to add this item
	 * 
	 * NOTE: although more accurate adding large amount of items decreases the efficiency of this structure
	 */
	public void addItem(ItemStack item, Integer amt){
		for (int i = 0; i < amt; i++){
			grabBag.add(item);
		}
		itemAmountInBag.put(item, amt);
	}

	@Override
	public ConfigurationSection asWriteable() {
		ConfigurationSection section = new YamlConfiguration();
		for (ItemStack item : itemAmountInBag.keySet()){
			if (item != null){
				String key = String.valueOf(itemAmountInBag.get(item));
				while (section.contains(key)){
					key = "0"+key;
				}
				section.set(key, ItemParser.buildConfigSection(item));
			}
		}
		return section;
	}

	@Override
	public RandomItem readItem(ConfigurationSection itemSection) {
		for (String key : itemSection.getKeys(false)){
			this.addItem(ItemParser.parseItem(itemSection.getConfigurationSection(key)), itemSection.getInt("AmountInBag"));
		}
		return this;
	}
	
	
}
