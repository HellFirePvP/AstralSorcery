/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.color.ColorThief;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ColorExtractUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ColorExtractUtil {

    private static final Map<Item, Optional<ColorWrapper>> itemColors = new HashMap<>();
    private static final Map<FluidType, Optional<ColorWrapper>> fluidColors = new HashMap<>();

    @Nonnull
    public static Optional<ColorWrapper> getColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        Item i = stack.getItem();

        if (!itemColors.containsKey(i)) {
            TextureAtlasSprite tas = RenderSpriteUtil.getTexture(stack);
            itemColors.put(i, getDominantColor(tas));
        }
        return itemColors.get(i).map(c -> ColorUtil.overlayColor(c, ColorUtil.getOverlayColor(stack)));
    }

    @Nonnull
    public static Optional<ColorWrapper> getColor(FluidStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        FluidType fluid = stack.getFluidType();

        if (!fluidColors.containsKey(fluid)) {
            TextureAtlasSprite tas = RenderSpriteUtil.getTexture(stack);
            fluidColors.put(fluid, getDominantColor(tas));
        }
        return fluidColors.get(fluid).map(c -> ColorUtil.overlayColor(c, ColorUtil.getOverlayColor(stack)));
    }

    private static Optional<ColorWrapper> getDominantColor(TextureAtlasSprite tas) {
        if (tas == null) {
            return Optional.empty();
        }
        try {
            BufferedImage extractedImage = extractImage(tas);
            int[] dominantColor = ColorThief.getColor(extractedImage);
            int color = (dominantColor[0] & 0xFF) << 16 | (dominantColor[1] & 0xFF) << 8 | (dominantColor[2] & 0xFF);
            return Optional.of(ColorWrapper.opaque(color));
        } catch (Exception exc) {
            AstralSorcery.LOG.error("Item Colorization Helper: Ignoring non-resolvable image " + tas.atlasLocation(), exc);
        }
        return Optional.empty();
    }

    @Nullable
    private static BufferedImage extractImage(TextureAtlasSprite tas) {
        SpriteContents sprite = tas.contents();
        int width = sprite.width();
        int height = sprite.height();
        int count = (int) sprite.getUniqueFrames().count();
        if (width <= 0 || height <= 0 || count <= 0) {
            return null;
        }

        BufferedImage bufferedImage = new BufferedImage(width, height * count, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < count; i++) {
            int[] pxArray = new int[width * height];
            for (int xx = 0; xx < width; xx++) {
                for (int zz = 0; zz < height; zz++) {
                    int argb = tas.getPixelRGBA(i, xx, zz);
                    pxArray[zz * width + xx] = argb & 0xFF00FF00 | ((argb & 0x00FF0000) >> 16) | ((argb & 0x000000FF) << 16);
                }
            }
            bufferedImage.setRGB(0, i * height, width, height, pxArray, 0, width);
        }
        return bufferedImage;
    }

    public static PreparableReloadListener reload() {
        return (preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
            return preparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
                itemColors.clear();
                fluidColors.clear();
            });
        };
    }
}
