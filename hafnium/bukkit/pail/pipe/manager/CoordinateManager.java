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
import hafnium.bukkit.pail.pipe.players.PailPlayer;
import hafnium.bukkit.pail.pipe.players.PailPlayer.PlayerCoord;
import hafnium.bukkit.pail.util.BlockLocation;
import hafnium.bukkit.pail.util.Coordinate;
import hafnium.bukkit.pail.util.commands.CommandDefinition;
import hafnium.bukkit.pail.util.commands.CommandExec;
import hafnium.bukkit.pail.util.commands.CommandUtil;
import hafnium.bukkit.pail.util.commands.node.StructuralNode;
import hafnium.bukkit.pail.util.sign.BlockSignText;
import hafnium.bukkit.pail.util.sign.SignLocation;
import hafnium.bukkit.pail.util.sign.SignText;
import hafnium.bukkit.pail.util.text.MessageableException;
import hafnium.bukkit.pail.util.text.PailMessage;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CoordinateManager implements Listener {
	private final PailPipe plugin;
	private final StructuralNode node;

	public CoordinateManager(PailPipe plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);

		this.node = new StructuralNode();
		this.node.setExecutor(new CoordCommand());
	}

	public StructuralNode getManagementNode() {
		return this.node;
	}

	public String[] getIdiom() {
		return hafnium.bukkit.pail.util.hf.array("coord");
	}

	public String getWandName() {
		return Material.getMaterial(CoordinateManager.this.plugin.getConfig().getInt("coordinate-wand")).name().toLowerCase();
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.isCancelled())
			return;

		if ((e.getItem() != null) && (e.getItem().getType().getId() == this.plugin.getConfig().getInt("coordinate-wand"))) {
			if (e.getClickedBlock() == null)
				return;

			if (e.getClickedBlock().getType().equals(org.bukkit.Material.WALL_SIGN)) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				if (this.scanSign(sign, e))
					return;
			}

			BlockLocation clickLoc = new BlockLocation(e.getClickedBlock().getLocation());
			PailPlayer pp = this.plugin.getPlayerManager().getPlayer(e.getPlayer());

			if (!pp.isAltCoord())
				switch (e.getAction()) {
				case LEFT_CLICK_BLOCK:
					this.setCoordA(clickLoc, pp);
					e.setCancelled(true);
					break;
				case RIGHT_CLICK_BLOCK:
					this.setCoordB(clickLoc, pp);
					break;
				default:
					return;
				}
			else {
				this.setCoordC(clickLoc, pp);
				e.setCancelled(true);
				pp.clearAltCoord();
			}
		}
	}

	private void setCoord(BlockLocation coord, PailPlayer pp, PlayerCoord toSet) {
		if (pp.setCoordinate(toSet, coord))
			PailMessage.from("Set coordinate ^n" + toSet + " ^gto: " + coord.asCoordinate().toFormattedString()).sendTo(pp.getPlayer());
	}

	private void setCoordA(BlockLocation coord, PailPlayer pp) {
		this.setCoord(coord, pp, PlayerCoord.A);
	}

	private void setCoordB(BlockLocation coord, PailPlayer pp) {
		this.setCoord(coord, pp, PlayerCoord.B);
	}

	private void setCoordC(BlockLocation coord, PailPlayer pp) {
		this.setCoord(coord, pp, PlayerCoord.C);
	}

	private boolean scanSign(Sign sign, PlayerInteractEvent e) {
		SignText text = new BlockSignText(sign);
		SignLocation loc = new SignLocation(sign);

		ArrayList<BlockLocation> locs = new ArrayList<BlockLocation>();

		for (int i = 0; i < 4; i++) {
			Coordinate coord = Coordinate.parse(text.getLine(i));
			if (coord != null)
				locs.add(coord.getAsRelativeTo(loc.getHostBlockLocation()));
		}

		if (locs.size() == 0)
			return false;

		PailMessage.from("^nCopying coordinates from sign:").sendTo(e.getPlayer());

		PailPlayer pp = this.plugin.getPlayerManager().getPlayer(e.getPlayer());

		if (locs.size() == 1)
			this.setCoordC(locs.get(0), pp);
		else if (locs.size() == 2) {
			this.setCoordA(locs.get(0), pp);
			this.setCoordB(locs.get(1), pp);
		} else if (locs.size() >= 3) {
			this.setCoordA(locs.get(0), pp);
			this.setCoordB(locs.get(1), pp);
			this.setCoordC(locs.get(2), pp);
		}

		e.setCancelled(true);

		final Block block = e.getClickedBlock();

		org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(PailPipe.getInstance(), new Runnable() {

			@Override
			public void run() {
				block.getState().update();
			}

		});

		return true;
	}

	public class CoordCommand implements CommandDefinition {
		@CommandExec
		public void onCoord(CommandSender sender) throws MessageableException {
			Player p = CommandUtil.assertIsPlayer(sender);
			PailMessage.from("^gPlacing coordinate ^nC^g ...").sendTo(sender);
			CoordinateManager.this.plugin.getPlayerManager().getPlayer(p).setAltCoord();
		}

		@Override
		public String getHelp() {
			return "Puts you into alternate coordinate mode. The next coordinate you specify with the coordinate wand ( ^n"
					+ CoordinateManager.this.getWandName() + " ^g) will be saved as coodinate ^nC^g.";
		}
	}
}
