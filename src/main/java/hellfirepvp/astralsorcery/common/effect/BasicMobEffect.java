/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BasicMobEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BasicMobEffect extends MobEffect {

    public BasicMobEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.attachEventListeners(NeoForge.EVENT_BUS);
    }

    public BasicMobEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
        this.attachEventListeners(NeoForge.EVENT_BUS);
    }

    public void attachEventListeners(IEventBus bus) {}
}
