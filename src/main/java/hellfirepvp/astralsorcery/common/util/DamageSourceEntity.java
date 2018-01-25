/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DamageSourceEntity
 * Created by HellFirePvP
 * Date: 28.10.2017 / 22:27
 */
public class DamageSourceEntity extends DamageSource {

    private EntityLivingBase source = null;

    public DamageSourceEntity(String damageTypeIn) {
        super(damageTypeIn);
    }

    public DamageSourceEntity setSource(EntityLivingBase source) {
        DamageSourceEntity newInstance = new DamageSourceEntity(this.damageType);
        if(newInstance.canHarmInCreative()) {
            newInstance.setDamageAllowedInCreativeMode();
        }
        if(newInstance.isDamageAbsolute()) {
            newInstance.setDamageIsAbsolute();
        }
        if(newInstance.isProjectile()) {
            newInstance.setProjectile();
        }
        if(newInstance.isExplosion()) {
            newInstance.setExplosion();
        }
        if(newInstance.isFireDamage()) {
            newInstance.setFireDamage();
        }
        if(newInstance.isMagicDamage()) {
            newInstance.setMagicDamage();
        }
        if(newInstance.isDifficultyScaled()) {
            newInstance.setDifficultyScaled();
        }
        newInstance.source = source;
        return newInstance;
    }

    @Nullable
    @Override
    public Entity getTrueSource() {
        return source;
    }

}
