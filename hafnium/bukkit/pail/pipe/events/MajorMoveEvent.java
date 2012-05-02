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
package hafnium.bukkit.pail.pipe.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

public class MajorMoveEvent extends Event implements Cancellable {
	private static final long serialVersionUID = 1L;

	private final Location to, from;
	private final Player player;

	boolean cancelled;

	public MajorMoveEvent(PlayerMoveEvent event) {
		this.to = event.getTo();
		this.from = event.getFrom();
		this.player = event.getPlayer();
		this.cancelled = event.isCancelled();

	}

	/**
	 * @return the to
	 */
	public Location getTo() {
		return this.to;
	}

	/**
	 * @return the from
	 */
	public Location getFrom() {
		return this.from;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return this.player;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	public static boolean isMajor(PlayerMoveEvent e) {
		Location from, to;

		from = e.getFrom();
		to = e.getTo();

		return (from.getBlockX() != to.getBlockX()) || (from.getBlockY() != to.getBlockY()) || (from.getBlockZ() != to.getBlockZ());
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
