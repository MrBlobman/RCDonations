package me.MrBlobman.RCDonations.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
	
	private ItemStack item = new ItemStack(Material.AIR);
	
	public ItemBuilder(){ }

	public ItemBuilder setMaterial(Material mat){
		this.item.setType(mat);
		return this;
	}
	
	public ItemBuilder setData(Short durability){
		this.item.setDurability(durability);
		return this;
	}
	
	public ItemBuilder setAmount(int amount){
		this.item.setAmount(amount);
		return this;
	}
	
	public ItemBuilder setName(String name){
		ItemMeta meta = this.item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		this.item.setItemMeta(meta);
		return this;
	}
	
	public ItemBuilder setLore(String... lore){
		ItemMeta meta = this.item.getItemMeta();
		List<String> loreList = new ArrayList<String>();
		for (String string : lore){
			loreList.add(ChatColor.translateAlternateColorCodes('&', string));
		}
		meta.setLore(loreList);
		this.item.setItemMeta(meta);
		return this;
	}
	
	public ItemBuilder addEnchantment(Enchantment ench, int level){
		this.item.addUnsafeEnchantment(ench, level);
		return this;
	}
	
	public ItemStack build(){
		return this.item;
	}
}
