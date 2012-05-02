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
package hafnium.bukkit.pail.pipe;

import hafnium.bukkit.pail.pipe.events.EventSupplement;
import hafnium.bukkit.pail.pipe.manager.AreaManager;
import hafnium.bukkit.pail.pipe.manager.CoordinateManager;
import hafnium.bukkit.pail.pipe.manager.DrawingManager;
import hafnium.bukkit.pail.pipe.manager.PlayerManager;
import hafnium.bukkit.pail.pipe.manager.TextManager;
import hafnium.bukkit.pail.pipe.plugins.AbstractPailPlugin;
import hafnium.bukkit.pail.util.commands.node.CommandNode;
import hafnium.bukkit.pail.util.commands.node.CommandRoot;

public class PailPipe extends AbstractPailPlugin {
	private static PailPipe instance = null;

	public static PailPipe getInstance() {
		return instance;
	}

	private long ticks;

	private Runnable saveProcess;
	private Runnable optimizationProcess;

	private TextManager textManager;
	private AreaManager areaManager;
	private PlayerManager playerManager;
	private CoordinateManager coordManager;
	private DrawingManager drawingManager;

	private VIPList vipList;

	public PailPipe() {
		instance = this;
	}

	@Override
	public void onPailEnable() {
		this.getConfig().options().copyHeader(true);
		this.getConfig().options().copyDefaults(true);

		this.saveConfig();

		this.vipList = new VIPList(this);
		this.vipList.update();

		this.textManager = new TextManager();
		this.textManager.load();

		this.areaManager = new AreaManager();
		this.areaManager.load();

		this.playerManager = new PlayerManager(this);
		this.coordManager = new CoordinateManager(this);
		this.drawingManager = new DrawingManager(this);

		this.ticks = 0;

		org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				PailPipe.this.ticks++;
			}
		}, 0, 1);

		CommandNode cmdPail = new CommandRoot();
		cmdPail.addNode(this.textManager.getManagementNode().getLiteralParent(this.textManager.getIdiom()));
		cmdPail.addNode(this.coordManager.getManagementNode().getLiteralParent(this.coordManager.getIdiom()));
		cmdPail.addNode(this.areaManager.getManagementNode().getLiteralParent(this.areaManager.getIdiom()));

		this.getCommand("pail").setExecutor(cmdPail);
		this.getCommand("text").setExecutor(this.textManager.getManagementNode());
		this.getCommand("area").setExecutor(this.areaManager.getManagementNode());

		this.saveProcess = new Runnable() {
			@Override
			public void run() {
				PailPipe.this.getTextManager().save();
				PailPipe.this.getAreaManager().save();
				PailPipe.this.getPlayerManager().maintain();
			}
		};

		this.optimizationProcess = new Runnable() {
			@Override
			public void run() {
				PailPipe.this.getTextManager().getController().optimize();
				PailPipe.this.getAreaManager().getController().optimize();
			}
		};

		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.saveProcess, 0, 20 * 60 * 10);

		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.optimizationProcess, 0, 60);

		new EventSupplement(this);

	}

	@Override
	public void onPailDisable() {
		this.saveProcess.run();
	}

	public TextManager getTextManager() {
		return this.textManager;
	}

	/**
	 * @return the areaManager
	 */
	public AreaManager getAreaManager() {
		return this.areaManager;
	}

	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public CoordinateManager getCoordinateManager() {
		return this.coordManager;
	}

	public DrawingManager getDrawingManager() {
		return this.drawingManager;
	}

	/**
	 * @return the vipList
	 */
	public VIPList getVipList() {
		return this.vipList;
	}

	/*
	 * Static universal utils:
	 */
	public static long getServerTime() {
		return PailPipe.instance.ticks;
	}
}
