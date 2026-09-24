/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.lib.DamageTypesAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import hellfirepvp.astralsorcery.common.visual.type.CelestialStrikeEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialStrike
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialStrike {

    private static final double RADIUS = 16D;

    private CelestialStrike() {}

    public static void play(@Nullable LivingEntity attacker, ServerLevel sLevel, Vector3 at, Vector3 displayPosition) {
        play(attacker, sLevel, at, displayPosition, 1F, 0F);
    }

    public static void play(@Nullable LivingEntity attacker, ServerLevel sLevel, Vector3 at, Vector3 displayPosition, float damageMultiplier, float allyHealing) {
        AABB box = new AABB(at.toVector3d(), at.toVector3d()).inflate(RADIUS, RADIUS / 2, RADIUS);
        List<LivingEntity> targets = sLevel.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive);
        if (attacker != null) {
            targets.remove(attacker);
        }

        float dmg = (25F + DayTimeHelper.getCurrentDaytimeDistribution(sLevel) * 10F) * damageMultiplier;
        Holder<Enchantment> fireAspect = sLevel.holderOrThrow(Enchantments.FIRE_ASPECT);

        for (LivingEntity living : targets) {
            if (isProtectedTarget(living)) {
                continue;
            }
            float dstPerc = 1F - Mth.clamp((float) (Vector3.atCenter(living).distance(at) / RADIUS), 0F, 1F);

            if (isAlly(living, attacker)) {
                if (allyHealing > 0F && dstPerc > 0F) {
                    living.heal(allyHealing * dstPerc);
                    sLevel.sendParticles(ParticleTypes.HEART,
                            living.getX(), living.getY() + living.getBbHeight(), living.getZ(),
                            4, 0.3D, 0.3D, 0.3D, 0D);
                }
                continue;
            }

            float dmgDealt = dstPerc * dmg;
            if (dmgDealt <= 0.5F) {
                continue;
            }
            if (attacker == null) {
                DamageUtil.attackEntity(living, DamageTypesAS.STELLAR, dmgDealt);
            } else {
                DamageUtil.attackEntityFrom(living, DamageTypesAS.STELLAR, dmgDealt, attacker);

                int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(fireAspect, attacker);
                if (fireAspectLevel > 0 && !living.isOnFire()) {
                    living.setRemainingFireTicks(fireAspectLevel * 80);
                }
            }
        }

        CelestialStrikeEffect.at(displayPosition).sendToNearby(sLevel, at.toBlockPos());
    }

    public static boolean isValidTarget(LivingEntity living, @Nullable LivingEntity attacker) {
        return !isProtectedTarget(living) && !isAlly(living, attacker);
    }

    private static boolean isProtectedTarget(LivingEntity living) {
        if (living instanceof Player player && (player.isSpectator() || player.isCreative())) {
            return true;
        }
        return living instanceof AbstractVillager;
    }

    private static boolean isAlly(LivingEntity living, @Nullable LivingEntity attacker) {
        if (attacker == null) {
            return false;
        }
        if (living.isAlliedTo(attacker)) {
            return true;
        }
        return living instanceof TamableAnimal tamable && attacker.getUUID().equals(tamable.getOwnerUUID());
    }
}
