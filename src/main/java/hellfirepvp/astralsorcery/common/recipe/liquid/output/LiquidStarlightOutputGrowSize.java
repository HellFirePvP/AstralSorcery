/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.output;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXMotionFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.types.LiquidStarlightRecipeOutputTypesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightOutputGrowSize
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightOutputGrowSize extends LiquidStarlightRecipeOutputModifier {

    private static final LiquidStarlightOutputGrowSize INSTANCE = new LiquidStarlightOutputGrowSize();
    public static final MapCodec<LiquidStarlightOutputGrowSize> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightOutputGrowSize> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<LiquidStarlightOutputGrowSize> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private LiquidStarlightOutputGrowSize() {}

    public static LiquidStarlightOutputGrowSize getInstance() {
        return INSTANCE;
    }

    @Override
    public Type<?> getType() {
        return LiquidStarlightRecipeOutputTypesAS.GROW_SIZE.get();
    }

    @Override
    public boolean isOutput(LiquidStarlightRecipeInput input, ItemEntity output) {
        return false;
    }

    @Override
    public void createOutput(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input) {
        ItemStack crystal = input.getTriggerEntity().getItem().copyWithCount(1);
        CrystalAttributesComponent cmp = crystal.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());

        if (cmp.getTotalTierCount() >= cmp.getProperties().maxTierCount()) return;

        float chance = 1;
        if (cmp.getTotalTierCount() >= cmp.getProperties().generateCount()) {
            chance = 0.4F;
        }

        if (RandomSource.create().nextFloat() < chance) {
            int current = cmp.getAttributeTier(CrystalPropertiesAS.SIZE);
            if (current >= CrystalPropertiesAS.SIZE.get().getMaxTier()) return;
            cmp = cmp.setAttributeTier(CrystalPropertiesAS.SIZE, current + 1);
            crystal.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, cmp);
        }

        ItemEntity trigger = input.getTriggerEntity();
        ItemUtil.dropItem(trigger.level(), trigger.getX(), trigger.getY(), trigger.getZ(), crystal);
        recipe.consumeItemInputs(input);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playCraftingEffects(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input, RandomSource rand, int craftingTick) {
        super.playCraftingEffects(recipe, input, rand, craftingTick);

        for (int i = 0; i < 4; i++) {
            Vector3 target = new Vector3(input.getTriggerEntity());
            Vector3 pos = VectorUtil.withRandomOffset(target, rand, 1F + rand.nextFloat() * 1.5F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(0.15F + rand.nextFloat() * 0.2F)
                    .setAlpha(0.8F)
                    .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.proximity(target::copy, 0.1F)))
                    .color(FXColorFunction.WHITE)
                    .motion(FXMotionFunction.target(target::copy, 0.04F))
                    .setMaxAge(20 + rand.nextInt(10));
        }
    }
}
