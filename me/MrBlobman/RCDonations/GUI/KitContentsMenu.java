package me.MrBlobman.RCDonations.GUI;

import java.util.HashMap;

import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.Items.Kit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitContentsMenu implements Menu{

	private Player player;
	private Kit kit;
	private Inventory inv;
	
	public KitContentsMenu(Player player, Kit kit){
		this.player = player;
		this.kit = kit;
		buildInventory();
	}
	
	private void buildInventory(){
		this.inv = Bukkit.createInventory(player, 27, this.kit.getDisplayName());
		this.inv.setContents(this.kit.getKitInstance(player).getUntakenItems());
	}
	
	@Override
	public void open() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(RCDonations.plugin, new Runnable(){
			@Override
			public void run(){
				player.openInventory(inv);
			}
		});
	}

	@Override
	public Menu getNext(int clickedSlot) throws IllegalArgumentException {
		throw new IllegalArgumentException("This menu is and end menu.");
	}

	@Override
	public boolean isEnd() {
		return true;
	}

	@Override
	public boolean handleClick(int slot) throws IllegalStateException {
		if (slot < 27 && slot >= 0){
			ItemStack item = inv.getItem(slot);
			if (item != null){
				HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(item);
				if (leftOver.isEmpty()){
					this.inv.setItem(slot, null);
				}else{
					this.inv.setItem(slot, leftOver.get(0));
				}
			}
		}
		return true;
	}

	@Override
	public void close() {
		this.kit.closeKit(player, inv.getContents());
	}

}
