/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.AirBlockRenderWorld;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileTranslucent;
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
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRTranslucentBlock
 * Created by HellFirePvP
 * Date: 17.01.2017 / 03:57
 */
public class TESRTranslucentBlock extends TileEntitySpecialRenderer<TileTranslucent> {

    protected static final List<TranslucentBlockState> blocks = new LinkedList<>();
    private static int hash = -1;
    private static int batchDList = -1;

    public static void renderTranslucentBlocks() {
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(Minecraft.getMinecraft().getRenderPartialTicks());
        GlStateManager.color(1F, 1F, 1F,1F);

        if(batchDList == -1) {
            batchBlocks();
            hash = hashBlocks();
            blocks.clear();
        } else {
            int currentHash = hashBlocks();
            if(hash != currentHash) {
                GLAllocation.deleteDisplayLists(batchDList);
                batchBlocks();
                hash = currentHash;
                blocks.clear();
            }
        }
        GL11.glEnable(GL11.GL_BLEND);
        Blending.CONSTANT_ALPHA.apply();
        GL11.glCallList(batchDList);
        blocks.clear();
        Blending.DEFAULT.apply();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void batchBlocks() {
        IBlockAccess iba = new AirBlockRenderWorld(Biomes.PLAINS, Minecraft.getMinecraft().world.getWorldType());
        batchDList = GLAllocation.generateDisplayLists(1);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.CONSTANT_ALPHA.apply();
        GL11.glNewList(batchDList, GL11.GL_COMPILE);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        for (TranslucentBlockState tbs : blocks) {
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(tbs.state, tbs.pos, iba, vb);
        }
        tes.draw();
        GL11.glEndList();
        Blending.DEFAULT.apply();
    }

    private static int hashBlocks() {
        int hash = 80238287;
        for (TranslucentBlockState tbs : blocks) {
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getX() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getY() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getZ() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.state.hashCode() * 5449 % 130651);
        }
        return hash % 75327403;
    }

    public static void cleanUp() {
        hash = -1;
        if(batchDList != -1) {
            GLAllocation.deleteDisplayLists(batchDList);
            batchDList = -1;
        }
    }

    @Override
    public void render(TileTranslucent te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(te.getFakedState() == null) return;
        IBlockState renderState = te.getFakedState();
        addForRender(renderState, te.getPos());
    }

    public static void addForRender(IBlockState state, BlockPos pos) {
        TESRTranslucentBlock.blocks.add(new TESRTranslucentBlock.TranslucentBlockState(state, pos));
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
