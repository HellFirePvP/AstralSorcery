/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.ColorExtractUtil;
import hellfirepvp.astralsorcery.common.lib.types.LiquidStarlightRecipeOutputTypesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
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
 * Class: LiquidStarlightOutputDropItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightOutputDropItem extends LiquidStarlightRecipeOutputModifier {

    public static final MapCodec<LiquidStarlightOutputDropItem> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemStack.CODEC.fieldOf("result").forGetter(LiquidStarlightOutputDropItem::getResult)
    ).apply(inst, LiquidStarlightOutputDropItem::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightOutputDropItem> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            LiquidStarlightOutputDropItem::getResult,
            LiquidStarlightOutputDropItem::new);
    public static final Type<LiquidStarlightOutputDropItem> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final ItemStack result;

    private LiquidStarlightOutputDropItem(ItemStack result) {
        this.result = result;
    }

    public static LiquidStarlightOutputDropItem create(ItemStack result) {
        return new LiquidStarlightOutputDropItem(result);
    }

    public ItemStack getResult() {
        return this.result.copy();
    }

    @Override
    public Type<?> getType() {
        return LiquidStarlightRecipeOutputTypesAS.DROP_ITEM.get();
    }

    @Override
    public boolean isOutput(LiquidStarlightRecipeInput input, ItemEntity output) {
        return output.getItem().is(this.getResult().getItem());
    }

    @Override
    public void createOutput(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input) {
        ItemEntity trigger = input.getTriggerEntity();
        ItemUtil.dropItemNaturally(trigger.level(), trigger.getX(), trigger.getY(), trigger.getZ(), this.getResult());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playCraftingEffects(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input, RandomSource rand, int craftingTick) {
        super.playCraftingEffects(recipe, input, rand, craftingTick);

        ColorWrapper color = ColorExtractUtil.getColor(this.getResult()).orElse(ColorWrapper.WHITE);
        for (int i = 0; i < 5; i++) {
            Vector3 pos = Vector3.atCenter(input.getTriggerEntity().blockPosition());
            pos = VectorUtil.withRandomOffset(pos, rand, 0.5F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(FXColorFunction.constant(color))
                    .setScale(0.15F + rand.nextFloat() * 0.2F)
                    .setAlpha(0.8F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setMotion(Vector3.y(0.15F))
                    .setGravity(Vector3.y(-0.005F + rand.nextFloat() * -0.004F));
        }
    }
}
