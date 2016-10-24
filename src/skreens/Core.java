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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import animation.AnimationLauncher;
import animation.FramePlayer;
import imageprocessing.AnimatedGif;
import metrics.Metrics;
import skapi.registers.RegisterConditions;
import skapi.registers.RegisterEffects;
import skapi.registers.RegisterExpressions;

public class Core extends JavaPlugin {
	public static Hashtable<String, SKreen> SKREENS;
	public static Hashtable<String, AnimatedGif> Gifs;
	public static Core pluginInstance;
	public static Logger logger;
	public static Hashtable<String, FramePlayer> ActiveFramePlayers;

	public static int maxDisplay;
	public static int maxWidth;
	public static int maxHeight;
	public static boolean debugMsgs;

	public Core() {
		SKREENS = new Hashtable<String, SKreen>();
		Gifs = new Hashtable<String, AnimatedGif>();
		ActiveFramePlayers = new Hashtable<String, FramePlayer>();
		pluginInstance = this;
		logger = this.getLogger();
	}

	@SuppressWarnings("unchecked")
	public void onEnable() {
		startMetrics();
		Plugin skript = Bukkit.getServer().getPluginManager().getPlugin("Skript");
		if (skript != null) {
			RegisterEffects.SKreens();
			RegisterExpressions.SKreens();
			RegisterConditions.SKreens();
			sendLog("SKreens Activated!");
			saveDefaultConfig();
			try {
				File folder = new File("plugins/SKreens");
				if (!folder.exists()) {
					folder.mkdir();
				}
				folder = new File("plugins/SKreens/images");
				if (!folder.exists()) {
					folder.mkdir();
				}
				final File skreens = new File("plugins/SKreens/SKreens.dat");
				if (!skreens.exists()) {
					skreens.createNewFile();
				}
				final FileInputStream fileIn = new FileInputStream(skreens);
				ObjectInputStream in = null;
				if (skreens.length() > 0L) {
					in = new ObjectInputStream(fileIn);
					SKREENS = (Hashtable<String, SKreen>) in.readObject();
					in.close();
					for (final SKreen ledArray : SKREENS.values()) {
						if (ledArray.getIsRunning()) {
							if (imagesExist(ledArray.gifNames)) {
								final AnimationLauncher anim = new AnimationLauncher(ledArray, false);
								anim.runTaskAsynchronously((Plugin) pluginInstance);
							} else {
								ledArray.setIsRunning(false);
							}
						}
					}
				}
			} catch (Exception ex) {
			}
			loadConfigData();
		} else {
			Bukkit.getPluginManager().disablePlugin(pluginInstance);
			sendLog("Plugin is now disabled. Why you no haz Skript?");
		}
	}

	public void onDisable() {
		try {
			final File folder = new File("plugins/SKreens");
			if (!folder.exists()) {
				folder.mkdir();
			}
			final File skreens = new File("plugins/SKreens/SKreens.dat");
			if (!skreens.exists()) {
				skreens.createNewFile();
			}
			final FileOutputStream fileOut = new FileOutputStream("plugins/SKreens/SKreens.dat");
			final ObjectOutputStream out = new ObjectOutputStream(fileOut);
			for (final SKreen skreen : SKREENS.values()) {
				if (ActiveFramePlayers.containsKey(skreen.getName())) {
					skreen.setIsRunning(true);
				} else {
					skreen.setIsRunning(false);
				}
			}
			out.writeObject(SKREENS);
			out.close();
		} catch (Exception ex) {
		}
	}

	public static ArrayList<String> getImageNames() {
		final ArrayList<String> names = new ArrayList<String>();
		final File folder = new File("plugins/SKreens/images");
		File[] files = null;
		if (folder.exists()) {
			files = folder.listFiles();
			File[] array;
			for (int length = (array = files).length, i = 0; i < length; ++i) {
				final File file = array[i];
				if (file.isFile()) {
					names.add(file.getName().replaceAll(".dat", ""));
				}
			}
		}
		return names;
	}

	public static boolean imagesExist(final String[] imgNames) {
		final ArrayList<String> imageNamesFromFile = getImageNames();
		for (final String imgName : imgNames) {
			boolean exists = false;
			for (final String fileName : imageNamesFromFile) {
				if (fileName.equals(imgName)) {
					exists = true;
				}
			}
			if (!exists) {
				return false;
			}
		}
		return true;
	}

	public static String memoryUsage() {
		double memUsage = 0.0;
		for (final AnimatedGif gif : Gifs.values()) {
			memUsage += gif.sizeInMemory();
		}
		final DecimalFormat df = new DecimalFormat("#.##");
		return df.format(memUsage);
	}

	public static boolean gifIsRunning(final String gifName) {
		for (final SKreen ledArray : SKREENS.values()) {
			if (ledArray.getIsRunning()) {
				String[] gifNames;
				for (int length = (gifNames = ledArray.gifNames).length, i = 0; i < length; ++i) {
					final String name = gifNames[i];
					if (name.equals(gifName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void loadConfigData() {
		maxDisplay = getConfig().getInt("display-max");
		maxWidth = getConfig().getInt("display-max-width");
		maxHeight = getConfig().getInt("display-max-height");
		debugMsgs = getConfig().getBoolean("debug-messages");
	}

	public static void sendOpMsg(String string) {
		if (debugMsgs) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.isOp()) {
					player.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "SKreens" + ChatColor.DARK_AQUA
							+ "] " + ChatColor.GREEN + string);
				}
			}
		}
	}

	public static void sendLog(String string) {
		Bukkit.getServer().getLogger().info(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "SKreens" + ChatColor.DARK_AQUA
				+ "] " + ChatColor.GREEN + string);
	}

	private void startMetrics() {
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
	}
}