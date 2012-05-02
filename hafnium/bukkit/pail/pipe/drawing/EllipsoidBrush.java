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

import org.bukkit.util.Vector;

public class EllipsoidBrush implements DrawingBrush {
	private final int xdim, ydim, zdim;
	private final int x1, x2, y1, y2, z1, z2;
	private final double rx, rz, ry, rx2, rz2, ry2, cx, cz, cy;

	private int pos;

	public EllipsoidBrush(Vector a, Vector b) {
		Vector min = Vector.getMinimum(a, b);
		Vector max = Vector.getMaximum(a, b);

		this.x1 = min.getBlockX();
		this.x2 = max.getBlockX();
		this.y1 = min.getBlockY();
		this.y2 = max.getBlockY();
		this.z1 = min.getBlockZ();
		this.z2 = max.getBlockZ();

		this.xdim = this.x2 - this.x1 + 1;
		this.ydim = this.y2 - this.y1 + 1;
		this.zdim = this.z2 - this.z1 + 1;

		// find axis lengths
		this.rx = (this.x2 - this.x1 + 1) / 2d;
		this.rz = (this.z2 - this.z1 + 1) / 2d;
		this.ry = (this.y2 - this.y1 + 1) / 2d;

		this.rx2 = 1 / (this.rx * this.rx);
		this.rz2 = 1 / (this.rz * this.rz);
		this.ry2 = 1 / (this.ry * this.ry);

		// find center points
		this.cx = (this.x2 + this.x1) / 2d;
		this.cz = (this.z2 + this.z1) / 2d;
		this.cy = (this.y2 + this.y1) / 2d;

		this.pos = 0;
	}

	@Override
	public Vector next() {
		if (this.pos >= this.xdim * this.ydim * this.zdim)
			return null;

		int x = (this.pos / (this.ydim * this.zdim)) % this.xdim + this.x1;
		int y = (this.pos / (this.zdim)) % this.ydim + this.y1;
		int z = (this.pos) % this.zdim + this.z1;

		this.pos++;

		double dx = (x - this.cx);
		double dy = (y - this.cy);
		double dz = (z - this.cz);

		if ((dx * dx) * this.rx2 + (dz * dz) * this.rz2 + (dy * dy) * this.ry2 <= 1)
			return new Vector(x, y, z);

		return this.next();
	}

	@Override
	public int estimateSize() {
		return (int) ((4d * Math.PI * this.rx * this.ry * this.rz) / 3d);
	}
}
