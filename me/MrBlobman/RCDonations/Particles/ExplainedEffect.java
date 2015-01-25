package me.MrBlobman.RCDonations.Particles;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Effect;

public enum ExplainedEffect {
	
	POTION_BREAK(Effect.POTION_BREAK, "Visual effect of a \nsplash potion breaking. \nNeeds potion data value \nas additional info."),
	ENDER_SIGNAL(Effect.ENDER_SIGNAL, "An ender eye signal \na visual effect."),
	MOBSPAWNER_FLAMES(Effect.MOBSPAWNER_FLAMES, "The flames seen \non a mobspawner; \na visual effect."),
	FIREWORKS_SPARK(Effect.FIREWORKS_SPARK, "The spark that comes \noff a firework trail."),
	CRIT(Effect.CRIT, "Critical hit particles."),
	MAGIC_CRIT(Effect.MAGIC_CRIT, "Blue critical hit \nparticles."),
	POTION_SWIRL(Effect.POTION_SWIRL, "Multicolored potion effect \nparticles."),
	POTION_SWIRL_TRANSPARENT(Effect.POTION_SWIRL_TRANSPARENT, "Multicolored potion effect \nparticles that are \nslightly transparent."),
	SPELL(Effect.SPELL, "A puff of white \npotion swirls."),
	INSTANT_SPELL(Effect.INSTANT_SPELL, "A puff of white \nstars."),
	WITCH_MAGIC(Effect.WITCH_MAGIC, "A puff of purple \nparticles."),
	NOTE(Effect.NOTE, "The note that appears \nabove note blocks."),
	PORTAL(Effect.PORTAL, "The particles shown \nat nether portals."),
	FLYING_GLYPH(Effect.FLYING_GLYPH, "The symbols that \nfly towards the \nenchantment table."),
	FLAME(Effect.FLAME, "Fire particles."),
	LAVA_POP(Effect.LAVA_POP, "The particles that \npop out of lava."),
	FOOTSTEP(Effect.FOOTSTEP, "A small gray square."),
	SPLASH(Effect.SPLASH, "Water particles."),
	PARTICLE_SMOKE(Effect.PARTICLE_SMOKE, "Smoke particles."),
	EXPLOSION_HUGE(Effect.EXPLOSION_HUGE, "The biggest explosion \nparticle effect."),
	EXPLOSION_LARGE(Effect.EXPLOSION_LARGE, "A larger version of \nthe explode particle."),
	EXPLOSION(Effect.EXPLOSION, "Explosion particles."),
	VOID_FOG(Effect.VOID_FOG, "Small gray particles."),
	SMALL_SMOKE(Effect.SMALL_SMOKE, "Small gray particles."),
	CLOUD(Effect.CLOUD, "A puff of white smoke."),
	COLOURED_DUST(Effect.COLOURED_DUST, "Multicolored dust particles."),
	SNOWBALL_BREAK(Effect.SNOWBALL_BREAK, "Snowball breaking."),
	WATERDRIP(Effect.WATERDRIP, "The water drip particle \nthat appears on \nblocks under water."),
	LAVADRIP(Effect.LAVADRIP, "The lava drip particle \nthat appears on blocks \nunder lava."),
	SNOW_SHOVEL(Effect.SNOW_SHOVEL, "White particles."),
	SLIME(Effect.SLIME, "The particle shown \nwhen a slime jumps."),
	HEART(Effect.HEART, "The particle that \nappears when breading \nanimals."),
	VILLAGER_THUNDERCLOUD(Effect.VILLAGER_THUNDERCLOUD, "The particle that \nappears when hitting a \nvillager."),
	HAPPY_VILLAGER(Effect.HAPPY_VILLAGER, "The particle that appears \nwhen trading with a \nvillager."),
	LARGE_SMOKE(Effect.LARGE_SMOKE, "The smoke particles that \nappears on blazes, minecarts \nwith furnaces and fire."),
	ITEM_BREAK(Effect.ITEM_BREAK, "The particles generated when \na tool breaks. \nThis particle requires a Material \nso that the client can select \nthe correct texture.", "material"),
	TILE_BREAK(Effect.TILE_BREAK, "The particles generated while \nbreaking a block. \nThis particle requires a Material \nand data value so that the \nclient can select the \ncorrect texture.", "materialdata"),
	TILE_DUST(Effect.TILE_DUST, "The particles generated while \nsprinting a block. \nThis particle requires a Material \nand data value so that the \nclient can select the \ncorrect texture.", "materialdata");
	
	private Effect effect;
	private String explanation;
	private String parseAs;
	
	ExplainedEffect(Effect effect, String explained){
		this.effect = effect;
		this.explanation = explained;
	}
	
	ExplainedEffect(Effect effect, String explained, String parseAs){
		this(effect, explained);
		this.parseAs = parseAs;
	}
	
	public Effect getEffect(){
		return this.effect;
	}
	
	public String getExplanation(){
		return this.explanation;
	}

	/**
	 * This is used to format the info as a multilined string as \n is not supported
	 * @return the string described above
	 */
	public String getItemFormattedExplanation(ChatColor color){
		String[] parts = this.explanation.split("\n");
		String info = "display:{Name:";
		if (parts.length >= 1){
			// \"Name\"
			info = info + "\"" + color + parts[0] + "\"";
			if (parts.length >=2){
				info = info + ", Lore:[";
				for (int i = 1; i< parts.length; i++) {
					info = info + "\"" + color + parts[i] + "\", ";
				}
				info = info.substring(0, info.length()-2);
				info = info + "]";
			}
			info = info + "}";
		}
		return "{id:1,tag:{"+info+"}}";
	}
	
	public String getParseAs(){
		return parseAs;
	}
}
