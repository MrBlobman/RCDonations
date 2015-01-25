package me.MrBlobman.RCDonations;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.MrBlobman.RCDonations.Items.Flags;
import me.MrBlobman.RCDonations.Items.Kit;
import me.MrBlobman.RCDonations.KitBuilding.ChestEditingPlayer;
import me.MrBlobman.RCDonations.Utils.Confirmation;
import me.MrBlobman.RCDonations.Utils.ItemParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitEditingManager implements CommandExecutor{
	
	public static Map<UUID, ChestEditingPlayer> editingPlayers = new ConcurrentHashMap<UUID, ChestEditingPlayer>();
	
	public static ChestEditingPlayer getChestEditingPlayer(UUID id){
		return (editingPlayers.containsKey(id) ? editingPlayers.get(id) : null);
	}
	
	public static void addChestEditingPlayer(ChestEditingPlayer player){
		editingPlayers.put(player.getPlayer().getUniqueId(), player);
	}
	
	public static void removeChestEditingPlayer(UUID id){
		ChestEditingPlayer player = getChestEditingPlayer(id);
		if (player != null){
			editingPlayers.remove(id);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(RCDonations.prefix + ChatColor.RED + "This command can only be used by a Player.");
			return true;
		}
		Player player = (Player) sender;
		if (editingPlayers.containsKey(player.getUniqueId())){
			player.sendMessage(RCDonations.prefix + ChatColor.GOLD + "You are currently in the middle of editing "+editingPlayers.get(player.getUniqueId()).getKitName()+". Use /stopEditing to stop AND UNDO all the progress on the kit you are editing.");
		}
		if (!player.hasPermission(cmd.getPermission())){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "You aren't allowed to use this command.");
			return true;
		}
		if (cmd.getName().equals("createKit")){
			return handleCreateKit(player, args);
		}else if (cmd.getName().equals("editKit")){
			return handleEditKit(player, args);
		}else if (cmd.getName().equals("setIcon")){
			return handleSetIcon(player, args);
		}else if (cmd.getName().equals("removeKit")){
			return handleRemoveKit(player, args);
		}else if (cmd.getName().equals("getFlag")){
			return handleGetFlag(player, args);
		}else if (cmd.getName().equals("stopEditing")){
			return handleStopEditing(player, args);
		}else{
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "MrBlobman forgot to handle a registered command. Please let him know which one.");
			return true;
		}
	}
	
	public boolean handleCreateKit(Player player, String[] args){
		if (args.length != 4){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Invalid argument count. Expected /createKit <kitName> <displayName> <cooldownInSec> <permissionToUse>");
			return true;
		}
		String kitName = args[0];
		String kitDisplayName = args[1];
		String kitCooldownStr = args[2];
		String kitPermission = args[3];
		Long kitCooldown;
		if (KitManager.kitExists(kitName)){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Kit already exists. Use /editKit "+kitName);
			return true;
		}
		try{
			kitCooldown = Long.parseLong(kitCooldownStr)*1000;
		}catch (NumberFormatException e){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + kitCooldownStr + " is not a valid number. No decimal places or non-number characters allowed.");
			return true;
		}
		Kit kit = new Kit();
		kit.setCooldown(kitCooldown);
		kit.setRawName(kitName);
		kit.setDisplayName(kitDisplayName);
		kit.setPermission(kitPermission);
		KitEditingManager.editingPlayers.put(player.getUniqueId(), new ChestEditingPlayer(player, kit));
		player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "The next chest you break will be turned into a kit. /getFlag to set up some more advanced items.");
		return true;
	}

	public boolean handleEditKit(Player player, String[] args){
		if (args.length != 1){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Invalid argument count. Expected /editKit <kitName>");
			return true;
		}
		String kitName = args[0];
		if (!KitManager.kitExists(kitName)){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Kit doesn't exists. Use /createKit "+kitName+" <cooldownInSec> <permissionToUse>");
			return true;
		}
		if (!KitManager.kitIsLoaded(kitName)){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Kit is not loaded. Trying to load it now...");
			if (KitManager.loadKit(new File(RCDonations.plugin.getDataFolder()+File.separator+"Kits"+File.separator, kitName+".yml"))){
				player.sendMessage(RCDonations.prefix+ChatColor.GREEN+"Successfully loaded kit. Continue.");
			}else{
				player.sendMessage(RCDonations.prefix+ChatColor.RED+"Could not load kit. Check console for more details.");
				return true;
			}
		}
		Kit kit = KitManager.getByName(kitName);
		KitEditingManager.editingPlayers.put(player.getUniqueId(), new ChestEditingPlayer(player, kit));
		player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "The next chest you break will be turned into a kit. /getFlag to set up some more advanced items.");
		return true;
	}
	
	public boolean handleSetIcon(Player player, String[] args){
		if (args.length != 1){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Invalid argument count. Expected /setIcon <kitName>");
			return true;
		}
		String kitName = args[0];
		if (KitManager.kitExists(kitName)){
			File kitFile = new File(RCDonations.plugin.getDataFolder() + File.separator + "Kits" + File.separator, kitName+".yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(kitFile);
			ItemStack item = player.getItemInHand();
			config.set("Icon", ItemParser.buildConfigSection(item));
			if (KitManager.kitIsLoaded(kitName)){
				Kit loadedKit = KitManager.getByName(kitName);
				loadedKit.setKitIcon(item);
			}
			try {
				config.save(kitFile);
			} catch (IOException e) {
				player.sendMessage(RCDonations.prefix + ChatColor.RED + "The " + kitName + " icon could not be saved as an error occured saving to "+kitFile.getPath());
				return true;
			}
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "The icon for " + kitName + " was set the the item currently in your hand.");
			return true;
		}
		player.sendMessage(RCDonations.prefix + ChatColor.RED + "The " + kitName + " does not exist. Cannot set item.");
		return true;
		
	}
	
	public boolean handleRemoveKit(final Player player, String[] args){
		if (args.length != 1){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Invalid argument count. Expected /removeKit <kitName>");
			return true;
		}
		final String kitName = args[0];
		if (!KitManager.kitExists(kitName)){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Kit doesn't exist!");
			return true;
		}
		player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "You are about to completly remove the " + kitName + " kit. This action is NOT REVERSABLE.");
		player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "Enter Yes or Y in chat to confirm this action. After 30s or if you enter any other text in chat the action will be canceled.");
		new Confirmation(player, new Runnable(){
			@Override
			public void run(){
				KitManager.unloadKit(KitManager.getByName(kitName));
				player.sendMessage(RCDonations.prefix + ChatColor.GREEN + kitName + (KitManager.removeKit(kitName) ? " successfully removed." : " could not be removed."));
			}
		}, new Runnable(){
			@Override
			public void run(){
				player.sendMessage(RCDonations.prefix + ChatColor.GREEN + kitName + " removal aborted.");
			}
		}, 600L);
		return true;
	}
	
	public boolean handleGetFlag(Player player, String[] args){
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("timedependent")){
				player.getInventory().addItem(Flags.TIME_DEPENDENT_ITEM.FLAG);
				player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "You were given the Time Dependent flag if you inventory was not full.");
				return true;
			}else if (args[0].equalsIgnoreCase("random")){
				player.getInventory().addItem(Flags.RANDOM_ITEM.FLAG);
				player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "You were given the Random flag if you inventory was not full.");
				return true;
			}
		}
		Player.Spigot spigotPlayer = player.spigot();
		BaseComponent[] prefix = TextComponent.fromLegacyText(RCDonations.prefix + ChatColor.GREEN + " Click the flag you wish to recieve.");
		TextComponent timeFlag = new TextComponent("      - Time Dependent Flag");
		timeFlag.setColor(ChatColor.YELLOW);
		timeFlag.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/getFlag time"));
		timeFlag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to recieve the flag!").color(ChatColor.GREEN).create()));
		TextComponent randomFlag = new TextComponent("      - Random Flag");
		randomFlag.setColor(ChatColor.YELLOW);
		randomFlag.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/getFlag random"));
		randomFlag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to recieve the flag!").color(ChatColor.GREEN).create()));
		spigotPlayer.sendMessage(prefix);
		spigotPlayer.sendMessage(timeFlag);
		spigotPlayer.sendMessage(randomFlag);
		return true;
	}
	
	public boolean handleStopEditing(final Player player, String[] args){
		ChestEditingPlayer chestEditingPlayer = KitEditingManager.getChestEditingPlayer(player.getUniqueId());
		if (chestEditingPlayer != null){
			final String kitName = chestEditingPlayer.getKitName();
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Are you sure you want to stop your current progress on " + kitName + "?");
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Once confirmed this action is irreversible. Enter YES or Y in chat to confirm.");
			new Confirmation(player, new Runnable(){
				@Override
				public void run() {
					player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "You are no longer editing " + kitName + ". All changes are lost and have not taken effect.");
					KitEditingManager.removeChestEditingPlayer(player.getUniqueId());
				}
			}, new Runnable(){
				@Override
				public void run(){
					player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "Request to stop editing " + kitName + " has been aborted. Please continue editng.");
				}
			}, 600L);
			return true;
		}
		player.sendMessage(RCDonations.prefix + ChatColor.RED + "You are not editing any kits.");
		return true;
	}
}
