/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lumen.binding.effect.LumenBindingEffect;
import hellfirepvp.astralsorcery.common.lumen.binding.usage.LumenBindingUsage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBinding
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBinding {

    public static final Codec<LumenBinding> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            LumenBindingUsage.CODEC.fieldOf("lumen_usage").forGetter(LumenBinding::getLumenUsage),
            ComponentSerialization.CODEC.listOf().fieldOf("display_text").forGetter(LumenBinding::getDisplayText),
            LumenBindingEffect.CODEC.fieldOf("effect").forGetter(LumenBinding::getEffect)
    ).apply(inst, LumenBinding::new));

    private final LumenBindingUsage lumenUsage;
    private final List<Component> displayText;
    private final LumenBindingEffect effect;

    private LumenBinding(LumenBindingUsage lumenUsage, List<Component> displayText, LumenBindingEffect effect) {
        this.lumenUsage = lumenUsage;
        this.displayText = displayText;
        this.effect = effect;
    }

    public static LumenBinding of(LumenBindingUsage lumenUsage, List<Component> displayText, LumenBindingEffect effect) {
        return new LumenBinding(lumenUsage, displayText, effect);
    }

    public LumenBindingUsage getLumenUsage() {
        return this.lumenUsage;
    }

    public List<Component> getDisplayText() {
        return this.displayText.stream().map(Component::copy).collect(Collectors.toUnmodifiableList());
    }

    public LumenBindingEffect getEffect() {
        return this.effect;
    }
}
