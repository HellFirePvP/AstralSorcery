/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.DamageTypeRegistryObject;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DamageUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DamageUtil {

    public static boolean attackEntity(Entity entity, DamageTypeRegistryObject src, float amount) {
        return attackEntity(entity, src.source(entity.damageSources()), amount);
    }

    public static boolean attackEntity(Entity entity, DamageSource src, float amount) {
        return entity.hurt(src, amount);
    }

    public static boolean attackEntityFrom(Entity entity, DamageTypeRegistryObject src, float amount, Entity newSource) {
        return attackEntityFrom(entity, src.source(entity.damageSources()), amount, newSource);
    }

    public static boolean attackEntityFrom(Entity entity, DamageSource src, float amount, Entity newSource) {
        DamageSource ovr = new DamageSource(src.typeHolder(), newSource, newSource, src.sourcePositionRaw());
        return attackEntity(entity, ovr, amount);
    }

    public static <T extends Entity> void shotgunAttack(T targeted, Consumer<T> fn) {
        int hurtTime = targeted.invulnerableTime;
        targeted.invulnerableTime = 0;
        try {
            fn.accept(targeted);
        } finally {
            targeted.invulnerableTime = hurtTime;
        }
    }
}
