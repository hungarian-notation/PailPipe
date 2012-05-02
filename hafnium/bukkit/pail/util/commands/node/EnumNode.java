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
package hafnium.bukkit.pail.util.commands.node;

import hafnium.bukkit.pail.util.AliasedEnum;
import hafnium.bukkit.pail.util.hf;
import hafnium.bukkit.pail.util.commands.CommandPart;
import hafnium.bukkit.pail.util.commands.ParseArgs;
import hafnium.bukkit.pail.util.text.MessageableException;

public class EnumNode extends CaptureNode {

	private final Enum<?>[] options;

	public EnumNode(String expectation, Class<? extends Enum<?>> clazz) {
		super(expectation);
		this.options = clazz.getEnumConstants();
	}

	@Override
	public boolean parse(ParseArgs args) throws MessageableException {
		String arg = args.peek();

		for (Enum<?> e : this.options)
			if (e.name().equalsIgnoreCase(arg) || ((e instanceof AliasedEnum) && hf.containsIgnoreCase(((AliasedEnum) e).getAliases(), arg))) {
				args.pop();
				args.addExecArg(e);
				this.passOn(args);
				return true;
			}

		return false;
	}

	@Override
	protected CommandPart[] getFullTags() {
		CommandPart[] parts = new CommandPart[this.options.length];
		for (int i = 0; i < this.options.length; i++)
			parts[i] = new CommandPart(this.options[i].name().toLowerCase(), true);
		return parts;
	}
}
