/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.world.effect.MobEffectInstance;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MobEffectUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MobEffectUtil {

    public static MobEffectInstance newAmplifier(MobEffectInstance inst, int amplifier) {
        MobEffectInstance newInst = new MobEffectInstance(inst);
        newInst.amplifier = amplifier;
        return newInst;
    }

    public static MobEffectInstance newDuration(MobEffectInstance inst, int duration) {
        MobEffectInstance newInst = new MobEffectInstance(inst);
        newInst.duration = duration;
        return newInst;
    }
}
