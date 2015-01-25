package me.MrBlobman.RCDonations.KitBuilding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.Items.KitItem;
import me.MrBlobman.RCDonations.Items.RandomItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RandomItemStep implements Step, Listener{

	private Player player;
	private Inventory inv;
	private Runnable callWhenDone;
	private RandomItem kitItem;
	private boolean itemsLocked = false;
	private int[] amtMap = new int[54];
	private String dataKey;
	
	RandomItemStep(Player player, String dataKey){
		this.player = player;
		this.dataKey = dataKey;
		this.inv = Bukkit.createInventory(player, 54, ChatColor.GREEN+"Set Random Item");
		Bukkit.getServer().getPluginManager().registerEvents(this, RCDonations.plugin);
		for (int i = 0; i < 54; i++){
			amtMap[i] = 1;
		}
	}
	
	RandomItemStep(Player player, String dataKey, Map<String, ItemStack> alreadySetContents){
		this(player, dataKey);
		int slot = 0;
		for (String amt : alreadySetContents.keySet()){
			inv.addItem(alreadySetContents.get(amt));
			amtMap[slot] = Integer.parseInt(amt);
			slot++;
		}
	}
	
	private void setupChangeAmounts(){
		int slot = 0;
		for (ItemStack item : this.inv.getContents()){
			if (item != null){
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				if (meta.hasLore()){
					lore = meta.getLore();
				}
				lore.add(0, ChatColor.GREEN+"Amount in grab bag: "+amtMap[slot]);
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			slot++;
		}
		player.sendMessage(RCDonations.prefix+ChatColor.GREEN+"Left click "+ChatColor.DARK_GREEN+"to "+ChatColor.GREEN+"deincrement "+ChatColor.DARK_GREEN+" and "+ChatColor.GREEN+"right click "+ChatColor.DARK_GREEN+"to "+ChatColor.GREEN+"increment "+ChatColor.DARK_GREEN+"the number of times this item is placed into the grab bag. A single ItemStack will be given to the player so this is how you can modify the probabilities of each ItemStack.");
		Bukkit.getScheduler().runTask(RCDonations.plugin, new Runnable(){
			@Override
			public void run() {
				player.openInventory(inv);
			}
		});
	}
	
	private void tearDownChangeAmounts(){
		for (ItemStack item : this.inv.getContents()){
			if (item != null){
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.getLore();
				lore.remove(0);
				meta.setLore(lore);
			}
		}
	}
	
	private ItemMeta modAmt(ItemStack item, boolean increment, int slot){
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		int newAmt = Integer.parseInt(lore.get(0).substring(lore.get(0).indexOf(":"+2)));
		if (!increment){
			if (newAmt > 1){
				newAmt = newAmt - 1;
			}
		}else{
			newAmt = newAmt + 1;
		}
		amtMap[slot] = newAmt;
		lore.set(0, ChatColor.GREEN+"Amount in grab bag: "+newAmt);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return meta;
	}
	
	private void convertInvToData(){
		this.kitItem = new RandomItem();
		int slot = 0;
		for (ItemStack item : inv.getContents()){
			if (item != null){
				int amt = amtMap[slot];
				this.kitItem.addItem(item, amt);
			}
			slot++;
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if (event.getPlayer() == this.player){
			if (!itemsLocked){
				this.itemsLocked = true;
				setupChangeAmounts();
			}else{
				InventoryCloseEvent.getHandlerList().unregister(this);
				InventoryClickEvent.getHandlerList().unregister(this);
				tearDownChangeAmounts();
				convertInvToData();
				this.callWhenDone.run();
			}
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event){
		if (event.getWhoClicked() == this.player){
			event.setCancelled(true);
			if (event.getCurrentItem() != null){
				ItemStack item = event.getCurrentItem();
				if (event.getClick().isLeftClick()){
					//Decrease amt by one
					item.setItemMeta(modAmt(item, false, event.getRawSlot()));
				}else if (event.getClick().isRightClick()){
					//Increase amt by one
					item.setItemMeta(modAmt(item, true, event.getRawSlot()));
				}
			}
		}
	}
	
	@Override
	public void start(Runnable callWhenDone) {
		this.callWhenDone = callWhenDone;
	}

	@Override
	public KitItem getKitItem() {
		return this.kitItem;
	}

	@Override
	public String getDataKey() {
		return this.dataKey;
	}

}
