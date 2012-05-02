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

import hafnium.bukkit.pail.pipe.plugins.PailPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PailMessage {
	private static final int CLIENT_LINE_LENGTH = 52;

	private final StringBuffer str;
	private final boolean parseColors;

	public PailMessage(String msg, boolean parseColors) {
		this.str = new StringBuffer(msg);
		this.parseColors = parseColors;
	}

	public PailMessage() {
		this("", true);
	}

	// EDITING

	public PailMessage(String message) {
		this(message, true);
	}

	public void append(String msg) {
		this.str.append(msg);
	}

	public void appendLine(String line) {
		this.str.append('\n');
		this.str.append(line);
	}

	// COMPILATION AND DISPLAY

	private List<String> getLines() {
		ArrayList<String> lines = new ArrayList<String>();

		for (String line : this.str.toString().split("\n"))
			lines.add(line);

		return lines;
	}

	private List<String> compile(int lineLength) {
		List<String> lines = new ArrayList<String>();
		List<String> plainLines = this.getLines();

		for (String plainLine : plainLines) {
			if (this.parseColors)
				plainLine = PColor.apply(plainLine);

			String[] words = plainLine.split(" |(?:\\b|\\B)(?=\t)|(?<=\t)(?:\\b|\\B)");

			int wpos = 0;

			while (wpos < words.length) {
				int lpos = 0;

				String cline = "";

				if (lines.size() > 0)
					cline += getLastColor(lines.get(lines.size() - 1), PColor.GENERAL.getColor()).toString();

				do {
					String word = words[wpos];
					int length = ChatColor.stripColor(word).length();

					if (word.equals("\t")) {
						cline += "  ";
						lpos += 2;
					} else {
						cline += word + " ";
						lpos += length + 1;
					}

					++wpos;
				} while ((wpos < words.length) && ((lpos + (words[wpos].equals("\t") ? 2 : ChatColor.stripColor(words[wpos]).length())) < lineLength));

				lines.add(cline);
			}
		}

		return lines;
	}

	private static ChatColor getLastColor(String line, ChatColor def) {
		ChatColor last = def;

		for (int i = 0; i < line.length(); i++)
			if (line.charAt(i) == '§') {
				ChatColor potential = ChatColor.getByChar(line.charAt(i + 1));

				if (potential != null)
					last = potential;
			}

		return last;
	}

	private static String flashFix = ChatColor.BLACK.toString() + ChatColor.WHITE.toString();

	public void sendTo(Player p) {
		List<String> lines = this.compile(CLIENT_LINE_LENGTH);

		for (String line : lines)
			p.sendMessage(flashFix + line);
	}

	public void sendToServer() {
		List<String> lines = this.compile(CLIENT_LINE_LENGTH);

		for (String line : lines)
			org.bukkit.Bukkit.getServer().broadcastMessage(flashFix + line);
	}

	public void sendToConsole() {
		List<String> lines = this.compile(48);
		for (String line : lines)
			System.out.println(ChatColor.stripColor(line));
	}

	public void logAs(Level level, PailPlugin context) {
		List<String> lines = this.compile(48);
		for (String line : lines)
			context.getLogger().log(level, ChatColor.stripColor(line));
	}

	// TYPES

	public static String stripFormatting(String text) {
		return ChatColor.stripColor(PColor.apply(text));
	}

	public static enum PColor {
		GENERAL('g', ChatColor.WHITE), ERROR('e', ChatColor.RED), USER_INPUT('u', ChatColor.BLUE), COMMAND('c', ChatColor.GOLD), NOTEABLE('n',
				ChatColor.AQUA), IDENTIFIER('i', ChatColor.GREEN);

		private final char id;
		private final ChatColor color;

		private PColor(char id, ChatColor color) {
			this.id = id;
			this.color = color;
		}

		public ChatColor getColor() {
			return this.color;
		}

		public char getID() {
			return this.id;
		}

		@Override
		public String toString() {
			return this.getColor().toString();
		}

		public static PColor forID(char id) {
			for (PColor pc : PColor.values())
				if (pc.getID() == id)
					return pc;

			return PColor.GENERAL;
		}

		public static String apply(String line) {
			for (PColor c : PColor.values())
				line = line.replace("^" + c.getID(), c.getColor().toString());

			return line;
		}
	}

	public void sendTo(CommandSender sender) {
		if (sender instanceof Player)
			this.sendTo((Player) sender);
		else
			this.sendToConsole();
	}

	public static PailMessage from(String message) {
		return new PailMessage(message, true);
	}

	public static PailMessage warning(String warning) {
		return new PailMessage("^g[^eWarning]^g: " + warning);
	}
}
