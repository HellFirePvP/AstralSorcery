/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AccessorGameRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(GameRenderer.class)
public interface AccessorGameRenderer {

    @Accessor
    float getFov();

    @Accessor
    float getOldFov();

    @Invoker
    double callGetFov(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting);

}
