/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tick.PlayerTickPerk;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeySpawnLights
 * Created by HellFirePvP
 * Date: 31.08.2019 / 22:38
 */
public class KeySpawnLights extends KeyPerk implements PlayerTickPerk {

    private static final int defaultLightSpawnRate = 15;
    private static final int defaultLightSpawnRadius = 5;
    private static final int defaultChargeCost = 60;

    public static final Config CONFIG = new Config("key.spawn_lights");

    public KeySpawnLights(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void onPlayerTick(PlayerEntity player, LogicalSide side) {
        if (side.isServer()) {
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (!prog.isValid() || !prog.doPerkAbilities()) {
                return;
            }
            int spawnRate = this.applyMultiplierI(CONFIG.lightSpawnRate.get());
            spawnRate = Math.max(spawnRate, 1);
            if (player.ticksExisted % spawnRate == 0) {
                int attempts = 4;
                while (attempts > 0) {
                    int radius = this.applyMultiplierI(CONFIG.lightSpawnRadius.get());
                    BlockPos pos = player.getPosition().add(
                            rand.nextInt(radius) * (rand.nextBoolean() ? 1 : -1),
                            rand.nextInt(radius) * (rand.nextBoolean() ? 1 : -1),
                            rand.nextInt(radius) * (rand.nextBoolean() ? 1 : -1));
                    if (MiscUtils.executeWithChunk(player.getEntityWorld(), pos, () -> {
                        if (TileIlluminator.ILLUMINATOR_CHECK.test(player.getEntityWorld(), pos, player.getEntityWorld().getBlockState(pos)) &&
                                AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCost.get(), true)) {
                            if (player.getEntityWorld().setBlockState(pos, BlocksAS.FLARE_LIGHT.getDefaultState())) {
                                AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCost.get(), false);
                                return true;
                            }
                            return false;
                        }
                        return false;
                    }, false)) {
                        return;
                    }

                    attempts--;
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue lightSpawnRate;
        private ForgeConfigSpec.IntValue lightSpawnRadius;
        private ForgeConfigSpec.IntValue chargeCost;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.lightSpawnRate = cfgBuilder
                    .comment("Defines the rate in ticks a position to spawn a light in is attempted to be found near the player")
                    .translation(translationKey("lightSpawnRate"))
                    .defineInRange("lightSpawnRate", defaultLightSpawnRate, 4, 1000);
            this.lightSpawnRadius = cfgBuilder
                    .comment("Defines the radius around the player the perk will search for a suitable position")
                    .translation(translationKey("lightSpawnRadius"))
                    .defineInRange("lightSpawnRadius", defaultLightSpawnRadius, 2, 10);
            this.chargeCost = cfgBuilder
                    .comment("Defines the amount of starlight charge consumed per spawned light.")
                    .translation(translationKey("chargeCost"))
                    .defineInRange("chargeCost", defaultChargeCost, 1, 500);
        }
    }
}
