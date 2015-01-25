package me.MrBlobman.RCDonations.Particles;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface Shape {
	
	public Vector[] getPath();
	public String getName();
	public void playOut(Particle particle, Location location);
	public boolean playOnClock();
	
}
