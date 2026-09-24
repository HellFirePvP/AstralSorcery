/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source.orbital;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXMotionFunction;
import hellfirepvp.astralsorcery.client.effect.source.FXOrbitalSource;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXItemAttunementOrbitalSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FXItemAttunementOrbitalSource extends FXOrbitalSource {

    private final Vector3 target;
    private final BaseConstellation constellation;

    public FXItemAttunementOrbitalSource(Vector3 pos, Vector3 target, BaseConstellation constellation) {
        super(pos);
        this.target = target;
        this.constellation = constellation;
    }

    @Override
    public void spawnOrbitalParticle(Vector3 pos) {
        Vector3 motion = this.getPos().subtract(pos).crossProduct(this.getOrbitAxis()).normalize().multiply(0.1 + rand.nextFloat() * 0.1);
        motion.add(this.getOrbitAxis().normalize().multiply(0.15 + rand.nextFloat() * 0.15));

        Vector3 vortexPos = VectorUtil.withRandomOffset(pos.copy(), rand, 0.4F);

        EntityVisualFX fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(vortexPos)
                .color(FXColorFunction.WHITE)
                .setScale(0.2F + rand.nextFloat() * 0.1F)
                .setAlpha(0.75F)
                .alpha(FXAlphaFunction.proximity(this.target::copy, 3F))
                .setMotion(motion)
                .motion(FXMotionFunction.target(this.target::copy, 0.075F))
                .setMaxAge(60);

        if (rand.nextInt(3) == 0) {
            fx.color(FXColorFunction.constant(this.constellation.getConstellationColor()));
        }
    }
}
