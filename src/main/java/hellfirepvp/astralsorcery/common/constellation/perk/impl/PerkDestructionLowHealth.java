/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDestructionLowHealth
 * Created by HellFirePvP
 * Date: 29.07.2017 / 11:16
 */
public class PerkDestructionLowHealth extends ConstellationPerk {

    private static float digSpeedIncrease = 1.5F;
    private static float damageIncrease = 3F;

    public PerkDestructionLowHealth() {
        super("DTR_LOW_HEALTH", Target.ENTITY_ATTACK, Target.PLAYER_HARVEST_SPEED);
    }

    @Override
    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        float healthPerc = 1F - (attacker.getHealth() / attacker.getMaxHealth());
        addAlignmentCharge(attacker, 0.05 * healthPerc);
        return dmgIn * (1F + (healthPerc * damageIncrease));
    }

    @Override
    public float onHarvestSpeed(EntityPlayer harvester, IBlockState broken, @Nullable BlockPos at, float breakSpeedIn) {
        float healthPerc = 1F - (harvester.getHealth() / harvester.getMaxHealth());
        addAlignmentCharge(harvester, 0.02 * healthPerc);
        return breakSpeedIn * (1F + (healthPerc * digSpeedIncrease));
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        digSpeedIncrease = cfg.getFloat(getKey() + "DigSpeedIncrease", getConfigurationSection(), digSpeedIncrease, 1F, 32F, "Defines the dig speed multiplier you get additionally to your normal dig speed when being low on health (25% health = 75% of this additional multiplier)");
        damageIncrease = cfg.getFloat(getKey() + "DamageIncrease", getConfigurationSection(), damageIncrease, 1F, 32F, "Defines the damage multiplier you get additionally to your normal damage when being low on health (25% health = 75% of this additional multiplier)");
    }

}
