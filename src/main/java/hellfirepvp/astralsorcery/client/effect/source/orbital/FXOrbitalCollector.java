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
import hellfirepvp.astralsorcery.client.effect.source.FXSource;
import hellfirepvp.astralsorcery.client.effect.source.FXSourceOrbital;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.awt.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXOrbitalCollector
 * Created by HellFirePvP
 * Date: 19.07.2019 / 10:02
 */
public class FXOrbitalCollector extends FXSourceOrbital<FXFacingParticle, BatchRenderContext<FXFacingParticle>> {

    private final VFXColorFunction<FXFacingParticle> primaryColor, secondaryColor;

    public FXOrbitalCollector(Vector3 pos, Color color) {
        super(pos, EffectTemplatesAS.GENERIC_PARTICLE);
        this.primaryColor = VFXColorFunction.constant(color);
        this.secondaryColor = VFXColorFunction.constant(color.brighter());
    }

    @Override
    public void spawnOrbitalParticle(Vector3 pos, Function<Vector3, FXFacingParticle> effectRegistrar) {
        if (rand.nextInt(3) == 0) {
            effectRegistrar.apply(pos)
                    .color(primaryColor)
                    .setScaleMultiplier(0.15F)
                    .setMaxAge(15);
        }
        if (rand.nextInt(3) == 0) {
            effectRegistrar.apply(pos)
                    .color(secondaryColor)
                    .setMotion(new Vector3(
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1)
                    ))
                    .setScaleMultiplier(0.15F)
                    .setMaxAge(25);
        }
    }

    @Override
    public void populateProperties(EffectProperties<FXFacingParticle> properties) {}
}
