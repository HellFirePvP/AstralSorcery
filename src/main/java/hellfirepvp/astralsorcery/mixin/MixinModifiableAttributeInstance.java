/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinModifiableAttributeInstance
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ModifiableAttributeInstance.class)
public class MixinModifiableAttributeInstance {

    @Inject(method = "computeValue", at = @At("RETURN"), cancellable = true)
    public void postProcessAtrributeValue(CallbackInfoReturnable<Double> cir) {
        ModifiableAttributeInstance attributeInstance = (ModifiableAttributeInstance)(Object) this;
        cir.setReturnValue(AttributeEvent.postProcessVanilla(cir.getReturnValue(), attributeInstance));
    }

}
