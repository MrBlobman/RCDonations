package me.MrBlobman.RCDonations;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.MrBlobman.RCDonations.Particles.ExplainedEffect;
import me.MrBlobman.RCDonations.Particles.ParticleIcon;
import me.MrBlobman.RCDonations.Particles.ParticleIconBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticleEditingManager implements CommandExecutor{

	public static Map<UUID, ParticleIconBuilder> builders = new ConcurrentHashMap<UUID, ParticleIconBuilder>();
	
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
		if (cmd.getName().equals("addParticle")){
			return handleAddParticle(player, args);
		}else if (cmd.getName().equals("removeParticle")){
			return handleRemoveParticle(player, args);
		}else{
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "MrBlobman forgot to handle a registered command. Please let him know which one.");
			return true;
		}
	}

	private boolean handleAddParticle(Player player, String[] args){
		if (!builders.containsKey(player.getUniqueId())){
			String name;
			Integer index;
			String permission;
			String extraData = null;
			if (args.length == 3){
				name = args[0];
				try{
					index = Integer.parseInt(args[1]);
					if (index < 10 || index > 54){
						player.sendMessage(RCDonations.prefix + ChatColor.RED + index + " must be between 10 and 54 inclusive.");
						return true;
					}
				}catch(NumberFormatException e){
					player.sendMessage(RCDonations.prefix + ChatColor.RED + args[1] + " could not be turned into a number.");
					return true;
				}
				permission = args[2];
			}else{
				player.sendMessage(RCDonations.prefix + ChatColor.RED +"You need to give atleast 3 arguments. The name of the particle effect. Index of the particle in the menu (10-54). Permission to use the particle.");
				return true;
			}
			if (args.length == 4){
				extraData = args[3];
			}
			ParticleIconBuilder builder = new ParticleIconBuilder();
			builder.setName(name);
			builder.setIndex(index);
			builder.setPermission(permission);
			if (extraData != null){ builder.setDataInput(extraData);}
			builders.put(player.getUniqueId(), builder);
			sendEffectInfo(player);
			return true;
		}else{
			//Player already did the above, we can skip to the good part
			ItemStack icon = player.getItemInHand();
			if (icon != null){
				if (!icon.getType().equals(Material.AIR)){
					ParticleIconBuilder builder = builders.get(player.getUniqueId());
					builder.setItem(icon);
					if (args.length >= 1){
						player.sendMessage(args[0]);
						builder.setEffect(Effect.valueOf(args[0]));
						if (args.length >= 2){
							builder.parseInput(args[1]);
						}
					}else{
						player.sendMessage(RCDonations.prefix + ChatColor.RED +"Second step in the build process expects an effect and parse as string.");
						return true;
					}
					if (!RCDonations.particleManager.writeMasterList(builder.getIndex(), builder.build())){
						//write failed!!!
						player.sendMessage(RCDonations.prefix + ChatColor.RED +"I regret to inform you that something went terribly wrong in writing the particle to Particles.yml. We have lost it D:");
						return true;
					}else{
						builders.remove(player.getUniqueId());
						player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "Particle added!");
						return true;
					}
				}
			}
			sendEffectInfo(player);
			player.sendMessage(RCDonations.prefix + ChatColor.RED +"You must be holding the item you want to use as an icon when you click!");
			return true;
		}
	}
	
	private void sendEffectInfo(Player player){
		Player.Spigot spigotPlayer = player.spigot();
		BaseComponent[] infoLine = TextComponent.fromLegacyText(RCDonations.prefix + ChatColor.GREEN + " Click the effect you wish to use while holding to icon you wish to use.");
		spigotPlayer.sendMessage(infoLine);
		boolean yellow = true;
		ExplainedEffect[] effects = ExplainedEffect.values();
		for (int i = 1; i <= 19; i++){
			ComponentBuilder builder = new ComponentBuilder("");
			for (int j = 1; j <= 2; j++){
				if (i*2+j < effects.length){
					ExplainedEffect effect = effects[i*2+j];
					builder.append(String.format(j==1 ? "%-35s" : "%s", effect.getEffect().toString()));
					builder.color(yellow ? ChatColor.YELLOW : ChatColor.GOLD);
					builder.event(new ClickEvent(Action.RUN_COMMAND, "/addParticle "+effect.getEffect().toString()+" "+(effect.getParseAs()!=null ? effect.getParseAs() : "none")));
					builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(effect.getItemFormattedExplanation(ChatColor.GREEN)).create()));
				}else{
					break;
				}
			}
			spigotPlayer.sendMessage(builder.create());
			yellow = !yellow;
		}
	}
	
	private boolean handleRemoveParticle(Player player, String[] args){
		if (args.length != 1){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Invalid arguments. Expected /removeParticle <index>. Where index is a number from 10-54 where the particle is located in the Particle Menu.");
			return true;
		}
		Integer index;
		try{
			index = Integer.parseInt(args[0]);
		}catch(NumberFormatException e){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + args[0] + " is not a number. Please given a number from 10-54.");
			return true;
		}
		if (index < 10 || index > 54){
			player.sendMessage(RCDonations.prefix + ChatColor.RED + index + " is not a number between 10-54.");
			return true;
		}
		ParticleIcon iconRemoved = RCDonations.particleManager.removeMasterList(index);
		if (iconRemoved != null){
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " Successfully removed the particle: ");
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " Name: "+iconRemoved.getParticle().getName());
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " Effect: "+iconRemoved.getParticle().getEffect());
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " ItemType: "+iconRemoved.getItem().getType());
			player.sendMessage(RCDonations.prefix + ChatColor.GREEN + " Index: "+index);
			return true;
		}else{
			player.sendMessage(RCDonations.prefix + ChatColor.RED + "Particle does not exist at the index given. "+index);
			return true;
		}
	}
}
