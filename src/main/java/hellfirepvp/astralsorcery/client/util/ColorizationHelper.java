/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.image.ColorThief;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.util.Unit;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.resource.SelectiveReloadStateHandler;
import net.minecraftforge.resource.VanillaResourceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ColorizationHelper
 * Created by HellFirePvP
 * Date: 24.09.2019 / 20:59
 */
public class ColorizationHelper {

    private static Map<Item, Optional<Color>> itemColors = new HashMap<>();
    private static Map<Fluid, Optional<Color>> fluidColors = new HashMap<>();

    private ColorizationHelper() {}

    @Nonnull
    public static Optional<Color> getColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        Item i = stack.getItem();

        if (!itemColors.containsKey(i)) {
            TextureAtlasSprite tas = RenderingUtils.getParticleTexture(stack);
            if (tas != null) {
                itemColors.put(i, getDominantColor(tas));
            } else {
                itemColors.put(i, Optional.empty());
            }
        }
        return itemColors.get(i).map(c -> ColorUtils.overlayColor(c, new Color(ColorUtils.getOverlayColor(stack))));
    }

    @Nonnull
    public static Optional<Color> getColor(FluidStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        Fluid fluid = stack.getFluid();

        if (!fluidColors.containsKey(fluid)) {
            TextureAtlasSprite tas = RenderingUtils.getParticleTexture(stack);
            if (tas != null) {
                fluidColors.put(fluid, getDominantColor(tas));
            } else {
                fluidColors.put(fluid, Optional.empty());
            }
        }
        return fluidColors.get(fluid).map(c -> ColorUtils.overlayColor(c, new Color(ColorUtils.getOverlayColor(stack))));
    }

    private static Optional<Color> getDominantColor(TextureAtlasSprite tas) {
        if (tas == null) {
            return Optional.empty();
        }
        try {
            BufferedImage extractedImage = extractImage(tas);
            int[] dominantColor = ColorThief.getColor(extractedImage);
            int color = (dominantColor[0] & 0xFF) << 16 | (dominantColor[1] & 0xFF) << 8 | (dominantColor[2] & 0xFF);
            return Optional.of(new Color(color));
        } catch (Exception exc) {
            AstralSorcery.log.error("Item Colorization Helper: Ignoring non-resolvable image " + tas.getName().toString());
            exc.printStackTrace();
        }
        return Optional.empty();
    }

    @Nullable
    private static BufferedImage extractImage(TextureAtlasSprite tas) {
        int w = tas.getWidth();
        int h = tas.getHeight();
        int count = tas.getFrameCount();
        if (w <= 0 || h <= 0 || count <= 0) {
            return null;
        }

        BufferedImage bufferedImage = new BufferedImage(w, h * count, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < count; i++) {
            int[] pxArray = new int[tas.getWidth() * tas.getHeight()];
            for (int xx = 0; xx < tas.getWidth(); xx++) {
                for (int zz = 0; zz < tas.getHeight(); zz++) {
                    int argb = tas.getPixelRGBA(0, xx, zz + (i * tas.getHeight()));
                    pxArray[zz * tas.getWidth() + xx] = argb & 0xFF00FF00 | ((argb & 0x00FF0000) >> 16) | ((argb & 0x000000FF) << 16);
                }
            }
            bufferedImage.setRGB(0, i * h, w, h, pxArray, 0, w);
        }
        return bufferedImage;
    }

    public static IFutureReloadListener onReload() {
        return (stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                stage.markCompleteAwaitingOthers(Unit.INSTANCE).thenRunAsync(() -> {
                    if (!SelectiveReloadStateHandler.INSTANCE.get().test(VanillaResourceType.TEXTURES)) {
                        return;
                    }

                    itemColors.clear();
                    fluidColors.clear();
                });
    }
}
