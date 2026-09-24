/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingEffectEffectiveness
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingEffectEffectiveness extends LumenBindingEffect {

    public static final MapCodec<LumenBindingEffectEffectiveness> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("effect_multiplier").forGetter(LumenBindingEffectEffectiveness::getEffectMultiplier)
    ).apply(inst, LumenBindingEffectEffectiveness::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingEffectEffectiveness> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            LumenBindingEffectEffectiveness::getEffectMultiplier,
            LumenBindingEffectEffectiveness::new);

    private final float effectMultiplier;

    private LumenBindingEffectEffectiveness(float effectMultiplier) {
        this.effectMultiplier = effectMultiplier;
    }

    public static LumenBindingEffectEffectiveness of(float effectMultiplier) {
        return new LumenBindingEffectEffectiveness(effectMultiplier);
    }

    public float getEffectMultiplier() {
        return effectMultiplier;
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        int flatRoundedChance = Math.round(this.getEffectMultiplier() * 100F);
        return List.of(Component.translatable("lumen.binding.astralsorcery.increased_effect", flatRoundedChance));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.EFFECTIVENESS;
    }
}
