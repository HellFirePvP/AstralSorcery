/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinAttributeInstance
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(AttributeInstance.class)
public class MixinAttributeInstance {

    @Inject(method = "calculateValue", at = @At("RETURN"), cancellable = true)
    public void postProcessAttributeValue(CallbackInfoReturnable<Double> cir) {
        AttributeInstance thisInstance = MiscUtil.cast(this);
        cir.setReturnValue(AttributeEvent.postProcessVanilla(cir.getReturnValue(), thisInstance));
    }
}
