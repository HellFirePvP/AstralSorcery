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
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.event.CrystalPropertyEvent;
import hellfirepvp.astralsorcery.common.item.tool.CrystalToolItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.LiquidStarlightRecipeOutputTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightOutputEngraveLumen
 * Created by HellFirePvP
 * Date: 19.10.2026 / 13:43
 */
public class LiquidStarlightOutputEngraveLumen extends LiquidStarlightRecipeOutputModifier {

    public static final MapCodec<LiquidStarlightOutputEngraveLumen> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            WeightedEntry.Wrapper.codec(RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES.byNameCodec()).listOf()
                    .fieldOf("properties").forGetter(LiquidStarlightOutputEngraveLumen::getProperties)
    ).apply(inst, LiquidStarlightOutputEngraveLumen::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightOutputEngraveLumen> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.weightedEntryCodec(ByteBufCodecs.registry(RegistriesAS.KEY_CRYSTAL_PROPERTIES)).apply(ByteBufCodecs.list()), LiquidStarlightOutputEngraveLumen::getProperties,
            LiquidStarlightOutputEngraveLumen::new
    );
    public static final Type<LiquidStarlightOutputEngraveLumen> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final List<WeightedEntry.Wrapper<CrystalProperty>> properties;

    public LiquidStarlightOutputEngraveLumen(List<WeightedEntry.Wrapper<CrystalProperty>> properties) {
        this.properties = List.copyOf(properties);
    }

    public static LiquidStarlightOutputEngraveLumen of(WeightedEntry.Wrapper<CrystalProperty>... properties) {
        return new LiquidStarlightOutputEngraveLumen(Arrays.asList(properties));
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<WeightedEntry.Wrapper<CrystalProperty>> getProperties() {
        return this.properties;
    }

    @Override
    public Type<?> getType() {
        return LiquidStarlightRecipeOutputTypesAS.ENGRAVE_LUMEN.get();
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

        RandomSource rand = RandomSource.create();
        CrystalProperty prop = WeightedRandom.getRandomItem(rand, this.properties).map(WeightedEntry.Wrapper::data).orElse(null);
        if (prop != null) {
            int tier = cmp.getAttributeTier(prop);
            if (tier < prop.getMaxTier()) {
                CrystalAttributesComponent prev = cmp;
                cmp = cmp.setAttributeTier(prop, tier + 1);

                CrystalPropertyEvent.Change engraved = new CrystalPropertyEvent.Change(crystal,
                        CrystalPropertyEvent.Change.Type.ENGRAVE_PROPERTY, prev, cmp);
                NeoForge.EVENT_BUS.post(engraved);

                crystal.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, engraved.getResultComponent());
                CrystalToolItem.updateModifiers(crystal);
            }
        }

        ItemEntity trigger = input.getTriggerEntity();
        ItemUtil.dropItem(trigger.level(), trigger.getX(), trigger.getY(), trigger.getZ(), crystal);
        recipe.consumeItemInputs(input);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playCraftingEffects(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input, RandomSource rand, int craftingTick) {
        super.playCraftingEffects(recipe, input, rand, craftingTick);

        Holder<Lumen> lumenRef = LumenAS.NONE;
        for (ItemEntity other : input.getOtherEntities()) {
            Holder<Lumen> otherRef = other.getItem().getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen();
            if (!otherRef.is(LumenAS.NONE.getKey())) {
                lumenRef = otherRef;
                break;
            }
        }
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

    public static class Builder {

        private final List<WeightedEntry.Wrapper<CrystalProperty>> properties = new ArrayList<>();

        private Builder() {}

        public Builder add(Supplier<? extends CrystalProperty> property, int weight) {
            return this.add(property.get(), Weight.of(weight));
        }

        public Builder add(CrystalProperty property, Weight weight) {
            this.properties.add(new WeightedEntry.Wrapper<>(property, weight));
            return this;
        }

        public LiquidStarlightOutputEngraveLumen build() {
            return new LiquidStarlightOutputEngraveLumen(this.properties);
        }
    }
}
