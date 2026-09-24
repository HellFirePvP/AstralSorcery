/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.output;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.types.LiquidStarlightRecipeOutputTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightOutputFillLumen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightOutputFillLumen extends LiquidStarlightRecipeOutputModifier {

    private static final LiquidStarlightOutputFillLumen INSTANCE = new LiquidStarlightOutputFillLumen();
    public static final MapCodec<LiquidStarlightOutputFillLumen> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightOutputFillLumen> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<LiquidStarlightOutputFillLumen> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private LiquidStarlightOutputFillLumen() {}

    public static LiquidStarlightOutputFillLumen getInstance() {
        return INSTANCE;
    }

    @Override
    public Type<?> getType() {
        return LiquidStarlightRecipeOutputTypesAS.FILL_LUMEN.get();
    }

    @Override
    public boolean isOutput(LiquidStarlightRecipeInput input, ItemEntity output) {
        return output.is(input.getTriggerEntity());
    }

    @Override
    public boolean isValidInputForOutput(LiquidStarlightRecipeInput input, List<ItemEntity> otherValidInputs) {
        Holder<Lumen> lumenRef = input.getTriggerEntity().getItem().getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen();
        if (lumenRef.is(LumenAS.NONE.getKey())) return false;
        return !this.findStorageItemStack(otherValidInputs, lumenRef.value()).isEmpty();
    }

    @Override
    public void createOutput(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input) {
        ItemEntity inputEntity = input.getTriggerEntity();
        Level level = inputEntity.level();

        Holder<Lumen> lumenRef = inputEntity.getItem().getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen();
        if (lumenRef.is(LumenAS.NONE.getKey())) return;
        Lumen lumen = lumenRef.value();

        ItemStack storeOutput = this.findStorageItemStack(input.getOtherEntities(), lumen);
        if (storeOutput.isEmpty()) return;

        ItemStack output = storeOutput.copyWithCount(1);
        StoredLumenComponent cmp = output.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
        int storeMaxAmount = cmp.getStoredLumen(lumen).map(StoredLumenComponent.StoredLumen::maxAmount).orElse(cmp.properties().capacity());
        int storedAmount = cmp.getStoredLumen(lumen).map(StoredLumenComponent.StoredLumen::amount).orElse(0);
        cmp = cmp.updateLumenStack(lumen, Math.min(storedAmount + StoredLumenComponent.DEFAULT_CAPACITY, storeMaxAmount), storeMaxAmount);
        output.set(DataComponentsAS.STORED_LUMEN, cmp);
        IdentifierComponent.createIdentifierIfNotExists(output);

        ItemUtil.dropItem(level, inputEntity.getX(), inputEntity.getY(), inputEntity.getZ(), output);
        recipe.consumeItemInputs(input);
    }

    private ItemStack findStorageItemStack(List<ItemEntity> inputs, Lumen storeType) {
        return inputs.stream()
                .map(ItemEntity::getItem)
                .filter(stack -> {
                    StoredLumenComponent cmp = stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
                    return cmp.canStoreFromExistingLumen(storeType);
                })
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playCraftingEffects(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input, RandomSource rand, int craftingTick) {
        super.playCraftingEffects(recipe, input, rand, craftingTick);

        ItemEntity inputEntity = input.getTriggerEntity();
        Holder<Lumen> lumenRef = inputEntity.getItem().getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen();
        if (lumenRef.is(LumenAS.NONE.getKey())) return;
        Lumen lumen = lumenRef.value();

        long tick = ClientProxy.getClientTick() / 60;
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
