/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.effect.*;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraftforge.common.MinecraftForge;

import static hellfirepvp.astralsorcery.common.lib.EffectsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEffects
 * Created by HellFirePvP
 * Date: 26.08.2019 / 19:12
 */
public class RegistryEffects {

    private RegistryEffects() {}

    public static void init() {
        EFFECT_BLEED = register(new EffectBleed());
        EFFECT_CHEAT_DEATH = register(new EffectCheatDeath());
        EFFECT_DROP_MODIFIER = register(new EffectDropModifier());
        EFFECT_TIME_FREEZE = register(new EffectTimeFreeze());
    }

    private static <T extends EffectCustomTexture> T register(T effect) {
        effect.setRegistryName(NameUtil.fromClass(effect, "Effect"));
        effect.attachEventListeners(MinecraftForge.EVENT_BUS);
        AstralSorcery.getProxy().getRegistryPrimer().register(effect);
        return effect;
    }
}
