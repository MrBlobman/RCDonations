package me.MrBlobman.RCDonations.Particles;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class WaveTrail implements Shape{

	private static Vector[] PATH;
	
	static{
		Vector[] trail = new Vector[30];
		for (int i = 0; i < 30; i++){
			trail[i] = new Vector(0d, Math.sin(2*i*Math.PI/30d), 0d);
		}
		PATH = trail;
	}
	
	private int step = 0;
	
	public WaveTrail(){ }
	
	@Override
	public Vector[] getPath() {
		return PATH;
	}

	@Override
	public String getName() {
		return "Wave Trail";
	}

	@Override
	public void playOut(Particle particle, Location location) {
		particle.playOut(location.add(PATH[step++]));
		if (step >= PATH.length){
			step = 0;
		}
	}

	@Override
	public boolean playOnClock(){
		return false;
	}
}
