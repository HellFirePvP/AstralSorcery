/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockArrayRender
 * Created by HellFirePvP
 * Date: 30.09.2016 / 11:37
 */
public class BlockArrayRenderHelper {

    private BlockArray blocks;
    private WorldBlockArrayRenderAccess renderAccess;
    private double rotX, rotY, rotZ;

    public BlockArrayRenderHelper(BlockArray blocks) {
        this.blocks = blocks;
        this.renderAccess = new WorldBlockArrayRenderAccess(blocks);
        resetRotation();
    }

    private void resetRotation() {
        this.rotX = -30;
        this.rotY = 45;
        this.rotZ = 0;
    }

    public void rotate(double x, double y, double z) {
        this.rotX += x;
        this.rotY += y;
        this.rotZ += z;
    }

    public void render3DGUI(double x, double y, float pTicks) {
        GuiScreen scr = Minecraft.getMinecraft().currentScreen;
        if(scr == null) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        double sc = new ScaledResolution(mc).getScaleFactor();
        GL11.glTranslated(x + 16D / sc, y + 16D / sc, 512);

        double mul = 10.5;

        double size = 2;
        double minSize = 0.5;

        Vec3i max = blocks.getMax();
        Vec3i min = blocks.getMin();

        double maxLength = 0;
        double pointDst = max.getX() - min.getX();
        if(pointDst > maxLength) maxLength = pointDst;
        pointDst = max.getY() - min.getY();
        if(pointDst > maxLength) maxLength = pointDst;
        pointDst = max.getZ() - min.getZ();
        if(pointDst > maxLength) maxLength = pointDst;
        maxLength -= 5;

        if(maxLength > 0) {
            size = (size - minSize) * (1D - (maxLength / 20D));
        }

        double dr = -5.75*size;
        GL11.glTranslated(dr, dr, dr);
        GL11.glRotated(rotX, 1, 0, 0);
        GL11.glRotated(rotY, 0, 1, 0);
        GL11.glRotated(rotZ, 0, 0, 1);
        GL11.glTranslated(-dr, -dr, -dr);

        GL11.glScaled(-size*mul, -size*mul, -size*mul);

        BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
        VertexFormat blockFormat = DefaultVertexFormats.BLOCK;

        TextureHelper.setActiveTextureToAtlasSprite();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        vb.begin(GL11.GL_QUADS, blockFormat);
        for (Map.Entry<BlockPos, BakedBlockData> data : renderAccess.blockRenderData.entrySet()) {
            BlockPos offset = data.getKey();
            BakedBlockData renderData = data.getValue();
            if(renderData.tileEntity != null) {
                renderData.tileEntity.setWorld(Minecraft.getMinecraft().world);
                renderData.tileEntity.setPos(offset);
            }
            if(renderData.type != Blocks.AIR) {
                brd.renderBlock(renderData.state, offset, renderAccess, vb);
            }
        }
        tes.draw();

        for (Map.Entry<BlockPos, BakedBlockData> data : renderAccess.blockRenderData.entrySet()) {
            BlockPos offset = data.getKey();
            BakedBlockData renderData = data.getValue();
            if(renderData.tileEntity != null && renderData.tesr != null) {
                renderData.tileEntity.setWorld(Minecraft.getMinecraft().world);
                renderData.tileEntity.setPos(offset);
                renderData.tesr.render(renderData.tileEntity, offset.getX(), offset.getY(), offset.getZ(), pTicks, 0, 1F);
            }
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static class BakedBlockData extends BlockArray.BlockInformation {

        private TileEntity tileEntity;
        private TileEntitySpecialRenderer tesr;

        protected BakedBlockData(Block type, IBlockState state, TileEntity te) {
            super(type, state);
            this.tileEntity = te;
            if(te != null) {
                tesr = TileEntityRendererDispatcher.instance.getRenderer(te);
            }
        }

    }

    public static class WorldBlockArrayRenderAccess implements IBlockAccess {

        private Map<BlockPos, BakedBlockData> blockRenderData = new HashMap<>();

        public WorldBlockArrayRenderAccess(BlockArray array) {
            for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : array.getPattern().entrySet()) {
                BlockPos offset = entry.getKey();
                BlockArray.BlockInformation info = entry.getValue();
                if(info.type.hasTileEntity(info.state)) {
                    TileEntity te = info.type.createTileEntity(Minecraft.getMinecraft().world, info.state);
                    BlockArray.TileEntityCallback callback = array.getTileCallbacks().get(offset);
                    if(te != null && callback != null) {
                        if(callback.isApplicable(te)) {
                            callback.onPlace(this, offset, te);
                        }
                    }
                    blockRenderData.put(offset, new BakedBlockData(info.type, info.state, te));
                } else {
                    blockRenderData.put(offset, new BakedBlockData(info.type, info.state, null));
                }
            }
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return blockRenderData.containsKey(pos) ? blockRenderData.get(pos).tileEntity : null;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 0;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return isInBounds(pos) ? blockRenderData.get(pos).state : Blocks.AIR.getDefaultState();
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return !isInBounds(pos) || blockRenderData.get(pos).type == Blocks.AIR;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public Biome getBiome(BlockPos pos) {
            return Biome.getBiome(0);
        }

        private boolean isInBounds(BlockPos pos) {
            return blockRenderData.containsKey(pos);
        }

        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return 0;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public WorldType getWorldType() {
            return Minecraft.getMinecraft().world.getWorldType();
        }

        @Override
        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
            return isInBounds(pos) ? getBlockState(pos).isSideSolid(this, pos, side) : _default;
        }

    }

}
