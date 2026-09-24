/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.altar.effect.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarEffectsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarEffectsAS {

    public static final DeferredRegister<AltarEffect> ALTAR_EFFECT_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_ALTAR_EFFECTS, AstralSorcery.MODID);

    public static final DeferredHolder<AltarEffect, DefaultAltarEffectCentralBeam> DEFAULT_CENTRAL_BEAM =
            ALTAR_EFFECT_REGISTER.register("default_central_beam", DefaultAltarEffectCentralBeam::new);
    public static final DeferredHolder<AltarEffect, DefaultAltarEffectAltarSparkle> DEFAULT_ALTAR_SPARKLE =
            ALTAR_EFFECT_REGISTER.register("default_altar_sparkle", DefaultAltarEffectAltarSparkle::new);
    public static final DeferredHolder<AltarEffect, DefaultAltarEffectRelayInput> DEFAULT_RELAY_INPUT =
            ALTAR_EFFECT_REGISTER.register("default_relay_input", DefaultAltarEffectRelayInput::new);
    public static final DeferredHolder<AltarEffect, DefaultAltarEffectLumenInput> DEFAULT_LUMEN_INPUT =
            ALTAR_EFFECT_REGISTER.register("default_lumen_input", DefaultAltarEffectLumenInput::new);

}
