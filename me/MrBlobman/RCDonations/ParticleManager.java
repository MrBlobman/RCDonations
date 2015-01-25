package me.MrBlobman.RCDonations;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import me.MrBlobman.RCDonations.Particles.ParticleEffect;
import me.MrBlobman.RCDonations.Particles.ParticleIcon;

public class ParticleManager implements Listener{
	private static Map<Integer, ParticleIcon> masterList = new ConcurrentHashMap<Integer, ParticleIcon>();
	private static Map<UUID, ParticleEffect> playOnMove = new ConcurrentHashMap<UUID, ParticleEffect>();
	private static Map<UUID, ParticleEffect> playOnClock = new ConcurrentHashMap<UUID, ParticleEffect>();
	private BukkitTask clock;
	
	
	/**
	 * Used for registering listeners only
	 */
	ParticleManager(){ 
		saveDefault();
		readMasterList();
		Bukkit.getPluginManager().registerEvents(this, RCDonations.plugin);
		clock = Bukkit.getScheduler().runTaskTimer(RCDonations.plugin, new Runnable(){
			@Override
			public void run(){
				for (ParticleEffect effect : playOnClock.values()){
					effect.playOut();
				}
			}
		}, 60L, 60L);
	}
	
	private boolean saveDefault(){
		File particleFile = new File(RCDonations.plugin.getDataFolder() + File.separator, "Particles.yml");
		if (!particleFile.exists()){
			particleFile.getParentFile().mkdirs();
			try {
				particleFile.createNewFile();
			} catch (IOException e) {
				RCDonations.plugin.getLogger().severe("[RCDonations] ERROR creating Particles.yml.");
				RCDonations.plugin.getLogger().severe(e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	private void readMasterList(){
		File file = new File(RCDonations.plugin.getDataFolder() + File.separator, "Particles.yml");
		if (!file.exists()){
			RCDonations.plugin.getLogger().severe("[RCDonations] " + file.getAbsolutePath() + " does not exist!");
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String key : config.getKeys(false)){
			try{
				Integer intKey = Integer.parseInt(key);
				if (intKey < 10 || intKey > 54){
					RCDonations.plugin.getLogger().severe("[RCDonations] ERROR in Particles.yml, "+key+" is not a number between 10 and 54.");
					continue;
				}
				masterList.put(intKey-1, (ParticleIcon) config.get(key));
			} catch (NumberFormatException e){
				RCDonations.plugin.getLogger().severe("[RCDonations] ERROR in Particles.yml, "+key+" is not a number between 10 and 54.");
			}
		}
	}
	
	public boolean writeMasterList(Integer index, ParticleIcon icon){
		File file = new File(RCDonations.plugin.getDataFolder() + File.separator, "Particles.yml");
		if (!file.exists()){
			RCDonations.plugin.getLogger().severe("[RCDonations] " + file.getAbsolutePath() + " does not exist!");
			return false;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set(String.valueOf(index), icon);
		try {
			config.save(file);
		} catch (IOException e) {
			RCDonations.plugin.getLogger().severe("[RCDonations] ERROR saving writeOut to Particles.yml");
			RCDonations.plugin.getLogger().severe("[RCDonations] " +e.getMessage());
			return false;
		}
		masterList.put(index, icon);
		return true;
	}
	
	public ParticleIcon removeMasterList(Integer index){
		if (masterList.containsKey(index)){
			File file = new File(RCDonations.plugin.getDataFolder() + File.separator, "Particles.yml");
			if (!file.exists()){
				RCDonations.plugin.getLogger().severe("[RCDonations] " + file.getAbsolutePath() + " does not exist!");
				return null;
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set(String.valueOf(index), null);
			try {
				config.save(file);
			} catch (IOException e) {
				RCDonations.plugin.getLogger().severe("[RCDonations] ERROR saving writeOut to Particles.yml");
				RCDonations.plugin.getLogger().severe("[RCDonations] " +e.getMessage());
				return null;
			}
			ParticleIcon removed = masterList.get(index);
			masterList.remove(index);
			return removed;
		}
		return null;
	}
	
	public boolean hasRegisteredParticle(Player player){
		return playOnMove.containsKey(player.getUniqueId()) || playOnClock.containsKey(player.getUniqueId());
	}
	
	public void registerOnMoveEffect(UUID id, ParticleEffect effect){
		playOnMove.put(id, effect);
		if (playOnClock.containsKey(id)){
			playOnClock.remove(id);
		}
	}
	
	public void registerOnClockEffect(UUID id, ParticleEffect effect){
		playOnClock.put(id, effect);
		if (playOnMove.containsKey(id)){
			playOnMove.remove(id);
		}
	}
	
	public void unregisterEffect(UUID id){
		if (playOnMove.containsKey(id)){
			playOnMove.remove(id);
		}
		if (playOnClock.containsKey(id)){
			playOnClock.remove(id);
		}
	}
	
	public ParticleEffect getRegisteredEffect(Player player){
		UUID id = player.getUniqueId();
		if (playOnMove.containsKey(id)){
			return playOnMove.get(id);
		}
		if (playOnClock.containsKey(id)){
			return playOnClock.get(id);
		}
		return null;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		if (playOnMove.containsKey(event.getPlayer().getUniqueId())){
			playOnMove.get(event.getPlayer().getUniqueId()).playOut();
		}
	}
	
	public void done(){
		this.clock.cancel();
		PlayerMoveEvent.getHandlerList().unregister(this);
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
	
	public Map<Integer, ParticleIcon> getMasterList(){
		return masterList;
	}
	
	public ParticleIcon getParticleIconByIndex(Integer key){
		return (masterList.containsKey(key) ? masterList.get(key) : null);
	}
}
