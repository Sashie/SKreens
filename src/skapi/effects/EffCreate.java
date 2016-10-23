/*
	This file is part of SKreens - A Skript addon
      
	Copyright (C) 2016  Sashie

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package skapi.effects;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import skreens.Core;
import skreens.SKreen;

public class EffCreate extends Effect {
	private Expression<String> idName;
	private Expression<Location> loc;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		idName = (Expression<String>) exprs[0];
		loc = (Expression<Location>) exprs[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "create skreen[ array named] %string% at %location%";
	}

	@Override
	protected void execute(Event e) {
		String idName = (String) this.idName.getSingle(e);
		Location center = loc.getSingle(e);

		if (!Core.SKREENS.containsKey(idName)) {
			int displayMax = 0;
			try {
				displayMax = Core.maxDisplay;
				if (Core.SKREENS.size() < displayMax) {
					final SKreen ledArray = new SKreen(center.getWorld(), center, idName, 48, 48);
					Core.SKREENS.put(idName, ledArray);
					Core.sendOpMsg(ChatColor.GOLD + "Successfully created " + ChatColor.AQUA + "'" + idName + "'");
				} else {
					Core.sendOpMsg(ChatColor.RED
							+ "The max number of allowed displays has been reached! Delete an array or increase the max value of displays allowed in config.");
				}
			} catch (Exception ex) {
				Core.sendOpMsg(ChatColor.RED + "Incorrect display-max config value. Value must be an integer.");
			}
		} else {
			Core.sendOpMsg(ChatColor.RED + "That SKreen name is already taken");
		}
	}
}