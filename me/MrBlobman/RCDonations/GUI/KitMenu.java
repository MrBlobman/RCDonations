package me.MrBlobman.RCDonations.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.MrBlobman.RCDonations.KitManager;
import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.Items.Kit;
import me.MrBlobman.RCDonations.Utils.DateUtils;
import me.MrBlobman.RCDonations.Utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class KitMenu implements Menu{

	private Inventory inv;
	private int page = 0; //State variable
	private List<Kit[]> kits = new ArrayList<Kit[]>();
	private List<ItemStack[]> menuPages = new ArrayList<ItemStack[]>();
	private Player player;
	private BukkitTask updateTask;
	
	KitMenu(Player player){
		this.player = player;
		buildMenuPages();
		buildInventory();
	}
	
	private void buildMenuPages(){
		ItemStack backPage = new ItemBuilder().setMaterial(Material.INK_SACK).setData((short) 1).setName(ChatColor.YELLOW+"<< Back").setLore(ChatColor.GOLD+"Click to return to", ChatColor.GOLD+"the previous page.").build();
		ItemStack forwardPage = new ItemBuilder().setMaterial(Material.INK_SACK).setData((short) 2).setName(ChatColor.YELLOW+"Forward >>").setLore(ChatColor.GOLD+"Click to proceed to", ChatColor.GOLD+"the next page.").build();
		Kit[] allowedKits = KitManager.getAllowedKits(player);
		while (allowedKits.length > 25){
			kits.add(Arrays.copyOfRange(allowedKits, 0, 25));
			allowedKits = Arrays.copyOfRange(allowedKits, 25, allowedKits.length);
		}
		kits.add(allowedKits);
		for (Kit[] kitz : kits){
			ItemStack[] items = new ItemStack[27];
			int slot = 0;
			for (Kit kit : kitz){
				items[slot] = kit.getKitIcon();
				slot++;
			}
			items[25] = backPage;
			items[26] = forwardPage;
			menuPages.add(items);
		}
	}
	
	private void buildInventory(){
		inv = Bukkit.createInventory(null, 27, ChatColor.DARK_RED + "[" +ChatColor.AQUA+"Kits"+ChatColor.DARK_RED+"]");
		inv.setContents(menuPages.get(page));
	}
	
	private void startUpdateTask(){
		this.updateTask = Bukkit.getScheduler().runTaskTimer(RCDonations.plugin, new Runnable(){
			@Override
			public void run(){
				Kit[] kitsOnPage = kits.get(page);
				for (int i = 0; i<kitsOnPage.length; i++){
					ItemStack item = kitsOnPage[i].getKitIcon();
					if (item != null){
						String timeLeft = ChatColor.DARK_RED + "Available: " + ChatColor.RED + DateUtils.getFormattedTimeRemaining(kitsOnPage[i].getTimeRemainingFor(player));
						List<String> lore = new ArrayList<String>();
						if (item.getItemMeta().hasLore()){
							lore = item.getItemMeta().getLore();
							lore.set(0, timeLeft);
						}else{
							lore.add(timeLeft);
						}
						ItemMeta meta = item.getItemMeta();
						meta.setLore(lore);
						item.setItemMeta(meta);
						inv.setItem(i, item);
					}
				}
			}
		}, 0L, 20L);
	}
	
	@Override
	public void open() {
		Bukkit.getScheduler().runTask(RCDonations.plugin, new Runnable(){
			@Override
			public void run(){
				player.openInventory(inv);
			}
		});
		startUpdateTask();
	}

	@Override
	public Menu getNext(int clickedSlot) throws IllegalArgumentException {
		if (this.inv.getItem(clickedSlot) != null){
			Kit clickedKit = kits.get(page)[clickedSlot];
			close();
			return new KitContentsMenu(player, clickedKit);
		}
		throw new IllegalArgumentException("Cannot handle the given slot.");
	}

	@Override
	public boolean isEnd() {
		return true;
	}

	@Override
	public boolean handleClick(int slot) throws IllegalStateException {
		//Handle forward and backwards
		if (slot >= 25){
			if (slot == 25){
				if (page >= 1){
					page = page-1;
				}
			}else{
				if (page < this.menuPages.size()-1){
					page = page + 1;
				}
			}
			this.updateTask.cancel();
			startUpdateTask();
			return true;
		}else{
			throw new IllegalStateException("This icon opens a new menu.");
		}
	}

	@Override
	public void close() {
		this.updateTask.cancel();
	}
}
