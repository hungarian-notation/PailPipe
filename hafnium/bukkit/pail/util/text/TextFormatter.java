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
package hafnium.bukkit.pail.util.text;

import org.bukkit.ChatColor;

public class TextFormatter {
	/**
	 * Formats a user-entered string for colored display.
	 * 
	 * @param text
	 * @return
	 */
	public static String format(String text) {
		StringBuffer buf = new StringBuffer();

		boolean special = false;

		for (int i = 0; i < text.length(); i++) {
			char nc = text.charAt(i);

			if (nc == '&') {
				if (special) {
					buf.append('&');
					special = false;
				} else
					special = true;
			} else if (special) {
				if ((nc == 'n') || (nc == 'N'))
					buf.append('\n');
				else {
					ChatColor c = ChatColor.getByChar(nc);
					if (c != null)
						buf.append(c.toString());
				}
				special = false;
			} else
				buf.append(nc);
		}

		return buf.toString();
	}
}
