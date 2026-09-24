/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.output;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.EnumExtensions;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.FlagsComponent;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.ingredient.IsLumenBindableIngredient;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.types.LiquidStarlightRecipeOutputTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.lumen.binding.data.LumenBindingTypeLoader;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.MobEffectUtil;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightOutputBindLumen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightOutputBindLumen extends LiquidStarlightRecipeOutputModifier {

    private static final LiquidStarlightOutputBindLumen INSTANCE = new LiquidStarlightOutputBindLumen();
    public static final MapCodec<LiquidStarlightOutputBindLumen> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightOutputBindLumen> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<LiquidStarlightOutputBindLumen> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final RandomSource rand = RandomSource.create();

    private LiquidStarlightOutputBindLumen() {}

    public static LiquidStarlightOutputBindLumen getInstance() {
        return INSTANCE;
    }

    @Override
    public Type<?> getType() {
        return LiquidStarlightRecipeOutputTypesAS.BIND_LUMEN.get();
    }

    @Override
    public boolean isOutput(LiquidStarlightRecipeInput input, ItemEntity output) {
        return output.is(input.getTriggerEntity());
    }

    @Override
    public void createOutput(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input) {
        ItemEntity inputEntity = input.getTriggerEntity();
        Level level = inputEntity.level();
        LogicalSide side = SidedHelper.getSide(level);

        ItemStack bindableInput = input.getOtherEntities().stream()
                .map(ItemEntity::getItem)
                .filter(IsLumenBindableIngredient.INSTANCE::test)
                .findFirst()
                .orElse(ItemStack.EMPTY);
        if (bindableInput.isEmpty()) return;

        Holder<Lumen> lumenRef = inputEntity.getItem().getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen();
        if (lumenRef.is(LumenAS.NONE.getKey())) return;
        Lumen lumen = lumenRef.value();

        LumenBindingTypeLoader.getInstance().getLumenBindingType(side, lumenRef.getKey()).ifPresent(bindingKey -> {
            ItemStack output = bindableInput.copyWithCount(1);

            if (IsLumenBindableIngredient.INSTANCE.isPotionLumenBindable(output)) {
                if (lumenRef.is(LumenAS.PRISMATIC.getKey())) {
                    FlagsComponent cmp = output.getOrDefault(DataComponentsAS.FLAGS, FlagsComponent.EMPTY);
                    if (!cmp.isSet(FlagsComponent.Flag.HAS_PRISMATIC_LUMEN)) {

                        PotionContents contents = output.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                        PotionContents newContents = new PotionContents(Optional.empty(), contents.customColor(), List.of());
                        for (MobEffectInstance effect : contents.customEffects()) {
                            effect = MobEffectUtil.newDuration(effect, Mth.ceil(effect.getDuration() * (1.2F + this.rand.nextFloat() * 0.5F)));
                            effect = MobEffectUtil.newAmplifier(effect, effect.getAmplifier() + 1);
                            newContents = newContents.withEffectAdded(effect);
                        }
                        output.set(DataComponents.POTION_CONTENTS, newContents);

                        cmp = cmp.setFlag(FlagsComponent.Flag.HAS_PRISMATIC_LUMEN);
                        output.set(DataComponentsAS.FLAGS, cmp);
                        output.set(DataComponents.RARITY, EnumExtensions.RARITY_RELIC.getValue());
                    }
                } else {
                    LumenBindingTypeLoader.getInstance().getBindingType(side, bindingKey).ifPresent(bindingType -> {
                        bindingType.getPotionEffect().ifPresent(effect -> {
                            PotionContents contents = output.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                            contents = new PotionContents(Optional.empty(), contents.customColor(), contents.customEffects());
                            contents = contents.withEffectAdded(effect);
                            output.set(DataComponents.POTION_CONTENTS, contents);
                            output.set(DataComponents.RARITY, EnumExtensions.RARITY_RELIC.getValue());

                            ItemUtil.dropItem(level, inputEntity.getX(), inputEntity.getY(), inputEntity.getZ(), output);
                            recipe.consumeItemInputs(input);
                        });
                    });
                }
                return;
            }

            StoredLumenComponent cmp = output.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
            int storeMaxAmount = cmp.getStoredLumen(lumen).map(StoredLumenComponent.StoredLumen::maxAmount).orElse(cmp.properties().capacity());
            int storedAmount = cmp.getStoredLumen(lumen).map(StoredLumenComponent.StoredLumen::amount).orElse(0);

            if (!lumenRef.is(LumenAS.PRISMATIC.getKey())) {
                Set<Lumen> otherCopy = new HashSet<>(cmp.boundLumen().keySet());
                for (Lumen otherLumen : otherCopy) {
                    if (otherLumen.getRegistryKey().isEmpty() || !LumenAS.PRISMATIC.is(otherLumen.getRegistryKey().get())) {
                        cmp = cmp.removeBinding(otherLumen);
                        cmp = cmp.updateLumenStack(otherLumen, 0, cmp.properties().capacity());
                    }
                }
            }

            cmp = cmp.updateLumenStack(lumen, Math.min(storedAmount + StoredLumenComponent.DEFAULT_CAPACITY, storeMaxAmount), storeMaxAmount);
            cmp = cmp.updateProperties(StoredLumenComponent.Properties::allowFill);
            output.set(DataComponentsAS.STORED_LUMEN, cmp);
            LumenBindingType.applyBinding(lumen, bindingKey, output);

            ItemUtil.dropItemNaturally(level, inputEntity.getX(), inputEntity.getY(), inputEntity.getZ(), output);
            recipe.consumeItemInputs(input);
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playCraftingEffects(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input, RandomSource rand, int craftingTick) {
        super.playCraftingEffects(recipe, input, rand, craftingTick);

        ItemEntity inputEntity = input.getTriggerEntity();
        Holder<Lumen> lumenRef = inputEntity.getItem().getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen();
        if (lumenRef.is(LumenAS.NONE.getKey())) return;
        Lumen lumen = lumenRef.value();

        long tick = ClientProxy.getClientTick();
        ColorWrapper color = lumen.getColor(tick);

        if (rand.nextInt(7) == 0) {
            Vector3 pos = new Vector3(input.getTriggerEntity());
            pos = VectorUtil.withRandomOffset(pos, rand, 0.1F).setY(pos.getY());

            EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                    .spawn(pos)
                    .setup(pos.copy().addY(4F + rand.nextFloat() * 3F), 0.8F, 0.8F)
                    .color(FXColorFunction.constant(() -> lumen.getColor(tick)));
        }

        for (int i = 0; i < 3; i++) {
            Vector3 grav = Vector3.y(-0.002F + rand.nextFloat() * -0.001F);
            float scale = 0.15F + rand.nextFloat() * 0.1F;

            Vector3 pos = new Vector3(input.getTriggerEntity());
            pos = VectorUtil.withRandomOffset(pos, rand, 0.4F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(FXColorFunction.constant(color))
                    .setScale(scale)
                    .setAlpha(0.8F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setMotion(Vector3.y(0.1F))
                    .setGravity(grav);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(FXColorFunction.WHITE)
                    .setScale(scale * 0.3F)
                    .setAlpha(0.8F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setMotion(Vector3.y(0.1F))
                    .setGravity(grav);
        }
    }
}
