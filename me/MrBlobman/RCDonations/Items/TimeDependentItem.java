package me.MrBlobman.RCDonations.Items;

import java.util.HashMap;
import java.util.Map;

import me.MrBlobman.RCDonations.Utils.DateUtils;
import me.MrBlobman.RCDonations.Utils.Day;
import me.MrBlobman.RCDonations.Utils.ItemParser;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class TimeDependentItem implements KitItem {
	
	Map<Day, ItemStack> week = new HashMap<Day, ItemStack>();
	
	public TimeDependentItem(){ }
	
	/**
	 * @return returns the itemstack corresponding to todays day of the week or itemstack of type Material.AIR
	 */
	@Override
	public ItemStack getItem() {
		Day day = DateUtils.getDay();
		if (week.containsKey(day)){
			return week.get(day);
		}else{
			return new ItemStack(Material.AIR);
		}
	}

	public void addItem(Day day, ItemStack item){
		this.week.put(day, item);
	}
	
	@Override
	public ConfigurationSection asWriteable() {
		ConfigurationSection section = new YamlConfiguration();
		for (Day day : week.keySet()){
			section.set(day.toString(), ItemParser.buildConfigSection(week.get(day)));
		}
		return section;
	}

	@Override
	public KitItem readItem(ConfigurationSection itemSection) {
		for (String day : itemSection.getKeys(false)){
			this.addItem(Day.valueOf(day), ItemParser.parseItem(itemSection.getConfigurationSection(day)));
		}return this;
	}

}
