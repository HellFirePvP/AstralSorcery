/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source.orbital;

import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.source.FXSourceOrbital;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXOrbitalPelotrio
 * Created by HellFirePvP
 * Date: 31.05.2020 / 12:36
 */
public class FXOrbitalPelotrio extends FXSourceOrbital<FXFacingParticle, BatchRenderContext<FXFacingParticle>> {

    private static final VFXColorFunction<FXFacingParticle> pelotrioColor =
            VFXColorFunction.constant(ColorsAS.CONSTELLATION_PELOTRIO);

    public FXOrbitalPelotrio(Vector3 pos) {
        super(pos, EffectTemplatesAS.GENERIC_PARTICLE);
    }

    @Override
    public void spawnOrbitalParticle(Vector3 pos, Function<Vector3, FXFacingParticle> effectRegistrar) {
        if (rand.nextInt(4) == 0) {
            effectRegistrar.apply(pos)
                    .color(pelotrioColor)
                    .setScaleMultiplier(0.3F + rand.nextFloat() * 0.2F)
                    .setMotion(new Vector3(
                            (rand.nextFloat() * 0.02F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.02F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.02F) * (rand.nextBoolean() ? 1 : -1)
                    ))
                    .setMaxAge(45);
        }

        if (rand.nextInt(4) == 0) {
            effectRegistrar.apply(pos)
                    .color(VFXColorFunction.WHITE)
                    .setScaleMultiplier(0.3F + rand.nextFloat() * 0.2F)
                    .setMotion(new Vector3(
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1)
                    ))
                    .setMaxAge(30);
        }
    }

    @Override
    public void populateProperties(EffectProperties<FXFacingParticle> properties) {}
}
