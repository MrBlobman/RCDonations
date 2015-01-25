package me.MrBlobman.RCDonations.KitBuilding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import me.MrBlobman.RCDonations.KitEditingManager;
import me.MrBlobman.RCDonations.KitManager;
import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.Items.Flags;
import me.MrBlobman.RCDonations.Items.Kit;
import me.MrBlobman.RCDonations.Utils.DateUtils;
import me.MrBlobman.RCDonations.Utils.Day;
import me.MrBlobman.RCDonations.Utils.ItemBuilder;
import me.MrBlobman.RCDonations.Utils.ItemParser;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestEditingPlayer implements Listener {
	
	private Player player;
	private String kitName;
	private Kit kit;
	private Queue<Step> steps = new LinkedList<Step>();
	private Runnable runWhenStepDone;
	//No saves to this file until is finished. An interruption to the creation process results in a loss of built data
	private FileConfiguration kitConfig;
	private File kitFile;
	private ItemStack kitChest;
	
	public ChestEditingPlayer(Player player, Kit kit){
		this.player = player;
		this.kitName = kit.getRawName();
		this.kit = kit;
		this.kitFile = new File(RCDonations.plugin.getDataFolder()+File.separator+"Kits"+File.separator, kitName+".yml");
		Bukkit.getPluginManager().registerEvents(this, RCDonations.plugin);
		this.runWhenStepDone = new Runnable(){
			@Override
			public void run() {
				callNextStep();
			}
		};
		if (!kitFile.exists()){
			kitFile.getParentFile().mkdirs();
			try {
				kitFile.createNewFile();
			} catch (IOException e) {
				setFinishedEditing();
			}
		}
		this.kitConfig = YamlConfiguration.loadConfiguration(kitFile);
		this.kitChest = new ItemBuilder().setMaterial(Material.CHEST).setName("&c&l"+this.kitName).setLore("&bPlace this to create", "&bor edit the kit.", "&bUse the &e/getFlag &bcommand", "&bto recieve usable flags.", "&bBreak this chest when finished.").build();
		player.getInventory().addItem(this.kitChest);
		setExistingKitValues();
	}
	
	private void setExistingKitValues(){
		this.kitConfig.set("Cooldown", DateUtils.longTimeToString(kit.getCooldown()));
		this.kitConfig.set("Name", kit.getDisplayName());
		this.kitConfig.set("Permission", kit.getPermission());
	}
	
	public void setFinishedEditing(){
		//Unregister all listeners
		BlockBreakEvent.getHandlerList().unregister(this);
		BlockPlaceEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
		try{
			this.kitConfig.save(kitFile);
		}catch(IOException e){
			player.sendMessage(RCDonations.prefix + "Could not create "+kitFile.getName()+". Stopping the build process. Any changes were lost.");
			player.sendMessage(RCDonations.prefix + e.getMessage());
			RCDonations.plugin.getLogger().severe("[RCDonations] Could not create "+kitFile.getName());
			RCDonations.plugin.getLogger().severe(e.getMessage());
		}
		KitManager.registerKit(kit);
		KitEditingManager.removeChestEditingPlayer(player.getUniqueId());
		player.sendMessage(RCDonations.prefix + ChatColor.GREEN + "If not yet set or you wish to override this kits icon you can use /setIcon <kitName> to set the item in your hand as the kit icon.");
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		if (event.getPlayer() == this.player){
			setFinishedEditing();
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if (event.getPlayer() == this.player){
			if (event.getItemInHand().equals(kitChest)){
				if (event.getBlock().getState() instanceof Chest){
					Chest chest = (Chest) event.getBlock().getState();
					Inventory inv = chest.getInventory();
					if (this.kitConfig.contains("Items")){
						ConfigurationSection items = this.kitConfig.getConfigurationSection("Items");
						for (String key : items.getKeys(false)){
							if (items.contains(key+".Type")){
								String type = items.getString(key+".Type");
								try{
									Integer slotId = Integer.parseInt(key);
									if (type.equals("NORMAL")){
										inv.setItem(slotId, items.getItemStack(key+".Item"));
									}else if (type.equals("RANDOM")){
										inv.setItem(slotId, Flags.RANDOM_ITEM.FLAG);
									}else if (type.equals("TIME_DEPENDENT")){
										inv.setItem(slotId, Flags.TIME_DEPENDENT_ITEM.FLAG);
									}else{
										player.sendMessage(RCDonations.prefix + ChatColor.RED+"Found unknown type "+type+" in "+this.kitFile.getName()+" at Items."+key+".Type. Skipping...");
									}
								}catch(Exception e){
									player.sendMessage(RCDonations.prefix + ChatColor.RED+"ERROR: "+e.getMessage());
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if (event.getPlayer() == this.player){
			if (event.getBlock().getState() instanceof Chest){
				//This broken chest needs to be made into a kit
				Chest chest = (Chest) event.getBlock().getState();
				int slot = -1;
				for (ItemStack item : chest.getInventory().getContents()){
					slot++;
					String key = "Items."+String.valueOf(slot);
					if (item != null){
						if (item.equals(Flags.RANDOM_ITEM.FLAG)){
							//RandomItem, add this step
							if (this.kitConfig.contains(key+".Type")){
								if (this.kitConfig.getString(key+".Type").equalsIgnoreCase("RANDOM")){
									Map<String, ItemStack> items = new LinkedHashMap<String, ItemStack>();
									for (String amt : this.kitConfig.getConfigurationSection(key+".Item").getKeys(false)){
										items.put(amt, ItemParser.parseItem(this.kitConfig.getConfigurationSection(key+".Items."+amt)));
									}
									this.steps.offer(new RandomItemStep(this.player, key, items));
									continue;
								}
							}
							//Start fresh, no previous random setup
							this.steps.offer(new RandomItemStep(this.player, key));
						}else if (item.equals(Flags.TIME_DEPENDENT_ITEM.FLAG)){
							//TimeDependent, add this step
							if (this.kitConfig.contains(key+".Type")){
								if (this.kitConfig.getString(key+".Type").equalsIgnoreCase("TIME_DEPENDENT")){
									List<ItemStack> items = new ArrayList<ItemStack>();
									for (Day day : Day.values()){
										if (this.kitConfig.contains(key+".Item."+day.toString())){
											items.add(ItemParser.parseItem(this.kitConfig.getConfigurationSection(key+".Item."+day.toString())));
										}else{
											//Spacer
											items.add(null);
										}
									}
									this.steps.offer(new TimeDependentStep(this.player, key, items));
									continue;
								}
							}
							//Start fresh, no previous time dependent setup
							this.steps.offer(new TimeDependentStep(this.player, key));
						}else{
							//NormalItem
							this.kitConfig.set(key+".Item", ItemParser.buildConfigSection(item));
							this.kitConfig.set(key+".Type", "NORMAL");
						}
					}else{
						this.kitConfig.set("Items."+String.valueOf(slot), null);
					}
				}
				//Dont drop anything
				event.setCancelled(true);
				chest.getBlockInventory().clear();
				event.getBlock().setType(Material.AIR);
				if (!this.steps.isEmpty()){
					this.steps.peek().start(this.runWhenStepDone);
				}else{
					setFinishedEditing();
				}
			}
		}
	}
	
	public void callNextStep(){
		if (!this.steps.isEmpty()){
			Step lastCompletedStep = this.steps.poll();
			this.kitConfig.set(lastCompletedStep.getDataKey(), lastCompletedStep.getKitItem().asWriteable());
		}if (!this.steps.isEmpty()){
			this.steps.peek().start(this.runWhenStepDone);
			return;
		}setFinishedEditing();
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public String getKitName(){
		return this.kitName;
	}
}
