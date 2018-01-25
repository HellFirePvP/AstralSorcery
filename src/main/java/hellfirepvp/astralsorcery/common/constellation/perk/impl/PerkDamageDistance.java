/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDamageDistance
 * Created by HellFirePvP
 * Date: 02.12.2016 / 21:55
 */
public class PerkDamageDistance extends ConstellationPerk {

    private static float maxMultiplier = 4F;
    private static double maxRelevantSqDistance = 64 * 64;

    public PerkDamageDistance() {
        super("DMG_DST", Target.ENTITY_ATTACK);
    }

    @Override
    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        double dst = attacker.getDistanceSqToEntity(attacked);
        float perc = (float) (dst / maxRelevantSqDistance);
        addAlignmentCharge(attacker, perc * 2 * Math.max(0, dmgIn));
        return dmgIn * (Math.max(1, 1 + (maxMultiplier * (perc > 1 ? 1 : perc))));
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        maxMultiplier = cfg.getFloat(getKey() + "MaxMultiplier", getConfigurationSection(), maxMultiplier, 1F, 60F, "Defines the multiplier how much the player can get additionally at max. distance defined.");
        double dst = cfg.getFloat(getKey() + "MaxDistance", getConfigurationSection(), 64F, 16F, 2048F, "Defines the max. distance that is relevant. If you hit something further away it won't grant more additional bonus than defined in MaxMultiplier");
        maxRelevantSqDistance = dst * dst;
    }
}
