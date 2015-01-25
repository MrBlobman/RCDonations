package me.MrBlobman.RCDonations.PotionEffects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Potion {
	
	private List<PotionInfo> effects = new ArrayList<PotionInfo>();
	
	public Potion(){ }
	
	public void addEffect(PotionInfo toAdd){
		if (!effects.contains(toAdd)){
			for (PotionInfo inList : effects){
				if (toAdd.isBetterThan(inList)){
					inList.setStrength(toAdd.getStrength());
					inList.setAmbient(toAdd.isAmbient());
					return;
				}
			}
			effects.add(toAdd);
		}
	}
	
	public void addEffect(PotionEffectType effect, int strength, boolean ambient){
		addEffect(new PotionInfo(effect, strength, ambient));
	}
	
	public void removeEffect(PotionEffectType effect){
		for (PotionInfo inList : effects){
			if (inList.getEffect().equals(effect)){
				effects.remove(inList);
				return;
			}
		}
	}
	
	public boolean hasEffect(PotionEffectType type){
		for (PotionInfo info : effects){
			if (info.getEffect().equals(type)){
				return true;
			}
		}return false;
	}
	
	public void clearEffects(){
		effects.clear();
	}
	
	public void applyToPlayer(Player player){
		for (PotionInfo info : effects){
			for (PotionEffect effect : player.getActivePotionEffects()){
				if (info.isBetterThan(effect)){
					player.removePotionEffect(effect.getType());
				}
			}
			player.addPotionEffect(new PotionEffect(info.getEffect(), 8*25, info.getStrength()-1, info.isAmbient()));
		}
	}
}
