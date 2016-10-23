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

import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

import imageprocessing.AnimatedGif;
import skreens.Core;
import skreens.SKreen;

public class FramePlayer extends BukkitRunnable {
    public SKreen skreen;
    public World world;
    
    public FramePlayer(final SKreen skreen) {
        this.skreen = skreen;
    }
    
    public void run() {
        try {
            AnimatedGif gif = skreen.getCurrentAnimatedGif();
            if (skreen.getChunk().isLoaded() && gif != null) {
                if (skreen.getWidth() != gif.getWidth() || skreen.getHeight() != gif.getHeight()) {
                    skreen.resizeDisplay(gif.getWidth(), gif.getHeight());
                }
                if (gif.currentFrame >= gif.getFrameCount()) {
                    final SKreen ledArray = skreen;
                    ++ledArray.currentGifIndex;
                    if (skreen.currentGifIndex >= skreen.gifNames.length) {
                        skreen.currentGifIndex = 0;
                    }
                    gif.currentFrame = 0;
                    gif = skreen.getCurrentAnimatedGif();
                    gif.currentFrame = 0;
                    if (skreen.getWidth() != gif.getWidth() || skreen.getHeight() != gif.getHeight()) {
                        skreen.resizeDisplay(gif.getWidth(), gif.getHeight());
                    }
                }
                for (int y = 0; y < gif.getHeight(); ++y) {
                    String customName = new String();
                    for (int x = 0; x < gif.getWidth(); ++x) {
                        customName = String.valueOf(customName) + gif.pixels[gif.currentFrame][x][y];
                    }
                    final ArmorStand[] armorStands = skreen.getArmorStands();
                    if (armorStands != null && armorStands[skreen.getHeight() - y - 1] != null) {
                        armorStands[skreen.getHeight() - y - 1].setCustomName(customName);
                    }
                }
                final AnimatedGif animatedGif = gif;
                ++animatedGif.currentFrame;
            }
        }
        catch (Exception e) {
            Core.ActiveFramePlayers.remove(skreen.getName());
            skreen.setIsRunning(false);
            skreen.gifNames = null;
            skreen.currentGifIndex = 0;
            cancel();
        }
    }
}
