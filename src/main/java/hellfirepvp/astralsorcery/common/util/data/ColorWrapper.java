/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ColorWrapper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ColorWrapper {

    public static final Codec<ColorWrapper> CODEC = Codec.INT.xmap(ColorWrapper::transparent, ColorWrapper::getColor);
    public static final StreamCodec<ByteBuf, ColorWrapper> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ColorWrapper::getColor,
            ColorWrapper::new);

    public static final ColorWrapper WHITE = opaque(0xFFFFFF);
    public static final ColorWrapper BLACK = opaque(0x000000);

    private final int colorARGB;

    private ColorWrapper(int colorARGB) {
        this.colorARGB = colorARGB;
    }

    public static ColorWrapper opaque(int colorARGB) {
        return new ColorWrapper(colorARGB | 0xFF000000);
    }

    public static ColorWrapper transparent(int colorARGB) {
        return new ColorWrapper(colorARGB);
    }

    public static ColorWrapper of(ChatFormatting color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException("Given ChatFormatting is not a color!");
        }
        return transparent(color.getColor());
    }

    public static ColorWrapper of(int r, int g, int b) {
        return of(r, g, b, 255);
    }

    public static ColorWrapper of(int r, int g, int b, int a) {
        return transparent((a << 24) | (r << 16) | (g << 8) | b);
    }

    public static ColorWrapper of(float r, float g, float b) {
        return of((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5));
    }

    public static ColorWrapper of(float r, float g, float b, float a) {
        return of((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5), (int) (a * 255 + 0.5));
    }

    //Literally just what awt color does
    public static ColorWrapper ofHSB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0 -> {
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                }
                case 1 -> {
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                }
                case 2 -> {
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                }
                case 3 -> {
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                }
                case 4 -> {
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                }
                case 5 -> {
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                }
            }
        }
        return opaque((r << 16) | (g << 8) | b);
    }

    //Literally just what awt color does
    public static float[] RGBtoHSB(int r, int g, int b) {
        float hue, saturation, brightness;
        float[] hsbvals = new float[3];
        int cmax = Math.max(r, g);
        if (b > cmax) cmax = b;
        int cmin = Math.min(r, g);
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0) {
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        } else {
            saturation = 0;
        }
        if (saturation == 0) {
            hue = 0;
        } else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) {
                hue = bluec - greenc;
            } else if (g == cmax) {
                hue = 2.0f + redc - bluec;
            } else {
                hue = 4.0f + greenc - redc;
            }
            hue = hue / 6.0f;
            if (hue < 0) {
                hue = hue + 1.0f;
            }
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public ColorWrapper brighter() {
        return ColorWrapper.of(
                Math.min((int) (this.getRed()   / 0.7F), 255),
                Math.min((int) (this.getGreen() / 0.7F), 255),
                Math.min((int) (this.getBlue()  / 0.7F), 255),
                this.getAlpha());
    }

    public ColorWrapper darker() {
        return ColorWrapper.of(
                Math.max((int) (this.getRed()   * 0.7F), 0),
                Math.max((int) (this.getGreen() * 0.7F), 0),
                Math.max((int) (this.getBlue()  * 0.7F), 0),
                this.getAlpha());
    }

    public ColorWrapper copyWithRed(int red) {
        return ColorWrapper.of(red & 0xFF, this.getGreen(), this.getBlue(), this.getAlpha());
    }

    public ColorWrapper copyWithGreen(int green) {
        return ColorWrapper.of(this.getRed(), green & 0xFF, this.getBlue(), this.getAlpha());
    }

    public ColorWrapper copyWithBlue(int blue) {
        return ColorWrapper.of(this.getRed(), this.getGreen(), blue & 0xFF, this.getAlpha());
    }

    public ColorWrapper copyWithAlpha(int alpha) {
        return ColorWrapper.of(this.getRed(), this.getGreen(), this.getBlue(), alpha & 0xFF);
    }

    public int getColor() {
        return this.colorARGB;
    }

    public int getAlpha() {
        return (this.colorARGB >> 24) & 0xFF;
    }

    public int getRed() {
        return (this.colorARGB >> 16) & 0xFF;
    }

    public int getGreen() {
        return (this.colorARGB >> 8) & 0xFF;
    }

    public int getBlue() {
        return this.colorARGB & 0xFF;
    }

    public float[] getRgba() {
        return new float[] {
                this.getRed() / 255F,
                this.getGreen() / 255F,
                this.getBlue() / 255F,
                this.getAlpha() / 255F
        };
    }
}
