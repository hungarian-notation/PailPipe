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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemForm {
	private int material = -1;
	private byte data = -1;

	public ItemForm() {

	}

	public ItemForm(int mat) {
		this.material = mat;
	}

	public ItemForm(int mat, byte data) {
		this.material = mat;
		this.data = data;
	}

	/**
	 * @return the material
	 */
	public int getMaterial() {
		return this.material;
	}

	/**
	 * @param material
	 *            the material to set
	 */
	public void setMaterial(int material) {
		this.material = material;
	}

	/**
	 * @return the data
	 */
	public byte getData() {
		return this.data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(byte data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return (this.material < 0 ? "*" : this.material) + (this.data >= 0 ? ":" + this.data : "");
	}

	public boolean isMaterialDefined() {
		return this.material >= 0;
	}

	public boolean isDataDefined() {
		return this.data >= 0;
	}

	public boolean isValidMaterial() {
		return this.isMaterialDefined() && (Material.getMaterial(this.getMaterial()) != null);
	}

	public boolean isValidBlock() {
		Material mat = Material.getMaterial(this.material);
		return ((mat != null) && mat.isBlock());
	}

	public void spawn(BlockLocation location, int amount) {
		Location sloc = location.getHorizontalCenter();
		org.bukkit.World world = sloc.getWorld();

		int fullStacks = amount / 64;
		int remainder = amount % 64;
		int nAmount = 64;

		for (int i = 0; i < fullStacks + (remainder > 0 ? 1 : 0); i++) {
			if (i == fullStacks)
				nAmount = remainder;

			world.dropItem(sloc, new ItemStack(this.getMaterial(), nAmount, this.getData()));
		}
	}

	private static Pattern pattern = Pattern.compile("^([0-9]+|\\*|-1)?(?:[:]([0-9]+|\\*|-1)?)?$");

	public static ItemForm parse(String def) {
		Matcher m = pattern.matcher(def);

		if (!m.matches())
			return null;

		int mat = ((m.group(1) == null) || m.group(1).equals("*") || m.group(1).equals("-1") ? -1 : Integer.parseInt(m.group(1)));
		byte data = ((m.group(2) == null) || m.group(2).equals("*") || m.group(2).equals("-1") ? -1 : Byte.parseByte(m.group(2)));

		return new ItemForm(mat, data);
	}
}
