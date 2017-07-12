/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ISpellEffect
 * Created by HellFirePvP
 * Date: 07.07.2017 / 10:43
 */
public interface ISpellEffect {

    public IConstellation getParentConstellation();

    public int getSpellPlagueDuration(float multiplier);

    public void affect(EntityLivingBase entity, EffectType type);

    public void affectProjectile(SpellProjectile projectile);

    public void impact(SpellProjectile projectile, RayTraceResult hit);

    public static enum EffectType {

        ENTITY_SPELL_PLAGUE,
        ENTITY_BUFF,
        ENTITY_AOE

    }

}
