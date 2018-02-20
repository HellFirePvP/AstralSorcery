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
import hellfirepvp.astralsorcery.common.world.AstralWorldGenerator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChunkVersionBuffer
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:38
 */
public class ChunkVersionBuffer extends CachedWorldData {

    private Map<ChunkPos, Integer> chunkVersions = new HashMap<>();

    public ChunkVersionBuffer() {
        super(WorldCacheManager.SaveKey.CHUNK_VERSIONING);
    }

    public void markChunkGeneration(ChunkPos c) {
        chunkVersions.put(c, AstralWorldGenerator.CURRENT_WORLD_GENERATOR_VERSION);
        //markDirty();
    }

    @Nullable
    public Integer getGenerationVersion(ChunkPos c) {
        return chunkVersions.get(c);
    }

    @Override
    public void updateTick(World world) {}

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        chunkVersions.clear();

        for (String key : compound.getKeySet()) {
            String[] chunkCoords = key.split(";");
            ChunkPos pos = new ChunkPos(Integer.parseInt(chunkCoords[0]), Integer.parseInt(chunkCoords[1]));
            chunkVersions.put(pos, compound.getInteger(key));
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        //for (Map.Entry<ChunkPos, Integer> versionEntry : chunkVersions.entrySet()) {
        //    ChunkPos cp = versionEntry.getKey();
        //    compound.setInteger(String.format("%d;%d", cp.chunkXPos, cp.chunkZPos), versionEntry.getValue());
        //}

    }

}
