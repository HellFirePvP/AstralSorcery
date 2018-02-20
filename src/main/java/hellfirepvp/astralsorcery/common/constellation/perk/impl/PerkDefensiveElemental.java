/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDefensiveElemental
 * Created by HellFirePvP
 * Date: 04.12.2016 / 18:09
 */
public class PerkDefensiveElemental extends ConstellationPerk {

    private static float multiplierElementalReduction = 0.7F;

    public PerkDefensiveElemental() {
        super("DEF_ELEMENTAL", Target.ENTITY_HURT);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        if(source.isFireDamage() || source.isExplosion() || source.isMagicDamage()) {
            addAlignmentCharge(hurt, 0.2 * Math.max(0 ,dmgIn));
            dmgIn *= multiplierElementalReduction;
        }
        return dmgIn;
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        multiplierElementalReduction = cfg.getFloat(getKey() + "ReductionMultipler", getConfigurationSection(), 0.7F, 0.1F, 1F, "Sets the damage-reduction multiplier when the player is hit by fire, magic or explosion.");
    }

}
