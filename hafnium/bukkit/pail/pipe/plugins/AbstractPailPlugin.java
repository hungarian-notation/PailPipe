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
package hafnium.bukkit.pail.pipe.plugins;

import hafnium.bukkit.pail.pipe.logging.PailFileLogger;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractPailPlugin extends JavaPlugin implements PailPlugin {
	private PailFileLogger logfile;

	/**
	 * Called by bukkit when this SubPail is being disabled.
	 */
	@Override
	public final void onDisable() {
		this.log("Disabling...");

		this.onPailDisable();

		this.log("Disabled.");

		this.logfile.stopLogging();
	}

	/**
	 * Called by bukkit when this SubPail is being enabled.
	 */
	@Override
	public final void onEnable() {
		this.logfile = new PailFileLogger(this);

		this.log("Enabling...");

		this.onPailEnable();

		this.log("Enabled.");
	}

	/**
	 * Called while this SubPail is being enabled to allow it to initialize
	 * itself.
	 */
	public abstract void onPailEnable();

	/**
	 * Called while this SubPail is being disabled to allow it to finalize
	 * itself.
	 */
	public abstract void onPailDisable();

	public void log(String message) {
		this.getLogger().info(message);
	}

	public void error(String message) {
		this.error(message, null);
	}

	public void error(String message, Exception e) {
		this.getLogger().log(Level.WARNING, message, e);
	}

	@Override
	public final String getPermissionRoot() {
		return this.getDescription().getName().toLowerCase();
	}
}