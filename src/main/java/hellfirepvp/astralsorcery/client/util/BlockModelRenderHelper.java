/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockModelRenderHelper
 * Created by HellFirePvP
 * Date: 21.06.2018 / 19:52
 */
public class BlockModelRenderHelper {

    private static BlockFluidRenderer bfr;

    private static BlockFluidRenderer getFluidRenderer() {
        if (bfr == null) {
            bfr = new BlockFluidRenderer(new BlockColorsOverride(Minecraft.getMinecraft().getBlockColors()));
        }
        return bfr;
    }

    public static void renderBlockModelWithColor(IBlockAccess world, BlockPos pos, IBlockState state, BufferBuilder vb, int color) {
        try {
            EnumBlockRenderType type = state.getRenderType();
            if (type == EnumBlockRenderType.INVISIBLE) return;
            state = state.getActualState(world, pos);

            switch (type) {
                case MODEL:
                    IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
                    state = state.getBlock().getExtendedState(state, world, pos);
                    renderModelFlat(world, model, state, pos, vb, true, MathHelper.getPositionRandom(pos), color);
                    break;
                case LIQUID:
                    BlockColorsOverride.override = color;
                    getFluidRenderer().renderFluid(world, state, pos, vb);
                    BlockColorsOverride.override = -1;
                    break;
                default:
                    break;
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(crashreport);
        }
    }

    private static void renderModelFlat(IBlockAccess worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, long rand, int color) {
        BitSet bitset = new BitSet(3);

        for (EnumFacing enumfacing : EnumFacing.values()) {
            List<BakedQuad> sidedQuads = modelIn.getQuads(stateIn, enumfacing, rand);
            if (!sidedQuads.isEmpty() && (!checkSides || stateIn.shouldSideBeRendered(worldIn, posIn, enumfacing))) {
                int i = stateIn.getPackedLightmapCoords(worldIn, posIn.offset(enumfacing));
                renderQuadsFlat(worldIn, stateIn, posIn, i, false, buffer, sidedQuads, bitset, color);
            }
        }
        List<BakedQuad> quads = modelIn.getQuads(stateIn, null, rand);
        if (!quads.isEmpty()) {
            renderQuadsFlat(worldIn, stateIn, posIn, -1, true, buffer, quads, bitset,  color);
        }
    }

    private static void renderQuadsFlat(IBlockAccess blockAccessIn, IBlockState stateIn, BlockPos posIn,
                                 int brightnessIn, boolean ownBrightness, BufferBuilder buffer,
                                 List<BakedQuad> list, BitSet bitSet, int color) {
        Vec3d vec3d = stateIn.getOffset(blockAccessIn, posIn);
        double d0 = (double)posIn.getX() + vec3d.x;
        double d1 = (double)posIn.getY() + vec3d.y;
        double d2 = (double)posIn.getZ() + vec3d.z;
        int i = 0;

        for (int j = list.size(); i < j; ++i) {
            BakedQuad bakedquad = list.get(i);

            if (ownBrightness) {
                fillQuadBounds(stateIn, bakedquad.getVertexData(), bakedquad.getFace(), (float[]) null, bitSet);
                BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
                brightnessIn = stateIn.getPackedLightmapCoords(blockAccessIn, blockpos);
            }

            buffer.addVertexData(bakedquad.getVertexData());
            buffer.putBrightness4(brightnessIn, brightnessIn, brightnessIn, brightnessIn);

            if (EntityRenderer.anaglyphEnable) {
                color = TextureUtil.anaglyphColor(color);
            }

            float red   = (float) (color >> 16  & 255) / 255F;
            float green = (float) (color >> 8   & 255) / 255F;
            float blue  = (float) (color        & 255) / 255F;
            if(bakedquad.shouldApplyDiffuseLighting()) {
                float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
                red *= diffuse;
                green *= diffuse;
                blue *= diffuse;
            }
            buffer.putColorMultiplier(red, green, blue, 4);
            buffer.putColorMultiplier(red, green, blue, 3);
            buffer.putColorMultiplier(red, green, blue, 2);
            buffer.putColorMultiplier(red, green, blue, 1);

            buffer.putPosition(d0, d1, d2);
        }
    }

    private static void fillQuadBounds(IBlockState stateIn, int[] vertexData, EnumFacing face, @Nullable float[] quadBounds, BitSet boundsFlags)
    {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        for (int i = 0; i < 4; ++i) {
            float f6 = Float.intBitsToFloat(vertexData[i * 7]);
            float f7 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
            float f8 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (quadBounds != null) {
            quadBounds[EnumFacing.WEST.getIndex()] = f;
            quadBounds[EnumFacing.EAST.getIndex()] = f3;
            quadBounds[EnumFacing.DOWN.getIndex()] = f1;
            quadBounds[EnumFacing.UP.getIndex()] = f4;
            quadBounds[EnumFacing.NORTH.getIndex()] = f2;
            quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
            int j = EnumFacing.values().length;
            quadBounds[EnumFacing.WEST.getIndex() + j] = 1.0F - f;
            quadBounds[EnumFacing.EAST.getIndex() + j] = 1.0F - f3;
            quadBounds[EnumFacing.DOWN.getIndex() + j] = 1.0F - f1;
            quadBounds[EnumFacing.UP.getIndex() + j] = 1.0F - f4;
            quadBounds[EnumFacing.NORTH.getIndex() + j] = 1.0F - f2;
            quadBounds[EnumFacing.SOUTH.getIndex() + j] = 1.0F - f5;
        }

        switch (face) {
            case DOWN:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f1 < 1.0E-4F || stateIn.isFullCube()) && f1 == f4);
                break;
            case UP:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f4 > 0.9999F || stateIn.isFullCube()) && f1 == f4);
                break;
            case NORTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f2 < 1.0E-4F || stateIn.isFullCube()) && f2 == f5);
                break;
            case SOUTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f5 > 0.9999F || stateIn.isFullCube()) && f2 == f5);
                break;
            case WEST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f < 1.0E-4F || stateIn.isFullCube()) && f == f3);
                break;
            case EAST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f3 > 0.9999F || stateIn.isFullCube()) && f == f3);
            default:
                break;
        }
    }

    private static class BlockColorsOverride extends BlockColors {

        private static int override = -1;
        private final BlockColors prev;

        public BlockColorsOverride(BlockColors prev) {
            this.prev = prev;
        }

        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess blockAccess, @Nullable BlockPos pos, int tintIndex) {
            if(override != -1) {
                return override;
            }
            return prev.colorMultiplier(state, blockAccess, pos, tintIndex);
        }
    }


}
