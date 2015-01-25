package me.MrBlobman.RCDonations.Particles;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ParticleIconBuilder {
	
	private Particle particle;
	private ItemStack item;
	private String name;
	private String permission;
	private Object data;
	private Effect effect;
	private String dataInput;
	private Integer index;
	
	public ParticleIconBuilder(){ }

	public Particle getParticle() {
		return particle;
	}

	public void setParticle(Particle particle) {
		this.particle = particle;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}
	
	public String getDataInput() {
		return dataInput;
	}
	
	public String getDataInputAndParseBehind(String parseAs){
		parseInput(parseAs);
		return dataInput;
	}

	public void setDataInput(String dataInput) {
		this.dataInput = dataInput;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	@SuppressWarnings("deprecation") //MaterialData no longer uses magicValues as MC stopped that
	public void parseInput(String parseAs){
		switch(parseAs){
		case "potion":
			PotionEffectType type = PotionEffectType.getByName(dataInput);
			if (type == null){
				//Default
				type = PotionEffectType.REGENERATION;
			}
			this.data = new Potion(PotionType.getByEffect(type));
			break;
		case "material":
			this.data = Material.getMaterial(dataInput);
			if (this.data == null){
				this.data = Material.STONE;
			}
			break;
		case "materialdata":
			if (dataInput.contains(":")){
				this.data = new MaterialData(Material.getMaterial(dataInput.split(":")[0]), Byte.parseByte(dataInput.split(":")[1]));
			}else{
				this.data = new MaterialData(Material.getMaterial(dataInput));
			}
			if (this.data == null){
				this.data = Material.STONE;
			}
			break;
		}
	}
	
	public ParticleIcon build(){
		this.particle = new Particle(effect, data, name);
		return new ParticleIcon(item, particle, permission);
	}
	
}
