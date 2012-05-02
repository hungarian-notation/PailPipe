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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an immutable worldless XYZ triplet.
 * 
 * @author Chris
 * 
 */
public class Coordinate {
	private int x, y, z;

	public Coordinate() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Coordinate(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
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

	public BlockLocation getAsRelativeTo(BlockLocation loc) {
		return new BlockLocation(loc.getWorldName(), loc.getX() + this.getX(), loc.getY() + this.getY(), loc.getZ() + this.getZ());
	}

	public static Coordinate getRelative(BlockLocation reference, BlockLocation location) {
		return new Coordinate(location.getX() - reference.getX(), location.getY() - reference.getY(), location.getZ() - reference.getZ());
	}

	public String toSimpleString() {
		return this.getX() + " " + this.getY() + " " + this.getZ();
	}

	@Override
	public String toString() {
		return "( " + this.getX() + ", " + this.getY() + ", " + this.getZ() + " )";
	}

	public String toFormattedString() {
		return "^g(^n " + this.getX() + "^g, ^n" + this.getY() + "^g, ^n" + this.getZ() + " ^g)";
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
		if (this.getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		if (this.z != other.z)
			return false;
		return true;
	}

	private static Pattern coord = Pattern.compile("^([-]?[0-9]+) ([-]?[0-9]+) ([-]?[0-9]+)$");

	public static Coordinate parse(String def) {
		Matcher parser = coord.matcher(def);

		if (!parser.matches())
			return null;

		try {
			return new Coordinate(Integer.parseInt(parser.group(1)), Integer.parseInt(parser.group(2)), Integer.parseInt(parser.group(3)));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
