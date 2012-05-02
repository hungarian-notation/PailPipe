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
package hafnium.bukkit.pail.pipe.manager;

import hafnium.bukkit.pail.pipe.data.AbstractDataController;
import hafnium.bukkit.pail.pipe.data.DataController;
import hafnium.bukkit.pail.pipe.data.DataManager;
import hafnium.bukkit.pail.pipe.data.DataScope;
import hafnium.bukkit.pail.pipe.data.DataUse;
import hafnium.bukkit.pail.pipe.data.DatumID;
import hafnium.bukkit.pail.pipe.data.TextDatum;
import hafnium.bukkit.pail.util.hf;
import hafnium.bukkit.pail.util.commands.CommandDefinition;
import hafnium.bukkit.pail.util.commands.CommandException;
import hafnium.bukkit.pail.util.commands.CommandExec;
import hafnium.bukkit.pail.util.commands.node.CommandNode;
import hafnium.bukkit.pail.util.commands.node.DatumNode;
import hafnium.bukkit.pail.util.commands.node.EnumNode;
import hafnium.bukkit.pail.util.commands.node.IntegerNode;
import hafnium.bukkit.pail.util.commands.node.LiteralNode;
import hafnium.bukkit.pail.util.commands.node.StringNode;
import hafnium.bukkit.pail.util.commands.node.StructuralNode;
import hafnium.bukkit.pail.util.text.MessageableException;
import hafnium.bukkit.pail.util.text.PailMessage;
import hafnium.bukkit.pail.util.text.TextFormatter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class TextManager implements DataManager<TextDatum> {
	private final TextController textData;
	private final StructuralNode node;

	public TextManager() {
		this.textData = new TextController();

		this.node = new StructuralNode();

		LiteralNode add, edit, remove, display, append, list;
		CommandDefinition addDef, editDef, removeDef, displayDef, appendDef, listDef;

		addDef = new AddText();
		editDef = new EditText();
		removeDef = new RemoveText();
		displayDef = new DisplayText();
		appendDef = new AppendText();
		listDef = new ListText();

		this.node.addNode(add = new LiteralNode(DataUse.ADD.getIdiom()));

		{
			CommandNode id, text;
			add.addNode(id = new DatumNode());
			id.addNode(text = new StringNode("text"));

			text.setExecutor(addDef);
		}

		this.node.addNode(edit = new LiteralNode(DataUse.EDIT.getIdiom()));

		{
			CommandNode id, text;
			edit.addNode(id = new DatumNode());
			id.addNode(text = new StringNode("new text"));

			text.setExecutor(editDef);
		}

		this.node.addNode(remove = new LiteralNode(DataUse.REMOVE.getIdiom()));

		{
			CommandNode id;
			remove.addNode(id = new DatumNode());

			id.setExecutor(removeDef);
		}

		this.node.addNode(append = new LiteralNode("append", "+", "postfix"));

		{
			CommandNode id, text;
			append.addNode(id = new DatumNode());
			id.addNode(text = new StringNode("new text"));

			text.setExecutor(appendDef);
		}

		this.node.addNode(display = new LiteralNode("show", "s", "test", "display"));

		{
			CommandNode id;
			display.addNode(id = new DatumNode());

			id.setExecutor(displayDef);
		}

		this.node.addNode(list = new LiteralNode("list"));

		{
			CommandNode scope, page;
			list.addNode(scope = new EnumNode("scope", DataScope.class));
			scope.addNode(page = new IntegerNode("page number", null));

			list.setExecutor(listDef);
			scope.setExecutor(listDef);
			page.setExecutor(listDef);
		}
	}

	@Override
	public DataController<TextDatum> getController() {
		return this.textData;
	}

	@Override
	public String[] getIdiom() {
		return hf.array("text", "txt", "message", "msg", "string");
	}

	@Override
	public StructuralNode getManagementNode() {
		return this.node;
	}

	public void save() {
		this.getController().save();
	}

	public void load() {
		this.getController().load();
	}

	public class AddText implements CommandDefinition {
		@CommandExec
		public void add(CommandSender sender, DatumID dID, String text) throws MessageableException {
			TextController ctrl = (TextController) TextManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.ADD, ctrl);

			if (ctrl.has(dID))
				throw new CommandException("^eText record " + dID.toFormattedString() + " ^ealready exists.");

			ctrl.put(dID, new TextDatum(text));

			PailMessage.from("^gAdded " + dID.toFormattedString() + "^g:\n\t^u" + text).sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Adds a new text record.";
		}
	}

	public class EditText implements CommandDefinition {
		@CommandExec
		public void edit(CommandSender sender, DatumID dID, String newText) throws MessageableException {
			TextController ctrl = (TextController) TextManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.EDIT, ctrl);

			if (!ctrl.has(dID))
				throw new CommandException("^eText record " + dID.toFormattedString() + " ^edoes not exist.");

			ctrl.put(dID, new TextDatum(newText));

			PailMessage.from("^gEdited " + dID.toFormattedString() + "^g:\n\t^u" + newText).sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Edits a text record.";
		}
	}

	// TODO: Confirmation system.

	public class RemoveText implements CommandDefinition {
		@CommandExec
		public void remove(CommandSender sender, DatumID dID) throws MessageableException {
			TextController ctrl = (TextController) TextManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.REMOVE, ctrl);

			if (!ctrl.has(dID))
				throw new CommandException("^eText record " + dID.toFormattedString() + " ^edoes not exist.");

			ctrl.remove(dID);

			PailMessage.from("^gRemoved " + dID.toFormattedString() + "^g.").sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Removes a text record.";
		}
	}

	public class AppendText implements CommandDefinition {
		@CommandExec
		public void append(CommandSender sender, DatumID dID, String newText) throws MessageableException {
			TextController ctrl = (TextController) TextManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.EDIT, ctrl);

			if (!ctrl.has(dID))
				throw new CommandException("^eText record " + dID.toFormattedString() + " ^edoes not exist.");

			newText = ctrl.get(dID) + " " + newText;

			ctrl.put(dID, new TextDatum(newText));

			PailMessage.from("^gAppended " + dID.toFormattedString() + "^g:\n\t^u" + newText).sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Appends text to a text record.";
		}

	}

	public class DisplayText implements CommandDefinition {
		@CommandExec
		public void display(CommandSender sender, DatumID dID) throws MessageableException {
			TextController ctrl = (TextController) TextManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.USE, ctrl);

			if (!ctrl.has(dID))
				throw new CommandException("^eText record " + dID.toFormattedString() + " ^edoes not exist.");

			PailMessage.from(dID.toFormattedString() + "^g:\n").sendTo(sender);
			sender.sendMessage("");
			(new PailMessage(TextFormatter.format(ctrl.get(dID).getData()), false)).sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Displays a text record with formatting.";
		}
	}

	// TODO: Player argument for listing personals from others.

	public class ListText implements CommandDefinition {
		@CommandExec
		public void list(CommandSender sender) throws MessageableException {
			this.list(sender, DataScope.PERSONAL, 1);
		}

		@CommandExec
		public void list(CommandSender sender, DataScope scope) throws MessageableException {
			this.list(sender, scope, 1);
		}

		@CommandExec
		public void list(CommandSender sender, DataScope scope, Integer page) throws MessageableException {
			TextController ctrl = (TextController) TextManager.this.getController();

			DatumID.getFor(sender, "any", scope).assertCanPerform(sender, DataUse.USE, ctrl);

			List<DatumID> ids = ctrl.getIDs(sender, scope);

			ArrayList<String> entries = new ArrayList<String>();

			for (DatumID id : ids)
				entries.add(id.toFormattedString());

			hafnium.bukkit.pail.util.text.ListDisplay.list("Text Records", entries, page, sender);
		}

		@Override
		public String getHelp() {
			return "Lists text records in a given scope.";
		}
	}

	private class TextController extends AbstractDataController<TextDatum> {
		public TextController() {
			super(TextDatum.class, "text", "txt");
		}
	}
}
