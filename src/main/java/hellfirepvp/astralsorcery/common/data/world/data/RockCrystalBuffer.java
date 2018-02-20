/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world.data;

import hellfirepvp.astralsorcery.common.data.world.CachedWorldData;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RockCrystalBuffer
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:30
 */
public class RockCrystalBuffer extends CachedWorldData {

    private Map<ChunkPos, List<BlockPos>> crystalPositions = new HashMap<>();
    private static final Object lock = new Object();

    public RockCrystalBuffer() {
        super(WorldCacheManager.SaveKey.ROCK_CRYSTAL);
    }

    @Override
    public void updateTick(World world) {}

    public List<BlockPos> collectPositions(ChunkPos center, int rad) {
        List<BlockPos> out = new LinkedList<>();
        for (int xx = -rad; xx <= rad; xx++) {
            for (int zz = -rad; zz <= rad; zz++) {
                ChunkPos other = new ChunkPos(center.x + xx, center.z + zz);
                List<BlockPos> saved = crystalPositions.get(other);
                if(saved != null) {
                    out.addAll(saved);
                }
            }
        }
        return out;
    }

    public void addOre(BlockPos pos) {
        ChunkPos ch = new ChunkPos(pos);
        synchronized (lock) {
            if(!crystalPositions.containsKey(ch)) {
                crystalPositions.put(ch, new LinkedList<>());
            }
            crystalPositions.get(ch).add(pos);
        }

        markDirty();
    }

    public void removeOre(BlockPos pos) {
        ChunkPos ch = new ChunkPos(pos);
        if(!crystalPositions.containsKey(ch)) return;
        boolean removed;
        synchronized (lock) {
            removed = crystalPositions.get(ch).remove(pos);
            if(crystalPositions.get(ch).size() == 0) {
                crystalPositions.remove(ch);
                removed = true;
            }
        }

        if(removed) {
            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        synchronized (lock) {
            crystalPositions.clear();
        }

        Map<ChunkPos, List<BlockPos>> work = new HashMap<>();
        if(nbt.hasKey("crystalList")) {
            NBTTagList list = nbt.getTagList("crystalList", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound chList = list.getCompoundTagAt(i);
                int chX = chList.getInteger("chX");
                int chZ = chList.getInteger("chZ");
                ChunkPos pos = new ChunkPos(chX, chZ);
                List<BlockPos> positions = new LinkedList<>();
                NBTTagList entries = chList.getTagList("crystals", 10);
                for (int j = 0; j < entries.tagCount(); j++) {
                    NBTTagCompound tag = entries.getCompoundTagAt(j);
                    positions.add(NBTUtils.readBlockPosFromNBT(tag));
                }
                work.put(pos, positions);
            }
        }

        synchronized (lock) {
            crystalPositions = work;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList listCrystals = new NBTTagList();
        synchronized (lock) {
            for (ChunkPos pos : crystalPositions.keySet()) {
                NBTTagCompound comp = new NBTTagCompound();
                comp.setInteger("chX", pos.x);
                comp.setInteger("chZ", pos.z);
                NBTTagList chList = new NBTTagList();
                for (BlockPos exactPos : crystalPositions.get(pos)) {
                    NBTTagCompound tag = new NBTTagCompound();
                    NBTUtils.writeBlockPosToNBT(exactPos, tag);
                    chList.appendTag(tag);
                }
                comp.setTag("crystals", chList);
                listCrystals.appendTag(comp);
            }
        }
        nbt.setTag("crystalList", listCrystals);
    }

}
