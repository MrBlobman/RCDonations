package me.MrBlobman.RCDonations.GUI;

import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.Utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MainMenu implements Menu{
	/* Inventory Model
	 * | 00 | 01 | 02 | 03 | 04 | 05 | 06 | 07 | 08 |
	 * | 09 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 |
	 * | 18 | 19 | 20 | 21 | 22 | 23 | 24 | 25 | 26 |
	 * | 27 | 28 | 29 | 30 | 31 | 32 | 33 | 34 | 35 |
	 * | 36 | 37 | 38 | 39 | 40 | 41 | 42 | 43 | 44 |
	 * | 45 | 46 | 47 | 48 | 49 | 50 | 51 | 52 | 53 |
	 */

	private Inventory inv;
	private Player player;
	
	MainMenu(Player player){
		this.player = player;
		buildInventory();
	}
	
	@Override
	public void open() {
		Bukkit.getScheduler().runTask(RCDonations.plugin, new Runnable(){

			@Override
			public void run() {
				player.openInventory(inv);
			}
			
		});
	}
	
	private void buildInventory(){
		this.inv = Bukkit.createInventory(null, 54, RCDonations.prefix);
		this.inv.setItem(11, new ItemBuilder().setMaterial(Material.DIAMOND_CHESTPLATE).setName(ChatColor.YELLOW+"Kits").setLore(ChatColor.GREEN+"Click here to", ChatColor.GREEN+"access all of the", ChatColor.GREEN+"kits you have access to!").build());
		this.inv.setItem(13, new ItemBuilder().setMaterial(Material.EXP_BOTTLE).setName(ChatColor.YELLOW+"Particles").setLore(ChatColor.GREEN+"Click here to", ChatColor.GREEN+"access all of the", ChatColor.GREEN+"particles you have access to!").build());
		this.inv.setItem(15, new ItemBuilder().setMaterial(Material.BREWING_STAND_ITEM).setName(ChatColor.YELLOW+"Potion Effects").setLore(ChatColor.GREEN+"Click here to", ChatColor.GREEN+"access all of the potion", ChatColor.GREEN+"effects you have access to!").build());
		Configuration config = RCDonations.plugin.getConfig();
		if (config.contains("TS3Server")){
			this.inv.setItem(27, new ItemBuilder().setMaterial(Material.NOTE_BLOCK).setName(ChatColor.YELLOW+"TeamSpeak3 Server").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"link to connect", ChatColor.GREEN+"to the TS3", ChatColor.GREEN+"server!").build());
		}
		if (config.contains("Website")){
			this.inv.setItem(28, new ItemBuilder().setMaterial(Material.MAP).setName(ChatColor.YELLOW+"Website").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"link to open the", ChatColor.GREEN+"website.").build());
		}
		if (config.contains("Forums")){
			this.inv.setItem(29, new ItemBuilder().setMaterial(Material.SIGN).setName(ChatColor.YELLOW+"Forums").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"link to open the", ChatColor.GREEN+"forums.").build());
		}
		if (config.contains("Store")){
			this.inv.setItem(30, new ItemBuilder().setMaterial(Material.GOLD_INGOT).setName(ChatColor.YELLOW+"Store").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"link to visit the", ChatColor.GREEN+"store.").build());
		}
		if (config.contains("Twitter")){
			this.inv.setItem(31, new ItemBuilder().setMaterial(Material.EGG).setName(ChatColor.YELLOW+"Twitter").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"link to visit the", ChatColor.GREEN+"server's twitter page.").build());
		}
		if (config.contains("Facebook")){
			this.inv.setItem(32, new ItemBuilder().setMaterial(Material.SKULL_ITEM).setData((short) 3).setName(ChatColor.YELLOW+"Facebook").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"link to visit the", ChatColor.GREEN+"server's facebook page.").build());
		}
		if (config.contains("YouTube")){
			this.inv.setItem(33, new ItemBuilder().setMaterial(Material.BED).setName(ChatColor.YELLOW+"YouTube").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"link to visit the", ChatColor.GREEN+"server's youtube page.").build());
		}
		if (config.contains("Poll")){
			this.inv.setItem(34, new ItemBuilder().setMaterial(Material.BOOK_AND_QUILL).setName(ChatColor.YELLOW+"Poll").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"link to visit the", ChatColor.GREEN+"polling site and", ChatColor.GREEN+"give you opinion.").build());
		}
		if (config.contains("Vote")){
			this.inv.setItem(35, new ItemBuilder().setMaterial(Material.DIAMOND).setName(ChatColor.YELLOW+"Vote").setLore(ChatColor.GREEN+"Click to get the", ChatColor.GREEN+"links to visit the", ChatColor.GREEN+"voting pages.").build());
		}
	}

	@Override
	public Menu getNext(int clickedSlot) throws IllegalArgumentException{
		switch (clickedSlot){
		case 11:
			return new KitMenu(player);
		case 13:
			return new ParticleMenu(player);
		case 15:
			return new PotionMenu(player);
		default:
			throw new IllegalArgumentException("No handle for the given slot.");
		}
	}

	@Override
	public boolean isEnd() {
		return false;
	}

	@Override
	public boolean handleClick(int slot) throws IllegalStateException{
		if (slot < 0 || slot > 53){
			throw new IllegalStateException("Slot must be in the inventory.");
		}
		if (inv.getItem(slot) != null){
			Configuration config = RCDonations.plugin.getConfig();
			switch (slot){
			case 27:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click the link below to get the TeamSpeak3 ip in your text area so you can copy and paste it into TeamSpeak3.").color(ChatColor.GREEN).create());
				player.spigot().sendMessage(new ComponentBuilder("    - "+config.getString("TS3Server")).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, config.getString("TS3Server"))).create());
				return true;
			case 28:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click the link below to visit the website.").color(ChatColor.GREEN).create());
				player.spigot().sendMessage(new ComponentBuilder("    - "+config.getString("Website")).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("Website"))).create());
				return true;
			case 29:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click the link below to visit the forums.").color(ChatColor.GREEN).create());
				player.spigot().sendMessage(new ComponentBuilder("    - "+config.getString("Forums")).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("Forums"))).create());
				return true;
			case 30:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click the link below to visit store.").color(ChatColor.GREEN).create());
				player.spigot().sendMessage(new ComponentBuilder("    - "+config.getString("Store")).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("Store"))).create());
				return true;
			case 31:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click the link below to visit the server's Twitter page.").color(ChatColor.GREEN).create());
				player.spigot().sendMessage(new ComponentBuilder("    - "+config.getString("Twitter")).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("Twitter"))).create());
				return true;
			case 32:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click the link below to visit the server's Facebook page.").color(ChatColor.GREEN).create());
				player.spigot().sendMessage(new ComponentBuilder("    - "+config.getString("Facebook")).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("Facebook"))).create());
				return true;
			case 33:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click the link below to visit the server's YouTube channel.").color(ChatColor.GREEN).create());
				player.spigot().sendMessage(new ComponentBuilder("    - "+config.getString("YouTube")).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("YouTube"))).create());
				return true;
			case 34:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click the link below to vote on the current poll.").color(ChatColor.GREEN).create());
				player.spigot().sendMessage(new ComponentBuilder("    - "+config.getString("Poll")).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("Poll"))).create());
				return true;
			case 35:
				player.spigot().sendMessage(new ComponentBuilder(RCDonations.prefix).append("Click each link below to vote for the server.").color(ChatColor.GREEN).create());
				for (String link : config.getStringList("Vote")){
					player.spigot().sendMessage(new ComponentBuilder("    - "+link).color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.OPEN_URL, link)).create());
				}
				return true;
			default:
				throw new IllegalStateException("Could not handle given slot.");
			}
		}else{
			throw new IllegalStateException("Slot given is empty. No handle available.");
		}
	}

	@Override
	public void close() {
	}
}
