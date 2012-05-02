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
package hafnium.bukkit.pail.pipe.drawing;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class CuboidBrush implements DrawingBrush {
	private final Vector base;
	private final int xdim, ydim, zdim;

	public CuboidBrush(Vector a, Vector b) {
		Vector min = Vector.getMinimum(a, b);
		Vector max = Vector.getMaximum(a, b);

		this.base = min;
		this.xdim = max.getBlockX() - min.getBlockX() + 1;
		this.ydim = max.getBlockY() - min.getBlockY() + 1;
		this.zdim = max.getBlockZ() - min.getBlockZ() + 1;
	}

	int pos = 0;

	private int maxPos() {
		return this.xdim * this.ydim * this.zdim - 1;
	}

	private Vector getFor(int pos) {
		Vector v = this.base.clone();
		return v.add(getOffset(pos, this.xdim, this.ydim, this.zdim));
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10 * 10 * 10; i++)
			System.out.println(getOffset(i, 10, 10, 10));
	}

	private static BlockVector getOffset(int pos, int xdim, int ydim, int zdim) {
		int x = (pos / (ydim * zdim)) % xdim;
		int y = (pos / (zdim)) % ydim;
		int z = (pos) % zdim;
		return new BlockVector(x, y, z);
	}

	@Override
	public Vector next() {
		if (this.pos > this.maxPos())
			return null;
		return this.getFor(this.pos++);
	}

	@Override
	public int estimateSize() {
		return this.xdim * this.ydim * this.zdim;
	}
}
