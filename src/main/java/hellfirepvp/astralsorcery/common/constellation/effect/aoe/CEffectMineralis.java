/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ListEntries;
import hellfirepvp.astralsorcery.common.data.config.registry.OreBlockRarityRegistry;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockLayerPositionGenerator;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockPositionGenerator;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockRandomPositionGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectMineralis
 * Created by HellFirePvP
 * Date: 01.02.2020 / 10:54
 */
public class CEffectMineralis extends CEffectAbstractList<ListEntries.PosEntry> {

    public static MineralisConfig CONFIG = new MineralisConfig();

    public CEffectMineralis(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.mineralis, CONFIG.maxAmount.get(), (world, pos, state) -> true);
        this.excludeRitualColumn();
        this.selectSphericalPositions();
    }

    @Nonnull
    @Override
    protected BlockPositionGenerator createPositionStrategy() {
        return new BlockLayerPositionGenerator();
    }

    @Nonnull
    @Override
    protected BlockPositionGenerator selectPositionStrategy(BlockPositionGenerator defaultGenerator, ConstellationEffectProperties properties) {
        if (!properties.isCorrupted()) {
            return new BlockRandomPositionGenerator();
        }
        return defaultGenerator;
    }

    @Nullable
    @Override
    public ListEntries.PosEntry recreateElement(CompoundNBT tag, BlockPos pos) {
        return new ListEntries.PosEntry(pos);
    }

    @Nullable
    @Override
    public ListEntries.PosEntry createElement(World world, BlockPos pos) {
        return new ListEntries.PosEntry(pos);
    }

    @Override
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {

    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        ListEntries.PosEntry entry = this.peekNewPosition(world, pos, properties);
        if (entry != null) {
            BlockState atState = world.getBlockState(entry.getPos());
            if (properties.isCorrupted()) {
                if (world.isAirBlock(entry.getPos())) {
                    if (rand.nextInt(25) == 0) {
                        Block ore = OreBlockRarityRegistry.MINERALIS_RITUAL.getRandomBlock(rand);
                        if (ore != null) {
                            return world.setBlockState(entry.getPos(), ore.getDefaultState());
                        } else {
                            return world.setBlockState(entry.getPos(), Blocks.STONE.getDefaultState());
                        }
                    } else {
                        return world.setBlockState(entry.getPos(), Blocks.STONE.getDefaultState());
                    }
                }
            } else {
                if (atState.getBlock() == Blocks.STONE) {
                    Block ore = OreBlockRarityRegistry.MINERALIS_RITUAL.getRandomBlock(rand);
                    if (ore != null) {
                        return world.setBlockState(entry.getPos(), ore.getDefaultState());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class MineralisConfig extends CountConfig {

        public MineralisConfig() {
            super("mineralis", 6D, 4D, 1);
        }
    }
}
