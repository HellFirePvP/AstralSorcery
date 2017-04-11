/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.IModel;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
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
    private static boolean skipSetup = true;

    private Table<Item, Integer, Color> colorizationMap = HashBasedTable.create();

    private ItemColorizationHelper() {}

    private void setupRegistry() {
        List<ItemStack> collect = collectNecessaryItemStacks();
        for (ItemStack stack : collect) {
            if(stack == null || stack.getItem() == null) continue;
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
        if(stack == null || stack.getItem() == null) return null;
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
                for (TraitRecipe.TraitAltarSlot tas : TraitRecipe.TraitAltarSlot.values()) {
                    ItemHandle handle = tr.getTraitItemHandle(tas);
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
                for (TraitRecipe.TraitAltarSlot tas : TraitRecipe.TraitAltarSlot.values()) {
                    ItemHandle handle = tr.getTraitItemHandle(tas);
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
        TextureAtlasSprite tas = getTexture(stack);
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
            return new Color(r, g, b);
        } catch (Exception exc) {
            AstralSorcery.log.error("Item Colorization Helper: Ignoring non-resolvable image " + tas.getIconName());
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
        if(stack == null || stack.getItem() == null) return -1;
        if(stack.getItem() instanceof ItemBlock) {
            IBlockState state = ItemUtils.createBlockState(stack);
            if(state == null) return -1;
            return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, null, null, 0);
        } else {
            return Minecraft.getMinecraft().getItemColors().getColorFromItemstack(stack, 0);
        }
    }

    @Nullable
    private TextureAtlasSprite getTexture(ItemStack stack) {
        if(stack == null || stack.getItem() == null) return null;
        IBakedModel model = MeshRegisterHelper.getIMM().getItemModel(stack);
        if(model == MeshRegisterHelper.getIMM().getModelManager().getMissingModel()) {
            return null;
        }
        if(stack.getItem() instanceof ItemBlock) {
            IBlockState state = ItemUtils.createBlockState(stack);
            if(state == null) return null;
            TextureAtlasSprite tas = MeshRegisterHelper.getBMShapes().getTexture(state);
            if(tas == Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite()) return null;
            return tas;
        } else {
            return MeshRegisterHelper.getIMM().getItemModel(stack).getParticleTexture();
        }
    }

    private void nukeRegistry() {
        colorizationMap.clear();
    }

    public void reloadRegistry() {
        AstralSorcery.log.info("Item Colorization Helper: Rebuilding colorization cache! This might take longer for higher-res texture packs...");
        long startMs = System.currentTimeMillis();
        nukeRegistry();
        setupRegistry();
        AstralSorcery.log.info("Item Colorization Helper: Cache rebuilt! Time required: " + (System.currentTimeMillis() - startMs) + "ms - Entries cached: " + colorizationMap.size());
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        if(skipSetup) {
            skipSetup = false;
            return;
        }
        reloadRegistry();
    }

}
