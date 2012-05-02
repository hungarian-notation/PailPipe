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
package hafnium.bukkit.pail.util.redstone;

public enum Current {
	RISING(true, true), // Current going from off to on.
	FALLING(false, true), // Current going from on to off.
	ON(true, false), // Current is on and steady.
	OFF(false, false), // Current is off and steady.
	DISCONN(false, false), // There is no block at the location.
	UNLOADED(false, false); // The location is not loaded.

	private final boolean isPowered;
	private final boolean isChanging;

	private Current(boolean isPowered, boolean isChanging) {
		this.isPowered = isPowered;
		this.isChanging = isChanging;
	}

	/**
	 * Returns true iff the Current represents a presence of redstone power.
	 * 
	 * @return true iff the Current represents a presence of redstone power.
	 */
	public boolean asBoolean() {
		return this.isPowered;
	}

	/**
	 * Returns true iff the Current represents a changing level.
	 * 
	 * @return true iff the Current represents a changing level.
	 */
	public boolean isChanging() {
		return this.isChanging;
	}

	/**
	 * Gets the conclusion of the current, aka what the current is or is
	 * changing into.
	 * 
	 * @return
	 */
	public Current getConclusion() {
		if (!this.isChanging())
			return this;

		switch (this) {
		case RISING:
			return ON;
		case FALLING:
			return OFF;
		}

		return null;
	}

	public static Current interpolate(Current from, Current to) {
		if ((from.asBoolean() == false) && (to.asBoolean() == true))
			return Current.RISING;

		if ((from.asBoolean() == true) && (to == Current.OFF))
			return Current.FALLING;

		return to;
	}
}
