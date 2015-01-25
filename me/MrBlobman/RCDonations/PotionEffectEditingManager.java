package me.MrBlobman.RCDonations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.MrBlobman.RCDonations.PotionEffects.PotionIcon;
import me.MrBlobman.RCDonations.PotionEffects.PotionIconBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectEditingManager implements CommandExecutor{

	private Map<Player, PotionIconBuilder> builders = new ConcurrentHashMap<Player, PotionIconBuilder>();
	
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
		
		if (cmd.getName().equals("addPotionEffect")){
			return handleAddPotionEffect(player, args);
		}else if (cmd.getName().equals("removePotionEffect")){
			return handleRemovePotionEffect(player, args);
		}else{
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "MrBlobman forgot to handle a registered command. Please let him know which one.");
			return true;
		}
	}
	
	private boolean handleAddPotionEffect(Player player, String[] args){
		if (!builders.containsKey(player)){
			if (args.length != 3){
				player.sendMessage(RCDonations.prefix + ChatColor.RED + "Invalid Arguments. Expected /addPotionEffect <permissionToUse> <potionStrength> <isAmbient> where permissionToUse is the permission a player must have to access the effect and potionStrength is the strength of the effect (>=1) and isAmbient is a boolean (true/false) stating if the particles should be transparent.");
				return true;
			}
			if (player.getItemInHand() == null){
				player.sendMessage(RCDonations.prefix + ChatColor.RED + "You must be holding the item that you wish to use as the icon in the PotionEffects Menu.");
				return true;
			}
			int strength;
			try{
				strength = Integer.valueOf(args[1]);
			} catch (NumberFormatException e){
				player.sendMessage(RCDonations.prefix + ChatColor.RED + args[1] + " is not a number. Argument 2 must be the number value for the effect strength.");
				return true;
			}
			boolean ambient;
			try{
				ambient = Boolean.valueOf(args[2]);
			} catch (NumberFormatException e){
				player.sendMessage(RCDonations.prefix + ChatColor.RED + args[2] + " is not a boolean. Argument 3 must be true or false stating if the particles for the potion effect should be transparent or not.");
				return true;
			}
			PotionIconBuilder builder = new PotionIconBuilder();
			builder.setItem(player.getItemInHand());
			builder.setPermission(args[0]);
			builder.setStrength(strength);
			builder.setAmbient(ambient);
			builder.setStep(1);
			builders.put(player, builder);
			sendStep1(player);
			return true;
		}else{
			PotionIconBuilder builder = builders.get(player);
			if (args.length == 1){
				int step = builder.getStep();
				switch(step){
				case 1:
					Integer index;
					try{
						index = Integer.valueOf(args[0]);
					} catch (NumberFormatException e){
						player.sendMessage(RCDonations.prefix + ChatColor.RED + args[0] + " is not a number. Argument 1 must be the index.");
						sendStep1(player);
						return true;
					}
					builder.setIndex(index);
					builder.setStep(2);
					sendStep2(player);
					return true;
				case 2:
					PotionEffectType type = PotionEffectType.getByName(args[0]);
					if (type == null){
						player.sendMessage(RCDonations.prefix + ChatColor.RED + args[0] + " is not a valid PotionEffect name. Please select another.");
						sendStep2(player);
						return true;
					}
					builder.setType(type);
					PotionIcon icon = builder.build();
					RCDonations.potionManager.writeMasterList(builder.getIndex(), icon);
					player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " Successfully added the potion!");
					return true;
				}
			}
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "ERROR: Something went wrong with the ParticleIconBuilder. Your step did not follow expected flow. Please let MrBlobman know!");
			builders.remove(player);
			return true;
		}
	}
	
	private void sendStep1(Player player){
		player.sendMessage(RCDonations.prefix + ChatColor.YELLOW + "Below is a diagram of an inventory. Click the position you wish to put the PotionIcon you are building.");
		player.sendMessage(ChatColor.GOLD+"NOTE: "+ChatColor.YELLOW+"Numbers in red indicate that a PotionIcon already exists in the menu slot and choosing that slot will replace it.");
		Player.Spigot spigotPlayer = player.spigot();
		for (int i = 0; i < 6; i++){
			ComponentBuilder line = new ComponentBuilder("|").color(ChatColor.GRAY);
			for (int j = 0; j < 9; j++){
				boolean hasRegisteredEffect = RCDonations.potionManager.indexHasRegisteredEffect((i*9+j));
				line.append(" "+(i*9+j<=9 ? "0"+(i*9+j) : (i*9+j))+" ");
				line.color(hasRegisteredEffect ? ChatColor.RED : ChatColor.GREEN);
				line.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hasRegisteredEffect ? "Will replace an existing effect." : "Will add a new effect.").color(ChatColor.YELLOW).create()));
				line.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/addPotionEffect "+(i*9+j)));
				line.append("|");
				line.color(ChatColor.GRAY);
			}
			spigotPlayer.sendMessage(line.create());
		}
	}
	
	private void sendStep2(Player player){
		player.sendMessage(RCDonations.prefix + ChatColor.YELLOW + "Click the effect you wish to bind to the PotionIcon you are currently working on.");
		Player.Spigot spigotPlayer = player.spigot();
		boolean gray = true;
		ComponentBuilder line = null;
		PotionEffectType[] effects = PotionEffectType.values();
		for (int i = 0; i<effects.length-1; i++){
			if (i%3 == 0){
				line = new ComponentBuilder(String.format("%-17s",effects[i+1].getName()));
				line.color(gray ? ChatColor.GRAY : ChatColor.DARK_GRAY);
				line.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/addPotionEffect "+effects[i+1].getName()));
			}else if (i%3 == 1){
				line.append(String.format("%-17s",effects[i+1].getName()));
				line.color(gray ? ChatColor.GRAY : ChatColor.DARK_GRAY);
				line.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/addPotionEffect "+effects[i+1].getName()));
			}else {
				line.append(effects[i+1].getName());
				line.color(gray ? ChatColor.GRAY : ChatColor.DARK_GRAY);
				line.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/addPotionEffect "+effects[i+1].getName()));
			}
			if (i%3 == 2 || i == effects.length-1){
				spigotPlayer.sendMessage(line.create());
				gray = !gray;
				line = null;
			}
		}
	}
	
	public boolean handleRemovePotionEffect(Player player, String[] args){
		if (args.length != 1){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Invalid arguments. Expected /removePotionEffect <index>. Where index is a number from 1-54 where the PotionIcon is located in the Potion Effect Menu.");
			return true;
		}
		Integer index;
		try{
			index = Integer.parseInt(args[0]);
		}catch(NumberFormatException e){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + args[0] + " is not a number. Please given a number from 1-54.");
			return true;
		}
		if (index < 1 || index > 54){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + index + " is not a number between 1-54.");
			return true;
		}
		PotionIcon iconRemoved = RCDonations.potionManager.removeMasterList(index);
		if (iconRemoved != null){
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " Successfully removed the potion: ");
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " Effect: "+iconRemoved.getInfo().getEffect().getName() + iconRemoved.getInfo().getStrength());
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " ItemType: "+iconRemoved.getItem().getType());
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " Index: "+index);
			return true;
		}else{
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Potion does not exist at the index given ("+index+").");
			return true;
		}
	}
}
