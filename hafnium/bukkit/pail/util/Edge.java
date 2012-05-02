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
package hafnium.bukkit.pail.util;

import hafnium.bukkit.pail.util.redstone.Current;

public enum Edge implements AliasedEnum {
	RISING("on", "lit", "up", "rise"), FALLING("off", "unlit", "down", "fall"), BOTH("any", "either", "all");

	private final String[] aliases;

	private Edge(String... aliases) {
		this.aliases = aliases;
	}

	public boolean isMatch(Current c) {
		switch (this) {
		case RISING:
			return c == Current.RISING;
		case FALLING:
			return c == Current.FALLING;
		case BOTH:
			return (c == Current.RISING) || (c == Current.FALLING);
		}

		return false;
	}

	@Override
	public String[] getAliases() {
		return this.aliases;
	}
}
