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

public class hf {
	public static <T> T[] array(T... args) {
		return args;
	}

	public static <T> T[] array(T[] array, T newArg) {
		@SuppressWarnings("unchecked")
		// Just re-branding an Object array here, nothing to go wrong.
		T[] newArray = (T[]) new Object[array.length + 1];

		for (int i = 0; i < array.length; i++)
			newArray[i] = array[i];

		newArray[array.length] = newArg;

		return newArray;
	}

	public static void debugPattern(Pattern p, String input) {
		Matcher m = p.matcher(input);

		int matches = 0;

		while (m.find()) {
			matches++;
			System.out.println("MATCH:");
			for (int i = 0; i <= m.groupCount(); i++)
				System.out.println("\t" + m.group(i));
		}

		if (matches == 0)
			System.out.println("NO MATCHES");
		else if (matches == 1)
			System.out.println("1 MATCH");
		else
			System.out.println(matches + " MATCHES");
	}

	private static Pattern playerName = Pattern.compile("^" + getPlayerNameRegex() + "$");

	public static String getPlayerNameRegex() {
		return "[a-zA-Z0-9._]+";
	}

	public static Pattern getPlayerNamePattern() {
		return playerName;
	}

	public static boolean isPlayerName(String name) {
		return playerName.matcher(name).matches();
	}

	public static <T> boolean contains(T[] objects, T object) {
		for (T o : objects)
			if (o.equals(object))
				return true;
		return false;
	}

	public static boolean containsIgnoreCase(String[] strings, String string) {
		for (String alias : strings)
			if (alias.equalsIgnoreCase(string))
				return true;

		return false;
	}
}
