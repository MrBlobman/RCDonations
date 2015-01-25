package me.MrBlobman.RCDonations.Items;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.MrBlobman.RCDonations.RCDonations;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Kit{
	
	private Long cooldown = 0L;
	private String rawName = "";
	private String displayName = "";
	private ItemStack kitIcon = new ItemStack(Material.AIR);
	private String permission = "";
	private KitItem[] items;
	private Map<UUID, KitInstance> accessedKits = new ConcurrentHashMap<UUID, KitInstance>();
	
	/**
	 * Blank instance used for building
	 */
	public Kit(){ }
	
	public Kit(KitItem... items){
		this.setItems(items);
	}

	public KitItem[] getItems() {
		return items;
	}

	public void setItems(KitItem[] items) {
		this.items = items;
	}

	public Long getCooldown() {
		return cooldown;
	}

	public void setCooldown(Long cooldown) {
		this.cooldown = cooldown;
	}
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getRawName() {
		return rawName;
	}
	
	/**
	 * Used for file access. Change displayName for all cosmetic changes.
	 * This field should only be changed if you know what you are doing.
	 * @param rawName The new rawName for the kit.
	 */
	public void setRawName(String rawName) {
		this.rawName = rawName;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public ItemStack getKitIcon() {
		return kitIcon;
	}

	public void setKitIcon(ItemStack kitIcon) {
		this.kitIcon = kitIcon;
	}

	public boolean hasOpenKit(Player player){
		return accessedKits.containsKey(player);
	}
	
	public void initializeInstances(FileConfiguration kitConfig){
		if (kitConfig.contains("Instances")){
			for (String key : kitConfig.getConfigurationSection("Instances").getKeys(false)){
				this.accessedKits.put(UUID.fromString(key), new KitInstance().readItemsLeft(kitConfig, "Instances."+key));
			}
		}
	}
	
	public boolean hasKitInstance(Player player){
		return accessedKits.containsKey(player.getUniqueId());
	}
	
	public Long getTimeRemainingFor(Player player){
		if (accessedKits.containsKey(player.getUniqueId())){
			return accessedKits.get(player.getUniqueId()).getTimeRemaining();
		}return 0L;
	}
	
	/**
	 * Creates a new instance if player does not have one
	 * @param player
	 * @return KitInstance for the player
	 */
	public KitInstance getKitInstance(Player player){
		UUID id = player.getUniqueId();
		if (!accessedKits.containsKey(id)){
			accessedKits.put(id, new KitInstance(this));
		}
		if (accessedKits.get(id).getTimeRemaining() <= 0L){
			accessedKits.put(id, new KitInstance(this));
		}
		return accessedKits.get(id);
	}
	
	public void openKit(final Player player){
		Bukkit.getScheduler().scheduleSyncDelayedTask(RCDonations.plugin, new Runnable(){

			@Override
			public void run() {
				Inventory inv = Bukkit.createInventory(player, items.length, getDisplayName());
				inv.setContents(getKitInstance(player).getUntakenItems());
				player.openInventory(inv);
			}
			
		});
	}
	
	public void closeKit(Player player, ItemStack[] itemsLeft){
		KitInstance instance = getKitInstance(player);
		instance.setUntakenItems(itemsLeft);
		instance.writeOutItemsLeft(new File(RCDonations.plugin.getDataFolder()+File.separator+"Kits"+File.separator, this.rawName+".yml"), "Instances."+player.getUniqueId().toString());
	}
}
