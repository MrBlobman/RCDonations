package me.MrBlobman.RCDonations.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.MrBlobman.RCDonations.RCDonations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemParser {
	
	public static ItemStack parseItem(ConfigurationSection itemSection){
		try{
			if (!itemSection.contains("Material")){
				RCDonations.plugin.getLogger().warning(itemSection.getCurrentPath() + " is missing a material. Skipping...");
				return null;
			}
			ItemStack item = null;
			try{
				item = new ItemStack(Material.getMaterial(itemSection.getString("Material")), 1);
			}catch(IllegalArgumentException e){
				RCDonations.plugin.getLogger().warning(itemSection.getCurrentPath() + " material is invalid. Skipping...");
				return null;
			}
			ItemMeta meta = item.getItemMeta();
			if (itemSection.contains("Amount")){
				item.setAmount(itemSection.getInt("Amount"));
			}
			if (itemSection.contains("DataValue")){
				item.setDurability((short) itemSection.getInt("DataValue"));
			}
			if (itemSection.contains("Name")){
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemSection.getString("Name")));
			}
			if (itemSection.contains("Lore")){
				List<String> lore = itemSection.getStringList("Lore");
				for (int i = 0; i < lore.size(); i++){
					lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
				}
				meta.setLore(lore);
			}
			if (itemSection.contains("Enchantments")){
				Map<String, Object> enchSection = itemSection.getConfigurationSection("Enchantments").getValues(false);
				Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
				if (!enchSection.isEmpty()){
					for (String ench : enchSection.keySet()){
						try{
							enchantments.put(Enchantment.getByName(ench), (Integer) enchSection.get(ench));
						}catch(ClassCastException e){
							RCDonations.plugin.getLogger().warning(itemSection.getCurrentPath() + " enchantment level is not an integer. " + e.getMessage() + ". Skipping...");
						}catch(IllegalArgumentException e){
							RCDonations.plugin.getLogger().warning(itemSection.getCurrentPath() + " enchantment is not a real enchantment. " + e.getMessage() + ". Skipping...");
							return null;
						}
					}
					item.addUnsafeEnchantments(enchantments);
				}
			}
			item.setItemMeta(meta);
			return item;
		}catch (ClassCastException e){
			RCDonations.plugin.getLogger().warning(itemSection.getCurrentPath() + " is incorrectly defined. " + e.getMessage() + ". Skipping...");
			return null;
		}catch (Exception e){
			RCDonations.plugin.getLogger().warning(itemSection.getCurrentPath() + " is incorrectly defined. Unsure what specifically went wrong. Skipping...");
			return null;
		}
	}
	
	public static ConfigurationSection buildConfigSection(ItemStack item){
		ConfigurationSection section = new YamlConfiguration();
		section.set("Material", item.getType().toString());
		section.set("Amount", item.getAmount());
		if (item.getDurability() != (short) 0){
			section.set("DataValue", item.getDurability());
		}
		if (item.hasItemMeta()){
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName()){
				section.set("Name", meta.getDisplayName());
			}
			if (meta.hasLore()){
				section.set("Lore", meta.getLore());
			}
			if (meta.hasEnchants()){
				Map<Enchantment, Integer> enchantments = meta.getEnchants();
				for (Enchantment ench : enchantments.keySet()){
					section.set("Enchantments."+ench.getName(), enchantments.get(ench));
				}
			}
		}
		return section;
	}
}
