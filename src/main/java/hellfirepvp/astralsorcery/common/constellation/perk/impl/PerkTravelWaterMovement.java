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
        if(player.isInWater() && !player.capabilities.isFlying) {
            player.motionX *=        swimSpeedMultiplier;
            player.motionY *= 0.9F * swimSpeedMultiplier;
            player.motionZ *=        swimSpeedMultiplier;
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
