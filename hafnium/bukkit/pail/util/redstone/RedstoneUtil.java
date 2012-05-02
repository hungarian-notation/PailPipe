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

import hafnium.bukkit.pail.util.BlockLocation;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneUtil {

	/**
	 * Checks to see if the specified block represents a loaded lever.
	 * 
	 * @param block
	 * @return
	 */
	public static boolean isLever(Block block) {
		return (block != null) && block.getChunk().isLoaded() && (block.getType() == Material.LEVER);
	}

	/**
	 * Checks if there is a loaded lever at the specified location.
	 * 
	 * @param loc
	 * @return
	 */
	public static boolean isLever(BlockLocation loc) {
		return isLever(loc.getBlock());
	}

	/**
	 * Checks to see if the specified block is a loaded button.
	 * 
	 * @param block
	 * @return
	 */
	public static boolean isButton(Block block) {
		return (block != null) && block.getChunk().isLoaded() && (block.getType() == Material.STONE_BUTTON);
	}

	/**
	 * Checks to see if the block represented by the specified location is a
	 * loaded button.
	 * 
	 * @param loc
	 * @return
	 */
	public static boolean isButton(BlockLocation loc) {
		return isButton(loc.getBlock());
	}

	/**
	 * Sets the lever at the specified location to the specified position.
	 * 
	 * @param loc
	 * @param position
	 * @return Sucess. False means that there is no lever, or that it is
	 *         unloaded.
	 */
	public static boolean setLever(BlockLocation loc, boolean position) {
		if (isLever(loc)) {
			Block lever = loc.getBlock();
			lever.setData((byte) (position ? (lever.getData() | 0x8) : (lever.getData() & ~0x8)));
			lever.getState().update();
			return true;
		} else
			return false;
	}

	public static boolean isRedstoneDevice(Block block) {
		switch (block.getType()) {
		case REDSTONE_WIRE:
		case LEVER:
		case STONE_BUTTON:
		case WOOD_PLATE:
		case STONE_PLATE:
		case DETECTOR_RAIL:
		case POWERED_RAIL:
			// case DIODE_BLOCK_ON:
			// case DIODE_BLOCK_OFF:
		case REDSTONE_TORCH_ON:
		case REDSTONE_TORCH_OFF:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Checks if the specified block is on. This works on all redstone carrying
	 * items.
	 * 
	 * @param b
	 * @return
	 */
	public static boolean isOn(Block block) {
		switch (block.getType()) {
		case REDSTONE_WIRE:
		case WOOD_PLATE:
		case STONE_PLATE:
			return block.getData() > 0;
		case LEVER:
		case STONE_BUTTON:
		case DETECTOR_RAIL:
		case POWERED_RAIL:
			return (block.getData() & 0x8) == 0x8;
			// case DIODE_BLOCK_ON: Diodes are ignored because they do not fire
			// usabel events.
		case REDSTONE_TORCH_ON:
			return true;
			// case DIODE_BLOCK_OFF:
		case REDSTONE_TORCH_OFF:
			return false;
		default:
			return false;
		}
	}

	public static Current getCurrent(Block block) {
		if (isRedstoneDevice(block)) {
			if (isOn(block))
				return Current.ON;
			else
				return Current.OFF;
		} else if (block.getChunk().isLoaded())
			return Current.DISCONN;
		else
			return Current.UNLOADED;
	}

	public static Current getCurrent(BlockLocation loc) {
		return getCurrent(loc.getBlock());
	}

	public static Current getCurrent(BlockRedstoneEvent event) {
		if (isRedstoneDevice(event.getBlock())) {
			if ((event.getOldCurrent() == 0) ^ (event.getNewCurrent() == 0)) {
				if (event.getOldCurrent() > 0)
					return Current.FALLING;
				return Current.RISING;
			} else {
				if (event.getNewCurrent() > 0)
					return Current.ON;
				return Current.OFF;
			}
		} else
			return Current.DISCONN;
	}
}
