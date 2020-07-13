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
 * Class: FXOrbitalLucerna
 * Created by HellFirePvP
 * Date: 19.07.2019 / 09:53
 */
public class FXOrbitalLucerna extends FXSourceOrbital<FXFacingParticle, BatchRenderContext<FXFacingParticle>> {

    private static final VFXColorFunction<FXFacingParticle> lucernaColor =
            VFXColorFunction.constant(ColorsAS.RITUAL_CONSTELLATION_LUCERNA);

    private int count = 2 + rand.nextInt(2);

    public FXOrbitalLucerna(Vector3 pos) {
        super(pos, EffectTemplatesAS.GENERIC_PARTICLE);
        this.refresh(fx -> {
            count--;
            return count > 0;
        });
    }

    @Override
    public void tick() {
        super.tick();

        this.move(new Vector3(0, 0.05, 0));
    }

    @Override
    public void spawnOrbitalParticle(Vector3 pos, Function<Vector3, FXFacingParticle> effectRegistrar) {
        if (rand.nextInt(2) == 0) {
            effectRegistrar.apply(pos)
                    .color(lucernaColor)
                    .setScaleMultiplier(0.25F)
                    .addPosition(new Vector3(
                            (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1)
                    ))
                    .setMaxAge(45);
        }

        if (rand.nextInt(3) == 0) {
            effectRegistrar.apply(pos)
                    .color(VFXColorFunction.WHITE)
                    .setScaleMultiplier(0.25F)
                    .setMotion(new Vector3(
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                            (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1)
                    ))
                    .setMaxAge(35);
        }
    }

    @Override
    public void populateProperties(EffectProperties<FXFacingParticle> properties) {}
}
