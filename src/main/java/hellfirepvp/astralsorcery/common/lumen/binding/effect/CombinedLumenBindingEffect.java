/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CombinedLumenBindingEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CombinedLumenBindingEffect extends LumenBindingEffect {

    public static final MapCodec<CombinedLumenBindingEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            LumenBindingEffect.CODEC.listOf().fieldOf("effects").forGetter(CombinedLumenBindingEffect::getEffects)
    ).apply(inst, CombinedLumenBindingEffect::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CombinedLumenBindingEffect> STREAM_CODEC = StreamCodec.composite(
            LumenBindingEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),
            CombinedLumenBindingEffect::getEffects,
            CombinedLumenBindingEffect::new);

    private final List<LumenBindingEffect> effects = new ArrayList<>();

    private CombinedLumenBindingEffect(List<LumenBindingEffect> effects) {
        this.effects.addAll(effects);
    }

    public List<LumenBindingEffect> getEffects() {
        return Collections.unmodifiableList(this.effects);
    }

    public static CombinedLumenBindingEffect of(LumenBindingEffect... effects) {
        return new CombinedLumenBindingEffect(List.of(effects));
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        List<Component> display = new ArrayList<>();
        this.effects.forEach(effect -> display.addAll(effect.getDisplayText(side, stack)));
        return display;
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.COMBINED;
    }
}
