package me.MrBlobman.RCDonations.Particles;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class ParticleIcon implements ConfigurationSerializable{
	private ItemStack item;
	private Particle particle;
	private String permission;
	
	public ParticleIcon(ItemStack item, Particle particle, String permission){
		this.item = item;
		this.particle = particle;
		this.permission = permission;
	}
	
	/**
	 * Deserialization
	 * @param map the config section as a map
	 */
	public ParticleIcon(Map<String, Object> map){
		Object icon = map.get("Icon");
		if (icon instanceof ItemStack){
			this.item = (ItemStack) icon;
		}else{
			this.item = new ItemStack(Material.STONE, 17);
		}
		Object particle = map.get("Particle");
		if (particle instanceof Particle){
			this.particle = (Particle) particle;
		}else{
			this.particle = new Particle(Effect.EXPLOSION_HUGE, "&7Explosion");
		}
		Object permission = map.get("Permission");
		if (permission instanceof String){
			this.permission = (String) permission;
		}else{
			this.permission = "no.permission.given";
		}
	}
	
	public void setItem(ItemStack item){
		this.item = item;
	}
	
	public void setParticle(Particle particle){
		this.particle = particle;
	}
	
	public ItemStack getItem(){
		return this.item;
	}
	
	public Particle getParticle(){
		return this.particle;
	}
	
	public String getPermission(){
		return this.permission;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("Icon", item);
		map.put("Particle", particle);
		map.put("Permission", permission);
		return map;
	}
	
}
