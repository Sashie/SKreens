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
import org.bukkit.plugin.Plugin;

import animation.AnimationLauncher;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import imageprocessing.AnimatedGif;
import skreens.Core;
import skreens.SKreen;

public class EffStart extends Effect {
	private Expression<String> inputIdName;
	private Expression<String> inputImages;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		inputIdName = (Expression<String>) exprs[0];
		inputImages = (Expression<String>) exprs[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "start skreen[ array named] %string% with processed image[s] %strings%";
	}

	@Override
	protected void execute(Event e) {
		String idName = inputIdName.getSingle(e);
		String[] images = inputImages.getAll(e);

		final SKreen skreen = Core.SKREENS.get(idName);
		if (skreen != null) {
			boolean gifIsRunning = false;
			for (int j = 0; j < images.length - 1; ++j) {
				if (Core.gifIsRunning(images[j])) {
					gifIsRunning = true;
					break;
				}
			}
			if (!gifIsRunning) {
				Core.sendOpMsg(ChatColor.GOLD + "Starting image(s)");
				final AnimatedGif[] gifs = new AnimatedGif[images.length];
				skreen.gifNames = new String[images.length];
				boolean previouslyLoaded = true;
				for (int i = 0; i < images.length; ++i) {
					if (!Core.Gifs.containsKey(images[i])) {
						previouslyLoaded = false;
						break;
					}
					gifs[i] = Core.Gifs.get(images[i]);
					skreen.gifNames[i] = gifs[i].getName();
				}
				if (previouslyLoaded) {
					final AnimationLauncher anim = new AnimationLauncher(skreen, false);
					anim.start(gifs);
				} else {
					skreen.gifNames = new String[images.length];
					for (int i = 0; i < images.length; ++i) {
						skreen.gifNames[i] = images[i];
					}
					if (Core.imagesExist(skreen.gifNames)) {
						final AnimationLauncher anim = new AnimationLauncher(skreen, false);
						anim.runTaskAsynchronously((Plugin) Core.pluginInstance);
					} else {
						Core.sendOpMsg(ChatColor.RED + "One or more of those image ids do not exist.");
					}
				}
			} else {
				Core.sendOpMsg(ChatColor.RED + "One or more of those gifs are already running somewhere else.\n" + "A gif can only be run on one SKreen at a time.");
			}
		} else {
			Core.sendOpMsg(ChatColor.RED + "SKreen id does not exist");
		}
	}
}