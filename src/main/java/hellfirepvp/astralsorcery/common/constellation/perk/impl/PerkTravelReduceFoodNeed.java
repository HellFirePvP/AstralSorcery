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
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTravelReduceFoodNeed
 * Created by HellFirePvP
 * Date: 04.12.2016 / 15:14
 */
public class PerkTravelReduceFoodNeed extends ConstellationPerk {

    private static float tickExhaustionReduction = 0.01F;

    public PerkTravelReduceFoodNeed() {
        super("TRV_FOODREDUCTION", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            FoodStats stats = player.getFoodStats();
            if(stats.foodExhaustionLevel > -50F) {
                stats.addExhaustion(-tickExhaustionReduction);
            }
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        tickExhaustionReduction = cfg.getFloat(getKey() + "TickExhaustionReduction", getConfigurationSection(), 0.01F, 0F, 0.1F, "Defines the food-exhaustion reduction applied each tick.");
    }

}
