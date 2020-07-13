/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source.orbital;

import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXMotionController;
import hellfirepvp.astralsorcery.client.effect.source.FXSourceOrbital;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXOrbitalCrystalAttunement
 * Created by HellFirePvP
 * Date: 12.01.2020 / 17:41
 */
public class FXOrbitalCrystalAttunement extends FXSourceOrbital<FXFacingParticle, BatchRenderContext<FXFacingParticle>> {

    private Vector3 targetPoint;
    private IConstellation cst;

    public FXOrbitalCrystalAttunement(Vector3 pos, Vector3 floatTarget, IConstellation constellation) {
        super(pos, EffectTemplatesAS.GENERIC_PARTICLE);
        this.targetPoint = floatTarget.clone();
        this.cst = constellation;
    }

    @Override
    public void spawnOrbitalParticle(Vector3 pos, Function<Vector3, FXFacingParticle> effectRegistrar) {
        Vector3 motion = this.getPosition().subtract(pos).crossProduct(this.getOrbitAxis()).normalize().multiply(0.1 + rand.nextFloat() * 0.1);
        motion.add(this.getOrbitAxis().normalize().multiply(0.15 + rand.nextFloat() * 0.15));

        Vector3 vortexPos = pos.clone();
        MiscUtils.applyRandomOffset(vortexPos, rand, 0.4F);

        FXFacingParticle p = effectRegistrar.apply(vortexPos)
                .color(VFXColorFunction.WHITE)
                .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                .setAlphaMultiplier(0.75F)
                .alpha(VFXAlphaFunction.proximity(this.targetPoint::clone, 3F))
                .motion(VFXMotionController.target(this.targetPoint::clone, 0.075F))
                .setMotion(motion)
                .setMaxAge(60);

        if (rand.nextInt(3) == 0) {
            p.color(VFXColorFunction.constant(cst.getConstellationColor()));
        }
    }

    @Override
    public void populateProperties(EffectProperties<FXFacingParticle> properties) {}
}
