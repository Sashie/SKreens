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

import animation.FramePlayer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import skreens.Core;
import skreens.SKreen;

public class EffStop extends Effect {
	private Expression<String> inputIdName;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		inputIdName = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "stop skreen[ array named] %string%";
	}

	@Override
	protected void execute(Event e) {
		String idName = inputIdName.getSingle(e);

		final FramePlayer framePlayer = Core.ActiveFramePlayers.get(idName);
		if (framePlayer != null) {
			Core.ActiveFramePlayers.remove(idName);
			framePlayer.cancel();
			final SKreen ledArray = Core.SKREENS.get(idName);
			if (ledArray != null) {
				ledArray.setIsRunning(false);
				ledArray.currentGifIndex = 0;
				ledArray.gifNames = null;
			}
			Core.sendOpMsg(ChatColor.GOLD + "Stopped " + ChatColor.AQUA + "'" + idName + "'");
		} else {
			Core.sendOpMsg(ChatColor.RED + "SKreen is not running");
		}
	}
}