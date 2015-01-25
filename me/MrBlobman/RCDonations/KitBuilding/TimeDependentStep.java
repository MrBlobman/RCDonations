package me.MrBlobman.RCDonations.KitBuilding;

import java.util.List;

import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.Items.KitItem;
import me.MrBlobman.RCDonations.Items.TimeDependentItem;
import me.MrBlobman.RCDonations.Utils.Day;
import me.MrBlobman.RCDonations.Utils.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TimeDependentStep implements Step, Listener {
	
	private Player player;
	private Inventory inv;
	private Runnable callWhenDone;
	private TimeDependentItem kitItem;
	private String dataKey;
	
	TimeDependentStep(Player p, String dataKey){
		this.player = p;
		this.dataKey = dataKey;
		this.inv = initialInventory();
		Bukkit.getServer().getPluginManager().registerEvents(this, RCDonations.plugin);
	}
	
	/**
	 * 
	 * @param p
	 * @param step
	 * @param alreadySetContents Expects blank slots to be null otherwise no caps will be made
	 */
	TimeDependentStep(Player p, String dataKey, List<ItemStack> alreadySetContents){
		this.player = p;
		this.dataKey = dataKey;
		this.inv = initialInventory();
		int slot = 0;
		for (ItemStack item : alreadySetContents){
			if (item != null){
				inv.setItem(slot, item);
			}
			slot++;
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, RCDonations.plugin);
	}
	
	private Inventory initialInventory(){
		Inventory inv = Bukkit.createInventory(player, 18, ChatColor.GREEN+"Set Time Dependent Item");
		inv.setItem(9, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData((short) 4).setName("&eSunday").setLore("&ePlace Sunday's item", "&eabove this.").build());
		inv.setItem(10, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData((short) 5).setName("&aMonday").setLore("&aPlace Monday's item", "&aabove this.").build());
		inv.setItem(11, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData((short) 3).setName("&bTuesday").setLore("&bPlace Tuesday's item", "&babove this.").build());
		inv.setItem(12, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData((short) 14).setName("&4Wednesday").setLore("&4Place Wednesday's item", "&4above this.").build());
		inv.setItem(13, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData((short) 8).setName("&7Thursday").setLore("&7Place Thursday's item", "&7above this.").build());
		inv.setItem(14, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData((short) 6).setName("&dFriday").setLore("&dPlace Friday's item", "&dabove this.").build());
		inv.setItem(15, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData((short) 1).setName("&6Saturday").setLore("&6Place Saturday's item", "&6above this.").build());
		return inv;
	}

	@Override
	public void start(Runnable callWhenDone) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(RCDonations.plugin, new Runnable(){
			@Override
			public void run(){
				player.openInventory(inv);
			}
		});
		this.callWhenDone = callWhenDone;
	}
	
	public void done() {
		InventoryCloseEvent.getHandlerList().unregister(this);
		this.kitItem = new TimeDependentItem();
		for (int i = 0; i < 7; i++){
			if (inv.getItem(i) != null){
				this.kitItem.addItem(Day.fromId(i), inv.getItem(i));
			}
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent event){
		if (event.getPlayer() == player){
			done();
			this.callWhenDone.run();
		}
	}
	
	@Override
	public KitItem getKitItem(){
		return this.kitItem;
	}

	@Override
	public String getDataKey() {
		return this.dataKey;
	}
}
