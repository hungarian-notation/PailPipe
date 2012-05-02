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

import hafnium.bukkit.pail.pipe.PailPipe;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventSupplement implements Listener {
	public EventSupplement(PailPipe plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (!MajorMoveEvent.isMajor(event))
			return;

		MajorMoveEvent mme = new MajorMoveEvent(event);

		boolean wasCancelled;

		mme.setCancelled(wasCancelled = event.isCancelled());

		org.bukkit.Bukkit.getServer().getPluginManager().callEvent(mme);

		if (mme.isCancelled() && !wasCancelled)
			event.setCancelled(true);
		// TODO: Safe move-back.
	}

	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		this.onPlayerMoveEvent(event);
	}

	@EventHandler
	public void onPlayerPortalEvent(PlayerPortalEvent event) {
		this.onPlayerTeleportEvent(event);
	}

}
