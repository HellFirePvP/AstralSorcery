/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ImmediateVFX
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
//VFX that can't be batched for one reason or another
public interface ImmediateVFX {

    <T extends EntityVisualFX & ImmediateVFX> void renderImmediate(EffectTemplate<T> template, Camera camera, MultiBufferSource.BufferSource drawBuffer, float pTicks);

}
