/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.common.item.AstrolabeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinAbstractClientPlayer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer {

    @Inject(method = "getFieldOfViewModifier", at = @At(value = "RETURN"), cancellable = true)
    public void getSextantFovModifier(CallbackInfoReturnable<Float> cir) {
        AbstractClientPlayer thisPlayer = (AbstractClientPlayer) (Object) this;
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && AstrolabeItem.isUsingAstrolabe(thisPlayer)) {
            cir.setReturnValue(AstrolabeItem.FOV_MODIFIER);
        }
    }
}
