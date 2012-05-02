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
package hafnium.bukkit.pail.util.commands;

import hafnium.bukkit.pail.util.commands.node.CommandNode;
import hafnium.bukkit.pail.util.text.PailMessage;

import java.util.ArrayList;
import java.util.List;

// TODO: Comment the shit out of this code.

public class HelpBuilder {
	private HelpBuilder() {
		// No instantiation.
	}

	// TODO: This method gets kinda messy. Perhaps it should be refactored at a
	// later date.

	public static PailMessage getHelp(CommandNode tnode, ParseArgs args) {
		ArrayList<CommandForm> forms = new ArrayList<CommandForm>();
		List<CommandPart> parts = new ArrayList<CommandPart>();

		parts.add(new CommandPart(args.getAlias(), true));

		if (args.hasPassedNodes())
			for (int i = 0; i < args.getPassedNodes().size(); i++)
				parts.add(CommandPart.getFor(args.getPassedNodes().get(i)));

		boolean isExec = buildForms(tnode, parts, forms);
		int discreteCommands = countDiscreteCommands(forms);

		PailMessage message;

		if (forms.size() > 1) {
			message = PailMessage.from("^n\nCommands:");
			for (CommandForm form : forms)
				message.appendLine("\t" + form.getUsage());

			if (isExec || (discreteCommands == 1))
				message.appendLine("^nUsage:");
		} else
			message = PailMessage.from("\n^c" + forms.get(0).getUsage());

		if (isExec)
			message.appendLine("\t^g" + tnode.getExecutor().getHelp());
		else if ((forms.size() == 1) || (discreteCommands == 1))
			message.appendLine("\t^g" + forms.get(0).getHelp());

		return message;
	}

	private static int countDiscreteCommands(List<CommandForm> forms) {
		ArrayList<Class<? extends CommandDefinition>> defs = new ArrayList<Class<? extends CommandDefinition>>();
		for (CommandForm form : forms)
			if (!defs.contains(form.getDefinition().getClass()))
				defs.add(form.getDefinition().getClass());

		return defs.size();
	}

	/**
	 * 
	 * @param cNode
	 * @param parts
	 * @param forms
	 * @param include
	 *            Whether or not to include cNode in the forms. Usually false
	 *            when starting the recursion.
	 * @return
	 */
	private static boolean buildForms(CommandNode cNode, List<CommandPart> parts, List<CommandForm> forms) {
		boolean isExecutable = false; // If the cNode is executable.

		parts.add(CommandPart.getFor(cNode));

		if (cNode.hasExecutor()) {
			forms.add(new CommandForm(parts, cNode.getExecutor()));
			isExecutable = true;
		}

		for (CommandNode node : cNode.getNodes())
			buildForms(node, parts, forms);

		parts.remove(parts.size() - 1);

		return isExecutable;
	}

	private static class CommandForm {
		private final List<CommandPart> parts;
		private final CommandDefinition def;

		public CommandForm(List<CommandPart> parts, CommandDefinition def) {
			this.parts = new ArrayList<CommandPart>(parts);
			this.def = def;
		}

		/**
		 * @return the help
		 */
		public String getHelp() {
			return this.def.getHelp();
		}

		public CommandDefinition getDefinition() {
			return this.def;
		}

		public String getUsage() {
			StringBuffer buf = new StringBuffer();
			buf.append("^c/");

			boolean first = true;

			for (int i = 0; i < this.parts.size(); i++)
				if (this.parts.get(i).isPrintable()) {
					if (!first)
						buf.append(' ');

					buf.append(this.parts.get(i).toString());
					first = false;
				}

			return buf.toString();
		}
	}
}
