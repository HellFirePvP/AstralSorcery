/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.time;

import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
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
 * Date: 31.08.2019 / 13:30
 */
public class TimeStopZone {

    final EntityTargetController targetController;

    final float range;
    final BlockPos offset;
    private final World world;
    private int ticksToLive = 0;

    private boolean active = true;

    private List<TileEntity> cachedTiles = new LinkedList<>();

    TimeStopZone(EntityTargetController ctrl, float range, BlockPos offset, World world, int tickLivespan) {
        this.targetController = ctrl;
        this.range = range;
        this.offset = offset;
        this.world = world;
        this.ticksToLive = tickLivespan;
    }

    void onServerTick() {
        if (!active) return;
        this.ticksToLive--;

        int minX = MathHelper.floor((offset.getX() - range) / 16.0D);
        int maxX = MathHelper.floor((offset.getX() + range) / 16.0D);
        int minZ = MathHelper.floor((offset.getZ() - range) / 16.0D);
        int maxZ = MathHelper.floor((offset.getZ() + range) / 16.0D);

        for (int xx = minX; xx <= maxX; ++xx) {
            for (int zz = minZ; zz <= maxZ; ++zz) {
                Chunk ch = world.getChunk(xx, zz);
                if (!ch.isEmpty()) {
                    Map<BlockPos, TileEntity> map = ch.getTileEntityMap();
                    for (Map.Entry<BlockPos, TileEntity> teEntry : map.entrySet()) {
                        TileEntity te = teEntry.getValue();
                        if (te instanceof ITickableTileEntity &&
                                te.getPos().withinDistance(offset, range) &&
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
        if (te == null) return;

        for (TileEntity tile : cachedTiles) {
            if (tile.getPos().equals(te.getPos())) {
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
            BlockState state = world.getBlockState(cached.getPos());
            if (state.getBlock().hasTileEntity(state)) {
                TileEntity te = state.getBlock().createTileEntity(state, world);
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

    boolean interceptEntityTick(LivingEntity e) {
        return active && e != null && targetController.shouldFreezeEntity(e) && Vector3.atEntityCorner(e).distance(offset) <= range;
    }

    //Mainly because we still want to be able to do damage.
    static void handleImportantEntityTicks(LivingEntity e) {
        if (e.hurtTime > 0) {
            e.hurtTime--;
        }
        if (e.hurtResistantTime > 0) {
            e.hurtResistantTime--;
        }
        e.prevPosX = e.getPosX();
        e.prevPosY = e.getPosY();
        e.prevPosZ = e.getPosZ();
        e.prevLimbSwingAmount = e.limbSwingAmount;
        e.prevRenderYawOffset = e.renderYawOffset;
        e.prevRotationPitch = e.rotationPitch;
        e.prevRotationYaw = e.rotationYaw;
        e.prevRotationYawHead = e.rotationYawHead;
        e.prevSwingProgress = e.swingProgress;
        e.prevDistanceWalkedModified = e.distanceWalkedModified;

        if (e.isPotionActive(EffectsAS.EFFECT_TIME_FREEZE)) {
            EffectInstance eff = e.getActivePotionEffect(EffectsAS.EFFECT_TIME_FREEZE);
            if (!eff.tick(e, () -> {})) {
                if (!e.world.isRemote()) {
                    e.removePotionEffect(EffectsAS.EFFECT_TIME_FREEZE);
                }
            }
        }

        if (e instanceof EnderDragonEntity) {
            IPhase phase = ((EnderDragonEntity) e).getPhaseManager().getCurrentPhase();
            if (phase.getType() != PhaseType.HOLDING_PATTERN &&
                    phase.getType() != PhaseType.DYING) {
                ((EnderDragonEntity) e).getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
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

        boolean shouldFreezeEntity(LivingEntity e) {
            if (!e.isAlive() || e.getHealth() <= 0) {
                return false;
            }
            if (e instanceof EnderDragonEntity && ((EnderDragonEntity) e).getPhaseManager().getCurrentPhase().getType() == PhaseType.DYING) {
                return false;
            }
            if (hasOwner && e.getEntityId() == ownerId) {
                return false;
            }
            return targetPlayers || !(e instanceof PlayerEntity);
        }

        public static EntityTargetController allExcept(Entity entity) {
            return new EntityTargetController(entity.getEntityId(), true, true);
        }

        public static EntityTargetController noPlayers() {
            return new EntityTargetController(-1, false, false);
        }

        @Nonnull
        public CompoundNBT serializeNBT() {
            CompoundNBT out = new CompoundNBT();
            out.putBoolean("targetPlayers", this.targetPlayers);
            out.putBoolean("hasOwner", this.hasOwner);
            out.putInt("ownerEntityId", this.ownerId);
            return out;
        }

        @Nonnull
        public static EntityTargetController deserializeNBT(CompoundNBT cmp) {
            boolean targetPlayers = cmp.getBoolean("targetPlayers");
            boolean hasOwner = cmp.getBoolean("hasOwner");
            int ownerId = cmp.getInt("ownerEntityId");
            return new EntityTargetController(ownerId, hasOwner, targetPlayers);
        }

    }

}