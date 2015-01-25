package me.MrBlobman.RCDonations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.MrBlobman.RCDonations.GUI.NavigatingPlayer;
import me.MrBlobman.RCDonations.Items.Kit;
import me.MrBlobman.RCDonations.Items.KitItem;
import me.MrBlobman.RCDonations.Items.NormalItem;
import me.MrBlobman.RCDonations.Items.RandomItem;
import me.MrBlobman.RCDonations.Items.TimeDependentItem;
import me.MrBlobman.RCDonations.Utils.DateUtils;
import me.MrBlobman.RCDonations.Utils.InventoryUtils;
import me.MrBlobman.RCDonations.Utils.ItemParser;

public class KitManager implements CommandExecutor {
	public static Map<String, Kit> kits = new ConcurrentHashMap<String, Kit>();
	public static Map<UUID, NavigatingPlayer> navPlayers = new ConcurrentHashMap<UUID, NavigatingPlayer>();
	
	/**
	 * Attempts to load all kits defined in the Kits folder
	 * @return true if kits were successfully loaded, else false
	 */
	public static boolean loadKits(){
		kits.clear();
		File kitFolder = new File(RCDonations.plugin.getDataFolder() + File.separator + "Kits");
		if (!kitFolder.exists()){
			RCDonations.plugin.getLogger().severe("Kit folder does not exist!");
			return false;
		}
		if (!kitFolder.isDirectory()){
			RCDonations.plugin.getLogger().severe("Kits folder is not a directory. Kits should be a folder containing the kits.");
			return false;
		}
		if (!kitFolder.canRead()){
			RCDonations.plugin.getLogger().severe("Your current permissions do not allow RCDonations to read from the Kits folder and cannot load kits from it.");
			return false;
		}
		File[] kitFiles = kitFolder.listFiles();
		for (File kitFile : kitFiles){
			loadKit(kitFile);
		}
		return true;
	}
	
	public static boolean loadKit(File kitFile){
		FileConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
		Long cooldown;
		String permission;
		String name;
		ItemStack icon;
		if (kitConfig.contains("Cooldown")){
			try{
				cooldown = DateUtils.stringTimeToLong(kitConfig.getString("Cooldown"));
			} catch (NumberFormatException e){
				RCDonations.plugin.getLogger().warning(kitFile.getName() + " cooldown format is incorrect. Use <num><D/H/M/S>:<num><D/H/M/S>:... EX: 1D:2s:3M = 1day 3min 2sec. Skipping...");
				return false;
			}
		}else{
			RCDonations.plugin.getLogger().warning(kitFile.getName() + " does not contain a cooldown. i.e. Cooldown: 1D:5H. Skipping...");
			return false;
		}
		if (kitConfig.contains("Permission")){
			permission = kitConfig.getString("Permission");
		}else{
			RCDonations.plugin.getLogger().warning(kitFile.getName() + " does not contain a permission. i.e. Permission: 'kits.myKit.isTheBest'. Skipping...");
			return false;
		}
		if (kitConfig.contains("Name")){
			name = ChatColor.translateAlternateColorCodes('&', kitConfig.getString("Name"));
		}else{
			RCDonations.plugin.getLogger().warning(kitFile.getName() + " does not contain a name. i.e. Name: '&bThe &4BEST &bkit!'. Using file name as kitname.");
			return false;
		}
		if (kitConfig.contains("Icon")){
			icon = ItemParser.parseItem(kitConfig.getConfigurationSection("Icon"));
		}else{
			icon = new ItemStack(Material.AIR);
			RCDonations.plugin.getLogger().warning(kitFile.getName() + " does not contain a display icon. i.e. Icon: ... Using air.");
			return false;
		}
		if (kitConfig.contains("Items")){
			Map<Integer, KitItem> items = new HashMap<Integer, KitItem>();
			Integer maxKey = 0;
			for (String key : kitConfig.getConfigurationSection("Items").getKeys(false)){
				if (kitConfig.contains("Items."+key+".Type")){
					String type = kitConfig.getString("Items."+key+".Type");
					Integer slot;
					try{
						slot = Integer.parseInt(key);
					}catch (NumberFormatException e){
						RCDonations.plugin.getLogger().warning(kitFile.getName() + " invalid Items." + key + ". The key " + key + " is not a number.");
						return false;
					}
					if (slot < 0 || slot > 53){
						RCDonations.plugin.getLogger().warning(kitFile.getName() + " invalid Items." + key + ". The key " + key + " is the slot of an inventory and cannot be greater than 53 or less than 0.");
						return false;
					}
					if (slot > maxKey){
						maxKey = slot;
					}
					switch (type.toUpperCase()){
					case "RANDOM":
						items.put(slot, new RandomItem().readItem(kitConfig.getConfigurationSection("Items."+key+".Item")));
						break;
					case "NORMAL":
						items.put(slot, new NormalItem().readItem(kitConfig.getConfigurationSection("Items."+key+".Item")));
						break;
					case "TIME_DEPENDENT":
						items.put(slot, new TimeDependentItem().readItem(kitConfig.getConfigurationSection("Items."+key+".Item")));
						break;
					default:
						RCDonations.plugin.getLogger().warning(kitFile.getName() + " invalid Items." + key + ".Type. Valid types are Normal, Random, Time. Skipping...");
						return false;
					}
				}else{
					RCDonations.plugin.getLogger().warning(kitFile.getName() + " missing Items." + key + ".Type. Skipping...");
					return false;
				}
			}
			KitItem[] kitItems = new KitItem[InventoryUtils.getInvSize(maxKey)];
			for (Integer key : items.keySet()){
				kitItems[key] = items.get(key);
			}
			Kit kit = new Kit(kitItems);
			kit.initializeInstances(kitConfig);
			kit.setPermission(permission);
			kit.setRawName(kitFile.getName().replace(".yml", ""));
			kit.setDisplayName(name);
			kit.setKitIcon(icon);
			kit.setCooldown(cooldown);
			kits.put(kit.getRawName(), kit);
			return true;
		}else{
			RCDonations.plugin.getLogger().warning(kitFile.getName() + " does not contain any items. I SHALL NEVER LOAD AN EMPTY KIT! WHY ARE U WASTING MY TIME! OMGGG.");
			return false;
		}
	}
	/**
	 * Directly add a kit to the master registry
	 * @param kit The kit you wish to add
	 */
	public static void registerKit(Kit kit){
		KitManager.kits.put(kit.getRawName(), kit);
	}
	
	/**
	 * @param name: the name of the kit you are looking for
	 * @return the first appearance of a Kit with the same name as given or null if not found
	 */
	public static Kit getByName(String name){
		if (kits.containsKey(name)){
			return kits.get(name);
		}return null;
	}
	
	/**
	 * 
	 * @param name the rawName of the kit you are inquiring about
	 * @return true if the kit exists (not necessarily loaded!), false otherwise
	 */
	public static boolean kitExists(String name){
		if (new File(RCDonations.plugin.getDataFolder()+File.separator+"Kits"+File.separator, name+".yml").exists()){
			return true;
		}else{
			return false;
		}
	}
	
	/** 
	 * 
	 * @param name the rawName of the kit you are inquiring about
	 * @return true if the kit is loaded, false otherwise
	 */
	public static boolean kitIsLoaded(String name){
		return kits.containsKey(name);
	}
	
	/**
	 * Unloads the given kit, safe to call if kit is loaded or not
	 * @param kit the Kit you are unloading
	 */
	public static void unloadKit(Kit kit){
		if (KitManager.kits.containsKey(kit.getRawName())){
			KitManager.kits.remove(kit.getRawName());
		}
	}
	
	/**
	 * Completely remove a kit including the kit's file.
	 * @param name the rawName of the kit to remove
	 * @return true if the kit was removed, false otherwise
	 */
	public static boolean removeKit(String name){
		File kitFile = new File(RCDonations.plugin.getDataFolder() + File.separator + "Kits" + File.separator, name+".yml");
		if (!kitFile.exists()){
			try{
				boolean success = kitFile.delete();
				return success;
			}catch (SecurityException e){
				RCDonations.plugin.getLogger().severe(RCDonations.prefix + "Plugin is not allowed to delete " + name + ".yml");
				return false;
			}
		}
		return true;
	}
	
	public static Kit[] getAllowedKits(Player player){
		ArrayList<Kit> allowedKits = new ArrayList<Kit>();
		for (Kit kit : kits.values()){
			if (player.hasPermission(kit.getPermission())){
				allowedKits.add(kit);
			}
		}
		return allowedKits.toArray(new Kit[allowedKits.size()]);
	}
	
	public static boolean purgeKits(){
		File kitFolder = new File(RCDonations.plugin.getDataFolder() + File.separator + "Kits");
		if (!kitFolder.exists()){
			RCDonations.plugin.getLogger().severe("Kit folder does not exist!");
			return false;
		}
		if (!kitFolder.isDirectory()){
			RCDonations.plugin.getLogger().severe("Kits folder is not a directory. Kits should be a folder containing the kits.");
			return false;
		}
		if (!kitFolder.canRead()){
			RCDonations.plugin.getLogger().severe("Your current permissions do not allow RCDonations to read from the Kits folder and cannot load kits from it.");
			return false;
		}
		File[] kitFiles = kitFolder.listFiles();
		for (File kitFile : kitFiles){
			purgeKit(kitFile);
		}
		return true;
	}
	
	public static void purgeKit(File kitFile){
		FileConfiguration config = YamlConfiguration.loadConfiguration(kitFile);
		if (config.contains("Instances")){
			for (String uuid : config.getConfigurationSection("Instances").getKeys(false)){
				if (config.getLong("Instances."+uuid+".CooldownEnds") <= System.currentTimeMillis()){
					config.set("Instances."+uuid, null);
				}
			}
		}
		try {
			config.save(kitFile);
		} catch (IOException e) {
			RCDonations.plugin.getLogger().warning("[RCDonations] Purge FAILED on "+kitFile.getPath());
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(RCDonations.prefix + ChatColor.RED + "This command can only be used by a Player.");
			return true;
		}
		Player player = (Player) sender;
		if (!player.hasPermission(cmd.getPermission())){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "You aren't allowed to use this command.");
			return true;
		}
		if (cmd.getName().equals("menu")){
			if (navPlayers.containsKey(player.getUniqueId())){
				player.sendMessage(RCDonations.prefix + ChatColor.RED + "You can only open one menu at a time.");
				return true;
			}else{
				navPlayers.put(player.getUniqueId(), new NavigatingPlayer(player));
				return true;
			}
		}else{
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "MrBlobman forgot to handle a registered command. Please let him know which one.");
			return true;
		}
	}
}
