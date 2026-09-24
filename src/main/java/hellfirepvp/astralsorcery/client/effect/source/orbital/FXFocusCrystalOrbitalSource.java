/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source.orbital;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.source.FXOrbitalSource;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXFocusCrystalOrbitalSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FXFocusCrystalOrbitalSource extends FXOrbitalSource {

    private final FXColorFunction<?> primary, secondary;

    public FXFocusCrystalOrbitalSource(Vector3 pos, ColorWrapper color) {
        super(pos);
        this.primary = FXColorFunction.constant(color);
        this.secondary = FXColorFunction.constant(color.brighter());
    }

    @Override
    public void spawnOrbitalParticle(Vector3 pos) {
        if (this.rand.nextFloat() < 0.7F) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(this.primary)
                    .setScale(0.15F)
                    .setGravity(Vector3.y(0.001F))
                    .setMaxAge(15);
        }
        if (this.rand.nextInt(5) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(this.secondary)
                    .setScale(0.1F + this.rand.nextFloat() * 0.1F)
                    .setMotion(Vector3.random(rand).normalize().multiply(0.02F + this.rand.nextFloat() * 0.01F))
                    .setGravity(Vector3.y(0.001F))
                    .setMaxAge(25);
        }
    }
}
