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
package hafnium.bukkit.pail.pipe.logging;

import org.bukkit.plugin.Plugin;

// TODO: Actual file logging for errors.

@Deprecated
public class PPLogger {
	private final Plugin plugin;
	private int messages = 0;

	/**
	 * Constructs a logger for the specified plugin.
	 * 
	 * @param pailPlugin
	 */
	public PPLogger(Plugin pailPlugin) {
		this.plugin = pailPlugin;
	}

	/**
	 * Logs a message to the console.
	 * 
	 * @param message
	 */
	public void log(String message) {
		String idString = "[" + ((this.messages == 0) ? (this.plugin.getDescription().getFullName()) : this.plugin.getDescription().getName()) + "]";
		System.out.println(idString + " " + message);
		this.messages++;
	}

	public void error(String message, Exception e) {
		this.log("[ERROR] " + message + " / " + e.getMessage());
	}
}
