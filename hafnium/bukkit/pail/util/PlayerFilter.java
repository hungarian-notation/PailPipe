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

import hafnium.bukkit.pail.pipe.PailPipe;
import hafnium.bukkit.pail.pipe.VIPList.Role;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerFilter {
	private boolean inverted;
	private boolean exclusive;

	public boolean matches(CommandSender player, String playerContext) {
		return this.matches((player instanceof Player ? player.getName() : "#"), playerContext);
	}

	public abstract boolean matches(String sender, String playerContext);

	/**
	 * If this matcher supports string names. (Permissions can't)
	 * 
	 * @return
	 */
	public boolean supportsString() {
		return true;
	}

	/**
	 * @return the inverted
	 */
	public boolean isInverted() {
		return this.inverted;
	}

	/**
	 * @param inverted
	 *            the inverted to set
	 */
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	/**
	 * @return the exclusive
	 */
	public boolean isExclusive() {
		return this.exclusive;
	}

	/**
	 * @param exclusive
	 *            the exclusive to set
	 */
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public String invPart() {
		if (this.isInverted())
			return "^";
		return "";
	}

	public String exPart() {
		if (this.isExclusive())
			return "/";
		return "";
	}

	/**
	 * Called by the parser to populate a Filter's fields with arguments from a
	 * parser.
	 * 
	 * @param matcher
	 */
	protected abstract void setVariables(String... variable);

	public static PlayerFilter parse(String def) {
		for (FilterType type : FilterType.values()) {
			Matcher matcher = type.pattern.matcher(def);
			if (matcher.matches())
				try {
					PlayerFilter filter = type.clazz.newInstance();

					String[] variables = new String[matcher.groupCount() - 2];

					for (int i = 0; i < variables.length; i++)
						variables[i] = matcher.group(i + 3);

					if (matcher.group(1) != null)
						filter.setExclusive(true);

					if (matcher.group(2) != null)
						filter.setInverted(true);

					filter.setVariables(variables);

					return filter;
				} catch (Exception e) {
					PailPipe.getInstance().getLogger().log(Level.SEVERE, "Error while instantiating a PlayerFilter:", e);
					return null;
				}
		}

		return null;
	}

	private static enum FilterType {

		CONSOLE("[#]", ConsolePlayer.class),

		ANY("[*@]", AnyPlayer.class),

		LITERAL("([a-zA-Z0-9_.]{2,16}|~)", LiteralPlayer.class),

		PERMISSIONS("[?]([^ ]+)", PermissionPlayer.class),

		VIP("[$]([lpcdtu])", VIPPlayer.class);

		final Pattern pattern;
		final Class<? extends PlayerFilter> clazz;

		private FilterType(String regex, Class<? extends PlayerFilter> clazz) {
			this.pattern = Pattern.compile("^([/])?([\\^])?" + regex + "$");
			this.clazz = clazz;
		}
	}

	public static class ConsolePlayer extends PlayerFilter {
		public ConsolePlayer() {

		}

		@Override
		public boolean matches(String sender, String playerContext) {
			return sender.equals("#") ^ this.isInverted();
		}

		@Override
		public String toString() {
			return this.exPart() + this.invPart() + "#";
		}

		@Override
		protected void setVariables(String... variable) {

		}
	}

	public static class AnyPlayer extends PlayerFilter {
		public AnyPlayer() {

		}

		@Override
		public boolean matches(String player, String playerContext) {
			return true ^ this.isInverted();
		}

		@Override
		public String toString() {
			return this.exPart() + this.invPart() + "*";
		}

		@Override
		protected void setVariables(String... variable) {

		}
	}

	public static class LiteralPlayer extends PlayerFilter {
		private String name;

		public LiteralPlayer() {

		}

		public LiteralPlayer(String name) {
			this.setName(name);
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public boolean matches(String player, String playerContext) {
			return ((this.getName().equals("~") && player.equalsIgnoreCase(playerContext)) || (player.equalsIgnoreCase(this.getName())))
					^ this.isInverted();
		}

		@Override
		public String toString() {
			return this.exPart() + this.invPart() + this.name;
		}

		@Override
		protected void setVariables(String... variable) {
			this.setName(variable[0]);
		}
	}

	public static class PermissionPlayer extends PlayerFilter {
		private String permission;

		public PermissionPlayer() {

		}

		public PermissionPlayer(String permission) {
			this.setPermission(permission);
		}

		/**
		 * @return the permission
		 */
		public String getPermission() {
			return this.permission;
		}

		/**
		 * @param permission
		 *            the permission to set
		 */
		public void setPermission(String permission) {
			this.permission = permission;
		}

		@Override
		public boolean matches(String player, String playerContext) {
			throw new UnsupportedOperationException(
					"Cannot filter players by permission given player names, permissions of offline players are not checkable.");
		}

		@Override
		public boolean supportsString() {
			return false;
		}

		@Override
		public boolean matches(CommandSender player, String playerContext) {
			return player.hasPermission(this.getPermission()) ^ this.isInverted();
		}

		@Override
		public String toString() {
			return this.exPart() + this.invPart() + "?" + this.getPermission();
		}

		@Override
		protected void setVariables(String... variable) {
			this.setPermission(variable[0]);
		}
	}

	public static class VIPPlayer extends PlayerFilter {
		private Role requiredRole;

		@Override
		public boolean matches(String sender, String playerContext) {
			return PailPipe.getInstance().getVipList().isRoleOrBetter(sender, this.getRequiredRole()) ^ this.isInverted();
		}

		/**
		 * @return the requiredRole
		 */
		public Role getRequiredRole() {
			return this.requiredRole;
		}

		/**
		 * @param requiredRole
		 *            the requiredRole to set
		 */
		public void setRequiredRole(Role requiredRole) {
			this.requiredRole = requiredRole;
		}

		@Override
		protected void setVariables(String... variable) {
			this.setRequiredRole(Role.getFor(variable[0].charAt(0)));
		}

		@Override
		public String toString() {
			return this.exPart() + this.invPart() + "$" + this.getRequiredRole().getId();
		}

	}
}
