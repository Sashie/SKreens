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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class GifDecoder {
    public static final int STATUS_OK = 0;
    public static final int STATUS_FORMAT_ERROR = 1;
    public static final int STATUS_OPEN_ERROR = 2;
    protected BufferedInputStream in;
    protected int status;
    protected int width;
    protected int height;
    protected boolean gctFlag;
    protected int gctSize;
    protected int loopCount;
    protected int[] gct;
    protected int[] lct;
    protected int[] act;
    protected int bgIndex;
    protected int bgColor;
    protected int lastBgColor;
    protected int pixelAspect;
    protected boolean lctFlag;
    protected boolean interlace;
    protected int lctSize;
    protected int ix;
    protected int iy;
    protected int iw;
    protected int ih;
    protected Rectangle lastRect;
    protected BufferedImage image;
    protected BufferedImage lastImage;
    protected byte[] block;
    protected int blockSize;
    protected int dispose;
    protected int lastDispose;
    protected boolean transparency;
    protected int delay;
    protected int transIndex;
    protected static final int MaxStackSize = 4096;
    protected short[] prefix;
    protected byte[] suffix;
    protected byte[] pixelStack;
    protected byte[] pixels;
    protected ArrayList<GifFrame> frames;
    protected int frameCount;
    
    public GifDecoder() {
        this.loopCount = 1;
        this.block = new byte[256];
        this.blockSize = 0;
        this.dispose = 0;
        this.lastDispose = 0;
        this.transparency = false;
        this.delay = 0;
    }
    
    public int getDelay(final int n) {
        this.delay = -1;
        if (n >= 0 && n < this.frameCount) {
            this.delay = this.frames.get(n).delay;
        }
        return this.delay;
    }
    
    public int getFrameCount() {
        return this.frameCount;
    }
    
    public BufferedImage getImage() {
        return this.getFrame(0);
    }
    
    public int getLoopCount() {
        return this.loopCount;
    }
    
    protected void setPixels() {
        final int[] dest = ((DataBufferInt)this.image.getRaster().getDataBuffer()).getData();
        if (lastDispose > 0) {
            if (lastDispose == 3) {
                final int n = frameCount - 2;
                if (n > 0) {
                    lastImage = getFrame(n - 1);
                }
                else {
                    lastImage = null;
                }
            }
            if (lastImage != null) {
                final int[] prev = ((DataBufferInt)lastImage.getRaster().getDataBuffer()).getData();
                System.arraycopy(prev, 0, dest, 0, width * height);
                if (lastDispose == 2) {
                    final Graphics2D g = image.createGraphics();
                    Color c = null;
                    if (transparency) {
                        c = new Color(0, 0, 0, 0);
                    }
                    else {
                        c = new Color(lastBgColor);
                    }
                    g.setColor(c);
                    g.setComposite(AlphaComposite.Src);
                    g.fill(lastRect);
                    g.dispose();
                }
            }
        }
        int pass = 1;
        int inc = 8;
        int iline = 0;
        for (int i = 0; i < ih; ++i) {
            int line = i;
            if (interlace) {
                if (iline >= ih) {
                    switch (++pass) {
                        case 2: {
                            iline = 4;
                            break;
                        }
                        case 3: {
                            iline = 2;
                            inc = 4;
                            break;
                        }
                        case 4: {
                            iline = 1;
                            inc = 2;
                            break;
                        }
                    }
                }
                line = iline;
                iline += inc;
            }
            line += iy;
            if (line < height) {
                final int k = line * width;
                int dx = k + ix;
                int dlim = dx + iw;
                if (k + width < dlim) {
                    dlim = k + width;
                }
                int sx = i * iw;
                while (dx < dlim) {
                    final int index = pixels[sx++] & 0xFF;
                    final int c2 = act[index];
                    if (c2 != 0) {
                        dest[dx] = c2;
                    }
                    ++dx;
                }
            }
        }
    }
    
    public BufferedImage getFrame(final int n) {
        BufferedImage im = null;
        if (n >= 0 && n < frameCount) {
            im = frames.get(n).image;
        }
        return im;
    }
    
    public Dimension getFrameSize() {
        return new Dimension(width, height);
    }
    
    public int read(final BufferedInputStream is) {
        this.init();
        if (is != null) {
            in = is;
            readHeader();
            if (!err()) {
                readContents();
                if (frameCount < 0) {
                    status = 1;
                }
            }
        }
        else {
            status = 2;
        }
        try {
            is.close();
        }
        catch (IOException ex) {}
        return status;
    }
    
    public int read(InputStream is) {
        init();
        if (is != null) {
            if (!(is instanceof BufferedInputStream)) {
                is = new BufferedInputStream(is);
            }
            in = (BufferedInputStream)is;
            readHeader();
            if (!err()) {
                readContents();
                if (frameCount < 0) {
                    status = 1;
                }
            }
        }
        else {
            status = 2;
        }
        try {
            is.close();
        }
        catch (IOException ex) {}
        return status;
    }
    
    public int read(String name) {
        status = 0;
        try {
            name = name.trim().toLowerCase();
            if (name.indexOf("file:") >= 0 || name.indexOf(":/") > 0) {
                final URL url = new URL(name);
                in = new BufferedInputStream(url.openStream());
            }
            else {
                in = new BufferedInputStream(new FileInputStream(name));
            }
            status = read(in);
        }
        catch (IOException e) {
            status = 2;
        }
        return status;
    }
    
    protected void decodeImageData() {
        final int NullCode = -1;
        final int npix = iw * ih;
        if (pixels == null || pixels.length < npix) {
            pixels = new byte[npix];
        }
        if (prefix == null) {
            prefix = new short[4096];
        }
        if (suffix == null) {
            suffix = new byte[4096];
        }
        if (pixelStack == null) {
            pixelStack = new byte[4097];
        }
        final int data_size = read();
        final int clear = 1 << data_size;
        final int end_of_information = clear + 1;
        int available = clear + 2;
        int old_code = NullCode;
        int code_size = data_size + 1;
        int code_mask = (1 << code_size) - 1;
        for (int code = 0; code < clear; ++code) {
            prefix[code] = 0;
            suffix[code] = (byte)code;
        }
        int bi;
        int pi;
        int top;
        int first;
        int count;
        int datum;
        int bits = datum = (count = (first = (top = (pi = (bi = 0)))));
        int i = 0;
        while (i < npix) {
            if (top == 0) {
                if (bits < code_size) {
                    if (count == 0) {
                        count = readBlock();
                        if (count <= 0) {
                            break;
                        }
                        bi = 0;
                    }
                    datum += (block[bi] & 0xFF) << bits;
                    bits += 8;
                    ++bi;
                    --count;
                    continue;
                }
                int code = datum & code_mask;
                datum >>= code_size;
                bits -= code_size;
                if (code > available) {
                    break;
                }
                if (code == end_of_information) {
                    break;
                }
                if (code == clear) {
                    code_size = data_size + 1;
                    code_mask = (1 << code_size) - 1;
                    available = clear + 2;
                    old_code = NullCode;
                    continue;
                }
                if (old_code == NullCode) {
                    pixelStack[top++] = suffix[code];
                    old_code = code;
                    first = code;
                    continue;
                }
                final int in_code;
                if ((in_code = code) == available) {
                    pixelStack[top++] = (byte)first;
                    code = old_code;
                }
                while (code > clear) {
                    pixelStack[top++] = suffix[code];
                    code = prefix[code];
                }
                first = (suffix[code] & 0xFF);
                if (available >= 4096) {
                    break;
                }
                pixelStack[top++] = (byte)first;
                prefix[available] = (short)old_code;
                suffix[available] = (byte)first;
                if ((++available & code_mask) == 0x0 && available < 4096) {
                    ++code_size;
                    code_mask += available;
                }
                old_code = in_code;
            }
            --top;
            pixels[pi++] = pixelStack[top];
            ++i;
        }
        for (i = pi; i < npix; ++i) {
            pixels[i] = 0;
        }
    }
    
    protected boolean err() {
        return status != 0;
    }
    
    protected void init() {
        status = 0;
        frameCount = 0;
        frames = new ArrayList<GifFrame>();
        gct = null;
        lct = null;
    }
    
    protected int read() {
        int curByte = 0;
        try {
            curByte = in.read();
        }
        catch (IOException e) {
            status = 1;
        }
        return curByte;
    }
    
    protected int readBlock() {
        blockSize = read();
        int n = 0;
        if (blockSize > 0) {
            try {
                for (int count = 0; n < blockSize; n += count) {
                    count = in.read(block, n, blockSize - n);
                    if (count == -1) {
                        break;
                    }
                }
            }
            catch (IOException ex) {}
            if (n < blockSize) {
                status = 1;
            }
        }
        return n;
    }
    
    protected int[] readColorTable(final int ncolors) {
        final int nbytes = 3 * ncolors;
        int[] tab = null;
        final byte[] c = new byte[nbytes];
        int n = 0;
        try {
            n = in.read(c);
        }
        catch (IOException ex) {}
        if (n < nbytes) {
            status = 1;
        }
        else {
            tab = new int[256];
            int i = 0;
            int j = 0;
            while (i < ncolors) {
                final int r = c[j++] & 0xFF;
                final int g = c[j++] & 0xFF;
                final int b = c[j++] & 0xFF;
                tab[i++] = (0xFF000000 | r << 16 | g << 8 | b);
            }
        }
        return tab;
    }
    
    protected void readContents() {
        boolean done = false;
        while (!done && !err()) {
            int code = read();
            switch (code) {
                case 44: {
                    readImage();
                    continue;
                }
                case 33: {
                    code = read();
                    switch (code) {
                        case 249: {
                            readGraphicControlExt();
                            continue;
                        }
                        case 255: {
                            readBlock();
                            String app = "";
                            for (int i = 0; i < 11; ++i) {
                                app = String.valueOf(app) + (char)block[i];
                            }
                            if (app.equals("NETSCAPE2.0")) {
                                readNetscapeExt();
                                continue;
                            }
                            skip();
                            continue;
                        }
                        default: {
                            skip();
                            continue;
                        }
                    }
                    //break;
                }
                case 59: {
                    done = true;
                    continue;
                }
                case 0: {
                    continue;
                }
                default: {
                    status = 1;
                    continue;
                }
            }
        }
    }
    
    protected void readGraphicControlExt() {
        read();
        final int packed = read();
        dispose = (packed & 0x1C) >> 2;
        if (dispose == 0) {
            dispose = 1;
        }
        transparency = ((packed & 0x1) != 0x0);
        delay = readShort() * 10;
        transIndex = read();
        read();
    }
    
    protected void readHeader() {
        String id = "";
        for (int i = 0; i < 6; ++i) {
            id = String.valueOf(id) + (char)read();
        }
        if (!id.startsWith("GIF")) {
            status = 1;
            return;
        }
        readLSD();
        if (gctFlag && !err()) {
            gct = readColorTable(gctSize);
            bgColor = gct[bgIndex];
        }
    }
    
    protected void readImage() {
        ix = readShort();
        iy = readShort();
        iw = readShort();
        ih = readShort();
        final int packed = read();
        lctFlag = ((packed & 0x80) != 0x0);
        interlace = ((packed & 0x40) != 0x0);
        lctSize = 2 << (packed & 0x7);
        if (lctFlag) {
            lct = readColorTable(lctSize);
            act = lct;
        }
        else {
            act = gct;
            if (bgIndex == transIndex) {
                bgColor = 0;
            }
        }
        int save = 0;
        if (transparency) {
            save = act[transIndex];
            act[transIndex] = 0;
        }
        if (act == null) {
            status = 1;
        }
        if (err()) {
            return;
        }
        decodeImageData();
        skip();
        if (err()) {
            return;
        }
        ++frameCount;
        image = new BufferedImage(width, height, 3);
        setPixels();
        frames.add(new GifFrame(image, delay));
        if (transparency) {
            act[transIndex] = save;
        }
        resetFrame();
    }
    
    protected void readLSD() {
        width = readShort();
        height = readShort();
        final int packed = read();
        gctFlag = ((packed & 0x80) != 0x0);
        gctSize = 2 << (packed & 0x7);
        bgIndex = read();
        pixelAspect = read();
    }
    
    protected void readNetscapeExt() {
        do {
            readBlock();
            if (block[0] == 1) {
                final int b1 = block[1] & 0xFF;
                final int b2 = block[2] & 0xFF;
                loopCount = (b2 << 8 | b1);
            }
        } while (blockSize > 0 && !err());
    }
    
    protected int readShort() {
        return read() | read() << 8;
    }
    
    protected void resetFrame() {
        lastDispose = dispose;
        lastRect = new Rectangle(ix, iy, iw, ih);
        lastImage = image;
        lastBgColor = bgColor;
        dispose = 0;
        transparency = false;
        delay = 0;
        lct = null;
    }
    
    protected void skip() {
        do {
            readBlock();
        } while (blockSize > 0 && !err());
    }
    
    public static BufferedImage resizeImageWithHint(final BufferedImage originalImage, final int type, final int IMG_WIDTH, final int IMG_HEIGHT) {
        final BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        final Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return resizedImage;
    }
    
    static class GifFrame {
        public BufferedImage image;
        public int delay;
        
        public GifFrame(final BufferedImage im, final int del) {
            this.image = im;
            this.delay = del;
        }
    }
}
