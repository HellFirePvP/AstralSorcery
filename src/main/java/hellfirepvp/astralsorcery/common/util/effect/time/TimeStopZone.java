/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect.time;

import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
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

    final EntityTargetController targetController;

    final float range;
    final BlockPos offset;
    private final World world;
    private int ticksToLive = 0;

    private boolean active = true;
    boolean reducedParticles = false;

    private List<TileEntity> cachedTiles = new LinkedList<>();

    TimeStopZone(EntityTargetController ctrl, float range, BlockPos offset, World world, int tickLivespan, boolean reducedParticles) {
        this.targetController = ctrl;
        this.range = range;
        this.offset = offset;
        this.world = world;
        this.ticksToLive = tickLivespan;
        this.reducedParticles = reducedParticles;
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

    public void setTicksToLive(int ticksToLive) {
        this.ticksToLive = ticksToLive;
    }

    void stopEffect() {
        for (TileEntity cached : cachedTiles) {
            IBlockState state = world.getBlockState(cached.getPos());
            if (state.getBlock().hasTileEntity(state)) {
                TileEntity te = state.getBlock().createTileEntity(world, state);
                if (te != null && te.getClass().isAssignableFrom(cached.getClass())) {
                    world.tickableTileEntities.add(cached);
                }
            }
        }
        this.cachedTiles.clear();
        this.active = false;
    }

    boolean shouldDespawn() {
        return ticksToLive <= 0 || !active;
    }

    boolean interceptEntityTick(EntityLivingBase e) {
        return active && e != null && targetController.shouldFreezeEntity(e) && Vector3.atEntityCorner(e).distance(offset) <= range;
    }

    //Mainly because we still want to be able to do damage.
    static void handleImportantEntityTicks(EntityLivingBase e) {
        if (e.hurtTime > 0) {
            e.hurtTime--;
        }
        if (e.hurtResistantTime > 0) {
            e.hurtResistantTime--;
        }
        e.prevPosX = e.posX;
        e.prevPosY = e.posY;
        e.prevPosZ = e.posZ;
        e.prevLimbSwingAmount = e.limbSwingAmount;
        e.prevRenderYawOffset = e.renderYawOffset;
        e.prevRotationPitch = e.rotationPitch;
        e.prevRotationYaw = e.rotationYaw;
        e.prevRotationYawHead = e.rotationYawHead;
        e.prevSwingProgress = e.swingProgress;
        e.prevDistanceWalkedModified = e.distanceWalkedModified;
        e.prevCameraPitch = e.cameraPitch;

        if(e.isPotionActive(RegistryPotions.potionTimeFreeze)) {
            PotionEffect pe = e.getActivePotionEffect(RegistryPotions.potionTimeFreeze);
            if (!pe.onUpdate(e)) {
                if (!e.world.isRemote) {
                    e.removePotionEffect(RegistryPotions.potionTimeFreeze);
                }
            }
        }

        if(e instanceof EntityDragon) {
            IPhase phase = ((EntityDragon) e).getPhaseManager().getCurrentPhase();
            if(phase.getType() != PhaseList.HOLDING_PATTERN && phase.getType() != PhaseList.DYING) {
                ((EntityDragon) e).getPhaseManager().setPhase(PhaseList.HOLDING_PATTERN);
            }
        }
    }

    public static class EntityTargetController {

        final int ownerId;
        final boolean hasOwner;
        final boolean targetPlayers;

        EntityTargetController(int ownerId, boolean hasOwner, boolean targetPlayers) {
            this.ownerId = ownerId;
            this.hasOwner = hasOwner;
            this.targetPlayers = targetPlayers;
        }

        boolean shouldFreezeEntity(EntityLivingBase e) {
            if(e.isDead || e.getHealth() <= 0) {
                return false;
            }
            if(e instanceof EntityDragon && ((EntityDragon) e).getPhaseManager().getCurrentPhase() == PhaseList.DYING) {
                return false;
            }
            if(hasOwner && e.getEntityId() == ownerId) {
                return false;
            }
            return targetPlayers || !(e instanceof EntityPlayer);
        }

        public static EntityTargetController allExcept(Entity entity) {
            return new EntityTargetController(entity.getEntityId(), true, true);
        }

        public static EntityTargetController noPlayers() {
            return new EntityTargetController(-1, false, false);
        }

        @Nonnull
        public NBTTagCompound serializeNBT() {
            NBTTagCompound out = new NBTTagCompound();
            out.setBoolean("targetPlayers", this.targetPlayers);
            out.setBoolean("hasOwner", this.hasOwner);
            out.setInteger("ownerEntityId", this.ownerId);
            return out;
        }

        @Nonnull
        public static EntityTargetController deserializeNBT(NBTTagCompound cmp) {
            boolean targetPlayers = cmp.getBoolean("targetPlayers");
            boolean hasOwner = cmp.getBoolean("hasOwner");
            int ownerId = cmp.getInteger("ownerEntityId");
            return new EntityTargetController(ownerId, hasOwner, targetPlayers);
        }

    }

}
