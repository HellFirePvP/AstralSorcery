/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.StructuresAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockPositionGenerator;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockSpherePositionGenerator;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectEvorsio
 * Created by HellFirePvP
 * Date: 24.11.2019 / 10:03
 */
public class CEffectEvorsio extends CEffectAbstractList<ListEntries.PosEntry> {

    public static EvorsioConfig CONFIG = new EvorsioConfig();

    //Used to disable pedestal whitelisting when finding positions to break blocks at.
    private boolean isLinkedRitual = false;

    public CEffectEvorsio(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.evorsio, 1, (world, pos, state) -> true);
    }

    @Nonnull
    @Override
    protected BlockPositionGenerator createPositionStrategy() {
        BlockPositionGenerator gen = new BlockSpherePositionGenerator();
        gen.andFilter(pos -> !BlockPos.ZERO.equals(pos));
        //Shift down as the ritual originates from a ritual link position.
        gen.andFilter(pos -> this.isLinkedRitual || !StructuresAS.STRUCT_RITUAL_PEDESTAL.hasBlockAt(pos));
        return gen;
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
        //TODO effects
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if (!MiscUtils.isChunkLoaded(world, pos) || !(world instanceof ServerWorld)) {
            return false;
        }
        this.isLinkedRitual = MiscUtils.getTileAt(world, pos, TileRitualLink.class, true) != null;

        if (properties.isCorrupted()) {
            ListEntries.PosEntry newEntry = this.peekNewPosition(world, pos, properties);
            if (newEntry != null) {
                BlockPos at = newEntry.getPos();
                if (at.getY() < pos.getY() && world.isAirBlock(at)) {
                    BlockState state = Blocks.DIRT.getDefaultState();
                    if (rand.nextInt(3) == 0) {
                        state = Blocks.STONE.getDefaultState();
                    }
                    world.setBlockState(at, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
                }
            }
            return false;
        }

        ListEntries.PosEntry newEntry = this.peekNewPosition(world, pos, properties);
        if (newEntry != null) {
            BlockPos at = newEntry.getPos();
            if (this.canBreakBlock(world, at, world.getBlockState(at))) {
                BlockDropCaptureAssist.startCapturing();
                try {
                    BlockUtils.breakBlockWithoutPlayer((ServerWorld) world, at, world.getBlockState(at),
                            ItemStack.EMPTY, true, true, true);
                } finally {
                    NonNullList<ItemStack> captured = BlockDropCaptureAssist.getCapturedStacksAndStop();
                    captured.forEach((stack) -> ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, stack));
                }
                return true;
            }
        }
        return false;
    }

    private boolean canBreakBlock(World world, BlockPos pos, BlockState state) {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0 || hardness >= 75) {
            return false;
        }
        return !state.isAir(world, pos);
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class EvorsioConfig extends Config {

        public EvorsioConfig() {
            super("evorsio", 6D, 4D);
        }
    }
}
