/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.data.TickTokenizedMap;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBreakAssist
 * Created by HellFirePvP
 * Date: 26.07.2017 / 16:19
 */
public class BlockBreakAssist {

    private static final Map<Integer, TickTokenizedMap<BlockPos, BreakEntry>> breakMap = new HashMap<>();

    public static BreakEntry addProgress(World world, BlockPos pos, float expectedHardness, float percStrength) {
        TickTokenizedMap<BlockPos, BreakEntry> map = breakMap.get(world.provider.getDimension());
        if(map == null) {
            map = new TickTokenizedMap<>(TickEvent.Type.SERVER);
            TickManager.getInstance().register(map);
            breakMap.put(world.provider.getDimension(), map);
        }

        BreakEntry breakProgress = map.get(pos);
        if(breakProgress == null) {
            breakProgress = new BreakEntry(expectedHardness, world, pos, world.getBlockState(pos));
            map.put(pos, breakProgress);
        }

        breakProgress.breakProgress -= percStrength;
        breakProgress.idleTimeout = 0;
        return breakProgress;
    }

    @SideOnly(Side.CLIENT)
    public static void blockBreakAnimation(PktPlayEffect pktPlayEffect) {
        RenderingUtils.playBlockBreakParticles(pktPlayEffect.pos, Block.getStateById(pktPlayEffect.data));
    }

    public static class BreakEntry implements TickTokenizedMap.TickMapToken<Float>,CEffectPositionListGen.CEffectGenListEntry {

        private float breakProgress;
        private final World world;
        private BlockPos pos;
        private IBlockState expected;

        private int idleTimeout;

        public BreakEntry(World world) {
            this.world = world;
        }

        public BreakEntry(@Nonnull Float value, World world, BlockPos at, IBlockState expectedToBreak) {
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
            if(breakProgress > 0) return;

            IBlockState nowAt = world.getBlockState(pos);
            if(nowAt.getBlock().equals(expected.getBlock()) && nowAt.getBlock().getMetaFromState(nowAt) == expected.getBlock().getMetaFromState(expected)) {
                MiscUtils.breakBlockWithoutPlayer((WorldServer) world, pos, world.getBlockState(pos), true, true, true);
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
        public void readFromNBT(NBTTagCompound nbt) {
            this.breakProgress = nbt.getFloat("breakProgress");
            this.pos = NBTUtils.readBlockPosFromNBT(nbt);
            this.expected = Block.getStateById(nbt.getInteger("expectedStateId"));
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setFloat("breakProgress", this.breakProgress);
            NBTUtils.writeBlockPosToNBT(this.pos, nbt);
            nbt.setInteger("expectedStateId", Block.getStateId(this.expected));
        }

    }
}
