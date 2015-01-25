package me.MrBlobman.RCDonations.Particles;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Cube implements Shape{

	private static final Vector[] PATH;
	
	static{
		Vector[] aura = new Vector[18];
		int slot = 0;
		for (int x = -1; x <= 1; x++){
			for (int z = -1; z <= 1; z++){
				for (int y = 0; y <= 1; y++){
					aura[slot] = new Vector(x, y, z);
					slot++;
				}
			}
		}
		PATH = aura;
	}
	
	public Cube(){	}
	
	@Override
	public Vector[] getPath() {
		return PATH;
	}

	@Override
	public String getName() {
		return "Aura";
	}

	@Override
	public void playOut(Particle particle, Location location) {
		for (Vector point : PATH){
			particle.playOut(location.add(point));
			location.subtract(point);
		}
	}

	@Override
	public boolean playOnClock(){
		return true;
	}
}
