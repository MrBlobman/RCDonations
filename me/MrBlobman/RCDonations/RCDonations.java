package me.MrBlobman.RCDonations;

import java.util.HashMap;
import me.MrBlobman.RCDonations.Particles.Particle;
import me.MrBlobman.RCDonations.Particles.ParticleIcon;
import me.MrBlobman.RCDonations.PotionEffects.PotionIcon;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class RCDonations extends JavaPlugin{
	public static HashMap<Player, String> kitChests = new HashMap<Player, String>();
	public static RCDonations plugin;
	public static String prefix = ChatColor.DARK_RED+"["+ChatColor.AQUA+"RCDonations"+ChatColor.DARK_RED+"]"+ChatColor.RESET+" ";
	public static ParticleManager particleManager;
	public static PotionEffectManager potionManager;
	private static BukkitTask purgeTask;
	
	public void onEnable(){
		plugin = this;
		ConfigurationSerialization.registerClass(Particle.class);
		ConfigurationSerialization.registerClass(ParticleIcon.class);
		ConfigurationSerialization.registerClass(PotionIcon.class);
		this.saveDefaultConfig();
		initCommands();
		if (!KitManager.loadKits()){
			this.getLogger().severe("[RCDonations] Something went wrong loading the kits.");
		}
		particleManager = new ParticleManager();
		potionManager = new PotionEffectManager();
		schedulePurgeTask();
		this.getLogger().info("[RCDonations] enabled.");
	}
	public void onDisbale(){
		purgeTask.cancel();
		particleManager.done();
		this.getLogger().info("[RCDonations] disabled.");
		
	}
	
	private void initCommands(){
		getCommand("menu").setExecutor(new KitManager());
		getCommand("createKit").setExecutor(new KitEditingManager());
		getCommand("editKit").setExecutor(new KitEditingManager());
		getCommand("setIcon").setExecutor(new KitEditingManager());
		getCommand("removeKit").setExecutor(new KitEditingManager());
		getCommand("stopEditing").setExecutor(new KitEditingManager());
		getCommand("getFlag").setExecutor(new KitEditingManager());
		getCommand("addParticle").setExecutor(new ParticleEditingManager());
		getCommand("removeParticle").setExecutor(new ParticleEditingManager());
		getCommand("addPotionEffect").setExecutor(new PotionEffectEditingManager());
		getCommand("removePotionEffect").setExecutor(new PotionEffectEditingManager());
	}
	
	private void schedulePurgeTask(){
		Bukkit.getScheduler().runTaskTimer(this, new Runnable(){

			@Override
			public void run() {
				KitManager.purgeKits();
			}
			
		}, 0L, 2160000L);
	}
}
