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
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import skreens.Core;
import skreens.SKreen;

public class EffRemoveImageFromMem extends Effect {
	private Expression<String> inputIdName;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		inputIdName = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "remove skreen image named %string% from memory";
	}

	@Override
	protected void execute(Event e) {
		String idName = inputIdName.getSingle(e);

		if (Core.Gifs.containsKey(idName)) {
			Core.Gifs.remove(idName);
			for (final SKreen ledArray2 : Core.SKREENS.values()) {
				String[] gifNames;
				for (int length = (gifNames = ledArray2.gifNames).length, l = 0; l < length; ++l) {
					final String gifName = gifNames[l];
					if (gifName.equals(idName)) {
						ledArray2.setIsRunning(false);
					}
				}
			}
			Core.sendOpMsg(ChatColor.GREEN + "Successfully removed image from memory!");
		} else {
			Core.sendOpMsg(ChatColor.RED + "That image is not loaded in currently loaded in memory");
		}
		Core.sendOpMsg(ChatColor.GREEN + "Memory Usage: " + Core.memoryUsage() + " Mb");
	}
}