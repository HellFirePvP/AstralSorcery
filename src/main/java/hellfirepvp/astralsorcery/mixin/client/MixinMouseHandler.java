/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.client.helper.RenderAstrolabeOverlay;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinMouseHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Unique
    private static boolean astralSorcery_1_21$expectMouseGrab = false;

    @Inject(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;grabMouse()V"))
    public void expectNonScreenMouseGrab(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        if (RenderAstrolabeOverlay.isDrawing()) {
            astralSorcery_1_21$expectMouseGrab = true;
        }
    }

    @Inject(method = "grabMouse", at = @At("HEAD"), cancellable = true)
    public void preventMouseGrab(CallbackInfo ci) {
        if (astralSorcery_1_21$expectMouseGrab) { //It's okay to reset, we only want to suppress a method call.
            astralSorcery_1_21$expectMouseGrab = false;
            ci.cancel();
        }
    }
}
