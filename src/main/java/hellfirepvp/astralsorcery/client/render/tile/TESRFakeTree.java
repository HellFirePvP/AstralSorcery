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
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(Minecraft.getMinecraft().getRenderPartialTicks());
        GL11.glTranslated(0.5, -0.25, 0.5);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.ADDITIVEDARK.apply();

        for (TranslucentBlockState tbs : blocks) {
            GL11.glPushMatrix();
            BlockPos bp = tbs.pos;
            GL11.glTranslated(bp.getX(), bp.getY(), bp.getZ());
            GL11.glScaled(4, 4, 4);
            ri.renderItem(tbs.stack, ItemCameraTransforms.TransformType.GROUND);
            GL11.glPopMatrix();
        }

        blocks.clear();

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @Override
    public void renderTileEntityAt(TileFakeTree te, double x, double y, double z, float partialTicks, int destroyStage) {
        TextureHelper.refreshTextureBindState();
        IBlockState renderState = te.getFakedState();
        if(te.getFakedState() == null) return;

        ItemStack i = renderState.getBlock().getPickBlock(renderState, new RayTraceResult(Vec3d.ZERO, EnumFacing.UP, te.getPos()), Minecraft.getMinecraft().theWorld, te.getPos(), Minecraft.getMinecraft().thePlayer);
        if(i != null) {
            blocks.add(new TranslucentBlockState(i, te.getPos()));
        }

        TextureHelper.refreshTextureBindState();
    }

    public static class TranslucentBlockState {

        public final ItemStack stack;
        public final BlockPos pos;

        public TranslucentBlockState(ItemStack stack, BlockPos pos) {
            this.stack = stack;
            this.pos = pos;
        }
    }

}
