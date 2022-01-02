/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.CooldownSetEvent;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.ServerCooldownTracker;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinCooldownTracker
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(CooldownTracker.class)
public class MixinCooldownTracker {

    @ModifyVariable(method = "setCooldown", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int fireCooldownEvent(int cooldownTicks) {
        CooldownTracker tracker = (CooldownTracker)(Object) this;
        if (tracker instanceof ServerCooldownTracker) {
            CooldownSetEvent event = new CooldownSetEvent(((ServerCooldownTracker) tracker).player, cooldownTicks);
            MinecraftForge.EVENT_BUS.post(event);
            cooldownTicks = Math.max(event.getResultCooldown(), 1);
        }
        return cooldownTicks;
    }

}
