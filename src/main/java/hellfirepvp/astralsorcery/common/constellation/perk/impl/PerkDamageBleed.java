/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDamageBleed
 * Created by HellFirePvP
 * Date: 04.12.2016 / 17:49
 */
public class PerkDamageBleed extends ConstellationPerk {

    private static int amplifierApplied = 1;
    private static int durationApplied = 80;

    public PerkDamageBleed() {
        super("DMG_BLEED", Target.ENTITY_ATTACK);
    }

    @Override
    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        if (!attacker.equals(attacked)) {
            attacked.addPotionEffect(new PotionEffect(RegistryPotions.potionBleed, durationApplied, amplifierApplied, false, true));
            addAlignmentCharge(attacker, 0.1 * Math.max(0, dmgIn));
        }
        return dmgIn;
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        amplifierApplied = cfg.getInt(getKey() + "BleedAmplifier", getConfigurationSection(), 1, 0, 40, "Defines the PotionEffect amplifier of the bleed applied.");
        durationApplied = cfg.getInt(getKey() + "BleedTickDuration", getConfigurationSection(), 80, 1, 600, "Defines the PotionEffect duration of the bleed applied");
    }

}
