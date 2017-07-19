/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell.controller;

import hellfirepvp.astralsorcery.common.constellation.spell.ISpellEffect;
import hellfirepvp.astralsorcery.common.constellation.spell.SpellControllerEffect;
import net.minecraft.entity.EntityLivingBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectControllerAevitas
 * Created by HellFirePvP
 * Date: 15.07.2017 / 14:34
 */
public class EffectControllerAevitas extends SpellControllerEffect {

    public EffectControllerAevitas(EntityLivingBase caster) {
        super(caster);
    }

    @Override
    public void startCasting() {

    }

    @Override
    public void updateController() {
        for (ISpellEffect effect : this)  {
            effect.affect(this.caster, ISpellEffect.EffectType.ENTITY_BUFF);
        }
    }

}
