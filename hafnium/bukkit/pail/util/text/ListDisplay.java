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

import java.util.Comparator;
import java.util.List;

import org.bukkit.command.CommandSender;

public class ListDisplay {
	private static final int itemsPerPage = 8;

	public static void list(String title, List<String> items, int page, CommandSender sender) {
		list(title, items, page, sender, true);
	}

	public static void list(String title, List<String> items, int page, CommandSender sender, boolean sort) {
		if (sort)
			java.util.Collections.sort(items, new Comparator<String>() {
				@Override
				public int compare(String a, String b) {
					return PailMessage.stripFormatting(a).compareToIgnoreCase(PailMessage.stripFormatting(b));
				}
			});

		int totalPages = (items.size() / itemsPerPage) + (items.size() % itemsPerPage != 0 ? 1 : 0);

		if (totalPages == 0)
			PailMessage.from("^n" + title + " ^ehas no entries.").sendTo(sender);
		else if (page > totalPages)
			PailMessage.from("^n" + title + " ^eonly has ^n" + totalPages + " ^epages.").sendTo(sender);
		else if (page <= 0)
			PailMessage.from("^ePage number must be greater than 0.");
		else {
			int min = (page - 1) * itemsPerPage;
			int max = min + (itemsPerPage - 1);

			PailMessage msg = PailMessage.from("^n" + title + "^g(^n" + page + " ^g/ ^n" + totalPages + "^g)^n:");
			for (int i = min; (i < max) && (i < items.size()); i++)
				msg.appendLine("\t" + items.get(i));
			msg.sendTo(sender);
		}
	}
}
