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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkWaterMovement
 * Created by HellFirePvP
 * Date: 04.12.2016 / 14:51
 */
public class PerkTravelWaterMovement extends ConstellationPerk {

    private static float swimSpeedMultiplier = 1.2F;

    public PerkTravelWaterMovement() {
        super("TRV_WATERMOV", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(!player.capabilities.isFlying) {
            if(player.isInWater()) {
                player.motionX *= swimSpeedMultiplier;
                player.motionY *= swimSpeedMultiplier;
                player.motionZ *= swimSpeedMultiplier;
                addAlignmentCharge(player, 0.01);
            }
            if(player.isInLava()) {
                player.motionX *= 1.4F * swimSpeedMultiplier;
                player.motionY *= 1.2F * swimSpeedMultiplier;
                player.motionZ *= 1.4F * swimSpeedMultiplier;
                addAlignmentCharge(player, 0.2);
            }
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        swimSpeedMultiplier = cfg.getFloat(getKey() + "SwimSpeedMultipler", getConfigurationSection(), 1.2F, 1F, 1.5F, "Sets the swim speed multiplier when a player has this perk, is in water and is not flying.");
    }

}
