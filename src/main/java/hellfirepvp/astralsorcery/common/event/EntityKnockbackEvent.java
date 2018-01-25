/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityKnockbackEvent
 * Created by HellFirePvP
 * Date: 02.12.2016 / 19:03
 */
public class EntityKnockbackEvent extends LivingEvent {

    private Entity attacker;
    private float strength;

    public EntityKnockbackEvent(EntityLivingBase knockbacked, @Nullable Entity attacker, float strength) {
        super(knockbacked);
        this.attacker = attacker;
        this.strength = strength;
    }

    @Nullable
    public Entity getAttacker() {
        return attacker;
    }

    public float getStrength() {
        return strength;
    }

}
