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

import hafnium.bukkit.pail.pipe.PailPipe;
import hafnium.bukkit.pail.pipe.data.AbstractDataController;
import hafnium.bukkit.pail.pipe.data.AreaDatum;
import hafnium.bukkit.pail.pipe.data.DataController;
import hafnium.bukkit.pail.pipe.data.DataManager;
import hafnium.bukkit.pail.pipe.data.DataScope;
import hafnium.bukkit.pail.pipe.data.DataUse;
import hafnium.bukkit.pail.pipe.data.DatumID;
import hafnium.bukkit.pail.pipe.players.PailPlayer;
import hafnium.bukkit.pail.util.Region;
import hafnium.bukkit.pail.util.hf;
import hafnium.bukkit.pail.util.commands.CommandDefinition;
import hafnium.bukkit.pail.util.commands.CommandException;
import hafnium.bukkit.pail.util.commands.CommandExec;
import hafnium.bukkit.pail.util.commands.CommandUtil;
import hafnium.bukkit.pail.util.commands.node.CommandNode;
import hafnium.bukkit.pail.util.commands.node.DatumNode;
import hafnium.bukkit.pail.util.commands.node.EnumNode;
import hafnium.bukkit.pail.util.commands.node.IntegerNode;
import hafnium.bukkit.pail.util.commands.node.LiteralNode;
import hafnium.bukkit.pail.util.commands.node.StructuralNode;
import hafnium.bukkit.pail.util.text.MessageableException;
import hafnium.bukkit.pail.util.text.PailMessage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreaManager implements DataManager<AreaDatum> {
	private final AreaController areaData;
	private final StructuralNode node;

	public AreaManager() {
		this.areaData = new AreaController();
		this.node = new StructuralNode();

		LiteralNode add, edit, remove, restore, list;
		CommandDefinition addDef, editDef, removeDef, restoreDef, listDef;

		addDef = new AddArea();
		editDef = new EditArea();
		removeDef = new RemoveArea();
		restoreDef = new RestoreArea();
		listDef = new ListAreas();

		this.node.addNode(add = new LiteralNode(DataUse.ADD.getIdiom()));

		{
			CommandNode id;
			add.addNode(id = new DatumNode());
			id.setExecutor(addDef);
		}

		this.node.addNode(edit = new LiteralNode(DataUse.EDIT.getIdiom()));

		{
			CommandNode id;
			edit.addNode(id = new DatumNode());
			id.setExecutor(editDef);
		}

		this.node.addNode(remove = new LiteralNode(DataUse.REMOVE.getIdiom()));

		{
			CommandNode id;
			remove.addNode(id = new DatumNode());
			id.setExecutor(removeDef);
		}

		this.node.addNode(restore = new LiteralNode(hf.array("restore", "show")));

		{
			CommandNode id;
			restore.addNode(id = new DatumNode());
			id.setExecutor(restoreDef);
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
	public DataController<AreaDatum> getController() {
		return this.areaData;
	}

	@Override
	public StructuralNode getManagementNode() {
		return this.node;
	}

	@Override
	public String[] getIdiom() {
		return hf.array("area", "region");
	}

	public class AddArea implements CommandDefinition {
		@CommandExec
		public void addArea(CommandSender sender, DatumID dID) throws MessageableException {
			CommandUtil.assertIsPlayer(sender);

			AreaController ctrl = (AreaController) AreaManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.ADD, ctrl);

			if (ctrl.has(dID))
				throw new CommandException("^eText record " + dID.toFormattedString() + " ^ealready exists.");

			PailPlayer player = PailPipe.getInstance().getPlayerManager().getPlayer((Player) sender);

			Region region = player.getDefinedRegion();

			if (region == null)
				throw new CommandException("^eYou need to define coordinates A and B first. The coordinate wand is ^n"
						+ PailPipe.getInstance().getCoordinateManager().getWandName() + "^e.");

			AreaDatum area = new AreaDatum(region);

			ctrl.put(dID, area);

			PailMessage.from("^gAdded " + dID.toFormattedString() + "^g.").sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Creates a new area as defined by coordinates A and B.";
		}
	}

	public class EditArea implements CommandDefinition {
		@CommandExec
		public void editArea(CommandSender sender, DatumID dID) throws MessageableException {
			AreaController ctrl = (AreaController) AreaManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.EDIT, ctrl);

			if (!ctrl.has(dID))
				throw new CommandException("^eArea " + dID.toFormattedString() + " ^edoes not exist.");

			AreaDatum extantArea = ctrl.get(dID);
			AreaDatum newArea = extantArea.redefine();

			ctrl.put(dID, newArea);

			PailMessage.from("^gEdited " + dID.toFormattedString() + "^g.").sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Modifies an area to reflect the current state of the region it encompasses.";
		}

	}

	public class RemoveArea implements CommandDefinition {
		@CommandExec
		public void removeArea(CommandSender sender, DatumID dID) throws MessageableException {
			AreaController ctrl = (AreaController) AreaManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.REMOVE, ctrl);

			if (!ctrl.has(dID))
				throw new CommandException("^eArea " + dID.toFormattedString() + " ^edoes not exist.");

			ctrl.remove(dID);

			PailMessage.from("^gRemoved " + dID.toFormattedString() + "^g.").sendTo(sender);
		}

		@Override
		public String getHelp() {
			return "Removes an area. This makes no changes to the world, but just removes the stored form of an area.";
		}

	}

	public class RestoreArea implements CommandDefinition {
		@CommandExec
		public void restoreArea(CommandSender sender, DatumID dID) throws MessageableException {
			AreaController ctrl = (AreaController) AreaManager.this.getController();

			ctrl.fill(dID, sender);

			dID.assertCanPerform(sender, DataUse.USE, ctrl);

			if (!ctrl.has(dID))
				throw new CommandException("^eArea " + dID.toFormattedString() + " ^edoes not exist.");

			AreaDatum area = ctrl.get(dID);

			PailMessage.from("^gRestoring " + dID.toFormattedString() + "^g...").sendTo(sender);

			area.restore();
		}

		@Override
		public String getHelp() {
			return "Restores the region an area encompasses to the state it was in when the area was defined.";
		}

	}

	public class ListAreas implements CommandDefinition {
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
			AreaController ctrl = (AreaController) AreaManager.this.getController();

			DatumID.getFor(sender, "any", scope).assertCanPerform(sender, DataUse.USE, ctrl);

			List<DatumID> ids = ctrl.getIDs(sender, scope);

			ArrayList<String> entries = new ArrayList<String>();

			for (DatumID id : ids)
				entries.add(id.toFormattedString());

			hafnium.bukkit.pail.util.text.ListDisplay.list("Areas", entries, page, sender);
		}

		@Override
		public String getHelp() {
			return "Lists areas in a given scope.";
		}
	}

	private class AreaController extends AbstractDataController<AreaDatum> {
		public AreaController() {
			super(AreaDatum.class, "area", "area");
		}
	}

	public void load() {
		this.getController().load();
	}

	public void save() {
		this.getController().save();
	}
}
