package me.MrBlobman.RCDonations.Items;

import java.io.File;
import java.io.IOException;

import me.MrBlobman.RCDonations.RCDonations;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.drtshock.playervaults.vaultmanagement.Serialization;

public class KitInstance {
	
	private Long cooldownEnds;
	private ItemStack[] untakenItems;
	
	KitInstance(){ }
	
	KitInstance(Kit kit){
		this.cooldownEnds = System.currentTimeMillis()+kit.getCooldown();
		KitItem[] kitItems = kit.getItems();
		this.setUntakenItems(new ItemStack[kitItems.length]);
		for (int i = 0; i < kitItems.length; i++){
			if (kitItems[i] == null){
				continue;
			}
			untakenItems[i] = kitItems[i].getItem();
		}
	}
	
	public Long getTimeRemaining(){
		return this.cooldownEnds-System.currentTimeMillis();
	}

	public ItemStack[] getUntakenItems() {
		return untakenItems;
	}

	public void setUntakenItems(ItemStack[] untakenItems) {
		this.untakenItems = untakenItems;
	}
	
	public void writeOutItemsLeft(File kitFile, String key){
		FileConfiguration config = YamlConfiguration.loadConfiguration(kitFile);
		config.set(key+".CooldownEnds", cooldownEnds);
		config.set(key+".Items", Serialization.toString(untakenItems));
		try {
			config.save(kitFile);
		} catch (IOException e) {
			RCDonations.plugin.getLogger().severe(RCDonations.prefix + "Could not save the kit instance at "+key+" in "+kitFile.getPath());
		}
	}
	
	public KitInstance readItemsLeft(FileConfiguration config, String key){
		ItemStack[] itemsLeft = Serialization.toItemStackArray(config.getStringList(key+".Items"));
		this.setUntakenItems(itemsLeft);
		this.cooldownEnds = config.getLong(key+".CooldownEnds");
		return this;
	}
}
