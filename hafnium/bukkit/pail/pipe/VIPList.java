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
package hafnium.bukkit.pail.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VIPList {
	private final PailPipe plugin;
	private final ReadWriteLock lock;

	private final ArrayList<VIP> vips;

	public VIPList(PailPipe plugin) {
		this.plugin = plugin;
		this.lock = new ReentrantReadWriteLock();
		this.vips = new ArrayList<VIP>();
	}

	public List<VIP> getVIPList() {
		if (!this.lock.readLock().tryLock())
			return new ArrayList<VIP>();

		@SuppressWarnings("unchecked")
		// vips is explicitly declared as containing <VIP>
		List<VIP> vips = (List<VIP>) this.vips.clone();

		this.lock.readLock().unlock();

		java.util.Collections.sort(vips);

		return vips;
	}

	public VIP getVIP(String player) {
		if (!this.lock.readLock().tryLock())
			return null;

		VIP vip = null;

		for (VIP nVIP : this.vips)
			if (nVIP.getName().equalsIgnoreCase(player)) {
				vip = nVIP;
				break;
			}

		this.lock.readLock().unlock();

		return vip;
	}

	public boolean hasNoteableRole(String player) {
		VIP vip;
		return ((vip = this.getVIP(player)) != null) && (vip.getBestRole() != Role.USER);
	}

	public boolean isRoleOrBetter(String player, Role role) {
		return this.getBestRole(player).compareTo(role) <= 0;
	}

	public boolean isRole(String player, Role role) {
		if (role == Role.USER)
			return true;
		VIP vip = this.getVIP(player);
		if (vip == null)
			return false;
		return vip.isRole(role);
	}

	public Role getBestRole(String player) {
		VIP vip = this.getVIP(player);
		if (vip == null)
			return Role.USER;
		return vip.getBestRole();
	}

	public void update() {
		Thread updateThread = new Thread(new VIPQuery());
		updateThread.start();
	}

	public void synchUpdate() {
		(new VIPQuery()).run();
	}

	public static class VIP implements Comparable<VIP> {
		private final String name;
		private final Role[] roles;

		public VIP(String name, Role... roles) {
			this.name = name;
			this.roles = roles;
		}

		public Role getBestRole() {
			Role bestRole = Role.USER;

			for (Role r : this.roles)
				if ((bestRole == null) || (r.compareTo(bestRole) < 0))
					bestRole = r;

			return bestRole;
		}

		public String getName() {
			return this.name;
		}

		public Role[] getRoles() {
			return this.roles;
		}

		public boolean isRole(Role role) {
			for (Role nRole : this.getRoles())
				if (nRole == role)
					return true;

			return false;
		}

		@Override
		public String toString() {
			return this.getName() + ", " + this.getBestRole().getCleanName();
		}

		@Override
		public int compareTo(VIP other) {
			return this.getBestRole().compareTo(other.getBestRole());
		}
	}

	public static enum Role {
		LEAD("lead developer", 'l'), PROGRAMMER("programmer", 'p'), CONTRIBUTOR("contributor", 'c'), DONATOR("donator", 'd'), TESTER("beta tester",
				't'), USER("user", 'u');

		private final String cleanName;
		private final char id;

		private Role(String cleanName, char id) {
			this.cleanName = cleanName;
			this.id = id;
		}

		/**
		 * @return the cleanName
		 */
		public String getCleanName() {
			return this.cleanName;
		}

		/**
		 * @return the id
		 */
		public char getId() {
			return this.id;
		}

		public boolean isPlural() {
			return this != Role.LEAD;
		}

		public static Role getFor(char roleID) {
			for (Role role : Role.values())
				if (role.getId() == roleID)
					return role;

			return null;
		}
	}

	public class VIPQuery implements Runnable {
		@Override
		public void run() {
			VIPList list = VIPList.this;

			list.plugin.getLogger().info("Updating VIPs");
			URLConnection conn = null;

			try {
				URL vipdb = new URL("http://pail.hafnium.ws/donators.txt");

				conn = vipdb.openConnection();

				BufferedReader vipReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				String line;

				ArrayList<VIP> vips = new ArrayList<VIP>();

				Pattern vipPattern = Pattern.compile("^([a-zA-Z0-9_.]+)/([a-z]+)$");

				while ((line = vipReader.readLine()) != null) {
					line = line.trim();

					if (line.length() == 0)
						continue;
					if (line.charAt(0) == '#')
						continue;

					Matcher vipParser = vipPattern.matcher(line);

					if (!vipParser.matches())
						continue;

					String vipName = vipParser.group(1);

					String roleFlags = vipParser.group(2);

					Role[] roles = new Role[roleFlags.length()];

					for (int i = 0; i < roleFlags.length(); i++)
						roles[i] = Role.getFor(roleFlags.charAt(i));

					vips.add(new VIP(vipName, roles));
				}

				list.lock.writeLock().lock();

				list.vips.clear();
				list.vips.addAll(vips);

				list.lock.writeLock().unlock();

			} catch (Exception e) {
				e.printStackTrace();
				list.plugin.getLogger().info("Could not fetch VIP list. (" + e.getMessage() + ")");
			} finally {
				try {
					if (conn != null)
						conn.getInputStream().close();
				} catch (IOException e) {

				}
			}
		}
	}
}
