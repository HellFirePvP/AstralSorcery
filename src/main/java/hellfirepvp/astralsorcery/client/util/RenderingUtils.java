/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 29.08.2016 / 16:51
 */
public class RenderingUtils {

    private static final Random rand = new Random();
    private static ParticleDigging.Factory diggingFactory = new ParticleDigging.Factory();

    public static Field itemPhysics_fieldSkipRenderHook = null;

    public static void playBlockBreakParticles(BlockPos pos, IBlockState state) {
        ParticleManager pm = Minecraft.getMinecraft().effectRenderer;

        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                for (int l = 0; l < 4; l++) {
                    double d0 = (double) pos.getX() + ((double) j + 0.5D) / 4D;
                    double d1 = (double) pos.getY() + ((double) k + 0.5D) / 4D;
                    double d2 = (double) pos.getZ() + ((double) l + 0.5D) / 4D;
                    Particle digging = diggingFactory.createParticle(0, Minecraft.getMinecraft().world,
                            d0, d1, d2,
                            d0 - (double) pos.getX() - 0.5D,
                            d1 - (double) pos.getY() - 0.5D,
                            d2 - (double) pos.getZ() - 0.5D,
                            Block.getStateId(state));
                    pm.addEffect(digging);
                }
            }
        }
    }

    public static void renderItemAsEntity(ItemStack stack, double x, double y, double z, float pTicks, int age) {
        EntityItem ei = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, stack);
        ei.age = age;
        ei.hoverStart = 0;
        if (itemPhysics_fieldSkipRenderHook != null) {
            try {
                itemPhysics_fieldSkipRenderHook.set(ei, true);
            } catch (Exception ignored) {}
        }
        Minecraft.getMinecraft().getRenderManager().renderEntity(ei, x + 0.5, y + 0.6, z + 0.5, 0, pTicks, true);
    }

    @Nonnull
    public static TextureAtlasSprite tryGetFlowingTextureOfFluidStack(FluidStack stack) {
        ResourceLocation res = stack.getFluid().getFlowing(stack);
        TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(res.toString());
        if(tas == null) tas = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        return tas;
    }

    @Nullable
    public static TextureAtlasSprite tryGetMainTextureOfItemStack(ItemStack stack) {
        if(stack.isEmpty()) return null;
        ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        IBakedModel model = imm.getItemModel(stack);
        if(model == imm.getModelManager().getMissingModel()) {
            return null;
        }
        if(stack.getItem() instanceof ItemBlock) {
            IBlockState state = ItemUtils.createBlockState(stack);
            if(state == null) return null;
            TextureAtlasSprite tas = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
            if(tas == Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite()) return null;
            return tas;
        } else {
            return imm.getItemModel(stack).getParticleTexture();
        }
    }

    @Nullable
    public static TextureAtlasSprite tryGetTextureOfBlockState(IBlockState state) {
        if (state.getBlock().isAir(state, null, BlockPos.ORIGIN)) {
            return null;
        }
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    public static Particle spawnBlockBreakParticle(Vector3 pos, TextureAtlasSprite tas) {
        Particle digging = diggingFactory.createParticle(0, Minecraft.getMinecraft().world,
                pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0);
        Minecraft.getMinecraft().effectRenderer.addEffect(digging);
        digging.setParticleTexture(tas);
        return digging;
    }

    public static EntityFXFloatingCube spawnFloatingBlockCubeParticle(Vector3 pos, TextureAtlasSprite tas) {
        EntityFXFloatingCube cube = new EntityFXFloatingCube(tas);
        cube.setPosition(pos);
        EffectHandler.getInstance().registerFX(cube);
        return cube;
    }

    public static float getCurrentRenderPartialTicks() {
        return Minecraft.getMinecraft().isGamePaused() ? 0 : Minecraft.getMinecraft().getRenderPartialTicks();
    }

    public static void renderBlockSafelyWithOptionalColor(IBlockAccess world, BlockPos offset, IBlockState state, BufferBuilder vb, int color) {
        if (color == -1) {
            renderBlockSafely(world, offset, state, vb);
        } else {
            BlockModelRenderHelper.renderBlockModelWithColor(world, offset, state, vb, color);
        }
    }

    public static void renderBlockSafely(IBlockAccess world, BlockPos offset, IBlockState state, BufferBuilder vb) {
        BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
        if (brd != null) {
            try {
                brd.renderBlock(state, offset, world, vb);
            } catch (Exception exc) {
                EnumBlockRenderType type = state.getRenderType();
                if (type != EnumBlockRenderType.INVISIBLE) {
                    if (world.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
                        try {
                            state = state.getActualState(world, offset);
                        } catch (Exception ignored) {}
                    }
                    switch (type){
                        case MODEL:
                            IBakedModel model = brd.getModelForState(state);
                            try {
                                state = state.getBlock().getExtendedState(state, world, offset);
                            } catch (Exception ignored) {}
                            brd.getBlockModelRenderer().renderModel(world, model, state, offset, vb, true);
                        default:
                            break;
                    }
                }
            }
        }
    }

    public static void renderTexturedCubeCentral(Vector3 offset, double size, double u, double v, double uLength, double vLength) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        double half = size / 2D;

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u, v).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u + uLength, v).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u,           v + vLength).endVertex();

        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u, v).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u,           v + vLength).endVertex();

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u + uLength, v).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u , v + vLength).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u,           v).endVertex();

        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u + uLength, v).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u , v + vLength).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u,           v).endVertex();

        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u, v).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u, v + vLength).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u + uLength,           v).endVertex();

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u, v).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u, v + vLength).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u + uLength,           v).endVertex();

        tes.draw();
    }

    public static void renderTexturedCubeCentralWithColor(Vector3 offset, double size,
                                                                  double u, double v, double uLength, double vLength,
                                                                  float cR, float cG, float cB, float cA) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        double half = size / 2D;

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u, v).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u, v).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();

        tes.draw();
    }

    public static void renderTexturedCubeCentralWithLightAndColor(Vector3 offset, double size,
                                                          double u, double v, double uLength, double vLength,
                                                          int lX, int lY,
                                                          float cR, float cG, float cB, float cA) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        double half = size / 2D;

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() - half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() - half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() - half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() - half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        vb.pos(offset.getX() - half, offset.getY() - half, offset.getZ() + half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() - half, offset.getY() + half, offset.getZ() + half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() + half, offset.getZ() + half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        vb.pos(offset.getX() + half, offset.getY() - half, offset.getZ() + half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        tes.draw();
    }

    //You might not want to call this too often.
    public static void triggerChunkRerender() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    public static void tryRenderItemWithColor(ItemStack stack, IBakedModel model, Color c, float alpha) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer()) {
                GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, alpha);
                GlStateManager.enableRescaleNormal();
                TileEntityItemStackRenderer.instance.renderByItem(stack);
            } else {
                renderColoredItemModel(model, stack, c, alpha);
            }
            GlStateManager.popMatrix();
        }
    }

    private static void renderColoredItemModel(IBakedModel model, ItemStack stack, Color color, float alpha) {
        Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(Math.round(alpha * 255F), 0, 255));
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        for (EnumFacing enumfacing : EnumFacing.values()) {
            renderColoredQuads(bufferbuilder, model.getQuads(null, enumfacing, 0L), alphaColor, stack);
        }
        renderColoredQuads(bufferbuilder, model.getQuads(null, null, 0L), alphaColor, stack);
        tessellator.draw();
    }

    private static void renderColoredQuads(BufferBuilder renderer, List<BakedQuad> quads, Color color, ItemStack stack) {
        boolean flag = color.equals(Color.WHITE) && color.getAlpha() == 255 && !stack.isEmpty();
        int i = 0;

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = quads.get(i);
            int rgb = color.getRGB() | (color.getAlpha() << 24);

            if (flag && bakedquad.hasTintIndex()) {
                rgb = itemColors.colorMultiplier(stack, bakedquad.getTintIndex());

                if (EntityRenderer.anaglyphEnable) {
                    rgb = TextureUtil.anaglyphColor(rgb);
                }

                Color purify = new Color(rgb, false);
                rgb = purify.getRGB() | (color.getAlpha() << 24);
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, rgb);
        }
    }

    public static void sortVertexData(BufferBuilder vb) {
        vb.sortVertexData((float) TileEntityRendererDispatcher.staticPlayerX,
                (float) TileEntityRendererDispatcher.staticPlayerY,
                (float) TileEntityRendererDispatcher.staticPlayerZ);
    }

    public static Color clampToColor(int rgb) {
        return clampToColorWithMultiplier(rgb, 1F);
    }

    public static Color clampToColorWithMultiplier(int rgb, float mul) {
        int r = ((rgb >> 16) & 0xFF);
        int g = ((rgb >> 8)  & 0xFF);
        int b = ((rgb >> 0)  & 0xFF);
        return new Color(
                MathHelper.clamp((int) (((float) r) * mul), 0, 255),
                MathHelper.clamp((int) (((float) g) * mul), 0, 255),
                MathHelper.clamp((int) (((float) b) * mul), 0, 255));
    }

    public static Color clampToColor(int r, int g, int b) {
        return new Color(
                MathHelper.clamp((int) (((float) r)), 0, 255),
                MathHelper.clamp((int) (((float) g)), 0, 255),
                MathHelper.clamp((int) (((float) b)), 0, 255));
    }

    public static Vector3 interpolatePosition(Entity e, float partialTicks) {
        return new Vector3(
                RenderingUtils.interpolate(e.lastTickPosX, e.posX, partialTicks),
                RenderingUtils.interpolate(e.lastTickPosY, e.posY, partialTicks),
                RenderingUtils.interpolate(e.lastTickPosZ, e.posZ, partialTicks)
        );
    }

    public static double interpolate(double oldP, double newP, float partialTicks) {
        if(oldP == newP) return oldP;
        return oldP + ((newP - oldP) * partialTicks);
    }

    public static float interpolateRotation(float prevRotation, float nextRotation, float partialTick) {
        float rot = nextRotation - prevRotation;
        while (rot >= 180.0F) {
            rot -= 360.0F;
        }
        while (rot >= 180.0F) {
            rot -= 360.0F;
        }
        return prevRotation + partialTick * rot;
    }

    //Use with caution. Big block of rendering hack.
    @Deprecated
    public static void unsafe_preRenderHackCamera(EntityPlayer renderView, double x, double y, double z, double prevX, double prevY, double prevZ, double yaw, double yawPrev, double pitch, double pitchPrev) {
        TileEntityRendererDispatcher.staticPlayerX = x;
        TileEntityRendererDispatcher.staticPlayerY = y;
        TileEntityRendererDispatcher.staticPlayerZ = z;

        Entity rv = Minecraft.getMinecraft().getRenderViewEntity();
        if(rv == null || !rv.equals(renderView)) {
            Minecraft.getMinecraft().setRenderViewEntity(renderView);
            rv = renderView;
        }
        EntityPlayer render = (EntityPlayer) rv;

        render.posX = x;
        render.posY = y;
        render.posZ = z;
        render.prevPosX = prevX;
        render.prevPosY = prevY;
        render.prevPosZ = prevZ;
        render.lastTickPosX = prevX;
        render.lastTickPosY = prevY;
        render.lastTickPosZ = prevZ;

        render.rotationYawHead =     (float)yaw;
        render.rotationYaw =         (float)yaw;
        render.prevRotationYaw =     (float)yawPrev;
        render.prevRotationYawHead = (float)yawPrev;
        render.cameraYaw =           (float)yaw;
        render.prevCameraYaw =       (float)yawPrev;
        render.rotationPitch =       (float)pitch;
        render.prevRotationPitch =   (float)pitchPrev;

        render = Minecraft.getMinecraft().player;

        render.posX = x;
        render.posY = y;
        render.posZ = z;
        render.prevPosX = prevX;
        render.prevPosY = prevY;
        render.prevPosZ = prevZ;
        render.lastTickPosX = prevX;
        render.lastTickPosY = prevY;
        render.lastTickPosZ = prevZ;

        render.rotationYawHead =     (float)yaw;
        render.rotationYaw =         (float)yaw;
        render.prevRotationYaw =     (float)yawPrev;
        render.prevRotationYawHead = (float)yawPrev;
        render.cameraYaw =           (float)yaw;
        render.prevCameraYaw =       (float)yawPrev;
        render.rotationPitch =       (float)pitch;
        render.prevRotationPitch =   (float)pitchPrev;

        Minecraft.getMinecraft().inGameHasFocus = false;
        ActiveRenderInfo.updateRenderInfo(render, false);
        ClientUtils.grabMouseCursor();
    }

    @Deprecated
    public static void unsafe_resetCamera() {
        if(Minecraft.getMinecraft().player != null) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            Minecraft.getMinecraft().setRenderViewEntity(player);
            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;
            RenderManager rm = Minecraft.getMinecraft().getRenderManager();
            rm.setRenderPosition(x, y, z);
            rm.viewerPosX = x;
            rm.viewerPosY = y;
            rm.viewerPosZ = z;

            TileEntityRendererDispatcher.staticPlayerX = x;
            TileEntityRendererDispatcher.staticPlayerY = y;
            TileEntityRendererDispatcher.staticPlayerZ = z;

            if(Minecraft.getMinecraft().currentScreen != null) {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }

            if (Display.isActive()) {
                ClientUtils.ungrabMouseCursor();
            }

            Minecraft.getMinecraft().inGameHasFocus = false;
            Minecraft.getMinecraft().setIngameFocus();
        }
    }

    public static void renderLightRayEffects(double x, double y, double z, Color effectColor, long seed, long continuousTick, int dstJump, int countFancy, int countNormal) {
        renderLightRayEffects(x, y, z, effectColor, seed, continuousTick, dstJump, 1, countFancy, countNormal);
    }

    public static void renderLightRayEffects(double x, double y, double z, Color effectColor, long seed, long continuousTick, int dstJump, float scale, int countFancy, int countNormal) {
        rand.setSeed(seed);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        int fancy_count = !FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? countNormal : countFancy;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        RenderHelper.disableStandardItemLighting();
        float f1 = continuousTick / 400.0F;
        float f2 = 0.4F;

        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.ADDITIVE_ALPHA.applyStateManager();
        Blending.ADDITIVE_ALPHA.apply();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        for (int i = 0; i < fancy_count; i++) {
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);
            vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            float fa = rand.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = rand.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(dstJump, 10 * scale) / 10.0F);
            f4 /= 30.0F / (Math.min(dstJump, 10 * scale) / 10.0F);
            vb.pos(0, 0, 0).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), (int) (255.0F * (1.0F - f2))).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos( 0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos( 0.0D,      fa,    1.0F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            tes.draw();
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    public static void renderBlueStackTooltip(int x, int y, List<Tuple<ItemStack, String>> tooltipData, FontRenderer fr, RenderItem ri) {
        renderStackTooltip(x, y, tooltipData, new Color(0x000027), new Color(0x000044), Color.WHITE, fr, ri);
    }

    public static void renderStackTooltip(int x, int y, List<Tuple<ItemStack, String>> tooltipData, Color color, Color colorFade, Color strColor, FontRenderer fr, RenderItem ri) {
        TextureHelper.setActiveTextureToAtlasSprite();

        if (!tooltipData.isEmpty()) {
            int esWidth = 0;
            for (Tuple<ItemStack, String> toolTip : tooltipData) {
                int width = fr.getStringWidth(toolTip.value) + 17;
                if (width > esWidth)
                    esWidth = width;
            }
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            if(x + 15 + esWidth > sr.getScaledWidth()) {
                x -= esWidth + 24;
            }
            int pX = x + 12;
            int pY = y - 12;
            int sumLineHeight = 8;
            if (tooltipData.size() > 1)
                sumLineHeight += 2 + (tooltipData.size() - 1) * 17;
            float z = 300F;

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawGradientRect(pX - 3,           pY - 4,                 z, pX + esWidth + 3, pY - 3,                 color, colorFade);
            drawGradientRect(pX - 3,           pY + sumLineHeight + 3, z, pX + esWidth + 3, pY + sumLineHeight + 4, color, colorFade);
            drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX - 4,           pY - 3,                 z, pX - 3,           pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX + esWidth + 3, pY - 3,                 z, pX + esWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int rgb = color.getRGB();
            int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
            Color colOp = new Color(col);
            drawGradientRect(pX - 3,           pY - 3 + 1,             z, pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(pX + esWidth + 2, pY - 3 + 1,             z, pX + esWidth + 3, pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY - 3 + 1,                 colOp, colOp);
            drawGradientRect(pX - 3,           pY + sumLineHeight + 2, z, pX + esWidth + 3, pY + sumLineHeight + 3,     color, color);

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            for (Tuple<ItemStack, String> stackDesc : tooltipData) {
                fr.drawString(stackDesc.value, pX + 17, pY, strColor.getRGB());
                GlStateManager.color(1F, 1F, 1F, 1F);
                GL11.glPushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                ri.renderItemAndEffectIntoGUI(stackDesc.key, pX - 1, pY - 5);

                GL11.glEnable(GL11.GL_BLEND);
                Blending.DEFAULT.apply();

                GL11.glPopMatrix();
                pY += 17;
            }
            GL11.glPopAttrib();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        GlStateManager.enableAlpha();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        TextureHelper.refreshTextureBindState();
    }

    public static void renderBlueTooltip(int x, int y, List<String> tooltipData, FontRenderer fontRenderer) {
        renderTooltip(x, y, tooltipData, new Color(0x000027), new Color(0x000044), Color.WHITE, fontRenderer);
    }

    public static void renderTooltip(int x, int y, List<String> tooltipData, Color color, Color colorFade, Color strColor, FontRenderer fontRenderer) {
        TextureHelper.setActiveTextureToAtlasSprite();
        boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
        if (lighting)
            RenderHelper.disableStandardItemLighting();

        if (!tooltipData.isEmpty()) {
            int esWidth = 0;
            for (String toolTip : tooltipData) {
                int width = fontRenderer.getStringWidth(toolTip);
                if (width > esWidth)
                    esWidth = width;
            }
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            if(x + 15 + esWidth > sr.getScaledWidth()) {
                x -= esWidth + 24;
            }
            int pX = x + 12;
            int pY = y - 12;
            int sumLineHeight = 8;
            if (tooltipData.size() > 1)
                sumLineHeight += 2 + (tooltipData.size() - 1) * 10;
            float z = 300F;

            drawGradientRect(pX - 3,           pY - 4,                 z, pX + esWidth + 3, pY - 3,                 color, colorFade);
            drawGradientRect(pX - 3,           pY + sumLineHeight + 3, z, pX + esWidth + 3, pY + sumLineHeight + 4, color, colorFade);
            drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX - 4,           pY - 3,                 z, pX - 3,           pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX + esWidth + 3, pY - 3,                 z, pX + esWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int rgb = color.getRGB();
            int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
            Color colOp = new Color(col);
            drawGradientRect(pX - 3,           pY - 3 + 1,             z, pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(pX + esWidth + 2, pY - 3 + 1,             z, pX + esWidth + 3, pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY - 3 + 1,                 colOp, colOp);
            drawGradientRect(pX - 3,           pY + sumLineHeight + 2, z, pX + esWidth + 3, pY + sumLineHeight + 3,     color, color);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int i = 0; i < tooltipData.size(); ++i) {
                String str = tooltipData.get(i);
                fontRenderer.drawString(str, pX, pY, strColor.getRGB());
                if (i == 0)
                    pY += 2;
                pY += 10;
            }
            GlStateManager.color(1F, 1F, 1F, 1F);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        if (lighting)
            RenderHelper.enableStandardItemLighting();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public static void renderBlueTooltipBox(int x, int y, int width, int height) {
        renderTooltipBox(x, y, width, height, new Color(0x000027), new Color(0x000044));
    }

    public static void renderTooltipBox(int x, int y, int width, int height, Color color, Color colorFade) {
        int pX = x + 12;
        int pY = y - 12;
        float z = 300F;

        drawGradientRect(pX - 3,           pY - 4,           z, pX + width + 3, pY - 3,         color, colorFade);
        drawGradientRect(pX - 3,           pY + height + 3,  z, pX + width + 3, pY + height + 4, color, colorFade);
        drawGradientRect(pX - 3,           pY - 3,           z, pX + width + 3, pY + height + 3, color, colorFade);
        drawGradientRect(pX - 4,           pY - 3,           z, pX - 3,         pY + height + 3, color, colorFade);
        drawGradientRect(pX + width + 3,   pY - 3,           z, pX + width + 4, pY + height + 3, color, colorFade);

        int rgb = color.getRGB();
        int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
        Color colOp = new Color(col);
        drawGradientRect(pX - 3,           pY - 3 + 1,      z, pX - 3 + 1,     pY + height + 3 - 1, color, colOp);
        drawGradientRect(pX + width + 2,   pY - 3 + 1,      z, pX + width + 3, pY + height + 3 - 1, color, colOp);
        drawGradientRect(pX - 3,           pY - 3,          z, pX + width + 3, pY - 3 + 1,          colOp, colOp);
        drawGradientRect(pX - 3,           pY + height + 2, z, pX + width + 3, pY + height + 3,     color, color);

        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public static void removeStandartTranslationFromTESRMatrix(float partialTicks) {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) rView = Minecraft.getMinecraft().player;
        Entity entity = rView;
        double tx = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double ty = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double tz = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        GlStateManager.translate(-tx, -ty, -tz);
    }

    public static Vector3 getStandartTranslationRemovalVector(float partialTicks) {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) rView = Minecraft.getMinecraft().player;
        Entity entity = rView;
        double tx = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double ty = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double tz = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        return new Vector3(-tx, -ty, -tz);
    }

    public static void renderAngleRotatedTexturedRectVB(Vector3 renderOffset, Vector3 axis, double angleRad, double scale, double u, double v, double uLength, double vLength, Color c, int alpha, BufferBuilder vb, float partialTicks) {
        GL11.glPushMatrix();
        //removeStandartTranslationFromTESRMatrix(partialTicks);
        Vector3 shift = getStandartTranslationRemovalVector(partialTicks);

        Vector3 renderStart = axis.clone().perpendicular().rotate(angleRad, axis).normalize();

        Vector3 vec = renderStart.clone().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(renderOffset);
        vb.pos(shift.getX() + vec.getX(), shift.getY() + vec.getY(), shift.getZ() + vec.getZ()).tex(u,           v + vLength).color(c.getRed(), c.getGreen(), c.getBlue(), alpha).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(renderOffset);
        vb.pos(shift.getX() + vec.getX(), shift.getY() + vec.getY(), shift.getZ() + vec.getZ()).tex(u + uLength, v + vLength).color(c.getRed(), c.getGreen(), c.getBlue(), alpha).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(renderOffset);
        vb.pos(shift.getX() + vec.getX(), shift.getY() + vec.getY(), shift.getZ() + vec.getZ()).tex(u + uLength, v          ).color(c.getRed(), c.getGreen(), c.getBlue(), alpha).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(renderOffset);
        vb.pos(shift.getX() + vec.getX(), shift.getY() + vec.getY(), shift.getZ() + vec.getZ()).tex(u,           v          ).color(c.getRed(), c.getGreen(), c.getBlue(), alpha).endVertex();

        GL11.glPopMatrix();
    }

    public static void renderAngleRotatedTexturedRect(Vector3 renderOffset, Vector3 axis, double angleRad, double scale, double u, double v, double uLength, double vLength, float partialTicks) {
        GlStateManager.pushMatrix();
        removeStandartTranslationFromTESRMatrix(partialTicks);

        Vector3 renderStart = axis.clone().perpendicular().rotate(angleRad, axis).normalize();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        Vector3 vec = renderStart.clone().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(renderOffset);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u,           v + vLength).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(renderOffset);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u + uLength, v + vLength).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(renderOffset);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u + uLength, v          ).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(renderOffset);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u,           v          ).endVertex();

        tes.draw();

        GlStateManager.popMatrix();
    }

    public static void drawGradientRect(int x, int y, float z, int toX, int toY, Color color, Color colorFade) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(toX, y,   z).color(color.getRed(),     color.getGreen(),     color.getBlue(),     color.getAlpha())    .endVertex();
        vb.pos(x,   y,   z).color(color.getRed(),     color.getGreen(),     color.getBlue(),     color.getAlpha())    .endVertex();
        vb.pos(x,   toY, z).color(colorFade.getRed(), colorFade.getGreen(), colorFade.getBlue(), colorFade.getAlpha()).endVertex();
        vb.pos(toX, toY, z).color(colorFade.getRed(), colorFade.getGreen(), colorFade.getBlue(), colorFade.getAlpha()).endVertex();
        tes.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void renderFacingFullQuadVB(BufferBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, float colorRed, float colorGreen, float colorBlue, float alpha) {
        renderFacingQuadVB(vb, px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1, colorRed, colorGreen, colorBlue, alpha);
    }

    public static void renderFacingQuadVB(BufferBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength, float colorRed, float colorGreen, float colorBlue, float alpha) {
        float arX =  ActiveRenderInfo.getRotationX();
        float arZ =  ActiveRenderInfo.getRotationZ();
        float arYZ = ActiveRenderInfo.getRotationYZ();
        float arXY = ActiveRenderInfo.getRotationXY();
        float arXZ = ActiveRenderInfo.getRotationXZ();

        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e == null) {
            e = Minecraft.getMinecraft().player;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec).normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }
        vb.pos(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ).tex(u + uLength,           v + vLength).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ).tex(u + uLength, v).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ).tex(u, v          ).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ).tex(u,           v + vLength).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
    }

    public static void renderFacingFullQuad(double px, double py, double pz, float partialTicks, float scale, float angle) {
        renderFacingQuad(px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1);
    }

    public static void renderFacingQuad(double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength) {
        float arX =  ActiveRenderInfo.getRotationX();
        float arZ =  ActiveRenderInfo.getRotationZ();
        float arYZ = ActiveRenderInfo.getRotationYZ();
        float arXY = ActiveRenderInfo.getRotationXY();
        float arXZ = ActiveRenderInfo.getRotationXZ();

        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e == null) {
            e = Minecraft.getMinecraft().player;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec).normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }
        Tessellator t = Tessellator.getInstance();
        BufferBuilder vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ).tex(u,           v + vLength).endVertex();
        vb.pos(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ).tex(u + uLength, v + vLength).endVertex();
        vb.pos(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ).tex(u + uLength, v          ).endVertex();
        vb.pos(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ).tex(u,           v          ).endVertex();
        t.draw();
    }

    public static void renderFacingFullColoredQuad(double px, double py, double pz, float partialTicks, float scale, float angle, int r, int g, int b, int a) {
        renderFacingColoredQuad(px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1, r, g, b, a);
    }

    public static void renderFacingColoredQuad(double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength, int r, int g, int b, int a) {
        float arX =  ActiveRenderInfo.getRotationX();
        float arZ =  ActiveRenderInfo.getRotationZ();
        float arYZ = ActiveRenderInfo.getRotationYZ();
        float arXY = ActiveRenderInfo.getRotationXY();
        float arXZ = ActiveRenderInfo.getRotationXZ();
        float cR = MathHelper.clamp(r / 255F, 0F, 1F);
        float cG = MathHelper.clamp(g / 255F, 0F, 1F);
        float cB = MathHelper.clamp(b / 255F, 0F, 1F);
        float cA = MathHelper.clamp(a / 255F, 0F, 1F);

        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e == null) {
            e = Minecraft.getMinecraft().player;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec).normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }
        Tessellator t = Tessellator.getInstance();
        BufferBuilder vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        vb.pos(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        vb.pos(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ).tex(u + uLength, v          ).color(cR, cG, cB, cA).endVertex();
        vb.pos(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ).tex(u,               v          ).color(cR, cG, cB, cA).endVertex();
        vb.pos(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ).tex(u,            v + vLength).color(cR, cG, cB, cA).endVertex();
        t.draw();
    }

    static {
        Field attempt;
        try {
            attempt = ReflectionHelper.findField(EntityItem.class, "skipPhysicRenderer");
            attempt.setAccessible(true);
        } catch(Exception e) {
            attempt = null;
        }
        itemPhysics_fieldSkipRenderHook = attempt;
    }

}
