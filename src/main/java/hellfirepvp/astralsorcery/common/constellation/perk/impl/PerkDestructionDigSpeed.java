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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDestructionDigSpeed
 * Created by HellFirePvP
 * Date: 27.07.2017 / 18:39
 */
public class PerkDestructionDigSpeed extends ConstellationPerk {

    private static float harvestSpeedIncrease = 0.5F;

    public PerkDestructionDigSpeed() {
        super("DTR_BREAKSPEED", Target.PLAYER_HARVEST_SPEED);
    }

    @Override
    public float onHarvestSpeed(EntityPlayer harvester, IBlockState broken, @Nullable BlockPos at, float breakSpeedIn) {
        if(!harvester.getEntityWorld().isRemote) {
            addAlignmentCharge(harvester, 0.01);
        }
        return breakSpeedIn * (1.0F + harvestSpeedIncrease);
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        harvestSpeedIncrease = cfg.getFloat(getKey() + "HarvestSpeedIncrease", getConfigurationSection(), harvestSpeedIncrease, 0F, 32F, "Sets the speed how much the break-speed is increased by (percentage, multiplicative).");
    }

}
