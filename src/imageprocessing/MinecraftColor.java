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

public enum MinecraftColor implements Serializable {
    BLACK("BLACK", 0, "00", "00", "00", "§0\u2588"), 
    DARK_BLUE("DARK_BLUE", 1, "00", "00", "AA", "§1\u2588"), 
    DARK_GREEN("DARK_GREEN", 2, "00", "AA", "00", "§2\u2588"), 
    DARK_AQUA("DARK_AQUA", 3, "00", "AA", "AA", "§3\u2588"), 
    DARK_RED("DARK_RED", 4, "AA", "00", "00", "§4\u2588"), 
    DARK_PURPLE("DARK_PURPLE", 5, "AA", "00", "AA", "§5\u2588"), 
    GOLD("GOLD", 6, "FF", "AA", "00", "§6\u2588"), 
    GRAY("GRAY", 7, "AA", "AA", "AA", "§7\u2588"), 
    DARK_GRAY("DARK_GRAY", 8, "55", "55", "55", "§8\u2588"), 
    BLUE("BLUE", 9, "55", "55", "FF", "§9\u2588"), 
    GREEN("GREEN", 10, "55", "FF", "55", "§a\u2588"), 
    AQUA("AQUA", 11, "55", "FF", "FF", "§b\u2588"), 
    RED("RED", 12, "FF", "55", "55", "§c\u2588"), 
    LIGHT_PURPLE("LIGHT_PURPLE", 13, "FF", "55", "FF", "§d\u2588"), 
    YELLOW("YELLOW", 14, "FF", "FF", "55", "§e\u2588"), 
    WHITE("WHITE", 15, "FF", "FF", "FF", "§f\u2588"), 
    TRANSPARENT("TRANSPARENT", 16, "00", "00", "00", "§f\u2588");
    
    private String red;
    private String green;
    private String blue;
    private String symbol;
    
    private MinecraftColor(final String s, final int n, final String red, final String green, final String blue, final String symbol) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.symbol = symbol;
    }
    
    public int red() {
        return Integer.parseInt(red, 16);
    }
    
    public int green() {
        return Integer.parseInt(green, 16);
    }
    
    public int blue() {
        return Integer.parseInt(blue, 16);
    }
    
    public String symbol() {
        return symbol;
    }
}
