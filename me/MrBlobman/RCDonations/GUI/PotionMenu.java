package me.MrBlobman.RCDonations.GUI;

import java.util.List;
import java.util.Map;

import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.PotionEffects.PotionIcon;
import me.MrBlobman.RCDonations.Utils.InventoryUtils;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PotionMenu implements Menu{

	private Inventory inv;
	private Player player;
	private Map<Integer, PotionIcon> masterList;
	
	public PotionMenu(Player player){
		this.player = player;
		buildInventory();
	}
	
	private void buildInventory(){
		this.masterList = RCDonations.potionManager.getMasterList();
		this.inv = Bukkit.createInventory(null, InventoryUtils.getInvSize(RCDonations.potionManager.getMasterListMax()), ChatColor.DARK_RED + "[" + ChatColor.AQUA + "Potion Effects" + ChatColor.DARK_RED + "]");
		for (Integer slot : masterList.keySet()){
			PotionIcon icon = masterList.get(slot);
			if (player.hasPermission(icon.getPermission())){
				ItemStack item = icon.getItem();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.getLore();
				lore.set(0, RCDonations.potionManager.hasRegisteredEffect(player, icon.getInfo().getEffect()) ? ChatColor.GREEN+"ENABLED" : ChatColor.DARK_RED+"DISABLED");
				meta.setLore(lore);
				item.setItemMeta(meta);
				inv.setItem(slot, item);
			}
		}
	}
	
	@Override
	public void open() {
		Bukkit.getScheduler().runTask(RCDonations.plugin, new Runnable(){
			@Override
			public void run(){
				player.openInventory(inv);
			}
		});
	}

	@Override
	public Menu getNext(int clickedSlot) throws IllegalArgumentException {
		throw new IllegalArgumentException("This menu is an end menu.");
	}

	@Override
	public boolean isEnd() {
		return true;
	}

	@Override
	public boolean handleClick(int slot) throws IllegalStateException {
		if (this.masterList.containsKey(Integer.valueOf(slot))){
			PotionIcon icon = this.masterList.get(Integer.valueOf(slot));
			boolean wasEnabled = RCDonations.potionManager.hasRegisteredEffect(player, icon.getInfo().getEffect());
			if (wasEnabled){
				RCDonations.potionManager.unregisterEffect(player, icon.getInfo().getEffect());
			}else{
				RCDonations.potionManager.registerEffect(player, icon.getInfo());
			}
			ItemStack item = icon.getItem();
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			lore.set(0, wasEnabled ? ChatColor.DARK_RED+"DISABLED" : ChatColor.GREEN+"ENABLED");
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(slot, item);
			return true;
		}else{
			throw new IllegalStateException("Cannot handle a click in the location "+slot);
		}
	}

	@Override
	public void close() {
	}
	
}
