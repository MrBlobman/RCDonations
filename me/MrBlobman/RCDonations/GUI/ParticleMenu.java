package me.MrBlobman.RCDonations.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.MrBlobman.RCDonations.RCDonations;
import me.MrBlobman.RCDonations.Particles.Cube;
import me.MrBlobman.RCDonations.Particles.Halo;
import me.MrBlobman.RCDonations.Particles.ParticleEffect;
import me.MrBlobman.RCDonations.Particles.ParticleIcon;
import me.MrBlobman.RCDonations.Particles.WaveTrail;
import me.MrBlobman.RCDonations.Utils.InventoryUtils;
import me.MrBlobman.RCDonations.Utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ParticleMenu implements Menu{

	public ItemStack NO_PARTICLE;
	
	private Inventory inv;
	private ItemStack particle;
	private ParticleEffect effect;
	private Player player;
	private Map<Integer, ParticleIcon> masterList;
	
	public ParticleMenu(Player player){
		this.NO_PARTICLE = new ItemBuilder().setMaterial(Material.STAINED_GLASS).setName(ChatColor.YELLOW+"Your Current Effect").setLore(ChatColor.GREEN+"Particle: Not picked yet.", ChatColor.GREEN+"Shape: Not picked yet.").build();
		this.player = player;
		buildCurrentParticle();
		buildInventory();
	}
	
	private void buildInventory(){
		//The first row in the inv is reserved for me
		this.masterList = RCDonations.particleManager.getMasterList();
		inv = Bukkit.createInventory(null, InventoryUtils.getInvSize(RCDonations.particleManager.getMasterListMax()+1), ChatColor.DARK_RED + "[" + ChatColor.AQUA + "Particles" + ChatColor.DARK_RED + "]");
		for (Integer slot : masterList.keySet()){
			if (player.hasPermission(masterList.get(slot).getPermission())){
				inv.setItem(slot, masterList.get(slot).getItem());
			}
		}
		inv.setItem(1, particle);
		inv.setItem(3, new ItemBuilder().setMaterial(Material.STAINED_GLASS).setName("&7Clear").setLore("&8Resets your current", "&8particle and if saved", "&8results in no effect.").build());
		inv.setItem(5, new ItemBuilder().setMaterial(Material.MOB_SPAWNER).setName("&bCube").setLore("&3Sets the shape of your", "&3selected particle to", "&3a cube of particles", "&3around you.").build());
		inv.setItem(6, new ItemBuilder().setMaterial(Material.BEACON).setName("&eHalo").setLore("&6Sets the shape of your", "&6selected particle to", "&6circling ring above", "&6your head.").build());
		inv.setItem(7, new ItemBuilder().setMaterial(Material.WATER_BUCKET).setName("&aWaveTrail").setLore("&2Set the shape of your", "&2selected particle to", "&2folow your every step", "&2while oscilating.").build());
	}
	
	private void buildCurrentParticle(){
		if (RCDonations.particleManager.hasRegisteredParticle(player)){
			this.effect = RCDonations.particleManager.getRegisteredEffect(player);
			this.particle = new ItemBuilder().setMaterial(Material.STAINED_GLASS).setData((short) 4).setName(ChatColor.YELLOW+"Your Current Effect").setLore(ChatColor.GREEN+"Particle: "+effect.getParticle().getName(), ChatColor.GREEN+"Shape: "+effect.getShape().getName()).build();
		}else{
			this.effect = new ParticleEffect();
			this.effect.setEntity(player);
			this.particle = NO_PARTICLE;
		}
	}
	
	private void registerParticle(){
		if (effect != null){
			if (effect.getShape() != null && effect.getParticle() != null){
				RCDonations.particleManager.unregisterEffect(player.getUniqueId());
				if (effect.getShape().playOnClock()){
					RCDonations.particleManager.registerOnClockEffect(player.getUniqueId(), effect);
				}else{
					RCDonations.particleManager.registerOnMoveEffect(player.getUniqueId(), effect);
				}
			}else if (effect.getShape() == null && effect.getParticle() == null){
				RCDonations.particleManager.unregisterEffect(player.getUniqueId());
			}
		}
	}
	
	private void updateParticle(){
		ItemMeta meta = this.particle.getItemMeta();
		List<String> lore;
		if (meta.hasLore()){
			lore = meta.getLore();
		}else{
			lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN+"Particle: Not picked yet.");
			lore.add(ChatColor.GREEN+"Shape: Not picked yet.");
		}
		if (effect.getParticle() != null){
			if (effect.getParticle().getName() != null){
				lore.set(0, ChatColor.GREEN+"Particle: "+ChatColor.translateAlternateColorCodes('&', effect.getParticle().getName()));
			}
		}
		if (effect.getShape() != null){
			if (effect.getShape().getName() != null){
				lore.set(1, ChatColor.GREEN+"Shape: "+effect.getShape().getName());
			}
		}
		meta.setLore(lore);
		this.particle.setItemMeta(meta);
		if (this.effect.getShape() != null && this.effect.getParticle() != null){
			if (this.particle.getDurability() != 5){
				this.particle.setDurability((short) 5);
			}
		}else if (this.effect.getShape() == null && this.effect.getParticle() == null){
			if (this.particle.getDurability() != 0){
				this.particle.setDurability((short) 0);
			}
		}else{
			if (this.particle.getDurability() != 14){
				this.particle.setDurability((short) 14);
			}
		}
		this.inv.setItem(1, particle);
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
		throw new IllegalArgumentException("This menu is an end menu.");
	}

	@Override
	public boolean isEnd() {
		return true;
	}

	@Override
	public boolean handleClick(int slotInt) throws IllegalStateException {
		Integer slot = slotInt;
		switch(slot){
		case 1:	//Finished editing
			registerParticle();
			player.closeInventory();
			return true;
		case 3: //Clear
			this.effect = new ParticleEffect();
			this.effect.setEntity(player);
			this.particle = NO_PARTICLE;
			this.inv.setItem(1, NO_PARTICLE);
			player.updateInventory();
			return true;
		case 5: //Aura shape
			this.effect.setShape(new Cube());
			updateParticle();
			return true;
		case 6: //Halo shape
			this.effect.setShape(new Halo());
			updateParticle();
			return true;
		case 7: //WaveTrail shape
			this.effect.setShape(new WaveTrail());
			updateParticle();
			return true;
		}
		if (masterList.containsKey(slot)){
			this.effect.setParticle(masterList.get(slot).getParticle());
			updateParticle();
			return true;
		}
		throw new IllegalStateException("Cannot handle a click in the location "+slotInt);
	}

	@Override
	public void close() {
	}

}
