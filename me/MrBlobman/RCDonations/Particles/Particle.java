package me.MrBlobman.RCDonations.Particles;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Particle implements ConfigurationSerializable{
	private Effect effect;
	private Object data;
	private String name;
	
	public Particle(Effect effect, Object data, String name){
		this.effect = effect;
		this.name = name;
		this.data = data;
	}
	
	public Particle(Map<String, Object> map){
		this.effect = Effect.valueOf((String) map.get("Effect"));
		this.name = (String) map.get("Name");
		if (map.containsKey("Data")){
			this.data = map.get(data);
		}
	}
	
	public Particle(Effect effect, String name){
		this(effect, null, name);
	}
	
	public void playOut(Location location){
		location.getWorld().playEffect(location, effect, data);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("Effect", effect.toString());
		map.put("Name", name);
		if (data != null){
			map.put("Data", data);
		}
		return map;
	}

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
