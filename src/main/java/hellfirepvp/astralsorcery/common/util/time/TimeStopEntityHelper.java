/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.time;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeStopEntityHelper
 * Created by HellFirePvP
 * Date: 09.10.2026 / 13:48
 */
public class TimeStopEntityHelper {

    public static boolean isFrozenDirectly(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return SyncDataManager.getInstance().getClientData(SyncDataTypesAS.TIME_STOP_ENTITIES).isFrozen(entity);
        } else {
            return SyncDataManager.getInstance().getData(SyncDataTypesAS.TIME_STOP_ENTITIES).isFrozen(entity);
        }
    }

    public static boolean skipAndRunLivingTick(LivingEntity entity) {
        if (isFrozenDirectly(entity)) {
            boolean shouldFreeze = entity.isAlive() && entity.getHealth() > 0;
            if (entity instanceof EnderDragon dragon && dragon.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
                shouldFreeze = false;
            }

            if (shouldFreeze) {
                if (entity.level().isClientSide()) {
                    TimeStopEffectHelper.playEntityTickParticles(entity, entity.getRandom());
                } else {
                    runImportantEntityTicks(entity);
                    return true;
                }
            }
        }
        return false;
    }

    public static void runImportantEntityTicks(LivingEntity entity) {
        if (entity.hurtTime > 0) {
            entity.hurtTime--;
        }
        if (entity.invulnerableTime > 0) {
            entity.invulnerableTime--;
        }
        entity.xOld = entity.getX();
        entity.yOld = entity.getY();
        entity.zOld = entity.getZ();
        entity.walkAnimation.update(entity.walkAnimation.speed(), 0F);
        entity.yBodyRotO = entity.yBodyRot;
        entity.xRotO = entity.getXRot();
        entity.yRotO = entity.getYRot();
        entity.yHeadRotO = entity.yHeadRot;
        entity.oAttackAnim = entity.attackAnim;
        entity.walkDistO = entity.walkDist;

        if (!entity.level().isClientSide()) {
            entity.travel(Vec3.ZERO);
        }

        if (entity instanceof EnderDragon dragon) {
            DragonPhaseInstance phase = dragon.getPhaseManager().getCurrentPhase();
            if (phase.getPhase() != EnderDragonPhase.HOLDING_PATTERN && phase.getPhase() != EnderDragonPhase.DYING) {
                dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            }
        }
    }
}
