/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.data;

import hellfirepvp.astralsorcery.common.lumen.binding.LumenBinding;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.util.RandomMobEffectInstance;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingTypeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingTypeBuilder {

    private final LumenBindingDataProvider provider;
    private final ResourceLocation id;
    private final Map<LumenBindingType.SlotType, LumenBinding> bindings = new HashMap<>();
    private RandomMobEffectInstance effect = null;

    public LumenBindingTypeBuilder(LumenBindingDataProvider provider, ResourceLocation id) {
        this.provider = provider;
        this.id = id;
    }

    public LumenBindingTypeBuilder put(LumenBindingType.SlotType type, LumenBinding binding) {
        this.bindings.put(type, binding);
        return this;
    }

    public LumenBindingTypeBuilder potionEffect(Holder<MobEffect> instance, IntRange duration, IntRange amplifier) {
        return this.potionEffect(RandomMobEffectInstance.of(instance, duration, amplifier));
    }

    public LumenBindingTypeBuilder potionEffect(Holder<MobEffect> instance, IntRange duration, IntRange amplifier, RandomMobEffectInstance hiddenEffect) {
        return this.potionEffect(RandomMobEffectInstance.of(instance, duration, amplifier, hiddenEffect));
    }

    public LumenBindingTypeBuilder potionEffect(RandomMobEffectInstance effect) {
        this.effect = effect;
        return this;
    }

    protected LumenBindingDataProvider getProvider() {
        return this.provider;
    }

    ResourceLocation getId() {
        return this.id;
    }

    protected LumenBindingType toBindingType() {
        return LumenBindingType.of(this.bindings, this.effect);
    }

    public LumenBindingDataProvider.BuiltBindingType build() {
        return this.getProvider().build(this.getId(), this.toBindingType());
    }
}
