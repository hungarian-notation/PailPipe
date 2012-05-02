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

import hafnium.bukkit.pail.util.hf;
import hafnium.bukkit.pail.util.commands.CommandDefinition;
import hafnium.bukkit.pail.util.commands.CommandException;
import hafnium.bukkit.pail.util.commands.CommandPart;
import hafnium.bukkit.pail.util.commands.HelpBuilder;
import hafnium.bukkit.pail.util.commands.ParseArgs;
import hafnium.bukkit.pail.util.text.MessageableException;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// TODO: Automatic tree generation from CommandDefinition instances.

public abstract class CommandNode implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] sargs) {
		ParseArgs args = new ParseArgs(sender, alias, sargs);

		try {
			this.passOn(args);
		} catch (MessageableException e) {
			e.announce(sender);
		}

		return true;
	}

	// Parsing

	protected void passOn(ParseArgs args) throws MessageableException {
		if (args.hasMoreArgs()) {
			args.notePassing(this);

			for (CommandNode n : this.nodes)
				if (n.parse(args))
					return;

			throw new CommandException("^eI could not understand ^u"
					+ args.peek()
					+ "^e.\n"
					+ (this.getNodes().size() > 0 ? "After ^u" + args.lastArg() + "^e, I expected: ^c" + this.getExpectations()
							: "I did not expect any arguments after^u" + args.lastArg() + "."));
		} else if (args.isHelpRequest())
			this.help(args);
		else if (this.hasExecutor())
			this.execute(args);
		else
			throw new CommandException("^eAfter ^u" + args.lastArg() + "^e, I expected: ^c" + this.getExpectations());
	}

	public abstract boolean parse(ParseArgs args) throws MessageableException;

	// Type Management

	public abstract boolean isLiteral();

	public abstract String getTag();

	// Tree Management

	private final ArrayList<CommandNode> nodes = new ArrayList<CommandNode>();

	public void addNode(CommandNode node) {
		this.nodes.add(node);
	}

	public List<CommandNode> getNodes() {
		return this.nodes;
	}

	// Execution Management

	private CommandDefinition executor = null;

	public void setExecutor(CommandDefinition executor) {
		this.executor = executor;
	}

	public boolean hasExecutor() {
		return this.executor != null;
	}

	public CommandDefinition getExecutor() {
		return this.executor;
	}

	private void execute(ParseArgs args) throws MessageableException {
		System.out.println("Executing.");

		args.invoke(this.getExecutor());
	}

	// Help Management

	private void help(ParseArgs args) {
		HelpBuilder.getHelp(this, args).sendTo(args.getSender());
	}

	protected CommandPart[] getFullTags() {
		return hf.array(new CommandPart(this.getTag(), this.isLiteral()));
	}

	protected String getExpectations() {
		StringBuffer buf = new StringBuffer();

		boolean first = true;

		for (CommandPart part : this.getExpectationList()) {
			if (!first)
				buf.append("^e, ");
			buf.append(part.toString());

			first = false;
		}

		return buf.toString();
	}

	protected CommandPart[] getExpectationList() {
		ArrayList<CommandPart> parts = new ArrayList<CommandPart>();

		for (int i = 0; i < this.nodes.size(); i++)
			for (CommandPart part : this.nodes.get(i).getFullTags())
				parts.add(part);

		return parts.toArray(new CommandPart[0]);
	}
}
