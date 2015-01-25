package me.MrBlobman.RCDonations;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.MrBlobman.RCDonations.PotionEffects.Potion;
import me.MrBlobman.RCDonations.PotionEffects.PotionIcon;
import me.MrBlobman.RCDonations.PotionEffects.PotionInfo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class PotionEffectManager {
	
	private Map<Integer, PotionIcon> masterList = new ConcurrentHashMap<Integer, PotionIcon>();
	private Map<Player, Potion> registeredEffects = new ConcurrentHashMap<Player, Potion>();
	private BukkitTask task;
	
	public PotionEffectManager(){
		saveDefault();
		readMasterList();
		scheduleTask();
	}
	
	private void scheduleTask(){
		//Every 4s add the 8s effects
		this.task = Bukkit.getScheduler().runTaskTimer(RCDonations.plugin, new Runnable(){
			@Override
			public void run(){
				for (Player player : registeredEffects.keySet()){
					if (player.isOnline()){
						registeredEffects.get(player).applyToPlayer(player);
					}else{
						registeredEffects.remove(player);
					}
				}
			}
		}, 0L, 80L);
	}
	
	/**
	 * Stops the task that applies effects if it is not already stopped
	 */
	public void stopTask(){
		if (this.task != null){
			this.task.cancel();
		}
	}
	
	/**
	 * Starts the task that applies the effects if it is not already running
	 */
	public void startTask(){
		if (this.task == null){
			scheduleTask();
		}
	}
	
	public void registerEffect(Player player, PotionEffectType type, int strength, boolean ambient){
		if (registeredEffects.containsKey(player)){
			registeredEffects.get(player).addEffect(type, strength, ambient);
		}else{
			Potion potion = new Potion();
			potion.addEffect(type, strength, ambient);
			registeredEffects.put(player, potion);
		}
	}
	
	public void registerEffect(Player player, PotionInfo info){
		if (registeredEffects.containsKey(player)){
			registeredEffects.get(player).addEffect(info);
		}else{
			Potion potion = new Potion();
			potion.addEffect(info);
			registeredEffects.put(player, potion);
		}
	}
	
	public void unregisterEffect(Player player, PotionEffectType type){
		if (registeredEffects.containsKey(player)){
			registeredEffects.get(player).removeEffect(type);
		}
	}
	
	public boolean hasRegisteredEffect(Player player, PotionEffectType type){
		if (registeredEffects.containsKey(player)){
			if (registeredEffects.get(player).hasEffect(type)){
				return true;
			}
		}
		return false;
	}
	
	public boolean indexHasRegisteredEffect(Integer slot){
		return masterList.containsKey(slot);
	}
	
	private boolean saveDefault(){
		File particleFile = new File(RCDonations.plugin.getDataFolder() + File.separator, "Potions.yml");
		if (!particleFile.exists()){
			particleFile.getParentFile().mkdirs();
			try {
				particleFile.createNewFile();
			} catch (IOException e) {
				RCDonations.plugin.getLogger().severe("[RCDonations] ERROR creating Potions.yml.");
				RCDonations.plugin.getLogger().severe(e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	private void readMasterList(){
		File file = new File(RCDonations.plugin.getDataFolder() + File.separator, "Potions.yml");
		if (!file.exists()){
			RCDonations.plugin.getLogger().severe("[RCDonations] " + file.getAbsolutePath() + " does not exist!");
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String key : config.getKeys(false)){
			try{
				Integer intKey = Integer.parseInt(key);
				if (intKey < 1 || intKey > 54){
					RCDonations.plugin.getLogger().severe("[RCDonations] ERROR in Potions.yml, "+key+" is not a number between 1 and 54.");
					continue;
				}
				masterList.put(intKey-1, (PotionIcon) config.get(key));
			} catch (NumberFormatException e){
				RCDonations.plugin.getLogger().severe("[RCDonations] ERROR in Potions.yml, "+key+" is not a number between 1 and 54.");
			}
		}
	}
	
	public boolean writeMasterList(Integer index, PotionIcon icon){
		File file = new File(RCDonations.plugin.getDataFolder() + File.separator, "Potions.yml");
		if (!file.exists()){
			RCDonations.plugin.getLogger().severe("[RCDonations] " + file.getAbsolutePath() + " does not exist!");
			return false;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set(String.valueOf(index), icon);
		try {
			config.save(file);
		} catch (IOException e) {
			RCDonations.plugin.getLogger().severe("[RCDonations] ERROR saving writeOut to Potions.yml");
			RCDonations.plugin.getLogger().severe("[RCDonations] " +e.getMessage());
			return false;
		}
		masterList.put(index, icon);
		return true;
	}
	
	public PotionIcon removeMasterList(Integer index){
		if (masterList.containsKey(index)){
			File file = new File(RCDonations.plugin.getDataFolder() + File.separator, "Potions.yml");
			if (!file.exists()){
				RCDonations.plugin.getLogger().severe("[RCDonations] " + file.getAbsolutePath() + " does not exist!");
				return null;
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set(String.valueOf(index), null);
			try {
				config.save(file);
			} catch (IOException e) {
				RCDonations.plugin.getLogger().severe("[RCDonations] ERROR saving writeOut to Potions.yml");
				RCDonations.plugin.getLogger().severe("[RCDonations] " +e.getMessage());
				return null;
			}
			PotionIcon removed = masterList.get(index);
			masterList.remove(index);
			return removed;
		}
		return null;
	}
	
	/**
	 * Gets the maximum index in the master list.
	 * @return the maximum Integer key in the master list
	 */
	public int getMasterListMax(){
		Integer max = 0;
		for (Integer key : masterList.keySet()){
			if (key > max){
				max = key;
			}
		}
		return max;
	}
	
	public Map<Integer, PotionIcon> getMasterList(){
		return masterList;
	}
	
	public PotionIcon getPotionIconByIndex(Integer key){
		return (masterList.containsKey(key) ? masterList.get(key) : null);
	}
}
