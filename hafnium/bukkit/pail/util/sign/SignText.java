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
package hafnium.bukkit.pail.util.sign;

import org.bukkit.ChatColor;

public abstract class SignText {
	public abstract String getLine(int lineIndex);

	public abstract void setLine(int lineIndex, String text);

	public void paintLine(int lineIndex, ChatColor c) {
		this.setLine(lineIndex, c + ChatColor.stripColor(this.getLine(lineIndex)));
	}

	public void paintLines(ChatColor c) {
		for (int i = 0; i < 4; i++)
			this.paintLine(i, c);
	}

	public void capitalizeLine(int lineIndex) {
		this.setLine(lineIndex, this.getLine(lineIndex).toUpperCase());
	}

	public void decapitalizeLine(int lineIndex) {
		this.setLine(lineIndex, this.getLine(lineIndex).toLowerCase());
	}

	public boolean isBlank() {
		return this.getLine(0).trim().equals("") && this.getLine(1).trim().equals("") && this.getLine(2).trim().equals("")
				&& this.getLine(3).trim().equals("");
	}

	public abstract SignLocation getLocation();
}
