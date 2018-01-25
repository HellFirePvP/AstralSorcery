/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect.time;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopZone
 * Created by HellFirePvP
 * Date: 17.10.2017 / 22:14
 */
public class TimeStopZone {

    final int ownerId;
    final boolean hasOwner;

    final float range;
    final BlockPos offset;
    private final World world;
    private int ticksToLive = 0;

    private boolean active = true;

    private List<TileEntity> cachedTiles = new LinkedList<>();

    TimeStopZone(Entity owner, float range, BlockPos offset, World world, int tickLivespan) {
        this.hasOwner = owner != null;
        if(this.hasOwner) {
            this.ownerId = owner.getEntityId();
        } else {
            this.ownerId = -1;
        }
        this.range = range;
        this.offset = offset;
        this.world = world;
        this.ticksToLive = tickLivespan;
    }

    void onServerTick() {
        if(!active) return;
        this.ticksToLive--;

        int minX = MathHelper.floor((offset.getX() - range) / 16.0D);
        int maxX = MathHelper.floor((offset.getX() + range) / 16.0D);
        int minZ = MathHelper.floor((offset.getZ() - range) / 16.0D);
        int maxZ = MathHelper.floor((offset.getZ() + range) / 16.0D);

        for (int xx = minX; xx <= maxX; ++xx) {
            for (int zz = minZ; zz <= maxZ; ++zz) {
                Chunk ch = world.getChunkFromChunkCoords(xx, zz);
                if(!ch.isEmpty()) {
                    Map<BlockPos, TileEntity> map = ch.getTileEntityMap();
                    for (Map.Entry<BlockPos, TileEntity> teEntry : map.entrySet()) {
                        TileEntity te = teEntry.getValue();
                        if(te != null &&
                                te instanceof ITickable &&
                                offset.getDistance(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()) <= range &&
                                world.tickableTileEntities.contains(te)) {
                            world.tickableTileEntities.remove(te);
                            safeCacheTile(te);
                        }
                    }
                }
            }
        }
    }

    private void safeCacheTile(TileEntity te) {
        if(te == null) return;

        for (TileEntity tile : cachedTiles) {
            if(tile.getPos().equals(te.getPos())) {
                return;
            }
        }
        cachedTiles.add(te);
    }

    public void stopEffect() {
        world.tickableTileEntities.addAll(cachedTiles);
        this.cachedTiles.clear();
        this.active = false;
    }

    boolean shouldDespawn() {
        return ticksToLive <= 0 || !active;
    }

    boolean interceptEntityTick(EntityLivingBase e) {
        return active && e != null && (!hasOwner || e.getEntityId() != ownerId) && Vector3.atEntityCorner(e).distance(offset) <= range;
    }

    //Mainly because we still want to be able to do damage.
    void handleImportantEntityTicks(EntityLivingBase e) {
        if (e.hurtTime > 0) {
            e.hurtTime--;
        }
        if (e.hurtResistantTime > 0) {
            e.hurtResistantTime--;
        }
    }

}
