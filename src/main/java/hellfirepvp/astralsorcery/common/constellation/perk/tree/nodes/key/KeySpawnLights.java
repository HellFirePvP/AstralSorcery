/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeySpawnLights
 * Created by HellFirePvP
 * Date: 11.08.2018 / 21:27
 */
public class KeySpawnLights extends KeyPerk implements IPlayerTickPerk {

    private int lightSpawnRate = 15;
    private int radiusToSpawnLight = 5;

    public KeySpawnLights(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                lightSpawnRate = cfg.getInt("SpawnLightRate", getConfigurationSection(), lightSpawnRate, 5, 100_000,
                        "Defines the rate in ticks a position to spawn a light in is attempted to be found near the player.");
                radiusToSpawnLight = cfg.getInt("RadiusSpawnLight", getConfigurationSection(), radiusToSpawnLight, 2, 10,
                        "Defines the radius around the player the perk will search for a suitable position.");
            }
        });
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            if(player.ticksExisted % lightSpawnRate == 0) {
                int attempts = 4;
                while (attempts > 0) {
                    BlockPos pos = player.getPosition().add(
                            rand.nextInt(radiusToSpawnLight) * (rand.nextBoolean() ? 1 : -1),
                            rand.nextInt(radiusToSpawnLight) * (rand.nextBoolean() ? 1 : -1),
                            rand.nextInt(radiusToSpawnLight) * (rand.nextBoolean() ? 1 : -1));
                    if(MiscUtils.isChunkLoaded(player.getEntityWorld(), pos) &&
                            TileIlluminator.illuminatorCheck.isStateValid(player.getEntityWorld(), pos, player.getEntityWorld().getBlockState(pos))) {
                        if (player.getEntityWorld().setBlockState(pos, BlocksAS.blockVolatileLight.getDefaultState())) {
                            return;
                        }
                    }
                    attempts--;
                }
            }
        }
    }
}
