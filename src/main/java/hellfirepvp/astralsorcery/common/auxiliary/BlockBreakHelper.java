/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.tick.TickTokenMap;
import hellfirepvp.observerlib.common.util.tick.TickManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBreakHelper
 * Created by HellFirePvP
 * Date: 21.09.2019 / 18:35
 */
public class BlockBreakHelper {

    private static final Map<ResourceLocation, TickTokenMap<BlockPos, BreakEntry>> breakMap = new HashMap<>();

    public static BreakEntry addProgress(IWorld world, BlockPos pos, float expectedHardness, float percStrength) {
        TickTokenMap<BlockPos, BreakEntry> map = breakMap.computeIfAbsent(world.getDimension().getType().getRegistryName(), key -> {
            TickTokenMap<BlockPos, BreakEntry> tkMap = new TickTokenMap<>(TickEvent.Type.SERVER);
            AstralSorcery.getProxy().getTickManager().register(tkMap);
            return tkMap;
        });

        BreakEntry breakProgress = map.get(pos);
        if (breakProgress == null) {
            breakProgress = new BreakEntry(expectedHardness, world, pos, world.getBlockState(pos));
            map.put(pos, breakProgress);
        }

        breakProgress.breakProgress -= percStrength;
        breakProgress.idleTimeout = 0;
        return breakProgress;
    }

    public static void clearServerCache() {
        TickManager mgr = AstralSorcery.getProxy().getTickManager();
        breakMap.values().forEach(mgr::unregister);
        breakMap.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static void blockBreakAnimation(PktPlayEffect pktPlayEffect) {
        BlockPos pos = ByteBufUtils.readPos(pktPlayEffect.getExtraData());
        int id = pktPlayEffect.getExtraData().readInt();
        BlockState state = Block.getStateById(id);

        RenderingUtils.playBlockBreakParticles(pos, state, state);
    }

    public static class BreakEntry implements TickTokenMap.TickMapToken<Float>, CEffectAbstractList.ListEntry {

        private float breakProgress;
        private final IWorld world;
        private BlockPos pos;
        private BlockState expected;

        private int idleTimeout;

        public BreakEntry(@Nonnull Float value, IWorld world, BlockPos at, BlockState expectedToBreak) {
            this.breakProgress = value;
            this.world = world;
            this.pos = at;
            this.expected = expectedToBreak;
        }

        @Override
        public int getRemainingTimeout() {
            return (breakProgress <= 0 || idleTimeout >= 20) ? 0 : 1;
        }

        @Override
        public void tick() {
            idleTimeout++;
        }

        @Override
        public void onTimeout() {
            if (breakProgress > 0) {
                return;
            }

            BlockState nowAt = world.getBlockState(pos);
            if (world instanceof ServerWorld && BlockUtils.matchStateExact(expected, nowAt)) {
                BlockUtils.breakBlockWithoutPlayer((ServerWorld) world, pos, world.getBlockState(pos), ItemStack.EMPTY,
                        true, true, true);
            }
        }

        @Override
        public Float getValue() {
            return breakProgress;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(CompoundNBT nbt) {
            this.breakProgress = nbt.getFloat("breakProgress");
            this.pos = NBTHelper.readBlockPosFromNBT(nbt);
            this.expected = Block.getStateById(nbt.getInt("expectedStateId"));
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {
            nbt.putFloat("breakProgress", this.breakProgress);
            NBTHelper.writeBlockPosToNBT(this.pos, nbt);
            nbt.putInt("expectedStateId", Block.getStateId(this.expected));
        }

    }
}
