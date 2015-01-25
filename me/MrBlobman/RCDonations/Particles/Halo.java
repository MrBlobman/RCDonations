package me.MrBlobman.RCDonations.Particles;

import me.MrBlobman.RCDonations.RCDonations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Halo implements Shape{
	private static final double[] UNIT_CIRCLE_FIRST_QUAD = new double[]{1/6d, 1/4d, 1/3d, 1/2d};
	private final Vector[] PATH;
	private BukkitTask task;
	
	public Halo(){
		Vector[] halo = new Vector[16];
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 4; j++){
				halo[j*4+i] = new Vector(0.5*Math.sin(Math.PI*(UNIT_CIRCLE_FIRST_QUAD[j] + i*0.5)), 2.1, 0.5*Math.cos(Math.PI*(UNIT_CIRCLE_FIRST_QUAD[j] + i*0.5)));
			}
		}
		PATH = halo;
	}
	
	@Override
	public Vector[] getPath() {
		return PATH;
	}

	@Override
	public String getName() {
		return "Halo";
	}

	@Override
	public void playOut(final Particle particle, final Location center) {
		if (task == null){
			task = Bukkit.getScheduler().runTaskTimer(RCDonations.plugin, new Runnable(){
				int step = 0;
				@Override
				public void run(){
					if (step < 16){
						particle.playOut(center.add(PATH[step]));
						center.subtract(PATH[step]);
						step++;
					}else{
						task.cancel();
						task = null;
					}
				}
			}, 0L, 1L);
		}
	}
	
	@Override
	public boolean playOnClock(){
		return true;
	}
}
