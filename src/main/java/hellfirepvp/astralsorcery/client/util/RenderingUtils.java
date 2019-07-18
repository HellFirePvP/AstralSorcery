/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nonnull
    public static TextureAtlasSprite getSprite(FluidStack stack) {
        ResourceLocation res = stack.getFluid().getFlowing(stack);
        return Minecraft.getInstance().getTextureMap().getSprite(res);
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
