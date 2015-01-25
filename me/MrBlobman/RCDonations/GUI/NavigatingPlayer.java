package me.MrBlobman.RCDonations.GUI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import me.MrBlobman.RCDonations.KitManager;
import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.Utils.Navigation;

public class NavigatingPlayer implements Listener{
	
	private Navigation nav;
	private Player player;
	private boolean backup = false;
	
	public NavigatingPlayer(Player player){
		this.player = player;
		this.nav = new Navigation(new MainMenu(player));
		Bukkit.getPluginManager().registerEvents(this, RCDonations.plugin);
		openMenu(nav.getCurrent());
	}
	
	public void done(){
		InventoryClickEvent.getHandlerList().unregister(this);
		InventoryCloseEvent.getHandlerList().unregister(this);
		if (KitManager.navPlayers.containsKey(player.getUniqueId())){
			KitManager.navPlayers.remove(player.getUniqueId());
		}
	}
	
	public void openMenu(Menu menu){
		menu.open();
	}
	
	private void backUp(){
		if (nav.isInRoot()){
			done();
			return;
		}
		nav.pullTop().close();
		nav.getCurrent().open();
	}
	
	private boolean stepForward(int slotClicked){
		Menu menu;
		try{
			menu = nav.getCurrent().getNext(slotClicked);
		} catch (IllegalArgumentException e){
			//Do nothing as they clicked somewhere where we can't handle
			return true;
		}
		openMenu(menu);
		nav.addTop(menu);
		this.backup = false;
		return true;
	}
	
	private boolean handleClick(int slot){
		try{
			return nav.getCurrent().handleClick(slot);
		} catch (IllegalStateException e){
			//Tried to handle a click on a directory
			//Therefore try to handle the click differently
			return stepForward(slot);
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent event){
		if (event.getPlayer() == this.player){
			if (backup){
				backUp();
			}else{
				this.backup = true;
			}
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event){
		if (event.getWhoClicked() == this.player){
			event.setCancelled(handleClick(event.getRawSlot()));
		}
	}
}
