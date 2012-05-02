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

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * This is an alternative to bukkit's location class that uses (usually) less
 * memory and can survive the unloading of a world.
 * 
 * BlockLocation instances are immutable.
 * 
 * @author Chris
 * 
 */
public class BlockLocation {

	private String worldName;
	private int x, y, z;

	public BlockLocation(Location l) {
		this(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public BlockLocation(World world, int x, int y, int z) {
		this(world.getName(), x, y, z);
	}

	public BlockLocation(String worldName, int x, int y, int z) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockLocation() {
		this("world", 0, 0, 0);
	}

	public BlockLocation(BlockLocation other) {
		this(other.worldName, other.x, other.y, other.z);
	}

	/**
	 * Get the world the block is in.
	 * 
	 * @return
	 */
	public World getWorld() {
		return Bukkit.getServer().getWorld(this.getWorldName());
	}

	/**
	 * @return the worldName
	 */
	public String getWorldName() {
		return this.worldName;
	}

	/**
	 * @param worldName
	 *            the worldName to set
	 */
	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return this.z;
	}

	/**
	 * @param z
	 *            the z to set
	 */
	public void setZ(int z) {
		this.z = z;
	}

	public Chunk getChunk() {
		return this.getWorld().getChunkAt(this.getX(), this.getZ());
	}

	public Block getBlock() {
		return this.getWorld().getBlockAt(this.getX(), this.getY(), this.getZ());
	}

	public Location asLocation() {
		return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.worldName == null) ? 0 : this.worldName.hashCode());
		result = prime * result + this.x;
		result = prime * result + this.y;
		result = prime * result + this.z;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BlockLocation))
			return false;
		BlockLocation other = (BlockLocation) obj;
		if (this.worldName == null) {
			if (other.worldName != null)
				return false;
		} else if (!this.worldName.equals(other.worldName))
			return false;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		if (this.z != other.z)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.worldName + "@" + toWINT(this.x) + "." + toWINT(this.y) + "." + toWINT(this.z);
	}

	private static String toWINT(int val) {
		return (val < 0 ? "n" : "") + Math.abs(val);
	}

	/**
	 * 
	 * @param block
	 *            The block to check.
	 * @return true if this BlockLocation defines the specified block.
	 */
	public boolean locates(Block block) {
		return this.locates(block.getLocation());
	}

	/**
	 * 
	 * @param loc
	 *            The location to check.
	 * @return true if this BlockLocation defines the block that contains the
	 *         specified location.
	 */
	public boolean locates(Location loc) {
		return this.getWorldName().equals(loc.getWorld().getName()) && (this.getX() == loc.getBlockX()) && (this.getY() == loc.getBlockY())
				&& (this.getZ() == loc.getBlockZ());
	}

	public Coordinate asCoordinate() {
		return new Coordinate(this.x, this.y, this.z);
	}

	public Vector asVector() {
		return new Vector(this.x, this.y, this.z);
	}

	public boolean isComplete() {
		return this.worldName != null;
	}

	public boolean isLoaded() {
		return this.getChunk().isLoaded();
	}

	public Location getHorizontalCenter() {
		return this.asLocation().add(0.5, 0, 0.5);
	}

	public Location getAbsoluteCenter() {
		return this.asLocation().add(0.5, 0.5, 0.5);
	}

	public BlockLocation add(int x, int y, int z) {
		BlockLocation nloc = new BlockLocation(this);

		nloc.setX(nloc.getX() + x);
		nloc.setY(nloc.getY() + y);
		nloc.setZ(nloc.getZ() + z);

		return nloc;
	}
}
