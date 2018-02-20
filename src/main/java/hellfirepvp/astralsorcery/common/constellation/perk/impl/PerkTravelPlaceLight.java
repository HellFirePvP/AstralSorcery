/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTravelPlaceLight
 * Created by HellFirePvP
 * Date: 02.12.2016 / 22:53
 */
public class PerkTravelPlaceLight extends ConstellationPerk {

    private static int chanceToSpawnLight = 100;

    public PerkTravelPlaceLight() {
        super("TRV_LIGHT", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            if(rand.nextInt(chanceToSpawnLight) == 0) {
                BlockPos pos = player.getPosition().add(
                        rand.nextInt(4) - 2,
                        rand.nextInt(4) - 2,
                        rand.nextInt(4) - 2);
                if(TileIlluminator.illuminatorCheck.isStateValid(player.getEntityWorld(), pos, player.getEntityWorld().getBlockState(pos))) {
                    addAlignmentCharge(player, 2);
                    player.getEntityWorld().setBlockState(pos, BlocksAS.blockVolatileLight.getDefaultState());
                }
            }
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        chanceToSpawnLight = cfg.getInt(getKey() + "ChanceForLight", getConfigurationSection(), 100, 20, 4000, "Sets the chance (Random.nextInt(chance) == 0) to try to see if a light close to the player might be spawned");
    }
}
