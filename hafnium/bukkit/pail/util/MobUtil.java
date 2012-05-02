/*
 * Copyright (c) 2012 Chris Bode
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * No affiliation with PailPipe or any related projects is claimed.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package hafnium.bukkit.pail.util;

import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.material.Colorable;

public class MobUtil {
	public static boolean isAged(Class<? extends Entity> clazz) {
		return Animals.class.isAssignableFrom(clazz);
	}

	public static void setAge(LivingEntity entity, Age age) {
		if (entity instanceof Animals)
			age.setOn(((Animals) entity));
	}

	public static boolean isAgitatable(Class<? extends Entity> clazz) {
		return (clazz == Wolf.class) || (clazz == PigZombie.class) || (clazz == Spider.class);
	}

	public static void setDemeanor(LivingEntity entity, Demeanor demeanor) {
		if (demeanor == Demeanor.CALM)
			return;

		if (entity instanceof Wolf)
			((Wolf) entity).setAngry(true);

		if (entity instanceof Spider)
			((Spider) entity).setTarget(getNearestPlayer(entity.getLocation(), 10));

		if (entity instanceof PigZombie)
			((PigZombie) entity).setAngry(true);
	}

	public static boolean isColorable(Class<? extends Entity> class1) {
		return Colorable.class.isAssignableFrom(class1);
	}

	public static void color(LivingEntity entity, DyeColor color) {
		if (entity instanceof Colorable)
			((Colorable) entity).setColor(color);
	}

	public static boolean isSizeable(Class<? extends Entity> clazz) {
		return Slime.class.isAssignableFrom(clazz);
	}

	public static void setSize(LivingEntity entity, int size) {
		if (entity instanceof Slime)
			((Slime) entity).setSize(size);
	}

	public static LivingEntity spawnNicely(CreatureType creature, BlockLocation location) {
		Location bloc = location.asLocation().add(0.5, 0.0, 0.5);

		Player target = getNearestPlayer(bloc, -1);

		if (target != null) {
			Location tloc = target.getLocation();
			double angle = Math.toDegrees(Math.atan2(tloc.getX() - bloc.getX(), tloc.getY() - bloc.getY()));
			bloc.setPitch((float) angle);

		}

		LivingEntity spawned = bloc.getWorld().spawnCreature(bloc, creature);
		spawned.teleport(bloc);

		bloc.getWorld().playEffect(bloc, org.bukkit.Effect.SMOKE, 4);

		return spawned;
	}

	private static Player getNearestPlayer(Location loc, int max) {
		List<Player> players = loc.getWorld().getPlayers();

		Player nearest = null;
		double nearestDistanceSqr = (max == -1 ? -1 : max * max);

		for (Player p : players) {
			double distSqr = p.getLocation().distanceSquared(loc);
			if ((distSqr < nearestDistanceSqr) || (nearestDistanceSqr == -1)) {
				nearestDistanceSqr = distSqr;
				nearest = p;
			}
		}

		return nearest;
	}

	public static class MobSignature {
		private CreatureType type;
		private Age age;
		private Demeanor demeanor;
		private DyeColor color;
		private int size;

		public MobSignature() {

		}

		public MobSignature(CreatureType type, Age age, Demeanor demeanor, DyeColor color, int size) {
			this.type = type;
			this.age = age;
			this.demeanor = demeanor;
			this.color = color;
			this.size = size;
		}

		/**
		 * @return the type
		 */
		public CreatureType getType() {
			return this.type;
		}

		/**
		 * @param type
		 *            the type to set
		 */
		public void setType(CreatureType type) {
			this.type = type;
		}

		/**
		 * @return the age
		 */
		public Age getAge() {
			return this.age;
		}

		/**
		 * @param age
		 *            the age to set
		 */
		public void setAge(Age age) {
			this.age = age;
		}

		/**
		 * @return the demeanor
		 */
		public Demeanor getDemeanor() {
			return this.demeanor;
		}

		/**
		 * @param demeanor
		 *            the demeanor to set
		 */
		public void setDemeanor(Demeanor demeanor) {
			this.demeanor = demeanor;
		}

		/**
		 * @return the color
		 */
		public DyeColor getColor() {
			return this.color;
		}

		/**
		 * @param color
		 *            the color to set
		 */
		public void setColor(DyeColor color) {
			this.color = color;
		}

		/**
		 * @return the size
		 */
		public int getSize() {
			return this.size;
		}

		/**
		 * @param size
		 *            the size to set
		 */
		public void setSize(int size) {
			this.size = size;
		}

		public void spawnAt(BlockLocation loc) {
			LivingEntity entity = MobUtil.spawnNicely(this.type, loc);
			MobUtil.setAge(entity, this.age);
			MobUtil.setDemeanor(entity, this.demeanor);
			MobUtil.color(entity, this.color);
			MobUtil.setSize(entity, this.size);
		}
	}

	public enum Demeanor implements AliasedEnum {
		ANGRY("aggressive", "a", "mad", "evil"), CALM("peaceful", "friendly", "nice");

		private final String[] aliases;

		private Demeanor(String... aliases) {
			this.aliases = aliases;
		}

		@Override
		public String[] getAliases() {
			return this.aliases;
		}
	}

	public enum Age implements AliasedEnum {
		BABY("young", "child"), ADULT("old", "grown");

		private final String[] aliases;

		private Age(String... aliases) {
			this.aliases = aliases;
		}

		@Override
		public String[] getAliases() {
			return this.aliases;
		}

		public void setOn(Animals animal) {
			if (this == BABY)
				animal.setBaby();
			else
				animal.setAdult();
		}
	}
}
