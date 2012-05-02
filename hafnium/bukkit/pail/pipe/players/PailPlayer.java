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
package hafnium.bukkit.pail.pipe.players;

import hafnium.bukkit.pail.pipe.PailPipe;
import hafnium.bukkit.pail.util.BlockLocation;
import hafnium.bukkit.pail.util.Region;

import java.util.EnumMap;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PailPlayer {
	private final Player player;

	private final HashMap<String, Object> metadata;

	private String coordinateWorld;
	private final EnumMap<PlayerCoord, BlockLocation> coordinates;

	private boolean altCoord;

	public PailPlayer(Player p) {
		this.player = p;
		this.metadata = new HashMap<String, Object>();
		this.coordinates = new EnumMap<PlayerCoord, BlockLocation>(PlayerCoord.class);

		this.altCoord = false;

		PailPipe.getInstance().getLogger().info("Created player manager for " + p.getName() + ".");
	}

	public PailPlayer(String playerName) {
		this(Bukkit.getPlayer(playerName));
	}

	public Player getPlayer() {
		return this.player;
	}

	public Object getData(String key) {
		return this.metadata.get(key);
	}

	public <T> T getData(String key, Class<T> clazz) {
		return clazz.cast(this.getData(key));
	}

	public void setData(String key, Object value) {
		this.metadata.put(key, value);
	}

	public boolean hasData(String key) {
		return this.getData(key) != null;
	}

	public boolean isOnline() {
		return this.player.isOnline();
	}

	public boolean hasCoordinate(PlayerCoord coord) {
		return this.coordinates.get(coord) != null;
	}

	public boolean setCoordinate(PlayerCoord coord, BlockLocation location) {
		boolean change = false;

		if (!location.getWorldName().equals(this.coordinateWorld)) {
			this.coordinateWorld = location.getWorldName();
			this.coordinates.clear();
			change = true;
		}

		if ((this.coordinates.get(coord) == null) || !this.coordinates.get(coord).equals(location.asCoordinate()))
			change = true;

		this.coordinates.put(coord, location);

		return change;
	}

	public BlockLocation getCoordinate(PlayerCoord coord) {
		return this.coordinates.get(coord);
	}

	public Region getDefinedRegion() {
		if (!(this.hasCoordinate(PlayerCoord.A) && this.hasCoordinate(PlayerCoord.B)))
			return null;

		BlockLocation coordA = this.getCoordinate(PlayerCoord.A);
		BlockLocation coordB = this.getCoordinate(PlayerCoord.B);

		if (!(coordA.isComplete() && coordB.isComplete()))
			return null;

		return new Region(coordA, coordB);
	}

	public String getCoordinateWorld() {
		return this.coordinateWorld;
	}

	public boolean isAltCoord() {
		return this.altCoord;
	}

	public void setAltCoord() {
		this.altCoord = true;
	}

	public void clearAltCoord() {
		this.altCoord = false;
	}

	public static enum PlayerCoord {
		A, B, C;
	}
}
