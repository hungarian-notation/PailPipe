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
package hafnium.bukkit.pail.pipe.data;

import hafnium.bukkit.pail.util.PailPermission;
import hafnium.bukkit.pail.util.PermissionException;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// TODO: Clean and document.

public class DatumID {
	private String owner;
	private String id;
	private DataScope scope;

	public DatumID() {
		this(null, null, null);
	}

	public DatumID(String id, DataScope scope) {
		this(null, id, scope);
	}

	public DatumID(String owner, String id, DataScope scope) {
		this.setOwner(owner);
		this.setId(id);
		this.setScope(scope);
	}

	public boolean isOwned() {
		return this.owner != null;
	}

	public boolean isPersonal() {
		return this.scope == DataScope.PERSONAL;
	}

	public boolean isGlobal() {
		return this.scope == DataScope.GLOBAL;
	}

	public boolean isGuessable() {
		return (this.scope != null) && (this.id != null);
	}

	public boolean isComplete() {
		return this.isOwned() && this.isGuessable();
	}

	public boolean isServerOwned() {
		return (this.owner != null) && this.owner.equals("#");
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return this.owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = (owner != null ? owner.toLowerCase() : null);
		if (this.isServerOwned())
			this.setScope(DataScope.GLOBAL);
	}

	public void setOwner(CommandSender context) {
		this.setOwner(context instanceof Player ? context.getName() : "#");
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = (id != null ? id.toLowerCase() : null);
	}

	/**
	 * @return the scope
	 */
	public DataScope getScope() {
		return this.scope;
	}

	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(DataScope scope) {
		if ((scope == DataScope.PERSONAL) && this.isServerOwned())
			scope = DataScope.GLOBAL;
		this.scope = scope;
	}

	@Override
	public String toString() {
		return (this.isOwned() ? this.getOwner() : "") + (this.getScope() == DataScope.GLOBAL ? '@' : '.') + this.getId();
	}

	public String toFormattedString() {
		return (this.isOwned() ? "^i" + this.getOwner() : "") + "^n" + (this.getScope() == DataScope.GLOBAL ? '@' : '.') + "^i" + this.getId();
	}

	public String toMinimalString() {
		return (this.getScope() == DataScope.GLOBAL ? '@' : "") + this.getId();
	}

	private static Pattern idPattern = Pattern.compile("^(?:([a-zA-Z0-9_.]+?|#)?([.@]))?([a-zA-Z0-9_-]+)$");

	public static boolean isValid(String did) {
		return idPattern.matcher(did).matches();
	}

	public static DatumID getFor(String did) {
		return getFor(null, did);
	}

	public static DatumID getFor(String defOwner, String did) {
		Matcher m = idPattern.matcher(did);

		if (!m.matches())
			return null;

		boolean isServerOwned = false;

		String owner = m.group(1);

		if (owner == null)
			owner = defOwner;

		if ((owner != null) && owner.equals("#"))
			isServerOwned = true;

		DataScope scope = DataScope.PERSONAL;

		if (m.group(2) != null)
			scope = (m.group(2).equals("@") ? DataScope.GLOBAL : DataScope.PERSONAL);

		if (isServerOwned)
			scope = DataScope.GLOBAL;

		String id = m.group(3);

		return new DatumID(owner, id, scope);
	}

	public static DatumID getFor(CommandSender sender, String id, DataScope scope) {
		return new DatumID((sender instanceof Player ? sender.getName() : "#"), id, (sender instanceof Player ? scope : DataScope.GLOBAL));
	}

	public boolean isOwnedBy(CommandSender requester) {
		if (requester instanceof Player)
			return this.getOwner().equalsIgnoreCase(requester.getName());
		else
			return this.getOwner().equals("#");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.toLowerCase().hashCode());
		result = prime * result + ((this.owner == null) ? 0 : this.owner.toLowerCase().hashCode());
		result = prime * result + ((this.scope == null) ? 0 : this.scope.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		DatumID other = (DatumID) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equalsIgnoreCase(other.id))
			return false;
		if (this.owner == null) {
			if (other.owner != null)
				return false;
		} else if (!this.owner.equalsIgnoreCase(other.owner))
			return false;
		if (this.scope != other.scope)
			return false;
		return true;
	}

	public void assertCanPerform(CommandSender sender, DataUse use, DataController<?> controller) throws PermissionException {
		String root = controller.getDataPermission();

		String permPersonal = root + ".personal";
		String permGlobal = root + ".global";
		String permAdmin = root + ".admin";

		if (this.getScope() == DataScope.PERSONAL) {
			if (this.isOwnedBy(sender))
				PailPermission.assertPerm(sender, permPersonal, "^eYou do not have permission to " + use.getPrintName()
						+ " personal data of this type.");
			else
				PailPermission.assertPerm(sender, permAdmin, "^eYou do not have permission to " + use.getPrintName()
						+ " others' personal data of this type.");
		} else if ((this.getScope() == DataScope.GLOBAL) || this.isOwnedBy(sender))
			if ((use == DataUse.USE) || this.isOwnedBy(sender))
				PailPermission.assertPerm(sender, permGlobal, "^eYou do not have permission to " + use.getPrintName() + " global data of this type.");
			else
				PailPermission.assertPerm(sender, permAdmin, "^eYou do not have permission to " + use.getPrintName()
						+ " others' global data of this type.");
	}

	public void assertCanPerform(String owner, DataUse use, DataController<?> controller) throws PermissionException {
		CommandSender sender = org.bukkit.Bukkit.getPlayer(owner);
		if (sender != null)
			this.assertCanPerform(sender, use, controller);
	}

	public File getFile(File root, String extension) {
		String owner = this.getOwner();
		String id = this.getId();

		if (owner.equalsIgnoreCase("#"))
			owner = ".console";

		root = new File(root, owner);

		if (this.scope == DataScope.PERSONAL)
			return new File(root, "p_" + id + "." + extension);
		else
			return new File(root, "g_" + id + "." + extension);
	}

	private static Pattern patFileName = Pattern.compile("^([pg])\\_([a-zA-Z0-9_-]+)\\.[^.]+$");

	public static DatumID parseFileName(String folder, String filename) {
		String owner = folder;

		if (owner.equals(".console"))
			owner = "#";

		Matcher m = patFileName.matcher(filename);

		if (m.matches()) {
			String scopeid = m.group(1);
			String id = m.group(2);

			DataScope scope = (scopeid.equalsIgnoreCase("p") ? DataScope.PERSONAL : DataScope.GLOBAL);

			return new DatumID(owner, id, scope);
		} else
			return null;
	}

	public static void main(String[] args) {
		File root = new File("D:\\Dev\\MC DEV\\Test Server\\plugins\\PailPipe\\text\\");
		Map<DatumID, File> data = findIn(root);
		for (DatumID id : data.keySet())
			System.out.println(id);
	}

	public static Map<DatumID, File> findIn(File root) {
		HashMap<DatumID, File> data = new HashMap<DatumID, File>();

		File[] folders = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}
		});

		if (folders == null)
			return data;

		for (File folder : folders) {
			File[] files = folder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isFile();
				}
			});

			for (File file : files) {
				DatumID id = DatumID.parseFileName(folder.getName(), file.getName());
				if (id != null)
					data.put(id, file);
			}
		}

		return data;
	}

	public boolean appliesTo(DatumID other) {
		if (!this.isGuessable() || !other.isGuessable())
			return false;
		if (!other.getId().equalsIgnoreCase(this.getId()))
			return false;
		if (other.getScope() != this.getScope())
			return false;
		if ((other.getOwner() == null) || (this.getOwner() == null))
			return true;
		if (other.getOwner().equalsIgnoreCase(this.getOwner()))
			return true;
		return false;
	}
}
