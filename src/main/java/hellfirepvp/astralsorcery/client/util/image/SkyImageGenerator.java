/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.image;

import org.apache.logging.log4j.util.TriConsumer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SkyImageGenerator
 * Created by HellFirePvP
 * Date: 06.01.2021 / 10:55
 */
public class SkyImageGenerator {

    public static BufferedImage generateStarBackground() {
        BufferedImage image = createBackground();
        placeRandomly(ImageTemplates.getLargeStar(), image, 200);
        placeRandomly(ImageTemplates.getSmallStar(), image, 500);
        placeRandomly(ImageTemplates.getDot(ImageTemplates.TR_2), image, 300);
        placeRandomly(ImageTemplates.getDot(ImageTemplates.TR_3), image, 600);
        return image;
    }

    private static void placeRandomly(ImageTemplate template, BufferedImage out, int count) {
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            int offsetX = rand.nextInt(out.getWidth() - template.getWidth());
            int offsetY = rand.nextInt(out.getHeight() - template.getHeight());

            template.place(createColorPlacer(offsetX, offsetY, rand, out));
        }
    }

    private static TriConsumer<Integer, Integer, Integer> createColorPlacer(int offsetX, int offsetY, Random rand, BufferedImage out) {
        return (oX, oY, color) -> {
            int x = oX + offsetX;
            int y = oY + offsetY;
            int newColor = blendAlphaAdditively(out.getRGB(x, y), color, 0.8F + rand.nextFloat() * 0.2F);
            out.setRGB(x, y, convertToABGR(newColor));
        };
    }

    private static int convertToABGR(int color) {
        return color & 0xFF00FF00 | ((color >> 16) & 0xFF) | (color & 0xFF) << 16;
    }

    private static int blendAlphaAdditively(int existing, int toWrite, float alphaMulOut) {
        int existingAlpha = (existing >> 24) & 0xFF;
        int newAlpha = Math.round((toWrite >> 24) * alphaMulOut) & 0xFF;

        float partNew = newAlpha / 255F;
        float partOld = 1F - partNew;

        int newColorAdd = Math.min(Math.round(((existing >> 16) & 0xFF) * partOld + ((toWrite >> 16) & 0xFF) * partNew), 255) << 16 |
                Math.min(Math.round(((existing >> 8) & 0xFF) * partOld + ((toWrite >> 8) & 0xFF) * partNew), 255) << 8 |
                Math.min(Math.round((existing & 0xFF) * partOld + (toWrite & 0xFF) * partNew), 255);

        return (newColorAdd & 0xFFFFFF) | ((Math.min(existingAlpha + newAlpha, 255) & 0xFF) << 24);
    }

    private static BufferedImage createBackground() {
        BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D draw = image.createGraphics();
        draw.setColor(Color.BLACK);
        draw.fillRect(0, 0, image.getWidth(), image.getHeight());
        return image;
    }

}
