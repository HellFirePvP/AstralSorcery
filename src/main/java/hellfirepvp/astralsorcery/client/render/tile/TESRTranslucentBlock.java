/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderWorldBuffer;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileTranslucent;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRTranslucentBlock
 * Created by HellFirePvP
 * Date: 17.01.2017 / 03:57
 */
public class TESRTranslucentBlock extends TileEntitySpecialRenderer<TileTranslucent> {

    private static final Map<Color, Collection<TranslucentBlockState>> blockEffects = new HashMap<>();
    private static final Map<Color, Integer> hashes = new HashMap<>();
    private static final Map<Color, Integer> batches = new HashMap<>();

    public static void renderTranslucentBlocks() {
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GlStateManager.pushMatrix();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(Minecraft.getMinecraft().getRenderPartialTicks());
        GlStateManager.color(1F, 1F, 1F,1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        for (Map.Entry<Color, Collection<TranslucentBlockState>> setBlocks : blockEffects.entrySet()) {
            Color overlay = setBlocks.getKey();
            GL11.glColor4f(1F, 1F, 1F, overlay.getAlpha() / 255F);
            int batchList = batches.getOrDefault(setBlocks.getKey(), -1);
            Integer nullableHash = hashes.get(setBlocks.getKey());
            if (batchList == -1) {
                batches.put(overlay, batch(setBlocks.getValue(), overlay.getRGB()));
                hashes.put(overlay, hashBlocks(setBlocks.getValue()));
                setBlocks.getValue().clear();
            } else if(nullableHash == null) {
                GlStateManager.glDeleteLists(batchList, 1);
                batches.put(overlay, batch(setBlocks.getValue(), overlay.getRGB()));
                hashes.put(overlay, hashBlocks(setBlocks.getValue()));
                setBlocks.getValue().clear();
            } else {
                int newHash = hashBlocks(setBlocks.getValue());
                if (newHash != nullableHash) {
                    GlStateManager.glDeleteLists(batchList, 1);
                    batches.put(overlay, batch(setBlocks.getValue(), overlay.getRGB()));
                    hashes.put(overlay, newHash);
                    setBlocks.getValue().clear();
                }
            }
        }

        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.enableBlend();
        Blending.CONSTANT_ALPHA.applyStateManager();
        Blending.CONSTANT_ALPHA.apply();
        //Sync Statemanager
        GlStateManager.color(1F, 1F, 1F,1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        for (Color colorKey : blockEffects.keySet()) {
            Integer batch;
            if ((batch = batches.get(colorKey)) != null) {
                GlStateManager.callList(batch);
            }
            blockEffects.getOrDefault(colorKey, new ArrayList<>(0)).clear();
        }

        Blending.DEFAULT.applyStateManager();
        GlStateManager.color(1F, 1F, 1F,1F);
        //Drawing color-overlay'd blocks leaks color states into native GL context but doesn't apply them
        //to minecraft's GL wrapper. updating this manually.
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }

    private static int batch(Collection<TranslucentBlockState> set, int color) {
        RenderWorldBuffer iba = new RenderWorldBuffer(Biomes.PLAINS, Minecraft.getMinecraft().world.getWorldType(), new BlockArray());
        iba.appendAll(MiscUtils.splitMap(set, entry -> new Tuple<>(entry.pos, entry.state)));
        int batchDList = GLAllocation.generateDisplayLists(1);
        GlStateManager.enableBlend();
        Blending.CONSTANT_ALPHA.applyStateManager();
        GlStateManager.glNewList(batchDList, GL11.GL_COMPILE);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        for (TranslucentBlockState tbs : set) {
            RenderingUtils.renderBlockSafelyWithOptionalColor(iba, tbs.pos, tbs.state, vb, color);
        }
        tes.draw();
        GlStateManager.glEndList();
        Blending.DEFAULT.applyStateManager();
        return batchDList;
    }

    private static int hashBlocks(Collection<TranslucentBlockState> set) {
        int hash = 80238287;
        for (TranslucentBlockState tbs : set) {
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getX() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getY() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getZ() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.state.hashCode() * 5449 % 130651);
        }
        return hash % 75327403;
    }

    public static void cleanUp() {
        for (Integer batchList : batches.values()) {
            GlStateManager.glDeleteLists(batchList, 1);
        }
        batches.clear();
        hashes.clear();
        blockEffects.clear();
    }

    @Override
    public void render(TileTranslucent te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(te.getFakedState() == null) return;
        IBlockState renderState = te.getFakedState();
        if(x * x + y * y + z * z >= 64 * 64) return;
        addForRender(null, renderState, te.getPos());
    }

    public static void addForRender(@Nullable Color overlay, IBlockState state, BlockPos pos) {
        blockEffects.computeIfAbsent(overlay == null ? Color.WHITE : overlay, c -> new LinkedList<>()).add(new TranslucentBlockState(state, pos));
    }

    public static class TranslucentBlockState {

        public final IBlockState state;
        public final BlockPos pos;

        public TranslucentBlockState(IBlockState state, BlockPos pos) {
            this.state = state;
            this.pos = pos;
        }

    }

}
