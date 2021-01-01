/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ListEntries;
import hellfirepvp.astralsorcery.common.event.PlayerAffectionFlags;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockPositionGenerator;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockSpherePositionGenerator;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectEvorsio
 * Created by HellFirePvP
 * Date: 24.11.2019 / 10:03
 */
public class CEffectEvorsio extends CEffectAbstractList<ListEntries.PosEntry> {

    public static PlayerAffectionFlags.AffectionFlag FLAG = makeAffectionFlag("evorsio");
    public static EvorsioConfig CONFIG = new EvorsioConfig();

    public CEffectEvorsio(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.evorsio, 1, (world, pos, state) -> true);
        this.excludeRitualPositions();
    }

    @Nonnull
    @Override
    protected BlockPositionGenerator createPositionStrategy() {
        return new BlockSpherePositionGenerator();
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
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        float addY = 1F;
        if (!pedestal.getPos().equals(pos)) {
            addY = 0F;
        }
        Vector3 motion = Vector3.random().multiply(0.1);
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(pos).add(0.5, 0.5, 0.5).addY(addY))
                .alpha(VFXAlphaFunction.FADE_OUT)
                .setMotion(motion)
                .color(VFXColorFunction.constant(ColorsAS.CONSTELLATION_EVORSIO))
                .setScaleMultiplier(0.3F + rand.nextFloat() * 0.4F)
                .setMaxAge(50);
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if (!(world instanceof ServerWorld)) {
            return false;
        }

        return this.peekNewPosition(world, pos, properties).mapLeft(newEntry -> {
            BlockPos at = newEntry.getPos();

            if (properties.isCorrupted()) {
                if (at.getY() < pos.getY() && world.isAirBlock(at)) {
                    double distance = pos.distanceSq(at) / (properties.getSize() * properties.getSize());
                    BlockState state = Blocks.COBBLESTONE.getDefaultState();
                    if (distance >= 0.85F && rand.nextInt(4) == 0) {
                        state = Blocks.DIRT.getDefaultState();
                    }
                    if (distance <= 0.25F) {
                        state = Blocks.STONE.getDefaultState();
                    } else if (distance <= 0.1F && rand.nextInt(5) == 0) {
                        state = Blocks.OBSIDIAN.getDefaultState();
                    }
                    world.setBlockState(at, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
                }
                return false;
            }

            TileRitualPedestal pedestal = getPedestal(world, pos);
            if (pedestal != null) {
                BlockState state = world.getBlockState(at);
                if (this.canBreakBlock(world, at, state, buildFilter(pedestal))) {
                    BlockDropCaptureAssist.startCapturing();
                    try {
                        BlockUtils.breakBlockWithoutPlayer((ServerWorld) world, at, state,
                                ItemStack.EMPTY, true, true);
                    } finally {
                        NonNullList<ItemStack> captured = BlockDropCaptureAssist.getCapturedStacksAndStop();
                        captured.forEach((stack) -> ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, stack));
                    }
                    return true;
                } else {
                    sendConstellationPing(world, new Vector3(at).add(0.5, 0.5, 0.5));
                }
            }
            return false;
        }).ifRight(attemptedBreak -> {
            sendConstellationPing(world, new Vector3(attemptedBreak).add(0.5, 0.5, 0.5));
        }).left().orElse(false);
    }

    private boolean canBreakBlock(World world, BlockPos pos, BlockState state, Predicate<BlockState> blacklist) {
        if (blacklist.test(state)) {
            return false;
        }
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0 || hardness >= 75) {
            return false;
        }
        return !state.isAir(world, pos);
    }

    private Predicate<BlockState> buildFilter(TileRitualPedestal pedestal) {
        List<Predicate<BlockState>> filteredBlocks = pedestal.getConfiguredBlockStates().stream()
                .map(blockState -> (Predicate<BlockState>) blockState::equals)
                .collect(Collectors.toList());
        this.addDefaultBreakBlacklist(filteredBlocks);
        return blockState -> {
            for (Predicate<BlockState> filterTest : filteredBlocks) {
                if (filterTest.test(blockState)) {
                    return true;
                }
            }
            return false;
        };
    }

    private void addDefaultBreakBlacklist(List<Predicate<BlockState>> out) {
        out.add((state) -> state.getBlock().equals(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL));
        out.add((state) -> state.getBlock().equals(BlocksAS.ROCK_COLLECTOR_CRYSTAL));
        out.add((state) -> state.getBlock().equals(BlocksAS.LENS));
        out.add((state) -> state.getBlock().equals(BlocksAS.PRISM));
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    public PlayerAffectionFlags.AffectionFlag getPlayerAffectionFlag() {
        return FLAG;
    }

    private static class EvorsioConfig extends Config {

        public EvorsioConfig() {
            super("evorsio", 6D, 1D);
        }
    }
}
