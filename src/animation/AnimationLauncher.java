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

package animation;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import imageprocessing.AnimatedGif;
import skreens.Core;
import skreens.SKreen;

public class AnimationLauncher extends BukkitRunnable {
	private SKreen skreen;
	private boolean load;

	public AnimationLauncher(final SKreen skreen, final boolean load) {
		this.skreen = skreen;
		this.load = load;
	}

	public void run() {
		final AnimatedGif[] gifs = new AnimatedGif[skreen.gifNames.length];
		if (load) {
			int index = 0;
			String[] gifNames;
			for (int length = (gifNames = skreen.gifNames).length, i = 0; i < length; ++i) {
				final String gifName = gifNames[i];
				gifs[index] = Core.Gifs.get(gifName);
				++index;
			}
			Core.sendOpMsg(ChatColor.GOLD + "Image(s) loaded successfully!");

		} else if (skreen.gifNames != null) {
			int index = 0;
			final String[] gifNames2;
			final int length2 = (gifNames2 = skreen.gifNames).length;
			int j = 0;
			while (j < length2) {
				final String gifName = gifNames2[j];
				AnimatedGif gif = null;
				final File imageFile = new File("plugins/SKreens/images/" + gifName + ".dat");
				Label_0290: {
					if (imageFile.exists()) {
						Label_0330: {
							try {
								final FileInputStream fileIn = new FileInputStream(imageFile);
								ObjectInputStream in = null;
								if (imageFile.length() > 0L) {
									in = new ObjectInputStream(fileIn);
									gif = (AnimatedGif) in.readObject();
									in.close();
									gifs[index] = gif;
								}
								break Label_0330;
							} catch (Exception e) {
								Core.sendOpMsg(ChatColor.RED + "Error processing the image");
								break;
							}
						}
						++index;
						++j;
						continue;
					} else {
						break Label_0290;
					}
				}
				Core.sendOpMsg(ChatColor.RED + "One or more of those image ids do not exist.");
				break;
			}
			AnimatedGif[] array;
			for (int length3 = (array = gifs).length, k = 0; k < length3; ++k) {
				final AnimatedGif gif2 = array[k];
				Core.Gifs.put(gif2.getName(), gif2);
			}
			start(gifs);
		}
	}

	public void start(final AnimatedGif[] gifs) {
		if (gifs != null && gifs.length > 0) {

			final FramePlayer framePlayer = new FramePlayer(skreen);
			final FramePlayer fp = Core.ActiveFramePlayers.get(skreen.getName());
			if (fp != null) {
				Core.ActiveFramePlayers.remove(skreen.getName());
				fp.cancel();
			}
			skreen.setIsRunning(true);
			skreen.currentGifIndex = 0;
			Core.ActiveFramePlayers.put(skreen.getName(), framePlayer);
			framePlayer.runTaskTimer((Plugin) Core.pluginInstance, 0L, 2L);
			if (gifs[0] != null) {
				Core.sendOpMsg(String.valueOf(gifs[0].toString()) + ChatColor.GOLD + " started!");
			}
		} else {
			Core.sendOpMsg(ChatColor.RED + "Image id does not exist");
		}
	}
}
