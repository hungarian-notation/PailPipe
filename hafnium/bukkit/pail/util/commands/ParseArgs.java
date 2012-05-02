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

import hafnium.bukkit.pail.pipe.PailPipe;
import hafnium.bukkit.pail.util.PailException;
import hafnium.bukkit.pail.util.commands.node.CommandNode;
import hafnium.bukkit.pail.util.text.MessageableException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

// TODO: Clean and document.

public class ParseArgs {
	private final List<String> args;
	private int argPos;

	private final List<Object> execArgs;

	private final List<CommandNode> passedNodes;

	private boolean isHelpRequest;

	private final String alias;

	private final CommandSender sender;

	public ParseArgs(CommandSender sender, String alias, String[] args) {
		this.args = new ArrayList<String>();

		boolean skipFirst = false;

		if ((args.length > 0) && args[0].equalsIgnoreCase("help")) {
			skipFirst = true;
			this.isHelpRequest = true;
		} else
			this.isHelpRequest = false;

		for (int i = (skipFirst ? 1 : 0); i < args.length; i++)
			this.args.add(args[i]);

		this.argPos = 0;

		this.alias = alias;

		this.execArgs = new ArrayList<Object>();
		this.execArgs.add(sender);

		this.passedNodes = new ArrayList<CommandNode>();

		this.sender = sender;
	}

	public boolean isHelpRequest() {
		return this.isHelpRequest;
	}

	public boolean hasMoreArgs() {
		return this.argPos < this.args.size();
	}

	public String peek() {
		return this.args.get(this.argPos);
	}

	public String pop() {
		return this.args.get(this.argPos++);
	}

	public String lastArg() {
		if (this.argPos > 0)
			return this.args.get(this.argPos - 1);
		return this.alias;
	}

	public void addExecArg(Object arg) {
		this.execArgs.add(arg);
	}

	public void invoke(CommandDefinition executor) throws MessageableException {
		for (Method m : executor.getClass().getMethods()) {
			Annotation a = m.getAnnotation(CommandExec.class);
			if (a != null) {

				Class<?>[] classes = m.getParameterTypes();

				if (!(classes.length == this.execArgs.size()))
					continue;

				boolean valid = true;

				for (int i = 0; i < classes.length; i++)
					if (!classes[i].isInstance(this.execArgs.get(i))) {
						valid = false;
						break;
					}

				if (!valid)
					continue;

				try {
					m.invoke(executor, this.execArgs.toArray(new Object[0]));
				} catch (InvocationTargetException e) {
					Throwable cause = e.getCause();

					if (cause instanceof MessageableException)
						throw (MessageableException) cause;

					PailPipe.getInstance().getLogger().log(Level.SEVERE, e.getCause() + " while executing a command.", e.getCause());
				} catch (Exception e) {
					this.argException(executor);
				}

				return;
			}
		}

		this.argException(executor);
	}

	private void argException(CommandDefinition executor) {
		String params = "";
		boolean first = true;

		for (Object arg : this.execArgs) {
			if (!first)
				params += ", ";
			params += arg.getClass().getSimpleName();
			first = false;
		}

		throw new PailException("Expected CommandExec marked method in " + executor.getClass().getName() + " with the parameters to accept: "
				+ params);
	}

	boolean first = true;

	public void notePassing(CommandNode node) {
		if (!this.first)
			this.passedNodes.add(node);

		this.first = false;
	}

	public boolean hasPassedNodes() {
		return this.passedNodes.size() > 0;
	}

	public List<CommandNode> getPassedNodes() {
		return this.passedNodes;
	}

	public CommandSender getSender() {
		return this.sender;
	}

	public String getAlias() {
		return this.alias;
	}
}
