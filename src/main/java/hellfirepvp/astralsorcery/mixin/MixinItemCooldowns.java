/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.ItemCooldownEvent;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ServerItemCooldowns;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinItemCooldowns
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(ItemCooldowns.class)
public class MixinItemCooldowns {

    @ModifyVariable(method = "addCooldown", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int adjustCooldown(int cooldownTicks) {
        ItemCooldowns cooldowns = MiscUtil.cast(this);
        if (cooldowns instanceof ServerItemCooldowns serverItemCooldowns) {
            ItemCooldownEvent event = new ItemCooldownEvent(serverItemCooldowns.player, cooldownTicks);
            NeoForge.EVENT_BUS.post(event);
            cooldownTicks = Math.max(event.getCooldown(), 1);
        }
        return cooldownTicks;
    }

}
