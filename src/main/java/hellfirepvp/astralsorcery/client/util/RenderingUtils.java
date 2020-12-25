/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.reflection.ReflectionHelper;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import hellfirepvp.observerlib.client.util.RenderTypeDecorator;
import hellfirepvp.observerlib.common.util.RegistryUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ObjectUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:26
 */
public class RenderingUtils {

    private static final Random rand = new Random();
    private static IBlockDisplayReader plainRenderWorld = null;

    public static long getPositionSeed(BlockPos pos) {
        long seed = 1553015L;
        seed ^= pos.getX();
        seed ^= pos.getY();
        seed ^= pos.getZ();
        return seed;
    }

    @Nullable
    public static TextureAtlasSprite getParticleTexture(FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ResourceLocation res = stack.getFluid().getAttributes().getStillTexture(stack);
        if (MissingTextureSprite.getLocation().equals(res)) {
            return null;
        }
        return Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(res);
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
    public static void playBlockBreakParticles(BlockPos pos, @Nullable BlockState actualState, BlockState particleState) {
        ClientWorld world = Minecraft.getInstance().world;
        ParticleManager mgr = Minecraft.getInstance().particles;

        VoxelShape voxelshape;
        try {
            voxelshape = actualState == null ? VoxelShapes.fullCube() : actualState.getShape(world, pos);
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

    public static void translate(MatrixStack renderStack, float x, float y, float z, Consumer<MatrixStack> fn) {
        renderStack.push();
        renderStack.translate(x, y, z);
        fn.accept(renderStack);
        renderStack.pop();
    }

    public static void draw(int drawMode, VertexFormat format, Consumer<BufferBuilder> fn) {
        draw(drawMode, format, bufferBuilder -> {
            fn.accept(bufferBuilder);
            return null;
        });
    }

    public static <R> R draw(int drawMode, VertexFormat format, Function<BufferBuilder, R> fn) {
        BufferBuilder buf = Tessellator.getInstance().getBuffer();
        buf.begin(drawMode, format);
        R result = fn.apply(buf);
        finishDrawing(buf);
        return result;
    }

    public static void finishDrawing(BufferBuilder buf) {
        finishDrawing(buf, null);
    }

    public static void finishDrawing(BufferBuilder buf, @Nullable RenderType type) {
        if (buf.isDrawing()) {
            if (type != null) {
                type.finish(buf, 0, 0, 0);
            } else {
                buf.finishDrawing();
                WorldVertexBufferUploader.draw(buf);
            }
        }
    }

    public static void refreshDrawing(IVertexBuilder vb, RenderType type) {
        if (vb instanceof BufferBuilder) {
            type.finish((BufferBuilder) vb, 0, 0, 0);
            ((BufferBuilder) vb).begin(type.getDrawMode(), type.getVertexFormat());
        }
    }

    public static int renderInWorldText(ITextProperties text, Color color, Vector3 at, MatrixStack renderStack, float pTicks, boolean facePlayer) {
        float scale = (float) Minecraft.getInstance().getMainWindow().getGuiScaleFactor();
        return renderInWorldText(text, color, 0.02F * (Minecraft.getInstance().gameSettings.guiScale / scale), at, renderStack, pTicks, facePlayer);
    }

    public static int renderInWorldText(ITextProperties text, Color color, float scale, Vector3 at, MatrixStack renderStack, float pTicks, boolean facePlayer) {
        FontRenderer fr = Minecraft.getInstance().fontRenderer;

        renderStack.push();
        renderStack.translate(at.getX(), at.getY(), at.getZ());
        renderStack.scale(scale, -scale, scale);

        if (facePlayer) {
            Entity le = Minecraft.getInstance().renderViewEntity;
            if (le == null) {
                le = Minecraft.getInstance().player;
            }
            float iYaw = RenderingVectorUtils.interpolate(MathHelper.wrapDegrees(le.prevRotationYaw), MathHelper.wrapDegrees(le.rotationYaw), pTicks);
            renderStack.rotate(Vector3f.YP.rotationDegrees(-iYaw + 180F));
        }

        Matrix4f matr = renderStack.getLast().getMatrix();
        int length = fr.getStringPropertyWidth(text);
        IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        IReorderingProcessor processedText = LanguageMap.getInstance().func_241870_a(text);
        int drawnLength = fr.func_238416_a_(processedText, -(length / 2F), 0, color.getRGB(), false, matr, buffers, true, 0, LightmapUtil.getPackedFullbrightCoords());
        buffers.finish();

        renderStack.pop();
        return drawnLength;
    }

    public static void renderItemAsEntity(ItemStack stack, MatrixStack renderStack, IRenderTypeBuffer buffers, double x, double y, double z, int combinedLight, float pTicks, int age) {
        ItemEntity ei = new ItemEntity(Minecraft.getInstance().world, x, y, z, stack);
        ei.age = age;
        ei.hoverStart = 0;
        ReflectionHelper.setSkipItemPhysicsRender(ei);
        Minecraft.getInstance().getRenderManager().renderEntityStatic(ei, x, y, z, 0F, pTicks, renderStack, buffers, combinedLight);
    }

    public static void renderItemStackGUI(MatrixStack renderStack, ItemStack stack, @Nullable String alternativeText) {
        renderStack.push();
        renderStack.translate(0, 0, 100F);
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) {
            font = Minecraft.getInstance().fontRenderer;
        }
        renderTranslucentItemStackModelGUI(stack, renderStack, Color.WHITE, Blending.DEFAULT, 255);
        mcdefault_renderItemOverlayIntoGUI(font, renderStack, stack, Minecraft.getInstance().getRenderPartialTicks(), alternativeText);

        renderStack.pop();
    }

    public static void renderTranslucentItemStack(ItemStack stack, MatrixStack renderStack, float pTicks) {
        renderTranslucentItemStack(stack, renderStack, pTicks, Color.WHITE, 25);
    }

    public static void renderTranslucentItemStack(ItemStack stack, MatrixStack renderStack, float pTicks, Color overlayColor, int alpha) {
        renderStack.push();

        // EntityItemRenderer entity bobbing
        float sinBobY = MathHelper.sin((ClientScheduler.getClientTick() + pTicks) / 10.0F) * 0.1F + 0.1F;
        renderStack.translate(0, sinBobY, 0);
        float ageRotate = ((ClientScheduler.getClientTick() + pTicks) / 20.0F);
        renderStack.rotate(Vector3f.YP.rotation(ageRotate));

        renderTranslucentItemStackModelGround(stack, renderStack, overlayColor, Blending.PREALPHA, alpha);

        renderStack.pop();
    }

    public static void renderTranslucentItemStackModelGround(ItemStack stack, MatrixStack renderStack, Color overlayColor, Blending blendMode, int alpha) {
        IBakedModel bakedModel = getItemModel(stack);
        ForgeHooksClient.handleCameraTransforms(renderStack, bakedModel, ItemCameraTransforms.TransformType.GROUND, false);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();

        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        renderItemModelWithColor(stack, ItemCameraTransforms.TransformType.GROUND, bakedModel, renderStack, (renderType) -> {
            RenderTypeDecorator decorated = RenderTypeDecorator.wrapSetup(renderType, () -> {
                RenderSystem.enableBlend();
                blendMode.apply();
            }, () -> {
                Blending.DEFAULT.apply();
                RenderSystem.disableBlend();
            });
            return buffer.getBuffer(decorated);
        }, LightmapUtil.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY, overlayColor, alpha);
        buffer.finish();
    }

    public static void renderTranslucentItemStackModelGUI(ItemStack stack, MatrixStack renderStack, Color overlayColor, Blending blendMode, int alpha) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        blendMode.apply();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderStack.push();
        renderStack.translate(8.0F, 8.0F, 0.0F);
        renderStack.scale(16.0F, -16.0F, 16.0F);

        IBakedModel bakedModel = ForgeHooksClient.handleCameraTransforms(renderStack, getItemModel(stack), ItemCameraTransforms.TransformType.GUI, false);
        boolean isSideLit = bakedModel.isSideLit();
        if (!isSideLit) {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }

        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        renderItemModelWithColor(stack, ItemCameraTransforms.TransformType.GUI, bakedModel, renderStack, buffer,
                LightmapUtil.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY, overlayColor, MathHelper.clamp(alpha, 0, 255));
        buffer.finish();

        if (!isSideLit) {
            RenderHelper.setupGui3DDiffuseLighting();
        }

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.enableDepthTest();
        renderStack.pop();
    }

    //TODO wait for mojang to do their work and actually port this method so i don't have to do this myself
    @Deprecated
    public static void mcdefault_renderItemOverlayIntoGUI(FontRenderer fr, MatrixStack renderStack, ItemStack stack, float pTicks, @Nullable String text) {
        if (stack.isEmpty()) {
            return;
        }
        //TODO ugh.
        RenderSystem.disableLighting();

        renderStack.push();
        renderStack.translate(0, 0, 100F);
        if (stack.getCount() > 1 || text != null) {
            ITextProperties display = new StringTextComponent(ObjectUtils.firstNonNull(text, String.valueOf(stack.getCount())));
            int length = fr.getStringPropertyWidth(display);

            renderStack.push();
            renderStack.translate(17 - length, 9, 0);
            RenderingDrawUtils.renderStringAt(display, renderStack, fr, 0xFFFFFFFF, true);
            renderStack.pop();
        }

        if (stack.getItem().showDurabilityBar(stack)) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();

            float health = (float) stack.getItem().getDurabilityForDisplay(stack);
            float durabilityPercent = 13F - health * 13F;
            int color = stack.getItem().getRGBDurabilityForDisplay(stack);

            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, renderStack, 2, 13, 0, 13, 2)
                        .color(0, 0, 0, 255)
                        .draw();
                RenderingGuiUtils.rect(buf, renderStack, 2, 13, 0, durabilityPercent, 1)
                        .color(color >> 16 & 255, color >> 8 & 255, color & 255, 255)
                        .draw();
            });

            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        float cooldownPercent = player == null ? 0F : player.getCooldownTracker().getCooldown(stack.getItem(), pTicks);
        if (cooldownPercent > 0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, renderStack, 0, 16F * (1F - cooldownPercent), 0, 16, 16F * cooldownPercent)
                        .color(255, 255, 255, 127)
                        .draw();
            });

            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
        renderStack.pop();
    }

    private static IBakedModel getItemModel(ItemStack stack) {
        return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, Minecraft.getInstance().world, Minecraft.getInstance().player);
    }

    private static void renderItemModelWithColor(ItemStack stack, ItemCameraTransforms.TransformType transformType, IBakedModel model, MatrixStack renderStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, Color c, int alpha) {
        if (!stack.isEmpty()) {
            renderStack.push();
            renderStack.translate(-0.5, -0.5, -0.5);

            boolean renderThirdPersonView = transformType == ItemCameraTransforms.TransformType.GUI ||
                    transformType == ItemCameraTransforms.TransformType.GROUND ||
                    transformType == ItemCameraTransforms.TransformType.FIXED;

            if (model.isBuiltInRenderer() || (stack.getItem() == Items.TRIDENT && !renderThirdPersonView)) {
                int[] colors = new int[] { c.getRed(), c.getGreen(), c.getBlue(), alpha };
                IRenderTypeBuffer decoratedBuffer = type -> BufferDecoratorBuilder.withColor((r, g, b, a) -> colors).decorate(buffer.getBuffer(type));
                stack.getItem().getItemStackTileEntityRenderer().func_239207_a_(stack, transformType, renderStack, decoratedBuffer, combinedLight, combinedOverlay);
            } else if (model.isLayered()) {
                for (Pair<IBakedModel, RenderType> layerModel : model.getLayerModels(stack, true)) {
                    IBakedModel layer = layerModel.getFirst();
                    RenderType rType = layerModel.getSecond();
                    ForgeHooksClient.setRenderLayer(rType);
                    try {
                        IVertexBuilder vertexBuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, rType, true, stack.hasEffect());
                        renderColoredItemModel(stack, layer, renderStack, vertexBuilder,combinedLight, combinedOverlay, c, alpha);
                    } finally {
                        ForgeHooksClient.setRenderLayer(null);
                    }
                }
            } else {
                //Always get translucent renderType
                RenderType rType = RenderTypeLookup.func_239219_a_(stack, true);
                IVertexBuilder vertexBuilder;

                //Wth are you doing here mojang. Taken from ItemRenderer#renderItem
                if (stack.getItem() instanceof CompassItem && stack.hasEffect()) {
                    renderStack.push();
                    MatrixStack.Entry topEntry = renderStack.getLast();

                    if (transformType == ItemCameraTransforms.TransformType.GUI) {
                        topEntry.getMatrix().mul(0.5F);
                    } else if (transformType.isFirstPerson()) {
                        topEntry.getMatrix().mul(0.75F);
                    }
                    vertexBuilder = ItemRenderer.getDirectGlintVertexBuilder(buffer, rType, topEntry);
                    renderStack.pop();
                } else {
                    vertexBuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, rType, true, stack.hasEffect());
                }

                renderColoredItemModel(stack, model, renderStack, vertexBuilder, combinedLight, combinedOverlay, c, alpha);
            }

            renderStack.pop();
        }
    }

    private static void renderColoredItemModel(ItemStack stack, IBakedModel model, MatrixStack renderStack, IVertexBuilder buffer, int combinedLight, int combinedOverlay, Color color, int alpha) {
        Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

        Random renderRand = new Random();
        IModelData data = EmptyModelData.INSTANCE;
        for (Direction dir : Direction.values()) {
            renderRand.setSeed(42);
            renderColoredQuads(buffer, renderStack, model.getQuads(null, dir, renderRand, data), alphaColor, combinedLight, combinedOverlay, stack);
        }

        renderRand.setSeed(42);
        renderColoredQuads(buffer, renderStack, model.getQuads(null, null, renderRand, data), alphaColor, combinedLight, combinedOverlay, stack);
    }

    private static void renderColoredQuads(IVertexBuilder vb, MatrixStack renderStack, List<BakedQuad> quads, Color color, int combinedLight, int combinedOverlay, ItemStack stack) {
        boolean useOverlayColors = (color.getRGB() & 0xFFFFFF) == 0xFFFFFF && !stack.isEmpty();
        int i = 0;

        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = quads.get(i);
            int col = color.getRGB();
            if (useOverlayColors && bakedquad.hasTintIndex()) {
                col = itemColors.getColor(stack, bakedquad.getTintIndex());
            }

            float r = (col >> 16 & 255) / 255F;
            float g = (col >> 8 & 255) / 255F;
            float b = (col & 255) / 255F;
            float a = color.getAlpha() / 255F;

            vb.addVertexData(renderStack.getLast(), bakedquad, r, g, b, a, combinedLight, combinedOverlay, true);
        }
    }

    public static void renderSimpleBlockModel(BlockState state, MatrixStack renderStack, IVertexBuilder vb) {
        renderSimpleBlockModel(state, renderStack, vb, BlockPos.ZERO, null, false);
    }

    public static void renderSimpleBlockModel(BlockState state, MatrixStack renderStack, IVertexBuilder vb, BlockPos pos, @Nullable TileEntity te, boolean checkRenderSide) {
        if (plainRenderWorld == null) {
            plainRenderWorld = new EmptyRenderWorld(() -> RegistryUtil.client().getValue(Registry.BIOME_KEY, Biomes.PLAINS));
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
        brd.renderModel(state, pos, plainRenderWorld, renderStack, vb, checkRenderSide, rand, data);
    }

    public static void renderSimpleBlockModelCurrentWorld(BlockState state, MatrixStack renderStack, IVertexBuilder buf, int combinedOverlayIn) {
        renderSimpleBlockModelCurrentWorld(state, renderStack, buf, BlockPos.ZERO, null, combinedOverlayIn, false);
    }

    public static void renderSimpleBlockModelCurrentWorld(BlockState state, MatrixStack renderStack, IVertexBuilder buf, BlockPos pos, @Nullable TileEntity te, int combinedOverlayIn, boolean checkRenderSide) {
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
            brd.getBlockModelRenderer().renderModel(Minecraft.getInstance().world, model, state, pos, renderStack, buf, checkRenderSide, rand, state.getPositionRandom(pos), combinedOverlayIn, data);
        }
    }
}
