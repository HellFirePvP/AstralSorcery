/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRFakeTree
 * Created by HellFirePvP
 * Date: 11.11.2016 / 21:13
 */
public class TESRFakeTree extends TileEntitySpecialRenderer<TileFakeTree> {

    private static final List<TranslucentBlockState> blocks = new LinkedList<>();

    public static void renderTranslucentBlocks() {
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        World w = Minecraft.getMinecraft().world;
        RenderingUtils.removeStandartTranslationFromTESRMatrix(Minecraft.getMinecraft().getRenderPartialTicks());
        GL11.glEnable(GL11.GL_BLEND);
        Blending.ADDITIVEDARK.apply();

        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        for (TranslucentBlockState tbs : blocks) {
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(tbs.state, tbs.pos, w, vb);
        }

        vb.sortVertexData((float) TileEntityRendererDispatcher.staticPlayerX, (float) TileEntityRendererDispatcher.staticPlayerY, (float) TileEntityRendererDispatcher.staticPlayerZ);
        tes.draw();

        blocks.clear();

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @Override
    public void renderTileEntityAt(TileFakeTree te, double x, double y, double z, float partialTicks, int destroyStage) {
        if(te.getFakedState() == null) return;
        IBlockState renderState = te.getFakedState();
        blocks.add(new TranslucentBlockState(renderState, te.getPos()));
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
