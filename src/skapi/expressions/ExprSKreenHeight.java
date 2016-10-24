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

package skapi.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import skreens.Core;

import org.bukkit.event.Event;

import animation.FramePlayer;

import javax.annotation.Nullable;

/**
 * Edited by Sashie on 10/20/2016
 */
//Skript.registerExpression(ExprIDNames.class, String.class, ExpressionType.PROPERTY, "[all ]skreen array names");
public class ExprSKreenHeight extends SimpleExpression<Number> {
	private Expression<String> inputIdName;

    @Override
    @Nullable
    protected Number[] get(Event e) {
        final FramePlayer framePlayer = Core.ActiveFramePlayers.get(inputIdName.getSingle(e));
		return new Number[]{framePlayer.skreen.getHeight()};
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean init(Expression<?>[] expr, int i, Kleenean k, SkriptParser.ParseResult p) {
    	inputIdName = (Expression<String>) expr[0];
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public String toString(@Nullable Event e, boolean b) {
        return getClass().getName();
    }
}
