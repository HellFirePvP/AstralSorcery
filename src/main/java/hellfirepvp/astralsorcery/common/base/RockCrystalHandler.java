/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RockCrystalHandler
 * Created by HellFirePvP
 * Date: 05.04.2019 / 19:12
 */
public class RockCrystalHandler {

    private static final ResourceLocation CAP_ROCKCRYSTAL_NAME = new ResourceLocation(AstralSorcery.MODID, "cap_chunk_rock_crystal_position");
    public static RockCrystalHandler INSTANCE = new RockCrystalHandler();

    @CapabilityInject(RockCrystalPositions.class)
    private static Capability<RockCrystalPositions> CAPABILITY_CHUNK_ROCK_CRYSTALS = null;

    public List<BlockPos> collectPositions(World world, ChunkPos center, int chunkRadius) {
        List<BlockPos> out = new LinkedList<>();
        for (int xx = -chunkRadius; xx <= chunkRadius; xx++) {
            for (int zz = -chunkRadius; zz <= chunkRadius; zz++) {
                ChunkPos other = new ChunkPos(center.x + xx, center.z + zz);
                if (MiscUtils.isChunkLoaded(world, other)) {
                    Chunk ch = world.getChunkFromChunkCoords(other.x, other.z);
                    RockCrystalPositions positions = ch.getCapability(CAPABILITY_CHUNK_ROCK_CRYSTALS, null);
                    if (positions != null) {
                        out.addAll(positions.crystalPositions);
                    }
                }
            }
        }
        return out;
    }

    public boolean addOre(World world, BlockPos pos, boolean force) {
        ChunkPos ch = new ChunkPos(pos);
        if (force || MiscUtils.isChunkLoaded(world, ch)) {
            return this.addOre(world.getChunkFromChunkCoords(ch.x, ch.z), pos);
        }
        return false;
    }

    public boolean addOre(Chunk chunk, BlockPos pos) {
        RockCrystalPositions positions = chunk.getCapability(CAPABILITY_CHUNK_ROCK_CRYSTALS, null);
        if (positions != null) {
            if (positions.crystalPositions.add(pos)) {
                chunk.markDirty();
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean removeOre(World world, BlockPos pos, boolean force) {
        ChunkPos ch = new ChunkPos(pos);
        if (force || MiscUtils.isChunkLoaded(world, ch)) {
            return this.removeOre(world.getChunkFromChunkCoords(ch.x, ch.z), pos);
        }
        return false;
    }

    public boolean removeOre(Chunk chunk, BlockPos pos) {
        RockCrystalPositions positions = chunk.getCapability(CAPABILITY_CHUNK_ROCK_CRYSTALS, null);
        if (positions != null) {
            if (positions.crystalPositions.remove(pos)) {
                chunk.markDirty();
                return true;
            }
            return false;
        }
        return false;
    }

    @SubscribeEvent
    public void attachChunkCap(AttachCapabilitiesEvent<Chunk> chunkEvent) {
        chunkEvent.addCapability(CAP_ROCKCRYSTAL_NAME, new RockCrystalPositionProvider());
    }

    public static class RockCrystalPositions implements INBTSerializable<NBTTagCompound> {

        private Set<BlockPos> crystalPositions = new HashSet<>();

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound cmp = new NBTTagCompound();

            NBTTagList posList = new NBTTagList();
            for (BlockPos exactPos : this.crystalPositions) {
                NBTTagCompound tag = new NBTTagCompound();
                NBTHelper.writeBlockPosToNBT(exactPos, tag);
                posList.appendTag(tag);
            }

            cmp.setTag("posList", posList);
            return cmp;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            this.crystalPositions.clear();

            NBTTagList entries = nbt.getTagList("posList", 10);
            for (int j = 0; j < entries.tagCount(); j++) {
                NBTTagCompound tag = entries.getCompoundTagAt(j);
                this.crystalPositions.add(NBTHelper.readBlockPosFromNBT(tag));
            }
        }
    }

    public static class RockCrystalPositionProvider implements ICapabilitySerializable<NBTTagCompound> {

        private final RockCrystalPositions defaultImpl = new RockCrystalPositions();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability.equals(CAPABILITY_CHUNK_ROCK_CRYSTALS);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? CAPABILITY_CHUNK_ROCK_CRYSTALS.cast(defaultImpl) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return defaultImpl.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            defaultImpl.deserializeNBT(nbt);
        }
    }

    public static class ChunkFluidEntryFactory implements Callable<RockCrystalPositions> {
        @Override
        public RockCrystalPositions call() throws Exception {
            return new RockCrystalPositions();
        }
    }

}
