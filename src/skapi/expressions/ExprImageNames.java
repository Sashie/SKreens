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

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Edited by Sashie on 10/20/2016
 */
//Skript.registerExpression(ExprImageNames.class, String.class, ExpressionType.PROPERTY, "[all ]skreen image names");
public class ExprImageNames extends SimpleExpression<String> {

    @Override
    @Nullable
    protected String[] get(Event e) {
    	final ArrayList<String> imageNames = Core.getImageNames();
        return imageNames.toArray(new String[imageNames.size()]);
    }

    @Override
    public boolean init(Expression<?>[] e, int i, Kleenean k, SkriptParser.ParseResult p) {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
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
