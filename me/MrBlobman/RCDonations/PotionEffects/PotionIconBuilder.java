package me.MrBlobman.RCDonations.PotionEffects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PotionIconBuilder {
	private Integer index;
	private PotionEffectType type;
	private int strength;
	private boolean ambient;
	private ItemStack item;
	private String permission;
	private int step;
	
	public PotionIconBuilder(){ }

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public PotionEffectType getType() {
		return type;
	}

	public void setType(PotionEffectType type) {
		this.type = type;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public boolean isAmbient() {
		return ambient;
	}

	public void setAmbient(boolean ambient) {
		this.ambient = ambient;
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
	
	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public PotionIcon build(){
		PotionInfo info = new PotionInfo(type, index, ambient);
		return new PotionIcon(info, item, permission);
	}
}
