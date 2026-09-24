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
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.types.LiquidStarlightRecipeOutputTypesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightOutputMergeCrystal
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightOutputMergeCrystal extends LiquidStarlightRecipeOutputModifier {

    private static final LiquidStarlightOutputMergeCrystal INSTANCE = new LiquidStarlightOutputMergeCrystal();
    public static final MapCodec<LiquidStarlightOutputMergeCrystal> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightOutputMergeCrystal> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<LiquidStarlightOutputMergeCrystal> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private LiquidStarlightOutputMergeCrystal() {}

    public static LiquidStarlightOutputMergeCrystal getInstance() {
        return INSTANCE;
    }

    @Override
    public Type<?> getType() {
        return LiquidStarlightRecipeOutputTypesAS.MERGE_CRYSTAL.get();
    }

    @Override
    public boolean isOutput(LiquidStarlightRecipeInput input, ItemEntity output) {
        return false;
    }

    @Override
    public void createOutput(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input) {
        if (input.getOtherEntities().isEmpty()) return;

        RandomSource rand = RandomSource.create();
        ItemEntity crystal = input.getTriggerEntity();
        ItemEntity otherCrystal = input.getOtherEntities().getFirst();

        CrystalAttributesComponent cmpCrystal = crystal.getItem().getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
        CrystalAttributesComponent cmpOther = otherCrystal.getItem().getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());

        CrystalAttributesComponent mergeTo = cmpCrystal.getTotalTierCount() >= cmpOther.getTotalTierCount() ? cmpCrystal : cmpOther;
        CrystalAttributesComponent mergeFrom = mergeTo == cmpCrystal ? cmpOther : cmpCrystal;

        ItemStack resultStack = mergeTo == cmpCrystal ? crystal.getItem().copy() : otherCrystal.getItem().copy();

        int freeProperties = mergeTo.getProperties().maxTierCount() - mergeTo.getTotalTierCount();
        int copyAmount = Math.min(freeProperties, mergeFrom.getTotalTierCount());
        int mergeCount = 0;
        for (int i = 0; i < copyAmount; i++) {
            Optional<CrystalAttributesComponent.TieredAttribute> opt = MiscUtil.getWeightedRandomEntry(mergeFrom.getAttributes(), rand, CrystalAttributesComponent.TieredAttribute::getTier);
            if (opt.isPresent()) {
                CrystalAttributesComponent.TieredAttribute attr = opt.get();
                mergeFrom = mergeFrom.setAttributeTier(attr, attr.getTier() - 1);
                if (rand.nextFloat() <= (1F - Math.min(mergeCount, 3) * 0.2F)) {
                    mergeTo = mergeTo.setAttributeTier(attr, mergeTo.getAttributeTier(attr) + 1);
                }
                mergeCount++;
            }
        }

        resultStack.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, mergeTo);
        ItemUtil.dropItemNaturally(crystal.level(), crystal.getX(), crystal.getY(), crystal.getZ(), resultStack);
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
