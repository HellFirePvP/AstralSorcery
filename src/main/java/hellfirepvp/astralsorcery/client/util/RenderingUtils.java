/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:26
 */
public class RenderingUtils {

    private static final Random rand = new Random();
    private static IEnviromentBlockReader plainRenderWorld = null;

    @Nullable
    public static TextureAtlasSprite getParticleTexture(FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ResourceLocation res = stack.getFluid().getAttributes().getStill(stack);
        if (MissingTextureSprite.getLocation().equals(res)) {
            return null;
        }
        return Minecraft.getInstance().getTextureMap().getSprite(res);
    }

    @Nullable
    public static TextureAtlasSprite getParticleTexture(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ItemModelMesher imm = Minecraft.getInstance().getItemRenderer().getItemModelMesher();
        IBakedModel mdl = imm.getItemModel(stack);
        if (mdl.equals(imm.getModelManager().getMissingModel())) {
            return null;
        }
        return mdl.getParticleTexture(EmptyModelData.INSTANCE);
    }

    @Nullable
    public static TextureAtlasSprite getParticleTexture(BlockState state, @Nullable BlockPos positionHint) {
        World world = Minecraft.getInstance().world;
        if (world == null) {
            return null;
        }
        BlockPos pos = positionHint != null ? positionHint : BlockPos.ZERO;
        try {
            if (state.isAir(world, pos)) {
                return null;
            }
        } catch (Exception exc) {
            return null;
        }
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state, world, pos);
    }

    //Straight up ripped off of MC code.
    public static void playBlockBreakParticles(BlockPos pos, BlockState actualState, BlockState particleState) {
        World world = Minecraft.getInstance().world;
        ParticleManager mgr = Minecraft.getInstance().particles;

        VoxelShape voxelshape;
        try {
            voxelshape = actualState.getShape(world, pos);
        } catch (Exception exc) {
            voxelshape = VoxelShapes.fullCube();
        }
        voxelshape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double xDist = Math.min(1, maxX - minX);
            double yDist = Math.min(1, maxY - minY);
            double zDist = Math.min(1, maxZ - minZ);
            double i = Math.max(2, MathHelper.ceil(xDist / 0.25D));
            double j = Math.max(2, MathHelper.ceil(yDist / 0.25D));
            double k = Math.max(2, MathHelper.ceil(zDist / 0.25D));

            for (int xx = 0; xx < i; ++xx) {
                for (int yy = 0; yy < j; ++yy) {
                    for (int zz = 0; zz < k; ++zz) {

                        double d4 = (xx + 0.5D) / i;
                        double d5 = (yy + 0.5D) / j;
                        double d6 = (zz + 0.5D) / k;
                        double d7 = d4 * xDist + minX;
                        double d8 = d5 * yDist + minY;
                        double d9 = d6 * zDist + minZ;

                        DiggingParticle p = (new DiggingParticle(world,
                                pos.getX() + d7, pos.getY() + d8, pos.getZ() + d9,
                                d4 - 0.5D, d5 - 0.5D, d6 - 0.5D,
                                particleState));
                        p.init();
                        p.setBlockPos(pos);
                        mgr.addEffect(p);
                    }
                }
            }

        });
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

    public static boolean canEffectExist(EntityComplexFX fx) {
        Entity view = Minecraft.getInstance().getRenderViewEntity();
        if (view == null) {
            view = Minecraft.getInstance().player;
        }
        if (view == null) {
            return false;
        }
        return fx.getPosition().distanceSquared(view) <= RenderingConfig.CONFIG.getMaxEffectRenderDistanceSq();
    }

    public static void renderItemAsEntity(ItemStack stack, double x, double y, double z, float pTicks, int age) {
        ItemEntity ei = new ItemEntity(Minecraft.getInstance().world, x, y, z, stack);
        ei.age = age;
        ei.hoverStart = 0;
        Minecraft.getInstance().getRenderManager().renderEntity(ei, x, y, z, 0, pTicks, true);
        TextureHelper.bindBlockAtlas();
    }

    public static void renderItemStack(ItemRenderer itemRenderer, ItemStack stack, int x, int y, @Nullable String alternativeText) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();

        GlStateManager.translatef(0.0F, 0.0F, 32.0F);
        itemRenderer.zLevel = 200.0F;
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) {
            font = Minecraft.getInstance().fontRenderer;
        }
        itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y, alternativeText);

        itemRenderer.zLevel = 0.0F;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
    }

    public static void renderTranslucentItemStack(ItemStack stack, double x, double y, double z, float pTicks) {
        renderTranslucentItemStack(stack, x, y, z, pTicks, Color.WHITE, 0.1F);
    }

    public static void renderTranslucentItemStack(ItemStack stack, double x, double y, double z, float pTicks, Color overlayColor, float alpha) {
        GlStateManager.pushMatrix();
        IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModelWithOverrides(stack);

        // EntityItemRenderer entity bobbing
        float sinBobY = MathHelper.sin((ClientScheduler.getClientTick() + pTicks) / 10.0F) * 0.1F + 0.1F;
        GlStateManager.translated(x, y + sinBobY, z);
        float ageRotate = ((ClientScheduler.getClientTick() + pTicks) / 20.0F) * (180F / (float) Math.PI);
        GlStateManager.rotated(ageRotate, 0.0F, 1.0F, 0.0F);

        bakedModel = bakedModel.handlePerspective(ItemCameraTransforms.TransformType.GROUND).getLeft();

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.enableRescaleNormal();
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        GlStateManager.enableBlend();
        Blending.PREALPHA.applyStateManager();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);

        renderItemModelWithColor(stack, bakedModel, overlayColor, alpha);

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableBlend();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableRescaleNormal();

        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        GlStateManager.popMatrix();
    }

    private static void renderItemModelWithColor(ItemStack stack, IBakedModel model, Color c, float alpha) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translated(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer()) {
                GlStateManager.color4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, alpha);
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
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
        Random renderRand = new Random();
        IModelData data = EmptyModelData.INSTANCE;

        for (Direction dir : Direction.values()) {
            renderRand.setSeed(42);
            renderColoredQuads(bufferbuilder, model.getQuads(null, dir, renderRand, data), alphaColor, stack);
        }

        renderRand.setSeed(42);
        renderColoredQuads(bufferbuilder, model.getQuads(null, null, renderRand, data), alphaColor, stack);
        tessellator.draw();
    }

    private static void renderColoredQuads(BufferBuilder vb, List<BakedQuad> quads, Color color, ItemStack stack) {
        boolean useOverlayColors = color.equals(Color.WHITE) && !stack.isEmpty();
        int i = 0;

        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = quads.get(i);
            int col = color.getRGB();
            if (useOverlayColors && bakedquad.hasTintIndex()) {
                col = itemColors.getColor(stack, bakedquad.getTintIndex());
            }

            col &= 0x00FFFFFF;
            col |= (color.getAlpha() << 24);

            LightUtil.renderQuadColor(vb, bakedquad, col);
        }
    }

    public static void renderSimpleBlockModel(BlockState state, BufferBuilder buf) {
        renderSimpleBlockModel(state, buf, BlockPos.ZERO, null);
    }

    public static void renderSimpleBlockModel(BlockState state, BufferBuilder buf, BlockPos pos, @Nullable TileEntity te) {
        if (plainRenderWorld == null) {
            plainRenderWorld = new EmptyRenderWorld(Biomes.PLAINS);
        }

        BlockRenderType brt = state.getRenderType();
        if (brt == BlockRenderType.INVISIBLE) {
            return;
        }
        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRendererDispatcher();
        IModelData data = EmptyModelData.INSTANCE;
        if (te != null) {
            data = te.getModelData();
        }
        if (brt == BlockRenderType.MODEL) {
            IBakedModel model = brd.getModelForState(state);
            brd.getBlockModelRenderer().renderModel(plainRenderWorld, model, state, pos, buf, false, rand, state.getPositionRandom(pos), data);
        }
    }

}
