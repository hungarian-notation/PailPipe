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
package hafnium.bukkit.pail.pipe.manager;

import hafnium.bukkit.pail.pipe.PailPipe;
import hafnium.bukkit.pail.pipe.VIPList;
import hafnium.bukkit.pail.pipe.VIPList.Role;
import hafnium.bukkit.pail.pipe.players.PailPlayer;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerManager implements Listener {
	private final ArrayList<PailPlayer> players;
	private final PailPipe plugin;

	public PlayerManager(PailPipe plugin) {
		this.players = new ArrayList<PailPlayer>();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		for (Player p : org.bukkit.Bukkit.getServer().getOnlinePlayers())
			this.manage(p);

		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		this.manage(event.getPlayer());
		VIPList list = this.plugin.getVipList();
		if (list.hasNoteableRole(event.getPlayer().getName())) {
			Role role = list.getBestRole(event.getPlayer().getName());
			this.plugin.getLogger().info(
					event.getPlayer().getName() + " is " + (role.isPlural() ? "a" : "the") + " " + role.getCleanName() + " for this project.");
		}
	}

	private PailPlayer getPlayerSoftly(String playerName) {
		for (PailPlayer p : this.players)
			if (p.getPlayer().getName().equalsIgnoreCase(playerName))
				return p;

		return null;
	}

	private boolean manage(Player player) {
		if (this.isManaged(player))
			return true;

		if (!player.isOnline())
			return false;

		this.players.add(new PailPlayer(player));
		return true;
	}

	private boolean manage(String playerName) {
		Player p = Bukkit.getPlayer(playerName);
		if (p == null)
			return false;

		return this.manage(p);
	}

	public PailPlayer getPlayer(String playerName) {
		PailPlayer p = this.getPlayerSoftly(playerName);

		if (p != null)
			return p;

		if (!this.manage(playerName))
			return null;

		return this.getPlayerSoftly(playerName);
	}

	public PailPlayer getPlayer(Player player) {
		return this.getPlayer(player.getName());
	}

	public boolean isManaged(String playerName) {
		PailPlayer p;
		return ((p = this.getPlayerSoftly(playerName)) != null) && p.getPlayer().isOnline();
	}

	public boolean isManaged(Player player) {
		return this.isManaged(player.getName());
	}

	public void maintain() {
		ArrayList<PailPlayer> expired = new ArrayList<PailPlayer>();

		for (PailPlayer p : this.players)
			if (!p.isOnline())
				expired.add(p);

		for (PailPlayer p : expired) {
			this.players.remove(p);
			PailPipe.getInstance().getLogger().info("Removed player manager for " + p.getPlayer().getName() + ".");
		}
	}
}
