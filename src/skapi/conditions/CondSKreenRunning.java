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

package skapi.conditions;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import skreens.Core;

import org.bukkit.event.Event;

import animation.FramePlayer;

//Skript.registerCondition(CondSKreenRunning.class, "skreen is running");
public class CondSKreenRunning extends Condition {
    private Expression<String> inputIdName;
    
    @Override
    public boolean check(Event e) {
    	final FramePlayer framePlayer = Core.ActiveFramePlayers.get(inputIdName.getSingle(e));
    	if (framePlayer != null) {
            return framePlayer.skreen.getIsRunning();
        }else{
            return false;
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return getClass().getName();
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean init(Expression<?>[] expr, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
    	inputIdName = (Expression<String>) expr[0];
        return true;
    }
}