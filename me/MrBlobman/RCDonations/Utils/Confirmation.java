package me.MrBlobman.RCDonations.Utils;


import me.MrBlobman.RCDonations.RCDonations;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

public class Confirmation implements Listener{
	
	private BukkitTask task;
	private Runnable runOnConfirm;
	private Runnable runIfNotConfirmed;
	
	public Confirmation(Player player, Runnable runIfConfirmed, Runnable runIfNotConfirmed, Long delay){
		Bukkit.getPluginManager().registerEvents(this, RCDonations.plugin);
		this.runOnConfirm = runIfConfirmed;
		this.runIfNotConfirmed = runIfNotConfirmed;
		this.task = Bukkit.getScheduler().runTaskLater(RCDonations.plugin, new Runnable(){
			@Override
			public void run() {
				didNotConfirm();
			}
		}, delay);
	}
	
	private void didNotConfirm(){
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		runIfNotConfirmed.run();
	}
	
	private void confirmed(){
		this.task.cancel();
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		runOnConfirm.run();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		event.setCancelled(true);
		String msg = event.getMessage().toUpperCase();
		if (msg.contains("YES") || msg.contains("Y")){
			confirmed();
		}else{
			didNotConfirm();
		}
	}
}
