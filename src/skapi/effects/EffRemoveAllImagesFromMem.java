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

public class EffRemoveAllImagesFromMem extends Effect {

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "remove all skreen images from memory";
	}

	@Override
	protected void execute(Event e) {
		Core.Gifs.clear();
		for (final SKreen ledArray2 : Core.SKREENS.values()) {
			ledArray2.setIsRunning(false);
		}
		Core.sendOpMsg(ChatColor.GREEN + "Successfully removed all images from memory!");
		Core.sendOpMsg(ChatColor.GREEN + "Memory Usage: " + Core.memoryUsage() + " Mb");
	}
}