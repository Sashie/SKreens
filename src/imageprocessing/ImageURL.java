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

package imageprocessing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import skreens.Core;

public class ImageURL extends BukkitRunnable {
    private String webURL;
    private String gifName;
    
    public ImageURL(final String webURL, final String gifName) {
        this.webURL = webURL;
        this.gifName = gifName;
    }
    
    public void run() {
        URL url = null;
        try {
            url = new URL(webURL);
            final URLConnection connection = url.openConnection();
            connection.setReadTimeout(20000);
            final InputStream is = connection.getInputStream();
            final GifDecoder decoder = new GifDecoder();
            final int code = decoder.read(is);
            if (code == 0) {
                int width = decoder.getFrameSize().width;
                int height = decoder.getFrameSize().height;
                final int configWidth = Core.maxWidth;
                final int configHeight = Core.maxHeight;
                if (decoder.getFrameSize().height > configHeight) {
                	Core.sendOpMsg(ChatColor.GOLD + "Resizing image...");
                    final BufferedImage frame = decoder.getFrame(0);
                    double ratio = 0.0;
                    height = configHeight;
                    ratio = frame.getWidth() / frame.getHeight();
                    width = (int)(height * ratio);
                    if (width > configWidth) {
                        width = configWidth;
                    }
                }
                final AnimatedGif gif = new AnimatedGif(decoder.getFrameCount(), width, height, gifName);
                for (int i = 0; i < decoder.getFrameCount(); ++i) {
                    BufferedImage frame2 = decoder.getFrame(i);
                    if (frame2.getHeight() > height) {
                        final int type = (frame2.getType() == 0) ? 2 : frame2.getType();
                        frame2 = GifDecoder.resizeImageWithHint(frame2, type, width, height);
                    }
                    for (int x = 0; x < width; ++x) {
                        for (int y = 0; y < height; ++y) {
                            final int rgb = frame2.getRGB(x, y);
                            MinecraftColor closestColor = null;
                            final int mask = 255;
                            final int alpha = rgb >> 24 & mask;
                            final int red = rgb >> 16 & mask;
                            final int green = rgb >> 8 & mask;
                            final int blue = rgb & mask;
                            int difRGB = Integer.MAX_VALUE;
                            MinecraftColor[] values;
                            for (int length = (values = MinecraftColor.values()).length, j = 0; j < length; ++j) {
                                final MinecraftColor color = values[j];
                                final int r = color.red();
                                final int g = color.green();
                                final int b = color.blue();
                                final int dif = Math.abs(r - red) + Math.abs(g - green) + Math.abs(b - blue);
                                if (dif < difRGB) {
                                    difRGB = dif;
                                    closestColor = color;
                                }
                            }
                            if (alpha == 0) {
                                closestColor = MinecraftColor.TRANSPARENT;
                            }
                            gif.pixels[i][x][y] = closestColor.symbol();
                        }
                    }
                }
                Core.Gifs.put(gifName, gif);
                final File animGif = new File("plugins/SKreens/images/" + gifName + ".dat");
                if (!animGif.exists()) {
                    animGif.createNewFile();
                }
                final FileOutputStream fileOut = new FileOutputStream("plugins/SKreens/images/" + gifName + ".dat");
                final ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(gif);
                out.close();
                String message = ChatColor.GOLD + "Image ";
                message = String.valueOf(message) + ChatColor.AQUA + "'" + gifName + "'";
                message = String.valueOf(message) + ChatColor.GOLD + " was successfully processed!";
                Core.sendOpMsg(message);
            } else {
            	Core.sendOpMsg(ChatColor.RED + "Url not reachable or gif type not supported. Try a different url.");
            }
        }
        catch (Exception e) {
        	Core.sendOpMsg(ChatColor.RED + "Invalid URL, filetype, or config values. Try a different image.\n");
        }
    }
}
