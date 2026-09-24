/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.fluid;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.ActiveLiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightBlock extends LiquidBlock {

    private static final RandomSource rand = RandomSource.create();

    public LiquidStarlightBlock(FlowingFluid fluid) {
        super(fluid, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                .lightLevel(state -> 15));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, true));
        }
        if (entity instanceof ItemEntity itemEntity) {
            ActiveLiquidStarlightRecipe.tryProgressCraft(itemEntity);

            if (!level.isClientSide() && itemEntity.getItem().isEmpty()) {
                itemEntity.discard();
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (level.isClientSide()) {
            this.playLiquidStarlightEffects(state, pos);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playLiquidStarlightEffects(BlockState state, BlockPos pos) {
        Integer level = state.getValue(LEVEL);
        double percHeight = 1D - (((double) level + 1) / 8D);
        Vector3 at = new Vector3(pos).add(rand.nextFloat(), percHeight, rand.nextFloat());

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .setScale(0.2F + rand.nextFloat() * 0.1F)
                .alpha(FXAlphaFunction.FADE_OUT)
                .setGravity(Vector3.y(0.0005F))
                .setMaxAge(30 + rand.nextInt(10));
    }
}
