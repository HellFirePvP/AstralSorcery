/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.image.ColorThief;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.OreDictUniqueStackList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColorizationHelper
 * Created by HellFirePvP
 * Date: 09.04.2017 / 16:15
 */
public class ItemColorizationHelper implements IResourceManagerReloadListener {

    public static ItemColorizationHelper instance = new ItemColorizationHelper();

    private Table<Item, Integer, Color> colorizationMap = HashBasedTable.create();

    private ItemColorizationHelper() {}

    private void setupRegistry() {
        List<ItemStack> collect = collectNecessaryItemStacks();
        for (ItemStack stack : collect) {
            if(stack.isEmpty()) continue;
            resolveColor(stack);
        }
    }

    private void resolveColor(ItemStack stack) {
        Color dominant = getDominantColorFromStack(stack);
        if(dominant != null) {
            colorizationMap.put(stack.getItem(), getMeta(stack), dominant);
        }
    }

    private static int getMeta(ItemStack stack) {
        if(stack.getItem() instanceof ItemBlock) {
            return stack.getMetadata();
        }
        return stack.getItemDamage();
    }

    @Nullable
    public static Color getDominantColorFromItemStack(ItemStack stack) {
        if(stack.isEmpty()) return null;
        int dmg = getMeta(stack);
        Color c = instance.colorizationMap.get(stack.getItem(), dmg);
        if(c == null) {
            instance.resolveColor(stack);
        }
        return instance.colorizationMap.get(stack.getItem(), dmg);
    }

    private List<ItemStack> collectNecessaryItemStacks() {
        List<ItemStack> toPrepare = new OreDictUniqueStackList();
        for (AbstractAltarRecipe ar : AltarRecipeRegistry.getRecipesForLevel(TileAltar.AltarLevel.TRAIT_CRAFT)) {
            if(ar instanceof TraitRecipe) {
                TraitRecipe tr = (TraitRecipe) ar;
                toPrepare.add(tr.getOutputForRender());
                for (ItemHandle handle : tr.getTraitItemHandles()) {
                    if(handle != null && handle.handleType != ItemHandle.Type.OREDICT) {
                        toPrepare.addAll(handle.getApplicableItemsForRender());
                    }
                }
            }
        }
        for (AbstractAltarRecipe ar : AltarRecipeRegistry.getRecipesForLevel(TileAltar.AltarLevel.ENDGAME)) {
            if(ar instanceof TraitRecipe) {
                TraitRecipe tr = (TraitRecipe) ar;
                toPrepare.add(tr.getOutputForRender());
                for (ItemHandle handle : tr.getTraitItemHandles()) {
                    if(handle != null && handle.handleType != ItemHandle.Type.OREDICT) {
                        toPrepare.addAll(handle.getApplicableItemsForRender());
                    }
                }
            }
        }
        return toPrepare;
    }

    @Nullable
    private Color getDominantColorFromStack(ItemStack stack) {
        TextureAtlasSprite tas = RenderingUtils.tryGetMainTextureOfItemStack(stack);
        if(tas == null) return null;
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
            AstralSorcery.log.error("[AstralSorcery] Item Colorization Helper: Ignoring non-resolvable image " + tas.getIconName());
            exc.printStackTrace();
        }
        return null;
    }

    @Nullable
    private BufferedImage extractImage(TextureAtlasSprite tas) {
        int w = tas.getIconWidth();
        int h = tas.getIconHeight();
        int count = tas.getFrameCount();
        if (w <= 0 || h <= 0 || count <= 0) {
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(w, h * count, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < count; i++) {
            bufferedImage.setRGB(0, i * h, w, h, tas.getFrameTextureData(i)[0], 0, w);
        }
        return bufferedImage;
    }

    private int getOverlayColor(ItemStack stack) {
        if(stack.isEmpty()) return -1;
        if(stack.getItem() instanceof ItemBlock) {
            IBlockState state = ItemUtils.createBlockState(stack);
            if(state == null) return -1;
            return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, null, null, 0);
        } else {
            return Minecraft.getMinecraft().getItemColors().getColorFromItemstack(stack, 0);
        }
    }

    private void nukeRegistry() {
        colorizationMap.clear();
    }

    public void reloadRegistry() {
        AstralSorcery.log.info("[AstralSorcery] Item Colorization Helper: Rebuilding colorization cache! This might take longer for higher-res texture packs...");
        long startMs = System.currentTimeMillis();
        nukeRegistry();
        setupRegistry();
        AstralSorcery.log.info("[AstralSorcery] Item Colorization Helper: Cache rebuilt! Time required: " + (System.currentTimeMillis() - startMs) + "ms - Entries cached: " + colorizationMap.size());
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        reloadRegistry();
    }

}
