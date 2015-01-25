package me.MrBlobman.RCDonations.PotionEffects;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionInfo {
	private PotionEffectType effect;
	private int strength;
	private boolean ambient;
	
	public PotionInfo(PotionEffectType effect, int strength, boolean ambient) {
		this.effect = effect;
		this.strength = strength;
		this.ambient = ambient;
	}

	public PotionEffectType getEffect() {
		return effect;
	}

	public void setEffect(PotionEffectType effect) {
		this.effect = effect;
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
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof PotionInfo){
			PotionInfo info = (PotionInfo) obj;
			if (info.getEffect().equals(effect) && info.getStrength() == strength && info.isAmbient() == ambient){
				return true;
			}
		}
		return false;
	}
	
	public boolean isBetterThan(PotionInfo info){
		if (!info.getEffect().equals(effect)){
			return false;
		}
		if (strength >= info.getStrength()){
			return true;
		}
		return false;
	}
	
	public boolean isBetterThan(PotionEffect info){
		if (!info.getType().equals(effect)){
			return false;
		}
		if (strength >= info.getAmplifier()+1){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return effect.getName()+" "+strength;
	}
}
