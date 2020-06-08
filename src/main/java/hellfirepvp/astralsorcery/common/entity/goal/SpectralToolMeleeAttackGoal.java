/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.goal;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectPelotrio;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpectralToolMeleeAttackGoal
 * Created by HellFirePvP
 * Date: 22.02.2020 / 16:58
 */
public class SpectralToolMeleeAttackGoal extends SpectralToolGoal {

    private LivingEntity selectedTarget = null;

    public SpectralToolMeleeAttackGoal(EntitySpectralTool entity, double speed) {
        super(entity, speed);
    }

    private LivingEntity findClosestAttackableEntity() {
        List<LivingEntity> entities = this.getEntity().getEntityWorld().getEntitiesWithinAABB(
                LivingEntity.class,
                new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(8).offset(this.getEntity().getPosition()),
                e -> e != null && e.isAlive() && e.getType().getClassification() == EntityClassification.MONSTER
        );
        return EntityUtils.selectClosest(entities, entity -> (double) entity.getDistance(this.getEntity()));
    }


    @Override
    public boolean shouldExecute() {
        MovementController ctrl = this.getEntity().getMoveHelper();

        if (!ctrl.isUpdating()) {
            return true;
        } else {
            return this.findClosestAttackableEntity() != null;
        }
    }

    public boolean shouldContinueExecuting() {
        return selectedTarget != null;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();

        LivingEntity target = this.findClosestAttackableEntity();
        if (target != null) {
            this.selectedTarget = target;
            this.getEntity().getMoveHelper().setMoveTo(selectedTarget.getPosX(), selectedTarget.getPosY() + selectedTarget.getHeight() / 2, selectedTarget.getPosZ(), this.getSpeed());
        }
    }

    @Override
    public void resetTask() {
        super.resetTask();

        this.selectedTarget = null;
        this.actionCooldown = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (!shouldContinueExecuting()) {
            return;
        }

        if (this.actionCooldown < 0) {
            this.actionCooldown = 0; //lol. wtf.
        }

        boolean resetTimer = false;

        if (!this.selectedTarget.isAlive()) {
            this.selectedTarget = null;
            resetTimer = true;
        } else {
            this.getEntity().getMoveHelper().setMoveTo(selectedTarget.getPosX(), selectedTarget.getPosY() + selectedTarget.getHeight() / 2, selectedTarget.getPosZ(), this.getSpeed());

            if (Vector3.atEntityCorner(this.getEntity()).distance(this.selectedTarget) <= 4) {
                this.actionCooldown++;
                if (this.actionCooldown >= MantleEffectPelotrio.CONFIG.ticksPerSwordAttack.get()) {
                    DamageUtil.attackEntityFrom(this.selectedTarget, CommonProxy.DAMAGE_SOURCE_STELLAR, MantleEffectPelotrio.CONFIG.swordDamage.get().floatValue());
                    resetTimer = true;
                }
            }
        }

        if (resetTimer) {
            this.actionCooldown = 0;
        }
    }
}
