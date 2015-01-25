package me.MrBlobman.RCDonations.PotionEffects;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PotionIcon implements ConfigurationSerializable{
	private PotionInfo info;
	private ItemStack item;
	private String permission;
	
	public PotionIcon(PotionInfo info, ItemStack item, String permission) {
		this.info = info;
		this.item = item;
		this.permission = permission;
	}

	/**
	 * Deserialization
	 * @param map the config section as a map
	 */
	public PotionIcon(Map<String, Object> map){
		Object icon = map.get("Icon");
		if (icon instanceof ItemStack){
			this.item = (ItemStack) icon;
		}else{
			this.item = new ItemStack(Material.STONE, 17);
		}
		String effect = (String) (map.get("PotionEffect") instanceof String ? map.get("PotionEffect") : "SATURATION");
		Integer strength;
		try{
			strength = Integer.valueOf(map.get("Strength").toString());
		}catch (NumberFormatException e){
			strength = new Integer(1);
		}
		Boolean ambient = Boolean.valueOf(map.get("Ambient").toString());
		this.info = new PotionInfo(PotionEffectType.getByName(effect), strength, ambient);
		Object permission = map.get("Permission");
		if (permission instanceof String){
			this.permission = (String) permission;
		}else{
			this.permission = "no.permission.given";
		}
	}
	
	public PotionInfo getInfo() {
		return info;
	}

	public void setInfo(PotionInfo info) {
		this.info = info;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("Icon", item);
		map.put("PotionEffect", info.getEffect().getName());
		map.put("Strength", info.getStrength());
		map.put("Ambient", info.isAmbient());
		map.put("Permission", permission);
		return map;
	}
	
}
