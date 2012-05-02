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

import hafnium.bukkit.pail.pipe.PailPipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbstractDataController<DT extends Datum> implements DataController<DT> {
	protected final File dataLoc;
	protected final HashMap<DatumID, DatumRef> data;

	protected final String extension;
	protected final String typeName;

	protected final Class<DT> type;

	public AbstractDataController(Class<DT> datumClass, String typeName, String extension) {
		this.data = new HashMap<DatumID, DatumRef>();
		this.dataLoc = new File(PailPipe.getInstance().getDataFolder(), typeName);

		this.typeName = typeName;
		this.extension = extension;

		this.type = datumClass;
	}

	@Override
	public boolean has(DatumID id) {
		return this.data.get(id) != null;
	}

	@Override
	public DT get(DatumID id) {
		DatumRef ref = this.data.get(id);
		if (ref == null)
			return null;
		else
			return ref.getDatum();
	}

	protected void putSoftly(DatumID id, DT datum) {
		DatumRef ref = this.data.get(id);
		if (ref != null)
			ref.setDatum(datum);
		else
			this.data.put(id, new DatumRef(id, datum));
	}

	@Override
	public void put(DatumID id, DT datum) {
		this.putSoftly(id, datum);
	}

	@Override
	public void remove(DatumID id) {
		DatumRef ref = this.data.remove(id);
		if (ref != null) {
			ref.setDatum(null);
			ref.save();
		}
	}

	@Override
	public void fill(DatumID id, CommandSender context) {
		if (id.isOwned())
			return;

		if (id.isPersonal()) {
			id.setOwner(context);
			return;
		}

		if (this.fillGlobal(id))
			return;

		id.setOwner(context);
	}

	@Override
	public void fill(DatumID id, String context) {
		if (id.isOwned())
			return;

		if (id.isPersonal()) {
			id.setOwner(context);
			return;
		}

		if (this.fillGlobal(id))
			return;

		id.setOwner(context);
	}

	private boolean fillGlobal(DatumID id) {
		for (DatumID nid : this.data.keySet())
			if (nid.getId().equalsIgnoreCase(id.getId()) && nid.isGlobal()) {
				id.setOwner(nid.getOwner());
				this.get(nid);
				return true;
			}

		return false;
	}

	@Override
	public String getDataPermission() {
		return PailPipe.getInstance().getPermissionRoot() + "." + this.typeName;
	}

	@Override
	public String getAdminPermission() {
		return this.getDataPermission() + ".admin";
	}

	@Override
	public boolean isSynced() {
		for (DatumRef ref : this.data.values())
			if (ref.hasChanged())
				return false;

		return true;
	}

	private List<DatumRef> getChanged() {
		ArrayList<DatumRef> changes = new ArrayList<DatumRef>();
		for (DatumRef ref : this.data.values())
			if (ref.hasChanged())
				changes.add(ref);
		return changes;
	}

	@Override
	public void save() {
		List<DatumRef> changes = this.getChanged();

		if (changes.size() == 0)
			return;

		Logger log = PailPipe.getInstance().getLogger();

		log.info("Saving " + this.typeName + "... (" + changes.size() + ")");

		for (DatumRef ref : changes)
			ref.save();

		log.info("Saved " + this.typeName + ".");
	}

	@Override
	public void load() {
		if (!this.isSynced())
			this.save();

		this.data.clear();

		Logger log = PailPipe.getInstance().getLogger();

		Map<DatumID, File> data = DatumID.findIn(this.getDataRoot());

		log.info("Loading " + this.typeName + "... (" + data.size() + ")");

		for (DatumID id : data.keySet()) {
			DatumRef ref = new DatumRef(id);
			ref.load();
			this.data.put(id, ref);
		}

		log.info(this.typeName + " loaded.");
	}

	protected File getDataRoot() {
		return this.dataLoc;
	}

	@Override
	public List<DatumID> getIDs(CommandSender user, DataScope scope) {
		return this.getIDs((user instanceof Player ? user.getName() : "#"), scope);
	}

	@Override
	public List<DatumID> getIDs(String user, DataScope scope) {
		ArrayList<DatumID> ids = new ArrayList<DatumID>();

		for (DatumID id : this.data.keySet())
			if ((id.getScope() == scope) && (id.getOwner().equalsIgnoreCase(user) || (scope == DataScope.GLOBAL)))
				ids.add(id);

		return ids;
	}

	private Class<DT> getType() {
		return this.type;
	}

	@Override
	public void optimize() {
		for (DatumRef ref : this.data.values())
			ref.optimize();
	}

	public class DatumRef {
		private final DatumID id;
		private DT datum;
		private boolean change;
		private long lastAccessed;

		public DatumRef(DatumID id) {
			this.id = id;
			this.change = false;
			this.lastAccessed = 0;
		}

		public DatumRef(DatumID id, DT datum) {
			this(id);
			this.setDatum(datum);
		}

		/**
		 * @return the datum
		 */
		public DT getDatum() {
			if (this.datum == null)
				this.load();
			this.touchTimestamp();
			return this.datum;
		}

		/**
		 * @param datum
		 *            the datum to set
		 */
		public void setDatum(final DT datum) {
			this.touchTimestamp();
			this.datum = datum;
			this.change = true;
		}

		public boolean hasChanged() {
			return this.change;
		}

		public void save() {
			this.save(false);
		}

		private void touchTimestamp() {
			this.lastAccessed = PailPipe.getServerTime();
		}

		public void save(final boolean quiet) {
			if (!this.hasChanged())
				return;

			this.change = false;

			Logger log = PailPipe.getInstance().getLogger();

			AbstractDataController<DT> ctrl = AbstractDataController.this;

			File file = this.id.getFile(ctrl.getDataRoot(), ctrl.extension);

			if (this.datum == null) {
				if (!quiet)
					log.fine("deleting " + this.id + " from the filesystem");
				file.delete();
			} else
				try {
					file.getParentFile().mkdirs();
					if (!file.exists())
						file.createNewFile();
					this.datum.writeTo(file);
					if (!quiet)
						log.fine("saved " + this.id);
				} catch (IOException e) {
					log.log(Level.SEVERE, "Error saving: " + this.id + " Changes will be lost.", e);
				}
		}

		public void load() {
			this.load(false);
		}

		public void load(final boolean quiet) {
			if (this.hasChanged())
				this.save(true);
			else {
				Logger log = PailPipe.getInstance().getLogger();
				AbstractDataController<DT> ctrl = AbstractDataController.this;

				File file = this.id.getFile(ctrl.getDataRoot(), ctrl.extension);

				if (!file.exists())
					this.datum = null;
				else
					try {
						this.datum = ctrl.getType().newInstance();
						this.datum.readFrom(file);
						if (!quiet)
							log.fine("loaded " + this.id);
					} catch (Exception e) {
						if (!quiet)
							log.log(Level.SEVERE, "Error loading: " + this.id, e);
					}

				this.touchTimestamp();
			}
		}

		public void optimize() {
			if ((this.datum != null) && (this.lastAccessed + 60 * 20 < PailPipe.getServerTime())) {
				this.save();
				this.datum = null;
				PailPipe.getInstance().getLogger().fine("offloaded " + this.id);
			}
		}
	}

}
