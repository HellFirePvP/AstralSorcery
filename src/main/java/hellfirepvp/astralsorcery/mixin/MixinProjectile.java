/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.ProjectileInaccuracyEvent;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinProjectile
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(Projectile.class)
public class MixinProjectile {

    @ModifyVariable(method = "getMovementToShoot", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    public float modifyInaccuracy(float inaccuracy) {
        Projectile thisProjectile = MiscUtil.cast(this);
        ProjectileInaccuracyEvent event = new ProjectileInaccuracyEvent(thisProjectile, inaccuracy);
        NeoForge.EVENT_BUS.post(event);
        return event.getInaccuracy();
    }
}
