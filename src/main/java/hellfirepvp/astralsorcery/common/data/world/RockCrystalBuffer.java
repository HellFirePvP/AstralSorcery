/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world;

import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.SectionWorldData;
import hellfirepvp.observerlib.common.data.base.WorldSection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RockCrystalBuffer
 * Created by HellFirePvP
 * Date: 17.08.2019 / 22:42
 */
public class RockCrystalBuffer extends SectionWorldData<RockCrystalBuffer.BufferSection> {

    public RockCrystalBuffer(WorldCacheDomain.SaveKey<?> key) {
        super(key, PRECISION_AREA);
    }

    @Override
    protected BufferSection createNewSection(int sectionX, int sectionZ) {
        return new BufferSection(sectionX, sectionZ);
    }

    public List<BlockPos> collectPositions(ChunkPos center, int chunkRadius) {
        List<BlockPos> out = new LinkedList<>();
        for (int xx = -chunkRadius; xx <= chunkRadius; xx++) {
            for (int zz = -chunkRadius; zz <= chunkRadius; zz++) {
                ChunkPos other = new ChunkPos(center.x + xx, center.z + zz);
                BufferSection section = this.getSection(other.getBlock(0, 0, 0));
                if (section != null) {
                    out.addAll(section.crystalPositions);
                }
            }
        }
        return out;
    }

    public void addOre(BlockPos pos) {
        BufferSection section = this.getOrCreateSection(pos);
        section.crystalPositions.add(pos);
        markDirty(section);
    }

    public void removeOre(BlockPos pos) {
        BufferSection section = this.getSection(pos);
        if (section != null) {
            section.crystalPositions.remove(pos);
            markDirty(section);
        }
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {}

    @Override
    public void readFromNBT(CompoundNBT nbt) {}

    @Override
    public void updateTick(World world) {}

    public static class BufferSection extends WorldSection {

        private Set<BlockPos> crystalPositions = new HashSet<>();

        private BufferSection(int sX, int sZ) {
            super(sX, sZ);
        }

        @Override
        public void writeToNBT(CompoundNBT tag) {
            ListNBT posList = new ListNBT();
            for (BlockPos exactPos : crystalPositions) {
                posList.add(NBTHelper.writeBlockPosToNBT(exactPos, new CompoundNBT()));
            }
            tag.put("posList", posList);
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            crystalPositions.clear();

            ListNBT entries = tag.getList("posList", Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < entries.size(); j++) {
                crystalPositions.add(NBTHelper.readBlockPosFromNBT(entries.getCompound(j)));
            }
        }
    }

}
