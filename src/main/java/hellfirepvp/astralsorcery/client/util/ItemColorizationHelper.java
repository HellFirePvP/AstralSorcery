/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.image.ColorThief;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import javax.annotation.Nullable;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColorizationHelper
 * Created by HellFirePvP
 * Date: 24.09.2019 / 20:59
 */
public class ItemColorizationHelper implements ISelectiveResourceReloadListener {

    public static ItemColorizationHelper instance = new ItemColorizationHelper();
    private Map<Item, Color> colorizationMap = new HashMap<>();

    private ItemColorizationHelper() {}

    private void resolveColor(ItemStack stack) {
        Color dominant = getDominantColorFromStack(stack);
        if(dominant != null) {
            colorizationMap.put(stack.getItem(), dominant);
        }
    }

    @Nullable
    public static Color getDominantColorFromItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Color c = instance.colorizationMap.get(stack.getItem());
        if (c == null) {
            instance.resolveColor(stack);
        }
        return instance.colorizationMap.get(stack.getItem());
    }

    @Nullable
    private Color getDominantColorFromStack(ItemStack stack) {
        TextureAtlasSprite tas = RenderingUtils.getParticleSprite(stack);
        if (tas == null) {
            return null;
        }
        int overlay = getOverlayColor(stack);
        try {
            BufferedImage extractedImage = extractImage(tas);
            int[] dominantColor = ColorThief.getColor(extractedImage);
            int r = (int) ((dominantColor[0] - 1) * ((float) (overlay >> 16 & 255)) / 255F);
            int g = (int) ((dominantColor[1] - 1) * ((float) (overlay >>  8 & 255)) / 255F);
            int b = (int) ((dominantColor[2] - 1) * ((float) (overlay >>  0 & 255)) / 255F);
            r = MathHelper.clamp(r, 0, 255);
            g = MathHelper.clamp(g, 0, 255);
            b = MathHelper.clamp(b, 0, 255);
            return new Color(r, g, b).brighter();
        } catch (Exception exc) {
            AstralSorcery.log.error("Item Colorization Helper: Ignoring non-resolvable image " + tas.getName().toString());
            exc.printStackTrace();
        }
        return null;
    }

    @Nullable
    private BufferedImage extractImage(TextureAtlasSprite tas) {
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
                    pxArray[xx * tas.getHeight() + zz] = tas.getPixelRGBA(i, xx, zz);
                }
            }
            bufferedImage.setRGB(0, i * h, w, h, pxArray, 0, w);
        }
        return bufferedImage;
    }

    private int getOverlayColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        if (stack.getItem() instanceof BlockItem) {
            BlockState state = ItemUtils.createBlockState(stack);
            if(state == null) return -1;
            return Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        } else {
            return Minecraft.getInstance().getItemColors().getColor(stack, 0);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if (!resourcePredicate.test(VanillaResourceType.TEXTURES)) {
            return;
        }
        this.colorizationMap.clear();
    }
}
