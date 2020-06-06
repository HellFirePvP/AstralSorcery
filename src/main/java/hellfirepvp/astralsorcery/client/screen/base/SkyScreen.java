/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Tuple;
import net.minecraft.world.dimension.DimensionType;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SkyScreen
 * Created by HellFirePvP
 * Date: 03.08.2019 / 15:47
 */
public interface SkyScreen {

    static final float THRESHOLD_TO_START = 0.8F;
    static final float THRESHOLD_TO_SHIFT_BLUEGRAD = 0.5F;
    static final float THRESHOLD_TO_MAX_BLUEGRAD = 0.2F;

    static final float THRESHOLD_FROM_START = 1.0F;
    static final float THRESHOLD_FROM_SHIFT_BLUEGRAD = 0.6F;
    static final float THRESHOLD_FROM_MAX_BLUEGRAD = 0.3F;

    public static Tuple<Color, Color> getSkyGradient(boolean canSeeSky, float angleTransparency, float partialTicks) {
        ClientWorld renderWorld = Minecraft.getInstance().world;
        if (renderWorld.getDimension().getType().equals(DimensionType.THE_END)) {
            canSeeSky = false; //Only for effect rendering purposes, not functionality.
        }
        int rgbFrom, rgbTo;
        if (canSeeSky && angleTransparency > 1.0E-4) {
            float starBr = renderWorld.getStarBrightness(partialTicks) * 2;
            float rain = renderWorld.getRainStrength(partialTicks);
            rgbFrom = RenderingUtils.clampToColorWithMultiplier(calcRGBFromWithRain(starBr, rain), angleTransparency).getRGB();
            rgbTo = RenderingUtils.clampToColorWithMultiplier(calcRGBToWithRain(starBr, rain), angleTransparency).getRGB();
        } else {
            rgbFrom = 0x000000;
            rgbTo = 0x000000;
        }
        int alphaMask = 0xFF000000; //100% opacity.
        return new Tuple<>(new Color(alphaMask | rgbFrom, true), new Color(alphaMask | rgbTo, true));
    }

    static int calcRGBToWithRain(float starBr, float rain) {
        int to = calcRGBTo(starBr);
        if (starBr <= THRESHOLD_TO_START) {
            float starMul = 1F;
            if (starBr > THRESHOLD_TO_SHIFT_BLUEGRAD) {
                starMul = 1F - (starBr - THRESHOLD_TO_SHIFT_BLUEGRAD) / (THRESHOLD_TO_START - THRESHOLD_TO_SHIFT_BLUEGRAD);
            }
            float interpDeg = starMul * rain;
            Color safeTo = RenderingUtils.clampToColor(to);
            Vector3 vTo = new Vector3(safeTo.getRed(), safeTo.getGreen(), safeTo.getBlue()).divide(255D);
            Vector3 rainC = new Vector3(102, 114, 137).divide(255D);
            Vector3 interpVec = vTo.copyInterpolateWith(rainC, interpDeg);
            Color newColor = RenderingUtils.clampToColor((int) (interpVec.getX() * 255), (int) (interpVec.getY() * 255), (int) (interpVec.getZ() * 255));
            to = newColor.getRGB();
        }
        return RenderingUtils.clampToColor(to).getRGB();
    }

    static int calcRGBTo(float starBr) {
        if (starBr >= THRESHOLD_TO_START) { //Blue ranges from 0 (1.0F starBr) to 170 (0.7F starBr)
            return 0; //Black.
        } else if (starBr >= THRESHOLD_TO_SHIFT_BLUEGRAD) { //Blue ranges from 170/AA (0.7F) to 255 (0.4F), green from 0 (0.7F) to 85(0.4F)
            float partSize = (THRESHOLD_TO_START - THRESHOLD_TO_SHIFT_BLUEGRAD);
            float perc = 1F - (starBr - THRESHOLD_TO_SHIFT_BLUEGRAD) / partSize;
            return (int) (perc * 170F);
        } else if (starBr >= THRESHOLD_TO_MAX_BLUEGRAD) {
            float partSize = (THRESHOLD_TO_SHIFT_BLUEGRAD - THRESHOLD_TO_MAX_BLUEGRAD);
            float perc = 1F - (starBr - THRESHOLD_TO_MAX_BLUEGRAD) / partSize;
            int green = (int) (perc * 85F);
            int blue = green + 0xAA; //LUL
            return (green << 8) | blue;
        } else {
            float partSize = (THRESHOLD_TO_MAX_BLUEGRAD - 0.0F); //Blue is 255, green from 85 (0.4F) to 175 (0.0F)
            float perc = 1F - (starBr - 0) / partSize;
            int green = 85 + ((int) (perc * 90));
            int red = (int) (perc * 140);
            return (red << 16) | (green << 8) | 0xFF;
        }
    }

    static int calcRGBFromWithRain(float starBr, float rain) {
        int to = calcRGBFrom(starBr);
        if (starBr <= THRESHOLD_FROM_START) {
            float starMul = 1F;
            if (starBr > THRESHOLD_FROM_SHIFT_BLUEGRAD) {
                starMul = 1F - (starBr - THRESHOLD_FROM_SHIFT_BLUEGRAD) / (THRESHOLD_FROM_START - THRESHOLD_FROM_SHIFT_BLUEGRAD);
            }
            float interpDeg = starMul * rain;
            Color safeTo = RenderingUtils.clampToColor(to);
            Vector3 vTo = new Vector3(safeTo.getRed(), safeTo.getGreen(), safeTo.getBlue()).divide(255D);
            Vector3 rainC = new Vector3(102, 114, 137).divide(255D);
            Vector3 interpVec = vTo.copyInterpolateWith(rainC, interpDeg);
            Color newColor = RenderingUtils.clampToColor((int) (interpVec.getX() * 255), (int) (interpVec.getY() * 255), (int) (interpVec.getZ() * 255));
            to = newColor.getRGB();
        }
        return RenderingUtils.clampToColor(to).getRGB();
    }

    static int calcRGBFrom(float starBr) {
        if (starBr >= THRESHOLD_FROM_START) { //Blue ranges from 0 (1.0F starBr) to 170 (0.7F starBr)
            return 0; //Black.
        } else if (starBr >= THRESHOLD_FROM_SHIFT_BLUEGRAD) { //Blue ranges from 170/AA (0.7F) to 255 (0.4F), green from 0 (0.7F) to 85(0.4F)
            float partSize = (THRESHOLD_FROM_START - THRESHOLD_FROM_SHIFT_BLUEGRAD);
            float perc = 1F - (starBr - THRESHOLD_FROM_SHIFT_BLUEGRAD) / partSize;
            return (int) (perc * 170F);
        } else if (starBr >= THRESHOLD_FROM_MAX_BLUEGRAD) {
            float partSize = (THRESHOLD_FROM_SHIFT_BLUEGRAD - THRESHOLD_FROM_MAX_BLUEGRAD);
            float perc = 1F - (starBr - THRESHOLD_FROM_MAX_BLUEGRAD) / partSize;
            int green = (int) (perc * 85F);
            int blue = green + 0xAA; //LUL
            return (green << 8) | blue;
        } else {
            float partSize = (THRESHOLD_FROM_MAX_BLUEGRAD - 0.0F); //Blue is 255, green from 85 (0.4F) to 175 (0.0F)
            float perc = 1F - (starBr - 0) / partSize;
            int green = 85 + ((int) (perc * 90));
            int red = (int) (perc * 140);
            return (red << 16) | (green << 8) | 0xFF;
        }
    }

}
