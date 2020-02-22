/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.goal;

import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpectralToolGoal
 * Created by HellFirePvP
 * Date: 22.02.2020 / 15:43
 */
public abstract class SpectralToolGoal extends Goal {

    private final EntitySpectralTool entity;
    private final double speed;

    protected int actionCooldown = 0;

    public SpectralToolGoal(EntitySpectralTool entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.LOOK));
    }

    public EntitySpectralTool getEntity() {
        return entity;
    }

    public double getSpeed() {
        return speed;
    }
}
