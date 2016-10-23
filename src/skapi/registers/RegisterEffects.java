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
import skapi.effects.*;

public class RegisterEffects {
	public static void SKreens() {
		Skript.registerEffect(EffCreate.class, new String[] { "create skreen[ array named] %string% at %location%" } );
		Skript.registerEffect(EffLoadImageToMem.class, new String[] { "load skreen image named %string% to memory" } );
		Skript.registerEffect(EffProcess.class, new String[] { "process skreen image named %string% from url %string%" } );
		Skript.registerEffect(EffRemoveAll.class, new String[] { "remove all skreen arrays" } );
		Skript.registerEffect(EffRemoveAllImagesFromMem.class, new String[] { "remove all skreen images from memory" } );
		Skript.registerEffect(EffRemoveArray.class, new String[] { "remove skreen array named %string%" } );
		Skript.registerEffect(EffRemoveImage.class, new String[] { "remove skreen image named %string%" } );
		Skript.registerEffect(EffRemoveImageFromMem.class, new String[] { "remove skreen image named %string% from memory" } );
		Skript.registerEffect(EffStart.class, new String[] { "start skreen[ array named] %string% with processed image[s] %strings%" } );
		Skript.registerEffect(EffStop.class, new String[] { "stop skreen[ array named] %string%" } );
		Skript.registerEffect(EffTeleportToSKreen.class, new String[] { "teleport %players% to skreen[ named] %string%" } );
	}
	
//	public static void placeHolder() {
//	}
}