/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function.impl;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.VFXRenderOffsetFunction;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderOffsetFornax
 * Created by HellFirePvP
 * Date: 19.07.2019 / 10:25
 */
public class RenderOffsetFornax implements VFXRenderOffsetFunction<EntityVisualFX> {

    @Nonnull
    @Override
    public Vector3 changeRenderPosition(@Nonnull EntityVisualFX fx, Vector3 interpolatedPos, float pTicks) {
        Vector3 currentMotion = fx.getMotion();
        Vector3 perp = currentMotion.clone().perpendicular().normalize().multiply(0.05);
        Random r = new Random(fx.getId()); //LUL tho...

        int interv = (int) ((r.nextInt() + ClientScheduler.getClientTick()) % 9);
        float part = interv + pTicks;
        float perc = part / 10F;

        float sinPart = MathHelper.sin(perc * ((float) Math.PI) * 2F);
        return interpolatedPos.add(perp.rotate(r.nextFloat() * 360F, currentMotion).multiply(sinPart));
    }
}
