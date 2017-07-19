/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell.controller;

import hellfirepvp.astralsorcery.common.constellation.spell.SpellControllerEffect;
import net.minecraft.entity.EntityLivingBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectControllerDiscidia
 * Created by HellFirePvP
 * Date: 15.07.2017 / 15:17
 */
public class EffectControllerDiscidia extends SpellControllerEffect {

    public EffectControllerDiscidia(EntityLivingBase caster) {
        super(caster);
    }

    @Override
    public void startCasting() {
        int projectiles = rand.nextInt(2) + 3;
        for (int i = 0; i < projectiles; i++) {
            newProjectile(80F);
        }
    }

    @Override
    public void updateController() {

    }

}
