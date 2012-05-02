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

import hafnium.bukkit.pail.util.ItemForm;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BrushFill implements DrawingOperation {
	private final World world;
	private final DrawingBrush brush;
	private final ItemForm mat;
	private boolean done;

	public BrushFill(World world, DrawingBrush brush, ItemForm material) {
		this.world = world;
		this.brush = brush;

		this.mat = material;

		this.done = false;
	}

	@Override
	public void drawBlock() {
		if (this.done)
			return;

		Vector loc = this.brush.next();

		if (loc == null) {
			this.done = true;
			return;
		}

		if (!inWorld(loc, this.world)) {
			this.drawBlock();
			return;
		}

		Block block = this.world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

		if (this.mat.isMaterialDefined())
			block.setTypeId(this.mat.getMaterial());

		if (this.mat.isDataDefined())
			block.setData(this.mat.getData());
	}

	private static boolean inWorld(Vector loc, World w) {
		return (loc.getY() < w.getMaxHeight()) && (loc.getBlockY() >= 0);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}

}
