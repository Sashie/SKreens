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

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import skreens.Core;
import skreens.SKreen;

public class EffTeleportToSKreen extends Effect {
	private Expression<String> inputIdName;
	private Expression<Player> inputPlayers;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		inputPlayers = (Expression<Player>) exprs[0];
		inputIdName = (Expression<String>) exprs[1];
		return true;
	}
	
	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "teleport %players% to skreen[ named] %string%";
	}
	
	@Override
	protected void execute(Event e) {
		String idName = inputIdName.getSingle(e);
		List<Player> players = Arrays.asList(inputPlayers.getAll(e));
		
		if (Core.SKREENS.containsKey(idName)) {
            final SKreen skreen = Core.SKREENS.get(idName);
            final Location loc = new Location(Bukkit.getWorld(skreen.getWorldUID()), skreen.getX(), skreen.getY(), skreen.getZ());
            for (Player player : players) {
            	player.teleport(loc);
            }
            Core.sendOpMsg(ChatColor.GREEN + "Teleported to " + ChatColor.AQUA + "'" + idName + "'");
        }
        else {
        	Core.sendOpMsg(ChatColor.RED + "That array id does not exist");
        }
	}
}