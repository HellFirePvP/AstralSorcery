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
 * Class: PerkDefensiveDamageReduction
 * Created by HellFirePvP
 * Date: 04.12.2016 / 17:20
 */
public class PerkDefensiveDamageReduction extends ConstellationPerk {

    private static float dmgReductionMultiplier = 0.9F;

    public PerkDefensiveDamageReduction() {
        super("DEF_DMGREDUCTION", Target.ENTITY_HURT);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        addAlignmentCharge(hurt, 0.08 * Math.max(0 ,dmgIn));
        return dmgIn * dmgReductionMultiplier;
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        dmgReductionMultiplier = cfg.getFloat(getKey() + "DmgReduction", getConfigurationSection(), 0.9F, 0F, 1F, "Defines the multiplier for the damage reduction when a player was hit");
    }

}
