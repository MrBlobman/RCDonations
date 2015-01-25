package me.MrBlobman.RCDonations.Particles;

import org.bukkit.entity.Entity;

public class ParticleEffect {
	private Particle particle;
	private Shape shape;
	private Entity entity;
	
	public ParticleEffect(Particle particle, Shape shape, Entity entity){
		this.particle = particle;
		this.shape = shape;
		this.entity = entity;
	}
	
	/**
	 * Used for building a new ParticleEffect
	 */
	public ParticleEffect(){ }
	
	public void playOut(){
		shape.playOut(particle, entity.getLocation());
	}
	
	public Particle getParticle() {
		return particle;
	}

	public void setParticle(Particle particle) {
		this.particle = particle;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public void setEntity(Entity entity){
		this.entity = entity;
	}
}
