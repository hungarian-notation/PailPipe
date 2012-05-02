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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class Region {
	private BlockLocation boundA, boundB;

	int x1, y1, z1, x2, y2, z2;
	boolean complete = false;

	public Region() {

	}

	public Region(BlockLocation a, BlockLocation b) {
		this.setBoundA(a);
		this.setBoundB(b);
	}

	private void updateState() {
		if ((this.boundA == null) || !this.boundA.isComplete() || (this.boundB == null) || !this.boundB.isComplete()) {
			this.complete = false;
			return;
		}

		this.x1 = Math.min(this.boundA.getX(), this.boundB.getX());
		this.y1 = Math.min(this.boundA.getY(), this.boundB.getY());
		this.z1 = Math.min(this.boundA.getZ(), this.boundB.getZ());

		this.x2 = Math.max(this.boundA.getX(), this.boundB.getX());
		this.y2 = Math.max(this.boundA.getY(), this.boundB.getY());
		this.z2 = Math.max(this.boundA.getZ(), this.boundB.getZ());
		this.complete = true;
	}

	/**
	 * @return the boundA
	 */
	public BlockLocation getBoundA() {
		return this.boundA;
	}

	/**
	 * @param boundA
	 *            the boundA to set
	 */
	public void setBoundA(BlockLocation boundA) {
		this.boundA = boundA;
		this.updateState();
	}

	/**
	 * @return the boundB
	 */
	public BlockLocation getBoundB() {
		return this.boundB;
	}

	/**
	 * @param boundB
	 *            the boundB to set
	 */
	public void setBoundB(BlockLocation boundB) {
		this.boundB = boundB;
		this.updateState();
	}

	public BlockLocation getMinimumBound() {
		return new BlockLocation(this.getWorld(), this.x1, this.y1, this.z1);
	}

	public int getXDim() {
		return this.x2 - this.x1 + 1;
	}

	public int getYDim() {
		return this.y2 - this.y1 + 1;
	}

	public int getZDim() {
		return this.z2 - this.z1 + 1;
	}

	public BlockVector getMinimumCorner() {
		return new BlockVector(this.x1, this.y1, this.z1);
	}

	public int getVolume() {
		return this.getXDim() * this.getYDim() * this.getZDim();
	}

	public boolean isComplete() {
		return this.complete;
	}

	private void assertIsComplete() {
		if (!this.isComplete())
			throw new IllegalStateException("That operation requires a complete region. Some fields are not set.");
	}

	@Override
	public String toString() {
		return "Region[ from " + this.boundA.toString() + " to " + this.boundB.toString() + " ]";
	}

	public boolean contains(int x, int y, int z) {
		this.assertIsComplete();
		return (x >= this.x1) && (x <= this.x2) && (y >= this.y1) && (y <= this.y2) && (z >= this.z1) && (z <= this.z2);
	}

	public boolean contains(BlockLocation loc) {
		return this.getWorld().getName().equals(loc.getWorld().getName()) && this.contains(loc.getX(), loc.getY(), loc.getZ());
	}

	public boolean contains(org.bukkit.Location loc) {
		return this.getWorld().equals(loc.getWorld()) && this.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public boolean contains(Entity e) {
		return this.contains(e.getLocation());
	}

	private World world;

	public World getWorld() {
		this.assertIsComplete();
		if (this.world == null)
			this.world = this.boundA.getWorld();
		return this.world;
	}

	public Chunk[] getChunks() {
		this.assertIsComplete();

		int minCX = (int) Math.floor(this.x1 / 16d);
		int maxCX = (int) Math.floor(this.x2 / 16d);
		int minCZ = (int) Math.floor(this.z1 / 16d);
		int maxCZ = (int) Math.floor(this.z2 / 16d);

		int rw = maxCX - minCX + 1;
		int rh = maxCZ - minCZ + 1;

		Chunk[] chunks = new Chunk[rw * rh];

		World w = this.getWorld();

		for (int cx = minCX; cx <= maxCX; cx++)
			for (int cz = minCZ; cz <= maxCZ; cz++)
				chunks[(cx - minCX) * (rh) + (cz - minCZ)] = w.getChunkAt(cx, cz);

		return chunks;
	}

	public List<Player> getContainedPlayers() {
		List<Entity> playerEntities = this.getContainedEntities(new Filter<Entity>() {
			@Override
			public boolean accept(Entity item) {
				return item instanceof Player;
			}
		});

		List<Player> players = new ArrayList<Player>();

		for (Entity e : playerEntities)
			players.add((Player) e);

		return players;
	}

	public List<Entity> getContainedEntities(Filter<Entity> filter) {
		ArrayList<Entity> contents = new ArrayList<Entity>();

		for (Chunk c : this.getChunks())
			for (Entity e : c.getEntities())
				if (filter.accept(e) && this.contains(e))
					contents.add(e);

		return contents;
	}
}
