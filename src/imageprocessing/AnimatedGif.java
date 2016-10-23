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

import java.io.Serializable;

import org.bukkit.ChatColor;

public class AnimatedGif implements Serializable {
	private static final long serialVersionUID = 1L;
	public String[][][] pixels;
	private int frameCount;
	private int width;
	private int height;
	private String name;
	public int currentFrame;

	public AnimatedGif(final int frameCount, final int width, final int height, final String name) {
		this.width = width;
		this.height = height;
		this.frameCount = frameCount;
		this.currentFrame = 0;
		this.name = name;
		this.pixels = new String[frameCount][width][height];
	}

	public int getFrameCount() {
		return frameCount;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return ChatColor.AQUA + "'" + name + "'";
	}

	public double sizeInMemory() {
		int sizeInBytes = 0;
		for (int i = 0; i < frameCount; ++i) {
			for (int j = 0; j < width; ++j) {
				for (int k = 0; k < height; ++k) {
					sizeInBytes += 5;
				}
			}
		}
		sizeInBytes += 80;
		sizeInBytes += name.length() * 8;
		return sizeInBytes / 1000000.0;
	}
}
