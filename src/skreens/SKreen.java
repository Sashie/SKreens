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

package skreens;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import imageprocessing.AnimatedGif;
import imageprocessing.MinecraftColor;

public class SKreen implements Serializable {
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private double z;
	private int width;
	private int height;
	private UUID worldUID;
	private String name;
	public String[] gifNames;
	private boolean isRunning;
	public int currentGifIndex;
	public ArrayList<UUID> armorStandUUIDs;

	public SKreen(final World world, final Location loc, final String name, final int width, final int height) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.name = name;
		this.isRunning = false;
		this.gifNames = null;
		this.worldUID = world.getUID();
		this.width = width;
		this.height = height;
		this.currentGifIndex = 0;
		this.armorStandUUIDs = new ArrayList<UUID>();
		this.isRunning = false;
		this.initDisplay(this.gifNames, this.width, this.height);
	}

	public void initDisplay(final String[] gifNames, final int width, final int height) {
		this.gifNames = gifNames;
		this.width = width;
		this.height = height;
		this.armorStandUUIDs = new ArrayList<UUID>();
		final World world = Bukkit.getWorld(this.worldUID);
		final Location loc = new Location(world, this.x, this.y, this.z);
		for (int i = 0; i < 100; ++i) {
			loc.setY(loc.getY() + 0.2185);
			final ArmorStand armorStand = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
			armorStand.setBasePlate(false);
			armorStand.setGravity(false);
			armorStand.setVisible(false);
			String customName = new String();
			if (i >= 48) {
				armorStand.setCustomNameVisible(false);
				customName = " ";
			} else {
				armorStand.setCustomNameVisible(true);
				for (int j = 0; j < width; ++j) {
					customName = String.valueOf(customName) + MinecraftColor.BLACK.symbol();
				}
				armorStand.setCustomName(customName);
			}
			armorStandUUIDs.add(armorStand.getUniqueId());
		}
	}

	public void resizeDisplay(final int width, final int height) {
		final ArmorStand[] armorStands = this.getArmorStands();
		if (this.height >= height) {
			for (int dif = this.height - height, i = 0; i < dif; ++i) {
				armorStands[this.height - 1 - i].setCustomName(" ");
				armorStands[this.height - 1 - i].setCustomNameVisible(false);
			}
		} else {
			for (int dif = height - this.height, i = 0; i < dif; ++i) {
				armorStands[this.height + i].setCustomNameVisible(true);
			}
		}
		this.width = width;
		this.height = height;
	}

	public AnimatedGif getCurrentAnimatedGif() {
		if (this.gifNames != null) {
			return Core.Gifs.get(this.gifNames[this.currentGifIndex]);
		}
		return null;
	}

	public ArmorStand[] getArmorStands() {
		final Chunk chunk = this.getChunk();
		if (chunk != null) {
			final ArmorStand[] armorStands = new ArmorStand[this.armorStandUUIDs.size()];
			Entity[] entities;
			for (int length = (entities = chunk.getEntities()).length, i = 0; i < length; ++i) {
				final Entity entity = entities[i];
				if (entity.getType() == EntityType.ARMOR_STAND) {
					final int index = this.armorStandIndex(entity.getUniqueId());
					if (index != -1) {
						final ArmorStand armorStand = (ArmorStand) entity;
						armorStands[index] = armorStand;
					}
				}
			}
			return armorStands;
		}
		return null;
	}

	private int armorStandIndex(final UUID uuid) {
		int index = -1;
		for (int i = 0; i < this.armorStandUUIDs.size(); ++i) {
			if (this.armorStandUUIDs.get(i).equals(uuid)) {
				index = i;
				break;
			}
		}
		return index;
	}

	public void killArmorStands() {
		final ArmorStand[] armorStands = this.getArmorStands();
		if (armorStands != null) {
			ArmorStand[] array;
			for (int length = (array = armorStands).length, i = 0; i < length; ++i) {
				final ArmorStand armorStand = array[i];
				if (armorStand != null) {
					armorStand.remove();
				}
			}
		}
		this.armorStandUUIDs = new ArrayList<UUID>();
	}

	public Chunk getChunk() {
		final World world = Bukkit.getWorld(this.worldUID);
		final Location loc = new Location(world, this.x, this.y, this.z);
		final Chunk chunk = world.getChunkAt(loc);
		return chunk;
	}

	public void setWidth(final int width) {
		this.width = width;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setLocation(Location loc) {
		this.worldUID = loc.getWorld().getUID();
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
	}

	public Location getLocation() {
		final World world = Bukkit.getWorld(this.worldUID);
		return new Location(world, this.x, this.y, this.z);
	}

	public void setX(final double x) {
		this.x = x;
	}

	public void setY(final double y) {
		this.y = y;
	}

	public void setZ(final double z) {
		this.z = z;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public String getName() {
		return this.name;
	}

	public UUID getWorldUID() {
		return this.worldUID;
	}

	@Override
	public String toString() {
		final String str = ChatColor.AQUA + "'" + this.name + "'";
		return str;
	}

	public void setIsRunning(final boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean getIsRunning() {
		return this.isRunning;
	}
}
