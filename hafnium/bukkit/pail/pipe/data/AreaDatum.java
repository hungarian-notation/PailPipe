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
package hafnium.bukkit.pail.pipe.data;

import hafnium.bukkit.pail.pipe.drawing.DrawingOperation;
import hafnium.bukkit.pail.util.BlockLocation;
import hafnium.bukkit.pail.util.Region;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.Attachable;

public class AreaDatum implements Datum {
	private BlockLocation loc;
	private int xsize, ysize, zsize;
	private byte[] block;
	private byte[] data;
	private String name, owner;

	public AreaDatum() {

	}

	public AreaDatum(Region region) {
		this.loadFromRegion(region);
	}

	private void loadFromRegion(Region region) {
		this.loc = region.getMinimumBound();
		this.xsize = region.getXDim();
		this.ysize = region.getYDim();
		this.zsize = region.getZDim();

		this.block = new byte[this.xsize * this.ysize * this.zsize];
		this.data = new byte[this.block.length];

		World world = region.getWorld();

		int x, y, z;
		Block b;

		for (int i = 0; i < this.block.length; i++) {
			x = (i / (this.ysize * this.zsize)) % this.xsize + this.loc.getX();
			y = (i / (this.zsize)) % this.ysize + this.loc.getY();
			z = (i) % this.zsize + this.loc.getZ();

			b = world.getBlockAt(x, y, z);

			this.block[i] = (byte) b.getTypeId();
			this.data[i] = b.getData();
		}
	}

	/**
	 * Gets the name of this area. Useful when loading legacy areas. This data
	 * will ONLY be available when the area is loaded from a legacy format.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the owner of this area. Useful when loading legacy areas. This data
	 * will ONLY be available when the area is loaded from a legacy format.
	 * 
	 * @return the owner
	 */
	public String getOwner() {
		return this.owner;
	}

	@Override
	public void writeTo(File f) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));

		// These two strings are leftovers from the 3.x format. They are no
		// longer used.

		out.writeUTF("");
		out.writeUTF("");

		out.writeUTF(this.loc.getWorldName());

		out.writeInt(this.loc.getX());
		out.writeInt(this.loc.getY());
		out.writeInt(this.loc.getZ());

		out.writeInt(this.xsize);
		out.writeInt(this.ysize);
		out.writeInt(this.zsize);

		out.write(this.block);
		out.write(this.data);

		out.close();
	}

	@Override
	public void readFrom(File f) throws IOException {

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));

		// These two strings are leftovers from the 3.x format. They are no
		// longer used.

		this.name = in.readUTF();
		this.owner = in.readUTF();

		String worldName = in.readUTF();

		int x1 = in.readInt();
		int y1 = in.readInt();
		int z1 = in.readInt();

		this.loc = new BlockLocation(worldName, x1, y1, z1);

		this.xsize = in.readInt();
		this.ysize = in.readInt();
		this.zsize = in.readInt();

		this.block = new byte[this.xsize * this.ysize * this.zsize];
		this.data = new byte[this.xsize * this.ysize * this.zsize];

		in.read(this.block);
		in.read(this.data);

		in.close();
	}

	public Region getRegion() {
		return new Region(this.loc, this.loc.add(this.xsize - 1, this.ysize - 1, this.zsize - 1));
	}

	public AreaDatum redefine() {
		return new AreaDatum(this.getRegion());
	}

	public void restore() {
		hafnium.bukkit.pail.pipe.PailPipe.getInstance().getDrawingManager()
				.draw(new AreaDraw(this.loc, this.block, this.data, this.xsize, this.ysize, this.zsize));
	}

	public static class AreaDraw implements DrawingOperation {
		private final BlockLocation origin;
		private final byte[] blocks;
		private final byte[] data;

		private final int xsize, ysize, zsize;

		private final int maxPos;

		private final LinkedList<Integer> toRevisit;
		int pos;

		private boolean done;

		public AreaDraw(BlockLocation origin, byte[] blocks, byte[] data, int xsize, int ysize, int zsize) {
			this.origin = origin;

			this.blocks = blocks;
			this.data = data;

			this.xsize = xsize;
			this.ysize = ysize;
			this.zsize = zsize;

			this.maxPos = blocks.length;
			this.pos = 0;

			this.toRevisit = new LinkedList<Integer>();

			this.done = this.maxPos == 0;
		}

		@Override
		public void drawBlock() {
			if (this.pos < this.maxPos) {
				Material mat = Material.getMaterial(this.blocks[this.pos]);

				if (Attachable.class.isAssignableFrom(mat.getData()))
					this.toRevisit.add(this.pos);
				else
					this.draw(this.pos, mat, this.data[this.pos]);

				this.pos++;
			} else if (!this.toRevisit.isEmpty())
				this.draw(this.toRevisit.pop());
			else
				this.done = true;
		}

		private void draw(int pos) {
			this.draw(pos, Material.getMaterial(this.blocks[pos]), this.data[pos]);
		}

		private void draw(int pos, Material mat, byte data) {
			int xd = (pos / (this.ysize * this.zsize)) % this.xsize;
			int yd = (pos / (this.zsize)) % this.ysize;
			int zd = (pos) % this.zsize;

			Block block = this.origin.getWorld().getBlockAt(xd + this.origin.getX(), yd + this.origin.getY(), zd + this.origin.getZ());

			if (block.getType() != mat)
				block.setType(mat);
			if (block.getData() != data)
				block.setData(data);
		}

		@Override
		public boolean isDone() {
			return this.done;
		}
	}
}
