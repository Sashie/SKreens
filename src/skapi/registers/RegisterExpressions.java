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

package skapi.registers;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import skapi.expressions.*;

public class RegisterExpressions {
	public static void SKreens() {
		Skript.registerExpression(ExprArrayNames.class,String.class,ExpressionType.PROPERTY,"[all ]skreen array names");
		Skript.registerExpression(ExprImageNames.class,String.class,ExpressionType.PROPERTY,"[all ]skreen image names");
		Skript.registerExpression(ExprRunningArrayNames.class,String.class,ExpressionType.PROPERTY,"[all ]skreen running array names");
		Skript.registerExpression(ExprRunningImageNames.class,String.class,ExpressionType.PROPERTY,"[all ]skreen running image names");
	}
}