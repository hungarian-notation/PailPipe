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
package hafnium.bukkit.pail.util.sign;

import hafnium.bukkit.pail.util.BlockLocation;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Attachable;

public class SignLocation extends BlockLocation {
	private BlockFace attachedFace;

	public SignLocation(String world, int x, int y, int z, BlockFace face) {
		super(world, x, y, z);
		this.attachedFace = face;
	}

	/**
	 * Constructs a SignLocation at the specified position in the specified
	 * world with the specified orientation.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param attached
	 */
	public SignLocation(World world, int x, int y, int z, BlockFace attached) {
		this(world.getName(), x, y, z, attached);
	}

	/**
	 * Constructs a SignLocation for the specified location with the specified
	 * orientation.
	 * 
	 * @param location
	 * @param attached
	 */
	public SignLocation(Location location, BlockFace attached) {
		this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), attached);
	}

	/**
	 * Constructs a SignLocation for the given Sign BlockState.
	 * 
	 * @param sign
	 */
	public SignLocation(Sign sign) {
		this(sign.getBlock().getLocation(), ((Attachable) (sign.getData())).getAttachedFace());
	}

	public SignLocation(SignChangeEvent event) {
		this((Sign) event.getBlock().getState());
	}

	public SignLocation() {
		this("world", 0, 0, 0, null);
	}

	/**
	 * @param attachedFace
	 *            the attachedFace to set
	 */
	public void setAttachedFace(BlockFace attachedFace) {
		this.attachedFace = attachedFace;
	}

	/**
	 * Get the face the sign is attached to.
	 * 
	 * @return
	 */
	public BlockFace getAttachedFace() {
		return this.attachedFace;
	}

	public BlockLocation getHostBlockLocation() {
		return this.getRelative(1, 0, 0);
	}

	/**
	 * Convert this SignLocation into a Location object.
	 * 
	 * @return
	 */
	@Override
	public Location asLocation() {
		return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ());
	}

	/**
	 * Gets a Location relative to this SignLocation.
	 * 
	 * This method returns null for non-wallsigns.
	 * 
	 * Backwise is how many in, edgewise is how many across. The block the sign
	 * is attached to would be one backwise, zero edgewise. A sign to the left
	 * of this location would be zero backwise and negative one edgewise.
	 * 
	 * @param backwise
	 * @param edgewise
	 * @return
	 */
	public BlockLocation getRelative(int backwise, int edgewise, int topwise) {
		int xoff;
		int zoff;

		switch (this.getAttachedFace()) {
		case EAST:
			xoff = -edgewise;
			zoff = -backwise;
			break;
		case WEST:
			xoff = edgewise;
			zoff = backwise;
			break;
		case SOUTH:
			xoff = backwise;
			zoff = -edgewise;
			break;
		case NORTH:
			xoff = -backwise;
			zoff = edgewise;
			break;
		default:
			return null;
		}

		return new BlockLocation(this.getWorldName(), this.getX() + xoff, this.getY() + topwise, this.getZ() + zoff);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.attachedFace == null) ? 0 : this.attachedFace.hashCode());
		return result;
	}

	public Sign getSign() {
		return (Sign) this.getBlock().getState();
	}
}
