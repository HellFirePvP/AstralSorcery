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
 * Class: PerkDefensiveDamageDodge
 * Created by HellFirePvP
 * Date: 04.12.2016 / 17:24
 */
public class PerkDefensiveDamageDodge extends ConstellationPerk {

    private static int chanceDodgeDamage = 20;

    public PerkDefensiveDamageDodge() {
        super("DEF_DODGE", Target.ENTITY_HURT);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        if(source.canHarmInCreative()) return dmgIn;

        if(rand.nextInt(chanceDodgeDamage) == 0) {
            addAlignmentCharge(hurt, 1 * Math.max(0 ,dmgIn));
            return 0F;
        }
        return dmgIn;
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        chanceDodgeDamage = cfg.getInt(getKey() + "DodgeChance", getConfigurationSection(), 20, 1, 1000, "Sets the chance the player has to completely avoid/dodge an attack");
    }

}
