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
package hafnium.bukkit.pail.pipe.commands;

import hafnium.bukkit.pail.util.hf;
import hafnium.bukkit.pail.util.commands.CommandDefinition;
import hafnium.bukkit.pail.util.commands.CommandExec;
import hafnium.bukkit.pail.util.commands.node.CommandNode;
import hafnium.bukkit.pail.util.commands.node.CommandRoot;
import hafnium.bukkit.pail.util.commands.node.LiteralNode;
import hafnium.bukkit.pail.util.commands.node.StringNode;
import hafnium.bukkit.pail.util.text.PailMessage;

import org.bukkit.command.CommandSender;

public abstract class TestCommand {

	public static CommandNode makeTestController() {

		CommandNode ctrl = new CommandRoot();

		CommandNode getNameLit, getNameUpperLit, slapLit, slapWho, slapHow;

		{
			ctrl.addNode(getNameLit = new LiteralNode(hf.array("getname", "name")));
			getNameLit.addNode(getNameUpperLit = new LiteralNode("upper"));
			getNameLit.setExecutor(new TestCommand.GetName());
			getNameUpperLit.setExecutor(new TestCommand.GetNameUpper());
		}

		{
			ctrl.addNode(slapLit = new LiteralNode(hf.array("slap", "hit", "kick")));
			slapLit.addNode(slapWho = new StringNode("player name"));
			slapWho.addNode(slapHow = new StringNode("how"));

			Slap slap = new Slap();

			slapWho.setExecutor(slap);
			slapHow.setExecutor(slap);
		}

		return ctrl;
	}

	public static class GetName implements CommandDefinition {
		@CommandExec
		public void getName(CommandSender sender) {
			PailMessage.from("^gYour name is ^n" + sender.getName() + "^g.").sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Displays the name of the account you are currently using.";
		}
	}

	public static class GetNameUpper implements CommandDefinition {
		@CommandExec
		public void getName(CommandSender sender) {
			PailMessage.from("^gYour name in upper-case is ^n" + sender.getName().toUpperCase() + "^g.").sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Displays the name of the account you are currently using in upper-case.";
		}
	}

	public static class Slap implements CommandDefinition {
		@CommandExec
		public void slap(CommandSender sender, String who) {
			PailMessage.from("^n" + sender.getName() + " ^gslapped ^n" + who + "^g.").sendToServer();
		}

		@CommandExec
		public void slap(CommandSender sender, String who, String how) {
			PailMessage.from("^n" + sender.getName() + " ^gslapped ^n" + who + "^g " + how + ".").sendToServer();
		}

		@Override
		public String getHelp() {
			return "Slaps the specified person.";
		}
	}
}
